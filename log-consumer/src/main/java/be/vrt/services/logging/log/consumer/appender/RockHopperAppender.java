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
		try {
			clientSocket = new DatagramSocket(port, InetAddress.getByName("localhost"));
		} catch (Exception e) {
			System.err.println("Failed to connect to UDP on localhost:" + port);
		}
	}

	@Override
	protected void persist(String json) {
		try {
			if (!clientSocket.isConnected()) {
				try {
					clientSocket = new DatagramSocket(port);
				} catch (Exception e) {
					System.err.println("Failed to connect to UDP on localhost:" + port);
				}
			}
			byte[] sendData = json.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length);
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
