package be.vrt.services.logging.log.common.transaction;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionRegistery {

	private static final TransactionRegistery instance = new TransactionRegistery();

	private final List<AbstractTransactionLog> abstractTransactionLogs = Collections.synchronizedList(new ArrayList<AbstractTransactionLog>(1000));

	public void registerTransactionLocal(AbstractTransactionLog transaction) {
		while (abstractTransactionLogs.size() > 1000) {
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
