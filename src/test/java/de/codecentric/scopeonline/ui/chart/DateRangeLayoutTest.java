package de.codecentric.scopeonline.ui.chart;

import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import de.codecentric.scopeonline.util.CalendarCalculator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Calendar;

import static de.codecentric.scopeonline.ui.util.VaadinAssert.assertComponentIsPresent;
import static de.codecentric.scopeonline.ui.util.VaadinAssert.assertError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DateRangeLayoutTest {
	private DateRangeLayout layout;

	@Mock
	private DateRangeDelegate delegate;

	private final Calendar EARLIESTDATE = Calendar.getInstance();

	@Before
	public void setUp() throws Exception {
		initMocks( this );
		EARLIESTDATE.set( 2011, Calendar.JANUARY, 1 );
		layout = new DateRangeLayout( EARLIESTDATE );
		layout.setDelegate( delegate );
		layout.init();
	}

	private void assertGermanDateFormat( final DateField datePicker ) {
		assertEquals( "dd.MM.yyyy", datePicker.getDateFormat() );
	}

	private Calendar getToday() {
		return Calendar.getInstance();
	}

	private Calendar getYesterday() {
		return CalendarCalculator.getSpecifiedDayAfter( getToday(), -1 );
	}

	private Calendar getTomorrow() {
		return CalendarCalculator.getSpecifiedDayAfter( getToday(), 1 );
	}

	private Calendar getDayBeforeEarliestDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( EARLIESTDATE.getTime() );
		calendar.add( Calendar.DAY_OF_YEAR, -1 );
		return calendar;
	}

	@Test
	public void givenNewDateRangeLayout_shouldHaveCaption() throws Exception {
		assertEquals( "Display account balances", layout.getCaption() );

	}

	@Test
	public void givenNewDateRangeLayout_shouldCreateSubmitButton() throws Exception {
		Button submitButton = layout.getSubmitButton();
		assertComponentIsPresent( layout, submitButton );
		assertEquals( "Show", submitButton.getCaption() );
	}

	@Test
	public void givenNewDateRangeLayout_shouldCreateFromDatePicker() throws Exception {
		DateField fromDatePicker = layout.getFromDatePicker();
		assertComponentIsPresent( layout, fromDatePicker );
		assertEquals( "From:", fromDatePicker.getCaption() );
	}

	@Test
	public void givenNewDateRangeLayout_shouldCreateToDatePicker() throws Exception {
		DateField toDatePicker = layout.getToDatePicker();
		assertComponentIsPresent( layout, toDatePicker );
		assertEquals( "To:", toDatePicker.getCaption() );
	}

	@Test
	public void givenNewDateRangeLayout_datePickersShouldHaveGermanDateFormat() throws Exception {
		assertGermanDateFormat( layout.getFromDatePicker() );
		assertGermanDateFormat( layout.getToDatePicker() );
	}

	@Test
	public void givenNewDateRangeLayout_whenSettingRange_datePickersShouldReceiveCorrectValues() throws Exception {
		Calendar today = getToday();
		Calendar yesterday = getYesterday();
		layout.setRange( yesterday, today );
		assertEquals( yesterday.getTime(), layout.getFromDatePicker().getValue() );
		assertEquals( today.getTime(), layout.getToDatePicker().getValue() );
	}

	@Test
	public void givenNoDateInput_whenSubmittingRange_showsErrorMessage() throws Exception {
		layout.getSubmitButton().click();
		assertError( layout, "Please enter valid dates." );
	}

	@Test
	public void givenDateInput_whenSubmittingRange_submitSucceeds() throws Exception {
		layout.setRange( Calendar.getInstance(), Calendar.getInstance() );
		layout.getSubmitButton().click();
		assertNull( layout.getErrorMessage() );
		verify( delegate ).setDateRange( CalendarCalculator.createWithDate( layout.getFromDatePicker().getValue() ),
										 CalendarCalculator.createWithDate( layout.getToDatePicker().getValue() ) );
	}

	@Test
	public void givenFromDateAfterToDate_whenSubmittingRange_submitSucceedsWithReversedValues() throws Exception {
		Calendar today = getToday();
		Calendar yesterday = getYesterday();
		layout.setRange( today,
						 yesterday );
		layout.getSubmitButton().click();
		assertNull( layout.getErrorMessage() );
		verify( delegate ).setDateRange( yesterday,
										 today );
	}

	@Test
	public void givenFromDateBeforeEarliestDate_whenSubmittingRange_submitSucceedsWithFromDateEqualsEarliestDate()
			throws Exception {
		Calendar today = getToday();
		Calendar dayBeforeEarliestDate = getDayBeforeEarliestDate();

		layout.setRange( dayBeforeEarliestDate, today );
		layout.getSubmitButton().click();

		assertNull( layout.getErrorMessage() );
		verify( delegate ).setDateRange( EARLIESTDATE, today );
	}


	@Test
	public void givenToDateAfterToday_whenSubmittingRange_submitSucceedsWithToDateEqualsToday() throws Exception {
		Calendar today = getToday();
		Calendar tomorrow = getTomorrow();

		layout.setRange( today, tomorrow );
		layout.getSubmitButton().click();

		assertNull( layout.getErrorMessage() );
		verify( delegate ).setDateRange( any( Calendar.class ), any( Calendar.class ) );
		Calendar actual = Calendar.getInstance();
		actual.setTime( layout.getToDatePicker().getValue() );
		assertEquals( today.get( Calendar.YEAR ), actual.get( Calendar.YEAR ) );
		assertEquals( today.get( Calendar.DAY_OF_YEAR ), actual.get( Calendar.DAY_OF_YEAR ) );
	}

	@Test
	public void givenToDateBeforeEarliestDate_whenSubmittingRange_submitSucceedsWithFromDateEqualsEarliestDate()
			throws Exception {
		Calendar today = getToday();
		Calendar dayBeforeEarliestDate = getDayBeforeEarliestDate();

		layout.setRange( today, dayBeforeEarliestDate );
		layout.getSubmitButton().click();

		assertNull( layout.getErrorMessage() );
		verify( delegate ).setDateRange( EARLIESTDATE, today );
	}

	@Test
	public void givenNewDateRangeLayout_whenDestroyed_shouldRemoveAllComponents() throws Exception {
		layout.destroy();
		assertNull( layout.getFromDatePicker() );
		assertNull( layout.getToDatePicker() );
		assertNull( layout.getSubmitButton() );
		assertNull( layout.getDelegate() );

	}

	@After
	public void tearDown() throws Exception {
		layout = null;
	}
} 