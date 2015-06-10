package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.log.common.LogTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBreadcrumAuditAspect {

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		LogTransaction.increaseBreadCrum();
		try {
			return handleJoinPoint(joinPoint);
		} finally {
			LogTransaction.decreaseBreadCrum();
		}
	}

	protected abstract Object handleJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable;
}
