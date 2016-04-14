package be.vrt.services.log.collector.transaction.job;

import be.vrt.services.log.collector.exception.FailureException;
import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;


public interface JobCallable<V> {

	V call() throws FailureException;

	AbstractTransactionLog.Type onError();
}
