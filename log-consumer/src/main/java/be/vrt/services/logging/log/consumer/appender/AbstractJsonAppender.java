package be.vrt.services.logging.log.consumer.appender;

import be.vrt.services.logging.log.common.Constants;
import be.vrt.services.logging.log.consumer.config.EnvironmentSetting;
import be.vrt.services.logging.log.consumer.dto.JsonLogWrapperDto;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import org.slf4j.MDC;

public abstract class AbstractJsonAppender extends AppenderBase<ILoggingEvent> implements Constants {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void append(ILoggingEvent e) {
		
		Object[] objects = e.getArgumentArray();
		JsonLogWrapperDto dto = new JsonLogWrapperDto();
		dto.setDate(new Date());
		dto.setTransactionId(MDC.get(TRANSACTION_ID));
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			hostname = "<UnknownHost>";
		}
		dto.setHostName(hostname);
		dto.setContent(Arrays.asList(objects));
		dto.setDate(new Date(e.getTimeStamp()));
		dto.setClassName(e.getCallerData()[0].getClassName());
		dto.setMethodName(e.getCallerData()[0].getMethodName());
		dto.setLineNumber(e.getCallerData()[0].getLineNumber());
		dto.setEnvironmentInfo(EnvironmentSetting.log);
		dto.setLoggerName(e.getLoggerName());
		try {
			persist(mapper.writeValueAsString(dto));
		} catch (JsonProcessingException ex) {
			System.err.println("FAILED TO PROCESS JSON");
//			Logger.getLogger(AbstractJsonAppender.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	protected abstract void persist(String json);
}
