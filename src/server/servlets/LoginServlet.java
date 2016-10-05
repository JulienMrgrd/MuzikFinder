package server.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nosql.mongo.MongoService;
import sources.mongo.com.mongodb.BasicDBObject;
import sources.mongo.com.mongodb.DBCollection;
import sources.mongo.com.mongodb.DBCursor;
import sources.mongo.com.mongodb.DBObject;

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
		System.out.println("mongoservice OK");
		DBCollection collection = mongo.getCollection("songs");
		System.out.println("mongo collection OK");
		
		List<BasicDBObject> docs = mongo.createFakeBasicDBObjects();
		mongo.insertMany(collection, docs);
		System.out.println("mongo insert many OK");
		
		BasicDBObject before = new BasicDBObject("song", "One Sweet Day");
		BasicDBObject after = new BasicDBObject("$set", new BasicDBObject("artist", "Mariah Carey ft. Boyz II Men"));
		mongo.updateOne(collection, before, after);

		BasicDBObject findQuery = new BasicDBObject("weeksAtOne", new BasicDBObject("$gte",10));
		BasicDBObject orderBy = new BasicDBObject("decade", 1);
		DBCursor cursor = mongo.findBy(collection, findQuery, orderBy);

		while(cursor.hasNext()){
			DBObject doc = cursor.next();
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
