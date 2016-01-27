package be.vrt.services.log.exposer.es;

import be.vrt.services.log.exposer.es.query.ElasticSearchQuery;
import be.vrt.services.log.exposer.es.result.ElasticSearchResult;
import be.vrt.services.log.exposer.es.result.TestElasticSearchCountResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesArray;

import java.io.IOException;
import java.util.Map;

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
	public TestElasticSearchCountResult executeCountQuery(ElasticSearchQuery query) {
		try {
			SearchRequestBuilder request = client.prepareSearch("logging").setSearchType(SearchType.COUNT)
					.setQuery(mapper.writeValueAsString(query.getData().get("query")))
					.setAggregations(new BytesArray(mapper.writeValueAsString(query.getData().get("aggs")).getBytes()));
			SearchResponse response = request.execute().actionGet();
			return new TestElasticSearchCountResult(mapper.readValue(response.toString(), Map.class));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
