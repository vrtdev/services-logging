package be.vrt.services.logging.log.common;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppWithEnv {

	private final String app;
	private final String env;

	public AppWithEnv(String app, String env) {
		this.app = app;
		this.env = env;
	}

	private AppWithEnv(String[] appEnv) {
		this(appEnv[0], appEnv[1]);
	}

	public static List<AppWithEnv> toListFromString(String input) {
		String[] apps = input.split(",");
		List<AppWithEnv> list = new ArrayList<>();
		for (String app : apps) {
			String[] appEnv = app.split("@");
			if(appEnv.length == 2 && StringUtils.isNoneBlank(appEnv)) {
				list.add(new AppWithEnv(appEnv));
			}
		}
		return list;
	}

	public String getApp() {
		return app;
	}

	public String getEnv() {
		return env;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AppWithEnv that = (AppWithEnv) o;
		return Objects.equals(app, that.app) && Objects.equals(env, that.env);
	}

	@Override
	public int hashCode() {
		return Objects.hash(app, env);
	}
}
