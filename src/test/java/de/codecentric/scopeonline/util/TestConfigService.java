package de.codecentric.scopeonline.util;

import de.codecentric.scopeonline.service.ConfigService;
import de.codecentric.scopeonline.service.ConfigServiceImpl;

public class TestConfigService {

	private String user;
	private String pass;
	private String organization;
	private String customer;

	private static TestConfigService testConfigService;
	private ConfigService configService;

	private TestConfigService() {
		configService = new ConfigServiceImpl("test.properties");
		user = configService.getProperty("testUser");
		pass = configService.getProperty("testPassword");
		organization = configService.getProperty("testOrganization");
		customer = configService.getProperty("testCustomer");
	}

	public static TestConfigService getTestConfigService() {
		if (testConfigService == null) {
			testConfigService = new TestConfigService();
		}
		return testConfigService;
	}
	
	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public String getOrganization() {
		return organization;
	}

	public String getCustomer() {
		return customer;
	}

}
