package de.codecentric.scopeonline.domain;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;
import de.codecentric.scopeonline.util.CalendarCalculator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AccumulatorTest {
	private Accumulator accumulator;

	@Before
	public void setUp() throws Exception {
		accumulator = new AccumulatorImpl();
	}

	private List<RecordVO> createRecordsWithAmounts( double... amounts ) {
		List<RecordVO> input = new ArrayList<>();
		Calendar instance = Calendar.getInstance();
		instance.set( Calendar.MILLISECOND, 0 );
		addRecordsWithAmountsOnDate( input, amounts, instance );
		return input;
	}

	private void addRecordsWithAmountsOnDate( final List<RecordVO> input, final double[] amounts,
											  final Calendar date ) {
		for( double amount : amounts )
			input.add( new RecordVO( new BigDecimal( amount ), date ) );
	}

	@Test
	public void passingNullReturnsSingleBalanceOfZero() throws Exception {
		assertEquals( new BigDecimal( 0 ), accumulator.accumulate( null ).get( 0 ).amount );
	}

	@Test
	public void passingEmptyListReturnsSingleBalanceOfZero() throws Exception {
		assertEquals( new BigDecimal( 0 ), accumulator.accumulate( null ).get( 0 ).amount );
	}

	@Test
	public void passingSingleRecordOfZeroReturnsSingleBalanceOfZero() throws Exception {
		List<BalanceVO> output = accumulator.accumulate( createRecordsWithAmounts( 0.0 ) );
		assertEquals( new BigDecimal( 0 ), output.get( 0 ).amount );
	}

	@Test
	public void passingSingleRecordOfOneReturnsSingleBalanceOfOne() throws Exception {
		List<BalanceVO> output = accumulator.accumulate( createRecordsWithAmounts( 1.0 ) );
		assertEquals( new BigDecimal( 1 ), output.get( 0 ).amount );
	}

	@Test
	public void passingTwoRecordsOfOneReturnsSingleBalanceOfTwo() throws Exception {
		List<RecordVO> input = createRecordsWithAmounts( 1.0, 1.0 );
		List<BalanceVO> output = accumulator.accumulate( input );
		assertEquals( new BigDecimal( 2 ), output.get( 0 ).amount );
	}

	@Test
	public void passingSingleRecordOfOneToInitialBalanceOfOneReturnsSingleBalanceOfTwo() throws Exception {
		accumulator.setInitialBalance( new BalanceVO( new BigDecimal( 1 ) ) );
		List<RecordVO> input = createRecordsWithAmounts( 1.0 );
		List<BalanceVO> output = accumulator.accumulate( input );
		assertEquals( new BigDecimal( 2 ), output.get( 0 ).amount );
		assertEquals( new BigDecimal( 1 ), accumulator.getInitialBalance().amount );
	}

	@Test
	public void passingSingleRecordOfOneToInitialBalanceOfOneReturnsSingleBalanceOfTwoForOneDay() throws Exception {
		accumulator.setInitialBalance( new BalanceVO( new BigDecimal( 1 ) ) );
		List<RecordVO> input = createRecordsWithAmounts( 1.0 );
		Calendar todayAtMidnight = CalendarCalculator.getDayAtMidnight( Calendar.getInstance() );
		List<BalanceVO> output = accumulator.accumulate( input );
		assertEquals( new BigDecimal( 2 ), output.get( 0 ).amount );
		Assert.assertEquals( todayAtMidnight, output.get( 0 ).date );
	}


	@Test
	public void passingTwoRecordsOfOneOnDifferentDaysReturnsDifferentBalanceForTwoDays() throws Exception {
		accumulator.setInitialBalance( new BalanceVO( new BigDecimal( 1 ) ) );
		List<RecordVO> input = new ArrayList<>();

		Calendar calendar = createCalendar( 2014, Calendar.JANUARY, 15 );
		addRecordsWithAmountsOnDate( input, new double[]{ 1.0 }, calendar );

		calendar = Calendar.getInstance();
		calendar.set( 2014, Calendar.JANUARY, 16 );
		addRecordsWithAmountsOnDate( input, new double[]{ 1.0 }, calendar );

		List<BalanceVO> output = accumulator.accumulate( input );
		assertEquals( new BigDecimal( 2 ), output.get( 0 ).amount );
		assertEquals( new BigDecimal( 3 ), output.get( 1 ).amount );
	}

	@Test
	public void passingThreeRecordsOfOneOnTwoDifferentDaysUnsortedReturnsDifferentBalanceForTwoDays() throws Exception {
		accumulator.setInitialBalance( new BalanceVO( new BigDecimal( 1 ) ) );
		List<RecordVO> input = new ArrayList<>();

		Calendar calendar = createCalendar( 2014, Calendar.JANUARY, 15 );
		addRecordsWithAmountsOnDate( input, new double[]{ 1.0 }, calendar );

		calendar = Calendar.getInstance();
		calendar.set( 2014, Calendar.JANUARY, 16 );
		addRecordsWithAmountsOnDate( input, new double[]{ 1.0 }, calendar );

		calendar = Calendar.getInstance();
		calendar.set( 2014, Calendar.JANUARY, 15 );
		addRecordsWithAmountsOnDate( input, new double[]{ 1.0 }, calendar );

		List<BalanceVO> output = accumulator.accumulate( input );
		assertEquals( new BigDecimal( 3 ), output.get( 0 ).amount );
		assertEquals( new BigDecimal( 4 ), output.get( 1 ).amount );
	}

	private Calendar createCalendar( final int year, final int month, final int day ) {
		Calendar calendar = Calendar.getInstance();
		calendar.set( year, month, day );
		return calendar;
	}


	@After
	public void tearDown() throws Exception {
		accumulator = null;
	}

	private void assertEquals( final BigDecimal expected, final BigDecimal actual ) {
		Assert.assertEquals( 0, expected.compareTo( actual ) );
	}
}