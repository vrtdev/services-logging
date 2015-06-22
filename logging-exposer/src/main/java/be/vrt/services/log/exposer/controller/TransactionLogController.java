package be.vrt.services.log.exposer.controller;

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

@SuppressWarnings("serial")
public class TransactionLogController extends HttpServlet {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Map map = new HashMap();
		List logs;
		String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
		
		switch (path) {
			case "/error":
				logs = TransactionRegistery.listErrors();
				break;
			case "/fail":
				logs = TransactionRegistery.listFailures();
				break;
			case "/ids":
				logs = TransactionRegistery.listIds();
				break;
			default:
				logs = TransactionRegistery.list();
		}
		
		map.put("request-path", req.getPathInfo());
		map.put("transaction-list-size", logs.size());
		map.put("transaction-list", logs);
		
		resp.setContentType("application/json");
		String json = mapper.writeValueAsString(map);
		resp.getWriter().print(json);
		
	}
	
}
