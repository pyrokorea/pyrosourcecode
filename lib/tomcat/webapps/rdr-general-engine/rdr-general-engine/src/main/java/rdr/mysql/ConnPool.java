package rdr.mysql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Common class for the jdbc connection pool. 
 * This class might be used both web and non-web. In case of web, a jdbc connection will be get
 * through JNDI lookup. In case of non web, a jdbc connection will be get through<br>
 * manual connection pool. <br>
 * 
 * Connection pool instance would only be created through ConnPoolFactory class. 
 * 
 * @author Jason
 * @since ver 1.0.0
 */
public abstract class ConnPool {
	
	/**
	 * Get a jdbc connection. 
	 * Once a connection is obtained, it should be closed using closeConnection() method
	 * 
	 * @return a jdbc connection.
	 */
	public abstract Connection getConnection() throws SQLException;
	
	/**
	 * Close used jdbc connection
	 * 
	 * @param conn
	 */
	public abstract void closeConnection( Connection conn ) throws SQLException;
	
	
}
