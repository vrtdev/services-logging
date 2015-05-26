package be.vrt.services.log.collector.transaction.dto;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import java.util.Map;

public class AmqpTransactionLogDto extends AbstractTransactionLog {

	public static enum Type{
		OK, FAILED, ERROR
	};
	
	private Type status;
	private String errorReason;
	private String exchange;
	private String routingKey;
	private String queueName;
	private Map<String, Object> headers;
	
	@Override
	public String getType() {
		return "AMQP";
	}

	public Type getStatus() {
		return status;
	}

	public void setStatus(Type status) {
		this.status = status;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}
	
	
}
