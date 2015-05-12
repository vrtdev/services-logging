package be.vrt.services.log.collector.transaction;

import be.vrt.services.logging.log.common.Constants;
import org.slf4j.MDC;

public class LogTransaction {

	public static String id(){
		return MDC.get(Constants.TRANSACTION_ID);
	}
}
