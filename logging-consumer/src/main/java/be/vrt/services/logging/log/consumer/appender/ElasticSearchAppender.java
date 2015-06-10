package be.vrt.services.logging.log.consumer.appender;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchAppender extends AbstractJsonAppender {

	private String host;
	private int port;

	@Override
	public void start() {
		super.start();
	}

	@Override
	protected void persist(String json) {
		String esUrl = "http://" + host + ":" + port + "/logging/log/"+UUID.randomUUID().toString();
		try {
			
			
			URL url = new URL(esUrl);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(1000);
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			
			OutputStream out = con.getOutputStream();
			out.write(json.getBytes(Charset.forName("UTF8")));
			out.flush();
			out.close();

			if (con.getResponseCode() > 299) {
				System.err.println(">> Failed to save to ES > [" + con.getResponseCode() + "] :" + con.getResponseMessage() + " || " + esUrl);
				System.err.println(json);
			}
		} catch (Exception e) {
			System.out.println("e: " + e.getMessage() + " >> " + host + ":" + port);
		}
	}

	@Override
	public void stop() {
		super.stop();
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
