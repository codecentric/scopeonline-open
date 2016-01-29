package de.codecentric.scopeonline.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface ConfigService {

	void addValidUser( String s );

	boolean isUserValid( String s );

	void addProperty( String propertyName, String propertyValue );

	String getProperty( String propertyName );

	List<String> getOrganizations();

	String getCustomer();

	void loadProperties( final String name );

	Set<String> getValidAccounts( final String organization );

	BigDecimal getBaseLine();


}
