package de.codecentric.scopeonline.service;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;
import de.codecentric.scopeonline.rpc.SOAPAdapter;
import de.codecentric.scopeonline.rpc.SOAPAdapterTestData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.xml.soap.SOAPMessage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AccountServiceTest {
	private AccountService service;

	@Mock
	private SOAPAdapter adapter;
	@Mock
	ConfigService configService;

	@Before
	public void setUp() throws Exception {
		initMocks( this );
		service = new ScopeVisioAccountServiceImpl( adapter );
	}

	@Test
	public void givenNewAccountService_whenNothingCached_RetrievesAllRecordsFromScopeVisio()
			throws Exception {
		Calendar start = Calendar.getInstance();
		start.set( 2014, Calendar.JANUARY, 1, 0, 0, 0 );
		String startDate = "01.01.2014";

		Calendar end = Calendar.getInstance();
		end.set( 2014, Calendar.FEBRUARY, 1, 0, 0, 0 );
		String endDate = "01.02.2014";

		String organization = "Scopevisio Demo AG";

		SOAPMessage mockResponse = SOAPAdapterTestData.getTestRecordMessage();
		when( adapter.getSoapResponseWithRecordsBetweenTwoDates( startDate, endDate ) ).thenReturn( mockResponse );

		List<RecordVO> records = new ArrayList<>();
		when( adapter.getAllRecordsFromSoapResponse( mockResponse, organization ) ).thenReturn( records );

		assertEquals( records, service.getAllRecords( start, end, organization ) );

		verify( adapter ).getSoapResponseWithRecordsBetweenTwoDates( startDate, endDate );
	}


	@Test
	public void
	givenNewAccountService_whenAskedForBalancesAtSpecificDate_ShouldReturnAccordingBalancesListFromScopeVisio()
			throws Exception {
		Calendar date = Calendar.getInstance();
		date.set( 2011, Calendar.JANUARY, 1, 0, 0, 0 );
		String organization = "Scopevisio Demo AG";

		SOAPMessage mockResponse = SOAPAdapterTestData.getTestBalancesMessage();
		when( adapter.getSoapResponseWithBalancesAtSpecificDate( "01.01.2011" ) ).thenReturn( mockResponse );

		ArrayList<BalanceVO> expected = new ArrayList<>();
		expected.add( new BalanceVO( 0, date ) );
		when( adapter.getAllBalancesFromSusaResponse( mockResponse, date, organization ) ).thenReturn( expected );

		assertEquals( expected, service.getBalancesForDate( date, organization ) );

		verify( adapter ).getAllBalancesFromSusaResponse( mockResponse, date, organization );
	}

	@Test
	public void shouldCatchErrorsWhenSerializingSoapMessageForLogEntry() throws Exception {
		( (ScopeVisioAccountServiceImpl) service ).logResponse( null );
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}
} 