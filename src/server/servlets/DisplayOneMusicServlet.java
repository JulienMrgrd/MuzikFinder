package server.servlets;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nosql.NoSQLDB;
import server.dto.MusicDTO;
import server.services.MuzikFinderService;

public class DisplayOneMusicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public DisplayOneMusicServlet() { }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("DisplayOneMusicServlet doGet");
		//TODO getMusicById
		System.out.println(request.getParameter("idMusic"));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("DisplayOneMusicServlet doPost");
		doGet(request, response);
	}

}
