package be.vrt.services.log.agent.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogBuilderImpl implements LogBuilder, LogIndexNameObserver {

    private static final int MAX_BUFFER_SIZE = 4_000_000;
    private static final int BUFFER_SIZE = 4_010_000;
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
                .append("\n")
                .append("{\"index\":{\"_index\":\"").append(indexName).append("\",\"_type\":\"logs\"}}\n")
                .append(log);
        if(buffer.length() > MAX_BUFFER_SIZE) {
            flush();
        }
    }

    @Override
    public synchronized void flush() {
        LOGGER.debug("FLUSHING...");
        LOGGER.debug("size: {}", buffer.length());
        logFlusher.flush(buffer);
        buffer = createBuffer();
    }

    private StringBuilder createBuffer() {
        return new StringBuilder(BUFFER_SIZE);
    }

    @Override
    public synchronized void indexChanged(String indexName) {
        this.indexName = indexName;
    }
}
