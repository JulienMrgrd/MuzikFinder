package sql.mysql;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLService {
	
	// TODO : ajouter les variables globales issues du constructeur
	Connection connection;
	private static final String USER_DB_NAME = "user";
	private static final String SEARCH_DB_NAME = "search";

	public MySQLService() throws URISyntaxException, SQLException {
		URI dbUri = new URI("mysql://bd54e4b71469bc:231e3b61@us-cdbr-iron-east-04.cleardb.net/heroku_e819ab8bca89a87?reconnect=true");

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
	            + "   email	           VARCHAR(30))";

	    Statement stmt = connection.createStatement();
	    stmt.execute(sqlCreate);
		 stmt.close();
	}
	
	private void createTableSearch() throws SQLException {
	    String sqlCreate = "CREATE TABLE IF NOT EXISTS " + SEARCH_DB_NAME
	    		+ " ( id_user          INTEGER ,"
	            + "   recherche        VARCHAR(50),"
	            + "   date             VARCHAR(20))";

	    Statement stmt = connection.createStatement();
	    stmt.execute(sqlCreate);
		stmt.close();
	}
	
	public boolean insertNewUser(String pseudo, String password, String email) throws SQLException{

		Statement stmt = connection.createStatement();

		String sqlRequest = "SELECT * from user "+USER_DB_NAME+" where pseudo = '"+pseudo+"';";

		boolean results = stmt.execute(sqlRequest);

		while (results) {
			ResultSet rs = stmt.getResultSet();
			try {
				while (rs.next()) {
					//si on entre dans cette boucle c'est que le pseudo est déjà prit
					return false;
				}
			} finally {
				try { rs.close(); } catch (Throwable ignore) {}
			}

			// Parcourir les autres resultat de la requête si il y en a
			results = stmt.getMoreResults();
		}

		sqlRequest = "INSERT INTO "+USER_DB_NAME+"(pseudo,password,email) VALUES('"
				+ pseudo+"','"+password+"','"+email+"');";

		stmt.execute(sqlRequest);
		stmt.close();
		return true;
	}
	
	public boolean verifyConnexion(String pseudo, String password) throws SQLException{

		Statement stmt = connection.createStatement();

		String sqlRequest = "SELECT * from user "+USER_DB_NAME+" where pseudo = '"+pseudo+"' and password = '"+password+"';";

		boolean results = stmt.execute(sqlRequest);

		while (results) {
			ResultSet rs = stmt.getResultSet();
			try {
				while (rs.next()) {
					//si on entre dans cette boucle c'est que le pseudo et le password match bien
					return true;
				}
			} finally {
				try { rs.close(); } catch (Throwable ignore) {}
			}

			// Parcourir les autres resultat de la requête si il y en a
			results = stmt.getMoreResults();
		}
		stmt.close();
		return false;
	}
	
	
	
}
