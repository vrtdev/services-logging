package be.vrt.services.log.agent.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogFlusherImpl implements LogFlusher {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFlusherImpl.class);

    private String urlString;

    public LogFlusherImpl(String host, int esPort) {
        urlString = "http://" + host + ":" + esPort + "/_bulk";
    }

    @Override
    public void flush(StringBuilder buffer) {
        new Thread(() -> {
            String output = buffer.toString();
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Content-Length", "" + output.length());
                connection.setDoOutput(true);
                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    wr.writeBytes(output);
                    wr.close();
                }
                int responseCode = connection.getResponseCode();
                if(responseCode != 200) {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String s = bufferedReader.readLine();
                        LOGGER.error("Error while writing logs", s);
                        bufferedReader.close();
                    }
                }
            } catch (IOException e) {
                LOGGER.error("IO error on flush", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}
