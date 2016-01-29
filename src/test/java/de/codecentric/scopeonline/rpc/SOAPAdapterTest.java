package de.codecentric.scopeonline.rpc;

import de.codecentric.scopeonline.data.RecordVO;
import de.codecentric.scopeonline.service.ConfigService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class SOAPAdapterTest {

	static final        String START_DATE = "01.01.2010";
	static final        String END_DATE   = "31.12.2010";
	static final        String URL        = "https://appload.scopevisio.com/api/soap/accounting/Journal.read";
	public static final String ERROR      = "Testing exception "+
											"handling. Please ignore "+
											"this.";

	@Mock
	private SOAPRequest request;

	@Mock
	private ConfigService configService;

	private SOAPAdapter soapAdapter;

	private SOAPMessage getJournalSOAPResonse() {
		return soapAdapter.getSoapResponseWithRecordsBetweenTwoDates( START_DATE, END_DATE );
	}

	@Before
	public void setUp() throws Exception {
		initMocks( this );
		soapAdapter = new SOAPAdapter( configService );
		soapAdapter.setSoapRequest( request );
	}

	@Test
	public void checkThatRequestGetsBuiltForValidCredentials() throws Exception {
		SOAPMessage mockMessage = MessageFactory.newInstance().createMessage();
		when( request.sendAndReceive( URL ) ).thenReturn( mockMessage );
		assertNotNull( getJournalSOAPResonse() );
		verify( request ).setTimeFrame( START_DATE, END_DATE );
	}

	@Test
	public void givenPingRequestThrowsASoapException_shouldNotAcceptCredentialsAsValid() throws Exception {
		SOAPRequest soapRequest = mock( SOAPRequest.class );
		when( soapRequest.sendAndReceive( any( String.class ) ) ).thenThrow( new SOAPException( ERROR ) );
		soapAdapter.setSoapRequest( soapRequest );
		assertFalse( soapAdapter.areUserCredentialsValid() );
	}

	@Test
	public void givenPingRequestThrowsAnIoException_shouldNotAcceptCredentialsAsValid() throws Exception {
		SOAPRequest soapRequest = mock( SOAPRequest.class );
		when( soapRequest.sendAndReceive( any( String.class ) ) ).thenThrow( new IOException( ERROR ) );
		soapAdapter.setSoapRequest( soapRequest );
		assertFalse( soapAdapter.areUserCredentialsValid() );
	}

	@Test
	public void givenPingRequestDoesNotThrowAnException_shouldAcceptCredentialsAsValid() throws Exception {
		SOAPRequest soapRequest = mock( SOAPRequest.class );
		when( soapRequest.sendAndReceive( any( String.class ) ) )
				.thenReturn( MessageFactory.newInstance().createMessage() );
		soapAdapter.setSoapRequest( soapRequest );
		assertTrue( soapAdapter.areUserCredentialsValid() );
	}

	@Test
	public void givenSumAndBalanceRequest_whenRequestThrowsAnError_shouldReturnNull() throws Exception {
		SOAPRequest soapRequest = mock( SOAPRequest.class );
		when( soapRequest.sendAndReceive( any( String.class ) ) ).thenThrow( new IOException( ERROR ) );
		soapAdapter.setSoapRequest( soapRequest );
		assertNull( soapAdapter.getSoapResponseWithBalancesAtSpecificDate( "01.01.2014" ) );
	}

	@Test
	public void givenSumAndBalanceRequest_whenRequestDoesNotThrowAnError_shouldReturnSoapMessage() throws
																								   Exception {
		SOAPRequest soapRequest = mock( SOAPRequest.class );
		SOAPMessage mockMessage = MessageFactory.newInstance().createMessage();
		when( soapRequest.sendAndReceive( any( String.class ) ) ).thenReturn( mockMessage );
		soapAdapter.setSoapRequest( soapRequest );
		assertEquals( mockMessage, soapAdapter.getSoapResponseWithBalancesAtSpecificDate( "01.01.2014" ) );
	}

	@Test
	public void givenAllRecordsRequest_whenRequestThrowsAnError_shouldReturnNull() throws Exception {
		SOAPRequest soapRequest = mock( SOAPRequest.class );
		when( soapRequest.sendAndReceive( any( String.class ) ) ).thenThrow( new IOException( ERROR ) );
		soapAdapter.setSoapRequest( soapRequest );
		assertNull( soapAdapter.getSoapResponseWithRecordsBetweenTwoDates( "01.01.2014", "02.01.2014" ) );
	}

	@Test
	public void givenAllRecordsRequest_whenRequestDoesNotThrowAnError_shouldReturnSoapMessage() throws
																								Exception {
		SOAPRequest soapRequest = mock( SOAPRequest.class );
		SOAPMessage mockMessage = MessageFactory.newInstance().createMessage();
		when( soapRequest.sendAndReceive( any( String.class ) ) ).thenReturn( mockMessage );
		soapAdapter.setSoapRequest( soapRequest );
		assertEquals( mockMessage,
					  soapAdapter.getSoapResponseWithRecordsBetweenTwoDates( "01.01.2014", "02.01.2014" ) );
	}

	@Test
	public void getAllRecordsVOFromSOAPResponse() throws Exception {
		SOAPMessage exampleResponse = SOAPAdapterTestData.getTestRecordMessage();
		String organization = "Scopevisio Demo AG";
		Set<String> validAccounts = new HashSet<>();
		validAccounts.add( "1200" );
		when( configService.getValidAccounts( organization ) ).thenReturn( validAccounts );
		List<RecordVO> recordVOList = soapAdapter.getAllRecordsFromSoapResponse( exampleResponse, organization );
		assertEquals( 8, recordVOList.size() );
	}


	@Ignore
	@After
	public void tearDown() throws Exception {
		soapAdapter = null;
		request = null;
	}
}
