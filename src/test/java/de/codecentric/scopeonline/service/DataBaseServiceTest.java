package de.codecentric.scopeonline.service;

import de.codecentric.scopeonline.service.HSQLServiceImpl.catalogType;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DataBaseServiceTest {
	private DataBaseService dataBaseService;
	private HSQLServiceImpl hsqlService;

	@Mock
	Server         server;
	@Mock
	HsqlProperties hsqlProperties;

	@Before
	public void setUp() throws Exception {
		initMocks( this );

	}

	@Test
	public void givenNewHSQLServiceWithCatalogTypeFile_whenSetProperties_shouldSetProperties() throws Exception {
		hsqlService = new HSQLServiceImpl( catalogType.FILE, "/var/db/", "scopeonline" );
		hsqlService.setProperties( hsqlProperties );
		hsqlService.initProperties();
		verify( hsqlProperties ).setProperty( "server.database.0", "file:/var/db/scopeonline" );
	}

	@Test
	public void givenNewHSQLServiceWithCatalogTypeMEM_whenSetProperties_shouldSetProperties() throws Exception {
		hsqlService = new HSQLServiceImpl( catalogType.MEM, null, "scopeonline" );
		hsqlService.setProperties( hsqlProperties );
		hsqlService.initProperties();
		verify( hsqlProperties ).setProperty( "server.database.0", "mem:scopeonline" );
	}

	@Test
	public void givenNewHSQLServiceWithCatalogTypeRES_whenSetProperties_shouldSetProperties() throws Exception {
		hsqlService = new HSQLServiceImpl( catalogType.RES, "de.codecentric", "scopeonline" );
		hsqlService.setProperties( hsqlProperties );
		hsqlService.initProperties();
		verify( hsqlProperties ).setProperty( "server.database.0", "res:de.codecentric.scopeonline" );
	}


	@Test
	public void whenDataBaseStarted_shouldStartDB() throws Exception {
		hsqlService = new HSQLServiceImpl( catalogType.FILE, "/var/db/", "scopeonline" );
		hsqlService.setProperties( hsqlProperties );
		hsqlService.initProperties();
		hsqlService.setServer( server );
		hsqlService.startDB();
		verify( server ).setProperties( hsqlProperties );
		verify( server ).start();
	}

	@Test
	public void startActualDB() throws Exception {
		hsqlService = new HSQLServiceImpl( catalogType.FILE, "", "scopeonline" );
		hsqlService.setProperties( new HsqlProperties() );
		hsqlService.setServer( new Server() );
		hsqlService.initProperties();
		hsqlService.startDB();

	}

	@Test
	public void stopDBstopsDB() throws Exception {
		hsqlService = new HSQLServiceImpl( catalogType.FILE, "", "scopeonline" );
		hsqlService.setServer( server );
		hsqlService.stopDB();
		verify( server ).stop();
	}

	@After
	public void tearDown() throws Exception {
		dataBaseService = null;
	}
}
