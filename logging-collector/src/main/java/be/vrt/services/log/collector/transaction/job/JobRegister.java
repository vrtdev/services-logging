package be.vrt.services.log.collector.transaction.job;

import be.vrt.services.log.collector.exception.FailureException;
import be.vrt.services.log.collector.transaction.dto.AmqpTransactionLogDto;
import be.vrt.services.log.collector.transaction.dto.JobTransactionLogDto;
import be.vrt.services.logging.log.common.LogTransaction;
import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Date;

public class JobRegister {

	public static <T> T execute(JobCallable<T> jobCallable) throws FailureException {
		T result = null;
		LogTransaction.startNewTransaction();
		JobTransactionLogDto transaction = new JobTransactionLogDto();
		transaction.setStatus(AbstractTransactionLog.Type.OK);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		transaction.setStartDate(new Date(stopWatch.getStartTime()));

		try {
			result = jobCallable.call();
		} catch (FailureException e) {
			transaction.setErrorReason(e.getMessage());
			transaction.setStatus(AmqpTransactionLogDto.Type.FAILED);
			throw e;
		} catch (Throwable e) {
			transaction.setErrorReason(e.getMessage());
			transaction.setStatus(AmqpTransactionLogDto.Type.ERROR);
			throw e;
		}finally {
			stopWatch.stop();
			transaction.setDuration(stopWatch.getTime());
			TransactionRegistery.register(transaction);
			LogTransaction.resetThread();
		}
		return result;
	}
}
