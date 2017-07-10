package be.vrt.services.logging.log.common;

import be.vrt.services.logging.log.common.dto.ForkFlowDto;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.join;

public class LogTransaction implements Constants {

    // TAGS
    public static final String SUPPRESSED = "SUPPRESSED";

    private static final Logger LOG = LoggerFactory.getLogger("LogTransaction");
    private static final String TAG_LIST = "tags";
    private static final String TAG_SEPERATOR = ",";
    private static final String ID_LIST = "ids";
    private static final String ID_SEPERATOR = ",";
    private static final String SUBFLOWS_COUNT = "0";
    private static final String PARENT = "PARENT";
    private static final String LEVEL = "LOG_LEVEL";
    private static final String DATA_CLEANUP_REGEX = "[^-_\\w]";
    private static final ThreadLocal<NumberFormat> numberFormat =
            ThreadLocal.withInitial(() -> new DecimalFormat("00000000"));
    private static final ThreadLocal<DateFormat> dateFormat =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
    private static final String DEFAULT_LEVEL = Level.INFO.levelStr;

    private static String hostname;
    private static String transactionPrefix;

    /**
     * WARNING: Changing hostname will impact the transactionId for all current transactions..!!
     */
    public static void appendTransactionPrefix(String prefix) {
        LOG.info("### Appending transPrefix: {} += {}", transactionPrefix, prefix);
        transactionPrefix = hostname() + "-" + prefix;
    }

    public static void resetThread() {
        MDC.remove(TRANSACTION_ID);
        MDC.remove(BREADCRUMB_COUNTER);
        MDC.remove(FLOW_ID);
        MDC.remove(USER);
        MDC.remove(TAG_LIST);
        MDC.remove(ID_LIST);
        MDC.remove(PARENT);
        MDC.remove(SUBFLOWS_COUNT);
        MDC.remove(LEVEL);
    }

    public static void registerUser(String user) {
        MDC.put(USER, ofNullable(user).orElse("UNKNOWN"));
    }

    static void registerId(String id) {
        addToMDC(id, ID_LIST, ID_SEPERATOR);
    }

    private static void addToMDC(String value, String key, String separator) {
        value = clean(value);
        if (isNotEmpty(value)) {
            final Set<String> values = getMDCSet(key, separator);
            if (values.add(value)) { putMDC(values, key, separator); }
        }
    }

    private static String clean(String value) {
        return value == null ? null : value.replaceAll(DATA_CLEANUP_REGEX, "");
    }

    public static void createChildFlow(String msg) {
        MDC.put(SUBFLOWS_COUNT, Integer.toString(nbrOfSubflow() + 1));
        ForkFlowDto dto = newForkFlowDto(flow(), createFlowId(null, user()));
        LOG.info("### Creating SubFlow: {}", msg, dto);

        StringStack parentStack = new StringStack(MDC.get(PARENT));
        parentStack.push(dto.getParentFlowId());
        MDC.put(PARENT, parentStack.toString());
        MDC.put(FLOW_ID, dto.getChildFlowId());

    }

    public static void backToParentFlow(String msg) {
        StringStack parentStack = new StringStack(MDC.get(PARENT));
        String parent = parentStack.pop();
        LOG.info("### Back to parent flow: {}", msg, newForkFlowDto(parent, flow()));

        MDC.put(PARENT, parentStack.toString());
        MDC.put(FLOW_ID, parent);
    }

    private static ForkFlowDto newForkFlowDto(String parent, String flow) {
        ForkFlowDto dto = new ForkFlowDto();
        dto.setParentFlowId(parent);
        dto.setChildFlowId(flow);
        return dto;
    }

    public static int nbrOfSubflow() {
        return Integer.parseInt(getMDC(SUBFLOWS_COUNT).orElse("0"));
    }

    public static void logSuppress(String msg) {
        tagTransaction(SUPPRESSED);
        if (msg != null) {
            LOG.debug("Logging suppressed: {}", msg);
        }
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
        return new LinkedList<>(getMDCSet(ID_LIST, ID_SEPERATOR));
    }

    private static Set<String> getMDCSet(String key, String separator) {
        return Pattern.compile(separator).splitAsStream(getMDC(key).orElse("")).collect(Collectors.toSet());
    }

    public static void tagTransaction(String tag) {
        addToMDC(tag, TAG_LIST, TAG_SEPERATOR);
    }

    public static List<String> listTags() {
        return new LinkedList<>(getMDCSet(TAG_LIST, TAG_SEPERATOR));
    }

    public static void untagTransaction(String tag) {
        final Set<String> tagSet = getMDCSet(TAG_LIST, TAG_SEPERATOR);
        if (isNotEmpty(tag) && tagSet.remove(clean(tag))) { putMDC(tagSet, TAG_LIST, TAG_SEPERATOR); }
    }

    private static void putMDC(Collection<String> values, String key, String separator) {
        if (null == values || values.isEmpty()) {
            MDC.remove(key);
        } else {
            MDC.put(key, join(values, separator));
        }
    }

    public static boolean isTaggedWith(String tag) {
        return getMDCSet(TAG_LIST, TAG_SEPERATOR).contains(clean(tag));
    }

    private static Optional<String> getMDC(String key) {
        return ofNullable(MDC.get(key));
    }

    public static String user() {
        return getMDC(USER).orElse("NOT_SPECIFIED");
    }

    public static String id() {
        return getMDC(TRANSACTION_ID).orElseGet(() -> {
            final String uuid = generateTransactionId();
            MDC.put(TRANSACTION_ID, uuid);
            TransactionRegistery.registerTransaction();
            return uuid;
        });
    }

    public static int breadCrumb() {
        return Integer.parseInt(getMDC(BREADCRUMB_COUNTER).orElse("0"));
    }

    public static void increaseBreadCrumb() {
        int i = breadCrumb();
        MDC.put(BREADCRUMB_COUNTER, "" + ++i);
    }

    public static void decreaseBreadCrumb() {
        int i = breadCrumb();
        MDC.put(BREADCRUMB_COUNTER, "" + --i);
    }

    public static String flow() {
        return getMDC(FLOW_ID).orElseGet(() -> createFlowId(null, null));
    }

    public static String hostname() {
        if (null == hostname) {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                hostname = "[unknown]";
            }
        }
        return hostname;
    }

    private static String transactionPrefix() {
        if (null == transactionPrefix) {
            transactionPrefix = hostname();
        }
        return transactionPrefix;
    }

    public static String generateTransactionId() {
        return transactionPrefix() + "-" + UUID.randomUUID().toString();
    }

    public static String createFlowId(final String flowId, String user) {
        if (flowId != null) {
            updateFlowId(flowId);
            return flowId;
        }

        user = ofNullable(user).orElse("NOT_SPECIFIED");
        MDC.put(USER, user);
        final String flowid = new StringJoiner("-").add(UUID.randomUUID().toString())
                .add(numberFormat.get().format(nbrOfSubflow()))
                .add(dateFormat.get().format(new Date()))
                .add(user.replaceAll("[^-_.@a-zA-Z0-9]*", ""))
                .toString();
        updateFlowId(flowid);
        return flowid;
    }

    public static void startNewFlow(String user) {
        createFlowId(null, user);
    }

    public static void startNewTransaction() {
        MDC.put(TRANSACTION_ID, generateTransactionId());
    }

    public static void updateFlowId(String flowid) {
        String currentFlowId = MDC.get(FLOW_ID);
        if (currentFlowId != null && !currentFlowId.equals(flowid)) {
            LOG.info("update FlowId : [{}] ==> [{}]", flow(), flowid);
        }
        MDC.put(FLOW_ID, flowid);
    }

    public static String getLevel() {
        return Level.valueOf(ofNullable(MDC.get(LEVEL)).orElse(DEFAULT_LEVEL)).levelStr;
    }

    public static void setLevel(String desiredLevel) {
        MDC.put(LEVEL, Level.valueOf(ofNullable(desiredLevel).orElse(DEFAULT_LEVEL)).levelStr);
    }
}
