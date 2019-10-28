package be.vrt.services.log.agent;

import be.vrt.services.log.agent.log.LogBuilderImpl;
import be.vrt.services.log.agent.log.LogFlusherImpl;
import be.vrt.services.log.agent.udp.Server;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        int port;
        int esPort;
        String host;
        if (args.length == 3) {
            host = args[0];
            esPort = Integer.parseInt(args[1]);
            port = Integer.parseInt(args[2]);
        } else {
            throw new RuntimeException("expected ESHost ESPort udpPort");
        }
        LogBuilderImpl logBuilder = new LogBuilderImpl(new LogFlusherImpl(host, esPort));

        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(logBuilder::flush, 60, 10, TimeUnit.SECONDS);

        Server server = new Server(port, logBuilder);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            server.shutdown();
        }));
        server.run();
    }
}
