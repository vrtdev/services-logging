package be.vrt.services.logging.log.consumer.appender;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchAppenderTest {

	@Mock
	private ILoggingEvent logEvent;

	@InjectMocks
	@Spy
	private ElasticSearchAppender elasticSearchAppender;

	@Test
	public void append() {
		StackTraceElement[] stack = new StackTraceElement[1];
		StackTraceElement element = new StackTraceElement("AssetServiceImpl", "updateAsset", null, 50);
		stack[0] = element;

		when(logEvent.getMessage()).thenReturn("Test log comment");
		when(logEvent.getLoggerName()).thenReturn("Test logger name");
		when(logEvent.getTimeStamp()).thenReturn(5L);
		when(logEvent.getCallerData()).thenReturn(stack);
		when(logEvent.getLevel()).thenReturn(Level.INFO);

		elasticSearchAppender.append(logEvent);

		verify(elasticSearchAppender).persist(anyString());
	}

//	@Test
	public void persist() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ElasticSearchAppender appender = new ElasticSearchAppender();
		appender.setHost("localhost");
		appender.setPort(9200);

		appender.start();
		Map<String, Object> testValue = new HashMap<>();
		testValue.put("aKey", "aValueéé^ééLLL[]||@#§");
		appender.persist(mapper.writeValueAsString(testValue));

	}
//	@Test
	public void persist2() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ElasticSearchAppender appender = new ElasticSearchAppender();
		appender.setHost("localhost");
		appender.setPort(9200);

		appender.start();
		appender.persist("{\"date\":1433424869441,\"transactionId\":\"localhost-e48d9223-6c55-45fe-bb2e-9f01f5859bd8\",\"hostName\":\"localhost\",\"flowId\":null,\"className\":\"org.springframework.web.context.ContextLoader\",\"methodName\":\"initWebApplicationContext\",\"lineNumber\":285,\"logLevel\":\"INFO\",\"loggerName\":\"org.springframework.web.context.ContextLoader\",\"logComment\":\"Root WebApplicationContext: initialization started\",\"ids\":[],\"content\":{},\"environmentInfo\":null}");

	}
}
