package be.vrt.services.logging.log.consumer.dto;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class JsonLogWrapperDto {

	private Date date;
	private String transactionId;
	private String hostName;
	private String className;
	private String methodName;
	private int lineNumber;
	
	private String loggerName;

	private List<String> ids = new LinkedList<>();
	private List<Object> content = new LinkedList<>();
	private Object environmentInfo;


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public List<Object> getContent() {
		return content;
	}

	public void setContent(List<Object> content) {
		this.content = content;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Object getEnvironmentInfo() {
		return environmentInfo;
	}

	public void setEnvironmentInfo(Object environmentInfo) {
		this.environmentInfo = environmentInfo;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}
	
	
}
