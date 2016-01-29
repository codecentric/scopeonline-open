package de.codecentric.scopeonline.ui.login;

import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import de.codecentric.scopeonline.service.ConfigService;
import de.codecentric.scopeonline.service.LoginService;
import de.codecentric.scopeonline.service.error.UserUnknownException;
import de.codecentric.scopeonline.service.error.WrongPasswordException;

import javax.security.auth.Subject;

public class LoginLayout extends VerticalLayout {

	private TextField     nameField;
	private PasswordField passwordField;
	private Button        loginButton;
	private LoginService  loginService;
	private LoginDelegate delegate;
	private ConfigService configService;

	public LoginLayout( ConfigService configService ) {
		this.configService = configService;
		setHeight( "200px" );
		setWidth( "200px" );
		setCaption( "Welcome to ScopeOnline" );
		setDefaultComponentAlignment( Alignment.TOP_LEFT );
		setSpacing( true );
	}

	public void init() {
		initNameField();
		initPasswordField();
		initLoginButton();
	}

	private void initNameField() {
		nameField = new TextField();
		nameField.setCaption( "User name" );
		nameField.setWidth( getWidth(), Unit.PIXELS );
		addComponent( nameField );
	}

	private void initPasswordField() {
		passwordField = new PasswordField();
		passwordField.setCaption( "Password" );
		passwordField.setWidth( getWidth(), Unit.PIXELS );
		addComponent( passwordField );
	}

	private void initLoginButton() {
		loginButton = new Button( "Login" );
		loginButton.addClickListener( new Button.ClickListener() {
			public void buttonClick( ClickEvent event ) {
				submitLoginForm();
			}
		} );
		addComponent( loginButton );
		setComponentAlignment( loginButton, Alignment.TOP_RIGHT );
	}

	private void submitLoginForm() {
		String userName = nameField.getValue();
		String password = passwordField.getValue();
		String organization = configService.getOrganizations().get( 0 );
		if( userName == null || userName.equals( "" ) || password == null || password.equals( "" ) )
			fail( "Invalid input." );
		else
			performLogin( userName, password, organization );
	}

	private void performLogin( final String userName, final String password, final String organization ) {
		try {
			Subject subject = loginService.login( userName, password, organization );
			if( subject == null )
				fail( "Security failure: No Subject present." );
			else
				pass( subject );
		} catch( final UserUnknownException|WrongPasswordException e ) {
			fail( e.getMessage() );
		}
	}

	private void pass( final Subject subject ) {
		delegate.loginPass( subject );
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

	public TextField getNameField() {
		return nameField;
	}

	public PasswordField getPasswordField() {
		return passwordField;
	}

	public Button getLoginButton() {
		return loginButton;
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService( final LoginService loginService ) {
		this.loginService = loginService;
	}

	public void destroy() {
		removeAllComponents();
		nameField = null;
		passwordField = null;
		loginButton = null;
		loginService = null;
	}

	public void setDelegate( final LoginDelegate delegate ) {
		this.delegate = delegate;
	}

}
