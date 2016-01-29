package de.codecentric.scopeonline.service.error;

public class UserUnknownException extends RuntimeException {
	public UserUnknownException() {
		super( "Username unknown." );
	}

	public UserUnknownException( final String message ) {
		super( message );
	}

	public UserUnknownException( final String message, final Throwable cause ) {
		super( message, cause );
	}

	public UserUnknownException( final Throwable cause ) {
		super( cause );
	}
}
