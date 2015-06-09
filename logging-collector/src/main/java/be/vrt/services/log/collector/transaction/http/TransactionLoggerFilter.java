package be.vrt.services.log.collector.transaction.http;

import be.vrt.services.logging.log.common.LogTransaction;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.vrt.services.log.collector.transaction.dto.HttpTransactionLogDto;
import be.vrt.services.logging.log.common.Constants;

public class TransactionLoggerFilter implements Filter, Constants {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionLoggerFilter.class);

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		HttpTransactionLogDto transaction = generateTransactionLogDtoFromRequest(request);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		transaction.setStartDate(new Date(stopWatch.getStartTime()));

		response.setHeader(TRANSACTION_ID, transaction.getTransactionId());
		response.setHeader(FLOW_ID, transaction.getFlowId());
		try {
			chain.doFilter(request, response);
		} finally {
			stopWatch.stop();

			transaction.setFlowId(LogTransaction.flow());
			transaction.setDuration(stopWatch.getTime());
			transaction.setParameters(getParameters(request));
			transaction.responseStatus(response.getStatus());
			
			LOG.info("Filter Info: [{}] ==> {} | {} ", transaction.getResponseStatus(), transaction.getHttpMethod(), transaction.getResource(), transaction);
			TransactionRegistery.register(transaction);
		}
	}

	private HttpTransactionLogDto generateTransactionLogDtoFromRequest(HttpServletRequest request) {
		String serverName = request.getServerName();

		HttpTransactionLogDto transaction = new HttpTransactionLogDto();
		String flowId = LogTransaction.createFlowId(request.getHeader(FLOW_ID), request.getHeader(ORIGIN_USER));
		
		transaction.setFlowId(flowId);
		transaction.setTransactionId(LogTransaction.id());
		transaction.setServerName(serverName);

		transaction.setHttpMethod(request.getMethod());
		transaction.setUser(request.getUserPrincipal() == null ? null : request.getUserPrincipal().getName());
		transaction.setResource(request.getRequestURL().toString());

		return transaction;
	}

	private Map<String, String> getParameters(HttpServletRequest request) {
		Map<String, String> params = new HashMap<>();
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String paramName = keys.nextElement();
			params.put(paramName, request.getParameter(paramName));
		}
		return params;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
