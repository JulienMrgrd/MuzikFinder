package server.servlets;

import java.io.IOException;
import java.sql.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.services.MuzikFinderService;
import utils.MuzikFinderPreferences;
import utils.MuzikFinderUtils;

/**
 * Servlet implementation class AccountServlet
 */
public class AccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public AccountServlet() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = MuzikFinderUtils.getCookieValueByName(MuzikFinderPreferences.COOKIE_USERID, request.getCookies());		
		System.out.println(userId);
		request.setAttribute("success", true);
		//TODO: regarder pourquoi cookie == null
		/*if(userId == null){
			request.setAttribute("message", "Veuillez vous connecter");
			request.setAttribute("success", false);
		}*/
		MuzikFinderService.getInstance();	
		request.setAttribute("results",  MuzikFinderService.getInstance().getSearchByDateAndUser(/*userId*/"2",null));
	
		RequestDispatcher dispatcher = request.getRequestDispatcher("myAccount.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doPost");
		
		String button = request.getParameter("button");

		MuzikFinderService mzf = MuzikFinderService.getInstance();
		//TODO: les getParameter fonctionne pas encore
		String id_user = MuzikFinderUtils.getCookieValueByName(MuzikFinderPreferences.COOKIE_USERID, request.getCookies());		
		
        if ("ButtonSetAccount".equals(button)) {
        	String newPassword = request.getParameter("newPassword");
        	String newEmail = request.getParameter("newEmail");
        	mzf.update(id_user, newPassword, newEmail);
        } else if ("ButtonDeleteSearch".equals(button)) {
        	//TODO: A voir la forme que sa a pour le transformer en sqlDate
        	String date = request.getParameter("date");
        	//String dateNow = year+"-"+month+"-"+day;
			Date sqlDate = Date.valueOf(date);
        	mzf.deleteSearchByDateAndUser(id_user,sqlDate);
        } else if ("ButtonDeleteAccount".equals(button)) {
        	mzf.deleteAccountUser(id_user);
        }		
		doGet(request, response);
		
	}

}
