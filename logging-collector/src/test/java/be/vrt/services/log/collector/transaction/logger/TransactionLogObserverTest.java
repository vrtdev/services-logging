package be.vrt.services.log.collector.transaction.logger;

import static be.vrt.services.logging.log.common.dto.AbstractTransactionLog.Type.ERROR;
import static be.vrt.services.logging.log.common.dto.AbstractTransactionLog.Type.FAILED;
import static be.vrt.services.logging.log.common.dto.AbstractTransactionLog.Type.OK;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

@RunWith(MockitoJUnitRunner.class)
public class TransactionLogObserverTest {

	@Mock
	private AbstractTransactionLog transaction;

	@Mock
	private Appender appender;

	@Captor
	private ArgumentCaptor<LoggingEvent> argumentCaptor;


	@InjectMocks
	private TransactionLogObserver transactionLogObserver;


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
	public void update_givenPassedObjIsATransaction_whenStatusIsOk_thenNoMessageIsExtractedFromTransaction() {
		when(transaction.getStatus()).thenReturn(OK);

		transactionLogObserver.update(null, transaction);

		verify(transaction).getStatus();
		verifyNoMoreInteractions(transaction);
	}

	@Test
	public void update_givenPassedObjIsATransaction_whenStatusIsError_thenErrorMessageIsExtractedFromTransaction() {
		when(transaction.getStatus()).thenReturn(ERROR);

		transactionLogObserver.update(null, transaction);

		verify(appender).doAppend(argumentCaptor.capture());
		LoggingEvent loggingEvent = argumentCaptor.getValue();
		Assert.assertEquals(loggingEvent.getLevel(), Level.ERROR);
	}

	@Test
	public void update_givenPassedObjIsATransaction_whenStatusIsFailed_thenErrorMessageIsExtractedFromTransaction() {
		when(transaction.getStatus()).thenReturn(FAILED);

		transactionLogObserver.update(null, transaction);

		verify(appender).doAppend(argumentCaptor.capture());
		LoggingEvent loggingEvent = argumentCaptor.getValue();
		Assert.assertEquals(loggingEvent.getLevel(), Level.WARN);
	}

	@Test
	public void test() throws Exception {
		transactionLogObserver.afterPropertiesSet();

		int numberOfObservers = TransactionRegistery.instance().countObservers();

		assertTrue(numberOfObservers == 1);
	}

}
