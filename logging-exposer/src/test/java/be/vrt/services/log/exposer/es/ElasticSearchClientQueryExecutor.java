package be.vrt.services.log.exposer.es;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

import be.vrt.services.log.exposer.es.query.ElasticSearchQuery;
import be.vrt.services.log.exposer.es.result.ElasticSearchCountResult;
import be.vrt.services.log.exposer.es.result.ElasticSearchResult;

public class ElasticSearchClientQueryExecutor implements ElasticSearchQueryExecutor {

	private Client client;
	private final ObjectMapper mapper = new ObjectMapper();

	public ElasticSearchClientQueryExecutor(Client client) {
		this.client = client;
	}

	@Override
	public ElasticSearchResult executeSearchQuery(ElasticSearchQuery query) {
		try {
			SearchRequestBuilder request = client.prepareSearch("logging").setSource(mapper.writeValueAsString(query.getData()));
			SearchResponse response = request.execute().actionGet();
			return ElasticSearchResult.from(mapper.readValue(response.toString(), Map.class));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ElasticSearchResult executeSearchQueryMultiInstances(ElasticSearchQuery query) {
		return null;
	}

	@Override
	public ElasticSearchCountResult executeCountQuery(ElasticSearchQuery query) {
		return null;
	}
}
