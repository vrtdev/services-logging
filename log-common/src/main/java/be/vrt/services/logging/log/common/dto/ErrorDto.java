package be.vrt.services.logging.log.common.dto;

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
