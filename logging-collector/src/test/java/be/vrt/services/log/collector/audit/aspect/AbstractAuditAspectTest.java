package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.log.collector.audit.AuditLevelType;
import be.vrt.services.log.collector.exception.ErrorException;
import be.vrt.services.log.collector.exception.FailureException;
import be.vrt.services.logging.log.common.LogTransaction;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
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

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.googlecode.catchexception.CatchException.verifyException;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractAuditAspectTest {

    private static final String METHOD = "short";
    private static final String TYPE = "you're not my type";
    private static final List<be.vrt.services.logging.api.audit.annotation.Level> LEVELS_WO_OFF_ALL =
            Arrays.asList(be.vrt.services.logging.api.audit.annotation.Level.values())
                    .stream()
                    .filter(Predicate.isEqual(be.vrt.services.logging.api.audit.annotation.Level.OFF).negate())
                    .filter(Predicate.isEqual(be.vrt.services.logging.api.audit.annotation.Level.ALL).negate())
                    .collect(Collectors.toList());


    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;
    @Mock
    private MethodSignature signature;
    @Mock
    private Appender<ILoggingEvent> appender;

    @Captor
    private ArgumentCaptor<ILoggingEvent> argumentCaptor;

    private AbstractAuditAspect testInstance;

    @Before
    public void setUp() throws NoSuchMethodException {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.ALL);
        logger.addAppender(appender);
        testInstance = new AbstractAuditAspect() {
            @Override
            protected String getType() {
                return TYPE;
            }
        };
        LogTransaction.setLevel(Level.INFO.levelStr);
        wireMocks();
    }

    @After
    public void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(appender);
    }

    @Test
    public void handleJoinPoint_whenNoException_thenLevelIsOk() throws Throwable {
        testInstance.handleJoinPoint(proceedingJoinPoint);

        verify(appender).doAppend(argumentCaptor.capture());
        assertLoggingEvent(argumentCaptor.getValue(), AuditLevelType.OK, Level.INFO);
    }

    @Test
    public void handleJoinPoint_whenFailureException_thenLevelIsFail() throws Throwable {
        when(proceedingJoinPoint.proceed()).thenThrow(new FailureException());

        verifyException(testInstance, FailureException.class).handleJoinPoint(proceedingJoinPoint);

        verify(appender).doAppend(argumentCaptor.capture());
        assertLoggingEvent(argumentCaptor.getValue(), AuditLevelType.FAIL, Level.INFO);
    }

    @Test
    public void handleJoinPoint_whenErrorException_thenLevelIsError() throws Throwable {
        when(proceedingJoinPoint.proceed()).thenThrow(new ErrorException());

        verifyException(testInstance, ErrorException.class).handleJoinPoint(proceedingJoinPoint);

        verify(appender).doAppend(argumentCaptor.capture());
        assertLoggingEvent(argumentCaptor.getValue(), AuditLevelType.ERROR, Level.INFO);
    }

    @Test
    public void handleJoinPoint_whenLogTransactionLevel_thenLevelSame() throws Throwable {
        for (be.vrt.services.logging.api.audit.annotation.Level l : LEVELS_WO_OFF_ALL) {
            LogTransaction.setLevel(l.name());

            testInstance.handleJoinPoint(proceedingJoinPoint);

            verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
            assertLoggingEvent(argumentCaptor.getValue(), AuditLevelType.OK, Level.valueOf(l.name()));
        }
    }

    @Test
    public void handleJoinPoint_whenLogTransactionLevelAll_thenLevelError() throws Throwable {
        LogTransaction.setLevel(Level.ALL.levelStr);

        testInstance.handleJoinPoint(proceedingJoinPoint);

        verify(appender, atLeastOnce()).doAppend(argumentCaptor.capture());
        assertLoggingEvent(argumentCaptor.getValue(), AuditLevelType.OK, Level.ERROR);
    }

    @Test
    public void handleJoinPoint_whenLogTransactionLevelOff_thenNoLoggingEvent() throws Throwable {
        LogTransaction.setLevel(Level.OFF.levelStr);

        testInstance.handleJoinPoint(proceedingJoinPoint);

        verify(appender, never()).doAppend(any(ILoggingEvent.class));
    }

    private void assertLoggingEvent(ILoggingEvent loggingEvent, AuditLevelType auditLevelType, Level level) {
        assertThat(loggingEvent.getFormattedMessage(), containsString(auditLevelType.name()));
        assertThat(loggingEvent.getFormattedMessage(), containsString(METHOD));
        assertThat(loggingEvent.getFormattedMessage(), containsString(TYPE));
        assertThat(loggingEvent.getLevel(), is(level));
    }

    private void wireMocks() throws NoSuchMethodException {
        when(proceedingJoinPoint.getTarget()).thenReturn("string");
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new String[]{"hoedjeVanPapier"});
        when(signature.toShortString()).thenReturn(METHOD);
        when(signature.getMethod()).thenReturn(String.class.getMethod("toString"));
    }
}