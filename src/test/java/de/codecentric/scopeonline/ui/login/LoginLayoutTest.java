package de.codecentric.scopeonline.ui.login;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import de.codecentric.scopeonline.service.ConfigService;
import de.codecentric.scopeonline.service.LoginService;
import de.codecentric.scopeonline.service.error.UserUnknownException;
import de.codecentric.scopeonline.service.error.WrongPasswordException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.List;

import static de.codecentric.scopeonline.ui.util.VaadinAssert.assertComponentIsPresent;
import static de.codecentric.scopeonline.ui.util.VaadinAssert.assertError;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LoginLayoutTest {
	private LoginLayout   layout;
	private LoginService  service;
	private LoginDelegate delegate;
	private ConfigService configService;

	@Before
	public void setUp() throws Exception {
		configService = mock( ConfigService.class );
		List<String> organizations = new ArrayList<>();
		organizations.add( "Scopevisio Demo AG" );
		when( configService.getOrganizations() ).thenReturn( organizations );
		layout = new LoginLayout( configService );
		layout.init();

		service = mock( LoginService.class );
		layout.setLoginService( service );

		delegate = mock( LoginDelegate.class );
		layout.setDelegate( delegate );
	}

	private void loginWithNameAndPassword( final String name, final String pass ) {
		layout.getNameField().setValue( name );
		layout.getPasswordField().setValue( pass );
		layout.getLoginButton().click();
	}


	@Test
	public void givenNewLoginLayout_nameFieldIsPresent() throws Exception {
		TextField nameField = layout.getNameField();
		assertComponentIsPresent( layout, nameField );
		assertEquals( "User name", nameField.getCaption() );
	}

	@Test
	public void givenNewLoginLayout_passwordFieldIsPresent() throws Exception {
		PasswordField passwordField = layout.getPasswordField();
		assertComponentIsPresent( layout, passwordField );
		assertEquals( "Password", passwordField.getCaption() );
	}

	@Test
	public void givenNewLoginLayout_loginButtonIsPresent() throws Exception {
		Button loginButton = layout.getLoginButton();
		assertComponentIsPresent( layout, loginButton );
		assertEquals( Alignment.TOP_RIGHT, layout.getComponentAlignment( loginButton ) );
	}

	@Test
	public void givenNewLoginLayout_loginServiceIsPresent() throws Exception {
		assertNotNull( layout.getLoginService() );
	}

	@Test
	public void givenNewLoginLayout_displayAttributesAreSet() throws Exception {
		assertEquals( Alignment.TOP_LEFT, layout.getDefaultComponentAlignment() );
		assertEquals( "Welcome to ScopeOnline", layout.getCaption() );
	}

	@Test
	public void givenNewLoginLayout_whenLayoutIsDestroyed_componentsAreRemoved() throws Exception {
		layout.destroy();
		assertEquals( 0, layout.getComponentCount() );
		assertNull( layout.getNameField() );
		assertNull( layout.getPasswordField() );
		assertNull( layout.getLoginButton() );
		assertNull( layout.getLoginService() );
	}

	@Test
	public void givenNoUserInput_whenLoginButtonIsPressed_loginFails() throws Exception {
		layout.getLoginButton().click();
		assertError( layout, "Invalid input." );
	}

	@Test
	public void givenValidUserInput_whenLoginButtonIsPressed_loginSucceeds() throws Exception {
		when( service.login( "known", "correctpassword", "Scopevisio Demo AG" ) ).thenReturn( new Subject() );
		loginWithNameAndPassword( "known", "correctpassword" );
		assertNull( layout.getErrorMessage() );

		verify( delegate ).loginPass( any( Subject.class ) );
	}

	@Test
	public void givenValidUserInput_whenLoginButtonIsPressedAndNoSubjectIsReturnedFromService_loginFails() throws
																										   Exception {
		when( service.login( "known", "correctpassword", "Scopevisio Demo AG" ) ).thenReturn( null );
		loginWithNameAndPassword( "known", "correctpassword" );
		assertError( layout, "Security failure: No Subject present." );
	}


	@Test
	public void givenUserUnknown_whenLoginButtonIsPressed_loginFails() throws Exception {
		when( service.login( "unknown", "somepassword", "Scopevisio Demo AG" ) )
				.thenThrow( new UserUnknownException() );
		loginWithNameAndPassword( "unknown", "somepassword" );
		assertError( layout, "Username unknown." );
	}

	@Test
	public void givenWrongPassword_whenLoginButtonIsPressed_loginFails() throws Exception {
		when( service.login( "known", "wrong", "Scopevisio Demo AG" ) ).thenThrow( new WrongPasswordException() );
		loginWithNameAndPassword( "known", "wrong" );
		assertError( layout, "Wrong password." );
	}

	@After
	public void tearDown() throws Exception {
		layout = null;
	}
}

