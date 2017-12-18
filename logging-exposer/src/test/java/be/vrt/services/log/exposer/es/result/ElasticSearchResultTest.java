package be.vrt.services.log.exposer.es.result;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ElasticSearchResultTest {

	@Test
	public void testAddResultToEmpty() throws Exception {
		List<String> hitsList = asList("one", "two");
		Map<String, Object> data = createDataMapWithHits(hitsList);

		ElasticSearchResult result = ElasticSearchResult.empty().addResult(ElasticSearchResult.from(data));
		assertEquals(2, result.getData().get("total"));
		assertEquals(hitsList, result.getData().get("hits"));
	}

	@Test
	public void testAddResults() throws Exception {
		List<String> hitsList = asList("one", "two");
		Map<String, Object> data = createDataMapWithHits(hitsList);
		List<String> hitsList2 = asList("three", "four");
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
		List<String> hitsList = asList("one", "two");
		Map<String, Object> data = createDataMapWithHits(hitsList);

		ElasticSearchResult result = ElasticSearchResult.from(data)
				.addResult(ElasticSearchResult.empty());
		assertEquals(2, result.getData().get("total"));
		assertEquals(hitsList, result.getData().get("hits"));
	}

	private Map<String, Object> createDataMapWithHits(List<String> hitsList) {
		Map<String, Object> hitsData = new HashMap<>();
		hitsData.put("hits", hitsList);
		hitsData.put("total", hitsList.size());
		Map<String, Object> data = new HashMap<>();
		data.put("hits", hitsData);
		return data;
	}
}
