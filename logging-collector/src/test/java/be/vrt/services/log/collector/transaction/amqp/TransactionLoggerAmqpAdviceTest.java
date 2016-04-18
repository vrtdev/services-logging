package be.vrt.services.log.collector.transaction.amqp;

import be.vrt.services.log.collector.exception.ErrorException;
import be.vrt.services.log.collector.exception.FailureException;
import be.vrt.services.log.collector.transaction.dto.AmqpTransactionLogDto;
import be.vrt.services.logging.log.common.Constants;
import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import be.vrt.services.logging.log.common.dto.LogType;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionLoggerAmqpAdviceTest {

    public static final String USER = "aUser";
    public static final String FLOW_ID = "aFlowId";
    public static final String A_QUEUE = "aQueue";
    @Mock
    private MethodInvocation methodInvocation;

    @Mock
    private Message message;

    @Test
    public void invoke_whenCalled_thenRegistered() throws Throwable {
        when(methodInvocation.getArguments()).thenReturn(new Object[]{"firstArgument", message});
        when(message.getMessageProperties()).thenReturn(createMessageProperties());

        TransactionLoggerAmqpAdvice amqpAdvice = new TransactionLoggerAmqpAdvice();

        amqpAdvice.invoke(methodInvocation);

        for (AbstractTransactionLog abstractTransactionLog : TransactionRegistery.list()) {
            if(abstractTransactionLog instanceof AmqpTransactionLogDto){
                AmqpTransactionLogDto amqpTransactionLogDto = (AmqpTransactionLogDto) abstractTransactionLog;
                if(A_QUEUE.equals(amqpTransactionLogDto.getQueueName())){
                    return;
                }
            }
        }
        Assert.fail("The transactionRegistery should contain an AmqpTransactionLogDto having queue " + A_QUEUE);
    }


    @Test(expected = FailureException.class)
    public void invoke_whenFailure_thenRethrown() throws Throwable {
        when(methodInvocation.getArguments()).thenReturn(new Object[]{"firstArgument", message});
        when(message.getMessageProperties()).thenReturn(createMessageProperties());
        when(methodInvocation.proceed()).thenThrow(new FailureException("aMessage"));

        TransactionLoggerAmqpAdvice amqpAdvice = new TransactionLoggerAmqpAdvice();
        try {
            amqpAdvice.invoke(methodInvocation);
        } catch (FailureException fex){
            for (AbstractTransactionLog abstractTransactionLog : TransactionRegistery.list()) {
                if (abstractTransactionLog instanceof AmqpTransactionLogDto) {
                    AmqpTransactionLogDto amqpTransactionLogDto = (AmqpTransactionLogDto) abstractTransactionLog;
                    if (A_QUEUE.equals(amqpTransactionLogDto.getQueueName())
                            && amqpTransactionLogDto.getStatus() == LogType.FAILED) {
                        throw fex;
                    }
                }
            }
        }
        Assert.fail("The transactionRegistery should contain an AmqpTransactionLogDto having queue " + A_QUEUE + " and status " + LogType.FAILED);
    }


    @Test(expected = ErrorException.class)
    public void invoke_whenError_thenRethrown() throws Throwable {
        when(methodInvocation.getArguments()).thenReturn(new Object[]{"firstArgument", message});
        when(message.getMessageProperties()).thenReturn(createMessageProperties());
        when(methodInvocation.proceed()).thenThrow(new ErrorException("aMessage"));

        TransactionLoggerAmqpAdvice amqpAdvice = new TransactionLoggerAmqpAdvice();

        try {
            amqpAdvice.invoke(methodInvocation);
        } catch (ErrorException eex){
            for (AbstractTransactionLog abstractTransactionLog : TransactionRegistery.list()) {
                if (abstractTransactionLog instanceof AmqpTransactionLogDto) {
                    AmqpTransactionLogDto amqpTransactionLogDto = (AmqpTransactionLogDto) abstractTransactionLog;
                    if (A_QUEUE.equals(amqpTransactionLogDto.getQueueName())
                            && amqpTransactionLogDto.getStatus() == LogType.ERROR) {
                        throw eex;
                    }
                }
            }
        }
        Assert.fail("The transactionRegistery should contain an AmqpTransactionLogDto having queue " + A_QUEUE + " and status " + LogType.ERROR);
    }

    private MessageProperties createMessageProperties(){
        MessageProperties properties = new MessageProperties();
        properties.setHeader(Constants.FLOW_ID, FLOW_ID);
        properties.setHeader(Constants.ORIGIN_USER, USER);
        properties.setConsumerQueue(A_QUEUE);
        return properties;
    }
}