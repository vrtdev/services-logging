package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.log.collector.audit.AuditLevelType;
import be.vrt.services.log.collector.audit.dto.AuditLogDto;
import be.vrt.services.log.collector.exception.FailureException;
import be.vrt.services.logging.api.audit.annotation.Level;
import be.vrt.services.logging.log.common.LogTransaction;
import be.vrt.services.logging.log.common.dto.ErrorDto;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractAuditAspect extends AbstractBreadcrumbAuditAspect {

    private static final String LOG_MSG_TEMPLATE = "[{}] - {} >> {}";

    protected abstract String getType();

	@Override
	protected Object handleJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		AuditLogDto auditLogDto = new AuditLogDto();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			auditLogDto.setStartDate(new Date(stopWatch.getStartTime()));

			Object[] arguments = joinPoint.getArgs();

            auditLogDto.setArguments(IntStream.range(0, arguments.length)
                    .mapToObj(i -> cloneArgument("[" + i + "]", arguments[i]))
                    .collect(Collectors.toList()));
            auditLogDto.setMethod(joinPoint.getSignature().toShortString());
            auditLogDto.setClassName(joinPoint.getTarget().getClass().getSimpleName());
            final Object obj = joinPoint.proceed();
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
            final Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
            switch (Level.from(LogTransaction.getLevel())) {
                case WARN:
                    logger.warn(LOG_MSG_TEMPLATE, getType(), auditLogDto.getMethod(), auditLogDto.getAuditLevel(),
                            auditLogDto);
                    break;
                case ERROR:
                    logger.error(LOG_MSG_TEMPLATE, getType(), auditLogDto.getMethod(), auditLogDto.getAuditLevel(),
                            auditLogDto);
                    break;
                case TRACE:
                    logger.trace(LOG_MSG_TEMPLATE, getType(), auditLogDto.getMethod(), auditLogDto.getAuditLevel(),
                            auditLogDto);
                    break;
                case DEBUG:
                    logger.debug(LOG_MSG_TEMPLATE, getType(), auditLogDto.getMethod(), auditLogDto.getAuditLevel(),
                            auditLogDto);
                    break;
                case OFF:
                    break;
                default:
                    logger.info(LOG_MSG_TEMPLATE, getType(), auditLogDto.getMethod(), auditLogDto.getAuditLevel(),
                            auditLogDto);
            }
            // Add listener HERE!!
        }
    }

	protected Object cloneArgument(String prefix, Object arg) {
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
                try {
    				return BeanUtils.cloneBean(arg);
                } catch (Exception e) {
                    clonedArg.put(prefix + "failed_parse_data", arg.getClass().getSimpleName() + " --> " +  e.getMessage());
                }
			}
		return clonedArg;
	}
}
