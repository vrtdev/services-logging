package be.vrt.services.logging.log.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AppWithEnvTest {

	public static final String APP = "app";
	public static final String ENV = "env";

	@Test
	public void toListFromString() throws Exception {
		List<AppWithEnv> appWithEnvs = AppWithEnv.toListFromString("app@DEV,  ,noENV,wrong|symbol,app2@STAG,|trail");
		assertEquals(2, appWithEnvs.size());
		assertEquals(new AppWithEnv(APP, "DEV"), appWithEnvs.get(0));
		assertEquals(new AppWithEnv("app2", "STAG"), appWithEnvs.get(1));
	}

	@Test
	public void init_whenAppAndEnvProvided_thenTestCoverageIsIncreased(){
		AppWithEnv appWithEnv = new AppWithEnv(APP, ENV);

		Assert.assertEquals(APP, appWithEnv.getApp());
		Assert.assertEquals(ENV, appWithEnv.getEnv());
	}
}
