package de.codecentric.scopeonline.util;

import java.util.Calendar;
import java.util.Date;

public class CalendarCalculator {
	public static final int MILLISECONDS_PER_DAY = ( 1000*60*60*24 );

	public static long getDaysInBetween( final Calendar start, final Calendar end ) {
		Calendar startDate = getDayAtMidnight( start );
		Calendar endDate = getDayAtMidnight( end );
		long differenceInMilliseconds = endDate.getTimeInMillis()-startDate.getTimeInMillis();
		return differenceInMilliseconds/MILLISECONDS_PER_DAY;
	}


	public static Calendar getSpecifiedDayAfter( final Calendar inputDate, final int i ) {
		Calendar date = CalendarCalculator.copyDate( inputDate );
		date.add( Calendar.DAY_OF_YEAR, i );
		return date;
	}

	public static Calendar copyDate( final Calendar inputDate ) {
		Calendar date = Calendar.getInstance();
		date.setTime( inputDate.getTime() );
		return date;
	}

	public static Calendar getDateAtSpecifiedMonthAfter( final Calendar inputDate, final int i ) {
		Calendar date = CalendarCalculator.copyDate( inputDate );
		date.add( Calendar.MONTH, i );
		return date;
	}

	@SuppressWarnings("MagicConstant")
	public static Calendar getDayAtMidnight( final Calendar from ) {
		Calendar date = Calendar.getInstance();
		date.set( from.get( Calendar.YEAR ), from.get( Calendar.MONTH ), from.get( Calendar.DAY_OF_MONTH ), 0, 0, 0 );
		return date;
	}

	public static boolean isOnSameDay( final Calendar date, final Calendar compareDate ) {
		return isSameYear( date, compareDate ) &&
			   isSameMonth( date, compareDate ) &&
			   isSameDayOfMonth( date, compareDate );
	}

	public static boolean isSameDayOfMonth( final Calendar date, final Calendar compareDate ) {
		return date.get( Calendar.DAY_OF_MONTH ) == compareDate.get( Calendar.DAY_OF_MONTH );
	}

	public static boolean isSameMonth( final Calendar date, final Calendar compareDate ) {
		return date.get( Calendar.MONTH ) == compareDate.get( Calendar.MONTH );
	}

	public static boolean isSameYear( final Calendar date, final Calendar compareDate ) {
		return date.get( Calendar.YEAR ) == compareDate.get( Calendar.YEAR );
	}

	public static Calendar createWithDate( final Date date ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( date );
		return calendar;
	}
}
