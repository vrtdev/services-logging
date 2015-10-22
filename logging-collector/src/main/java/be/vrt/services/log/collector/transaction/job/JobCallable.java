package be.vrt.services.log.collector.transaction.job;

import be.vrt.services.log.collector.exception.FailureException;

@FunctionalInterface
public interface JobCallable<V> {

	V call() throws FailureException;
}
