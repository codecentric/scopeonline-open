package de.codecentric.scopeonline.rpc;

import javax.xml.soap.*;

class MockRecordMessage {
	private final SOAPBody body;

	MockRecordMessage( final String account ) throws SOAPException {
		body = createRecordListWithAccount( account );
	}

	private SOAPBody createRecordListWithAccount( final String account ) throws SOAPException {
		SOAPMessage message = MessageFactory.newInstance().createMessage();
		SOAPBody body = message.getSOAPBody();
		SOAPElement record = body.addChildElement( "record" );
		SOAPElement amount = record.addChildElement( "amount" );
		amount.setTextContent( "10.000,00" );
		SOAPElement date = record.addChildElement( "postingDate" );
		date.setTextContent( "01.01.1970" );
		SOAPElement accountNumber = record.addChildElement( "accountNumber" );
		accountNumber.setTextContent( account );
		return body;
	}

	public SOAPBody getBody() {
		return body;
	}
}
