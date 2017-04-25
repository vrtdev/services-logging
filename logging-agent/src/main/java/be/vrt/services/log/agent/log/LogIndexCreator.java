package be.vrt.services.log.agent.log;

import java.io.BufferedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LogIndexCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogIndexCreator.class);

    private final String mappingPath;
    private final LogIndexNameObserver observer;
    private final String urlString;

    public LogIndexCreator(String host, int esPort, String mappingPath, LogIndexNameObserver observer) {
        urlString = "http://" + host + ":" + esPort + "/";
        this.mappingPath = mappingPath;
        this.observer = observer;
    }

    public void createIndex() {
        String indexName = "svc-logging-" + DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now());
        createESIndex(indexName);
        observer.indexChanged(indexName);
    }

    private void createESIndex(String indexName) {
        HttpURLConnection connection = null;
        try {
            String output = readMapping();
            URL url = new URL(urlString + indexName);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", "" + output.length());
            connection.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(output);
                wr.close();
            }
            connection.getResponseCode();
        } catch (IOException e) {
            LOGGER.error("IO error on create index", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readMapping() throws IOException {
        return Optional.ofNullable(mappingPath)
                .map(path -> readInputMapping(path))
                .orElseGet(() -> readDefaultMapping());
    }

    private String readInputMapping(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException ex) {
            throw new RuntimeException("Something went wrong while reading mapping file: " + mappingPath);
        }
    }

    private String readDefaultMapping() {
        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("es-2-mapping.json")))) {
            output = reader.lines()
                    .reduce((t, u) -> t + u)
                    .get();
        } catch (IOException ex) {
            throw new RuntimeException("Something went wrong when reading default mapping file");
        }
        return output;
    }

}
