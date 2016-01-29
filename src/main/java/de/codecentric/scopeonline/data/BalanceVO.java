package de.codecentric.scopeonline.data;

import de.codecentric.scopeonline.util.CalendarCalculator;

import javax.persistence.Entity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

@Entity
public class BalanceVO extends DatedVO implements Serializable {


	public BigDecimal amount;

	public BalanceVO( final double amount, final Calendar date ) {
		super( CalendarCalculator.getDayAtMidnight( date ) );
		this.amount = new BigDecimal( amount );
	}

	public BalanceVO( final BigDecimal amount, final Calendar date ) {
		super( CalendarCalculator.getDayAtMidnight( date ) );
		this.amount = amount;
	}

	public BalanceVO( final BigDecimal amount ) {
		super( CalendarCalculator.getDayAtMidnight( Calendar.getInstance() ) );
		this.amount = amount;
	}

	public BalanceVO() {
		super( CalendarCalculator.getDayAtMidnight( Calendar.getInstance() ) );
		this.amount = new BigDecimal( 0 );
	}

	public String toString() {
		return getDateString()+" => "+
			   amount.setScale( 2, RoundingMode.HALF_UP );
	}

	public void add( final BalanceVO balanceVO ) {
		if( this.date.get( Calendar.YEAR ) == balanceVO.date.get( Calendar.YEAR ) &&
			this.date.get( Calendar.DAY_OF_YEAR ) == balanceVO.date.get( Calendar.DAY_OF_YEAR ) )
			this.amount = this.amount.add( balanceVO.amount );
	}

	@Override
	public boolean isBefore( final Calendar date ) {
		return super.isBefore( CalendarCalculator.getDayAtMidnight( date ) );
	}

	public void addAll( final List<BalanceVO> balanceVOList ) {
		for( BalanceVO balanceVO : balanceVOList )
			this.add( balanceVO );
	}
}
