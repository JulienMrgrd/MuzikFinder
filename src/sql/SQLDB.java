package sql;

import java.net.URISyntaxException;
import java.sql.SQLException;

import sql.mysql.MySQLService;

/**
 * Appel les fonctions de la BD choisies (MySQL, Oracle, etc ...)
 * @author JulienM
 */
public class SQLDB {

	protected MySQLService mySqlService;
	
	public SQLDB() {
		try {
			mySqlService = new MySQLService();
		} catch (URISyntaxException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
