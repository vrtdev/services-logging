package be.vrt.services.log.collector.audit.dto;

import be.vrt.services.log.collector.audit.AuditLevelType;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AuditLogDto {

	private List<Object> arguments;
	private Object response;
	private String method;
	private String className;
	private Date logDate;
	private long duration;
	private AuditLevelType auditLevel = AuditLevelType.OK;

	public List<Object> getArguments() {
		return arguments;
	}

	public void setArguments(List<Object> arguments) {
		this.arguments = arguments;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public AuditLevelType getAuditLevel() {
		return auditLevel;
	}

	public void setAuditLevel(AuditLevelType auditLevel) {
		this.auditLevel = auditLevel;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		//return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		return new ReflectionToStringBuilder(this, new RecursiveToStringStyle()).toString();
	}
}
