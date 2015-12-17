package be.vrt.services.log.exposer.es.result;

import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.collect.Maps;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ElasticSearchResultTest {

	@Test
	public void testAddResultToEmpty() throws Exception {
		List<String> hitsList = Lists.newArrayList("one", "two");
		Map<String, Object> data = createDataMapWithHits(hitsList);

		ElasticSearchResult result = ElasticSearchResult.empty().addResult(ElasticSearchResult.from(data));
		assertEquals(2, result.getData().get("total"));
		assertEquals(hitsList, result.getData().get("hits"));
	}

	@Test
	public void testAddResults() throws Exception {
		List<String> hitsList = Lists.newArrayList("one", "two");
		Map<String, Object> data = createDataMapWithHits(hitsList);
		List<String> hitsList2 = Lists.newArrayList("three", "four");
		Map<String, Object> data2 = createDataMapWithHits(hitsList2);

		ElasticSearchResult result = ElasticSearchResult.from(data)
				.addResult(ElasticSearchResult.from(data2));
		assertEquals(4, result.getData().get("total"));
		List<String> expectedList = new ArrayList<>(hitsList);
		expectedList.addAll(hitsList2);
		assertEquals(expectedList, result.getData().get("hits"));
	}

	@Test
	public void testAddEmptyToResult() throws Exception {
		List<String> hitsList = Lists.newArrayList("one", "two");
		Map<String, Object> data = createDataMapWithHits(hitsList);

		ElasticSearchResult result = ElasticSearchResult.from(data)
				.addResult(ElasticSearchResult.empty());
		assertEquals(2, result.getData().get("total"));
		assertEquals(hitsList, result.getData().get("hits"));
	}

	private Map<String, Object> createDataMapWithHits(List<String> hitsList) {
		Map<String, Object> hitsData = Maps.newHashMap();
		hitsData.put("hits", hitsList);
		hitsData.put("total", hitsList.size());
		Map<String, Object> data = Maps.newHashMap();
		data.put("hits", hitsData);
		return data;
	}
}
