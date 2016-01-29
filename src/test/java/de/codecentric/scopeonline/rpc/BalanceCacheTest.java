package de.codecentric.scopeonline.rpc;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.persistence.BalanceHashMapQueries;
import de.codecentric.scopeonline.util.CalendarCalculator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BalanceCacheTest {
	private static final Calendar YESTERDAY = CalendarCalculator.getSpecifiedDayAfter( Calendar.getInstance(), -1 );
	private static final Calendar TODAY     = Calendar.getInstance();
	private static final Calendar TOMORROW  = CalendarCalculator.getSpecifiedDayAfter( Calendar.getInstance(), 1 );

	private BalanceCache cache;

	@Mock
	private BalanceHashMapQueries balanceHashMapQueries;


	private Calendar getDecLast2011() {
		Calendar decThirtyOne2011 = Calendar.getInstance();
		decThirtyOne2011.set( 2011, Calendar.DECEMBER, 31 );
		return decThirtyOne2011;
	}

	private Calendar getJanuaryFirst2011() {
		Calendar janOne2011 = Calendar.getInstance();
		janOne2011.set( 2011, Calendar.JANUARY, 1 );
		return janOne2011;
	}

	private HashMap getHashMapWithAllDaysFrom2011() {
		HashMap expected = new HashMap<String, BalanceVO>();
		Calendar calendar = getJanuaryFirst2011();
		int i = 0;
		while( i<365 ) {
			BalanceVO balanceVO = new BalanceVO( new BigDecimal( i++ ), calendar );
			expected.put( balanceVO.dateAsString, balanceVO );
			calendar.add( Calendar.DAY_OF_YEAR, 1 );
		}
		return expected;
	}

	@Before
	public void setUp() throws Exception {
		initMocks( this );
		when( balanceHashMapQueries.getBalanceHashMap() ).thenReturn( new HashMap<String, BalanceVO>() );
		cache = new BalanceCache( balanceHashMapQueries );
	}

	@Test
	public void givenNoBalances_whenAskedIfContainsBalances_shouldReturnFalse() throws Exception {
		assertFalse( cache.hasBalances() );
	}

	@Test
	public void givenOneCachedBalance_whenAskedIfContainsBalanceForThisDay_shouldReturnTrue() throws Exception {
		cache.addBalance( new BalanceVO() );
		assertTrue( cache.hasBalances( Calendar.getInstance() ) );
	}


	@Test
	public void givenOneCachedBalance_whenAskedIfContainsBalanceForAnotherDay_shouldReturnFalse() throws Exception {
		cache.addBalance( new BalanceVO() );
		assertFalse( cache.hasBalances( TOMORROW ) );
	}

	@Test
	public void givenTwoCachedBalances_whenAskedIfContainsBalancesForBothDays_shouldReturnTrue() throws Exception {
		cache.addBalance( new BalanceVO( 0, TODAY ) );
		cache.addBalance( new BalanceVO( 0, TOMORROW ) );
		assertTrue( cache.hasBalances( TODAY, TOMORROW ) );
	}

	@Test
	public void givenTwoCachedBalances_whenAskedIfContainsBalancesForAnotherDay_shouldReturnFalse() throws Exception {
		cache.addBalance( new BalanceVO( 0, TODAY ) );
		cache.addBalance( new BalanceVO( 0, TOMORROW ) );
		assertFalse( cache.hasBalances( YESTERDAY ) );
	}

	@Test
	public void givenTwoCachedBalancesWithOneDayInBetween_whenAskedIfContainsBalancesTimeSpan_shouldReturnFalse()
			throws Exception {
		cache.addBalance( new BalanceVO( 0, YESTERDAY ) );
		cache.addBalance( new BalanceVO( 0, TOMORROW ) );
		assertFalse( cache.hasBalances( YESTERDAY, TOMORROW ) );
	}

	@Test
	public void givenAnEmptyListOfBalances_shouldReturnNullForLastAvailableDate() throws Exception {
		cache.addAll( new ArrayList<BalanceVO>() );
		assertEquals( null, cache.lastAvailableDateOrNull() );
	}


	@Test
	public void givenAListOfOneBalance_shouldReturnLastAvailableDate() throws Exception {
		ArrayList<BalanceVO> balanceVOs = new ArrayList<>();
		BalanceVO balance = new BalanceVO();
		balanceVOs.add( balance );
		cache.addAll( balanceVOs );
		assertEquals( balance.date, cache.lastAvailableDateOrNull() );
	}


	@Test
	public void givenAListOfBalances_shouldReturnLastAvailableDateRegardlessOfOrder() throws Exception {
		ArrayList<BalanceVO> balanceVOs = new ArrayList<>();
		BalanceVO balance = new BalanceVO( 0, TOMORROW );
		balanceVOs.add( balance );
		balanceVOs.add( new BalanceVO( 0, YESTERDAY ) );
		balanceVOs.add( new BalanceVO( 0, TODAY ) );
		cache.addAll( balanceVOs );
		assertEquals( balance.date, cache.lastAvailableDateOrNull() );
	}

	@Test
	public void givenAListOfBalances_shouldReturnListOfOrderedBalances() throws Exception {
		ArrayList<BalanceVO> balanceVOs = new ArrayList<>();
		BalanceVO tomorrow = new BalanceVO( 0, TOMORROW );
		balanceVOs.add( tomorrow );

		BalanceVO yesterday = new BalanceVO( 0, YESTERDAY );
		balanceVOs.add( yesterday );

		BalanceVO today = new BalanceVO( 0, TODAY );
		balanceVOs.add( today );

		cache.addAll( balanceVOs );

		List<BalanceVO> balances = cache.getBalancesBetween( YESTERDAY, TOMORROW );
		assertEquals( yesterday, balances.get( 0 ) );
		assertEquals( today, balances.get( 1 ) );
		assertEquals( tomorrow, balances.get( 2 ) );
	}

	@Test
	public void givenNewBalanceCache_withValuesInDB_loadTheseValues() throws Exception {
		HashMap expected = getHashMapWithAllDaysFrom2011();
		when( balanceHashMapQueries.getBalanceHashMap() ).thenReturn( expected );
		cache = new BalanceCache( balanceHashMapQueries );
		Calendar janOne2011 = getJanuaryFirst2011();
		Calendar decThirtyOne2011 = getDecLast2011();
		
		assertTrue( cache.hasBalances( janOne2011, decThirtyOne2011 ) );
		
		janOne2011.add( Calendar.DAY_OF_YEAR, -1 );          		
		assertFalse( cache.hasBalances( janOne2011 ) );
		
		decThirtyOne2011.add( Calendar.DAY_OF_YEAR, 1 );
		assertFalse( cache.hasBalances( decThirtyOne2011 ) );
	}

	@Test
	public void givenFilledBalanceCache_whenAskedToStoreBalances_shouldStoreBalancesInDB() throws Exception {
		HashMap map = new HashMap<String, BalanceVO>(  );
		when(balanceHashMapQueries.getBalanceHashMap()).thenReturn( map );
		cache = new BalanceCache( balanceHashMapQueries );
		cache.saveBalancesInDataBase();
		verify( balanceHashMapQueries ).insertBalanceHashMap( map );
	}

	@After
	public void tearDown() throws Exception {
		cache = null;
	}
} 