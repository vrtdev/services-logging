/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.vrt.services.logging.log.consumer.appender;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

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
		Object[] obj = new Object[1];
		
		when(logEvent.getMessage()).thenReturn("Test log comment");
		when(logEvent.getLoggerName()).thenReturn("Test logger name");
		when(logEvent.getTimeStamp()).thenReturn(5L);
		when(logEvent.getCallerData()).thenReturn(stack);
		when(logEvent.getArgumentArray()).thenReturn(obj);
		when(logEvent.getLevel()).thenReturn(Level.INFO);
		
		elasticSearchAppender.append(logEvent);
		
		verify(elasticSearchAppender).persist(anyString());
	}
	
}
