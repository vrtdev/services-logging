package be.vrt.services.logging.log.common;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AppWithEnvTest {

	@Test
	public void toListFromString() throws Exception {
		List<AppWithEnv> appWithEnvs = AppWithEnv.toListFromString("app@DEV,  ,noENV,wrong|symbol,app2@STAG,|trail");
		assertEquals(2, appWithEnvs.size());
		assertEquals(new AppWithEnv("app", "DEV"), appWithEnvs.get(0));
		assertEquals(new AppWithEnv("app2", "STAG"), appWithEnvs.get(1));
	}
}
