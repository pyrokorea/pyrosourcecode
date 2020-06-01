package rdr.sqlite;

import rdr.db.RDRDBManager;
import rdr.sqlite.SqliteConnection;

/**
 *
 * @author Kim Woo-Cheol (ucciri@gmail.com)
 */
public class  SqliteDBManager extends RDRDBManager {
	
	public SqliteDBManager()
	{
		;
	}
	
	@Override
	public boolean setConnection()
	{
		boolean flag = SqliteConnection.connect(dbIndex);
		c = SqliteConnection.connection;
		return flag;
	}

	
}
    

