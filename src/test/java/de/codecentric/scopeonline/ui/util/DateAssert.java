package de.codecentric.scopeonline.ui.util;

import java.util.Calendar;

import static org.junit.Assert.fail;

public class DateAssert {
	public static void assertWithinSameSecond( Calendar date, Calendar compareDate ) throws Exception {
		if( date.getTimeInMillis()<compareDate.getTimeInMillis()-500 ||
			date.getTimeInMillis()>compareDate.getTimeInMillis()+500 )
			fail( "Date values were not within the same second:\n"+date.toString()+"\n"+compareDate.toString() );
	}
}
