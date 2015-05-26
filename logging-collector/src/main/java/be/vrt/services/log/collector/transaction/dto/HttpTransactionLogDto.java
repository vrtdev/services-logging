package be.vrt.services.log.collector.transaction.dto;


import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class HttpTransactionLogDto extends AbstractTransactionLog {
	
	private String httpMethod;
	private int responseStatus;

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

	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
