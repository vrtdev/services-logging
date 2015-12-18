package be.vrt.services.log.exposer.es;

import be.vrt.services.log.exposer.es.query.ElasticSearchQuery;
import be.vrt.services.log.exposer.es.result.ElasticSearchCountResult;
import be.vrt.services.log.exposer.es.result.ElasticSearchResult;

public interface ElasticSearchQueryExecutor {

	ElasticSearchResult executeSearchQuery(ElasticSearchQuery query);

	ElasticSearchResult executeSearchQueryMultiInstances(ElasticSearchQuery query);

	ElasticSearchCountResult executeCountQuery(ElasticSearchQuery query);
}
