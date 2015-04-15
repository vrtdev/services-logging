package be.vrt.services.log.collector.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class AuditAspect {

	private Logger log = LoggerFactory.getLogger(AuditAspect.class);

	public AuditAspect() {
		log.info("I'm Alive!!!!");
	}

	@Pointcut("within(@be.vrt.services.log.collector.audit.annotation.AuditFacade) || || @annotation(be.vrt.services.log.collector.audit.annotation.AuditFacade)")
	public void anAuditFacade() {
	}

	@Pointcut("execution(public * *(..))")
	public void publicMethod() {
	}

	@Around("anAuditFacade() && publicMethod()")
	public void logAround(ProceedingJoinPoint joinPoint) throws Throwable {

		try {
			// log.info("Just before my method");
			
			joinPoint.getArgs();
			joinPoint.proceed();
		} finally {
			log.info("Just After my method");
		}
	}
}
