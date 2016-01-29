package de.codecentric.scopeonline.charts;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

public interface LineChartBuilder {
	void addLineToChart( Collection<BigDecimal> yValues, Collection xValues, String name );

	BufferedImage buildChart() throws IOException;


}
