package be.vrt.services.log.collector.transaction.advice;

import be.vrt.services.log.collector.exception.FailureException;
import be.vrt.services.log.collector.transaction.TransactionRegistery;
import be.vrt.services.log.collector.transaction.dto.AmqpTransactionLogDto;
import be.vrt.services.log.collector.transaction.filter.TransactionLoggerFilter;
import static be.vrt.services.logging.log.common.Constants.TRANSACTION_ID;
import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

public class TransactionLoggerAmqpAdvice implements MethodInterceptor {

	private final Logger log = LoggerFactory.getLogger(TransactionLoggerFilter.class);

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		AmqpTransactionLogDto transaction = generateTransactionLogDtoFromAmqpMessage(mi);
		MDC.put(TRANSACTION_ID, transaction.getTransactionId());

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
			transaction.setDuration(stopWatch.getTime());
			log.info("Filter Info: {}", transaction);
			TransactionRegistery.register(transaction);
		}
	}

	private AmqpTransactionLogDto generateTransactionLogDtoFromAmqpMessage(MethodInvocation mi) {
		AmqpTransactionLogDto transaction = new AmqpTransactionLogDto();

		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			if (mi.getArguments().length == 2 && mi.getArguments()[1] instanceof Message) {
				MessageProperties props = ((Message) mi.getArguments()[1]).getMessageProperties();
				transaction.setExchange(props.getReceivedExchange());
				transaction.setRoutingKey(props.getReceivedRoutingKey());
				transaction.setHeaders(props.getHeaders());
			}

		} catch (Exception ex) {
			//java.util.logging.Logger.getLogger(TransactionLoggerAmqpAdvice.class.getName()).log(Level.SEVERE, null, ex);
			hostname = "[unknown]";
		}
		String uuid = UUID.randomUUID().toString();
		String transactionUUID = hostname + "-" + uuid;
		transaction.setTransactionId(transactionUUID);
		transaction.setServerName(hostname);

//		transaction.setResource(request.getRequestURL().toString());
		return transaction;
	}
}
