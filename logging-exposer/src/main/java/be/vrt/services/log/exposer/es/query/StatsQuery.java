package be.vrt.services.log.exposer.es.query;

import be.vrt.services.log.exposer.controller.JsonArray;
import be.vrt.services.logging.log.common.AppWithEnv;

import java.util.List;
import java.util.Map;

import static be.vrt.services.log.exposer.controller.JsonMap.mapWith;

public class StatsQuery extends AppWithEnvsElasticSearchQuery implements ElasticSearchQuery {

    private final Map<String, Object> query;

    public StatsQuery(String date) {
        query = mapWith("aggs",
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
                                        ),
                                        mapWith("wildcard",
                                                mapWith("content.[4] AuditLogDto.method", "*Facade*")
                                        )
                                )
                        )
                )
        );
    }

    public StatsQuery(String date, List<AppWithEnv> appWithEnvList) {
        query = mapWith("aggs",
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
                                        ),
                                        mapWith("bool",
                                                mapWith("should", createAppEnvSubQuery(appWithEnvList)
                                                )
                                        ),
                                        mapWith("wildcard",
                                                mapWith("content.[4] AuditLogDto.method", "*Facade*")
                                        )
                                )
                        )
                )
        );
    }

    @Override
    public Map<String, Object> getData() {
        return query;
    }
}
