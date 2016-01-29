package de.codecentric.scopeonline.charts;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import de.codecentric.scopeonline.data.BalanceVO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class FlowChart {

	private LineChartBuilder lineChartBuilder;
	private final Logger log = LoggerFactory.getLogger( FlowChart.class );

	public FlowChart( LineChartBuilder lineChartBuilder ) {
		this.lineChartBuilder = lineChartBuilder;
	}

	public Collection<Date> getDateCollectionFromBalanceVOList( final List<BalanceVO> balanceVOList ) {
		Collection<Date> dateCollection = new ArrayList<>();
		for( BalanceVO balanceVO : balanceVOList ){
			if (balanceVO != null){
				dateCollection.add( balanceVO.date.getTime() );
			}
		}
		return dateCollection;
	}


	public Collection<BigDecimal> getBigDecimalCollectionFromBalanveVOList( final List<BalanceVO> balanceVOList ) {
		Collection<BigDecimal> bigDecimalCollection = new ArrayList<>();
		for( BalanceVO balanceVO : balanceVOList ){
			if (balanceVO != null){
				bigDecimalCollection.add( balanceVO.amount );
			}
		}
			
		return bigDecimalCollection;
	}


	public Collection<BigDecimal> getBaseLineCollection( final BigDecimal baseLineValue, final int sizeOfBalanceVOs ) {
		Collection<BigDecimal> baseLine = new ArrayList<>();
		for( int i = 0; i<sizeOfBalanceVOs; i++ )
			baseLine.add( baseLineValue );
		return baseLine;
	}

	public BufferedImage getBufferedImageFromBalancesAndBaseLine( final List<BalanceVO> balanceVOList,
																  final BigDecimal baseLineValue ) {
		if( balanceVOList.size() == 0 )
			balanceVOList.add( new BalanceVO( BigDecimal.ONE, Calendar.getInstance() ) );

		Collection<BigDecimal> amounts = getBigDecimalCollectionFromBalanveVOList( balanceVOList );
		Collection<Date> dates = getDateCollectionFromBalanceVOList( balanceVOList );
		Collection<BigDecimal> baseLine = getBaseLineCollection( baseLineValue, dates.size() );

		lineChartBuilder.addLineToChart( baseLine, dates, "Baseline" );
		lineChartBuilder.addLineToChart( amounts, dates, "CashFlow" );
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = lineChartBuilder.buildChart();
		} catch( IOException e ) {
			log.error( "Error while building chart.", e );
		}
		return bufferedImage;
	}


	public StreamResource getStreamSourceFromBufferedImage( BufferedImage bufferedImage ) {
		StreamSource streamSource = new FlowchartStreamSource( bufferedImage );
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmss" );
		return new StreamResource( streamSource, "flowChart"+sdf.format( calendar.getTime() ) );
	}

	public void setLineChartBuilder( LineChartBuilder lineChartBuilder ) {
		this.lineChartBuilder = lineChartBuilder;
	}

	public LineChartBuilder getLineChartBuilder() {
		return lineChartBuilder;
	}
}
