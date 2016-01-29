package de.codecentric.scopeonline.rpc;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class ConnectionFactory {
	public URLConnection buildConnection( String url ) throws IOException {
		URL urlObj = new URL( url );
		URLConnection conn = urlObj.openConnection();
		conn.addRequestProperty( "SOAPAction", "" );
		conn.addRequestProperty( "Cache-Control", "no-cache" );
		conn.addRequestProperty( "Content-Type", "text/xml; charset=utf-8" );
		conn.setDoOutput( true );
		return conn;
	}
}
