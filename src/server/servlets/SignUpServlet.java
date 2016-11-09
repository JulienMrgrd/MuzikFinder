package server.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import server.services.MuzikFinderService;
import sql.metier.User;
import utils.MuzikFinderPreferences;

public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SignUpServlet() { }

    // test if login already exist or if password is bad
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("SignUpServlet doGet");
	}

	// create account
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("SignUpServlet doPost");

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String mail = request.getParameter("mail");
		
		JsonObject myResponse = new JsonObject();
		MuzikFinderService service = MuzikFinderService.getInstance();
		
		if(username==null || username.isEmpty() || username.length()<5){
			myResponse.addProperty("message", "Username invalide");
			myResponse.addProperty("success", false);
			
		} else if(password==null || password.isEmpty() || password.length()<5){
			myResponse.addProperty("message", "Password invalide");
			myResponse.addProperty("success", false);
			
		} else if(mail==null || mail.isEmpty() || !mail.contains("@")){
			myResponse.addProperty("message", "Email invalide");
			myResponse.addProperty("success", false);
			
		} else if (service.checkLogin(username)){
			myResponse.addProperty("message", "Username already exists");
			myResponse.addProperty("success", false);
			
		} else {
			// inscription
			User user = service.createNewUser(username, password, mail, 1994, 02, 16); // TODO: date
			if(user!=null){
				Cookie userCookie = new Cookie(MuzikFinderPreferences.COOKIE_LOGIN, username);
				userCookie.setMaxAge(MuzikFinderPreferences.COOKIE_DURATION); //Store cookie for 1 day
				userCookie.setPath(MuzikFinderPreferences.COOKIE_PATH);
				response.addCookie(userCookie);
				
				userCookie = new Cookie(MuzikFinderPreferences.COOKIE_BIRTH, user.getDateBirth().toString()); // for stats
				userCookie.setMaxAge(MuzikFinderPreferences.COOKIE_DURATION); //Store cookie for 1 day
				userCookie.setPath(MuzikFinderPreferences.COOKIE_PATH);
				response.addCookie(userCookie);
				
				userCookie = new Cookie(MuzikFinderPreferences.COOKIE_USERID, user.getId()); // for stats
				userCookie.setMaxAge(MuzikFinderPreferences.COOKIE_DURATION); //Store cookie for 1 day
				userCookie.setPath(MuzikFinderPreferences.COOKIE_PATH);
				response.addCookie(userCookie);
				
				myResponse.addProperty("success", true);
			} else {
				myResponse.addProperty("success", true);
				myResponse.addProperty("message", "Inscription impossible...");
			}
			
		}
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/json");
	    PrintWriter out = response.getWriter();
	    out.print(myResponse);
	    out.close();
	}

}
