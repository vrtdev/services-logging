package be.vrt.services.logging.log.common.transaction;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionRegistery {

	private static final TransactionRegistery instance = new TransactionRegistery();

	private int bufferSize = 100;
	private int bufferSizeIds = 500;

	private final List<AbstractTransactionLog> transactionLogs = Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(100));
	private final List<AbstractTransactionLog> transactionFailedLogs = Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(100));
	private final List<AbstractTransactionLog> transactionErrorLogs = Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(100));

	private final List<TransactionIdLog> transactionIds = Collections.synchronizedList(new ArrayList<TransactionIdLog>(100));

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

	public static List<String> listFlowIds() {
		
		Set<String> flows = new HashSet<>();
		
		for (TransactionIdLog transactionId : instance.transactionIds) {
			
		}
		
		return new ArrayList<>();
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public static void setBufferSize(int size) {
		instance.bufferSize = size;
	}

	public int getBufferSizeIds() {
		return bufferSizeIds;
	}

	public void setBufferSizeIds(int bufferSizeIds) {
		this.bufferSizeIds = bufferSizeIds;
	}

}
