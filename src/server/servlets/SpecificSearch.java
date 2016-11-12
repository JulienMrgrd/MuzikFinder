package server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import server.services.MuzikFinderService;

/**
 * Servlet implementation class SpecificSearch
 */
public class SpecificSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SpecificSearch() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String search = request.getParameter("search");
		MuzikFinderService mzf = MuzikFinderService.getInstance();
		List<String> listArtist = mzf.getListNameArtistBeginWith(search);
		List<String> listTrackName = mzf.getListTrackNameBeginWith(search);
		JSONObject js=new JSONObject();
		for(String s: listArtist){
			js.append("artist", s);
		}
		for(String s: listTrackName){
			js.append("trackName", s);
		}
		PrintWriter out = response.getWriter();
	    out.print(js);
	    out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
