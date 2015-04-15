package be.vrt.services.log.collector.transaction.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

import com.sun.security.auth.UserPrincipal;

@RunWith(MockitoJUnitRunner.class)
public class TransactionLoggerFilterTest {
	
	@Mock
	private HttpServletRequest httpServletRequest;
	
	@Mock
	private HttpServletResponse httpServletResponse;
	
	@Mock
	private FilterChain filterChain;
	
	@Mock
	private Appender appender;

	@InjectMocks
	private TransactionLoggerFilter transactionLoggerFilter;
	
	@SuppressWarnings("unchecked")
	@Test
	public void doFilter() throws IOException, ServletException {
		Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	    logger.addAppender(appender);
		
		UserPrincipal userPrincipal = new UserPrincipal("testUser");
		when(httpServletRequest.getUserPrincipal()).thenReturn(userPrincipal);
		when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET);
		when(httpServletRequest.getServerName()).thenReturn("mediazone-dev");
		when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://mediazone-admin-dev.vrt.be/rest/client/v1/assestsources"));
		Map<String,String> httpParameters = new HashMap<>();
		when(httpServletRequest.getParameterNames()).thenReturn( new IteratorEnumeration(httpParameters.keySet().iterator()) );
		for (Map.Entry<String, String> entry : httpParameters.entrySet()) {
			when(httpServletRequest.getParameter(entry.getKey())).thenReturn(entry.getValue());
		}
		
		transactionLoggerFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		
		ArgumentCaptor<LoggingEvent> captorLoggingEvent = ArgumentCaptor.forClass(LoggingEvent.class);
		verify(appender).doAppend(captorLoggingEvent.capture());
        LoggingEvent loggingEvent = captorLoggingEvent.getValue();
        assertEquals(loggingEvent.getLevel(), (Level.INFO));
        assertTrue(loggingEvent.getFormattedMessage().contains(",duration=1,user=testUser,serverName=mediazone-dev,resource=http://mediazone-admin-dev.vrt.be/rest/client/v1/assestsources,httpMethod=GET,transactionUUID=mediazone-dev-"));
	}
	
}
