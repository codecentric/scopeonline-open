package de.codecentric.scopeonline.service;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;

import java.io.IOException;

public class HSQLServiceImpl implements DataBaseService {


	private HsqlProperties hsqlProperties;
	private Server         server;

	public static enum catalogType {FILE, MEM, RES}

	private final catalogType catalogType;

	private final String path;
	private final String dbName;

	public HSQLServiceImpl( final catalogType catalogType, final String path, final String dbName ) {
		this.catalogType = catalogType;
		this.path = path;
		this.dbName = dbName;
	}

	public void initProperties() {
		switch( catalogType ) {
			case FILE:
				hsqlProperties.setProperty( "server.database.0", String.format( "file:%s%s", path, dbName ) );
				break;
			case MEM:
				hsqlProperties.setProperty( "server.database.0", String.format( "mem:%s", dbName ) );
				break;
			case RES:
				hsqlProperties.setProperty( "server.database.0", String.format( "res:%s.%s", path, dbName ) );
				break;
		}
		hsqlProperties.setProperty( "server.dbname.0", "scopeonline" ) ;
		hsqlProperties.setProperty( "server.remote_open", "true" );

	}


	@Override
	public void startDB() {
		try {
			server.setProperties( hsqlProperties );
			server.start();
		} catch( AclFormatException|IOException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopDB() {
		server.stop();
	}


	public void setProperties( final HsqlProperties hsqlProperties ) {
		this.hsqlProperties = hsqlProperties;
	}

	public void setServer( final Server server ) {
		this.server = server;
	}


}
