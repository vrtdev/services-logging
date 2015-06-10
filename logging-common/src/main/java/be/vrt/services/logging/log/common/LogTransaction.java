package be.vrt.services.logging.log.common;

import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogTransaction implements Constants{

	public static void resetThread(){
		MDC.remove(TRANSACTION_ID);
		MDC.remove(BREADCRUM_COUNTER);
		MDC.remove(FLOW_ID);
	}
	
	public static String id() {
		if (MDC.get(TRANSACTION_ID) == null) {
			String uuid = generateTransactionId();
			MDC.put(TRANSACTION_ID, uuid);
			TransactionRegistery.register(uuid, flow());
		}
		return MDC.get(TRANSACTION_ID);
	}

	public static int breadCrum() {
		if (MDC.get(BREADCRUM_COUNTER) == null) {
			MDC.put(BREADCRUM_COUNTER, "0");
		}
		return Integer.valueOf(MDC.get(BREADCRUM_COUNTER));
	}

	public static void increaseBreadCrum() {
		Integer i = breadCrum();
		i++;
		MDC.put(BREADCRUM_COUNTER, "" + i);
	}

	public static void decreaseBreadCrum() {
		Integer i = breadCrum();
		i--;
		MDC.put(BREADCRUM_COUNTER, "" + i);
	}

	public static String flow() {
		return MDC.get(FLOW_ID);
	}

	public static String generateTransactionId() {
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			hostname = "[unknown]";
		}
		String uuid = hostname + "-" + UUID.randomUUID().toString();
		return uuid;
	}

	public static String createFlowId(String flowId, String user) {
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
		String currentFlowId = flow();
		if (currentFlowId != null && !currentFlowId.equals(flowid)) {
			LoggerFactory.getLogger(LogTransaction.class).info("update FlowId : [{}] ==> [{}]", flow(), flowid);
		}
		MDC.put(FLOW_ID, flowid);
	}

}
