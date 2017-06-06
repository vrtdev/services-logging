package be.vrt.services.logging.kothopper.udp;

import be.vrt.services.log.agent.udp.Server;
import be.vrt.services.log.agent.log.LogBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.*;

public class ServerIntegrationTest {

    private static final int PORT = 5514;
    private Server server;
    private DummyLogBuilder logBuilder;

    @Before
    public void setUp() throws Exception {
        logBuilder = new DummyLogBuilder();
        server = new Server(PORT, logBuilder);
        server.run();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void accept() throws Exception {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress inet = InetAddress.getByName("localhost");
            byte[] sendData = "{\"hello\":\"world\"}".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inet, PORT);
            clientSocket.send(sendPacket);
            Thread.sleep(500);
        }
        assertThat(logBuilder.logs, contains("{\"hello\":\"world\"}"));
    }

    private static class DummyLogBuilder implements LogBuilder {

        private List<String> logs = new ArrayList<>();

        @Override
        public void addLog(String log) {
            logs.add(log);
        }

        @Override
        public void flush() {
            logs.clear();
        }
    }
}
