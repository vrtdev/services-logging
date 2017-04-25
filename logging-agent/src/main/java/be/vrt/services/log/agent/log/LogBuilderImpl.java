package be.vrt.services.log.agent.log;

public class LogBuilderImpl implements LogBuilder, LogIndexNameObserver {

    private static final int MAX_BUFFER_SIZE = 4_000_000;

    private StringBuilder buffer = createBuffer();

    private LogFlusher logFlusher;
    private String indexName;

    public LogBuilderImpl(LogFlusher logFlusher) {
        this.logFlusher = logFlusher;
    }

    @Override
    public void addLog(String log) {
        buffer
                .append("\n")
                .append("{\"index\":{\"_index\":\"").append(indexName).append("\",\"_type\":\"logs\"}}\n")
                .append(log);
        if(buffer.length() > MAX_BUFFER_SIZE) {
            logFlusher.flush(buffer);
            buffer = createBuffer();
        }
    }

    private StringBuilder createBuffer() {
        return new StringBuilder(MAX_BUFFER_SIZE);
    }

    @Override
    public void indexChanged(String indexName) {
        this.indexName = indexName;
    }
}
