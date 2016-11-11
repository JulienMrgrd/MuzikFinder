package sql.mysql;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sql.metier.Search;
import sql.metier.User;

public class MySQLService {
	
	Connection connection;
	private static final String USER_DB_NAME = "user";
	private static final String SEARCH_DB_NAME = "search";

	public MySQLService() throws URISyntaxException, ClassNotFoundException, SQLException {
		URI dbUri = new URI("mysql://b9fb1bf9d96fd5:336ac448@us-cdbr-iron-east-04.cleardb.net:3306/heroku_1a48668fb87a67e?autoReconnect=true&reconnect=true");

	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:mysql://" + dbUri.getHost() +":"+ dbUri.getPort() + dbUri.getPath()+"?reconnect=true";
	    Class.forName("com.mysql.jdbc.Driver");
	    DriverManager.setLoginTimeout(0);
		connection = DriverManager.getConnection(dbUrl, username, password);
		connection.setAutoCommit(true);
		createTableUser();
		createTableSearch();
	}
	
	private void createTableUser() {
	    String sqlCreate = "CREATE TABLE IF NOT EXISTS " + USER_DB_NAME
	    		+ " ( id_user INTEGER PRIMARY KEY AUTO_INCREMENT ,"
	            + "   pseudo           VARCHAR(20),"
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
	            + "   id_recherche     VARCHAR(50),"
	            + "   date             DATE)";
	    
	    try {
	    	Statement stmt = connection.createStatement();
			stmt.execute(sqlCreate);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
	
	public void addSearch(String id_user, String recherche, String id_recherche) {
		if(id_user != null && ! id_user.isEmpty()){
			Statement stmt;
			try {
				stmt = connection.createStatement();
			
				java.util.Date utilDate = new java.util.Date();
			    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());		
			    
				String sqlRequest = "INSERT INTO "+SEARCH_DB_NAME+"(id_user,recherche,id_recherche,date) VALUES('"
						+ id_user+"','"+recherche+"','"+id_recherche+"','"+sqlDate+"');";
				stmt.execute(sqlRequest);
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Problème lors de l'ajout d'une recherche");
			}
		}
	}
	
	public void update(String id_user, String newPassword, String newEmail){
		if(id_user != null && ! id_user.isEmpty()){
			if(newPassword != null && !newPassword.isEmpty()){
				setPassword(id_user, newPassword);
			}
			if(newEmail != null && !newEmail.isEmpty()){
				setEmail(id_user, newEmail);
			}
		}
	}
	
	public void setPassword(String id_user, String newPassword) {
		if(id_user != null && ! id_user.isEmpty()){
			Statement stmt;
			try {
				stmt = connection.createStatement();
			
				String sqlRequest = "UPDATE "+USER_DB_NAME+" "
						+ "SET password='"+newPassword+"'"
						+ "WHERE id_user='"+id_user+"'";
				
				stmt.execute(sqlRequest);
				stmt.close();
			} catch (SQLException e) {
				System.out.println("Probleme sql lors de la modification du mot de passe (setPassword)");
			}
		}
	}
	
	public void setEmail(String id_user, String newEmail){
		if(id_user != null && ! id_user.isEmpty()){
			try{
				Statement stmt = connection.createStatement();
				
				String sqlRequest = "UPDATE "+USER_DB_NAME+" "
						+ "SET email='"+newEmail+"'"
						+ "WHERE id_user='"+id_user+"'";
				
				stmt.execute(sqlRequest);
				stmt.close();
			} catch (SQLException e) {
				System.out.println("Probleme sql lors de la modification de l'adresse email (setEmail)");
			}
		}
	}

	public void deleteSearchByDateAndUser(String id_user, Date date) {
		
		if(id_user != null && ! id_user.isEmpty()){
			try{
				Statement stmt = connection.createStatement();
		
				String sqlRequest = "DELETE  from "+SEARCH_DB_NAME+" "
						+ "WHERE id_user='"+id_user+"' and "
						+ "date='"+date+"';";
		
				stmt.execute(sqlRequest);
				stmt.close();
			} catch (SQLException e) {
				System.out.println("Probleme sql lors de la suppression des recherches (deleteSearchByDateAndUser)");
			}	
		}
	}
	
	public List<Search> getSearchByDateAndUser(String id_user, Date date) {
		
		List<Search> listSearch = new ArrayList<>();
		System.out.println(date);
		if(id_user != null && ! id_user.isEmpty()){
			try{
				Statement stmt = connection.createStatement();
				String sqlRequest="";
				if(date!=null){
					sqlRequest = "Select * from "+SEARCH_DB_NAME+" "
							+ "WHERE id_user='"+id_user+"' and "
							+ "date='"+date+"';";
				}else{
					sqlRequest = "Select * from "+SEARCH_DB_NAME+" "
							+ "WHERE id_user='"+id_user+"';";
				}
				boolean results = stmt.execute(sqlRequest);
				
				while (results) {
					ResultSet rs = stmt.getResultSet();
					try {
						while (rs.next()) {
							listSearch.add(0,new Search(rs.getString("id_user"), rs.getString("recherche"),
									rs.getString("id_recherche"), rs.getDate("date")));
						}
					} finally {
						try { rs.close(); } catch (Throwable ignore) {}
					}
					results = stmt.getMoreResults();
				}
			} catch (SQLException e) {
				return listSearch;
			}
		}
		return listSearch;
	}

	public User deleteAccountUser(String id_user) {
		if(id_user != null && ! id_user.isEmpty()){
			Statement stmt;
			try {
				stmt = connection.createStatement();
				
				String sqlRequest = "DELETE  from "+USER_DB_NAME+" "
						+ "WHERE id_user='"+id_user+"';";
		
				stmt.execute(sqlRequest);
				stmt.close();
				deleteSearchUser(id_user);
				return null;
			} catch (SQLException e) {
				System.out.println("deleteAccountUser a rencontré un problème");
				return null;
			}
		}
		return null;
	}
		
	public void deleteSearchUser(String id_user){
		if(id_user != null && ! id_user.isEmpty()){
			Statement stmt;
			try {
				stmt = connection.createStatement();

				String sqlRequest = "DELETE  from "+SEARCH_DB_NAME+" "
						+ "WHERE id_user='"+id_user+"';";
		
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