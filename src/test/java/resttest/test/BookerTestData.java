package resttest.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BookerTestData {
	public static String BASE_URL = "";
	public static String ADM_USERNAME = "";
	public static String ADM_PASSWORD = "";

	public static final int INITIAL_AMOUNT = 500;
	public static final int UPDATED_AMOUNT = 700;

	public BookerTestData() throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream configFile = classLoader.getResourceAsStream("config.properties");

		Properties connectionProperties = new Properties();
		connectionProperties.load(configFile);

		this.BASE_URL = connectionProperties.getProperty("service.url");
		this.ADM_USERNAME = connectionProperties.getProperty("admin.username");
		this.ADM_PASSWORD = connectionProperties.getProperty("admin.password");
	}

	public String getBaseUrl() {
		return this.BASE_URL;
	}

	public String getAdmUsername() {
		return this.ADM_USERNAME;
	}

	public String getAdmPassword() {
		return this.ADM_PASSWORD;
	}
}
