package be.vrt.services.log.exposer.es.query;

import be.vrt.services.log.exposer.controller.JsonArray;

import java.util.Map;

import static be.vrt.services.log.exposer.controller.JsonMap.mapWith;

public class DetailQuery implements ElasticSearchQuery {

	private final Map<String, Object> query;

	public DetailQuery(String id) {
		query = mapWith("query",
				mapWith("bool",
						mapWith("should",
								JsonArray.with(
										mapWith("match_phrase_prefix", mapWith("transactionId", id)),
										mapWith("match_phrase_prefix", mapWith("flowId", id))
								)
						)));
	}

	@Override
	public Map<String, Object> getData() {
		return query;
	}
}
