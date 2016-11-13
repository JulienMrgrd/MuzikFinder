package server.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nosql.mongo.MongoCollectionsAndKeys;
import server.services.MuzikFinderService;
import utils.MuzikFinderPreferences;
import utils.MuzikFinderUtils;

/**
 * Servlet implementation class TopMusicsServlet
 */
public class TopMusicsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TopMusicsServlet() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("TopMusicsServlet doGet");
		
		String userId = MuzikFinderUtils.getCookieValueByName(MuzikFinderPreferences.COOKIE_USERID, request.getCookies());		
		if(userId == null){
			request.setAttribute("message", "Veuillez vous connecter");
			request.setAttribute("success", false);
		} else {
			request.setAttribute("success", true);
			request.setAttribute("topMusicWeek",  MuzikFinderService.getInstance().getTopMusicSearchThisWeek());
			request.setAttribute("topMusic-18",  
					MuzikFinderService.getInstance().getListMFMusicMostPopularByRange(MongoCollectionsAndKeys.MINUSEIGHTEEN_STATS));
			request.setAttribute("topMusic-25",  
					MuzikFinderService.getInstance().getListMFMusicMostPopularByRange(MongoCollectionsAndKeys.MINUSTWENTYFIVE_STATS));
			request.setAttribute("topMusic-50", 
					MuzikFinderService.getInstance().getListMFMusicMostPopularByRange(MongoCollectionsAndKeys.MINUSFIFTY_STATS));
			request.setAttribute("topMusic+50", 
					MuzikFinderService.getInstance().getListMFMusicMostPopularByRange(MongoCollectionsAndKeys.PLUSFIFTY_STATS));
		}
	
		RequestDispatcher dispatcher = request.getRequestDispatcher("tops.jsp");
		dispatcher.forward(request, response);
		
		MuzikFinderUtils.updateTimeCookies(request, response);
	}

}
