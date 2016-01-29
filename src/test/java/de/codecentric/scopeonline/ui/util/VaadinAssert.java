package de.codecentric.scopeonline.ui.util;

import com.vaadin.server.ErrorMessage;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;

import static org.junit.Assert.assertEquals;

public class VaadinAssert {
	public static void assertComponentIsPresent( final AbstractOrderedLayout container, final Component component ) {
		int index = container.getComponentIndex( component );
		assertEquals( component, container.getComponent( index ) );
	}

	public static void assertError( final AbstractComponent component, final String message ) {
		ErrorMessage errorMessage = component.getErrorMessage();
		assertEquals( ErrorLevel.ERROR, errorMessage.getErrorLevel() );
		assertEquals( message, errorMessage.getFormattedHtmlMessage() );
	}
}
