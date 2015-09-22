package be.vrt.services.log.exposer.controller;

import be.vrt.services.logging.log.common.LoggingProperties;
import static be.vrt.services.log.exposer.controller.JsonMap.mapWith;
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

			String id = path.substring("/transaction/".length()).trim();
			// Apache replace + by spaces => Known issue
			id = id.replaceAll(" ", "+");

			Map<String, Object> query = createEsDetailQuery(id);
			Map<String, Object> results = new HashMap<>();
			for (String connectionUrl : connectionUrls) {
				Map result = (Map) searchEsByQuery(connectionUrl, query).get("hits");
				concatResults(result, results);
			}
			map.put("hits", results);
		} else if (path.matches("/stats/overview/[^/]*")) {
			String date = path.substring("/stats/overview/".length()).trim();

			Map<String, Object> query = createEsStatsQuery(date);
			String connectionUrl = LoggingProperties.connectionStatUrl();
			Map result = (Map) searchAggEsByQuery(connectionUrl, query).get("aggregations");

			map.put("agg", result);

		} else if (path.matches("/stats/errors/[^/]*")) {
			String date = path.substring("/stats/errors/".length()).trim();

			Map<String, Object> query = createEsDailyProblemQuery(date, "ERROR");
			String connectionUrl = LoggingProperties.connectionStatUrl();
			Map result = (Map) searchEsByQuery(connectionUrl, query).get("hits");

			map.put("statshits", result);
		} else if (path.matches("/stats/failures/[^/]*")) {
			String date = path.substring("/stats/failures/".length()).trim();

			Map<String, Object> query = createEsDailyProblemQuery(date, "FAIL");
			String connectionUrl = LoggingProperties.connectionStatUrl();
			Map result = (Map) searchEsByQuery(connectionUrl, query).get("hits");

			map.put("statshits", result);
		}

		map.put("info",
			mapWith("urls", connectionUrls)
			.mapAnd("statUrl", LoggingProperties.connectionStatUrl())
		);

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

			String wtf = mapper.writeValueAsString(query);

			mapper.writeValue(con.getOutputStream(), query);
			if (con.getResponseCode() > 299) {
				log.info(">> Failed to query ES > " + connectionUrl + " [" + con.getResponseCode() + "] :" + con.getResponseMessage());
				log.info(">> Failed to query ES > " + wtf);
			} else {
				return (Map<String, Object>) mapper.readValue(con.getInputStream(), HashMap.class);
			}
		} catch (Exception ex) {
		}
		return new HashMap<>();
	}

	Map<String, Object> searchAggEsByQuery(String connectionUrl, Map<String, Object> query) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			URL url = new URL(connectionUrl + "?search_type=count");

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5000);
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			String wtf = mapper.writeValueAsString(query);
			System.out.println("QUERY: " + wtf);

			mapper.writeValue(con.getOutputStream(), query);
			if (con.getResponseCode() > 299) {
				log.info(">> Failed to query ES > " + connectionUrl + " [" + con.getResponseCode() + "] :" + con.getResponseMessage());
			} else {
				return (Map<String, Object>) mapper.readValue(con.getInputStream(), HashMap.class).get("aggregations");
			}
		} catch (Exception ex) {
		}
		return null;
	}

	Map<String, Object> createEsDetailQuery(String id) {
		Map<String, Object> query = mapWith("query",
			mapWith("bool",
				mapWith("should",
					JsonArray.with(
						mapWith("match_phrase", mapWith("transactionId", id)), // <-- backwards compatibility -> remove this on 10/10/2015
						mapWith("match_phrase_prefix", mapWith("transactionId", id)), 
						mapWith("match_phrase", mapWith("flowId", id)), // <-- backwards compatibility -> remove this on 10/10/2015
						mapWith("match_phrase_prefix", mapWith("flowId", id))
					)
				)));
		return query;
	}

	Map<String, Object> createEsDailyProblemQuery(String date, String level) {
		return mapWith("query",
			mapWith("filtered",
				mapWith("query",
					mapWith("wildcard",
						mapWith("content.[4] AuditLogDto.method", "*Facade*")
					)
				).mapAnd("filter",
					mapWith("bool",
						mapWith("must",
							JsonArray.with(
								mapWith("exists",
									mapWith("field", "content.[4] AuditLogDto.method")
								),
								mapWith("range",
									mapWith("logDate",
										mapWith("gte", date + "T00:00:00")
										.mapAnd("lte", date + "T00:00:00||+1d")
										.mapAnd("time_zone", "CET")
									)
								),
								mapWith("term",
									mapWith("content.[4] AuditLogDto.auditLevel", level)
								)
							)
						)
					)
				)
			)
		).mapAnd("sort",
			JsonArray.with(
				mapWith("logDate",
					mapWith("order", "desc")
				)
			)
		);
	}

	Map<String, Object> createEsStatsQuery(String date) {
		return mapWith("aggs",
			mapWith("time",
				mapWith("date_histogram",
					mapWith("field", "date")
					.mapAnd("interval", "hour")
				).mapAnd("aggs",
					mapWith("hosts",
						mapWith("terms",
							mapWith("field", "hostName")
							.mapAnd("size", 50)
						).mapAnd("aggs",
							mapWith("methods",
								mapWith("terms",
									mapWith("field", "content.[4] AuditLogDto.method")
									.mapAnd("size", 50)
								).mapAnd("aggs",
									mapWith("statistics",
										mapWith("extended_stats",
											mapWith("field", "content.[4] AuditLogDto.duration")
										)
									).mapAnd("Status",
										mapWith("terms",
											mapWith("field", "content.[4] AuditLogDto.auditLevel")
											.mapAnd("size", 50)
										)
									).mapAnd("distribution",
										mapWith("percentiles",
											mapWith("field", "content.[4] AuditLogDto.duration")
										)
									)
								)
							)
						)
					)
				)
			)
		).mapAnd("query",
			mapWith("filtered",
				mapWith("query",
					mapWith("wildcard",
						mapWith("content.[4] AuditLogDto.method", "*Facade*")
					)
				).mapAnd("filter",
					mapWith("bool",
						mapWith("must",
							JsonArray.with(
								mapWith("range",
									mapWith("logDate",
										mapWith("gte", date + "T00:00:00")
										.mapAnd("lte", date + "T00:00:00||+1d")
										.mapAnd("time_zone", "CET")
									)
								),
								mapWith("exists",
									mapWith("field", "content.[4] AuditLogDto.method")
								)
							)
						)
					)
				)
			)
		);
	}
}
