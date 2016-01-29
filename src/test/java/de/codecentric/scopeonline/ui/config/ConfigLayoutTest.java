package de.codecentric.scopeonline.ui.config;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static de.codecentric.scopeonline.ui.util.VaadinAssert.assertComponentIsPresent;
import static de.codecentric.scopeonline.ui.util.VaadinAssert.assertError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigLayoutTest {
	private ConfigLayout   layout;
	@Mock
	private ConfigDelegate delegate;

	@Before
	public void setUp() throws Exception {
		initMocks( this );
		layout = new ConfigLayout();
		layout.setDelegate( delegate );
		layout.init();
	}

	@Test
	public void givenNewConfigLayout_baseLineFieldIsPresent() throws Exception {
		TextField baseLineField = layout.getBaseLineField();
		assertComponentIsPresent( layout, baseLineField );
		assertEquals( "BaseLine", baseLineField.getCaption() );
	}

	@Test
	public void givenNewConfigLayout_saveButtonIsPresent() throws Exception {
		Button saveButton = layout.getSaveButton();
		assertComponentIsPresent( layout, saveButton );
		assertEquals( "Save", saveButton.getCaption() );
	}

	@Test
	public void givenNoInputValue_saveFails() throws Exception {
		clickSaveButton();
		assertError( layout, "BaseLine needs a value." );
	}

	private void clickSaveButton() {
		Button saveButton = layout.getSaveButton();
		saveButton.click();
	}

	@Test
	public void givenNonNumberInputValue_saveFails() throws Exception {
		layout.getBaseLineField().setValue( "Buxtehude.45645" );
		clickSaveButton();
		assertError( layout, "Please enter a number value." );
	}

	@Test
	public void givenNumberInputValue_saveSucceeds() throws Exception {
		layout.getBaseLineField().setValue( "1.0" );
		clickSaveButton();
		assertNull( layout.getErrorMessage() );
		verify( delegate ).saveBaseLine( "1.0" );
	}

	@Test
	public void givenNewConfigLayout_whenDestroyed_shouldRemoveAllComponents() throws Exception {
		layout.destroy();
		assertEquals( 0, layout.getComponentCount() );
		assertNull( layout.getBaseLineField() );
		assertNull( layout.getSaveButton() );
	}

	@Test
	public void givenNewConfigLayout_whenDestroyed_shouldClearDelegate() throws Exception {
		layout.destroy();
		assertNull( layout.getDelegate() );
	}

	@After
	public void tearDown() throws Exception {
		layout = null;
	}
} 