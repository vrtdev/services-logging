package be.vrt.services.logging.sample.http;

import be.vrt.services.log.exposer.controller.JsonArray;
import be.vrt.services.log.exposer.controller.JsonMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleServlet extends HttpServlet {

    Logger log = LoggerFactory.getLogger("SampleServlet");

	public SampleServlet() {
		log.info("Startup of the Sample Servlet - Is this love?");
	}
	
	

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        log.debug("I'm the debug-line");
        log.info("I'm the info-line");
        log.error("I'm the error-line");

        Map map = new HashMap();
        map.put("request-path", req.getPathInfo());

        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();

        if (path.matches("/.[^/]*")) {
            switch (path) {
                case "/error":
                    map.put("status", "Error");
                    resp.setStatus(500);
                    break;
                case "/fail":
                    map.put("status", "Failed");
                    resp.setStatus(400);
                    break;
                case "/test":
                    map.put("status", "Test");
                    map.put("hits", doTest());
                    break;
                default:
                    map.put("status", "Ok");
                    break;
            }
        }

        resp.setContentType("application/json");
        String json = mapper.writeValueAsString(map);
        resp.getWriter().print(json);
    }

    private Object doTest() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String id = "sabayon.lan.rto.be-11b13cc8-63ad-42d2-8791-6e0138f9e7ea";
            Map<String, Object> query = JsonMap.mapWith("query",
                JsonMap.mapWith("bool",
                    JsonMap.mapWith("should",
                        JsonArray.with(JsonMap.mapWith("match_phrase", JsonMap.mapWith("transactionId", id)), JsonMap.mapWith("match_phrase", JsonMap.mapWith("flowId", id))
                        )
                    )));
            URL url = new URL("http://localhost:9200/logg*/_search");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(1000);
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            mapper.writeValue(con.getOutputStream(), query);
            if (con.getResponseCode() > 299) {
                System.err.println(">> Failed to save to ES > [" + con.getResponseCode() + "] :" + con.getResponseMessage());
                mapper.writeValue(System.err, query);
            }

            HashMap<String, Object> result = mapper.readValue(con.getInputStream(), HashMap.class);

            return ((Map) result.get("hits"));
//            return  result;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
