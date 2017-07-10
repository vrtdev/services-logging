package be.vrt.services.logging.api.audit.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface LogWithLevel {
    Level value();
}
