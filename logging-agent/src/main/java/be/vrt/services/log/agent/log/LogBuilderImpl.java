package be.vrt.services.log.agent.log;

import be.vrt.services.log.agent.udp.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LogBuilderImpl implements LogBuilder {

    private static final int MAX_BUFFER_SIZE = 4_000_000;
    private static final int BUFFER_SIZE = MAX_BUFFER_SIZE + Server.CHANNEL_BUFFER;
    private static final Logger LOGGER = LoggerFactory.getLogger(LogBuilderImpl.class);

    private StringBuilder buffer = createBuffer();
    private LogFlusher logFlusher;
    private String indexName;

    public LogBuilderImpl(LogFlusher logFlusher) {
        this.logFlusher = logFlusher;
    }

    @Override
    public synchronized void addLog(String log) {
        buffer
                .append("{\"index\":{\"_index\":\"").append(indexName).append("\",\"_type\":\"logs\"}}\n")
                .append(log)
                .append("\n")
        ;
        int length = buffer.length();
        if (length > MAX_BUFFER_SIZE) {
            flushInternal(length);
        }
    }

    @Override
    public synchronized void flush() {
        int length = buffer.length();
        if (length > 0) {
            flushInternal(length);
        }
    }

    private void flushInternal(int length) {
        LOGGER.debug("Flushing buffer to {}; size={}", indexName, length);
        logFlusher.flush(buffer);
        buffer = createBuffer();
    }

    private StringBuilder createBuffer() {
        this.indexName = "svc-logging-" + DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now());
        return new StringBuilder(BUFFER_SIZE);
    }

}
