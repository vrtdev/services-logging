package be.vrt.services.logging.api.audit.annotation;

import java.lang.annotation.*;

/**
 * This annotation allows you to specify the exact level at which audit logs are generated.
 * <p>
 * The default audit log level is {@link Level#INFO}.
 * <p>
 * Breadcrumb audit logs are not set with this annotation, they will always be generated as {@link Level#DEBUG}.
 * However, if you set the audit log level to {@link Level#OFF}, the breadcrumb logs are also disabled.
 * <p>
 * This annotation should not be confused with the {@link LogSuppress} annotation:
 * <ul>
 * <li>when the {@link AuditLogLevel} is set to {@link Level#OFF}, no log entries are generated at all,</li>
 * <li>whereas a {@link LogSuppress} will generate log entries which are simply hidden in the log UI...</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface AuditLogLevel {
    Level value();
}
