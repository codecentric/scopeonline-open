package de.codecentric.scopeonline.service;

import de.codecentric.scopeonline.service.error.PropertyNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConfigServiceTest {
	private static String ERROR               = "Testing error handling. Please ignore this.";
	private static String VALID_USER_LIST_KEY = "validUserList";
	private static String EXAMPLE_USER        = "john@doe.com";

	@Mock
	Properties properties;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private ConfigService configService;

	@Before
	public void setUp() throws Exception {
		initMocks( this );
		configService = new ConfigServiceImpl( properties );
	}

	@Test
	public void givenPropertyService_whenAddingFirstUser_shouldStoreUserInNewHashSet() throws Exception {
		when( properties.containsKey( VALID_USER_LIST_KEY ) ).thenReturn( false );
		configService.addValidUser( EXAMPLE_USER );
		verify( properties ).setProperty( VALID_USER_LIST_KEY, EXAMPLE_USER );
	}

	@Test
	public void givenPropertyService_whenAddingSecondUser_shouldStoreUserInExistingHashSet() throws Exception {
		when( properties.containsKey( VALID_USER_LIST_KEY ) ).thenReturn( true );
		when( properties.get( VALID_USER_LIST_KEY ) ).thenReturn( EXAMPLE_USER );
		configService.addValidUser( EXAMPLE_USER+";"+EXAMPLE_USER );
	}

	@Test
	public void givenPropertyServiceWithoutUserList_whenAskedIfSpecificUserExists_shouldReturnFalse()
			throws Exception {
		when( properties.containsKey( VALID_USER_LIST_KEY ) ).thenReturn( false );
		boolean testValue = configService.isUserValid( EXAMPLE_USER );
		assertFalse( testValue );
	}

	@Test
	public void givenPropertyServiceWithUserListAndExampleUser_whenAskedIfExampleUserExists_shouldReturnTrue()
			throws Exception {
		when( properties.containsKey( VALID_USER_LIST_KEY ) ).thenReturn( true );
		when( properties.getProperty( VALID_USER_LIST_KEY ) ).thenReturn( EXAMPLE_USER );
		boolean testValue = configService.isUserValid( EXAMPLE_USER );
		assertTrue( testValue );
	}

	@Test
	public void givenPropertyServiceWithUserListAndWithoutExampleUser_whenAskedIfExampleUserExists_ShouldReturnFalse
			() throws Exception {
		when( properties.containsKey( VALID_USER_LIST_KEY ) ).thenReturn( true );
		when( properties.getProperty( VALID_USER_LIST_KEY ) ).thenReturn( "listWithoutExampleUser" );
		boolean testValue = configService.isUserValid( EXAMPLE_USER );
		assertFalse( testValue );
	}

	@Test
	public void givenPropertyService_whenPassedNewProperty_shouldStoreProperty() throws Exception {
		String demoPropertyName = "demoPropertyName";
		String demoPropertyValue = "demoPropertyValue";
		configService.addProperty( demoPropertyName, demoPropertyValue );
		verify( properties ).setProperty( demoPropertyName, demoPropertyValue );
	}

	@Test
	public void givenPropertyServiceWithoutProperties_whenAskedForProperty_shouldReturnNull() throws Exception {
		when( properties.getProperty( anyString() ) ).thenReturn( null );
		String propertyName = "demoPropertyName";
		expectedException.expect( PropertyNotFoundException.class );
		configService.getProperty( propertyName );
	}

	@Test
	public void givenPropertyServiceWithProperties_whenAskedForExistingProperty_shouldReturnValue() throws Exception {
		String propertyName = "validProperty";
		when( properties.getProperty( propertyName ) ).thenReturn( "validValue" );
		assertEquals( "validValue", configService.getProperty( propertyName ) );
	}

	@Test
	public void givenPropertyServiceWithLoadedValues_whenAskedForOrganization_shouldReturnOrganization()
			throws Exception {
		List<String> organizations = new ArrayList<>();
		organizations.add( "Orga1" );
		organizations.add( "Orga2" );
		when( properties.getProperty( "organizations" ) ).thenReturn( "Orga1;Orga2" );
		assertEquals( organizations, configService.getOrganizations() );
	}

	@Test
	public void givenPropertyServiceWithLoadedValues_whenAskedForCustomer_shouldReturnCustomer()
			throws Exception {
		when( properties.getProperty( "customer" ) ).thenReturn( "12345678" );
		assertEquals( "12345678", configService.getCustomer() );
	}

	@Test
	public void givenPropertyServiceWithLoadedValues_whenAskedForValidAccounts_shouldReturnValidAccounts() throws
																										   Exception {
		Set<String> validAccounts = new HashSet<>();
		validAccounts.add( "1111" );
		validAccounts.add( "2222" );
		validAccounts.add( "3333" );
		validAccounts.add( "4444" );

		when( properties.getProperty( "DemoAG" ) ).thenReturn( "1111;2222;3333;4444" );
		assertEquals( validAccounts, configService.getValidAccounts( "DemoAG" ) );
	}

	@Test
	public void shouldReturnBaseLineValue() throws Exception {
		when( properties.getProperty( "baseLine" ) ).thenReturn( "1000000.0" );
		BigDecimal oneMillionDollars = configService.getBaseLine();
		assertEquals( 0, BigDecimal.valueOf( 1000000.0 ).compareTo(  oneMillionDollars ));
	}

	@Test
	public void givenNewPropertyService_shouldLoadPropertiesFromStream() throws Exception {
		configService.loadProperties( "scopeonline.properties" );
		verify( properties ).load( any( InputStream.class ) );
	}

	@Test
	public void shouldCatchExceptionsWhenLoadingProperties() throws Exception {
		doThrow( new IOException( ERROR ) ).when( properties ).load( any( InputStream.class ) );
		//noinspection ConstantConditions
		configService = new ConfigServiceImpl( properties );
		configService.loadProperties( null );
	}

	@After
	public void tearDown() throws Exception {
		configService = null;
	}
}
