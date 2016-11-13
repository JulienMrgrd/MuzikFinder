package server.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import server.services.MuzikFinderService;
import utils.MuzikFinderPreferences;
import utils.MuzikFinderUtils;

/**
 * Servlet implementation class AccountServlet
 */
public class AccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public AccountServlet() {}

    /**
     * Insertion de données dans la page "My account"
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("AccountServlet doGet");
		
		String userId = MuzikFinderUtils.getCookieValueByName(MuzikFinderPreferences.COOKIE_USERID, request.getCookies());		
		if(userId == null){
			request.setAttribute("message", "Veuillez vous connecter");
			request.setAttribute("success", false);
		} else {
			request.setAttribute("success", true);
			request.setAttribute("results",  MuzikFinderService.getInstance().getSearchByDateAndUser(userId, null));
		}
	
		RequestDispatcher dispatcher = request.getRequestDispatcher("myAccount.jsp");
		dispatcher.forward(request, response);
		
		MuzikFinderUtils.updateTimeCookies(request, response);
	}

	/**
	 * 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("AccountServlet doPost");
		
		String type = request.getParameter("type");
		String password = request.getParameter("password");
		String passwordBis = request.getParameter("passwordBis");
		String mail = request.getParameter("mail");
		
		JsonObject myResponse = new JsonObject();
		MuzikFinderService mzf = MuzikFinderService.getInstance();
		String id_user = MuzikFinderUtils.getCookieValueByName(MuzikFinderPreferences.COOKIE_USERID, request.getCookies());		
		
        if (id_user!=null && type.equals("setAccount")) {
        	if(password!=null && !password.isEmpty() && passwordBis!=null && !passwordBis.isEmpty() 
        			&& !password.equals(passwordBis)){
        		myResponse.addProperty("success", false);
        		myResponse.addProperty("message", "Passwords not equals !");
        	} else if(password!=null && password.length()<5){
        		myResponse.addProperty("success", false);
        		myResponse.addProperty("message", "Password too short !");
        	} else if (mail != null && !mail.contains("@")){
        		myResponse.addProperty("success", false);
        		myResponse.addProperty("message", "Email not correct !");
        	} else {
        		mzf.update(id_user, password, mail);
        		myResponse.addProperty("success", true);
        	}
       
        } else if (id_user!=null && type.equals("deleteAccount")) {
        	mzf.deleteAccountUser(id_user);
    		RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
    		dispatcher.forward(request, response);
    		return;
        } else {
        	myResponse.addProperty("message", "Unknown error...");
        	myResponse.addProperty("success", false);
        }
        
        response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    out.print(myResponse);
	    out.close();
	    
	    MuzikFinderUtils.updateTimeCookies(request, response);
	}

}
