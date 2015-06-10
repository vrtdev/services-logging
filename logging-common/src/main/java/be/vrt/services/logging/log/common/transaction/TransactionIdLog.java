package be.vrt.services.logging.log.common.transaction;

import java.util.Date;

public class TransactionIdLog {

	private final Date date;
	private final String transactionId;
	private final String flowId;

	public TransactionIdLog(String transactionId, String flowId) {
		this.date = new Date();
		this.transactionId = transactionId;
		this.flowId = flowId;
	}

	public Date getDate() {
		return date;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getFlowId() {
		return flowId;
	}
}
