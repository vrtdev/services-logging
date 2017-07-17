package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.api.audit.annotation.AuditLogLevel;
import be.vrt.services.logging.log.common.LogTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Aspect
@Order(-50)
public class AuditLogLevelAspect {
    private Object logWithLevel(ProceedingJoinPoint joinPoint, AuditLogLevel auditLogLevel) throws Throwable {
        final String previousLevel = LogTransaction.getLevel();
        final String desiredLevel = auditLogLevel.value().name();
        try {
            LogTransaction.setLevel(desiredLevel);
            return joinPoint.proceed();
        } finally {
            LogTransaction.setLevel(previousLevel);
        }
    }

    @Around("within(@be.vrt.services.logging.api.audit.annotation.AuditLogLevel *) ")
    public Object logWithLevelOnClass(ProceedingJoinPoint joinPoint) throws Throwable {
        AuditLogLevel auditLogLevel = joinPoint.getTarget().getClass().getAnnotation(AuditLogLevel.class);
        return logWithLevel(joinPoint, auditLogLevel);
    }

    @Around("@annotation(auditLogLevel)")
    public Object logWithLevelOnMethod(ProceedingJoinPoint joinPoint, AuditLogLevel auditLogLevel) throws Throwable {
        return logWithLevel(joinPoint, auditLogLevel);
    }
}
 