package server.servlets;

import javax.servlet.http.HttpServlet;

import server.services.MuzikFinderService;

public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Automatically called by Tomcat at launch.
	 */
    public InitServlet() { 
    	// Launch all db connections
    	MuzikFinderService.getInstance();
    }

}
