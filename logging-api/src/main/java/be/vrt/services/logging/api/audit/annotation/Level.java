package be.vrt.services.logging.api.audit.annotation;

/**
 * An enumeration mapped to the standard logback levels for {@link AuditLogLevel}.
 */
public enum Level {
    OFF, ERROR, WARN, INFO, DEBUG, TRACE;

    public static Level from(String level) {
        try {
            return Level.valueOf(level);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Level.INFO;
        }
    }
}
