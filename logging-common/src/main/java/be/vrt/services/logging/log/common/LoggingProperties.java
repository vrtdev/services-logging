package be.vrt.services.logging.log.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingProperties {

	private static Logger log = LoggerFactory.getLogger(LoggingProperties.class);

	static LoggingProperties instance = new LoggingProperties();

	public final static String URLS = "log.connection.urls";
	public final static String STATS_URL = "log.connection.stat.url";
	public final static String APPLICATION = "log.app";
	public final static String ENVIRONEMENT = "log.env";

	private static final String LOGGING_PROPERTIES = "logging.properties";

	private Properties prop = new Properties();

	LoggingProperties() {

		if (!loadPropertiesFromFile()) {
			try {
				prop.load(this.getClass().getClassLoader().getResourceAsStream(LOGGING_PROPERTIES));
			} catch (Exception ex) {
				log.warn("Failed to load properties for logging ({})", LOGGING_PROPERTIES);
			}
		}
	}
	
	String getProperty(String key){
		String val = prop.getProperty(key);
		if (val == null) {
			log.warn("Property not defined " + key);
			val = "??"+key+"??";
		}
		return val;
	}

	boolean loadPropertiesFromFile() {
		String fileName = System.getProperty("logging.propertyFile");
		if (fileName == null) {
			log.info("Propertyfile not defined");
			return false;
		}
		if (!new File(fileName).canRead()) {
			log.warn("Propertyfile not readable: {}", fileName);
			return false;
		}
		try {
			prop.load(new FileInputStream(fileName));
			log.info("Propertyfile loaded: " + fileName);
			return true;
		} catch (IOException ex) {
			log.warn("Propertyfile accessible: [{}] - {}", fileName, ex.getMessage(), ex);
			return false;
		}

	}
	
	public static String env(){
		return instance.getProperty(ENVIRONEMENT);
	}
	
	public static String app(){
		return instance.getProperty(APPLICATION);
	}

	public static String[] connectionUrls() {
		return instance.getConnectionUrls();
	}

	String[] getConnectionUrls() {
		String urls = prop.getProperty(URLS);
		if (urls == null) {
			log.warn("Property not defined " + URLS);
			return new String[]{};
		}
		urls = urls.trim().replaceAll("\\s", "");
		return urls.split(",");
	}

	public static String connectionStatUrl() {
		String url = instance.prop.getProperty(STATS_URL);
		if (url == null) {
			log.warn("Property not defined " + STATS_URL);
			url = "";
		}
		return url;
	}

}
