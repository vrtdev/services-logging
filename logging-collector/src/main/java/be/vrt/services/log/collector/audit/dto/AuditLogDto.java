package be.vrt.services.log.collector.audit.dto;

import be.vrt.services.log.collector.audit.AuditLevelType;
import be.vrt.services.logging.log.common.DelayedLogObject;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AuditLogDto  implements DelayedLogObject{

	private List<Object> arguments;
	private Object response;
	private String method;
	private String className;
	private Date startDate;
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

	@Override
	public String toString() {
		//return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		return new ReflectionToStringBuilder(this, new RecursiveToStringStyle()).toString();
	}
}
