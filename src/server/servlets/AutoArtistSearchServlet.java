package server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import server.services.MuzikFinderService;

/**
 * Servlet implementation class SpecificSearch
 */
public class AutoArtistSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public AutoArtistSearchServlet() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String search = (String) request.getParameter("search");
		
		if(search!=null && !search.isEmpty() && !search.equals("null")){
			MuzikFinderService mzf = MuzikFinderService.getInstance();
			Set<String> listArtist = mzf.getArtistNamesBeginWith(search);
			//Set<String> listTrackName = mzf.getTrackNamesBeginWith(search); //not today unfortunately...
			JSONArray js=new JSONArray();
			for(String s: listArtist){
				js.put(s);
			}
			PrintWriter out = response.getWriter();
		    out.print(js);
		    out.close();
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doPost");
	}

}
