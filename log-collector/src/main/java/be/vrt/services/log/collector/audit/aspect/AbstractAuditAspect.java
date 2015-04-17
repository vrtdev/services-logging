package be.vrt.services.log.collector.audit.aspect;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.vrt.services.log.collector.audit.dto.AuditLogDto;
import be.vrt.services.log.collector.audit.dto.ErrorDto;

public abstract class AbstractAuditAspect {
	
	private Logger log = LoggerFactory.getLogger(AbstractAuditAspect.class);
	
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		AuditLogDto auditLogDto = new AuditLogDto();
		try {
			Object[] arguments = joinPoint.getArgs();
			List<Object> cloneArguments = new ArrayList<Object>();
			for (int i = 0; i < arguments.length; i++) {
				cloneArguments.add(cloneArgument(arguments[i]));
			}
			
			auditLogDto.setArguments(cloneArguments);
			auditLogDto.setMethod(joinPoint.getSignature().toShortString());
			
			Object obj = joinPoint.proceed();
			auditLogDto.setResponse(cloneArgument(obj));
			return obj;
		} catch (Throwable t) {
			auditLogDto.setResponse(cloneArgument(t));
			throw t;
		} finally {
			log.info("AuditLogDto: {}",auditLogDto);
		}
	}
	
	protected Object cloneArgument(Object arg) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		if (arg == null) {
			return null;
		}
		
		Map<String, Object> clonedArg = new HashMap<>();
 		if (arg instanceof String) {
 			clonedArg.put("aString", new String((String) arg));
		} else if (arg instanceof Integer) {
			clonedArg.put("anInteger", new Integer((Integer) arg));
		} else if (arg instanceof Long) {
			clonedArg.put("aLong", new Long((Long) arg));
		} else if (arg instanceof Character) {
			clonedArg.put("aCharacter", new Character((Character) arg));
		} else if (arg instanceof Date) {
			clonedArg.put("aDate", new Date(((Date) arg).getTime()));
		} else if (arg instanceof Double) {
			clonedArg.put("aDouble", new Double((Double) arg));
		} else if (arg instanceof Short) {
			clonedArg.put("aShort", new Short((Short) arg));
		} else if (arg instanceof Boolean) {
			clonedArg.put("aBoolean", new Boolean((Boolean) arg));
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
