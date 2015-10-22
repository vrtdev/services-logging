package be.vrt.services.log.collector.transaction.job;

import be.vrt.services.log.collector.exception.FailureException;


public interface JobCallable<V> {

	V call() throws FailureException;
}
