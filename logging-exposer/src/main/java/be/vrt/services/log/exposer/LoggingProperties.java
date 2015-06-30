package be.vrt.services.log.exposer;

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
	private static final String LOGGING_PROPERTIES = "logging.properties";

	private Properties prop = new Properties();

	LoggingProperties() {

		if (!loadPropertiesFromFile()) {
			try {
				prop.load(this.getClass().getClassLoader().getResourceAsStream(LOGGING_PROPERTIES));
			} catch (Exception ex) {
				log.warn("Failed to load properties for logging ({})",LOGGING_PROPERTIES);
			}
		}
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
			log.info("Propertyfile loaded: "+fileName);
			return true;
		} catch (IOException ex) {
			log.warn("Propertyfile accessible: [{}] - {}", fileName, ex.getMessage(), ex);
			return false;
		}

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

}
