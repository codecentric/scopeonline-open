package de.codecentric.scopeonline.service;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;
import de.codecentric.scopeonline.rpc.SOAPAdapter;

import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScopeVisioAccountServiceImpl implements AccountService {
	private final Logger log = LoggerFactory.getLogger( ScopeVisioAccountServiceImpl.class );

	private SOAPAdapter adapter;

	public ScopeVisioAccountServiceImpl( final SOAPAdapter adapter ) {
		this.adapter = adapter;
	}

	@Override
	public List<RecordVO> getAllRecords( final Calendar startDate, final Calendar endDate,
										 final String organization ) {
		SOAPMessage response = adapter.getSoapResponseWithRecordsBetweenTwoDates(
				dateAsString( startDate ),
				dateAsString( endDate ) );

		logResponse( response );
		return response != null
			   ? adapter.getAllRecordsFromSoapResponse( response, organization )
			   : new ArrayList<RecordVO>();
	}

	void logResponse( final SOAPMessage response ) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.writeTo( out );
			log.debug( "Received SOAP response:"+out.toString() );
		} catch( Exception e ) {
			log.error( "Error while serializing SOAP response.", e );
		}
	}

	@Override
	public List<BalanceVO> getBalancesForDate( final Calendar date, final String organization ) {
		SOAPMessage response = adapter.getSoapResponseWithBalancesAtSpecificDate( dateAsString( date ) );
		logResponse( response );
		return response != null
			   ? adapter.getAllBalancesFromSusaResponse( response, date, organization )
			   : new ArrayList<BalanceVO>();
	}

	private String dateAsString( Calendar date ) {
		SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy" );
		return ( sdf.format( date.getTime() ) );
	}

}
