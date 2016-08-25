package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.log.common.LogTransaction;
import static be.vrt.services.logging.log.common.LogTransaction.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LogSuppressingAspect {

	@Pointcut("within(@be.vrt.services.logging.api.audit.annotation.LogSuppress *) || @annotation(be.vrt.services.logging.api.audit.annotation.LogSuppress)")
	public void suppressing() {
	}

	@Pointcut("within(@be.vrt.services.logging.api.audit.annotation.LogUnsuppress *) || @annotation(be.vrt.services.logging.api.audit.annotation.LogUnsuppress)")
	public void unsuppress() {
	}

	@Pointcut("execution(public * *(..))")
	public void publicMethod() {
	}

	@Around("suppressing() ")
	public Object logSuppress(ProceedingJoinPoint joinPoint) throws Throwable {
		
		return joinPoint.proceed();
	}

	@Around("unsuppress()")
	public Object logUnsuppress(ProceedingJoinPoint joinPoint) throws Throwable {

		return joinPoint.proceed();
	}

}
