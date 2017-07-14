package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.api.audit.annotation.Level;
import be.vrt.services.logging.log.common.LogTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class BasicBreadcrumbAuditAspect extends AbstractBreadcrumbAuditAspect {
    @Override
    protected Object handleJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        log(joinPoint, "[BREADCRUMB] {}", joinPoint.getSignature().toShortString());
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            log(joinPoint, "[BREADCRUMB-EXCEPTION] {} -> {}",
                    joinPoint.getSignature().toShortString(), e.getMessage(), e);
            throw e;
        }
    }

    private void log(ProceedingJoinPoint jointPoint, String template, Object... args) {
        if (!Level.OFF.name().equals(LogTransaction.getLevel())) {
            LoggerFactory.getLogger(jointPoint.getTarget().getClass()).debug(template, args);
        }
    }
}
