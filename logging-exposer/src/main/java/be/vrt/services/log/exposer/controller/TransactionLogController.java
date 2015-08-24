package be.vrt.services.log.exposer.controller;

import be.vrt.services.log.exposer.LoggingProperties;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.vrt.services.logging.log.common.transaction.TransactionRegistery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class TransactionLogController extends HttpServlet {

	private String[] connectionUrls = LoggingProperties.connectionUrls();

	Logger log = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Map map = new HashMap();
		List logs;
		String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
		map.put("request-path", req.getPathInfo());

		logs = TransactionRegistery.list();
		if (path.matches("/[^/]*")) {
			switch (path) {
				case "/error":
					logs = TransactionRegistery.listErrors();
					break;
				case "/fail":
					logs = TransactionRegistery.listFailures();
					break;
				case "/flows":
					logs = TransactionRegistery.listIds();
					break;
				case "/static-flows":
					logs = new LinkedList();
					List flows = new LinkedList();
					for (Map.Entry<String, String> entrySet : TransactionRegistery.listStaticFlows().entrySet()) {
						String key = entrySet.getKey();
						String value = entrySet.getValue();
						Map<String, String> m = new HashMap<>();
						m.put("name", key);
						m.put("id", value);
						flows.add(m);
					}
					map.put("flows", flows);
					break;
			}
			map.put("logs", logs);
		} else if (path.matches("/transaction/[^/]*")) {

			// Apache replace space by + => Known issue
			String id = path.substring("/transaction/".length()).trim();
			id = id.replaceAll(" ", "+");

			Map<String, Object> query = createEsQuery(id);
			Map<String, Object> results = new HashMap<>();
			for (String connectionUrl : connectionUrls) {
				Map result = searchEsByQuery(connectionUrl, query);
				concatResults(result, results);
			}
			map.put("hits", results);
		}
		map.put("info", JsonMap.with("urls", connectionUrls));

		resp.setContentType("application/json");
		String json = mapper.writeValueAsString(map);
		resp.getWriter().print(json);

	}

	void concatResults(Map result, Map<String, Object> currentResults) {
		if (result == null) {
			return;
		}
		if (!currentResults.containsKey("hits")) {
			currentResults.put("hits", result.get("hits"));
			currentResults.put("total", result.get("total"));
		} else {
			List hits = (List) currentResults.get("hits");
			hits.addAll((Collection) result.get("hits"));

			int total = currentResults.containsKey("total") ? (Integer) currentResults.get("total") : 0;
			total += result.containsKey("total") ? (Integer) result.get("total") : 0;
			currentResults.put("total", total);

		}
		return;
	}

	Map<String, Object> searchEsByQuery(String connectionUrl, Map<String, Object> query) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			URL url = new URL(connectionUrl);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5000);
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			mapper.writeValue(con.getOutputStream(), query);
			if (con.getResponseCode() > 299) {
				log.info(">> Failed to query ES > " + connectionUrl + " [" + con.getResponseCode() + "] :" + con.getResponseMessage());
			} else {
				return (Map<String, Object>) mapper.readValue(con.getInputStream(), HashMap.class).get("hits");
			}
		} catch (Exception ex) {
		}
		return null;
	}

	Map<String, Object> createEsQuery(String id) {
		Map<String, Object> query = JsonMap.with("query",
			JsonMap.with("bool",
				JsonMap.with("should",
					JsonArray.with(
						JsonMap.with("match_phrase", JsonMap.with("transactionId", id)), JsonMap.with("match_phrase", JsonMap.with("flowId", id))
					)
				)));
		return query;
	}

}
