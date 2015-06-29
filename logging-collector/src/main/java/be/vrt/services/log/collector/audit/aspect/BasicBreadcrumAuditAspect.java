package be.vrt.services.log.collector.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

public class BasicBreadcrumAuditAspect extends AbstractBreadcrumbAuditAspect {

	@Override
	protected Object handleJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		log.debug("[BREADCRUMB] "+joinPoint.getSignature().toShortString());
		try {
			return joinPoint.proceed();
		} catch (Exception e) {
			log.debug("[BREADCRUMB-EXCEPTION] "+joinPoint.getSignature().toShortString() + " -> " + e.getMessage(), e);
			throw e;
		}
	}

}
