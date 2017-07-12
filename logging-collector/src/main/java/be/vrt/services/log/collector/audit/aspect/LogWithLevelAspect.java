package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.api.audit.annotation.LogWithLevel;
import be.vrt.services.logging.log.common.LogTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Aspect
@Order(-50)
public class LogWithLevelAspect {
    @Around("@target(logWithLevel)")
    public Object logWithLevel(ProceedingJoinPoint joinPoint, LogWithLevel logWithLevel) throws Throwable {
        final String previousLevel = LogTransaction.getLevel();
        final String desiredLevel = logWithLevel.value().name();
        try {
            LogTransaction.setLevel(desiredLevel);
            return joinPoint.proceed();
        } finally {
            LogTransaction.setLevel(previousLevel);
        }
    }

    @Around("@target(logWithLevel)")
    public Object logWithLevelOnClass(ProceedingJoinPoint joinPoint, LogWithLevel logWithLevel) throws Throwable {
        return logWithLevel(joinPoint, logWithLevel);
    }

    @Around("@annotation(logWithLevel)")
    public Object logWithLevelOnMethod(ProceedingJoinPoint joinPoint, LogWithLevel logWithLevel) throws Throwable {
        return logWithLevel(joinPoint, logWithLevel);
    }
}
 