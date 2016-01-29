package de.codecentric.scopeonline.ui;

import java.util.Calendar;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.security.auth.Subject;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.codecentric.scopeonline.charts.FlowChart;
import de.codecentric.scopeonline.charts.LineChartBuilderImpl;
import de.codecentric.scopeonline.domain.Account;
import de.codecentric.scopeonline.persistence.BalanceHashMapQueries;
import de.codecentric.scopeonline.persistence.BalanceHashMapQueriesImpl;
import de.codecentric.scopeonline.rpc.BalanceCache;
import de.codecentric.scopeonline.rpc.SOAPAdapter;
import de.codecentric.scopeonline.rpc.SOAPRequestImpl;
import de.codecentric.scopeonline.service.AccountService;
import de.codecentric.scopeonline.service.ConfigService;
import de.codecentric.scopeonline.service.ConfigServiceImpl;
import de.codecentric.scopeonline.service.LoginService;
import de.codecentric.scopeonline.service.LoginServiceImpl;
import de.codecentric.scopeonline.service.ScopeVisioAccountServiceImpl;
import de.codecentric.scopeonline.ui.chart.GraphLayout;
import de.codecentric.scopeonline.ui.chart.GraphMediator;
import de.codecentric.scopeonline.ui.login.LoginDelegate;
import de.codecentric.scopeonline.ui.login.LoginLayout;

@Theme("runo")
@SuppressWarnings("serial")
public class ScopeOnlineUI extends UI implements LoginDelegate {

	private final Calendar	earliestDate;

	public void setConfigService(final ConfigService configService) {
		this.configService = configService;
	}

	private ConfigService			configService;
	private VerticalLayout			basicLayout;
	private LoginLayout				loginLayout;
	private GraphLayout				graphLayout;
	private Image					logo;
	private Subject					subject;
	private BalanceHashMapQueries	balanceHashMapQueries;
	private String					configFileName;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ScopeOnlineUI.class, widgetset = "de.codecentric.scopeonline.ui.AppWidgetSet")
	public static class Servlet extends VaadinServlet {

	}

	public ScopeOnlineUI(final Calendar earliestDate,
			EntityManager entityManager, String configFileName) {
		this.earliestDate = earliestDate;
		this.configFileName = configFileName;
	}

	public ScopeOnlineUI() {
		this.earliestDate = Calendar.getInstance();
		earliestDate.set(2011, Calendar.JANUARY, 1, 0, 0, 0);
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("scopeonline");
		balanceHashMapQueries = new BalanceHashMapQueriesImpl(emf);
		configFileName = "scopeonline.properties";
	}

	@Override
	protected void init(VaadinRequest request) {
		configService = buildConfigService(configFileName);

		initBasicLayout();

		initLogo();
		basicLayout.addComponent(logo);
		basicLayout.setExpandRatio(logo, 1);
		basicLayout.setComponentAlignment(logo, Alignment.TOP_LEFT);

		initLoginLayout();
		basicLayout.addComponent(loginLayout);
		basicLayout.setExpandRatio(loginLayout, 9);

	}

	private void initBasicLayout() {
		basicLayout = new VerticalLayout();
		basicLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		basicLayout.setSizeFull();
		setContent(basicLayout);
	}

	private void initLogo() {
		logo = new Image();
		logo.setSource(new ExternalResource("VAADIN/logo-codecentric.png"));
		logo.setWidth("217px");
		logo.setHeight("37px");
	}

	private void initLoginLayout() {
		loginLayout = new LoginLayout(configService);
		loginLayout.setDelegate(this);

		loginLayout.setLoginService(buildLoginService());
		loginLayout.init();
	}

	private LoginService buildLoginService() {
		LoginServiceImpl loginService = new LoginServiceImpl();
		loginService.setConfigService(configService);
		return loginService;
	}

	@Override
	public void loginPass(final Subject subject) {
		this.subject = subject;

		removeLoginLayout();
		initGraphLayout();

		basicLayout.addComponent(getGraphLayout());
		basicLayout.setExpandRatio(getGraphLayout(), 9);
	}

	@Override
	public void logOut() {
		this.subject = null;

		removeGraphLayout();
		initLoginLayout();

		basicLayout.addComponent(loginLayout);
		basicLayout.setExpandRatio(getLoginLayout(), 9);
	}

	private void removeGraphLayout() {
		basicLayout.removeComponent(getGraphLayout());
		getGraphLayout().destroy();
		setGraphLayout(null);
	}

	private void removeLoginLayout() {
		basicLayout.removeComponent(loginLayout);
		loginLayout.destroy();
		loginLayout = null;
	}

	private void initGraphLayout() {
		HashMap<String, AccountService> accountServiceList = buildAccountServices();
		GraphMediator mediator = new GraphMediator(earliestDate);
		mediator.setAccountServices(accountServiceList);
		mediator.setAccount(new Account());
		mediator.setCache(new BalanceCache(balanceHashMapQueries));
		setGraphLayout(new GraphLayout(mediator));
		getGraphLayout().setConfigService(configService);
		getGraphLayout().setDelegate(this);
		getGraphLayout().setChart(buildFlowChart());
		getGraphLayout().init();
	}

	private HashMap<String, AccountService> buildAccountServices() {
		HashMap<String, AccountService> accountServiceList = new HashMap<>();
		for (String organization : configService.getOrganizations())
			accountServiceList.put(organization,
					buildAccountService(organization));
		return accountServiceList;
	}

	private ScopeVisioAccountServiceImpl buildAccountService(
			final String organization) {
		return new ScopeVisioAccountServiceImpl(buildSOAPAdapter(organization));
	}

	private ConfigService buildConfigService(String propertyFileName) {
		return new ConfigServiceImpl(propertyFileName);
	}

	private SOAPAdapter buildSOAPAdapter(final String organization) {
		SOAPAdapter adapter = new SOAPAdapter(configService);
		String user = getUserFromSubject();
		String pass = getPasswordFromSubject();
		String customer = getCustomerFromSubject();
		adapter.setSoapRequest(new SOAPRequestImpl(user, pass, organization,
				customer));
		return adapter;
	}

	public void setBalanceHashMapQueries(
			BalanceHashMapQueries balanceHashMapQueries) {
		this.balanceHashMapQueries = balanceHashMapQueries;
	}

	private String getUserFromSubject() {
		return (String) subject.getPrivateCredentials().toArray()[0];
	}

	private String getPasswordFromSubject() {
		return (String) subject.getPrivateCredentials().toArray()[1];
	}

	private String getCustomerFromSubject() {
		return (String) subject.getPrivateCredentials().toArray()[3];
	}

	private FlowChart buildFlowChart() {
		return new FlowChart(new LineChartBuilderImpl());
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public VerticalLayout getBasicLayout() {
		return basicLayout;
	}

	public LoginLayout getLoginLayout() {
		return loginLayout;
	}

	public GraphLayout getGraphLayout() {
		return graphLayout;
	}

	public Image getLogo() {
		return logo;
	}

	public void setGraphLayout(GraphLayout graphLayout) {
		this.graphLayout = graphLayout;
	}

}
