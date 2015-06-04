package be.vrt.services.log.exposer.controller;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class TransactionLogController extends HttpServlet {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Map map = new HashMap();
		List<AbstractTransactionLog> logs = TransactionRegistery.list();
		map.put("request-path", req.getPathInfo());
		map.put("transaction-list-size", logs.size());
		map.put("transaction-list", logs);
		
		resp.setContentType("application/json");
		String json = mapper.writeValueAsString(map);
		resp.getWriter().print(json);
		
	}

}
