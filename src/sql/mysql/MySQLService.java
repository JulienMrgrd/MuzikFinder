package sql.mysql;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLService {
	
	// TODO : ajouter les variables globales issues du constructeur

	public MySQLService() throws URISyntaxException, SQLException {
		URI dbUri = new URI("mysql://bd54e4b71469bc:231e3b61@us-cdbr-iron-east-04.cleardb.net/heroku_e819ab8bca89a87?reconnect=true");

	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();
	    System.out.println(username);
	    System.out.println(password);
	    System.out.println(dbUrl);
		Connection connection = DriverManager.getConnection(dbUrl, username, password);
		System.out.println(connection.getClientInfo());
		connection.close();
	}
	
}
