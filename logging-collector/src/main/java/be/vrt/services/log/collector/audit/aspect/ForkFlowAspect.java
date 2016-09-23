package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.log.common.LogTransaction;
import static be.vrt.services.logging.log.common.LogTransaction.SUPPRESSED;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

@Aspect
@Order(50)
public class ForkFlowAspect {


	@Pointcut("within(@be.vrt.services.logging.api.audit.annotation.LogForkFlow *) || @annotation(be.vrt.services.logging.api.audit.annotation.LogForkFlow)")
	public void fork() {
	}

	@Around("fork() ")
	public Object forkFlow(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			LogTransaction.createChildFlow(joinPoint.toShortString());
			
			return joinPoint.proceed();
		} finally {
			LogTransaction.backToParentFlow(joinPoint.toShortString());

		}
	}

}

