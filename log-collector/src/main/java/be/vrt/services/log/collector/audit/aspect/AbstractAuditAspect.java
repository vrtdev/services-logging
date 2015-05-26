package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.log.collector.audit.AuditLevelType;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;

import be.vrt.services.log.collector.audit.dto.AuditLogDto;
import be.vrt.services.log.collector.audit.dto.ErrorDto;
import be.vrt.services.log.collector.exception.FailureException;
import org.apache.commons.lang3.time.StopWatch;

public abstract class AbstractAuditAspect {

	protected abstract Logger getLogger();

	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		AuditLogDto auditLogDto = new AuditLogDto();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			auditLogDto.setLogDate(new Date(stopWatch.getStartTime()));

			Object[] arguments = joinPoint.getArgs();
			List<Object> cloneArguments = new ArrayList<Object>();
			for (Object argument : arguments) {
				cloneArguments.add(cloneArgument(argument));
			}

			auditLogDto.setArguments(cloneArguments);
			auditLogDto.setMethod(joinPoint.getSignature().toShortString());
			auditLogDto.setClassName(joinPoint.getTarget().getClass().getSimpleName());
			Object obj = joinPoint.proceed();
			auditLogDto.setResponse(cloneArgument(obj));
			return obj;
		} catch (Throwable t) {
			auditLogDto.setAuditLevel((t instanceof FailureException) ? AuditLevelType.FAIL : AuditLevelType.ERROR);
			auditLogDto.setResponse(cloneArgument(t));
			throw t;
		} finally {
			stopWatch.stop();
			auditLogDto.setDuration(stopWatch.getTime());
			getLogger().info("AuditLogDto: {}", auditLogDto);
			// Add listener HERE!!
		}
	}

	protected Object cloneArgument(Object arg) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		if (arg == null) {
			return null;
		}

		Map<String, Object> clonedArg = new HashMap<>();
		if (arg instanceof String) {
			clonedArg.put("aString", arg);
		} else if (arg instanceof Integer) {
			clonedArg.put("anInteger", arg);
		} else if (arg instanceof Long) {
			clonedArg.put("aLong", arg);
		} else if (arg instanceof Character) {
			clonedArg.put("aCharacter", arg);
		} else if (arg instanceof Date) {
			clonedArg.put("aDate", new Date(((Date) arg).getTime()));
		} else if (arg instanceof Double) {
			clonedArg.put("aDouble", arg);
		} else if (arg instanceof Short) {
			clonedArg.put("aShort", arg);
		} else if (arg instanceof Boolean) {
			clonedArg.put("aBoolean",  arg);
		} else if (arg instanceof Throwable) {
			ErrorDto dto = new ErrorDto();
			Throwable t = (Throwable) arg;
			dto.setMessage(t.getMessage());
			dto.setClassName(t.getClass().getName());
			return dto;
		} else {
			return BeanUtils.cloneBean(arg);
		}
		return clonedArg;
	}
}
