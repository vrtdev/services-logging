package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.log.collector.exception.FailureException;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BasicBreadcrumbAuditAspectTest {

    public static final String SHORT_METHOD_NAME = "short";
    public static final String MESSAGE = "aMessage";
    @Mock
    ProceedingJoinPoint joinPoint;
    @Mock
    private MethodSignature signature;
    @Mock
    private Appender appender;

    @Captor
    private ArgumentCaptor<LoggingEvent> argumentCaptor;

    @Before
    public void setUp(){
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(appender);
    }

    @After
    public void tearDown(){
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(appender);
    }

    @Test
    public void handleJoinPoint_whenCalled_thenJoinPointIsProceeded() throws Throwable {
        when(joinPoint.getTarget()).thenReturn("aTarget");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(SHORT_METHOD_NAME);
        BasicBreadcrumbAuditAspect basicBreadcrumbAuditAspect = new BasicBreadcrumbAuditAspect();

        basicBreadcrumbAuditAspect.handleJoinPoint(joinPoint);
        verify(joinPoint).proceed();
        verify(appender).doAppend(argumentCaptor.capture());
        LoggingEvent loggingEvent = argumentCaptor.getValue();
        assertTrue(loggingEvent.getFormattedMessage().contains(SHORT_METHOD_NAME));
    }

    @Test(expected = FailureException.class)
    public void handleJoinPoint_whenException_thenLoggedAndRethrown() throws Throwable {
        when(joinPoint.getTarget()).thenReturn("aTarget");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(SHORT_METHOD_NAME);
        when(joinPoint.proceed()).thenThrow(new FailureException(MESSAGE));
        BasicBreadcrumbAuditAspect basicBreadcrumbAuditAspect = new BasicBreadcrumbAuditAspect();

        basicBreadcrumbAuditAspect.handleJoinPoint(joinPoint);
        verify(joinPoint).proceed();
        verify(appender).doAppend(argumentCaptor.capture());
        LoggingEvent loggingEvent = argumentCaptor.getValue();
        assertTrue(loggingEvent.getFormattedMessage().contains(SHORT_METHOD_NAME));
        assertTrue(loggingEvent.getFormattedMessage().contains(MESSAGE));
    }
}