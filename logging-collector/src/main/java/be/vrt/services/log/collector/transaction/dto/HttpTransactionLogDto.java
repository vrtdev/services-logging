package be.vrt.services.log.collector.transaction.dto;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import be.vrt.services.logging.log.common.dto.LogType;

public class HttpTransactionLogDto extends AbstractTransactionLog {

	private String httpMethod;
	private int responseStatus;
	private String fullUrl;
	

	@Override
	public String getType() {
		return "HTTP";
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public int getResponseStatus() {
		return responseStatus;
	}
	
	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}

	public void responseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
		if (responseStatus < 400) {
			setStatus(LogType.OK);
		} else if (responseStatus < 500) {
			setStatus(LogType.FAILED);
			setErrorReason(" HTTP Client error: "+responseStatus);
		} else {
			setStatus(LogType.ERROR);
			setErrorReason(" HTTP Server error: "+responseStatus);
		}
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
}
