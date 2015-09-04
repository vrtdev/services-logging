package be.vrt.services.logging.log.consumer.config;

import be.vrt.services.logging.log.common.LogTransaction;
import be.vrt.services.logging.log.common.LoggingProperties;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EnvironmentSetting {

	private static EnvironmentSetting es = new EnvironmentSetting();

	private Map<String, Object> log = new HashMap<>();
	
	private EnvironmentSetting() {
		log.put("runtime", "runtime-" + LogTransaction.generateTransactionId());
		log.put("startup", new Date());
		log.put("app", LoggingProperties.app());
		log.put("env", LoggingProperties.env());
	}

	public static Map<String, Object> info(){
		return new HashMap<>(es.log);
	}
}