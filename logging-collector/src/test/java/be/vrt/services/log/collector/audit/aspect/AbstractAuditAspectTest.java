package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.log.collector.audit.AuditLevelType;
import be.vrt.services.log.collector.exception.ErrorException;
import be.vrt.services.log.collector.exception.FailureException;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractAuditAspectTest {

    public static final String METHOD = "short";
    public static final String TYPE = "you're not my type";
    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;
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
    public void handleJoinPoint_whenNoException_thenLevelIsOk() throws Throwable {
        TestAbstractAuditAspect abstractAuditAspect = new TestAbstractAuditAspect();
        when(proceedingJoinPoint.getTarget()).thenReturn("string");
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new String[]{"hoedjeVanPapier"});
        when(signature.toShortString()).thenReturn(METHOD);
        when(signature.getMethod()).thenReturn(String.class.getMethod("toString"));

        abstractAuditAspect.handleJoinPoint(proceedingJoinPoint);

        verify(appender).doAppend(argumentCaptor.capture());
        LoggingEvent loggingEvent = argumentCaptor.getValue();
        assertTrue(loggingEvent.getFormattedMessage().contains(AuditLevelType.OK.name()));
        assertTrue(loggingEvent.getFormattedMessage().contains(METHOD));
        assertTrue(loggingEvent.getFormattedMessage().contains(TYPE));
    }

    @Test(expected = FailureException.class)
    public void handleJoinPoint_whenFailureException_thenLevelIsFail() throws Throwable {
        TestAbstractAuditAspect abstractAuditAspect = new TestAbstractAuditAspect();
        when(proceedingJoinPoint.getTarget()).thenReturn("string");
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new String[]{"hoedjeVanPapier"});
        when(signature.toShortString()).thenReturn(METHOD);
        when(signature.getMethod()).thenReturn(String.class.getMethod("toString"));
        when(proceedingJoinPoint.proceed()).thenThrow(new FailureException());

        abstractAuditAspect.handleJoinPoint(proceedingJoinPoint);

        verify(appender).doAppend(argumentCaptor.capture());
        LoggingEvent loggingEvent = argumentCaptor.getValue();
        assertTrue(loggingEvent.getFormattedMessage().contains(AuditLevelType.FAIL.name()));
        assertTrue(loggingEvent.getFormattedMessage().contains(METHOD));
        assertTrue(loggingEvent.getFormattedMessage().contains(TYPE));
    }


    @Test(expected = ErrorException.class)
    public void handleJoinPoint_whenErrorException_thenLevelIsError() throws Throwable {
        TestAbstractAuditAspect abstractAuditAspect = new TestAbstractAuditAspect();
        when(proceedingJoinPoint.getTarget()).thenReturn("string");
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new String[]{"hoedjeVanPapier"});
        when(signature.toShortString()).thenReturn(METHOD);
        when(signature.getMethod()).thenReturn(String.class.getMethod("toString"));
        when(proceedingJoinPoint.proceed()).thenThrow(new ErrorException());

        abstractAuditAspect.handleJoinPoint(proceedingJoinPoint);

        verify(appender).doAppend(argumentCaptor.capture());
        LoggingEvent loggingEvent = argumentCaptor.getValue();
        assertTrue(loggingEvent.getFormattedMessage().contains(AuditLevelType.ERROR.name()));
        assertTrue(loggingEvent.getFormattedMessage().contains(METHOD));
        assertTrue(loggingEvent.getFormattedMessage().contains(TYPE));
    }

    private static class TestAbstractAuditAspect extends AbstractAuditAspect{
        @Override
        protected String getType() {
            return TYPE;
        }
    }

}