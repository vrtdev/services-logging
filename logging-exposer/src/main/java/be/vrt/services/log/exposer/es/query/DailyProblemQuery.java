package be.vrt.services.log.exposer.es.query;

import be.vrt.services.log.exposer.controller.JsonArray;

import java.util.Map;

import static be.vrt.services.log.exposer.controller.JsonMap.mapWith;

public class DailyProblemQuery implements ElasticSearchQuery {

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

	public DailyProblemQuery(String date, String level, String env, String[] apps) {
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
																mapWith("should",
																	JsonArray.with(
																			mapWith("missing", mapWith("field", "environmentInfo.app")),
																			mapWith("bool",
																					mapWith("must",
																						JsonArray.with(
																								mapWith("term",
																										mapWith("environmentInfo.env", env)
																								),
																								mapWith("terms",
																										mapWith("environmentInfo.app", apps)
																								)
																						)
																					)
																			)
																	)
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
