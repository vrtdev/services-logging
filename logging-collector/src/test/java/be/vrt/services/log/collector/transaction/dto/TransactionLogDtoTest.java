package be.vrt.services.log.collector.transaction.dto;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import org.junit.Test;

public class TransactionLogDtoTest {
	
	@Test
	public void toString_givenTransactionLogDto() {
		HttpTransactionLogDto transactionLogDto = new HttpTransactionLogDto();
		transactionLogDto.setDuration(1445L);
		transactionLogDto.setHttpMethod(HttpMethod.PUT);
		transactionLogDto.setStartTime(new Date());
		transactionLogDto.setUser("test");
		transactionLogDto.setServerName("mediazone-dev");
		transactionLogDto.setResource("http://mediazone-admin-dev.vrt.be/rest/client/v1/assetsources");
		transactionLogDto.setTransactionId("mediazone-dev-124-5GL");
		Map<String, String> params = new HashMap<>();
		params.put("ardomeId", "123456789098721");
		params.put("test", "test");
		transactionLogDto.setParameters(params);
		
		String result = transactionLogDto.toString();
		assertNotNull(result);
	}
	
}
