package be.vrt.services.logging.log.consumer.appender;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.MDC;

import be.vrt.services.logging.log.common.Constants;
import be.vrt.services.logging.log.common.dto.ErrorDto;
import be.vrt.services.logging.log.consumer.config.EnvironmentSetting;
import be.vrt.services.logging.log.consumer.dto.JsonLogWrapperDto;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJsonAppender extends AppenderBase<ILoggingEvent> implements Constants {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void append(ILoggingEvent e) {

//		if(!e.getMDCPropertyMap().containsKey(TRANSACTION_ID)){
//			return;
//		}
		JsonLogWrapperDto dto = new JsonLogWrapperDto();
		Object[] objects = e.getArgumentArray();
		try {
			dto.setDate(new Date());
			dto.setTransactionId(MDC.get(TRANSACTION_ID));
			String hostname;
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException ex) {
				hostname = "<UnknownHost>";
			}
			dto.setHostName(hostname);
			
			for (Object object : objects) {
				dto.getContent().add(wrapArg(object));
			}
			dto.setLogComment(e.getMessage());
			dto.setDate(new Date(e.getTimeStamp()));

			dto.setClassName(e.getCallerData()[0].getClassName());
			dto.setMethodName(e.getCallerData()[0].getMethodName());
			dto.setLineNumber(e.getCallerData()[0].getLineNumber());
			dto.setEnvironmentInfo(EnvironmentSetting.log);
			dto.setLoggerName(e.getLoggerName());
			dto.setLogLevel(e.getLevel().toString());
			persist(mapper.writeValueAsString(dto));
		} catch (Exception ex) {
			System.err.println("Failed to process Json: " + ex.getMessage());
		}
	}

	Object wrapArg(Object arg) {

		Map<String, Object> wrapArg = new HashMap<>();
		if (arg == null) {
			wrapArg.put("noValue", "null");
		} else if (arg instanceof String) {
			wrapArg.put("aString", ((String) arg));
		} else if (arg instanceof Integer) {
			wrapArg.put("anInteger", (Integer) arg);
		} else if (arg instanceof Long) {
			wrapArg.put("aLong", (Long) arg);
		} else if (arg instanceof Character) {
			wrapArg.put("aCharacter", (Character) arg);
		} else if (arg instanceof Date) {
			wrapArg.put("aDate", new Date(((Date) arg).getTime()));
		} else if (arg instanceof Double) {
			wrapArg.put("aDouble", (Double) arg);
		} else if (arg instanceof Short) {
			wrapArg.put("aShort", (Short) arg);
		} else if (arg instanceof Boolean) {
			wrapArg.put("aBoolean", (Boolean) arg);
		} else if (arg instanceof Throwable) {
			ErrorDto dto = new ErrorDto();
			Throwable t = (Throwable) arg;
			dto.setMessage(t.getMessage());
			dto.setClassName(t.getClass().getName());
			return dto;
		} else {
			return arg;
		}
		return wrapArg;

	}

	protected abstract void persist(String json);

	protected abstract Logger getLogger();
}
