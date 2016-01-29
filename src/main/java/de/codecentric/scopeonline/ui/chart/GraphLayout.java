package de.codecentric.scopeonline.ui.chart;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.codecentric.scopeonline.charts.FlowChart;
import de.codecentric.scopeonline.charts.LineChartBuilderImpl;
import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.service.ConfigService;
import de.codecentric.scopeonline.ui.login.LoginDelegate;
import de.codecentric.scopeonline.util.CalendarCalculator;

public class GraphLayout extends VerticalLayout implements DateRangeDelegate {
	private Label				label;
	private Image				image;

	private LoginDelegate		loginDelegate;
	private FlowChart			chart;
	private DateRangeLayout		dateRangeLayout;
	private Calendar			fromDate;
	private Calendar			toDate;
	private ConfigService		configService;
	private final GraphMediator	mediator;
	private Button				logOutButton;

	public GraphLayout(final GraphMediator mediator) {
		this.mediator = mediator;
		setDefaultComponentAlignment(Alignment.TOP_CENTER);
	}

	public void init() {
		initRange();
		initDateRangeLayout();
		initLogOutButton();
		initLabel();
		initChart();
	}

	private void initLogOutButton() {
		logOutButton = new Button("Logout");
		logOutButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				loginDelegate.logOut();
			}
		});
		addComponent(logOutButton);
	}

	private void initRange() {
		Calendar today = Calendar.getInstance();
		toDate = today;
		fromDate = CalendarCalculator.getDateAtSpecifiedMonthAfter(today, -1);
	}

	private void initDateRangeLayout() {
		dateRangeLayout = new DateRangeLayout(mediator.getEarliestDate());
		dateRangeLayout.init();
		dateRangeLayout.setRange(fromDate, toDate);
		dateRangeLayout.setDelegate(this);
		addComponent(dateRangeLayout);
	}

	private void initChart() {
		image = new Image();
		image.setSource(generateStreamResource());
		addComponent(image);
	}

	private StreamResource generateStreamResource() {

		chart.setLineChartBuilder(new LineChartBuilderImpl());
		BigDecimal baseLine = configService.getBaseLine();
		List<BalanceVO> balances = mediator.getBalances(getFromDate(),
				getToDate());
		BufferedImage bufferedImage = chart
				.getBufferedImageFromBalancesAndBaseLine(balances, baseLine);
		return chart.getStreamSourceFromBufferedImage(bufferedImage);
	}

	private void initLabel() {
		label = new Label();
		label.setValue("Graph for ");
		for (String organization : configService.getOrganizations())
			label.setValue(label.getValue() + organization + "; ");
		addComponent(label);
	}

	@Override
	public void setDateRange(final Calendar fromDate, final Calendar toDate) {
		this.fromDate = fromDate;
		this.toDate = toDate;
		refreshChart();
	}

	private void refreshChart() {
		int index = getComponentIndex(image);
		removeComponent(image);
		image = new Image();
		image.setSource(generateStreamResource());
		addComponent(image, index);
	}

	public void destroy() {
		dateRangeLayout.destroy();
		removeAllComponents();
		image = null;
		label = null;
		configService = null;
		chart = null;
		dateRangeLayout = null;
	}

	public Calendar getFromDate() {
		return fromDate;
	}

	public Calendar getToDate() {
		return toDate;
	}

	public Label getLabel() {
		return label;
	}

	public Image getImage() {
		return image;
	}

	public void setChart(final FlowChart chart) {
		this.chart = chart;
	}

	public FlowChart getChart() {
		return chart;
	}

	public DateRangeLayout getDateRangeLayout() {
		return dateRangeLayout;
	}

	public void setConfigService(final ConfigService configService) {
		this.configService = configService;
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public Button getLogOutButton() {
		return logOutButton;
	}

	public void setLoginDelegate(LoginDelegate loginDelegate) {
		this.loginDelegate = loginDelegate;
	}

	public void setDelegate(LoginDelegate loginDelegate) {
		this.loginDelegate = loginDelegate;
	}
}
