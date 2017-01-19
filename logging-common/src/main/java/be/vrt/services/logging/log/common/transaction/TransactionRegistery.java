package be.vrt.services.logging.log.common.transaction;

import be.vrt.services.logging.log.common.LogTransaction;
import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionRegistery extends Observable {

	private final Logger log = LoggerFactory.getLogger(TransactionRegistery.class);

	static TransactionRegistery instance = new TransactionRegistery();

	int bufferSize = 300;
	int bufferSizeIds = 500;

	final List<AbstractTransactionLog> transactionLogs = Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(bufferSize));
	final List<AbstractTransactionLog> transactionFailedLogs = Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(bufferSize));
	final List<AbstractTransactionLog> transactionErrorLogs = Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(bufferSize));

	final Map<String, String> flowIds = Collections.synchronizedMap(new HashMap<String, String>());

	final List<TransactionIdLog> transactionIds = Collections.synchronizedList(new ArrayList<TransactionIdLog>(bufferSizeIds));

	TransactionRegistery() {
		LogTransaction.startNewFlow("SYSTEM-STARTUP");
		_registerStaticFlow("SYSTEM-STARTUP");
	}

	public void registerTransactionLocal(AbstractTransactionLog transaction) {
		addToFixedSizeQueue(transactionLogs, transaction, bufferSize);
		if (!LogTransaction.isSuppressed()) {
			switch (transaction.getStatus()) {
				case FAILED:
					log.warn("{} [{}]  [{}] -> {} ", transaction.getStartDate(), transaction.getTransactionId(), transaction.getFlowId(), transaction.getErrorReason());
					addToFixedSizeQueue(transactionFailedLogs, transaction, bufferSize);
					break;
				case ERROR:
					log.error("{} [{}] [{}] -> {} ", transaction.getStartDate(), transaction.getTransactionId(), transaction.getFlowId(), transaction.getErrorReason());
					addToFixedSizeQueue(transactionErrorLogs, transaction, bufferSize);
			}
		}
		setChanged();
		notifyObservers(transaction);
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
		if (!LogTransaction.isSuppressed()) {
			instance.registerTransactionLocal(transaction);
		}
	}

	public static void registerTransaction() {
		instance.registerTransactionId(LogTransaction.id(), LogTransaction.flow());
	}

	public static void registerTransactionObserver(Observer observer) {
		instance.addObserver(observer);
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

	public static TransactionRegistery instance() {
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

	public static void registerStaticFlow(String name) {
		instance._registerStaticFlow(name);
	}

	private synchronized void _registerStaticFlow(String name) {
		String flowId = LogTransaction.flow();
		if (flowIds.containsKey(name)) {
			log.warn("Updating flowId [{}] {} >> {}", name, flowIds.get(name), flowId);
		}
		flowIds.put(name, flowId);
	}

	public static Map<String, String> listStaticFlows() {
		return new HashMap<>(instance.flowIds);
	}

}
