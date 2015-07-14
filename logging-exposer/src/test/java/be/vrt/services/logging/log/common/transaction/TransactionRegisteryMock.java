package be.vrt.services.logging.log.common.transaction;

import org.mockito.Mockito;

public class TransactionRegisteryMock {
	
	public static TransactionRegistery doSpy() {
		TransactionRegistery.instance = Mockito.spy(TransactionRegistery.class);
		return TransactionRegistery.instance;
	}
	
}
