package be.vrt.services.logging.log.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogTransaction {

	public static String id() {
		if (MDC.get(Constants.TRANSACTION_ID) == null) {

		}
		return MDC.get(Constants.TRANSACTION_ID);
	}

	public static String flow() {
		return MDC.get(Constants.FLOW_ID);
	}

	public static String createTransactionId(String hostname) {
		if (hostname == null) {
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException ex) {
				hostname = "[unknown]";
			}
		}
		String uuid = hostname + "-" + UUID.randomUUID().toString();
		MDC.put(Constants.TRANSACTION_ID, uuid);

		return uuid;
	}

	public static String generateTransactionId() {
		return createTransactionId(null);
	}

	public static String generateFlowId(String flowId, String user) {
		if (flowId != null) {
			updateFlowId(flowId);
			return flowId;
		}

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
		if (flow() != null && !flow().equals(flowid)) {
			LoggerFactory.getLogger(LogTransaction.class).info("update FlowId : " + flow() + " ==> " + flowid, flow(), flowid);
		}
		MDC.put(Constants.FLOW_ID, flowid);
	}

}
