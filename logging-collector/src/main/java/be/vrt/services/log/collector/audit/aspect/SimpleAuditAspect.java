package be.vrt.services.log.collector.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class SimpleAuditAspect extends AbstractAuditAspect {

	@Pointcut("within(@be.vrt.services.log.collector.audit.annotation.SimpleAudit *) || @annotation(be.vrt.services.log.collector.audit.annotation.SimpleAudit)")
	public void anAuditFacade() {
	}
	
	@Pointcut("execution(public * *(..))")
	public void publicMethod() {
	}

	@Around("anAuditFacade() && publicMethod()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		return super.logAround(joinPoint);
	}

	@Override
	protected Logger getLogger() {
		return LoggerFactory.getLogger(SimpleAuditAspect.class);
	}
	
	
}
