package de.codecentric.scopeonline.ui.chart;

import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import de.codecentric.scopeonline.util.CalendarCalculator;

import java.util.Calendar;

public class DateRangeLayout extends HorizontalLayout {
	private DateField fromDatePicker;
	private DateField toDatePicker;
	private Button    submitButton;

	private Calendar earliestDate;

	private DateRangeDelegate delegate;

	public DateRangeLayout( Calendar earliestDate ) {
		this.earliestDate = earliestDate;
	}

	public void init() {
		setCaption( "Display account balances" );
		initFromDatePicker();
		initToDatePicker();
		initSubmitButton();
	}

	private void initFromDatePicker() {
		fromDatePicker = new DateField( "From:" );
		fromDatePicker.setDateFormat( "dd.MM.yyyy" );
		addComponent( fromDatePicker );
	}

	private void initToDatePicker() {
		toDatePicker = new DateField( "To:" );
		toDatePicker.setDateFormat( "dd.MM.yyyy" );
		addComponent( toDatePicker );
	}

	private void initSubmitButton() {
		submitButton = new Button();
		submitButton.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( final ClickEvent event ) {
				submitDateRange();
			}
		} );
		submitButton.setCaption( "Show" );
		addComponent( submitButton );
	}

	private void submitDateRange() {
		Calendar fromDate = getDateFromPicker( fromDatePicker );
		Calendar toDate = getDateFromPicker( toDatePicker );
		if( fromDate == null || toDate == null )
			fail( "Please enter valid dates." );
		else if( fromDate.before( earliestDate ) ) {
			fromDatePicker.setValue( earliestDate.getTime() );
			submitDateRange();
		} else if( toDate.after( Calendar.getInstance() ) ) {
			toDatePicker.setValue( Calendar.getInstance().getTime() );
			submitDateRange();
		} else if( fromDate.after( toDate ) ) {
			reverseDateValues( fromDate, toDate );
			submitDateRange();
		} else
			delegate.setDateRange( fromDate, toDate );
	}

	private void reverseDateValues( final Calendar fromDate, final Calendar toDate ) {
		fromDatePicker.setValue( toDate.getTime() );
		toDatePicker.setValue( fromDate.getTime() );
	}

	private Calendar getDateFromPicker( final DateField datePicker ) {
		return datePicker.getValue() != null
			   ? CalendarCalculator.createWithDate( datePicker.getValue() )
			   : null;
	}


	private void fail( final String message ) {
		setComponentError( new ErrorMessage() {
			@Override
			public ErrorLevel getErrorLevel() {
				return ErrorLevel.ERROR;
			}

			@Override
			public String getFormattedHtmlMessage() {
				return message;
			}
		} );
	}


	public void destroy() {
		removeAllComponents();
		fromDatePicker = null;
		toDatePicker = null;
		submitButton = null;
		delegate = null;
	}

	public DateField getFromDatePicker() {
		return fromDatePicker;
	}


	public DateField getToDatePicker() {
		return toDatePicker;
	}

	public void setRange( final Calendar fromDate, final Calendar toDate ) {
		fromDatePicker.setValue( fromDate.getTime() );
		toDatePicker.setValue( toDate.getTime() );
	}

	public Button getSubmitButton() {
		return submitButton;
	}

	public DateRangeDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate( final DateRangeDelegate delegate ) {
		this.delegate = delegate;
	}

}
