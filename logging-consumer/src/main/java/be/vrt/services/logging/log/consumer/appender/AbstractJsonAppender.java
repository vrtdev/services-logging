package be.vrt.services.logging.log.consumer.appender;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.slf4j.Logger;

import be.vrt.services.logging.log.common.Constants;
import be.vrt.services.logging.log.common.DelayedLogObject;
import be.vrt.services.logging.log.common.LogTransaction;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import be.vrt.services.logging.log.consumer.config.EnvironmentSetting;
import be.vrt.services.logging.log.consumer.dto.JsonLogWrapperDto;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractJsonAppender extends AppenderBase<ILoggingEvent> implements Constants {
	
	// Tag startupthread for logging to static flow
	static {
		TransactionRegistery.instance();
	}

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void append(ILoggingEvent logEvent) {

		JsonLogWrapperDto dto = new JsonLogWrapperDto();
		Object[] objects = logEvent.getArgumentArray();
		String json;
		try {
			dto.setLogDate(new Date());
			dto.setDate(new Date(logEvent.getTimeStamp()));
			dto.setTransactionId(LogTransaction.id());
			dto.setFlowId(LogTransaction.flow());
			dto.setBreadCrumb(LogTransaction.breadCrumb());
			String hostname;
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException ex) {
				hostname = "<UnknownHost>";
			}
			dto.setHostName(hostname);

			if (objects != null) {
				int counter = 1;
				for (Object object : objects) {
					if(object instanceof DelayedLogObject){
						dto.setDate(((DelayedLogObject)object).getStartDate());
					}
					if (object == null) {
						dto.getContent().put("[" + (counter++) + "] noValue", "null");
					}
					dto.getContent().put("[" + (counter++) + "] " + object.getClass().getSimpleName(), object);
				}
			}
			dto.setLogComment(logEvent.getFormattedMessage());

			dto.setClassName(logEvent.getCallerData()[0].getClassName());
			dto.setMethodName(logEvent.getCallerData()[0].getMethodName());
			dto.setLineNumber(logEvent.getCallerData()[0].getLineNumber());
			dto.setEnvironmentInfo(EnvironmentSetting.info());
			dto.setLoggerName(logEvent.getLoggerName());
			dto.setLogLevel(logEvent.getLevel().toString());
			try {
				json = mapper.writeValueAsString(dto);
			} catch (Exception ex) {
				dto.setContent(null);
				dto.setLogComment(logEvent.getFormattedMessage());
				json = mapper.writeValueAsString(dto);
			}
			persist(json);
		} catch (Exception ex) {
			System.err.println("Failed to process Json2: " + logEvent.getLoggerName() + ":" + logEvent.getLevel() + ":" + logEvent.getFormattedMessage());
		}
	}

	protected abstract void persist(String json);

	protected abstract Logger getLogger();
}
