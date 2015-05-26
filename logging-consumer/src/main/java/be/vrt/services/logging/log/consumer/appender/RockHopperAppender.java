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
	}

	@Override
	protected void persist(String json) {
		try {
			clientSocket = new DatagramSocket();
			byte[] sendData = json.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), port);
			clientSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
