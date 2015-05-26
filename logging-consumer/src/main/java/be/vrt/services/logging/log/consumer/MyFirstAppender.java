package be.vrt.services.logging.log.consumer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class MyFirstAppender extends AppenderBase<ILoggingEvent> {

	@Override
	protected void append(ILoggingEvent e) {
		System.out.println("Do something here");
	}

}
