package de.codecentric.scopeonline.rpc;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;

public class SOAPRequestImpl implements SOAPRequest {
	private final Logger log = LoggerFactory.getLogger( SOAPRequestImpl.class );

	private SOAPBody          soapBody;
	private SOAPMessage       message;
	private SOAPElement       requestElement;
	private String            user;
	private String            pass;
	private String            organization;
	private String            customer;
	private MessageFactory    messageFactory;
	private ConnectionFactory connectionFactory;

	public SOAPRequestImpl( final String user, final String pass, final String organization, final String customer ) {
		this.user = user;
		this.pass = pass;
		this.organization = organization;
		this.customer = customer;
		this.connectionFactory = new ConnectionFactory();
		init();
	}

	public SOAPRequestImpl( final String user, final String pass, final ConnectionFactory connectionFactory,
							MessageFactory messageFactory ) {
		this.user = user;
		this.pass = pass;
		this.connectionFactory = connectionFactory;
		this.messageFactory = messageFactory;

		init();
	}

	private void init() {
		try {
			createRequest();
			applyAuthentication();
		} catch( SOAPException e ) {
			log.error( "Could not build SOAP message.", e );
		}
	}

	private void createRequest() throws SOAPException {
		if( messageFactory == null )
			messageFactory = MessageFactory.newInstance();
		message = messageFactory.createMessage();
		soapBody = message.getSOAPBody();
		requestElement = soapBody.addChildElement( SOAPRequest.REQ, SOAPRequest.NS1, SOAPRequest.URL );
	}

	private void applyAuthentication() throws SOAPException {
		new SOAPAuthenticationElementBuilder(
				user,
				pass,
				organization,
				customer,
				SOAPRequest.LANGUAGE ).build( requestElement );
	}

	@Override
	public void setTimeFrame( final String startDate, final String endDate ) throws SOAPException {
		removeExistingArgs();
		new SOAPTimeFrameElementBuilder( startDate, endDate ).build(
				(SOAPElement) soapBody.getChildElements().next() );
	}

	private void removeExistingArgs() {
		NodeList nodes = soapBody.getElementsByTagName( "args" );
		if( nodes != null && nodes.getLength()>0 ) {
			SOAPElement argsElement = (SOAPElement) nodes.item( 0 );
			argsElement.getParentNode().removeChild( argsElement );
		}
	}

	@Override
	public void setSumAndBalanceRequestArgs( final String date ) throws SOAPException {
		removeExistingArgs();
		new SOAPSumAndBalanceRequestBuilder( date ).build(
				(SOAPElement) soapBody.getChildElements().next() );
	}

	@Override
	public SOAPMessage sendAndReceive( final String url ) throws SOAPException, IOException {
		message.saveChanges();
		URLConnection conn = connectionFactory.buildConnection( url );
		logRequest();
		message.writeTo( conn.getOutputStream() );
		return messageFactory.createMessage( null, conn.getInputStream() );
	}

	private void logRequest() throws SOAPException, IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		message.writeTo( outputStream );
		log.debug( "Sent SOAP request:\n"+outputStream.toString() );
	}

	SOAPMessage getMessage() {
		return message;
	}


	private class SOAPSumAndBalanceRequestBuilder {

		public String date;

		public SOAPElement build( SOAPElement requestElement ) throws SOAPException {
			SOAPElement configElement = requestElement.addChildElement( "args" );
			configElement.addChildElement( "outputFormat" ).setTextContent( "csv" );
			configElement.addChildElement( "includeHeader" ).setTextContent( "false" );
			configElement.addChildElement( "includeZeroValued" ).setTextContent( "true" );
			configElement.addChildElement( "startDate" ).setTextContent( date );
			configElement.addChildElement( "endDate" ).setTextContent( date );
			return requestElement;
		}

		public SOAPSumAndBalanceRequestBuilder( final String date ) {
			this.date = date;
		}
	}
}

class SOAPTimeFrameElementBuilder {
	public String startDate;
	public String endDate;

	public SOAPElement build( SOAPElement requestElement ) throws SOAPException {
		SOAPElement configElement = requestElement.addChildElement( "args" );
		configElement.addChildElement( "startDate" ).setTextContent( startDate );
		configElement.addChildElement( "endDate" ).setTextContent( endDate );
		return requestElement;
	}

	SOAPTimeFrameElementBuilder( final String startDate, final String endDate ) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
}

class SOAPAuthenticationElementBuilder {
	public String user;
	public String pass;
	public String organisation;
	public String customer;
	public String language;

	public SOAPElement build( SOAPElement requestElement ) throws SOAPException {
		SOAPElement element = requestElement.addChildElement( "authn" );
		element.addChildElement( "customer" ).setTextContent( customer );
		element.addChildElement( "user" ).setTextContent( user );
		element.addChildElement( "pass" ).setTextContent( pass );
		element.addChildElement( "language" ).setTextContent( language );
		element.addChildElement( "organisation" ).setTextContent( organisation );
		return element;
	}

	SOAPAuthenticationElementBuilder( final String user, final String pass,
									  final String organisation,
									  final String customer,
									  final String language ) {
		this.user = user;
		this.pass = pass;
		this.organisation = organisation;
		this.customer = customer;
		this.language = language;
	}
}