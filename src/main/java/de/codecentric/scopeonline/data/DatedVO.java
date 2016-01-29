package de.codecentric.scopeonline.data;

import de.codecentric.scopeonline.util.CalendarCalculator;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
public class DatedVO implements Comparable<DatedVO> {


	@Id
	public String dateAsString;

	public Calendar date;

	private static SimpleDateFormat format = new SimpleDateFormat( "dd.MM.yyyy" );

	public String getDateString() {

		return format.format( date.getTime() );
	}

	public boolean isOnSameDayAs( final DatedVO compareVO ) {
		return CalendarCalculator.isOnSameDay( date, compareVO.date );
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public int compareTo( final DatedVO datedVO ) {
		return date.compareTo( datedVO.date );
	}

	public DatedVO( final Calendar date ) {
		this.date = date;
		dateAsString= format.format( date.getTime() );
	}

	public DatedVO() {
		date = Calendar.getInstance();
	}

	public boolean isBefore( final Calendar date ) {
		return this.date.before( date );
	}
}
