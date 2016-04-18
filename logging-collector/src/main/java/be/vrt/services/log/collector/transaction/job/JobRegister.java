package be.vrt.services.log.collector.transaction.job;

import be.vrt.services.log.collector.exception.FailureException;
import be.vrt.services.log.collector.transaction.dto.JobTransactionLogDto;
import be.vrt.services.logging.log.common.LogTransaction;
import be.vrt.services.logging.log.common.dto.LogType;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JobRegister {

	private static final Logger log = LoggerFactory.getLogger(JobRegister.class);

	public static <T> T execute(String jobName, JobCallable<T> jobCallable) throws FailureException {
		T result = null;
		LogTransaction.startNewTransaction();
		JobTransactionLogDto transaction = new JobTransactionLogDto();
		transaction.setStatus(LogType.OK);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		transaction.setStartDate(new Date(stopWatch.getStartTime()));
		transaction.setResource(jobName);
		transaction.setFlowId(LogTransaction.flow());
		transaction.setTransactionId(LogTransaction.id());
		transaction.setUser(System.getProperty("user.name"));
		transaction.setServerName(LogTransaction.hostname());

		try {
			result = jobCallable.call();
		} catch (FailureException e) {
			transaction.setErrorReason(e.getMessage());
			transaction.setStatus(LogType.FAILED);
			throw e;
		} catch (Throwable e) {
			transaction.setErrorReason(e.getMessage());
			transaction.setStatus(jobCallable.onError());
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
