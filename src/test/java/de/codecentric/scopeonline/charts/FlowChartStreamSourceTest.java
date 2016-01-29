package de.codecentric.scopeonline.charts;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class FlowChartStreamSourceTest {

	FlowchartStreamSource flowchartStreamSource;


	BufferedImage bufferedImage;

	@Before
	public void setUp() throws Exception {
		bufferedImage = new BufferedImage( 100, 100, BufferedImage.TYPE_INT_RGB );
		flowchartStreamSource = new FlowchartStreamSource( bufferedImage );
	}

	@Test
	public void givenNewFlowchartStreamSource_whenGetStreamCalled_shouldReturnStream() throws Exception {
		InputStream inputStream = flowchartStreamSource.getStream();
		assertNotNull( inputStream );
	}

	@After
	public void tearDown() throws Exception {
		flowchartStreamSource = null;
		bufferedImage = null;
	}
}
