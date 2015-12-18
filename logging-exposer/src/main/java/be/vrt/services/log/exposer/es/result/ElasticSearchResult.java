package be.vrt.services.log.exposer.es.result;

import java.util.*;

public class ElasticSearchResult {

	private final Map<String, Object> data;

	private ElasticSearchResult() {
		data = new HashMap<>();
	}

	private ElasticSearchResult(Map<String, Object> data) {
		this.data = data;
	}

	public static ElasticSearchResult empty() {
		return new ElasticSearchResult();
	}

	public static ElasticSearchResult from(Map<String, Object> data) {
		if(data != null && data.containsKey("hits")) {
			return new ElasticSearchResult((Map<String, Object>) data.get("hits"));
		}
		return ElasticSearchResult.empty();
	}

	public Map getData() {
		return data;
	}

	public ElasticSearchResult addResult(ElasticSearchResult elasticSearchResult) {
		if (elasticSearchResult == null) {
			return this;
		}
		Map<String, Object> result = new HashMap<>();
		if (!elasticSearchResult.data.containsKey("hits")) {
			result.put("hits", data.get("hits"));
			result.put("total", data.get("total"));
		} else {
			List hits = data.get("hits") == null ? new ArrayList() : new ArrayList<>((List) data.get("hits"));
			hits.addAll((Collection) elasticSearchResult.data.get("hits"));
			result.put("hits", hits);

			int total = elasticSearchResult.data.containsKey("total") ? (Integer) elasticSearchResult.data.get("total") : 0;
			total += data.containsKey("total") ? (Integer) data.get("total") : 0;
			result.put("total", total);

		}
		return new ElasticSearchResult(result);
	}
}
