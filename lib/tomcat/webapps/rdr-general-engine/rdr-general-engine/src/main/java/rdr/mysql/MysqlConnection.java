/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.mysql;

import java.sql.*;
import rdr.logger.Logger;

/**
 *
 * @author David Chung
 */
public class MysqlConnection {

	public static Connection connection;

    public static boolean connect(int pIndex)
    {
    	connection = (Connection) new MakeDAO().getConnection(pIndex);
    	
    	if (connection == null)
    	{
    		Logger.error( "DB Connection Failed, so exit" );
            return false;
    	}
    	else 
    	{
    		Logger.info("Opened database successfully, MysqlConnection");
    		return true;
    	}
    	
    	
    	/**
        try {
            connection = (Connection) new MakeDAO().getConnection();
            
        } catch ( Exception e ) {
            Logger.error( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        Logger.info("Opened database successfully, MysqlConnection");
        */
    }
}
