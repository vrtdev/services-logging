package be.vrt.services.log.collector.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

public class BasicBreadcrumAuditAspect extends AbstractBreadcrumAuditAspect {

	@Override
	protected Object handleJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		log.debug(joinPoint.getSignature().toShortString());
		return joinPoint.proceed();
	}

}
