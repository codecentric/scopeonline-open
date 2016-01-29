package de.codecentric.scopeonline.ui;

import static de.codecentric.scopeonline.ui.util.VaadinAssert.assertComponentIsPresent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.security.Principal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.security.auth.Subject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.sun.security.auth.UserPrincipal;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.persistence.BalanceHashMapQueries;
import de.codecentric.scopeonline.service.ConfigService;
import de.codecentric.scopeonline.service.ConfigServiceImpl;
import de.codecentric.scopeonline.ui.ScopeOnlineUI.Servlet;
import de.codecentric.scopeonline.ui.chart.GraphLayout;
import de.codecentric.scopeonline.ui.login.LoginLayout;
import de.codecentric.scopeonline.util.CalendarCalculator;
import de.codecentric.scopeonline.util.TestConfigService;

public class ScopeOnlineUITest {
	
	TestConfigService testConfigService = TestConfigService.getTestConfigService();
	
	
	
	private ScopeOnlineUI	ui;
	@Mock
	ConfigService			configService;
	@Mock
	BalanceHashMapQueries	balanceHashMapQueries;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("scopeonlinetest");
		EntityManager em = emf.createEntityManager();
		ui = new ScopeOnlineUI(CalendarCalculator.getDateAtSpecifiedMonthAfter(
				Calendar.getInstance(), -1), em, "test.properties");
		ui.init(null);
	}

	@Test
	public void servletClassShouldBeDeclared() throws Exception {
		assertNotNull(new Servlet());
	}

	@Test
	public void givenTheBasicApp_whenAppIsStarted_shouldBeSuccessfullyInitialized()
			throws Exception {
		assertEquals(ui.getBasicLayout(), ui.getContent());
	}

	@Test
	public void givenFreshStartup_shouldContainConfigService() throws Exception {
		assertNotNull(ui.getConfigService());
	}

	@Test
	public void givenFreshStartup_shouldContainLoginLayout() throws Exception {
		assertComponentIsPresent(ui.getBasicLayout(), ui.getLoginLayout());
	}

	@Test
	public void givenFreshStartup_shouldContainLogo() throws Exception {
		assertComponentIsPresent(ui.getBasicLayout(), ui.getLogo());

	}

	@Test
	public void givenSuccessfulLogin_shouldNoLongerContainLoginLayout()
			throws Exception {
		LoginLayout layout = ui.getLoginLayout();

		when(balanceHashMapQueries.getBalanceHashMap()).thenReturn(
				new HashMap<String, BalanceVO>());
		ui.setBalanceHashMapQueries(balanceHashMapQueries);

		ui.loginPass(buildDummySubject());
		assertFalse(layout.isAttached());
		assertNull(layout.getParent());
		assertNull(ui.getLoginLayout());
	}

	@Test
	public void givenSuccessfulLogin_shouldContainGraphLayout()
			throws Exception {
		when(balanceHashMapQueries.getBalanceHashMap()).thenReturn(
				new HashMap<String, BalanceVO>());
		ui.setBalanceHashMapQueries(balanceHashMapQueries);

		ui.loginPass(buildDummySubject());

		GraphLayout layout = ui.getGraphLayout();
		assertComponentIsPresent(ui.getBasicLayout(), layout);
	}

	@Test
	public void givenLogout_shouldCointainLoginLayout() throws Exception {
		GraphLayout graphLayout = mock(GraphLayout.class);
		ui.setGraphLayout(graphLayout);
		ui.logOut();

		LoginLayout loginLayout = ui.getLoginLayout();
		assertComponentIsPresent(ui.getBasicLayout(), loginLayout);
	}

	@After
	public void tearDown() throws Exception {
		ui = null;
	}

	private Subject buildDummySubject() {
		Set<Principal> principals = new LinkedHashSet<>();
		principals.add(new UserPrincipal("test"));

		Set<String> pubCredentials = new LinkedHashSet<>();
		pubCredentials.add(testConfigService.getUser());
		pubCredentials.add(testConfigService.getPass());
		pubCredentials.add(testConfigService.getOrganization());
		pubCredentials.add(testConfigService.getCustomer());

		Set<String> privCredentials = new LinkedHashSet<>();
		privCredentials.add(testConfigService.getUser());
		privCredentials.add(testConfigService.getPass());
		privCredentials.add(testConfigService.getOrganization());
		privCredentials.add(testConfigService.getCustomer());

		return new Subject(true, principals, pubCredentials, privCredentials);
	}
}