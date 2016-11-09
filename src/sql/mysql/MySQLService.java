package sql.mysql;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

import sql.metier.User;

public class MySQLService {
	
	Connection connection;
	private static final String USER_DB_NAME = "user";
	private static final String SEARCH_DB_NAME = "search";

	private MySQLService() throws URISyntaxException, SQLException, ClassNotFoundException {
		URI dbUri = new URI("mysql://b9fb1bf9d96fd5:336ac448@us-cdbr-iron-east-04.cleardb.net/heroku_1a48668fb87a67e?reconnect=true");

	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();
	    Class.forName("com.mysql.cj.jdbc.Driver");
	    DriverManager.setLoginTimeout(10);
		connection = DriverManager.getConnection(dbUrl, username, password);
		connection.setAutoCommit(true);
		createTableUser();
		createTableSearch();
	}
	
	/** Instance unique préinitialisée */
	private static MySQLService INSTANCE = getInstance();
 
	/**
	 * Technique du double checking (Singleton)
	 * @return
	 * @throws ExceptionInInitializerError
	 */
	public static MySQLService getInstance() throws ExceptionInInitializerError {	
		if (INSTANCE == null){ 	
			synchronized(MySQLService.class){
				if (INSTANCE == null){
					try {
						INSTANCE = new MySQLService();
					} catch (URISyntaxException | SQLException | ClassNotFoundException e) {
						throw new ExceptionInInitializerError(e);
					}
				}
			}
		}
		return INSTANCE;
	}
	
	private void createTableUser() {
	    String sqlCreate = "CREATE TABLE IF NOT EXISTS " + USER_DB_NAME
	    		+ " ( id_user INTEGER PRIMARY KEY AUTO_INCREMENT ,"
	            + "   pseudo           VARCHAR(10),"
	            + "   password         VARCHAR(20),"
	            + "   date             DATE,"
	            + "   email	           VARCHAR(30))";

	    try {
	    	Statement stmt = connection.createStatement();
			stmt.execute(sqlCreate);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Erreur MySQL lors de la création de TABLE_USER");
		}
	}
	
	private void createTableSearch() {
	    String sqlCreate = "CREATE TABLE IF NOT EXISTS " + SEARCH_DB_NAME
	    		+ " ( id_user          INTEGER ,"
	            + "   recherche        VARCHAR(50),"
	            + "   date             DATE)";
	    
	    try {
	    	Statement stmt = connection.createStatement();
			stmt.execute(sqlCreate);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Erreur MySQL lors de la création de TABLUE_SEARCH");
		}
	}
	
	private User getUserByLogin(String pseudo) {

		Statement stmt;
		try {
			stmt = connection.createStatement();
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
		} catch (SQLException e) {
			System.out.println("Problème de connection avec sql dans la méthode getUserByLogin");
		}
		return null;
		//retourne null car on a pas trouvé d'utilisateur correspondant à ce pseudo.
	}
	
	public boolean checkLogin(String username) {
		return getUserByLogin(username) != null;
	}
	
	public User createNewUser(String pseudo, String password, String email, int year, int month, int day){

		Statement stmt;
		try {
			stmt = connection.createStatement();
		
			String dateNow = year+"-"+month+"-"+day;
			Date sqlDate = Date.valueOf(dateNow);
			
			String sqlRequest = "INSERT INTO "+USER_DB_NAME+"(pseudo,password,email,date) VALUES('"
					+ pseudo+"','"+password+"','"+email+"','"+sqlDate+"');";
	
			stmt.execute(sqlRequest);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Problème de connection avec sql lors de la création d'un nouveau user");
			return null;
		}
		
		return getUserByLogin(pseudo);
	}
	
	public User checkConnexion(String pseudo, String password) {

		Statement stmt;
		try {
			stmt = connection.createStatement();
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
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Problème de connection sql lors de la connection de l'utilisateur");
			return null;
		}
		return null;
		//retourne null car on a pas trouvé d'utilisateur correspondant à la combinaison.
	}
	
	public void addSearch(User user, String recherche) {
		if(user!=null){
			Statement stmt;
			try {
				stmt = connection.createStatement();
			
				GregorianCalendar gc = new GregorianCalendar();
				gc.get(Calendar.WEEK_OF_YEAR);
				
				java.util.Date utilDate = new java.util.Date();
			    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());		
			    
				String sqlRequest = "INSERT INTO "+SEARCH_DB_NAME+"(id_user,recherche,date) VALUES('"
						+ user.getId()+"','"+recherche+"','"+sqlDate+"');";
		
				stmt.execute(sqlRequest);
				stmt.close();
			} catch (SQLException e) {
				System.out.println("problème lors de l'ajout d'une recherche");
			}
		}
	}
	
	public void setPassword(User user, String newPassword) {
		if(user!=null){
			Statement stmt;
			try {
				stmt = connection.createStatement();
			
				String sqlRequest = "UPDATE "+USER_DB_NAME+" "
						+ "SET password='"+newPassword+"'"
						+ "WHERE id_user='"+user.getId()+"'";
				
				stmt.execute(sqlRequest);
				stmt.close();
				user.setPassword(newPassword);
			} catch (SQLException e) {
				System.out.println("Probleme sql lors de la modification du mot de passe (setPassword)");
			}
		}
	}
	
	public void setEmail(User user, String newEmail){
		if(user!=null){
			try{
				Statement stmt = connection.createStatement();
				
				String sqlRequest = "UPDATE "+USER_DB_NAME+" "
						+ "SET email='"+newEmail+"'"
						+ "WHERE id_user='"+user.getId()+"'";
				
				stmt.execute(sqlRequest);
				stmt.close();
				user.setEmail(newEmail);
			} catch (SQLException e) {
				System.out.println("Probleme sql lors de la modification de l'adresse email (setEmail)");
			}
		}
	}

	public void deleteSearchByDateAndUser(User user, Date date) {
		
		if(user!=null){
			try{
				Statement stmt = connection.createStatement();
		
				String sqlRequest = "DELETE  from "+SEARCH_DB_NAME+" "
						+ "WHERE id_user='"+user.getId()+"' and "
						+ "date='"+date+"';";
		
				stmt.execute(sqlRequest);
				stmt.close();
			} catch (SQLException e) {
				System.out.println("Probleme sql lors de la suppression des recherches (deleteSearchByDateAndUser)");
			}	
		}
	}
	
	public String getSearchByDateAndUser(User user, Date date) {
		
		if(user!=null){
			try{
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
			} catch (SQLException e) {
				System.out.println("Probleme sql lors de la recherche des recherches (getSearchByDateAndUser)");
				return "";
			}
		}
		return "";
	}

	public User deleteAccountUser(User user) {
		if(user!=null){
			Statement stmt;
			try {
				stmt = connection.createStatement();
				
				String sqlRequest = "DELETE  from "+USER_DB_NAME+" "
						+ "WHERE id_user='"+user.getId()+"';";
		
				stmt.execute(sqlRequest);
				stmt.close();
				
				this.deleteSearchUser(user);
				return null;
			} catch (SQLException e) {
				System.out.println("deleteAccountUser a rencontré un problème");
				return null;
			}
		}
		return null;
	}
		
	public void deleteSearchUser(User user){
		if(user!=null){
			Statement stmt;
			try {
				stmt = connection.createStatement();

				String sqlRequest = "DELETE  from "+SEARCH_DB_NAME+" "
						+ "WHERE id_user='"+user.getId()+"';";
		
				stmt.execute(sqlRequest);
				stmt.close();
			} catch (SQLException e) {
				System.out.println("Probléme de connection avec sql lors du deleteSearchUser");
			}
	
		}
	}
	
	//TODO: A supprimer
	////////////////METHODE UTILES JUSTE PENDANT LA PERIODE DE DEV/////////////////////
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

}