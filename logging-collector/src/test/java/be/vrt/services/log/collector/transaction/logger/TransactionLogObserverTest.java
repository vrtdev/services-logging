package be.vrt.services.log.collector.transaction.logger;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static be.vrt.services.logging.log.common.dto.AbstractTransactionLog.Type.ERROR;
import static be.vrt.services.logging.log.common.dto.AbstractTransactionLog.Type.FAILED;
import static be.vrt.services.logging.log.common.dto.AbstractTransactionLog.Type.OK;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionLogObserverTest {

	@Mock
	private AbstractTransactionLog transaction;

	@InjectMocks
	private TransactionLogObserver transactionLogObserver;

	@Test
	public void update_givenPassedObjIsATransaction_whenStatusIsOk_thenNoMessageIsExtractedFromTransaction() {
		when(transaction.getStatus()).thenReturn(OK);

		transactionLogObserver.update(null, transaction);

		verify(transaction, times(0)).getErrorReason();
	}

	@Test
	public void update_givenPassedObjIsATransaction_whenStatusIsError_thenErrorMessageIsExtractedFromTransaction() {
		when(transaction.getStatus()).thenReturn(ERROR);

		transactionLogObserver.update(null, transaction);

		verify(transaction, times(1)).getErrorReason();
	}

	@Test
	public void update_givenPassedObjIsATransaction_whenStatusIsFailed_thenErrorMessageIsExtractedFromTransaction() {
		when(transaction.getStatus()).thenReturn(FAILED);

		transactionLogObserver.update(null, transaction);

		verify(transaction, times(1)).getErrorReason();
	}

	@Test
	public void test() throws Exception {
		transactionLogObserver.afterPropertiesSet();

		int numberOfObservers = TransactionRegistery.instance().countObservers();

		assertTrue(numberOfObservers == 1);
	}

}
