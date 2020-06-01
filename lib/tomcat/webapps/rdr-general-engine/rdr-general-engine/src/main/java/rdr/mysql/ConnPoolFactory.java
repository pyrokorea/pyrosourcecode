package rdr.mysql;
/**
 * Factory class which creates concrete connection pool instance.
 * 
 * @author Jason
 * @since ver 1.0.0
 */
public class ConnPoolFactory {
	private static ManualConnPool manConnPool;
	
	public static synchronized ConnPool getConnectionPool( ConnPoolProperty poolProperty ){
		
		// TODO : should be changed.
		if( manConnPool == null )
			manConnPool = new ManualConnPool( poolProperty );
		
		return manConnPool;
	}
}
