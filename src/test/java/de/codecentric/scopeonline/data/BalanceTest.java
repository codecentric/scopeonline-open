package de.codecentric.scopeonline.data;

import de.codecentric.scopeonline.util.CalendarCalculator;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

public class BalanceTest {
	@Test
	public void shouldReturnTrueIfBalanceHasTheSameDayAsBalance() throws Exception {
		BalanceVO balance = new BalanceVO( 1.0, Calendar.getInstance() );
		BalanceVO compareBalance = new BalanceVO( 1.0, Calendar.getInstance() );
		assertTrue( balance.isOnSameDayAs( compareBalance ) );
	}

	@Test
	public void shouldReturnFalseIfBalanceDoesNotHaveTheSameDayAsBalance() throws Exception {
		BalanceVO balance = new BalanceVO( 1.0, Calendar.getInstance() );
		BalanceVO compareBalance =
				new BalanceVO( 1.0, CalendarCalculator.getSpecifiedDayAfter( Calendar.getInstance(), 1 ) );
		assertFalse( balance.isOnSameDayAs( compareBalance ) );
	}

	@Test
	public void shouldReturnTrueIfBalanceHasTheSameDayAsRecord() throws Exception {
		BalanceVO balance = new BalanceVO( 1.0, Calendar.getInstance() );
		RecordVO record = new RecordVO( 1.0, Calendar.getInstance() );
		assertTrue( balance.isOnSameDayAs( record ) );
	}

	@Test
	public void shouldReturnFalseIfBalanceDoesNotHaveTheSameDayAsRecord() throws Exception {
		BalanceVO balance = new BalanceVO( 1.0, Calendar.getInstance() );
		RecordVO record =
				new RecordVO( 1.0, CalendarCalculator.getSpecifiedDayAfter( Calendar.getInstance(), 1 ) );
		assertFalse( balance.isOnSameDayAs( record ) );
	}

	@Test
	public void shouldFormatValuesToReadableString() throws Exception {
		Calendar date = Calendar.getInstance();
		BalanceVO balance = new BalanceVO( 1.0, date );
		SimpleDateFormat format = new SimpleDateFormat( "dd.MM.yyyy" );
		assertEquals( format.format( date.getTime() )+" => "+new BigDecimal( 1.0 ).setScale( 2, RoundingMode.HALF_UP ),
					  balance.toString() );
	}

	@Test
	public void shouldAddBalanceToInitialBalanceIfOnSameDate() throws Exception {
		Calendar date = Calendar.getInstance();
		BalanceVO balanceVO = new BalanceVO( 0.0, date );
		BalanceVO balanceVO1 = new BalanceVO( 1.0, date );
		balanceVO.add( balanceVO1 );
		assertEquals( 0, new BigDecimal( 1.0 ).compareTo( balanceVO.amount ) );
	}

	@Test
	public void shouldNotAddBalanceToInitialBalanceIfNotOnSameDate() throws Exception {
		Calendar today = Calendar.getInstance();
		Calendar yesterday = Calendar.getInstance();
		yesterday.add( Calendar.DAY_OF_YEAR, -1 );
		BalanceVO balanceVO = new BalanceVO( 0.0, today );
		BalanceVO balanceVOyesterday = new BalanceVO( 1.0, yesterday );
		balanceVO.add( balanceVOyesterday );
		assertEquals( 0, new BigDecimal( 0.0 ).compareTo( balanceVO.amount ) );
	}

	@Test
	public void shouldAddListOfBalanceToInitial() throws Exception {
		Calendar today = Calendar.getInstance();
		Calendar yesterday = Calendar.getInstance();
		yesterday.add( Calendar.DAY_OF_YEAR, -1 );
		List<BalanceVO> balanceVOList = new ArrayList<>();
		balanceVOList.add( new BalanceVO( 1.0, today ) );
		balanceVOList.add( new BalanceVO( 2.0, today ) );
		balanceVOList.add( new BalanceVO( 3.0, yesterday ) );

		BalanceVO balanceVO = new BalanceVO( 0.0, today );
		balanceVO.addAll( balanceVOList );
		assertEquals( 0, new BigDecimal( 3.0 ).compareTo( balanceVO.amount ) );
	}
}