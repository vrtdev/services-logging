package be.vrt.services.log.collector.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class AuditAspect {

	private Logger log = LoggerFactory.getLogger(AuditAspect.class);
	
	@Around("@annotation(be.vrt.services.log.collector.audit.aspect.AuditFacade)")
	public void logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("Just before my method");
		joinPoint.proceed();
		log.info("Just After my method");
	}
}
