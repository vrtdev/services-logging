package be.vrt.services.logging.log.common;

import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogTransaction implements Constants {

	private static String hostname;
	private final static String TAG_LIST = "tags";

	public static void resetThread() {
		MDC.remove(TRANSACTION_ID);
		MDC.remove(BREADCRUMB_COUNTER);
		MDC.remove(FLOW_ID);
		MDC.remove(USER);

	}

	public static void registerUser(String user) {
		if (user == null) { // We do this as the morons of JBoss logging cannot handle a simple thing as null values in a Set...
			user = "UNKNOWN";
		}
		MDC.put(USER, user);
	}

	public static void tagTransaction(String tag) {

		String tagList = MDC.get(TAG_LIST);
		if (tagList == null) {
			tagList = tag;
		} else {
			tagList += "," + tag;
		}
		MDC.put(TAG_LIST, tagList);
	}

	public static boolean isTaggedWith(String tag) {
		String tagList = MDC.get(TAG_LIST);
		if (tagList == null) {
			return false;
		} else {
			return Arrays.asList(tagList.split(",")).contains(tag);
		}
	}

	public static String user() {
		return MDC.get(USER);
	}

	public static String id() {
		if (MDC.get(TRANSACTION_ID) == null) {
			String uuid = generateTransactionId();
			MDC.put(TRANSACTION_ID, uuid);
			TransactionRegistery.registerTransaction();
		}
		return MDC.get(TRANSACTION_ID);
	}

	public static int breadCrumb() {
		if (MDC.get(BREADCRUMB_COUNTER) == null) {
			MDC.put(BREADCRUMB_COUNTER, "0");
		}
		return Integer.valueOf(MDC.get(BREADCRUMB_COUNTER));
	}

	public static void increaseBreadCrumb() {
		Integer i = breadCrumb();
		i++;
		MDC.put(BREADCRUMB_COUNTER, "" + i);
	}

	public static void decreaseBreadCrumb() {
		Integer i = breadCrumb();
		i--;
		MDC.put(BREADCRUMB_COUNTER, "" + i);
	}

	public static String flow() {
		return MDC.get(FLOW_ID);
	}

	private static String hostname() {
		if (hostname == null) {
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException ex) {
				hostname = "[unknown]";
			}
		}
		return hostname;
	}

	public static String generateTransactionId() {

		String uuid = hostname() + "-" + UUID.randomUUID().toString();
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
		buffer.append("000");
		buffer.append("-").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
		buffer.append("-").append(user);
		updateFlowId(buffer.toString());
		return buffer.toString();
	}

	public static void startNewFlow(String user) {
		createFlowId(null, user);
	}

	public static void startNewTransaction() {
		String uuid = generateTransactionId();
		MDC.put(TRANSACTION_ID, uuid);
	}

	public static void updateFlowId(String flowid) {
		String currentFlowId = flow();
		if (currentFlowId != null && !currentFlowId.equals(flowid)) {
			LoggerFactory.getLogger(LogTransaction.class).info("update FlowId : [{}] ==> [{}]", flow(), flowid);
		}
		MDC.put(FLOW_ID, flowid);
	}

}
