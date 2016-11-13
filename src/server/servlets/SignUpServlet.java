package server.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import server.services.MuzikFinderService;
import sql.metier.User;
import utils.MathUtils;
import utils.MuzikFinderUtils;

public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SignUpServlet() { }

	// create account
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("SignUpServlet doPost");

		JsonObject myResponse = new JsonObject();
		MuzikFinderService service = MuzikFinderService.getInstance();
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String mail = request.getParameter("mail");
		int day;
		int month;
		int year;
		try{
			day = Integer.parseInt(request.getParameter("day"));
			month = Integer.parseInt(request.getParameter("month"));
			year = Integer.parseInt(request.getParameter("year"));
		}catch(NumberFormatException e){
			myResponse.addProperty("message", "Date de naissance invalide");
			myResponse.addProperty("success", false);
			response.setCharacterEncoding("UTF-8");
		    response.setContentType("text/json");
		    PrintWriter out = response.getWriter();
		    out.print(myResponse);
		    out.close();
		    return;
		}
		
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
			
		} else if(!MathUtils.isDateValid(day, month, year)){
			myResponse.addProperty("message", "Date de naissance invalide");
			myResponse.addProperty("success", false);
		} else {
			// inscription
			User user = service.createNewUser(username, password, mail, year, month, day);
			if(user!=null){
				MuzikFinderUtils.createNewCookies(user, response);
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
