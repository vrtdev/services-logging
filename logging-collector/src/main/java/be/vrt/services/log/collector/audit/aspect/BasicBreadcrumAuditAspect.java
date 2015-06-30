package be.vrt.services.log.collector.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicBreadcrumAuditAspect extends AbstractBreadcrumbAuditAspect {

	@Override
	protected Object handleJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		getLogger(joinPoint).debug("[BREADCRUMB] "+joinPoint.getSignature().toShortString());
		try {
			return joinPoint.proceed();
		} catch (Exception e) {
			getLogger(joinPoint).debug("[BREADCRUMB-EXCEPTION] "+joinPoint.getSignature().toShortString() + " -> " + e.getMessage(), e);
			throw e;
		}
	}
	
	Logger getLogger(ProceedingJoinPoint joinPoint){
		return LoggerFactory.getLogger(joinPoint.getTarget().getClass());
	}

}
