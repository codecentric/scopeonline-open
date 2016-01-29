package de.codecentric.scopeonline.rpc;


import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;
import de.codecentric.scopeonline.service.ConfigService;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.util.*;

public class SOAPAdapter {
	private final Logger log = LoggerFactory.getLogger( SOAPAdapter.class );

	private SOAPRequest soapRequest;

	private ConfigService configService;

	private RecordParser  recordParser  = new RecordParser();
	private BalanceParser balanceParser = new BalanceParser();


	public SOAPAdapter( ConfigService configService ) {
		this.configService = configService;
	}

	public SOAPMessage getSoapResponseWithRecordsBetweenTwoDates( String startDate, String endDate ) {
		SOAPMessage message = null;
		try {
			soapRequest.setTimeFrame( startDate, endDate );
			message = soapRequest.sendAndReceive( "https://appload.scopevisio.com/api/soap/accounting/Journal.read" );
		} catch( SOAPException|IOException e ) {
			log.error( "Error while getting records.", e );
		}
		return message;
	}


	public SOAPMessage getSoapResponseWithBalancesAtSpecificDate( final String date ) {
		SOAPMessage message = null;
		try {
			soapRequest.setSumAndBalanceRequestArgs( date );
			message = soapRequest.sendAndReceive( "http://appload.scopevisio.com/api/soap/accounting/Susa.read" );
		} catch( SOAPException|IOException e ) {
			log.error( "Error while getting account balances.", e );
		}
		return message;
	}

	public boolean areUserCredentialsValid() {
		try {
			soapRequest.sendAndReceive( "https://appload.scopevisio.com/webservices/system.Ping" );
			return true;
		} catch( SOAPException|IOException e ) {
			return false;
		}
	}

	public List<RecordVO> getAllRecordsFromSoapResponse( SOAPMessage soapResponse, final String organization ) {
		List<RecordVO> recordVOList = new ArrayList<>();
		try {
			NodeList recordElements = soapResponse.getSOAPBody().getElementsByTagName( "record" );
			recordVOList = recordParser.parseRecords( recordElements, configService.getValidAccounts(organization) );
			Collections.sort( recordVOList );
		} catch( SOAPException e ) {
			log.error( "Error while parsing records.", e );
		}
		return recordVOList;
	}

	public List<BalanceVO> getAllBalancesFromSusaResponse( final SOAPMessage susaResponse,
														   final Calendar date, final String organization ) {
		List<BalanceVO> balanceVOList = new ArrayList<>();
		try {
			Node csvNode = susaResponse.getSOAPBody().getElementsByTagName( "susa" ).item( 0 );
			Set<String> accounts = configService.getValidAccounts( organization );
			List<BalanceVO> balances = balanceParser.parseAllBalancesForAccounts( csvNode,
																				  accounts,
																				  date );
			balanceVOList.addAll( balances );
		} catch( SOAPException e ) {
			log.error( "Error while parsing balances.", e );
		}

		return balanceVOList;
	}

	public void setSoapRequest( final SOAPRequest soapRequest ) {
		this.soapRequest = soapRequest;
	}

	public ConfigService getConfigService() {
		return configService;
	}


}

