package be.vrt.services.log.exposer.es;

import be.vrt.services.log.exposer.es.query.ElasticSearchQuery;
import be.vrt.services.log.exposer.es.result.ElasticSearchCountResult;
import be.vrt.services.log.exposer.es.result.ElasticSearchResult;
import be.vrt.services.logging.log.common.LoggingProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ElasticSearchSearchQueryHttpExecutor implements ElasticSearchQueryExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchSearchQueryHttpExecutor.class);

	private String[] connectionUrls = LoggingProperties.connectionUrls();
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public ElasticSearchResult executeSearchQuery(ElasticSearchQuery query) {
		String connectionUrl = LoggingProperties.connectionStatUrl();
		return executeSearchQuery(query, connectionUrl);
	}

	private ElasticSearchResult executeSearchQuery(ElasticSearchQuery query, String connectionUrl) {
		try {
			URL url = new URL(connectionUrl);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5000);
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			mapper.writeValue(con.getOutputStream(), query.getData());
			if (con.getResponseCode() > 299) {
				LOGGER.info(">> Failed to query ES > " + connectionUrl + " [" + con.getResponseCode() + "] :" + con.getResponseMessage());
				LOGGER.info(">> Failed to query ES > " + mapper.writeValueAsString(query));
				return ElasticSearchResult.empty();
			} else {
				return ElasticSearchResult.from((Map<String, Object>) mapper.readValue(con.getInputStream(), HashMap.class));
			}
		} catch (Exception ex) {
			return ElasticSearchResult.empty();
		}
	}

	@Override
	public ElasticSearchResult executeSearchQueryMultiInstances(ElasticSearchQuery query) {
		ElasticSearchResult result = ElasticSearchResult.empty();
		for (String connectionUrl : connectionUrls) {
			result = result.addResult(executeSearchQuery(query, connectionUrl));
		}
		return result;
	}

	@Override
	public ElasticSearchCountResult executeCountQuery(ElasticSearchQuery query) {
		String connectionUrl = LoggingProperties.connectionStatUrl();
		try {
			URL url = new URL(connectionUrl + "?search_type=count");

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5000);
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			mapper.writeValue(con.getOutputStream(), query.getData());
			if (con.getResponseCode() > 299) {
				LOGGER.info(">> Failed to query ES > " + connectionUrl + " [" + con.getResponseCode() + "] :" + con.getResponseMessage());
				LOGGER.info(">> Failed to query ES > " + mapper.writeValueAsString(query));
			} else {
				return ElasticSearchCountResult.from((Map<String, Object>) mapper.readValue(con.getInputStream(), HashMap.class));
			}
		} catch (Exception ex) {
			LOGGER.error("Failed to query ES", ex);
		}
		return ElasticSearchCountResult.empty();
	}
}
