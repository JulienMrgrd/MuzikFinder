package server.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import nosql.mongo.MongoService;

/**
 * Servlet implementation class ContactServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public LoginServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("LoginServlet doGet");
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("LoginServlet doPost");
		
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		
		MongoService mongo = new MongoService();
		MongoCollection<Document> collection = mongo.getCollection("songs");
		
		List<Document> docs = mongo.createFakeDocuments();
		mongo.insertMany(collection, docs);

		Document before = new Document("song", "One Sweet Day");
		Document after = new Document("$set", new Document("artist", "Mariah Carey ft. Boyz II Men"));
		mongo.updateOne(collection, before, after);

		Document findQuery = new Document("weeksAtOne", new Document("$gte",10));
		Document orderBy = new Document("decade", 1);
		MongoCursor<Document> cursor = mongo.findBy(collection, findQuery, orderBy);

		while(cursor.hasNext()){
			Document doc = cursor.next();
			System.out.println(
					"In the " + doc.get("decade") + ", " + doc.get("song") + 
					" by " + doc.get("artist") + " topped the charts for " + 
					doc.get("weeksAtOne") + " straight weeks."
					);
		}

		mongo.dropCollection(collection);

		mongo.close();
		
		if(name.equals(password)){
			request.setAttribute("message", "Hello Mr "+name+" !");
			RequestDispatcher dispatcher = request.getRequestDispatcher("accueil.jsp");
			dispatcher.forward(request, response);
		} else {
			request.setAttribute("message", "Bad password...");
			RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
			dispatcher.forward(request, response);
		}
	}

}
