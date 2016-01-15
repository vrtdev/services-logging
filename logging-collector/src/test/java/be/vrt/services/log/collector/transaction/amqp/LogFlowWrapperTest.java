package be.vrt.services.log.collector.transaction.amqp;

import be.vrt.services.log.collector.exception.ErrorException;
import be.vrt.services.logging.log.common.Constants;
import be.vrt.services.logging.log.common.LogTransaction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogFlowWrapperTest {


    public static final String MESSAGE = "theConvertedMessage";
    public static final String FLOW_ID = "aFlowId";
    public static final String USER = "aUser";
    public static final String ID = "myFlowId";
    @Mock
    private MessageConverter messageConverter;

    @Mock
    private Message message;

    @InjectMocks
    private LogFlowWrapper logFlowWrapper;

    @Test
    public void fromMessage_whenCalled_thenMessageConverted(){
        when(messageConverter.fromMessage(message)).thenReturn(MESSAGE);
        when(message.getMessageProperties()).thenReturn(createMessageProperties());

        Object convertedMessage = logFlowWrapper.fromMessage(message);
        assertTrue(convertedMessage instanceof String && convertedMessage.equals("theConvertedMessage"));
        assertEquals(FLOW_ID, LogTransaction.flow());
    }

    @Test(expected = ErrorException.class)
    public void fromMessage_whenExceptionWhileConverting_thenExceptionIsRethrown() {
        when(messageConverter.fromMessage(message)).thenThrow(new ErrorException());
        when(message.getMessageProperties()).thenReturn(createMessageProperties());

        logFlowWrapper.fromMessage(message);
        assertEquals(FLOW_ID, LogTransaction.flow());
    }

    @Test
    public void toMessage(){
        Object obj = new Object();
        LogTransaction.createFlowId(ID, "aUser");

        MessageProperties mp = new MessageProperties();
        logFlowWrapper.toMessage(obj, mp);

        verify(messageConverter).toMessage(obj, mp);
        Assert.assertEquals(mp.getHeaders().get(Constants.FLOW_ID), ID);
    }


    private MessageProperties createMessageProperties(){
        MessageProperties properties = new MessageProperties();
        properties.setHeader(Constants.FLOW_ID, FLOW_ID);
        properties.setHeader(Constants.ORIGIN_USER, USER);
        return properties;
    }
}