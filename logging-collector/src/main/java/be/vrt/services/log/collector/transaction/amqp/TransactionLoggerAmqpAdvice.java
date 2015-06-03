package be.vrt.services.log.collector.transaction.amqp;

import be.vrt.services.log.collector.exception.FailureException;
import be.vrt.services.logging.log.common.LogTransaction;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import be.vrt.services.log.collector.transaction.dto.AmqpTransactionLogDto;
import be.vrt.services.log.collector.transaction.http.TransactionLoggerFilter;
import be.vrt.services.logging.log.common.Constants;
import java.net.InetAddress;
import java.util.Date;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

public class TransactionLoggerAmqpAdvice implements MethodInterceptor {

	private final Logger log = LoggerFactory.getLogger(TransactionLoggerFilter.class);

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		AmqpTransactionLogDto transaction = generateTransactionLogDtoFromAmqpMessage(mi);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		transaction.setStartTime(new Date(stopWatch.getStartTime()));
		try {
			return mi.proceed();
		} catch (FailureException e) {
			transaction.setErrorReason(e.getMessage());
			transaction.setStatus(AmqpTransactionLogDto.Type.FAILED);
			throw e;
		} catch (Throwable e) {
			transaction.setErrorReason(e.getMessage());
			transaction.setStatus(AmqpTransactionLogDto.Type.ERROR);
			throw e;
		} finally {
			stopWatch.stop();
			transaction.setFlowId(LogTransaction.flow());
			transaction.setDuration(stopWatch.getTime());
			log.info("Filter Info: {}", transaction);
			TransactionRegistery.register(transaction);
		}
	}

	private AmqpTransactionLogDto generateTransactionLogDtoFromAmqpMessage(MethodInvocation mi) {
		AmqpTransactionLogDto transaction = new AmqpTransactionLogDto();

		String hostname;
		String headerFlowId = null;
		String originUser = null;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			if (mi.getArguments().length == 2 && mi.getArguments()[1] instanceof Message) {
				MessageProperties props = ((Message) mi.getArguments()[1]).getMessageProperties();
				transaction.setExchange(props.getReceivedExchange());
				transaction.setRoutingKey(props.getReceivedRoutingKey());
				transaction.setHeaders(props.getHeaders());
				headerFlowId = (String) props.getHeaders().get(Constants.FLOW_ID);
				originUser = (String) props.getHeaders().get(Constants.ORIGIN_USER);
			}

		} catch (Exception ex) {
			//java.util.logging.Logger.getLogger(TransactionLoggerAmqpAdvice.class.getName()).log(Level.SEVERE, null, ex);
			hostname = "[unknown]";
		}
		
		String transactionUUID = LogTransaction.createTransactionId(hostname);
		String flowId = LogTransaction.generateFlowId(headerFlowId, originUser);
		transaction.setFlowId(flowId);
		transaction.setTransactionId(transactionUUID);
		transaction.setServerName(hostname);

//		transaction.setResource(request.getRequestURL().toString());
		return transaction;
	}
}
