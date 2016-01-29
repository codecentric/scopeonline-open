package de.codecentric.scopeonline.charts;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LineChartBuilderTest {

	LineChartBuilder lineChartBuilder;

	@Mock
	Chart chart;

	@InjectMocks
	LineChartBuilderImpl lineChartBuilderImpl;

	@Before
	public void setUp() throws Exception {
		initMocks( this );
		lineChartBuilder = lineChartBuilderImpl;
	}

	@Test
	public void givenNewChart_whenXandYValuesAdded_shouldAddLine() throws Exception {
		Collection<BigDecimal> yValues = new ArrayList<>();
		yValues.add( new BigDecimal( 1 ) );
		Collection<Double> xValues = new ArrayList<>();
		xValues.add( 10.0 );
		Chart concreteChart = new Chart( 800, 600 );
		Series series = concreteChart.addSeries( "name", xValues, yValues );
		when( chart.addSeries( "name", xValues, yValues ) ).thenReturn( series );
		lineChartBuilder.addLineToChart( yValues, xValues, "name" );
		verify( chart ).addSeries( "name", xValues, yValues );
	}


	@Test
	public void givenNewChart_whenBuildIsExecuted_shouldBuildChart() throws Exception {
		Chart concreteChart = new Chart( 600, 500 );
		LineChartBuilderImpl concreteBuilderImpl = new LineChartBuilderImpl();
		concreteBuilderImpl.setChart( concreteChart );
		lineChartBuilder = concreteBuilderImpl;

		Collection<BigDecimal> values = new ArrayList<>();

		String nameOfLine = "nameOfLine";
		SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy" );
		List<Date> dates = new ArrayList<>();
		for( int i = 10; i<130; i++ ) {
			dates.add( sdf.parse( i+".01.2014" ) );
			values.add( BigDecimal.valueOf( i ) );
		}


		lineChartBuilder.addLineToChart( values, dates, nameOfLine );
		BufferedImage image = lineChartBuilder.buildChart();


		String lineChartString = "type = 1 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff "+
								 "amask=0 IntegerInterleavedRaster: width = 600 height = 500 #Bands = 3 xOff = 0 "+
								 "yOff = 0 dataOffset[0] 0";

		assertTrue( image.toString().contains( lineChartString ) );
	}

	@After
	public void tearDown() throws Exception {
		lineChartBuilder = null;
	}
}
