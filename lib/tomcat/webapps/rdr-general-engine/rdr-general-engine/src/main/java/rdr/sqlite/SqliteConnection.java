/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.sqlite;

import java.io.File;
import java.sql.*;
import rdr.logger.Logger;
import rdr.utils.RDRConfig;

/**
 *
 * @author David Chung
 */
public class SqliteConnection {

    public static Connection connection;
    
    public static boolean connect(int pIndex)
    {
        try 
        {
            Class.forName("org.sqlite.JDBC");
            
            StringBuilder sb = new StringBuilder();
            sb.append("jdbc:sqlite:");
            sb.append(RDRConfig.getDomainPath());
            sb.append(pIndex == 0 ? RDRConfig.getDefaultSqliteFile() 
            		              : RDRConfig.getSecondarySqliteFile());
            
            connection = (Connection) DriverManager.getConnection(sb.toString());
        } 
        catch ( Exception e ) 
        {
            Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
            return false;
        }
        
        Logger.info("Opened database successfully, SqliteConnection");
        return true;
    }
}
