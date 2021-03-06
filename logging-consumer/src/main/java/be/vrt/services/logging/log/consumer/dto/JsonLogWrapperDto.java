package be.vrt.services.logging.log.consumer.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonLogWrapperDto {

	private Date date;
	private Date logDate;
	private String transactionId;
	private String hostName;
	private String flowId;
	private String className;
	private String methodName;
	private int breadCrumb;
	private int lineNumber;
	private int subFlow;
	
	private String user;
	private String logLevel;
	
	private String loggerName;
	private String logComment;
	
	private List<String> ids = new LinkedList<>();
	private List<String> tags = new LinkedList<>();
	private Map<String, Object> content = new HashMap<>();
	private Object environmentInfo;

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

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

	public Map<String, Object> getContent() {
		return content;
	}

	public void setContent(Map<String, Object> content) {
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

	public String getLogComment() {
		return logComment;
	}

	public void setLogComment(String logComment) {
		this.logComment = logComment;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public int getBreadCrumb() {
		return breadCrumb;
	}

	public void setBreadCrumb(int breadCrumb) {
		this.breadCrumb = breadCrumb;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public int getSubFlow() {
		return subFlow;
	}

	public void setSubFlow(int subFlow) {
		this.subFlow = subFlow;
	}
	
	
	
}
