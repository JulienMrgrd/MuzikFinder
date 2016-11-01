package server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

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
		System.out.println(username + " : "+password);
		
		/*TODO : à vérifier en base*/ List<String> alreadyExistsNames = Arrays.asList(new String[]{"JULIEN", "FELIX", "MOUSSA"});
		
		JsonObject myResponse = new JsonObject();
		
		if(username==null || username.isEmpty() || username.length()<5){
			//username invalide
			myResponse.addProperty("message", "Username invalide");
			myResponse.addProperty("success", false);
			
		} else if(password==null || password.isEmpty() || password.length()<5){
			myResponse.addProperty("message", "Password invalide");
			myResponse.addProperty("success", false);
			
		} else if(mail==null || mail.isEmpty() || !mail.contains("@")){
			myResponse.addProperty("message", "Email invalide");
			myResponse.addProperty("success", false);
			
		} else if (alreadyExistsNames.contains(username.toUpperCase())){
			myResponse.addProperty("message", "Username already exists");
			myResponse.addProperty("success", false);
			
		} else {
			// inscription
			myResponse.addProperty("success", true);
			
			//MuzikFinderService service = new MuzikFinderService();
			/*TODO : inscrire en base*/ 
			request.getSession().setAttribute("acc", new Object());
		}
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/json");
	    PrintWriter out = response.getWriter();
	    out.print(myResponse);
	    out.close();
	}

}
