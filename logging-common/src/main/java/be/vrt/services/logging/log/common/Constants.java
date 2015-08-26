package be.vrt.services.logging.log.common;

public interface Constants {

	// Also used as plain text in logback.xml, changing not advised
	public final static String TRANSACTION_ID = "X-Log-Transaction-Id";
		public final static String BREADCRUMB_COUNTER = "X-Log-Breadcrumb-Counter";

	public final static String FLOW_ID = "X-Log-Flow-Id";
	public final static String ORIGIN_USER = "X-Log-Origin-User";
	
	public final static String USER = "X-Log-User";

}
