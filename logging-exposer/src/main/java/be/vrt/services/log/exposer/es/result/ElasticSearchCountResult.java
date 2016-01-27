package be.vrt.services.log.exposer.es.result;

import java.util.HashMap;
import java.util.Map;

public class ElasticSearchCountResult {

	private final Map<String, Object> data;

	private ElasticSearchCountResult() {
		this.data = new HashMap<>();
	}

	protected ElasticSearchCountResult(Map<String, Object> data) {
		this.data = data;
	}

	public static ElasticSearchCountResult from(Map<String, Object> data) {
		if(data != null && data.containsKey("aggregations")) {
			return new ElasticSearchCountResult((Map<String, Object>) data.get("aggregations"));
		}
		return ElasticSearchCountResult.empty();
	}

	public static ElasticSearchCountResult empty() {
		return new ElasticSearchCountResult();
	}

	public Map<String, Object> getAggregations(){
		return data;
	}
}
