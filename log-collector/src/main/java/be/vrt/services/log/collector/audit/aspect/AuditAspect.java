package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.log.collector.audit.dto.AuditLogDto;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
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

	@Pointcut("within(@be.vrt.services.log.collector.audit.annotation.AuditFacade *) || @annotation(be.vrt.services.log.collector.audit.annotation.AuditFacade)")
	public void anAuditFacade() {
	}

	@Pointcut("execution(public * *(..))")
	public void publicMethod() {
	}

	@Around("anAuditFacade() && publicMethod()")
	public void logAround(ProceedingJoinPoint joinPoint) throws Throwable {

		AuditLogDto auditLogDto = new AuditLogDto();
		try {
			log.info("Just before my method");
			Object[] params = joinPoint.getArgs();
			Object[] cloneParams = new Object[params.length];
			for (int i = 0; i < params.length; i++) {
				cloneParams[i] = cloneThroughByteSerialization(params[i]);
			}
			auditLogDto.setArguments(cloneParams);
			auditLogDto.setMethod(joinPoint.getSignature().toLongString());
			joinPoint.getArgs();
			joinPoint.proceed();
		} finally {
			log.info(ReflectionToStringBuilder.reflectionToString(auditLogDto));
			log.info("Just After my method");
		}
	}

	public Object cloneThroughByteSerialization(Object o) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		oos.flush();
		oos.close();
		bos.close();
		byte[] byteData = bos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
		return  (Object) new ObjectInputStream(bais).readObject();

	}
}
