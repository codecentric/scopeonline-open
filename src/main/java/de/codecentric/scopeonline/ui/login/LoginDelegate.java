package de.codecentric.scopeonline.ui.login;

import javax.security.auth.Subject;

public interface LoginDelegate {
	void loginPass(final Subject subject);

	void logOut();
}
