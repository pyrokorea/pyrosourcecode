package rdr.mysql;

import rdr.db.RDRDBManager;

/**
 *
 * @author Kim Woo-Cheol (ucciri@gmail.com)
 */
public class MysqlDBManager extends RDRDBManager{
	
	public MysqlDBManager()
	{
		;
	}
	
	@Override
	public boolean setConnection()
	{
		boolean flag = MysqlConnection.connect(dbIndex);
		c = MysqlConnection.connection;
		return flag;
	}
	
}
    

