package de.codecentric.scopeonline.service;

import com.sun.security.auth.UserPrincipal;
import de.codecentric.scopeonline.rpc.SOAPAdapter;
import de.codecentric.scopeonline.rpc.SOAPRequestImpl;
import de.codecentric.scopeonline.service.error.UserUnknownException;
import de.codecentric.scopeonline.service.error.WrongPasswordException;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Set;

public class LoginServiceImpl implements LoginService {

	private ConfigService configService;
	private SOAPAdapter   soapAdapter;

	@Override
	public Subject login( final String username, final String password, final String organization )
			throws UserUnknownException, WrongPasswordException {
		Subject subject = buildSubject( username, password, organization );
		String customer = configService.getCustomer();

		if( !configService.isUserValid( username ) )
			throw new UserUnknownException();
		else {

			if( !getSoapAdapter( username, password, organization, customer ).areUserCredentialsValid() )
				throw new WrongPasswordException();
		}
		return subject;
	}

	private Subject buildSubject( final String username, final String password, final String organization ) {
		Principal principal = new UserPrincipal( "test" );
		Set<Principal> principals = new LinkedHashSet<>();
		principals.add( principal );

		Set<String> pubCredentials = new LinkedHashSet<>();
		pubCredentials.add( username );
		pubCredentials.add( password );
		pubCredentials.add( organization );
		pubCredentials.add( configService.getCustomer() );

		Set<String> privCredentials = new LinkedHashSet<>();
		privCredentials.add( username );
		privCredentials.add( password );
		privCredentials.add( organization );
		privCredentials.add( configService.getCustomer() );

		return new Subject( true, principals, pubCredentials, privCredentials );
	}

	public void setConfigService( final ConfigService configService ) {
		this.configService = configService;
	}

	public SOAPAdapter getSoapAdapter( String username, String password, final String organization,
									   final String customer ) {
		if( soapAdapter == null ) {
			soapAdapter = new SOAPAdapter( configService );
			soapAdapter.setSoapRequest( new SOAPRequestImpl( username, password, organization, customer ) );
		}
		return soapAdapter;
	}

	public void setSoapAdapter( final SOAPAdapter soapAdapter ) {
		this.soapAdapter = soapAdapter;
	}


}
