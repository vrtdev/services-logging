package be.vrt.services.log.collector.audit.dto;

public class ErrorDto {
	
	private String message;
	private String className;
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
}
