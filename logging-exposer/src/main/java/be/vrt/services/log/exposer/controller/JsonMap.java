package be.vrt.services.log.exposer.controller;

import java.util.HashMap;

@SuppressWarnings("serial")
public class JsonMap extends HashMap<String, Object> {
	
	public static JsonMap with(String key, Object value) {
		JsonMap json = new JsonMap();
		json.put(key, value);
		return json;
	}
	
	public JsonMap and(String key, Object value) {
		put(key, value);
		return this;
	}
}
