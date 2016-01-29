package de.codecentric.scopeonline.service;

import de.codecentric.scopeonline.service.HSQLServiceImpl.catalogType;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class DataBaseRunner extends HttpServlet {

	private DataBaseService service;

	@Override
	public void init() throws ServletException {
		super.init();

		HSQLServiceImpl hsqlService = new HSQLServiceImpl(
				catalogType.FILE, "", "scopeonline" );
		hsqlService.setProperties( new HsqlProperties() );
		hsqlService.setServer( new Server() );
		hsqlService.initProperties();
		service = hsqlService;
		service.startDB();
	}

	@Override
	public void destroy() {
		super.destroy();
		service.stopDB();
	}

}
