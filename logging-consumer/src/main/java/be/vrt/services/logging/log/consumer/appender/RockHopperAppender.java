package be.vrt.services.logging.log.consumer.appender;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RockHopperAppender extends AbstractJsonAppender {

	private DatagramSocket clientSocket;

	private String host;
	private int port;
	
	@Override
	public void start() {
		super.start();
		try {
			clientSocket = new DatagramSocket();
			inet = InetAddress.getByName(host);
		} catch (Exception ex) {
			this.addError("Failed to initialize", ex);
		}
	}

	@Override
	protected void persist(String json) {
		try {
			
			byte[] sendData = json.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inet, port);
			clientSocket.send(sendPacket);
		} catch (Exception e) {
			this.addError("Failed to initalize", e);
		}
	}
	private InetAddress inet;

	@Override
	public void stop() {
		clientSocket.close();
	}

	@Override
	protected Logger getLogger() {
		return LoggerFactory.getLogger(RockHopperAppender.class);
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
