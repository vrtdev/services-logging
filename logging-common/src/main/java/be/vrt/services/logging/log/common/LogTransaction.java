package be.vrt.services.logging.log.common;

import be.vrt.services.logging.log.common.dto.ForkFlowDto;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;

public class LogTransaction implements Constants {

	private static Logger LOG = LoggerFactory.getLogger("LogTransaction");

	private static String hostname;
	private static final String TAG_LIST = "tags";
	private static final String ID_LIST = "ids";
	private static final String SUBFLOWS_COUNT = "0";
	public static final String PARENT = "PARENT";

	// TAGS
	public static final String SUPPRESSED = "SUPPRESSED";

	// Used in code
	private static final String TAG_SEPERATOR = ",";
	private static final String ID_SEPERATOR = ",";
	private static final String DATA_CLEANUP_REGEX = "[^-_\\w]";

	private static final ThreadLocal<NumberFormat> numberFormat = new ThreadLocal<NumberFormat>() {
		@Override
		public NumberFormat initialValue() {
			return new DecimalFormat("00000000");
		}
	};

	public static void resetThread() {
		MDC.remove(TRANSACTION_ID);
		MDC.remove(BREADCRUMB_COUNTER);
		MDC.remove(FLOW_ID);
		MDC.remove(USER);
		MDC.remove(TAG_LIST);
		MDC.remove(ID_LIST);
		MDC.remove(PARENT);
		MDC.remove(SUBFLOWS_COUNT);

	}

	public static void registerUser(String user) {
		if (user == null) {
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

	public static void createChildFlow(String msg) {
		MDC.put(SUBFLOWS_COUNT, Integer.toString(nbrOfSubflow()+1));
		ForkFlowDto dto = new ForkFlowDto();
		dto.setParentFlowId(flow());
		dto.setChildFlowId(createFlowId(null, user()));
		LOG.info("### Creating SubFlow: {}", msg, dto);
		
		StringStack parentStack = new StringStack(MDC.get(PARENT));
		parentStack.push(dto.getParentFlowId());
		MDC.put(PARENT, parentStack.toString());
		MDC.put(FLOW_ID, dto.getChildFlowId());
		
	}

	public static void backToParentFlow(String msg) {
		StringStack parentStack = new StringStack(MDC.get(PARENT));
		String parent = parentStack.pop();
		ForkFlowDto dto = new ForkFlowDto();
		dto.setParentFlowId(parent);
		dto.setChildFlowId(flow());
		LOG.info("### Back to parent flow: {}", msg, dto);
		
		MDC.put(PARENT, parentStack.toString());
		MDC.put(FLOW_ID, parent);
		
	}

	public static int nbrOfSubflow() {
		String subs = MDC.get(SUBFLOWS_COUNT);
		return subs == null ? 0 : Integer.parseInt(subs);
	}

	public static void logSuppress(String msg) {
		if (msg != null) {
			LOG.debug("Logging suppressed: {}", msg);
		}
		tagTransaction(SUPPRESSED);
	}

	public static void logUnsuppress(String msg) {
		untagTransaction(SUPPRESSED);
		if (msg != null) {
			LOG.debug("Logging unsuppressed: {}", msg);
		}
	}
	
	public static boolean isSuppressed() {
		return isTaggedWith(SUPPRESSED);
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
		String user = MDC.get(USER);
		return user == null ? "NOT_SPECIFIED" : user;
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

	public static String hostname() {
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
		MDC.put(USER, user);
		user = user.replaceAll("[^-_.@a-zA-Z0-9]*", "");
		StringBuffer buffer = new StringBuffer(UUID.randomUUID().toString());
		buffer.append("-").append(numberFormat.get().format(nbrOfSubflow()));
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
		String currentFlowId = MDC.get(FLOW_ID);
		if (currentFlowId != null && !currentFlowId.equals(flowid)) {
			LOG.info("update FlowId : [{}] ==> [{}]", flow(), flowid);
		}
		MDC.put(FLOW_ID, flowid);
	}

	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

}
