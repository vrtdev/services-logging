package be.vrt.services.log.exposer.controller;

import be.vrt.services.log.exposer.es.ElasticSearchQueryExecutor;
import be.vrt.services.log.exposer.es.query.DailyProblemQuery;
import be.vrt.services.log.exposer.es.query.ElasticSearchQuery;
import be.vrt.services.log.exposer.es.query.StatsQuery;
import be.vrt.services.log.exposer.es.result.ElasticSearchCountResult;
import be.vrt.services.log.exposer.es.result.ElasticSearchResult;
import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import be.vrt.services.logging.log.common.transaction.TransactionRegisteryMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionLogControllerTest {

	@Mock
	private HttpServletRequest aRequest;
	@Mock
	private HttpServletResponse aResponse;
	@Mock
	private PrintWriter printWriter;
	@Mock
	private ElasticSearchQueryExecutor elasticSearchQueryExecutor;

	@InjectMocks
	private TransactionLogController transactionLogController;

	@Mock
	private TransactionRegistery instance;

	@Test
	public void doGet_whenNothingMatches_thenJsonPrettyEmpty() throws ServletException, IOException {
		when(aRequest.getPathInfo()).thenReturn("aPathInfo");
		when(aResponse.getWriter()).thenReturn(printWriter);


		transactionLogController.doGet(aRequest, aResponse);

		verify(aResponse).setContentType("application/json");
		verify(printWriter).print("{\"request-path\":\"aPathInfo\",\"info\":{\"statUrl\":\"\",\"urls\":[]}}");

	}

	@Test
	public void doGet_whenErrorMatches_thenHasErrorLogs() throws ServletException, IOException {
		when(aRequest.getPathInfo()).thenReturn("/error");
		when(aResponse.getWriter()).thenReturn(printWriter);
		instance = TransactionRegisteryMock.doSpy();
		AbstractTransactionLog transactionLog = new AbstractTransactionLog() {
			@Override
			public String getType() {
				return "aType";
			}
		};
		transactionLog.setStatus(AbstractTransactionLog.Type.ERROR);
		transactionLog.setErrorReason("aReason");
		instance.registerTransactionLocal(transactionLog);

		transactionLogController.doGet(aRequest, aResponse);

		verify(aResponse).setContentType("application/json");
		ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
		verify(printWriter).print(json.capture());
		String value = json.getValue();
		Assert.assertTrue(value.contains("/error"));
		Assert.assertTrue(value.contains("\"errorReason\":\"aReason\""));
		Assert.assertTrue(value.contains("\"status\":\"ERROR\""));
	}

	@Test
	public void doGet_whenFailMatches_thenHasFailLogs() throws ServletException, IOException {
		when(aRequest.getPathInfo()).thenReturn("/fail");
		when(aResponse.getWriter()).thenReturn(printWriter);
		instance = TransactionRegisteryMock.doSpy();
		AbstractTransactionLog transactionLog = new AbstractTransactionLog() {
			@Override
			public String getType() {
				return "aType";
			}
		};
		transactionLog.setStatus(AbstractTransactionLog.Type.FAILED);
		transactionLog.setErrorReason("aReason");
		instance.registerTransactionLocal(transactionLog);

		transactionLogController.doGet(aRequest, aResponse);

		verify(aResponse).setContentType("application/json");
		ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
		verify(printWriter).print(json.capture());
		String value = json.getValue();
		Assert.assertTrue(value.contains("/fail"));
		Assert.assertTrue(value.contains("\"errorReason\":\"aReason\""));
		Assert.assertTrue(value.contains("\"status\":\"FAILED\""));
	}


	@Test
	public void doGet_whenFlowsMatches_thenHasFlowIdLogs() throws ServletException, IOException {
		when(aRequest.getPathInfo()).thenReturn("/flows");
		when(aResponse.getWriter()).thenReturn(printWriter);
		instance = TransactionRegisteryMock.doSpy();
		instance.registerTransactionId("aTransactionId", "aFlowId");

		transactionLogController.doGet(aRequest, aResponse);

		verify(aResponse).setContentType("application/json");
		ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
		verify(printWriter).print(json.capture());
		String value = json.getValue();
		Assert.assertTrue(value.contains("/flows"));
		Assert.assertTrue(value.contains("\"transactionId\":\"aTransactionId\",\"flowId\":\"aFlowId\""));
	}



	@Test
	public void doGet_whenStaticFlowsMatches_thenHasStaticFlowLogs() throws ServletException, IOException {
		when(aRequest.getPathInfo()).thenReturn("/static-flows");
		when(aResponse.getWriter()).thenReturn(printWriter);
		instance = TransactionRegisteryMock.doSpy();
		TransactionRegistery.registerStaticFlow("aStaticFlow");

		transactionLogController.doGet(aRequest, aResponse);

		verify(aResponse).setContentType("application/json");
		ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
		verify(printWriter).print(json.capture());
		String value = json.getValue();
		Assert.assertTrue(value.contains("/static-flows"));
		Assert.assertTrue(value.contains("\"name\":\"aStaticFlow\""));
	}

	@Test
	public void doGet_whenTransactionPathMatches_ESQueryExecuted() throws ServletException, IOException {
		when(aRequest.getPathInfo()).thenReturn("/transaction/blabla");
		when(aResponse.getWriter()).thenReturn(printWriter);
		when(elasticSearchQueryExecutor.executeSearchQueryMultiInstances(any(ElasticSearchQuery.class))).thenReturn(ElasticSearchResult.empty());
		instance = TransactionRegisteryMock.doSpy();

		transactionLogController.doGet(aRequest, aResponse);

		verify(aResponse).setContentType("application/json");
		verify(elasticSearchQueryExecutor).executeSearchQueryMultiInstances(any(ElasticSearchQuery.class));
		ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
		verify(printWriter).print(json.capture());
		String value = json.getValue();
		Assert.assertTrue(value.contains("/transaction/blabla"));
		Assert.assertTrue(value.contains("\"hits\":{}"));
	}

	@Test
	public void doGet_whenStatsOverviewPathMatches_ESQueryExecuted() throws ServletException, IOException {
		when(aRequest.getPathInfo()).thenReturn("/stats/overview/blabla");
		when(aResponse.getWriter()).thenReturn(printWriter);
		when(elasticSearchQueryExecutor.executeCountQuery(any(StatsQuery.class))).thenReturn(ElasticSearchCountResult.empty());
		instance = TransactionRegisteryMock.doSpy();

		transactionLogController.doGet(aRequest, aResponse);

		verify(aResponse).setContentType("application/json");
		verify(elasticSearchQueryExecutor).executeCountQuery(any(StatsQuery.class));
		ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
		verify(printWriter).print(json.capture());
		String value = json.getValue();
		Assert.assertTrue(value.contains("/stats/overview/blabla"));
		Assert.assertTrue(value.contains("\"agg\":{}"));
	}


	@Test
	public void doGet_whenStatsErrorsPathMatches_ESQueryExecuted() throws ServletException, IOException {
		when(aRequest.getPathInfo()).thenReturn("/stats/errors/blabla");
		when(aResponse.getWriter()).thenReturn(printWriter);
		when(elasticSearchQueryExecutor.executeSearchQuery(any(DailyProblemQuery.class))).thenReturn(ElasticSearchResult.empty());
		instance = TransactionRegisteryMock.doSpy();

		transactionLogController.doGet(aRequest, aResponse);

		verify(aResponse).setContentType("application/json");
		verify(elasticSearchQueryExecutor).executeSearchQuery(any(DailyProblemQuery.class));
		ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
		verify(printWriter).print(json.capture());
		String value = json.getValue();
		Assert.assertTrue(value.contains("/stats/errors/blabla"));
		Assert.assertTrue(value.contains("\"statshits\":{}"));
	}

	@Test
	public void doGet_whenStatsFailuresPathMatches_ESQueryExecuted() throws ServletException, IOException {
		when(aRequest.getPathInfo()).thenReturn("/stats/failures/blabla");
		when(aResponse.getWriter()).thenReturn(printWriter);
		when(elasticSearchQueryExecutor.executeSearchQuery(any(DailyProblemQuery.class))).thenReturn(ElasticSearchResult.empty());
		instance = TransactionRegisteryMock.doSpy();

		transactionLogController.doGet(aRequest, aResponse);

		verify(aResponse).setContentType("application/json");
		verify(elasticSearchQueryExecutor).executeSearchQuery(any(DailyProblemQuery.class));
		ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
		verify(printWriter).print(json.capture());
		String value = json.getValue();
		Assert.assertTrue(value.contains("/stats/failures/blabla"));
		Assert.assertTrue(value.contains("\"statshits\":{}"));
	}
}
