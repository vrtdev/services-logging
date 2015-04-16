package be.vrt.services.log.collector.transaction.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import org.slf4j.MDC;

import be.vrt.services.log.collector.transaction.dto.TransactionLogDto;

 
public class TransactionLoggerFilter implements Filter {
	
	private static final Logger LOG = LoggerFactory.getLogger("log.collector.transaction.logger");

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		String serverName = request.getServerName();
		String transactionUUID = addTransactionUUID(serverName);
		
		TransactionLogDto transaction = new TransactionLogDto();
		transaction.setTransactionUUID(transactionUUID);
		transaction.setServerName(serverName);
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		try{
			transaction.setStartTime(new Date(stopWatch.getStartTime()));
			transaction.setHttpMethod(request.getMethod());
			transaction.setUser(request.getUserPrincipal().getName());
			transaction.setResource(request.getRequestURL().toString());
			
			chain.doFilter(request, response);
			
			response.addHeader("transactionUUID", transactionUUID);
			
			stopWatch.stop();
			
			transaction.setDuration(stopWatch.getTime());
			transaction.setParameters(getParameters(request));
			
		} finally {
			LOG.info("Filter Info: {}", transaction);
		}
	}
	
	private String addTransactionUUID(String serverName) {
		String uuid = UUID.randomUUID().toString();
		String transactionUUID = serverName + "-" + uuid;
		MDC.put("transactionUUID", transactionUUID);
		return transactionUUID;
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
