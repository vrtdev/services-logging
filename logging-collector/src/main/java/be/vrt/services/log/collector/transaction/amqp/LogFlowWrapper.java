package be.vrt.services.log.collector.transaction.amqp;

import be.vrt.services.logging.log.common.LogTransaction;
import be.vrt.services.logging.log.common.Constants;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

public class LogFlowWrapper implements MessageConverter{

	private final MessageConverter converter;

	public static LogFlowWrapper decorate(MessageConverter converter){
		return new LogFlowWrapper(converter);
	} 
	
	public LogFlowWrapper(MessageConverter converter) {
		this.converter = converter;
	}

	@Override
	public Object fromMessage(Message msg) throws MessageConversionException {
		if(msg.getMessageProperties().getHeaders().get(Constants.FLOW_ID) != null){
			LogTransaction.updateFlowId(msg.getMessageProperties().getHeaders().get(Constants.FLOW_ID).toString());
		} else {
			LogTransaction.generateFlowId((String) msg.getMessageProperties().getHeaders().get(Constants.ORIGIN_USER));
		}
		return converter.fromMessage(msg);
	}

	@Override
	public Message toMessage(Object o, MessageProperties mp) throws MessageConversionException {
		mp.getHeaders().put(Constants.FLOW_ID, LogTransaction.flow());
		return converter.toMessage(o, mp);
	}
	
	
}
