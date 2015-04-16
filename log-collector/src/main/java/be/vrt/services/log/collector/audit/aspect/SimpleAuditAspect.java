package be.vrt.services.log.collector.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SimpleAuditAspect extends AbstractAuditAspect {

	@Pointcut("within(@be.vrt.services.log.collector.audit.annotation.AuditFacade *) || @annotation(be.vrt.services.log.collector.audit.annotation.AuditFacade)")
	public void anAuditFacade() {
	}
	
	@Pointcut("execution(public * *(..))")
	public void publicMethod() {
	}

	@Around("anAuditFacade() && publicMethod()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		return super.logAround(joinPoint);
	}
	
	
}