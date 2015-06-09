package be.vrt.services.logging.log.common.dto;

import be.vrt.services.logging.log.common.DelayedLogObject;
import java.util.Date;
import java.util.Map;

public abstract class AbstractTransactionLog implements DelayedLogObject{

	protected Date startDate;
	protected long duration;
	protected String user;
	protected String serverName;
	protected String resource;
	protected String transactionId;
	protected String flowId;
	protected Map<String, String> parameters;

	public static enum Type {

		OK, FAILED, ERROR
	};

	private Type status;
	private String errorReason;

	public abstract String getType();

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
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
}
