package de.codecentric.scopeonline.ui.chart;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;
import de.codecentric.scopeonline.domain.Account;
import de.codecentric.scopeonline.rpc.BalanceCache;
import de.codecentric.scopeonline.service.AccountService;
import de.codecentric.scopeonline.util.CalendarCalculator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class GraphMediator {
	private final Logger log = LoggerFactory.getLogger( GraphMediator.class );

	private BalanceCache                    cache;
	private Account                         account;
	private HashMap<String, AccountService> accountServices;

	private Calendar earliestDate;

	public GraphMediator( final Calendar earliestDate ) {
		this.earliestDate = earliestDate;
	}

	public GraphMediator() {
	}

	public List<BalanceVO> getBalances( final Calendar fromDate, final Calendar toDate ) {
		Calendar last = cache.lastAvailableDateOrNull();
		if( last == null )
			loadBalancesToCache( getEarliestCacheDate(), toDate );
		else if( last.before( toDate ) )
			loadBalancesToCache( last, toDate );
		return cache.getBalancesBetween( fromDate, toDate );
	}

	private Calendar getEarliestCacheDate() {
		return earliestDate != null ? earliestDate : getDateOnJanuaryOne1970();
	}

	private Calendar getDateOnJanuaryOne1970() {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis( 0 );
		return date;
	}

	private void loadBalancesToCache( final Calendar fromDate, final Calendar toDate ) {
		account.setInitialBalance( getInitialBalance( fromDate ) );
		Calendar from = CalendarCalculator.getSpecifiedDayAfter( fromDate, 1 );
		cache.addBalance( account.getInitialBalance() );
		List<BalanceVO> balanceVOList = getBalancesFromService( from, toDate );
		if( balanceVOList.size()>0 ) {
			cache.addAll( getBalancesFromService( from, toDate ) );
			cache.saveBalancesInDataBase();
		}
	}

	private BalanceVO getInitialBalance( final Calendar fromDate ) {
		BalanceVO balance = new BalanceVO( 0.0, fromDate );
		for( String organization : accountServices.keySet() ) {
			AccountService service = accountServices.get( organization );
			balance.addAll( service.getBalancesForDate( fromDate, organization ) );
		}
		return balance;
	}

	private List<BalanceVO> getBalancesFromService( final Calendar from, final Calendar to ) {
		account.setRecords( getRecordsForAllOrganizationsFromService( from, to ) );
		List<BalanceVO> displayBalances = account.getBalancesBetween(
				from,
				to );
		log.info( "Balances:"+displayBalances );
		return displayBalances;
	}

	private List<RecordVO> getRecordsForAllOrganizationsFromService( final Calendar from, final Calendar to ) {
		List<RecordVO> records = new ArrayList<>();
		for( String organization : accountServices.keySet() )
			records.addAll( getRecordsForOrganizationFromService( from, to, organization ) );
		return records;
	}

	private List<RecordVO> getRecordsForOrganizationFromService( final Calendar from, final Calendar to,
																 final String organization ) {
		AccountService service = accountServices.get( organization );
		return service.getAllRecords( from, to, organization );
	}

	public BalanceCache getCache() {
		return cache;
	}

	public void setCache( final BalanceCache cache ) {
		this.cache = cache;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount( final Account account ) {
		this.account = account;
	}

	public HashMap<String, AccountService> getAccountServices() {
		return accountServices;
	}

	public void setAccountServices( final HashMap<String, AccountService> accountServices ) {
		this.accountServices = accountServices;
	}

	public Calendar getEarliestDate() {
		return earliestDate;
	}
}
