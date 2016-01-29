package de.codecentric.scopeonline.service;

import de.codecentric.scopeonline.service.error.UserUnknownException;
import de.codecentric.scopeonline.service.error.WrongPasswordException;

import javax.security.auth.Subject;

public interface LoginService {
	Subject login( final String username, final String password, final String organization ) throws
																							 UserUnknownException,
																							 WrongPasswordException;
}
