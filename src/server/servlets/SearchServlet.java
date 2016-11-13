package server.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import interfaces.MFMusic;
import server.services.MuzikFinderService;
import utils.MuzikFinderPreferences;
import utils.MuzikFinderUtils;

public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SearchServlet() { }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("SearchServlet doGet");
		
		boolean success = true;
		String userSearch = (String) request.getParameter("userSearch");
		String artistOrLyrics = (String) request.getParameter("artistOrLyrics");
		
		String userLogin = MuzikFinderUtils.getCookieValueByName(MuzikFinderPreferences.COOKIE_LOGIN, request.getCookies());
		String userId = MuzikFinderUtils.getCookieValueByName(MuzikFinderPreferences.COOKIE_USERID, request.getCookies());
		String userBirth = MuzikFinderUtils.getCookieValueByName(MuzikFinderPreferences.COOKIE_BIRTH, request.getCookies());
		
		if(userLogin == null){
			success = false;
			request.setAttribute("message", "Veuillez vous connecter");
		} else if(userSearch==null || userSearch.isEmpty()){
			success = false;
			request.setAttribute("message", "Veuillez renseigner au moins 3 mots ...");
		}
		
		boolean isSearchArtist = (artistOrLyrics!=null && artistOrLyrics.equals("artist"));
		
		if(!success){
			request.setAttribute("success", false);
		}else {
			List<String> tags = Arrays.asList(userSearch.split(" "));
			
			if(!isSearchArtist && tags.size() < MuzikFinderPreferences.MIN_SIZE_OF_TAGS_FOR_SEARCH){
				request.setAttribute("success", false);
				request.setAttribute("message", "Veuillez renseigner au moins "
						+MuzikFinderPreferences.MIN_SIZE_OF_TAGS_FOR_SEARCH+" mots ...");
				
			} else if(!isSearchArtist && tags.size() > MuzikFinderPreferences.MAX_SIZE_OF_TAGS_FOR_SEARCH){
				request.setAttribute("success", false);
				request.setAttribute("message", "Veuillez renseigner au maximum "
						+MuzikFinderPreferences.MAX_SIZE_OF_TAGS_FOR_SEARCH+" mots ...");

			} else {
				request.setAttribute("success", true);
				String randomSearchId = MuzikFinderUtils.generateRandomIdSearch(userLogin);
				request.setAttribute("searchId", randomSearchId);
				MuzikFinderService ms = MuzikFinderService.getInstance();
				List<MFMusic> musics;
				
				if(isSearchArtist){ // recherche par artist
					request.setAttribute("artist", userSearch);
					musics = ms.getMusicsByArtist(userSearch);
				
				} else { // recherche par lyrics
					musics = ms.searchMusics(userId, tags, randomSearchId);
					// Regex construction (voir coloration des mots dans search.jsp)
					String str = tags.get(0);
					for(int i=1; i<tags.size(); i++){
						if(tags.get(i)!=null && !tags.get(i).isEmpty()) str+="|"+tags.get(i);
					}
					request.setAttribute("tagsRegex", "([ -'](?i)("+str+")[ -'])"); // exemple : "(?i)(work|let|roses)"
				}
				Set<MFMusic> musicsWithoutDuplicate = new HashSet<>(musics);
				request.setAttribute("results", musicsWithoutDuplicate);
				
				MuzikFinderUtils.updateTimeCookies(userLogin, userId, userBirth, response);
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("search.jsp");
		dispatcher.forward(request, response);
	}

}
