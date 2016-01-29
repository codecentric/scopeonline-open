package de.codecentric.scopeonline.rpc;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import de.codecentric.scopeonline.data.RecordVO;
import de.codecentric.scopeonline.util.CalendarCalculator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class RecordNode {
	private final Logger log = LoggerFactory.getLogger( RecordNode.class );

	private BigDecimal amount;
	private Date       date;
	private String account;
	private final SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy" );

	public RecordNode( final BigDecimal amount, final Date date, final String account ) {
		this.amount = amount;
		this.date = date;
		this.account = account;
	}

	public RecordNode( NodeList dataNodes ) {
		int i = -1;
		int nodeCount = dataNodes.getLength();
		while( ++i<nodeCount ) {
			Node node = dataNodes.item( i );
			try {
				parseNodeValue( node );
			} catch( ParseException e ) {
				log.error( "Error while parsing node:"+node, e );
			}
		}
	}

	private void parseNodeValue( Node dataNode ) throws ParseException {
		if( dataNode.getNodeName().equals( "amount" ) )
			amount = convertToBigDecimalAmount( dataNode.getTextContent() );
		else if( dataNode.getNodeName().equals( "postingDate" ) )
			date = convertToDate( dataNode.getTextContent() );
		else if( dataNode.getNodeName().equals( "accountNumber" ) )
			account = dataNode.getTextContent();

	}

	public BigDecimal convertToBigDecimalAmount( final String amountAsString ) {
		String amount = amountAsString;
		amount = amount.replace( ".", "" );
		amount = amount.replace( ",", "." );
		return new BigDecimal( Double.parseDouble( amount ) );
	}

	public Date convertToDate( final String dateAsString ) throws ParseException {
		return sdf.parse( dateAsString );
	}

	public RecordVO toRecord() {
		return new RecordVO( account, amount, CalendarCalculator.createWithDate( date ) );
	}
}
