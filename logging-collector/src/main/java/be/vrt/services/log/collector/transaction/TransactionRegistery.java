package be.vrt.services.log.collector.transaction;

import be.vrt.services.log.collector.transaction.dto.AbstractTransactionLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionRegistery {

	private static TransactionRegistery instance = new TransactionRegistery();

	private List<AbstractTransactionLog> abstractTransactionLogs =  Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(100));
	
	
	public void registerTransactionLocal(AbstractTransactionLog transaction) {
		while(abstractTransactionLogs.size() > 100 ){
			abstractTransactionLogs.remove(0);
		}
		abstractTransactionLogs.add(transaction);
	}
	
	
	public static void register(AbstractTransactionLog transaction) {
		instance.registerTransactionLocal(transaction);
	}
	
	public static List<AbstractTransactionLog> list() {
		return new ArrayList<>(instance.abstractTransactionLogs);
	}
}
