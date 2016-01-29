package de.codecentric.scopeonline.rpc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.codecentric.scopeonline.service.ConfigService;
import de.codecentric.scopeonline.service.ConfigServiceImpl;
import de.codecentric.scopeonline.util.TestConfigService;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SOAPRequestTest {
	TestConfigService testConfigService = TestConfigService.getTestConfigService();
	

	private SOAPRequestImpl   request;
	@Mock
	private ConnectionFactory connectionFactory;
	@Mock
	private URLConnection     connection;
	@Mock
	private MessageFactory    messageFactory;

	@Before
	public void setUp() throws Exception {
		request = new SOAPRequestImpl( testConfigService.getUser(), testConfigService.getPass(), 
				testConfigService.getOrganization(), testConfigService.getCustomer() );
		initMocks( this );
	}

	private SOAPElement getFirstChildOfSoapMessage() throws SOAPException {
		return (SOAPElement) request.getMessage().getSOAPBody().getChildElements().next();
	}

	private Node getAuthNode() throws SOAPException {
		SOAPElement req = getFirstChildOfSoapMessage();
		return req.getFirstChild();
	}

	private Node getArgsNode() throws SOAPException {
		SOAPElement req = getFirstChildOfSoapMessage();
		return req.getChildNodes().item( 1 );
	}

	private void assertNodeIsValid( final Node node, final String name, final String value ) {
		assertEquals( name, node.getNodeName() );
		assertEquals( value, node.getTextContent() );
	}

	@Test
	public void givenANewRequest_theUnderlyingSoapMessageShouldHaveARequestElement() throws Exception {
		assertNotNull( getFirstChildOfSoapMessage() );
	}

	@Test
	public void givenANewRequest_shouldCatchSoapExceptionsDuringInstantiation() throws Exception {
		when( messageFactory.createMessage() ).thenThrow( new SOAPException( "error" ) );
		assertNotNull( new SOAPRequestImpl( null, null, null, messageFactory ) );
	}

	@Test
	public void givenANewRequest_theRequestElementShouldHaveAnAuthenticationElement() throws Exception {
		Node auth = getAuthNode();
		assertEquals( "authn", auth.getNodeName() );
	}

	@Test
	public void givenANewRequest_authenticationInformationShouldBePresentInXmlRequest() throws Exception {
		NodeList auth = getAuthNode().getChildNodes();
		assertNodeIsValid( auth.item( 0 ), "customer", testConfigService.getCustomer() );
		assertNodeIsValid( auth.item( 1 ), "user", testConfigService.getUser() );
		assertNodeIsValid( auth.item( 2 ), "pass", testConfigService.getPass() );
		assertNodeIsValid( auth.item( 3 ), "language", SOAPRequest.LANGUAGE );
		assertNodeIsValid( auth.item( 4 ), "organisation", testConfigService.getOrganization() );
	}

	@Test
	public void givenANewRequest_whenSettingTimeFrame_timeFrameShouldBePresentInXmlRequest() throws Exception {
		String startDate = "01.01.2014";
		String endDate = "02.01.2014";

		request.setTimeFrame( startDate, endDate );

		NodeList timeFrame = getArgsNode().getChildNodes();
		assertNodeIsValid( timeFrame.item( 0 ), "startDate", startDate );
		assertNodeIsValid( timeFrame.item( 1 ), "endDate", endDate );
	}

	@Test
	public void givenANewRequest_whenSettingTimeFrameTwice_olderTimeFrameShouldBeReplaced() throws Exception {
		request.setTimeFrame( "01.01.2013", "01.02.2013" );

		String startDate = "01.01.2014";
		String endDate = "02.01.2014";
		request.setTimeFrame( startDate, endDate );

		NodeList args = getArgsNode().getChildNodes();
		assertNodeIsValid( args.item( 0 ), "startDate", startDate );
		assertNodeIsValid( args.item( 1 ), "endDate", endDate );
	}

	@Test
	public void givenANewRequest_whenSettingSumAndBalanceRequestDate_shouldContainArgs() throws Exception {
		String date = "01.01.2014";
		request.setSumAndBalanceRequestArgs( date );

		NodeList args = getArgsNode().getChildNodes();
		assertNodeIsValid( args.item( 0 ), "outputFormat", "csv" );
		assertNodeIsValid( args.item( 1 ), "includeHeader", "false" );
		assertNodeIsValid( args.item( 2 ), "includeZeroValued", "true" );
		assertNodeIsValid( args.item( 3 ), "startDate", date );
		assertNodeIsValid( args.item( 4 ), "endDate", date );
	}

	@Test
	public void givenANewRequest_whenSettingSumAndBalanceRequestDateTwice_ShouldContainOnlyLastDataset()
			throws Exception {
		String date1 = "01.01.2014";
		String date2 = "01.02.2014";

		request.setSumAndBalanceRequestArgs( date1 );
		request.setSumAndBalanceRequestArgs( date2 );
		NodeList args = getArgsNode().getChildNodes();
		assertNodeIsValid( args.item( 0 ), "outputFormat", "csv" );
		assertNodeIsValid( args.item( 1 ), "includeHeader", "false" );
		assertNodeIsValid( args.item( 2 ), "includeZeroValued", "true" );
		assertNodeIsValid( args.item( 3 ), "startDate", date2 );
		assertNodeIsValid( args.item( 4 ), "endDate", date2 );
	}

	@Test
	public void givenANewRequest_whenSettingRequestTimeFrameMultipleTimes_shouldAlwaysBuildCorrectHeader() throws
																										   Exception {
		String date1 = "01.12.2013";
		String date2 = "31.12.2013";

		request.setTimeFrame( date1, date2 );
		NodeList args = getArgsNode().getChildNodes();
		assertNodeIsValid( args.item( 0 ), "startDate", date1 );
		assertNodeIsValid( args.item( 1 ), "endDate", date2 );


		request.setSumAndBalanceRequestArgs( date1 );
		args = getArgsNode().getChildNodes();
		assertNodeIsValid( args.item( 0 ), "outputFormat", "csv" );
		assertNodeIsValid( args.item( 1 ), "includeHeader", "false" );
		assertNodeIsValid( args.item( 2 ), "includeZeroValued", "true" );
		assertNodeIsValid( args.item( 3 ), "startDate", date1 );
		assertNodeIsValid( args.item( 4 ), "endDate", date1 );


		request.setTimeFrame( date1, date2 );
		args = getArgsNode().getChildNodes();
		assertNodeIsValid( args.item( 0 ), "startDate", date1 );
		assertNodeIsValid( args.item( 1 ), "endDate", date2 );
	}

	@Test
	public void givenANewRequest_whenSendingAndReceiving_shouldWriteToOutputStream() throws Exception {
		mockConnection();
		ByteArrayOutputStream outputStream = mockOutputStream();
		ByteArrayInputStream inputStream = mockInputStream();
		SOAPMessage message = mockMessage( inputStream );

		request = new SOAPRequestImpl( testConfigService.getUser(), testConfigService.getPass(), connectionFactory, messageFactory );
		request.setTimeFrame( "01.01.2014", "02.01.2014" );

		assertEquals( message, request.sendAndReceive( "localhost" ) );
		verifySoapCalls( inputStream );
	}

	private void verifySoapCalls( final ByteArrayInputStream inputStream ) throws IOException, SOAPException {
		verify( connectionFactory ).buildConnection( "localhost" );
		verify( connection ).getOutputStream();
		verify( connection ).getInputStream();
		verify( messageFactory ).createMessage();
		verify( messageFactory ).createMessage( null, inputStream );
	}

	private void mockConnection() throws IOException {
		when( connectionFactory.buildConnection( "localhost" ) ).thenReturn( connection );
	}

	private SOAPMessage mockMessage( final ByteArrayInputStream inputStream ) throws SOAPException, IOException {
		SOAPMessage message = MessageFactory.newInstance().createMessage();
		when( messageFactory.createMessage() ).thenReturn( message );
		when( messageFactory.createMessage( null, inputStream ) ).thenReturn( message );
		return message;
	}

	private ByteArrayInputStream mockInputStream() throws IOException {
		byte[] arr = "validResponse".getBytes();
		ByteArrayInputStream inputStream = new ByteArrayInputStream( arr );
		when( connection.getInputStream() ).thenReturn( inputStream );
		return inputStream;
	}

	private ByteArrayOutputStream mockOutputStream() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when( connection.getOutputStream() ).thenReturn( outputStream );
		return outputStream;
	}

	@After
	public void tearDown() throws Exception {
		request = null;
		connection = null;
		connectionFactory = null;
		messageFactory = null;
	}
} 