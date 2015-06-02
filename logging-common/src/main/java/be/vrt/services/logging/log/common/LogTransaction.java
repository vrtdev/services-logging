package be.vrt.services.logging.log.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogTransaction {

	public static String id() {
		return MDC.get(Constants.TRANSACTION_ID);
	}

	public static String flow() {
		return MDC.get(Constants.FLOW_ID);
	}

	public static String generateFlowId(String user) {
		if (user == null) {
			user = "NOT_SPECIFIED";
		}
		user = user.replaceAll("[^-_.@a-zA-Z0-9]*", "");
		StringBuffer buffer = new StringBuffer(UUID.randomUUID().toString());
		buffer.append("-").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
		buffer.append("-").append(user);
		updateFlowId(buffer.toString());
		return buffer.toString();
	}

	public static void updateFlowId(String flowid) {
		LoggerFactory.getLogger(LogTransaction.class).info("update FlowId : "+flow()+" ==> " + flowid, flow() , flowid);
		MDC.put(Constants.FLOW_ID, flowid);
	}

}
