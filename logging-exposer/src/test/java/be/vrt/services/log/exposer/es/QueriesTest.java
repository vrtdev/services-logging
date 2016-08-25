package be.vrt.services.log.exposer.es;

import be.vrt.services.log.exposer.es.query.DailyProblemQuery;
import be.vrt.services.log.exposer.es.query.DetailQuery;
import be.vrt.services.log.exposer.es.query.StatsQuery;
import be.vrt.services.log.exposer.es.result.ElasticSearchCountResult;
import be.vrt.services.log.exposer.es.result.ElasticSearchResult;
import be.vrt.services.logging.log.common.AppWithEnv;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import org.elasticsearch.client.Client;
import org.joda.time.DateTime;
import org.junit.*;

import java.util.*;

public class QueriesTest {

	private static InMemoryMongo inMemoryMongo;
	private static ElasticSearchClientQueryExecutor elasticSearchQueryExecutor;

	@BeforeClass
	public static void setUpClass() throws Exception {
		inMemoryMongo = new InMemoryMongo().start();
		Client client = inMemoryMongo.getClient();

		elasticSearchQueryExecutor = new ElasticSearchClientQueryExecutor(client);
	}

	@Before
	public void setUp() throws Exception {
		inMemoryMongo.reCreateIndex("logging", "es-mapping.json");
	}

	private void index(String... logResources) {
		inMemoryMongo.index("logging", "logs", logResources);
		inMemoryMongo.flush("logging");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		inMemoryMongo.stop();
	}

	@Test
	public void testDetailQuery_transactionId() throws Exception {
		index("detail/log.json", "detail/log2.json");
		DetailQuery query = new DetailQuery("mezoapptest1.servers.vrt.be-10ce184d-b79f-4ec4-b75e-24b7135a175c");
		ElasticSearchResult elasticSearchResult = elasticSearchQueryExecutor.executeSearchQuery(query);
		assertHasNumberOfHits(elasticSearchResult, 1);
	}

	@Test
	public void testDetailQuery_transactionId_noMatch() throws Exception {
		index("detail/log.json", "detail/log2.json");
		DetailQuery query = new DetailQuery("mezoap.servers.vrt.be-10ce184d-b79f-4ec4-b75e-24b7135a175c");
		ElasticSearchResult elasticSearchResult = elasticSearchQueryExecutor.executeSearchQuery(query);
		assertHasNumberOfHits(elasticSearchResult, 0);
	}

	@Test
	public void testDetailQuery_flowId() throws Exception {
		index("detail/log.json", "detail/log2.json");
		DetailQuery query = new DetailQuery("f2f3a0f4-ab7e-4077-8810-846ab5eefa26000-2015-11-30T16:14:46.766+01:00-SYSTEM-FOLDERWATCH");
		ElasticSearchResult elasticSearchResult = elasticSearchQueryExecutor.executeSearchQuery(query);
		assertHasNumberOfHits(elasticSearchResult, 1);
	}

	@Test
	public void testDetailQuery_flowId_noMatch() throws Exception {
		index("detail/log.json", "detail/log2.json");
		DetailQuery query = new DetailQuery("-ab7e-4077-8810-846ab5eefa26000-2015-11-30T16:14:46.766+01:00-SYSTEM-FOLDERWATCH");
		ElasticSearchResult elasticSearchResult = elasticSearchQueryExecutor.executeSearchQuery(query);
		assertHasNumberOfHits(elasticSearchResult, 0);
	}

	@Test
	public void testDailyProblemQuery_onlyRetrieveFacadeInTimeRangeWithLevel_noAppAndEnv() throws Exception {
		index(
				new DailyLogEntry(new DateTime(2015, 1, 1, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 1, 12, 0), log("app1", "TEST")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 1, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "ERROR"),

				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "TEST")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "ERROR"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("otherAPP", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "DEV")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app2", "TEST")).addAuditLog("thisFacadeThing", "WARN"),

				new DailyLogEntry(new DateTime(2015, 1, 3, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 3, 12, 0), log("app1", "TEST")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 3, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "ERROR")
		);
		DailyProblemQuery query = new DailyProblemQuery("2015-01-02", "WARN");
		ElasticSearchResult elasticSearchResult = elasticSearchQueryExecutor.executeSearchQuery(query);
		assertHasNumberOfHits(elasticSearchResult, 4);
	}

	@Test
	public void testDailyProblemQuery_onlyRetrieveFacadeInTimeRangeWithLevelAppAndEnv() throws Exception {
		index(
				new DailyLogEntry(new DateTime(2015, 1, 1, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 1, 12, 0), log("app1", "TEST")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 1, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "ERROR"),

				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "STAG")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "TEST")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "STAG")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "ERROR"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "STAG")).addAuditLog("thisFacadeThing", "ERROR"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("otherAPP", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("otherAPP", "STAG")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app1", "DEV")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app2", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0), log("app2", "STAG")).addAuditLog("thisFacadeThing", "WARN"),

				new DailyLogEntry(new DateTime(2015, 1, 3, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 3, 12, 0), log("app1", "TEST")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 3, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "ERROR")
		);
		DailyProblemQuery query = new DailyProblemQuery("2015-01-02", "WARN", Lists.newArrayList(new AppWithEnv("app1", "TEST"), new AppWithEnv("app2", "STAG")));
		ElasticSearchResult elasticSearchResult = elasticSearchQueryExecutor.executeSearchQuery(query);
		assertHasNumberOfHits(elasticSearchResult, 2);
	}

	@Test
	public void testDailyProblemQuery_whenMultipleHits_thenSortByDate() throws Exception {
		index(
				new DailyLogEntry("first",  new DateTime(2015, 1, 2, 12, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry("second", new DateTime(2015, 1, 2, 12, 3), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN")
		);
		DailyProblemQuery query = new DailyProblemQuery("2015-01-02", "WARN", Lists.newArrayList(new AppWithEnv("app1", "TEST"), new AppWithEnv("app2", "STAG")));
		ElasticSearchResult elasticSearchResult = elasticSearchQueryExecutor.executeSearchQuery(query);
		assertHasNumberOfHits(elasticSearchResult, 2);
		assertHitsIdsInOrder(elasticSearchResult, "second", "first");
	}

	@Test
	public void testStatsQuery_onlyRetrieveFacadeInTimeRangeWithLevelAppAndEnv() throws Exception {
		index(
				new DailyLogEntry(new DateTime(2015, 1, 1, 12, 0, 13, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 1, 12, 0, 13, 0), log("app1", "TEST")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 1, 12, 0, 13, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "ERROR"),

				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("app1", "STAG")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("app1", "TEST")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("app1", "STAG")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "ERROR"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("app1", "STAG")).addAuditLog("thisFacadeThing", "ERROR"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("otherAPP", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("otherAPP", "STAG")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("app1", "DEV")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("app2", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 2, 12, 0, 13, 0), log("app2", "STAG")).addAuditLog("thisFacadeThing", "WARN"),

				new DailyLogEntry(new DateTime(2015, 1, 3, 12, 0, 13, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 3, 12, 0, 13, 0), log("app1", "TEST")).addAuditLog("thisServiceThing", "WARN"),
				new DailyLogEntry(new DateTime(2015, 1, 3, 12, 0, 13, 0), log("app1", "TEST")).addAuditLog("thisFacadeThing", "ERROR")
		);
		StatsQuery query = new StatsQuery("2015-01-02", Lists.newArrayList(new AppWithEnv("app1", "TEST"), new AppWithEnv("app2", "STAG")));
		ElasticSearchCountResult elasticSearchResult = elasticSearchQueryExecutor.executeCountQuery(query);
		assertHasNumberOfHits(elasticSearchResult, 3);
	}

	private void assertHitsIdsInOrder(ElasticSearchResult elasticSearchResult, String... ids) {
		Map hits = elasticSearchResult.getData();
		List hitList = (List) hits.get("hits");
		for (int i = 0; i < ids.length; i++) {
			Assert.assertEquals(ids[i], ((Map) hitList.get(i)).get("_id"));
		}
	}

	private void index(DailyLogEntry... entries) {
		inMemoryMongo.index("logging", "logs", entries);
		inMemoryMongo.flush("logging");
	}

	private void assertHasNumberOfHits(ElasticSearchResult elasticSearchResult, int expectedNumberOfHits) {
		Map hits = elasticSearchResult.getData();
		List hitList = (List) hits.get("hits");
		Assert.assertEquals(expectedNumberOfHits, hitList.size());
	}

	private void assertHasNumberOfHits(ElasticSearchCountResult elasticSearchResult, int expectedNumberOfHits) {
		Map hits = elasticSearchResult.getHits();
		Integer totalHits = (Integer) hits.get("total");
		Assert.assertEquals(expectedNumberOfHits, totalHits.intValue());
	}

	private static class DailyLogEntry implements InMemoryMongo.EntryWithId {

		private final String id;
		@JsonProperty
		private Date logDate;
		@JsonProperty
		private Map<String, Object> content = new HashMap<>();
		@JsonProperty
		private EnvironmentLogInfo environmentInfo;

		public DailyLogEntry(String id, DateTime logDate, EnvironmentLogInfo environmentInfo) {
			this.id = id;
			this.logDate = logDate.toDate();
			this.environmentInfo = environmentInfo;
		}

		public DailyLogEntry(DateTime logDate, EnvironmentLogInfo environmentInfo) {
			this(UUID.randomUUID().toString(), logDate, environmentInfo);
		}

		public DailyLogEntry(DateTime logDate) {
			this(UUID.randomUUID().toString(), logDate, null);
		}

		public DailyLogEntry addAuditLog(String method, String auditLevel) {
			this.content.put("[4] AuditLogDto", new BasicAuditLog(method, auditLevel));
			return this;
		}

		@Override
		public String getId() {
			return id;
		}
	}

	private EnvironmentLogInfo log(String app, String env) {
		return new EnvironmentLogInfo(app, env);
	}

	private static class EnvironmentLogInfo {
		@JsonProperty
		private String app;
		@JsonProperty
		private String env;

		public EnvironmentLogInfo(String app, String env) {
			this.app = app;
			this.env = env;
		}
	}

	private static class BasicAuditLog {
		@JsonProperty
		private String method;
		@JsonProperty
		private String auditLevel;

		public BasicAuditLog(String method, String auditLevel) {
			this.method = method;
			this.auditLevel = auditLevel;
		}
	}
}
