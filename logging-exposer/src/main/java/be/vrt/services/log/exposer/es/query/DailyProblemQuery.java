package be.vrt.services.log.exposer.es.query;

import be.vrt.services.log.exposer.controller.JsonArray;
import be.vrt.services.logging.log.common.AppWithEnv;

import java.util.List;
import java.util.Map;

import static be.vrt.services.log.exposer.controller.JsonMap.mapWith;

public class DailyProblemQuery extends AppWithEnvsElasticSearchQuery implements ElasticSearchQuery {

	private final Map<String, Object> query;

	public DailyProblemQuery(String date, String level) {
		query = mapWith("query",
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

	public DailyProblemQuery(String date, String level, List<AppWithEnv> appWithEnvList) {
		query = mapWith("query",
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
														),
														mapWith("bool",
																mapWith("should", createAppEnvSubQuery(appWithEnvList)
																)
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

	@Override
	public Map<String, Object> getData() {
		return query;
	}
}
