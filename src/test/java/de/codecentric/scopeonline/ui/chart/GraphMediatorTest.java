package de.codecentric.scopeonline.ui.chart;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;
import de.codecentric.scopeonline.domain.Account;
import de.codecentric.scopeonline.rpc.BalanceCache;
import de.codecentric.scopeonline.service.AccountService;
import de.codecentric.scopeonline.util.CalendarCalculator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class GraphMediatorTest {
	private GraphMediator mediator;

	@Mock
	private Account        account;
	@Mock
	private AccountService service;
	@Mock
	private BalanceCache   cache;

	private final Calendar TODAY            = Calendar.getInstance();
	private final Calendar TOMORROW         = CalendarCalculator.getSpecifiedDayAfter( Calendar.getInstance(), 1 );
	private final String   ORG              = "Scopevisio Demo AG";
	private final Calendar JANUARY_ONE_1970 = Calendar.getInstance();

	@Before
	public void setUp() throws Exception {
		JANUARY_ONE_1970.setTimeInMillis( 0 );

		initMocks( this );

		mediator = new GraphMediator();
		mediator.setAccount( account );
		mediator.setAccountServices( mockServiceList() );
		mediator.setCache( cache );
	}

	private HashMap<String, AccountService> mockServiceList() {
		HashMap<String, AccountService> accountServices = new HashMap<>();
		accountServices.put( ORG, service );
		return accountServices;
	}

	private void mockRecords() {
		ArrayList<RecordVO> recordVOs = new ArrayList<>();
		when( service.getAllRecords( any( Calendar.class ),
									 any( Calendar.class ),
									 eq( ORG ) ) ).thenReturn( recordVOs );
	}

	private void mockInitialBalance() {
		ArrayList<BalanceVO> initialBalances = new ArrayList<>();
		initialBalances.add( new BalanceVO( 0, JANUARY_ONE_1970 ) );
		when( service.getBalancesForDate( JANUARY_ONE_1970, ORG ) ).thenReturn( initialBalances );
		when( account.getInitialBalance() ).thenReturn( initialBalances.get( 0 ) );
	}

	@Test
	public void givenFreshMediator_whenAskedForBalances_shouldLoadEveryRecordSince1970FromService() throws Exception {
		mockRecords();
		mockInitialBalance();
		Calendar toDate = Calendar.getInstance();
		List<BalanceVO> balances = mediator.getBalances( Calendar.getInstance(), toDate );
		assertNotNull( balances );
		verify( account ).setInitialBalance( any( BalanceVO.class ) );
		verify( service ).getAllRecords( CalendarCalculator.getSpecifiedDayAfter( JANUARY_ONE_1970, 1 ), toDate, ORG );
	}

	@Test
	public void givenCacheIsLoaded_whenAskedForBalances_shouldLoadFromCache() throws Exception {
		when( cache.hasBalances() ).thenReturn( true );
		when( cache.lastAvailableDateOrNull() ).thenReturn( TOMORROW );
		when( cache.getBalancesBetween( TODAY, TOMORROW ) ).thenReturn( new ArrayList<BalanceVO>() );
		List<BalanceVO> balances = mediator.getBalances( TODAY, TOMORROW );
		assertNotNull( balances );
		verify( cache ).getBalancesBetween( TODAY, TOMORROW );
		verify( account, never() ).setInitialBalance( any( BalanceVO.class ) );
		verify( service, never() ).getAllRecords( CalendarCalculator.getSpecifiedDayAfter( JANUARY_ONE_1970, 1 ),
												  TOMORROW, ORG );
	}

	@Test
	public void givenCacheIsLoaded_whenAskedForBalancesAfterLastAvailableDate_shouldLoadOnlyMissingDaysFromCache()
			throws Exception {
		when( cache.hasBalances() ).thenReturn( true );
		when( cache.lastAvailableDateOrNull() ).thenReturn( TODAY );
		when( cache.getBalancesBetween( TODAY, TOMORROW ) ).thenReturn( new ArrayList<BalanceVO>() );
		List<BalanceVO> balances = mediator.getBalances( TODAY, TOMORROW );
		assertNotNull( balances );
		verify( cache ).getBalancesBetween( TODAY, TOMORROW );
		verify( account ).setInitialBalance( any( BalanceVO.class ) );
		verify( service ).getAllRecords( TOMORROW, TOMORROW, ORG );
	}


	@After
	public void tearDown() throws Exception {
		mediator = null;
	}
} 