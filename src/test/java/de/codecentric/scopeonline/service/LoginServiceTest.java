package de.codecentric.scopeonline.service;

import com.sun.security.auth.UserPrincipal;

import de.codecentric.scopeonline.rpc.SOAPAdapter;
import de.codecentric.scopeonline.service.error.UserUnknownException;
import de.codecentric.scopeonline.service.error.WrongPasswordException;
import de.codecentric.scopeonline.util.TestConfigService;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.security.auth.Subject;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LoginServiceTest {

	TestConfigService testConfigService = TestConfigService.getTestConfigService();

	@Mock
	private SOAPAdapter   soapAdapter;
	@Mock
	private ConfigService configService;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@InjectMocks
	private LoginServiceImpl loginServiceImpl;

	private LoginService loginService;

	@Before
	public void setUp() throws Exception {
		initMocks( this );
		loginService = loginServiceImpl;
	}

	private Subject buildDummySubject() {
		Principal principal = new UserPrincipal( "test" );
		Set<Principal> principals = new LinkedHashSet<>();
		principals.add( principal );

		Set<String> pubCredentials = new LinkedHashSet<>();
		pubCredentials.add( testConfigService.getUser() );
		pubCredentials.add( testConfigService.getPass() );
		pubCredentials.add( testConfigService.getOrganization() );
		pubCredentials.add( testConfigService.getCustomer() );

		Set<String> privCredentials = new LinkedHashSet<>();
		privCredentials.add( testConfigService.getUser() );
		privCredentials.add( testConfigService.getPass() );
		privCredentials.add( testConfigService.getOrganization() );
		privCredentials.add( testConfigService.getCustomer() );

		return new Subject( true, principals, pubCredentials, privCredentials );
	}

	@Test
	public void givenNewLoginService_whenInValidUserAndPasswordProvided_ShouldThrowUnknownUserException() throws
																										  Exception {
		when( configService.isUserValid( "unknown" ) ).thenReturn( false );
		expectedException.expect( UserUnknownException.class );
		loginService.login( "unknown", "wrong", testConfigService.getOrganization() );
	}

	@Test
	public void givenNewLoginService_whenValidUserAndInvalidPasswordProvided_ShouldThrowUnknownPasswordException()
			throws Exception {
		when( configService.isUserValid( "known" ) ).thenReturn( true );
		when( soapAdapter.areUserCredentialsValid() ).thenReturn( false );
		expectedException.expect( WrongPasswordException.class );
		loginService.login( "known", "wrong", testConfigService.getOrganization() );
	}

	@Test
	public void givenNewLoginService_whenValidUserAndValidPasswordProvided_ShouldReturnValidSubject() throws Exception {
		when( configService.isUserValid( testConfigService.getUser() ) ).thenReturn( true );
		when( soapAdapter.areUserCredentialsValid() ).thenReturn( true );
		List<String> organizations = new ArrayList<>();
		organizations.add( testConfigService.getOrganization() );
		when( configService.getOrganizations() ).thenReturn( organizations );
		when( configService.getCustomer() ).thenReturn( testConfigService.getCustomer() );
		Subject expected = buildDummySubject();
		Subject actual = loginService.login( testConfigService.getUser(), testConfigService.getPass(),
											 testConfigService.getOrganization() );
		assertEquals( expected, actual );
	}

	@Test
	public void shouldCreateSoapAdapterWithConfigServiceIfNonePresent() throws Exception {
		LoginServiceImpl impl = new LoginServiceImpl();
		impl.setConfigService( configService );
		SOAPAdapter adapter = ( impl.getSoapAdapter( null, null, null, null ) );
		assertEquals( configService, adapter.getConfigService() );
	}

	@After
	public void tearDown() throws Exception {
		loginService = null;
	}
}
