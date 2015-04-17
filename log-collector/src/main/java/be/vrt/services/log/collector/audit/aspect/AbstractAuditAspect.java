package be.vrt.services.log.collector.audit.aspect;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
				cloneArguments.add(cloneParameter(arguments[i]));
			}
			
			auditLogDto.setArguments(cloneArguments);
			auditLogDto.setMethod(joinPoint.getSignature().toShortString());
			
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
	
	protected Object cloneParameter(Object param) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		if (param == null) {
			return null;
		}
		
		Map<String, Object> clonedParam = new HashMap<>();
 		if (param instanceof String) {
 			clonedParam.put("aString", new String((String) param));
		} else if (param instanceof Integer) {
			clonedParam.put("anInteger", new Integer((Integer) param));
		} else if (param instanceof Long) {
			clonedParam.put("aLong", new Long((Long) param));
		} else if (param instanceof Character) {
			clonedParam.put("aCharacter", new Character((Character) param));
		} else if (param instanceof Date) {
			clonedParam.put("aDate", new Date(((Date) param).getTime()));
		} else if (param instanceof Double) {
			clonedParam.put("aDouble", new Double((Double) param));
		} else if (param instanceof Short) {
			clonedParam.put("aShort", new Short((Short) param));
		} else if (param instanceof Boolean) {
			clonedParam.put("aBoolean", new Boolean((Boolean) param));
		} else if (param instanceof Throwable) {
			ErrorDto dto = new ErrorDto();
			Throwable t = (Throwable) param;
			dto.setMessage(t.getMessage());
			dto.setClassName(t.getClass().getName());
			return dto;
		} else {
			return BeanUtils.cloneBean(param);
		}
 		return clonedParam;
	}
}
