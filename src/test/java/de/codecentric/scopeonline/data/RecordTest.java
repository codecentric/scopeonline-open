package de.codecentric.scopeonline.data;

import de.codecentric.scopeonline.util.CalendarCalculator;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class RecordTest {
	@Test
	public void compareReturnsNegativeWhenDateIsBeforeOtherDate() throws Exception {
		Calendar yesterday = Calendar.getInstance();
		yesterday.set( 2014, Calendar.JANUARY, 15 );
		Calendar today = Calendar.getInstance();
		today.set( 2014, Calendar.JANUARY, 16 );
		RecordVO before = new RecordVO( 0.0, yesterday );
		RecordVO after = new RecordVO( 0.0, today );
		assertEquals( -1, before.compareTo( after ) );
	}

	@Test
	public void compareReturnsPositiveWhenDateIsAfterOtherDate() throws Exception {
		Calendar yesterday = Calendar.getInstance();
		yesterday.set( 2014, Calendar.JANUARY, 15 );
		Calendar today = Calendar.getInstance();
		today.set( 2014, Calendar.JANUARY, 16 );
		RecordVO before = new RecordVO( 0.0, yesterday );
		RecordVO after = new RecordVO( 0.0, today );
		assertEquals( 1, after.compareTo( before ) );
	}

	@Test
	public void compareReturnsPositiveWhenDateIsSameDate() throws Exception {
		Calendar today = Calendar.getInstance();
		today.set( 2014, Calendar.JANUARY, 15, 0, 0, 0 );
		RecordVO before = new RecordVO( 0.0, today );
		RecordVO after = new RecordVO( 0.0, today );
		assertEquals( 0, after.compareTo( before ) );
	}

	@Test
	public void shouldReturnTrueIfRecordHasSameDayAsBalance() throws Exception {
		RecordVO record = new RecordVO( 1.0, Calendar.getInstance() );
		BalanceVO balance = new BalanceVO( 1.0, Calendar.getInstance() );
		assertTrue( record.isOnSameDayAs( balance ) );
	}

	@Test
	public void shouldReturnFalseIfRecordDoesNotHaveSameDayAsBalance() throws Exception {
		RecordVO record = new RecordVO( 1.0, Calendar.getInstance() );
		BalanceVO balance = new BalanceVO( 1.0, CalendarCalculator.getSpecifiedDayAfter( Calendar.getInstance(), 1 ) );
		assertFalse( record.isOnSameDayAs( balance ) );
	}

	@Test
	public void shouldReturnTrueIfRecordHasSameDayAsRecord() throws Exception {
		RecordVO record = new RecordVO( 1.0, Calendar.getInstance() );
		RecordVO compareRecord = new RecordVO( 1.0, Calendar.getInstance() );
		assertTrue( record.isOnSameDayAs( compareRecord ) );
	}

	@Test
	public void shouldReturnFalseIfRecordDoesNotHaveSameDayAsRecord() throws Exception {
		RecordVO record = new RecordVO( 1.0, Calendar.getInstance() );
		RecordVO compareRecord =
				new RecordVO( 1.0, CalendarCalculator.getSpecifiedDayAfter( Calendar.getInstance(), 1 ) );
		assertFalse( record.isOnSameDayAs( compareRecord ) );
	}


	@Test
	public void shouldFormatValuesToReadableString() throws Exception {
		Calendar date = Calendar.getInstance();
		RecordVO balance = new RecordVO( 1.0, date );
		SimpleDateFormat format = new SimpleDateFormat( "dd.MM.yyyy" );
		assertEquals( format.format( date.getTime() )+" => "+new BigDecimal( 1.0 ).setScale( 2, RoundingMode.HALF_UP ),
					  balance.toString() );
	}

	@Test
	public void shouldBeSortableByDate() throws Exception {
		Calendar yesterday = Calendar.getInstance();
		yesterday.add( Calendar.DAY_OF_YEAR, -1 );
		Calendar today = Calendar.getInstance();
		List<RecordVO> recordVOList = new ArrayList<>();
		recordVOList.add( new RecordVO( 1.0, today ) );
		recordVOList.add( new RecordVO( 2.0, yesterday ) );

		Collections.sort( recordVOList );

		assertEquals( yesterday, recordVOList.get( 0 ).date );
		assertEquals( today, recordVOList.get( 1 ).date );
	}

	@Test
	public void newInstanceShouldHaveAmountOfZero() throws Exception {
		assertEquals( new BigDecimal( 0 ), new RecordVO().amount );
	}
}