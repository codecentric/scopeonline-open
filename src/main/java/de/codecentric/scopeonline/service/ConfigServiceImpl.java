package de.codecentric.scopeonline.service;

import de.codecentric.scopeonline.service.error.PropertyNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

public class ConfigServiceImpl implements ConfigService {
	Properties properties;
	private final String validUserListKey = "validUserList";


	@Override
	public void addValidUser( final String validUser ) {
		if( properties.containsKey( validUserListKey ) ) {
			String validUserList = properties.getProperty( validUserListKey );
			validUserList += ";"+validUser;
			properties.setProperty( validUserListKey, validUserList );
		} else {
			properties.setProperty( validUserListKey, validUser );
		}
	}

	@Override
	public String getProperty( final String propertyName ) {
		if( properties.getProperty( propertyName ) == null )
			throw new PropertyNotFoundException();
		else
			return properties.getProperty( propertyName );
	}

	@Override
	public void addProperty( final String propertyName, final String propertyValue ) {
		properties.setProperty( propertyName, propertyValue );
	}

	@Override
	public boolean isUserValid( final String userInQuestion ) {
		if( properties.containsKey( validUserListKey ) ) {
			String validUserList = properties.getProperty( validUserListKey );
			if( validUserList.contains( userInQuestion ) )
				return true;
		}
		return false;
	}

	@Override
	public List<String> getOrganizations() {
		List<String> organizations = new ArrayList<>();
		String[] organizationsArray = properties.getProperty( "organizations" ).split( ";" );
		Collections.addAll( organizations, organizationsArray );
		return organizations;
	}

	@Override
	public String getCustomer() {
		return properties.getProperty( "customer" );
	}

	@Override
	public Set<String> getValidAccounts( final String organization ) {
		String[] validAccountsArray = properties.getProperty( organization.replace( " ", "" ) ).split( ";" );
		Set<String> validAccounts = new HashSet<String>();
		Collections.addAll( validAccounts, validAccountsArray );
		return validAccounts;
	}

	@Override
	public BigDecimal getBaseLine() {
		String baseLine = properties.getProperty( "baseLine" );
		return new BigDecimal( baseLine );
	}

	@Override
	public void loadProperties( final String name ) {
		try {
			InputStream inputStream = getClass().getResourceAsStream( "/"+name );
			properties.load( inputStream );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}

	public ConfigServiceImpl( final Properties properties ) {
		this.properties = properties;
	}

	public ConfigServiceImpl( String fileName ) {
		properties = new Properties();
		loadProperties( fileName );
	}
}
