package server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import server.services.MuzikFinderService;
import utils.MuzikFinderPreferences;
import utils.MuzikFinderUtils;

public class FindGoodMusicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public FindGoodMusicServlet() { }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("FindGoodMusicServlet doPost");
		
		String idGoodMusic = request.getParameter("idGoodMusic"); 
		JsonObject myResponse = new JsonObject();
		
		if(idGoodMusic==null || idGoodMusic.isEmpty()){
			myResponse.addProperty("success", false);
			
		} else {
			String userBirth = MuzikFinderUtils.getCookieValueByName(MuzikFinderPreferences.COOKIE_BIRTH, request.getCookies());
			if(userBirth == null){
				myResponse.addProperty("success", false);
				myResponse.addProperty("message", "Expired session. Please login.");
			} else {
				
				LocalDate dateUser = LocalDate.parse(userBirth); // see sql.Date.toString() method
				MuzikFinderService.getInstance().addNewSearch(idGoodMusic, dateUser); // Anonymous search
				myResponse.addProperty("success", true);
			}
			
		}
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    out.print(myResponse);
	    out.close();
	    
	    MuzikFinderUtils.updateTimeCookies(request, response);
	}
	
}
