package be.vrt.services.logging.log.common;

import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogTransaction implements Constants {

	private static String hostname;
	private static final String TAG_LIST = "tags";
	private static final String TAG_SEPERATOR = ",";
	private static final String ID_LIST = "ids";
	private static final String ID_SEPERATOR = ",";

	private static final String DATA_CLEANUP_REGEX = "[^-_\\w]";

	public static void resetThread() {
		MDC.remove(TRANSACTION_ID);
		MDC.remove(BREADCRUMB_COUNTER);
		MDC.remove(FLOW_ID);
		MDC.remove(USER);
		MDC.remove(TAG_LIST);
		MDC.remove(ID_LIST);

	}

	public static void registerUser(String user) {
		if (user == null) { // We do this as the morons of JBoss logging cannot handle a simple thing as null values in a Set...
			user = "UNKNOWN";
		}
		MDC.put(USER, user);
	}

	public static void registerId(String id) {
		id = id == null ? null : id.replaceAll(DATA_CLEANUP_REGEX, "");
		if (isEmpty(id)) {
			return;
		}

		String idList = MDC.get(ID_LIST);
		if (idList == null) {
			idList = id;
		} else if (!idList.contains(id)) {
			idList += ID_SEPERATOR + id;
		}
		MDC.put(ID_LIST, idList);
	}

	public static List<String> listIds() {

		String tagList = MDC.get(ID_LIST);
		if (isEmpty(tagList)) {
			return new LinkedList<>();
		} else {
			return new LinkedList<>(Arrays.asList(tagList.split(ID_SEPERATOR)));
		}
	}

	public static void tagTransaction(String tag) {
		tag = tag == null ? null : tag.replaceAll(DATA_CLEANUP_REGEX, "");

		if (isEmpty(tag)) {
			return;
		}

		String tagList = MDC.get(TAG_LIST);

		if (tagList == null) {
			tagList = tag;
		} else if (!tagList.contains(tag)) {
			tagList += TAG_SEPERATOR + tag;

		}
		MDC.put(TAG_LIST, tagList);
	}

	public static List<String> listTags() {

		String tagList = MDC.get(TAG_LIST);
		if (isEmpty(tagList)) {
			return new LinkedList<>();
		} else {
			return new LinkedList<>(Arrays.asList(tagList.split(TAG_SEPERATOR)));
		}
	}

	public static void untagTransaction(String tag) {

		tag = tag == null ? null : tag.replaceAll(DATA_CLEANUP_REGEX, "");

		String tags = MDC.get(TAG_LIST);

		if (tags == null || tag == null) {
			return;
		}

		List<String> tagist = new LinkedList<>(Arrays.asList(tags.split(TAG_SEPERATOR)));
		tagist.remove(tag);

		if (tagist.isEmpty()) {
			MDC.remove(TAG_LIST);
		} else {

			MDC.put(TAG_LIST, join(tagist, TAG_SEPERATOR));
		}

	}

	public static boolean isTaggedWith(String tag) {
		String tagList = MDC.get(TAG_LIST);
		if (tagList == null) {
			return false;
		} else {
			return Arrays.asList(tagList.split(TAG_SEPERATOR)).contains(tag);
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
		return MDC.get(FLOW_ID) == null ? createFlowId(null, null) : MDC.get(FLOW_ID);
	}

	private static String join(List<String> strings, String d) {
		if (strings.size() == 0) {
			return "";
		}
		Iterator<String> sit = strings.iterator();
		String s = sit.next();
		if (strings.size() == 1) {
			return s;
		}

		String rtnValue = s;
		while (sit.hasNext()) {
			d += d + sit.next();
		}
		return rtnValue;
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
		String currentFlowId =  MDC.get(FLOW_ID);
		if (currentFlowId != null && !currentFlowId.equals(flowid)) {
			LoggerFactory.getLogger(LogTransaction.class).info("update FlowId : [{}] ==> [{}]", flow(), flowid);
		}
		MDC.put(FLOW_ID, flowid);
	}

	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

}
