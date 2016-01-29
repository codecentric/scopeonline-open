package de.codecentric.scopeonline.rpc;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.persistence.BalanceHashMapQueries;
import de.codecentric.scopeonline.util.CalendarCalculator;

import java.text.SimpleDateFormat;
import java.util.*;

public class BalanceCache {

	private HashMap<String, BalanceVO> balances;
	private SimpleDateFormat formatter = new SimpleDateFormat( "dd.MM.yyyy" );
	private BalanceHashMapQueries balanceHashMapQueries;


	public BalanceCache( BalanceHashMapQueries balanceHashMapQueries ) {
		this.balanceHashMapQueries = balanceHashMapQueries;
		balances = balanceHashMapQueries.getBalanceHashMap();
	}


	public boolean hasBalances() {
		return count()>0;
	}

	private int count() {
		return balances != null ? balances.size() : 0;
	}

	public void addBalance( final BalanceVO balanceVO ) {
		if( balances == null )
			balances = new HashMap<>();
		balances.put( balanceVO.getDateString(), balanceVO );
	}

	public boolean hasBalances( final Calendar date ) {
		return balances.containsKey( formatter.format( date.getTime() ) );
	}

	public boolean hasBalances( final Calendar from, final Calendar to ) {
		Calendar fromDay = CalendarCalculator.getDayAtMidnight( from );
		Calendar toDay = CalendarCalculator.getDayAtMidnight( to );
		if( fromDay.equals( toDay ) )
			return hasBalances( fromDay );
		else {
			return containsAllBalancesBetween( fromDay, toDay );
		}
	}

	private boolean containsAllBalancesBetween( final Calendar fromDay, final Calendar toDay ) {
		final Calendar start = fromDay.before( toDay ) ? fromDay : toDay;
		final Calendar end = fromDay.before( toDay ) ? toDay : fromDay;
		boolean all = true;
		while( start.before( end ) ) {
			if( !balances.containsKey( formatter.format( start.getTime() ) ) )
				all = false;
			start.add( Calendar.DAY_OF_YEAR, 1 );
		}
		return all;
	}

	public void addAll( final List<BalanceVO> balanceVOs ) {
		for( BalanceVO balance : balanceVOs )
			addBalance( balance );
	}

	public Calendar lastAvailableDateOrNull() {
		List<BalanceVO> available = new ArrayList<>();
		if( balances != null )
			available.addAll( balances.values() );
		Collections.sort( available );
		return available.size() != 0 ? available.get( available.size()-1 ).date : null;
	}

	public List<BalanceVO> getBalancesBetween( final Calendar fromDate, final Calendar toDate ) {
		final Calendar start = CalendarCalculator.copyDate( fromDate );
		final ArrayList<BalanceVO> between = new ArrayList<>();
		while( start.before( toDate ) || start.equals( toDate ) ) {
			between.add( balances.get( formatter.format( start.getTime() ) ) );
			start.add( Calendar.DAY_OF_YEAR, 1 );
		}
		return between;
	}


	public void saveBalancesInDataBase() {
		balanceHashMapQueries.insertBalanceHashMap( balances );
	}
}
