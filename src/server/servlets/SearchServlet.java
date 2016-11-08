package server.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.services.MuzikFinderService;
import sql.metier.User;
import utils.MuzikFinderPreferences;
import utils.MuzikFinderUtils;

public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SearchServlet() { }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("SearchServlet doGet");
		
		boolean success = true;
		String userSearch = (String) request.getParameter("userSearch");
		System.out.println(request.getAttribute("userSearch"));
		System.out.println(request.getParameter("userSearch"));
		System.out.println(userSearch);
		User user = (User) request.getSession().getAttribute("acc");
		
		if(user == null){
			success = false;
			request.setAttribute("message", "Veuillez vous connecter");
		} else if(userSearch==null || userSearch.isEmpty()){
			success = false;
			request.setAttribute("message", "Veuillez renseigner au moins 3 mots ...");
		}
		
		if(!success){
			request.setAttribute("success", false);
		}else {	
			List<String> tags = Arrays.asList(userSearch.split(" "));
			
			if(tags.size() < MuzikFinderPreferences.MIN_SIZE_OF_TAGS_FOR_SEARCH){
				request.setAttribute("success", false);
				request.setAttribute("message", "Veuillez renseigner au moins "
						+MuzikFinderPreferences.MIN_SIZE_OF_TAGS_FOR_SEARCH+" mots ...");
				
			} else if(tags.size() > MuzikFinderPreferences.MAX_SIZE_OF_TAGS_FOR_SEARCH){
				request.setAttribute("success", false);
				request.setAttribute("message", "Veuillez renseigner au maximum "
						+MuzikFinderPreferences.MAX_SIZE_OF_TAGS_FOR_SEARCH+" mots ...");

			} else {
				request.setAttribute("success", true);
				request.setAttribute("results", new MuzikFinderService().searchMusics(tags, 
						MuzikFinderUtils.generateRandomIdSearch(user.getLogin())));
				
				// Regex construction (voir coloration des mots dans search.jsp)
				String str = tags.get(0);
				for(int i=1; i<tags.size(); i++){
					str+="|"+tags.get(i);
				}
				request.setAttribute("tagsRegex", "([ -'](?i)("+str+")[ -'])"); // exemple : "(?i)(work|let|roses)"
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("search.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("SearchServlet doPost");
		doGet(request, response);
	}
	
}
