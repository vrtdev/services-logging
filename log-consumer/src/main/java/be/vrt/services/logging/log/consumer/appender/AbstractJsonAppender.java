package be.vrt.services.logging.log.consumer.appender;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.MDC;

import be.vrt.services.logging.log.common.Constants;
import be.vrt.services.logging.log.consumer.config.EnvironmentSetting;
import be.vrt.services.logging.log.consumer.dto.JsonLogWrapperDto;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.fasterxml.jackson.databind.ObjectMapper;

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
			if (objects != null) {
				dto.setContent(Arrays.asList(objects));
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
			getLogger().error("Failed to process Json: " + ex.getMessage());
		}
	}

	protected abstract void persist(String json);
	
	protected abstract Logger getLogger();
}
