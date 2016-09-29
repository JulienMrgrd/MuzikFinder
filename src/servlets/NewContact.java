package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.services.ContactService;

/**
 * Servlet implementation class NewContact
 */
public class NewContact extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewContact() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("NewContact doGet");
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("NewContact doPost");
	
		String firstName = request.getParameter("firstname");
		String lastName = request.getParameter("lastname");
		String email = request.getParameter("email");
		
		/*TODO: vérification confirmité des champs*/
		boolean okFirstName = firstName!=null && firstName.length()>0; //&& not exists in DB
		boolean okLastName = lastName!=null && lastName.length()>0; //&& not exists in DB
		boolean okEmail = email!=null && email.length()>0; //&& not exists in DB
		
		if(okFirstName && okLastName && okEmail){
			new ContactService().addContact(firstName, lastName, email);
			request.setAttribute("message", firstName+" "+lastName+" has been added correctly !");
			RequestDispatcher dispatcher = request.getRequestDispatcher("accueil.jsp");
			dispatcher.forward(request, response);
		} else {
			request.setAttribute("message", "Error with one field...");
			RequestDispatcher dispatcher = request.getRequestDispatcher("addContact.jsp");
			dispatcher.forward(request, response);
		}
	}

}
