package de.codecentric.scopeonline.ui.chart;

import static de.codecentric.scopeonline.ui.util.DateAssert.assertWithinSameSecond;
import static de.codecentric.scopeonline.ui.util.VaadinAssert.assertComponentIsPresent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

import de.codecentric.scopeonline.charts.FlowChart;
import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.domain.Accumulator;
import de.codecentric.scopeonline.service.ConfigService;
import de.codecentric.scopeonline.util.CalendarCalculator;

public class GraphLayoutTest {

	private GraphLayout		layout;

	@Mock
	private ConfigService	configService;
	@Mock
	private FlowChart		flowChart;
	@Mock
	private Accumulator		accumulator;
	@Mock
	private GraphMediator	mediator;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		initMockMethods();
		initLayout();
		initMockingConditions();
		layout.init();
	}

	private void initMockingConditions() {
		when(configService.getBaseLine()).thenReturn(new BigDecimal("1000000"));
		BufferedImage bufferedImage = new BufferedImage(500, 500,
				BufferedImage.TYPE_INT_RGB);
		when(
				flowChart.getBufferedImageFromBalancesAndBaseLine(
						anyListOf(BalanceVO.class), any(BigDecimal.class)))
				.thenReturn(bufferedImage);
		List<String> organizations = new ArrayList<>();
		organizations.add("Scopevisio Demo AG");
		when(configService.getOrganizations()).thenReturn(organizations);
	}

	private void initMockMethods() {
		mockBaselineService();
		mockChartCreation(mockMonthBalances());
	}

	private void mockBaselineService() {
		when(configService.getBaseLine()).thenReturn(new BigDecimal("1.0"));

	}

	private ArrayList<BalanceVO> mockMonthBalances() {
		ArrayList<BalanceVO> monthBalances = new ArrayList<>();
		when(mediator.getBalances(any(Calendar.class), any(Calendar.class)))
				.thenReturn(monthBalances);
		return monthBalances;
	}

	private void mockChartCreation(final ArrayList<BalanceVO> monthBalances) {
		BufferedImage image = new BufferedImage(200, 200,
				BufferedImage.TYPE_INT_RGB);
		when(
				flowChart.getBufferedImageFromBalancesAndBaseLine(
						monthBalances, configService.getBaseLine()))
				.thenReturn(image);
	}

	private void initLayout() {
		layout = new GraphLayout(mediator);
		layout.setChart(flowChart);
		layout.setConfigService(configService);
	}

	@Test
	public void givenFreshGraphLayout_shouldContainLabel() throws Exception {
		Label label = layout.getLabel();
		assertComponentIsPresent(layout, label);
		assertEquals("Graph for Scopevisio Demo AG; ", label.getValue());
	}

	@Test
	public void givenFreshGraphLayout_shouldCreateFlowChartImage()
			throws Exception {
		Image image = layout.getImage();
		assertComponentIsPresent(layout, image);
	}

	@Test
	public void givenFreshGraphLayout_shouldSetDateRangeToTheLastMonth()
			throws Exception {
		Calendar today = Calendar.getInstance();
		assertWithinSameSecond(today, layout.getToDate());
		Calendar oneMonthAgo = CalendarCalculator.getDateAtSpecifiedMonthAfter(
				today, -1);
		assertWithinSameSecond(oneMonthAgo, layout.getFromDate());
	}

	@Test
	public void givenFreshGraphLayout_shouldCreateDateRangeLayout()
			throws Exception {
		DateRangeLayout dateRange = layout.getDateRangeLayout();
		assertComponentIsPresent(layout, dateRange);
	}

	@Test
	public void givenFreshGraphLayout_shouldCreateLogoutButton()
			throws Exception {
		Button button = layout.getLogOutButton();
		assertComponentIsPresent(layout, button);
	}

	@Test
	public void givenFreshGraphLayout_whenSubmittingInputFromDateRangeLayout_shouldRefreshDateRangeAndChart()
			throws Exception {
		ArrayList<BalanceVO> monthBalances = mockMonthBalances();
		mockChartCreation(monthBalances);

		Calendar today = Calendar.getInstance();
		Calendar tomorrow = CalendarCalculator.getSpecifiedDayAfter(today, 1);
		layout.setDateRange(today, tomorrow);

		verify(flowChart, times(2)).getBufferedImageFromBalancesAndBaseLine(
				monthBalances, configService.getBaseLine());
	}

	@Test
	public void givenFreshGraphLayout_whenDestroyed_shouldRemoveAllComponents()
			throws Exception {
		layout.destroy();
		assertNull(layout.getImage());
		assertNull(layout.getLabel());
		assertEquals(0, layout.getComponentCount());
	}

	@Test
	public void givenFreshGraphLayout_whenDestroyed_shouldClearDependencies()
			throws Exception {
		layout.destroy();
		assertNull(layout.getConfigService());
		assertNull(layout.getChart());
		assertNull(layout.getImage());
		assertNull(layout.getLabel());
		assertNull(layout.getDateRangeLayout());
	}

	@After
	public void tearDown() throws Exception {
		layout = null;
		flowChart = null;
		accumulator = null;
		configService = null;
	}
}