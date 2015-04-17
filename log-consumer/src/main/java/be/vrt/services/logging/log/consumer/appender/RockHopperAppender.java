package be.vrt.services.logging.log.consumer.appender;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class RockHopperAppender extends AbstractJsonAppender {

	private int port = 5514;
	private DatagramSocket clientSocket;

	@Override
	public void start() {
		super.start();
	}

	@Override
	protected void persist(String json) {
		try {
			clientSocket = new DatagramSocket();
			byte[] sendData = json.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLoopbackAddress(), port);
			clientSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		clientSocket.close();
	}

}
