package be.vrt.services.log.exposer.es.query;

import be.vrt.services.log.exposer.controller.JsonArray;
import be.vrt.services.logging.log.common.AppWithEnv;

import java.util.List;

import static be.vrt.services.log.exposer.controller.JsonMap.mapWith;

/**
 * When we migrate this to java 8 we can use a trait class?
 */
public abstract class AppWithEnvsElasticSearchQuery implements ElasticSearchQuery {

	protected JsonArray createAppEnvSubQuery(List<AppWithEnv> appWithEnvList) {
		JsonArray array = new JsonArray();
		for (AppWithEnv appWithEnv : appWithEnvList) {
			array.add(mapWith("bool",
					mapWith("must",
							JsonArray.with(
									mapWith("term",
											mapWith("environmentInfo.env", appWithEnv.getEnv())
									),
									mapWith("term",
											mapWith("environmentInfo.app", appWithEnv.getApp())
									)
							)
					)
			));
		}
		return array;
	}
}
