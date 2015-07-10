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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class TransactionLogController extends HttpServlet {

	private String[] connectionUrls = LoggingProperties.connectionUrls();

	Logger log = LoggerFactory.getLogger(this.getClass());

	private String proxyRedirect;

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Map map = new HashMap();
		List logs;
		String path = req.getPathInfo() == null ? "/" : req.getPathInfo();

		if (proxyRedirect != null) {
			redirectRequest(path, resp);
		}

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
				case "/ids":
					logs = TransactionRegistery.listIds();
					break;
			}
			map.put("logs", logs);
		} else if (path.matches("/transaction/[^/]*")) {
			String id = path.substring("/transaction/".length());
			Map<String, Object> query = createEsQuery(id);
			Map<String, Object> results = new HashMap<>();
			for (String connectionUrl : connectionUrls) {
				Map result = searchEsByQuery(connectionUrl, query);
				concatResults(result, results);
			}
			map.put("hits", results);
			map.put("info", JsonMap.with("urls", connectionUrls));
		}

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

	private void redirectRequest(String path, HttpServletResponse resp) throws MalformedURLException, IOException {
		String redirectUrl = proxyRedirect + path;
		URL url = new URL(redirectUrl);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(10000);
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		con.getInputStream();
		pipe(con.getInputStream(), resp.getOutputStream());
	}

	private void pipe(InputStream is, OutputStream os) throws IOException {
		int n;
		byte[] buffer = new byte[1024];
		while ((n = is.read(buffer)) > -1) {
			os.write(buffer, 0, n);   // Don't allow any extra bytes to creep in, final write
		}
	}
}
