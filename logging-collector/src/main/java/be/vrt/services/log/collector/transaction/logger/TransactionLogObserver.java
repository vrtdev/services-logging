package be.vrt.services.log.collector.transaction.logger;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Observable;
import java.util.Observer;

public class TransactionLogObserver implements Observer, InitializingBean {

	private final static Logger log = LoggerFactory.getLogger(TransactionLogObserver.class);

	@Override
	public void update(Observable observable, Object arg) {
		if (arg instanceof AbstractTransactionLog) {
			AbstractTransactionLog transaction = (AbstractTransactionLog) arg;
			switch (transaction.getStatus()) {
				case ERROR:
					log.error(transaction.getErrorReason());
					break;
				case FAILED:
					log.warn(transaction.getErrorReason());
					break;
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		TransactionRegistery.registerTransactionObserver(this);
	}
}
