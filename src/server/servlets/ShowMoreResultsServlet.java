package server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import interfaces.MFMusic;
import server.services.MuzikFinderService;
import utils.MuzikFinderUtils;

public class ShowMoreResultsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ShowMoreResultsServlet() { }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("ShowMoreResultsServlet doPost");
		
		String searchId = request.getParameter("searchId");
		
		// example : "[Justin R - Let me love][Alain Souchon - Sous le pont]...."
		String listMusicsAlreadyDisplayed = request.getParameter("listMusicsAlreadyDisplayed"); 
		JsonObject myResponse = new JsonObject();
		
		if(searchId==null || searchId.isEmpty()){
			myResponse.addProperty("success", false);
			
		} else {
			MuzikFinderService service = MuzikFinderService.getInstance();
			List<MFMusic> musics = service.getMoreResults(searchId);
			Set<MFMusic> musicsWithoutDuplicate = new HashSet<>(musics);
			musics.clear();
			for(MFMusic music : musicsWithoutDuplicate){
				if(!listMusicsAlreadyDisplayed.contains(music.getArtistName()+" - "+music.getTrackName())){
					musics.add(music);
				}
			}
			
			if(musics != null && !musics.isEmpty()){
				myResponse.addProperty("success", true);
				myResponse.addProperty("results", new Gson().toJson(musics));
				
			} else {
				myResponse.addProperty("success", false);
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
