package be.vrt.services.log.agent;

import be.vrt.services.log.agent.log.LogBuilderImpl;
import be.vrt.services.log.agent.log.LogFlusherImpl;
import be.vrt.services.log.agent.log.LogIndexCreator;
import be.vrt.services.log.agent.udp.Server;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        int port;
        int esPort;
        String host;
        String mappingPath = null;
        if (args.length >= 3) {
            host = args[0];
            esPort = Integer.parseInt(args[1]);
            port = Integer.parseInt(args[2]);
            if(args.length >= 4) {
                mappingPath = args[3];
            }
        } else {
            throw new RuntimeException("expected ESHost ESPort udpPort [mappingFilePath]");
        }
        LogBuilderImpl logBuilder = new LogBuilderImpl(new LogFlusherImpl(host, esPort));
        LogIndexCreator logIndexCreator = new LogIndexCreator(host, esPort, mappingPath, logBuilder);
        logIndexCreator.createIndex();

        Long minutesTillMidnight = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay(), ChronoUnit.MINUTES);
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(logIndexCreator::createIndex, minutesTillMidnight, 1440, TimeUnit.MINUTES);

        Server server = new Server(port, logBuilder);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            server.shutdown();
        }));
        server.run();
    }
}
