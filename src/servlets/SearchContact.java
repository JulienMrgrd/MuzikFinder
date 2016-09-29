package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.metier.Contact;
import domain.services.ContactService;

/**
 * Servlet implementation class NewContact
 */
public class SearchContact extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchContact() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("SearchContact doGet");
		
		String firstName = request.getParameter("firstname");
		String lastName = request.getParameter("lastname");
		String email = request.getParameter("email");
		
		boolean okFirstName = firstName!=null && !firstName.isEmpty();
		boolean okLastName = lastName!=null && !lastName.isEmpty();
		boolean okEmail = email!=null && !email.isEmpty();
		
		if(!okFirstName && !okLastName && !okEmail){
			request.setAttribute("message", "Please fill at least one field");
			RequestDispatcher dispatcher = request.getRequestDispatcher("searchContact.jsp");
			dispatcher.forward(request, response);
		} else {
		
			List<Contact> contacts = new ContactService().searchContact(firstName, lastName, email);
			if(contacts==null || contacts.isEmpty()){
				request.setAttribute("message", "No contacts found...");
			} else {
				request.setAttribute("message", "Results :");
				request.setAttribute("contacts", contacts);
			}
			RequestDispatcher dispatcher = request.getRequestDispatcher("searchContact.jsp");
			dispatcher.forward(request, response);
		}
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
