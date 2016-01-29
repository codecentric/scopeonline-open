package de.codecentric.scopeonline.service.error;

public class WrongPasswordException extends RuntimeException {
	public WrongPasswordException() {
		super( "Wrong password." );
	}

	public WrongPasswordException( final String message ) {
		super( message );
	}

	public WrongPasswordException( final String message, final Throwable cause ) {
		super( message, cause );
	}

	public WrongPasswordException( final Throwable cause ) {
		super( cause );
	}
}
