package be.vrt.services.log.agent.log;

public interface LogFlusher {
    void flush(StringBuilder buffer);
}
