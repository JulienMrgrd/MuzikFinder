package sql.mysql;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Year;

import sql.User;

public class MySQLService {
	
	// TODO : ajouter les variables globales issues du constructeur
	Connection connection;
	private static final String USER_DB_NAME = "user";
	private static final String SEARCH_DB_NAME = "search";

	public MySQLService() throws URISyntaxException, SQLException {
		URI dbUri = new URI("mysql://b9fb1bf9d96fd5:336ac448@us-cdbr-iron-east-04.cleardb.net/heroku_1a48668fb87a67e?reconnect=true");

	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();
	    System.out.println(username);
	    System.out.println(password);
	    System.out.println(dbUrl);
		connection = DriverManager.getConnection(dbUrl, username, password);
		System.out.println(connection.getClientInfo());
		createTableUser();
		createTableSearch();
	}
	
	private void createTableUser() throws SQLException {
	    String sqlCreate = "CREATE TABLE IF NOT EXISTS " + USER_DB_NAME
	    		+ " ( id_user INTEGER PRIMARY KEY AUTO_INCREMENT ,"
	            + "   pseudo           VARCHAR(10),"
	            + "   password         VARCHAR(20),"
	            + "   date             DATE,"
	            + "   email	           VARCHAR(30))";

	    Statement stmt = connection.createStatement();
	    stmt.execute(sqlCreate);
		stmt.close();
	}
	
	private void createTableSearch() throws SQLException {
	    String sqlCreate = "CREATE TABLE IF NOT EXISTS " + SEARCH_DB_NAME
	    		+ " ( id_user          INTEGER ,"
	            + "   recherche        VARCHAR(50),"
	            + "   date             DATE)";

	    Statement stmt = connection.createStatement();
	    stmt.execute(sqlCreate);
		stmt.close();
	}
	
	private User getUserByLogin(String pseudo) throws SQLException{

		Statement stmt = connection.createStatement();

		String sqlRequest = "SELECT * from user "+USER_DB_NAME+" where pseudo = '"+pseudo+"';";

		boolean results = stmt.execute(sqlRequest);

		while (results) {
			ResultSet rs = stmt.getResultSet();
			try {
				while (rs.next()) {
					User user = new User(rs.getString("id_user"),rs.getString("pseudo"),rs.getString("password"),rs.getString("email"), rs.getDate("date"));
					
					stmt.close();
					//On retourne l'utilisateur
					return user;
				}
			} finally {
				try { rs.close(); } catch (Throwable ignore) {}
			}
			// Parcourir les autres resultat de la requête si il y en a
			results = stmt.getMoreResults();
		}
		stmt.close();
		return null;
		//retourne null car on a pas trouvé d'utilisateur corrsepondant à ce pseudo.
	}
	
	public User createNewUser(String pseudo, String password, String email, int year, int month, int day) throws SQLException{

		User user = getUserByLogin(pseudo);
		if(user==null){
			
			Statement stmt = connection.createStatement();
		
			java.sql.Date sqlDate = new Date(year, month-1, day);	
			
			String sqlRequest = "INSERT INTO "+USER_DB_NAME+"(pseudo,password,email,date) VALUES('"
					+ pseudo+"','"+password+"','"+email+"','"+sqlDate+"');";
	
			stmt.execute(sqlRequest);
			stmt.close();
			
			return getUserByLogin(pseudo);
		}
		return null;
	}
	
	public User verifyConnexion(String pseudo, String password) throws SQLException{

		Statement stmt = connection.createStatement();

		String sqlRequest = "SELECT * from user "+USER_DB_NAME+" where pseudo = '"+pseudo+"' and password = '"+password+"';";

		boolean results = stmt.execute(sqlRequest);

		while (results) {
			ResultSet rs = stmt.getResultSet();
			try {
				while (rs.next()) {
					User user = new User(rs.getString("id_user"),rs.getString("pseudo"),rs.getString("password"),rs.getString("email"), rs.getDate("date"));
					//si on entre dans cette boucle c'est que le pseudo et le password match bien
					//On retourne alors l'utilisateur
					return user;
				}
			} finally {
				try { rs.close(); } catch (Throwable ignore) {}
			}

			// Parcourir les autres resultat de la requête si il y en a
			results = stmt.getMoreResults();
		}
		stmt.close();
		return null;
		//retourne null car on a pas trouvé d'utilisateur corrsepondant à la combinaison.
	}
	
	public void addSearch(User user, String recherche) throws SQLException{
		if(user!=null){
			Statement stmt = connection.createStatement();
			java.util.Date utilDate = new java.util.Date();
		    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());		
	
		    
			String sqlRequest = "INSERT INTO "+SEARCH_DB_NAME+"(id_user,recherche,date) VALUES('"
					+ user.getId()+"','"+recherche+"','"+sqlDate+"');";
	
			stmt.execute(sqlRequest);
			stmt.close();
		}
	}
	
	public void setPassword(User user, String newPassword) throws SQLException{
		if(user!=null){
			Statement stmt = connection.createStatement();
			
			String sqlRequest = "UPDATE "+USER_DB_NAME+" "
					+ "SET password='"+newPassword+"'"
					+ "WHERE id_user='"+user.getId()+"'";
			
			stmt.execute(sqlRequest);
			stmt.close();
			user.setPassword(newPassword);
		}
	}
	
	public void setEmail(User user, String newEmail) throws SQLException{
		if(user!=null){
			Statement stmt = connection.createStatement();
			
			String sqlRequest = "UPDATE "+USER_DB_NAME+" "
					+ "SET email='"+newEmail+"'"
					+ "WHERE id_user='"+user.getId()+"'";
			
			stmt.execute(sqlRequest);
			stmt.close();
			user.setEmail(newEmail);
		}
	}
	
	public void seeAllDBUser() throws SQLException{
		
		Statement stmt = connection.createStatement();

		String sqlRequest = "SELECT * from user "+USER_DB_NAME+";";

		boolean results = stmt.execute(sqlRequest);

		while (results) {
			ResultSet rs = stmt.getResultSet();
			try {
				while (rs.next()) {
					User user = new User(rs.getString("id_user"),rs.getString("pseudo"),
								rs.getString("password"),rs.getString("email"), rs.getDate("date"));
					System.out.println(user.toString());
					//si on entre dans cette boucle c'est que le pseudo et le password match bien
					//On retourne alors l'utilisateur
				}
			} finally {
				try { rs.close(); } catch (Throwable ignore) {}
			}

			// Parcourir les autres resultat de la requête si il y en a
			results = stmt.getMoreResults();
		}
		stmt.close();
	}
	
	public void seeAllDBSearch() throws SQLException{
		
		Statement stmt = connection.createStatement();

		String sqlRequest = "SELECT * from "+SEARCH_DB_NAME+";";

		boolean results = stmt.execute(sqlRequest);

		while (results) {
			ResultSet rs = stmt.getResultSet();
			try {
				while (rs.next()) {
					System.out.println(rs.getString("id_user")+" "+rs.getString("recherche")
								+" "+rs.getDate("date"));
					//si on entre dans cette boucle c'est que le pseudo et le password match bien
					//On retourne alors l'utilisateur
				}
			} finally {
				try { rs.close(); } catch (Throwable ignore) {}
			}

			// Parcourir les autres resultat de la requête si il y en a
			results = stmt.getMoreResults();
		}
		stmt.close();
	}
	
	public void deleteSearchByDateAndUser(User user, Date date) throws SQLException{
		
		if(user!=null){
			Statement stmt = connection.createStatement();
	
			String sqlRequest = "DELETE  from "+SEARCH_DB_NAME+" "
					+ "WHERE id_user='"+user.getId()+"' and "
					+ "date='"+date+"';";
	
			stmt.execute(sqlRequest);
			stmt.close();
		}
	}
	
	public String getSearchByDateAndUser(User user, Date date) throws SQLException{
		
		if(user!=null){
			System.out.println("Search pour l'user = "+user.getLogin()+" a la date du "+date);
			Statement stmt = connection.createStatement();
	
			String sqlRequest = "Select * from "+SEARCH_DB_NAME+" "
					+ "WHERE id_user='"+user.getId()+"' and "
					+ "date='"+date+"';";
	
			boolean results = stmt.execute(sqlRequest);
			
			
			while (results) {
				ResultSet rs = stmt.getResultSet();
				try {
					while (rs.next()) {
						System.out.println(rs.getString("recherche"));
						//si on entre dans cette boucle c'est que le pseudo et le password match bien
						//On retourne alors l'utilisateur
					}
				} finally {
					try { rs.close(); } catch (Throwable ignore) {}
				}

				// Parcourir les autres resultat de la requête si il y en a
				results = stmt.getMoreResults();
			}
		}
		return "";
	}

	public User deleteAccountUser(User user) throws SQLException{
		if(user!=null){
			Statement stmt = connection.createStatement();
	
			String sqlRequest = "DELETE  from "+USER_DB_NAME+" "
					+ "WHERE id_user='"+user.getId()+"';";
	
			stmt.execute(sqlRequest);
			stmt.close();
			this.deleteSearchUser(user);
			return null;
		}
		return null;
	}
		
	public void deleteSearchUser(User user)throws SQLException{
		if(user!=null){
			Statement stmt = connection.createStatement();
	
			String sqlRequest = "DELETE  from "+SEARCH_DB_NAME+" "
					+ "WHERE id_user='"+user.getId()+"';";
	
			stmt.execute(sqlRequest);
			stmt.close();
		}
}
	
}