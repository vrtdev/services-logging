package be.vrt.services.log.collector.audit.aspect;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.vrt.services.log.collector.audit.dto.AuditLogDto;
import be.vrt.services.log.collector.audit.dto.ErrorDto;

@Aspect
public class AuditAspect {

	private Logger log = LoggerFactory.getLogger(AuditAspect.class);

	public AuditAspect() {
	}

	@Pointcut("within(@be.vrt.services.log.collector.audit.annotation.AuditFacade * *(..)) ||  @annotation(be.vrt.services.log.collector.audit.annotation.AuditFacade *).*(..)")
	public void anAuditFacade() {
	}

	@Pointcut("execution(public * *(..))")
	public void publicMethod() {
	}

	@Around("anAuditFacade() && publicMethod()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

		AuditLogDto auditLogDto = new AuditLogDto();
		try {
			Object[] params = joinPoint.getArgs();
			List<Object> cloneParams = new ArrayList<Object>();
			for (int i = 0; i < params.length; i++) {
				cloneParams.add(cloneParameter(params[i]));
			}
			
			auditLogDto.setArguments(cloneParams);
			auditLogDto.setMethod(joinPoint.getSignature().toShortString());
			
			joinPoint.getArgs();
			Object obj = joinPoint.proceed();
			auditLogDto.setResponse(cloneParameter(obj));
			return obj;
		} catch (Throwable t) {
			auditLogDto.setResponse(cloneParameter(t));
			throw t;
		} finally {
			log.info("AuditLogDto: {}",auditLogDto);
		}
	}
	
	Object cloneParameter(Object param) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		if (param == null) {
			return null;
		}
 		if (param instanceof String) {
			return new String((String) param);
		} else if (param instanceof Integer) {
			return new Integer((Integer) param);
		} else if (param instanceof Long) {
			return new Long((Long) param);
		} else if (param instanceof Character) {
			return new Character((Character) param);
		} else if (param instanceof Date) {
			return new Date(((Date) param).getTime());
		} else if (param.getClass().isPrimitive()) {
			return param;
		} else if (param instanceof Throwable) {
			ErrorDto dto = new ErrorDto();
			Throwable t = (Throwable) param;
			dto.setMessage(t.getMessage());
			dto.setClassName(t.getClass().getName());
			return dto;
		} else {
			return BeanUtils.cloneBean(param);
		}
	}
}
