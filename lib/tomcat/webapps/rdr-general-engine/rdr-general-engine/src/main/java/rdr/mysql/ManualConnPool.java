
package rdr.mysql;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * Connection pool implementation using apache dbcp.
 * 
 * @author Jason
 * @since ver 1.0.0
 */
public class ManualConnPool extends ConnPool{
	private BasicDataSource ds;
	
	public ManualConnPool( ConnPoolProperty poolProperty ){
		setupDataSource( poolProperty.getUrl(), poolProperty.getDriverClass(), poolProperty.getUser(), poolProperty.getPassword() );
		setupPoolProperties( poolProperty );
	}
	
	private void setupPoolProperties( ConnPoolProperty poolProperty ){
		if( poolProperty.getInitSize() > 0 && poolProperty.getMaxSize() > poolProperty.getInitSize() ){
			ds.setInitialSize( poolProperty.getInitSize() );
			ds.setMaxActive( poolProperty.getMaxSize() );
		}else{
			ds.setInitialSize( 5 );
			ds.setMaxActive( 20 );
		}
		
		ds.setMaxWait( poolProperty.getMaxWaitMillis() );
		if( poolProperty.getValidationQuery() != null ){
			ds.setTestOnReturn(true);
			ds.setTestWhileIdle(true);
			ds.setValidationQuery( poolProperty.getValidationQuery() );
		}
	}
	
	private void setupDataSource( String url, String driverClass, String user, String password ){
		ds = new BasicDataSource();
        ds.setDriverClassName( driverClass );
        ds.setUsername( user );
        ds.setPassword( password );
        ds.setUrl( url );
       // System.out.println("url===> "+ url);
	}

	@Override
	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	@Override
	public Connection getConnection() throws SQLException{
		return ds.getConnection();
	}
}
