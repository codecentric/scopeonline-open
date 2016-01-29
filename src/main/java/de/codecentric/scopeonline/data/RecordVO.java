package de.codecentric.scopeonline.data;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

public class RecordVO extends DatedVO implements Serializable {
	public BigDecimal amount;
	public String     account;

	public RecordVO( final double amount, final Calendar date ) {
		super( date );
		this.amount = new BigDecimal( amount );
	}

	public RecordVO( final BigDecimal amount, final Calendar date ) {
		super( date );
		this.amount = amount;
	}

	public RecordVO( final String account, final BigDecimal amount, final Calendar date ) {
		super( date );
		this.account = account;
		this.amount = amount;
	}

	public RecordVO() {
		this.amount = new BigDecimal( 0.0 );
	}

	@SuppressWarnings("NullableProblems")
	public int compareTo( final RecordVO record ) {
		return compareTo( (DatedVO) record );
	}

	public String toString() {
		return this.getDateString()+" => "+this.amount.setScale( 2, RoundingMode.HALF_UP );
	}
}
