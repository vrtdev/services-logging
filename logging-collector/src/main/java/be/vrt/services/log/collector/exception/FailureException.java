package be.vrt.services.log.collector.exception;

@SuppressWarnings("serial")
public class FailureException extends Exception {
	
	public FailureException() {
	}
	
	public FailureException(String message) {
		super(message);
	}

	public FailureException(Throwable cause) {
		super(cause);
	}

	public FailureException(String message, Throwable cause) {
		super(message, cause);
	}
}
