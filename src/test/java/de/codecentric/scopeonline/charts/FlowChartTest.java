package de.codecentric.scopeonline.charts;

import de.codecentric.scopeonline.data.BalanceVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FlowChartTest {
	private static final String ERROR = "Testing error handling. Please ignore this.";
	@Mock
	LineChartBuilder lineChartBuilder;


	FlowChart flowChart;

	private List<BalanceVO> getFilledBalanceVOListWith20Values() {
		List<BalanceVO> balanceVOList = new ArrayList<>();
		for( int i = 0; i<20; i++ ) {
			BigDecimal bigDecimal = BigDecimal.valueOf( i );
			Calendar date = Calendar.getInstance();
			date.set( 2014, Calendar.JANUARY, i );
			balanceVOList.add( new BalanceVO( bigDecimal, date ) );
		}
		return balanceVOList;
	}

	@Before
	public void setUp() throws Exception {
		initMocks( this );
		flowChart = new FlowChart( lineChartBuilder );
	}

	@Test
	public void givenListOfBalanceVOs_whenAskedForDateCollection_shouldReturnCollectionWithDates() throws
																								   Exception {
		List<BalanceVO> balanceVOList = getFilledBalanceVOListWith20Values();
		Collection<Date> dateCollection = flowChart.getDateCollectionFromBalanceVOList( balanceVOList );
		assertEquals( 20, dateCollection.size() );
	}

	@Test
	public void givenListOfBalanceVOs_whenAskedForBigDecimalCollection_shouldReturnCollectionWithValues() throws
																										  Exception {
		List<BalanceVO> balanceVOList = getFilledBalanceVOListWith20Values();
		Collection<BigDecimal> doubleCollection = flowChart.getBigDecimalCollectionFromBalanveVOList( balanceVOList );
		assertEquals( 20, doubleCollection.size() );
	}

	@Test
	public void
	givenBaseLineValueAndNumberOfBalanceValues_whenAskedForBaseLineCollection_shouldReturnBaseLineCollection()
			throws Exception {
		BigDecimal baseLineValue = BigDecimal.valueOf( 10.0 );
		int sizeOfBalanceVOs = 10;
		Collection<BigDecimal> baseLine = flowChart.getBaseLineCollection( baseLineValue, sizeOfBalanceVOs );
		assertEquals( 10, baseLine.size() );
		for( BigDecimal value : baseLine )
			assertEquals( BigDecimal.valueOf( 10.0 ), value );
	}

	@Test
	public void givenNewFlowChart_whenPassedBalanceVOList_shouldGenerateBufferedImage() throws Exception {
		List<BalanceVO> balanceVOList = getFilledBalanceVOListWith20Values();
		flowChart.getBufferedImageFromBalancesAndBaseLine( balanceVOList, BigDecimal.valueOf( 10.0 ) );

		verify( lineChartBuilder ).addLineToChart( anyCollectionOf( BigDecimal.class ), anyCollectionOf( Date.class ),
												   eq( "Baseline" ) );
		verify( lineChartBuilder ).addLineToChart( anyCollectionOf( BigDecimal.class ), anyCollectionOf( Date.class ),
												   eq( "CashFlow" ) );
		verify( lineChartBuilder ).buildChart();
	}

	@Test
	public void givenNewFlowChart_whenPassedBalanceVOList_shouldCatchErrorsWhileCreatingImage() throws Exception {
		List<BalanceVO> balanceVOList = getFilledBalanceVOListWith20Values();
		when( lineChartBuilder.buildChart() ).thenThrow( new IOException( ERROR ) );

		flowChart.getBufferedImageFromBalancesAndBaseLine( balanceVOList, BigDecimal.valueOf( 10.0 ) );

		verify( lineChartBuilder ).addLineToChart( anyCollectionOf( BigDecimal.class ), anyCollectionOf( Date.class ),
												   eq( "Baseline" ) );
		verify( lineChartBuilder ).addLineToChart( anyCollectionOf( BigDecimal.class ), anyCollectionOf( Date.class ),
												   eq( "CashFlow" ) );
	}

	@Test
	public void shouldWrapBufferImageIntoStreamResource() throws Exception {
		assertNotNull( flowChart.getStreamSourceFromBufferedImage( new BufferedImage( 10,10,
																					  BufferedImage.TYPE_INT_RGB) ) );
	}

	@Test
	public void shouldSetLineChartBuilder() throws Exception {

		LineChartBuilder builder = new LineChartBuilderImpl();
		flowChart.setLineChartBuilder( builder );
		assertEquals( builder, flowChart.getLineChartBuilder() );

	}

	@After
	public void tearDown() throws Exception {
		flowChart = null;
	}
}
