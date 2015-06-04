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
import be.vrt.services.logging.log.common.dto.ErrorDto;
import be.vrt.services.log.collector.exception.FailureException;
import be.vrt.services.logging.log.common.LogTransaction;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.reflect.MethodSignature;

public abstract class AbstractAuditAspect extends AbstractBreadcrumAuditAspect{

	protected abstract Logger getLogger();

	@Override
	protected Object handleJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable  {
		LogTransaction.increaseBreadCrum();

		AuditLogDto auditLogDto = new AuditLogDto();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			auditLogDto.setLogDate(new Date(stopWatch.getStartTime()));

			Object[] arguments = joinPoint.getArgs();
			List<Object> cloneArguments = new ArrayList<Object>();
			for (int i = 0; i < arguments.length; i++) {
				cloneArguments.add(cloneArgument("[" + i + "]", arguments[i]));
			}

			auditLogDto.setArguments(cloneArguments);
			auditLogDto.setMethod(joinPoint.getSignature().toShortString());
			auditLogDto.setClassName(joinPoint.getTarget().getClass().getSimpleName());
			Object obj = joinPoint.proceed();
			if (joinPoint.getSignature() instanceof MethodSignature) {
				MethodSignature ms = (MethodSignature) joinPoint.getSignature();
				if (ms.getMethod().getReturnType() != null) {
					auditLogDto.setResponse(cloneArgument("[resp]", obj));
				}
			}
			return obj;
		} catch (Throwable t) {
			auditLogDto.setAuditLevel((t instanceof FailureException) ? AuditLevelType.FAIL : AuditLevelType.ERROR);
			auditLogDto.setResponse(cloneArgument("[resp-fail]", t));
			throw t;
		} finally {
			stopWatch.stop();
			auditLogDto.setDuration(stopWatch.getTime());
			getLogger().info("AuditLogDto -- {} -> {}", auditLogDto.getClassName(), auditLogDto.getMethod(), auditLogDto);
			LogTransaction.decreaseBreadCrum();

			// Add listener HERE!!
		}
	}

	protected Object cloneArgument(String prefix, Object arg) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

		Map<String, Object> clonedArg = new HashMap<>();
		if (arg == null) {
			clonedArg.put(prefix + "aNull", "[NULL]");
		} else if (arg instanceof String) {
			clonedArg.put(prefix + "aString", arg);
		} else if (arg instanceof Integer) {
			clonedArg.put(prefix + "anInteger", arg);
		} else if (arg instanceof Long) {
			clonedArg.put(prefix + "aLong", arg);
		} else if (arg instanceof Character) {
			clonedArg.put(prefix + "aCharacter", arg);
		} else if (arg instanceof Date) {
			clonedArg.put(prefix + "aDate", new Date(((Date) arg).getTime()));
		} else if (arg instanceof Double) {
			clonedArg.put(prefix + "aDouble", arg);
		} else if (arg instanceof Short) {
			clonedArg.put(prefix + "aShort", arg);
		} else if (arg instanceof Boolean) {
			clonedArg.put(prefix + "aBoolean", arg);
		} else if (arg instanceof Throwable) {
			ErrorDto dto = new ErrorDto();
			Throwable t = (Throwable) arg;
			dto.setMessage(t.getMessage());
			dto.setClassName(t.getClass().getName());
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			dto.setStackTrace(sw.toString());
			return dto;
		} else {
			return BeanUtils.cloneBean(arg);
		}
		return clonedArg;
	}
}
