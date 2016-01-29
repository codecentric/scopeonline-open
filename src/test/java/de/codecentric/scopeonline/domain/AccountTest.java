package de.codecentric.scopeonline.domain;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;
import de.codecentric.scopeonline.util.CalendarCalculator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AccountTest {
	private Account  account;
	private Calendar yesterday;
	private Calendar today;
	private Calendar tomorrow;
	private Calendar dayAfterTomorrow;

	@Before
	public void setUp() throws Exception {
		account = new Account();

		today = Calendar.getInstance();
		yesterday = CalendarCalculator.getSpecifiedDayAfter( today, -1 );
		tomorrow = CalendarCalculator.getSpecifiedDayAfter( today, 1 );
		dayAfterTomorrow = CalendarCalculator.getSpecifiedDayAfter( today, 2 );
	}

	@Test
	public void givenANewAccount_shouldInitializeWithZeroBalance() throws Exception {
		assertEquals( 0, new BigDecimal( 0 ).compareTo( account.getInitialBalance().amount ) );
	}

	@Test
	public void givenANewAccount_whenAskedForBalancesOfToday_shouldReturnListOfOneEmptyBalance() throws Exception {
		assertEquals( 0, new BigDecimal( 0 ).compareTo( account.getBalancesBetween( today, today ).get( 0 ).amount ) );
	}

	@Test
	public void givenANewAccount_whenAskedForBalancesOfTodayAndTomorrow_shouldReturnListOfTwoEmptyBalances() throws
																											 Exception {
		List<BalanceVO> balances = account.getBalancesBetween( today, tomorrow );

		assertEquals( 0, new BigDecimal( 0 ).compareTo( balances.get( 0 ).amount ) );
		assertEquals( 0, new BigDecimal( 0 ).compareTo( balances.get( 1 ).amount ) );
	}

	@Test
	public void givenAnInitialBalance_whenAskedForBalances_shouldReturnListOfBalancesWithInitialAmount()
			throws Exception {
		account.setInitialBalance( new BalanceVO( 1.0, today ) );

		List<BalanceVO> balances = account.getBalancesBetween( today, tomorrow );

		assertEquals( 0, new BigDecimal( 1 ).compareTo( balances.get( 0 ).amount ) );
		assertEquals( 0, new BigDecimal( 1 ).compareTo( balances.get( 1 ).amount ) );
	}

	@Test
	public void givenANewAccount_shouldHaveNoRecords() throws Exception {
		assertNull( account.getRecords() );
	}

	@Test
	public void givenAListOfRecords_shouldRetainRecords() throws Exception {
		ArrayList<RecordVO> records = new ArrayList<>();
		account.setRecords( records );
		assertEquals( records, account.getRecords() );
	}

	@Test
	public void givenAnInitialBalanceAndOneRecord_whenAskedForBalances_shouldReturnCalculatedList() throws
																									Exception {
		account.setInitialBalance( new BalanceVO( 1.0, today ) );

		List<RecordVO> records = new ArrayList<>();
		records.add( new RecordVO( 1.0, tomorrow ) );
		account.setRecords( records );

		List<BalanceVO> balances = account.getBalancesBetween( today, dayAfterTomorrow );

		assertEquals( 0, new BigDecimal( 1 ).compareTo( balances.get( 0 ).amount ) );
		assertEquals( 0, new BigDecimal( 2 ).compareTo( balances.get( 1 ).amount ) );
		assertEquals( 0, new BigDecimal( 2 ).compareTo( balances.get( 2 ).amount ) );
	}

	@Test
	public void givenAnInitialBalanceAndOlderRecord_whenAskedForTodaysBalance_shouldReturnCalculatedValue() throws
																											Exception {
		account.setInitialBalance( new BalanceVO( 1.0, CalendarCalculator.getSpecifiedDayAfter( today, -2 ) ) );

		List<RecordVO> records = new ArrayList<>();
		records.add( new RecordVO( 1.0, yesterday ) );
		account.setRecords( records );

		List<BalanceVO> balances = account.getBalancesBetween( today, today );

		assertEquals( 0, new BigDecimal( 2 ).compareTo( balances.get( 0 ).amount ) );
	}

	@After
	public void tearDown() throws Exception {
		account = null;
		today = null;
		tomorrow = null;
		dayAfterTomorrow = null;
	}
} 