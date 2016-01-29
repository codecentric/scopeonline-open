package de.codecentric.scopeonline.charts;

import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

public class LineChartBuilderImpl implements LineChartBuilder {

	private Chart chart = new Chart( 1280, 720 );


	@Override
	public void addLineToChart( final Collection<BigDecimal> yValues, final Collection xValues, final String name ) {
		Series series = chart.addSeries( name, xValues, yValues );
		series.setMarker( SeriesMarker.NONE );
	}


	@Override
	public BufferedImage buildChart() throws IOException {
		chart.getStyleManager().setDatePattern( "dd.MM.yy" );
		chart.getStyleManager().setXAxisTickMarkSpacingHint( 80 );
		chart.getStyleManager().setScientificDecimalPattern( "#,###,###" );

		return BitmapEncoder.getBufferedImage( chart );
	}


	public void setChart( final Chart chart ) {
		this.chart = chart;
	}


}
