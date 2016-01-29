package de.codecentric.scopeonline.domain;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;
import de.codecentric.scopeonline.util.CalendarCalculator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class Account implements Serializable {
	private BalanceVO      initialBalance;
	private List<RecordVO> records;

	public Account() {
		this.initialBalance = new BalanceVO( new BigDecimal( 0 ) );
	}

	public List<BalanceVO> getBalancesBetween( final Calendar from, final Calendar to ) {
		Calendar currentDate = CalendarCalculator.getDayAtMidnight( from );
		Calendar endDate = CalendarCalculator.getDayAtMidnight( to );
		ArrayList<BalanceVO> balances = new ArrayList<>();

		Map<String, BalanceVO> balanceMap = getBalanceMapForActiveDates();
		BigDecimal currentAmount = getInitialAmountForDateFromBalanceMap( currentDate, balanceMap );
		long daysInBetween = CalendarCalculator.getDaysInBetween( currentDate, endDate );
		int i = -1;
		while( ++i<=daysInBetween ) {
			BalanceVO dayBalance = balanceMap.get( getDateKey( currentDate ) );
			currentAmount = dayBalance != null ? dayBalance.amount : currentAmount;
			BalanceVO balance = new BalanceVO( currentAmount, currentDate );
			balances.add( balance );
			currentDate.add( Calendar.DAY_OF_YEAR, 1 );
		}
		return balances;
	}

	private String getDateKey( final Calendar date ) {
		SimpleDateFormat format = new SimpleDateFormat( "dd.MM.yyyy" );
		return format.format( date.getTime() );
	}

	private BigDecimal getInitialAmountForDateFromBalanceMap( final Calendar from,
															  final Map<String, BalanceVO> balanceMap ) {
		BigDecimal amount = initialBalance.amount;
		Calendar startDate = CalendarCalculator.getDayAtMidnight( from );

		List<BalanceVO> balances = getSortedListOfBalances( balanceMap );
		for( BalanceVO balance : balances )
			if( balance.isBefore( startDate ) )
				amount = balance.amount;
		return amount;
	}

	private List<BalanceVO> getSortedListOfBalances( final Map<String, BalanceVO> balanceMap ) {
		List<BalanceVO> balances = new ArrayList<>();
		balances.addAll( balanceMap.values() );
		Collections.sort( balances );
		return balances;
	}

	private Map<String, BalanceVO> getBalanceMapForActiveDates() {
		Map<String, BalanceVO> balanceMap = new HashMap<>();
		Accumulator accumulator = new AccumulatorImpl( initialBalance );
		List<BalanceVO> accumulated = accumulator.accumulate( records );
		for( BalanceVO balance : accumulated )
			balanceMap.put( balance.getDateString(), balance );
		return balanceMap;
	}

	public void setRecords( final List<RecordVO> records ) {
		this.records = records;

	}

	public List<RecordVO> getRecords() {
		return records;
	}

	public BalanceVO getInitialBalance() {
		return initialBalance;
	}

	public void setInitialBalance( final BalanceVO initialBalance ) {
		this.initialBalance = initialBalance;
	}
}
