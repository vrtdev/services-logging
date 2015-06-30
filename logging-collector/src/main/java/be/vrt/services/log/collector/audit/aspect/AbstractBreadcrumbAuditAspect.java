package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.log.common.LogTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBreadcrumbAuditAspect {

	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		LogTransaction.increaseBreadCrumb();
		try {
			return handleJoinPoint(joinPoint);
		} finally {
			LogTransaction.decreaseBreadCrumb();
		}
	}

	protected abstract Object handleJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable;
}
