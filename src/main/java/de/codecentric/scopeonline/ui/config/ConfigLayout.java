package de.codecentric.scopeonline.ui.config;

import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ConfigLayout extends VerticalLayout {
	private TextField      baseLineField;
	private Button         saveButton;
	private ConfigDelegate delegate;

	public TextField getBaseLineField() {
		return baseLineField;
	}

	public void init() {
		initBaseLineField();
		initSaveButton();
	}

	private void initSaveButton() {
		saveButton = new Button();
		saveButton.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( final ClickEvent event ) {
				performSave();
			}
		} );
		addComponent( saveButton );
		saveButton.setCaption( "Save" );

	}

	private void performSave() {
		String value = baseLineField.getValue();
		if( value == null || value.equals( "" ) )
			showInvalidInputError( "BaseLine needs a value." );
		else if( !inputIsANumber() )
			showInvalidInputError( "Please enter a number value." );
		else delegate.saveBaseLine( value );
	}

	private boolean inputIsANumber() {
		String value = baseLineField.getValue();
		return value.matches( "([0-9]+)(\\.[0-9]+)?" );
	}

	private void showInvalidInputError( final String message ) {
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

	private void initBaseLineField() {
		baseLineField = new TextField();
		addComponent( baseLineField );
		baseLineField.setCaption( "BaseLine" );
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public void setDelegate( final ConfigDelegate delegate ) {
		this.delegate = delegate;
	}

	public void destroy() {
		removeAllComponents();
		delegate = null;
		saveButton = null;
		baseLineField = null;
	}

	public ConfigDelegate getDelegate() {
		return delegate;
	}
}
