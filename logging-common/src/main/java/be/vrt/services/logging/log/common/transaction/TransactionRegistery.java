package be.vrt.services.logging.log.common.transaction;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionRegistery {

	static TransactionRegistery instance = new TransactionRegistery();

	int bufferSize = 100;
	int bufferSizeIds = 500;

	final List<AbstractTransactionLog> transactionLogs = Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(bufferSize));
	final List<AbstractTransactionLog> transactionFailedLogs = Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(bufferSize));
	final List<AbstractTransactionLog> transactionErrorLogs = Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(bufferSize));

	final List<TransactionIdLog> transactionIds = Collections.synchronizedList(new ArrayList<TransactionIdLog>(bufferSizeIds));

	public void registerTransactionLocal(AbstractTransactionLog transaction) {
		addToFixedSizeQueue(transactionLogs, transaction, bufferSize);
		switch (transaction.getStatus()) {
			case FAILED:
				addToFixedSizeQueue(transactionFailedLogs, transaction, bufferSize);
				break;
			case ERROR:
				addToFixedSizeQueue(transactionErrorLogs, transaction, bufferSize);
		}
	}

	public void registerTransactionId(String id, String flowId) {
		addToFixedSizeQueue(transactionIds, new TransactionIdLog(id, flowId), bufferSize);
	}

	private static <T> void addToFixedSizeQueue(List<T> list, T item, int maxSize) {
		while (list.size() >= maxSize) {
			list.remove(0);
		}
		list.add(item);

	}

	public static void register(AbstractTransactionLog transaction) {
		instance.registerTransactionLocal(transaction);
	}

	public static void register(String id, String flowId) {
		instance.registerTransactionId(id, flowId);
	}

	public static List<AbstractTransactionLog> list() {
		return new ArrayList<>(instance.transactionLogs);
	}

	public static List<AbstractTransactionLog> listErrors() {
		return new ArrayList<>(instance.transactionErrorLogs);
	}

	public static List<AbstractTransactionLog> listFailures() {
		return new ArrayList<>(instance.transactionFailedLogs);
	}

	public static List<TransactionIdLog> listIds() {
		return new ArrayList<>(instance.transactionIds);
	}

	public static TransactionRegistery instance(){
		return instance;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int size) {
		instance.bufferSize = size;
	}

	public int getBufferSizeIds() {
		return bufferSizeIds;
	}

	public void setBufferSizeIds(int bufferSizeIds) {
		this.bufferSizeIds = bufferSizeIds;
	}

}
