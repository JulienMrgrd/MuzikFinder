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
import utils.MuzikFinderPreferences;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public LoginServlet() { }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("LoginServlet doGet");
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("LoginServlet doPost");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		JsonObject myResponse = new JsonObject();
		
		if(username==null || username.isEmpty() || username.length()<MuzikFinderPreferences.MIN_SIZE_OF_USERNAME){
			myResponse.addProperty("message", "Username invalide");
			myResponse.addProperty("success", false);
			
		} else if(password==null || password.isEmpty() || password.length()<MuzikFinderPreferences.MIN_SIZE_OF_PASSWORD){
			myResponse.addProperty("message", "Password invalide");
			myResponse.addProperty("success", false);
			
		} else {
			MuzikFinderService service = new MuzikFinderService();
			User user = service.checkConnection(username, password);
			if(user != null){
				myResponse.addProperty("success", true);
				request.getSession().setAttribute("acc", /*TODO: get Account ou Cookie ou autre */user);
			
			} else {
				myResponse.addProperty("success", false);
				myResponse.addProperty("message", "Login ou mot de passe incorrect.");
			}
			
		}
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/json");
	    PrintWriter out = response.getWriter();
	    out.print(myResponse);
	    out.close();
	}

}
