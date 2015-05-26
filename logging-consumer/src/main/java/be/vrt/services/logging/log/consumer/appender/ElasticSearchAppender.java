package be.vrt.services.logging.log.consumer.appender;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchAppender extends AbstractJsonAppender {

	private Client client;

	private String host;
	private int port;

	@Override
	public void start() {
		super.start();
		client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(host, port));
	}

	@Override
	protected void persist(String json) {
		try {
			client.prepareIndex("logging", "log")
					.setSource(json)
					.execute()
					.actionGet();
		} catch (Exception e) {
			System.out.println("e: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		super.stop();
		client.close();
	}

	@Override
	protected Logger getLogger() {
		return LoggerFactory.getLogger(ElasticSearchAppender.class);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
