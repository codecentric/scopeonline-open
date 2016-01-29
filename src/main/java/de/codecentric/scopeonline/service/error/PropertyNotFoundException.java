package de.codecentric.scopeonline.service.error;

public class PropertyNotFoundException extends RuntimeException {
	public PropertyNotFoundException() {
		super( "Property not found." );
	}

	public PropertyNotFoundException( final String message ) {
		super( message );
	}

	public PropertyNotFoundException( final String message, final Throwable cause ) {
		super( message, cause );
	}

	public PropertyNotFoundException( final Throwable cause ) {
		super( cause );
	}
}
