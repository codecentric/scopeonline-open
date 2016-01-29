package de.codecentric.scopeonline.rpc;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;

public interface SOAPRequest {
	public static final String LANGUAGE = "de_DE";
	public static final String REQ      = "req";
	public static final String NS1      = "ns1";
	public static final String URL      = "http://www.scopevisio.com/";

	SOAPMessage sendAndReceive( final String url ) throws SOAPException, IOException;

	void setTimeFrame( String startDate, String endDate ) throws SOAPException;


	void setSumAndBalanceRequestArgs( String startDate ) throws SOAPException;

}
