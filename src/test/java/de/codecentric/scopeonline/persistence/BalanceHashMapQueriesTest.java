package de.codecentric.scopeonline.persistence;

import de.codecentric.scopeonline.data.BalanceVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BalanceHashMapQueriesTest {

	BalanceHashMapQueries balanceHashMapQueries;
	EntityManagerFactory  emf;

	private void fillDataBaseWithOneYearOfBalances() {
		Calendar calendar = Calendar.getInstance();
		calendar.set( 2011, Calendar.JANUARY, 1 );
		EntityManager em = startDatabaseTransaction();
		int i = 0;
		while( i<365 ) {
			em.persist( new BalanceVO( new BigDecimal( i++ ), calendar ) );
			calendar.add( Calendar.DAY_OF_YEAR, 1 );
		}
		commitDatabaseTransaction( em );
	}

	private void insertObjectInDB( final Object object ) {
		EntityManager em = startDatabaseTransaction();
		em.persist( object );
		commitDatabaseTransaction( em );
	}

	private EntityManager startDatabaseTransaction() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		return em;
	}

	private void commitDatabaseTransaction( final EntityManager em ) {
		em.getTransaction().commit();
		em.close();
	}

	@Before
	public void setUp() throws Exception {
		emf = Persistence.createEntityManagerFactory( "scopeonlinetest" );
		balanceHashMapQueries = new BalanceHashMapQueriesImpl( emf );
	}

	@Test
	public void givenEmptyDataBase_whenAskedForBalances_shouldReturnEmptyList() throws Exception {
		HashMap balances = balanceHashMapQueries.getBalanceHashMap();
		assertEquals( 0, balances.size() );
	}


	@Test
	public void givenDataBaseWithOneBalance_whenAskedForBalances_shouldReturnHashMapWithOneBalance() throws Exception {
		BalanceVO expected = new BalanceVO( BigDecimal.ONE, Calendar.getInstance() );
		insertObjectInDB( expected );
		HashMap<String, BalanceVO> balances = balanceHashMapQueries.getBalanceHashMap();
		assertEquals( 1, balances.size() );
		BalanceVO actual = balances.values().iterator().next();
		assertEquals( expected.date, actual.date );
		assertEquals( 0, actual.amount.compareTo( expected.amount ) );
	}

	@Test
	public void givenEmptyDatabase_whenBalanceMapAdded_shouldInsertIntoDatabase() throws Exception {
		HashMap<String, BalanceVO> balanceVOHashMap = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		calendar.set( 2011, Calendar.JANUARY, 1 );
		int i = 0;
		while( i<365 ) {
			BalanceVO balanceVO = new BalanceVO( new BigDecimal( i++ ), calendar );
			balanceVOHashMap.put( balanceVO.dateAsString, balanceVO );
			calendar.add( Calendar.DAY_OF_YEAR, 1 );
		}
		balanceHashMapQueries.insertBalanceHashMap( balanceVOHashMap );
		EntityManager em = emf.createEntityManager();
		List balances = em.createQuery( "select b from BalanceVO b" ).getResultList();
		assertEquals( balances.size(), balanceVOHashMap.size() );
		for (Object object : balances)                {
			BalanceVO actual = (BalanceVO) object;
			BalanceVO expected = balanceVOHashMap.get( actual.dateAsString );
			assertEquals(0, expected.amount.compareTo( actual.amount ));
			assertEquals( expected.date.get( Calendar.YEAR ), actual.date.get( Calendar.YEAR ) );
			assertEquals( expected.date.get( Calendar.DAY_OF_YEAR ), actual.date.get( Calendar.DAY_OF_YEAR ) );
		}
	}

	@Test
	public void givenDatabaseWithManyBalances_whenAskedForBalances_shouldReturnHashMapWithBalances() throws Exception {
		fillDataBaseWithOneYearOfBalances();
		Map<String, BalanceVO> balances = balanceHashMapQueries.getBalanceHashMap();
		Calendar calendar = Calendar.getInstance();
		calendar.set( 2011, Calendar.JANUARY, 1 );
		int i = 0;
		while( i<365 ) {
			BalanceVO expected = new BalanceVO( new BigDecimal( i++ ), calendar );
			BalanceVO actual = balances.get( expected.dateAsString );
			assertEquals( 0, expected.amount.compareTo( actual.amount ) );
			assertEquals( expected.date.get( Calendar.YEAR ), actual.date.get( Calendar.YEAR ) );
			assertEquals( expected.date.get( Calendar.DAY_OF_YEAR ), actual.date.get( Calendar.DAY_OF_YEAR ) );
			calendar.add( Calendar.DAY_OF_YEAR, 1 );
		}
	}

	@After
	public void tearDown() throws Exception {
		emf.close();
		balanceHashMapQueries = null;
	}
}
