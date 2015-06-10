package be.vrt.services.log.collector.transaction.amqp;

import be.vrt.services.logging.log.common.LogTransaction;
import be.vrt.services.logging.log.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

public class LogFlowWrapper implements MessageConverter {

	private final MessageConverter converter;
	
	private final Logger log = LoggerFactory.getLogger(LogFlowWrapper.class);

	public static LogFlowWrapper decorate(MessageConverter converter) {
		return new LogFlowWrapper(converter);
	}

	public LogFlowWrapper(MessageConverter converter) {
		this.converter = converter;
	}

	@Override
	public Object fromMessage(Message msg) throws MessageConversionException {
		MessageProperties props = msg.getMessageProperties();
		String headerFlowId = (String) props.getHeaders().get(Constants.FLOW_ID);
		String originUser = (String) props.getHeaders().get(Constants.ORIGIN_USER);
		LogTransaction.createFlowId(headerFlowId,originUser );
		try{
			Object o = converter.fromMessage(msg);
			log.debug("Message recieved on {}", msg.getMessageProperties().getConsumerQueue(), o);
			return o;
		} catch (Exception e){
			log.error("Failed to process message from {}", msg.getMessageProperties().getConsumerQueue(), e);
			throw e;
		} 
		
	}

	@Override
	public Message toMessage(Object o, MessageProperties mp) throws MessageConversionException {
		mp.getHeaders().put(Constants.FLOW_ID, LogTransaction.flow());
		log.debug("Send message AMQP",  o);
		return converter.toMessage(o, mp);
	}

}
