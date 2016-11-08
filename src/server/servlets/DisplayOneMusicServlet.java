package server.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.dto.MFMusicDTO;
import server.services.MuzikFinderService;

public class DisplayOneMusicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public DisplayOneMusicServlet() { }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("DisplayOneMusicServlet doGet");
		String idMusic = request.getParameter("idMusic");
		MFMusicDTO dto = (MFMusicDTO) new MuzikFinderService().getMusicById(idMusic);
		System.out.println(dto.getTrackId());
		System.out.println(dto.getTrackName());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("DisplayOneMusicServlet doPost");
		doGet(request, response);
	}

}
