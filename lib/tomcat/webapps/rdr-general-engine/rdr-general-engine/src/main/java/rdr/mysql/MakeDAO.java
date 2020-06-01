/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.mysql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import rdr.db.RDRDBManager;
import rdr.logger.Logger;
import rdr.utils.RDRConfig;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class MakeDAO {
	public ConnPool connPool;
	ConnPoolProperty prop;

	// connection object
	Connection connection;

	public static boolean isDBSwitched = false;
	
	public MakeDAO()
	{
		this.prop = new ConnPoolProperty();
	}
	
	public Connection getConnection(int pIndex) {
		if (isDBSwitched == false) {
			connection = getDefaultConnection(pIndex);
		} else {
			connection = getSwitchedConnection();
		}
		
		return connection;
	}

	public Connection getDefaultConnection(int pIndex) 
	{
		StringBuilder sb = new StringBuilder();
		String dbName = (pIndex == 0 ? RDRConfig.getDefaultDBName() : RDRConfig.getSecondaryDBName());
		
		if (RDRConfig.getDBType().equals(RDRDBManager.MYSQL) ||
		    RDRConfig.getDBType().equals(RDRDBManager.MARIADB))
		{
			sb.append(RDRConfig.getDBUrl());
			sb.append(dbName);
			sb.append("?useUnicode="+RDRConfig.getUseUnicode());
			sb.append("&characterEncoding="+RDRConfig.getCharacterEncoding());
			sb.append("&autoReconnect=true");
			sb.append("&useSSL=false");
		}
		else if (RDRConfig.getDBType().equals(RDRDBManager.MSSQL))
		{
			String tUrl = RDRConfig.getDBUrl();
			if (tUrl != null && tUrl.length() > 0 && tUrl.charAt(tUrl.length() - 1) == '/') 
			{
				tUrl = tUrl.substring(0, tUrl.length() - 1);
		    }
			sb.append(tUrl);
			sb.append(";databaseName=");
			sb.append(dbName);
			sb.append(";");
		}
		

		//sb.append("jdbc:sqlserver://localhost:1433;databaseName=generic_biopsy;");
		//sb.append("jdbc:sqlserver://localhost:1433/generic_biopsy;");
		
		
		try 
		{
			Class.forName(RDRConfig.getDBDriver());
			
			connection = DriverManager.getConnection(sb.toString(),
					                                 RDRConfig.getUser(), 
					                                 RDRConfig.getPassword());
			
			Logger.info("default connection created....");
			Logger.info("DB : " + sb.toString());
		} catch (Exception ie) {
			//showMessageDialog(null, "failed to connect to the server.");
			System.out.println(ie);
			Logger.error("DB Connection Failed, Check DB Configuration : " + sb.toString(), ie);
			return null;
		}
		return connection;
	}

	public Connection getSwitchedConnection() {
		StringBuilder sb = new StringBuilder();
		sb.append(RDRConfig.getDBUrl());
		sb.append(File.separator);
		sb.append(RDRConfig.getDefaultDBName());
		sb.append("?useUnicode="+RDRConfig.getUseUnicode());
		sb.append("&characterEncoding="+RDRConfig.getCharacterEncoding());
		
		try {
			System.out.println("switched driver: " + RDRConfig.getDBDriver());
			System.out.println("switched url: " + RDRConfig.getDBUrl());
			System.out.println("switched db: " + RDRConfig.getDefaultDBName());
			System.out.println("switched user: " + RDRConfig.getUser());
			System.out.println("switched pass: " + RDRConfig.getPassword());

			Class.forName("com.mysql.jdbc.Driver");

			connection = DriverManager.getConnection(sb.toString(), 
                    RDRConfig.getUser(), 
                    RDRConfig.getPassword());

			Logger.info("switched connection created successfully....");
			Logger.info("DB : " + sb.toString());
		} catch (Exception ie) {
			System.out.println(ie);
		}

		return connection;
	}

	public Connection getPoolConnection() {

		StringBuilder sb = new StringBuilder();
		sb.append(RDRConfig.getDBUrl());
		sb.append(File.separator);
		sb.append(RDRConfig.getDefaultDBName());
		sb.append("?useUnicode="+RDRConfig.getUseUnicode());
		sb.append("&characterEncoding="+RDRConfig.getCharacterEncoding());
		
		prop.setUrl(sb.toString());
		prop.setDriverClass(RDRConfig.getDBDriver());
		prop.setUser(RDRConfig.getUser());
		prop.setPassword(RDRConfig.getPassword());
		prop.setInitSize(5);
		prop.setMaxSize(10);
		prop.setMaxWaitMillis(3000);

		connPool = ConnPoolFactory.getConnectionPool(prop);
		try {
			connection = connPool.getConnection();
		} catch (Exception ie) {
			System.out.println(ie);
		}
		return connection;
	}
}
