package be.vrt.services.log.exposer.es.result;

import java.util.Map;

//TODO rework ElasticSearchCountResult so we don't need this class!!
public class TestElasticSearchCountResult extends ElasticSearchCountResult {

	private final Map<String, Object> data;

	public TestElasticSearchCountResult(Map<String, Object> data) {
		super((Map<String, Object>) data.get("aggregations"));
		this.data = data;
	}

	public Map<String, Object> getData() {
		return data;
	}
}
