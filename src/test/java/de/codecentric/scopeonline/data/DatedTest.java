package de.codecentric.scopeonline.data;

import de.codecentric.scopeonline.util.CalendarCalculator;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class DatedTest {
	@Test
	public void newInstanceShouldHaveCurrentDate() throws Exception {
		Calendar now = Calendar.getInstance();
		DatedVO dated = new DatedVO();

		assertEquals( now, dated.date );
	}

	@Test
	public void shouldReturnTrueIfOnSameDay() throws Exception {
		DatedVO dated = new DatedVO();

		assertTrue( dated.isOnSameDayAs( new DatedVO() ) );
	}

	@Test
	public void shouldReturnFalseIfNotOnSameDay() throws Exception {
		Calendar yesterday = getYesterday();
		DatedVO dated = new DatedVO( yesterday );

		assertFalse( dated.isOnSameDayAs( new DatedVO() ) );
	}

	@Test
	public void shouldReturnTrueIfVOIsBeforeDate() throws Exception {
		Calendar yesterday = getYesterday();
		DatedVO dated = new DatedVO( yesterday );

		assertTrue( dated.isBefore( Calendar.getInstance() ) );
	}

	@Test
	public void shouldReturnFalseIfVOIsNotBeforeDate() throws Exception {
		Calendar today = Calendar.getInstance();
		DatedVO dated = new DatedVO( today );

		assertFalse( dated.isBefore( today ) );
	}

	@Test
	public void shouldReturnFormattedDate() throws Exception {
		Calendar today = Calendar.getInstance();
		today.set( 2014, Calendar.JANUARY, 12, 0, 0, 0 );
		DatedVO dated = new DatedVO( today );

		assertEquals( "12.01.2014", dated.getDateString() );
	}

	@Test
	public void shouldBeComparable() throws Exception {
		Calendar today = Calendar.getInstance();
		DatedVO todayVO = new DatedVO( today );

		Calendar yesterday = getYesterday();
		DatedVO yesterdayVO = new DatedVO( yesterday );

		assertEquals( -1, yesterdayVO.compareTo( todayVO ) );
		assertEquals( 0, yesterdayVO.compareTo( yesterdayVO ) );
		assertEquals( 1, todayVO.compareTo( yesterdayVO ) );

	}

	private Calendar getYesterday() {
		return CalendarCalculator.getSpecifiedDayAfter( Calendar.getInstance(), -1 );
	}
}