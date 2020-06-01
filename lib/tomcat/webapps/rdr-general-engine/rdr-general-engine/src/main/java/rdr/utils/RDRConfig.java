package rdr.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import rdr.db.RDRDBManager;
import rdr.logger.Logger;

public class RDRConfig 
{
	private static String serviceName;
	
	private static final String DBTYPE = "databaseType";
	private static final String SQLITEFILE = "sqliteFile";
	private static final String DBDRIVER = "databaseDriver";
	private static final String DBURL = "dbURL";
	private static final String USEUNICODE = "useUnicode";
	private static final String CHARENCODING = "characterEncoding";
	private static final String DBNAME = "databaseName";
	private static final String USER = "user";
	private static final String PASS = "password";
	private static final String ARFFNAME = "arffFile";
	private static final String TESTARFFNAME = "testArffFile";
	private static final String NULLVALUE = "nullValue";
	
	private static boolean initialized = false;
	
	private static String cfgFn;

	private static String dbType;
	
	private static String [] sqliteFiles = new String[2];
	
	private static String dbDriver;
	private static String dbURL;
	private static String useUnicode;
	private static String characterEncoding;
	private static String [] dbNames = new String[2];
	private static String user;
	private static String pass;
	
	private static String rootPath;
	private static String domainPath;
	private static String arffPath;
	private static String sqlPath;
	private static String outPath;
	private static String logPath;
	
	private static String arffFile;
	private static String testArffFile;
	
	private static ArrayList<String> nullValues;	
	private static double rangeRatio;
	private static boolean bStrictValidationSCRDR;
	private static boolean bDebugInputCase;
	private static boolean bDebugRequest;
	private static boolean bDebugInference;
	private static boolean bDebugSql;
	
	private static String getKey(String dbType, String key)
	{
		return dbType + "_" + key;
	}

	public static boolean init()
	{
		String sn = "ekp";
		return init(sn);
	}
	
	public static boolean init(String pServiceName)
	{
		pServiceName = pServiceName.replaceAll("/", "");
		
		String fn = String.format("%s.cfg", pServiceName);
		//String fn = String.format("rdr_%s.cfg", pServiceName);
		return init(fn, pServiceName);
	}
	
	public static boolean initWithRootPath(String pServiceName, String pRootPath)
	{
		pServiceName = pServiceName.replaceAll("/", "");
		
		rootPath = pRootPath;
		
		String fn = String.format("%s.cfg", pServiceName);
		//String fn = String.format("rdr_%s.cfg", pServiceName);
		return init(fn, pServiceName);
	}
		
	public static boolean init(String pCfgFn, String pServiceName)
	{
		if (initialized)
			return true;
		
		serviceName = pServiceName;
		cfgFn = pCfgFn;   
		
		if (rootPath == null || rootPath.isEmpty())
		{
			rootPath = System.getProperty("user.dir") + File.separator + "cfg";
		}
		
		rootPath = rootPath + File.separator;
		
		domainPath = rootPath + pServiceName + File.separator + "domain" + File.separator;
		arffPath = domainPath + "cases" + File.separator;
		sqlPath = rootPath + "sql" + File.separator;
		outPath = rootPath + pServiceName + File.separator + "out" + File.separator;
		logPath = rootPath + pServiceName + File.separator + "log" + File.separator;
		
		String fn = rootPath + pCfgFn;
		
		try 
		{
			FileInputStream fis = new FileInputStream(fn);
			Properties inProp = new Properties();
			inProp.load(fis);
			
			dbType = inProp.getProperty(DBTYPE);
			
			sqliteFiles[0] = inProp.getProperty(SQLITEFILE);
			sqliteFiles[0] = inProp.getProperty(SQLITEFILE + "2");
			dbDriver = inProp.getProperty(RDRConfig.getKey(dbType, DBDRIVER));
			dbURL = inProp.getProperty(RDRConfig.getKey(dbType, DBURL));
			useUnicode = inProp.getProperty(RDRConfig.getKey(dbType, USEUNICODE));
			characterEncoding = inProp.getProperty(RDRConfig.getKey(dbType, CHARENCODING));
			dbNames[0] = inProp.getProperty(RDRConfig.getKey(dbType, DBNAME));
			dbNames[1] = inProp.getProperty(RDRConfig.getKey(dbType, DBNAME) + "2");
			user = inProp.getProperty(RDRConfig.getKey(dbType, USER));
			pass = inProp.getProperty(RDRConfig.getKey(dbType, PASS));
			
			arffFile = domainPath + "cases" + File.separator + inProp.getProperty(ARFFNAME);
			testArffFile = domainPath + "cases" + File.separator + inProp.getProperty(TESTARFFNAME); 
			
			nullValues = new ArrayList<String>();
			String tmp = inProp.getProperty(NULLVALUE);
			ArrayList<String> tmps = Utility.convertCSVStringToArray(tmp, ",");
			for (int i = 0; i < tmps.size(); i++)
			{
				nullValues.add(tmps.get(i));
			}
			
			rangeRatio = Double.parseDouble(inProp.getProperty("rangeRatio"));
			bStrictValidationSCRDR = (inProp.getProperty("strictValidationSCRDR").equals("Y"));
			bDebugInputCase = (inProp.getProperty("debug_inputCase").equals("Y"));
			bDebugRequest = (inProp.getProperty("debug_request").equals("Y"));
			bDebugInference = (inProp.getProperty("debug_inference").equals("Y"));
			bDebugSql = (inProp.getProperty("debug_sql").equals("Y"));

			fis.close();
			
		}
		catch (Exception e)
		{
			System.out.println(e);
			String msg = "cfg file loading failed :	" + fn;
			System.out.println(msg + " " + e);
			return false;
		}
		
		String log4jConfPath = getLogPath() + "log4j.properties";
	    PropertyConfigurator.configure(log4jConfPath);
		
		if (checkDBConfig() == false)
		{
			Logger.error("DB Config is invalid : " + fn);
			System.out.println("DB Config is invalid : " + fn);
			return false;
		}
		
		RDRConfig.print();
		
		initialized = true;
		return true;
	}
	
	public static void print()
	{
		Logger.info("CfgFile : " + cfgFn);
		Logger.info(" " + DBTYPE + " : " + dbType);
		Logger.info(" " + SQLITEFILE + " : " + Arrays.toString(sqliteFiles));
		Logger.info(" " + DBDRIVER + " : " + dbDriver);
		Logger.info(" " + DBURL + " : " + dbURL);
		Logger.info(" " + USEUNICODE + " : " + useUnicode);
		Logger.info(" " + CHARENCODING + " : " + characterEncoding);
		Logger.info(" " + DBNAME + " : " + Arrays.toString(dbNames));
		Logger.info(" " + USER + " : " + user);
		Logger.info(" " + PASS + " : " + pass);
		Logger.info(" rootPath   : " + rootPath);
		Logger.info(" domainPath : " + domainPath);
		Logger.info(" sqlPath    : " + sqlPath);
		Logger.info(" logPath    : " + logPath);
		Logger.info(" arffFile   : " + arffFile);
		Logger.info(" testArffFile : " + testArffFile);
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nullValues.size(); i++)
		{
			sb.append("[" + nullValues.get(i) + "] ");
		}
		Logger.info(" nullValues : " + sb.toString());
		
		Logger.info("rangeRatio : " + rangeRatio);
		Logger.info("strictValidationSCRDR : " + (bStrictValidationSCRDR ? "Y" : "N"));
		Logger.info("debug_inputCase : " + (bDebugInputCase ? "Y" : "N"));
		Logger.info("debug_reqeust : " + (bDebugRequest ? "Y" : "N"));
		Logger.info("debug_inference : " + (bDebugInference ? "Y" : "N"));
		Logger.info("debug_sql : " + (bDebugSql ? "Y" : "N"));
	}
	
	public static boolean checkDBConfig()
	{
		if (dbType == null || dbType.isEmpty()) return false;
		
		if (dbType.equals("sqlite"))
		{
			if (sqliteFiles[0] == null || sqliteFiles[0].isEmpty()) return false;
		}
		else
		{
			if (dbDriver == null || dbDriver.isEmpty() ||
				dbURL == null || dbURL.isEmpty() ||
				dbNames[0] == null || dbNames[0].isEmpty() ||
				user == null || user.isEmpty() ||
				pass == null || pass.isEmpty() )
			{
				return false;
			}
			
			if ((RDRConfig.getDBType().equals(RDRDBManager.MYSQL) ||
			     RDRConfig.getDBType().equals(RDRDBManager.MARIADB)) &&
				(useUnicode == null || useUnicode.isEmpty() ||
				 characterEncoding == null || characterEncoding.isEmpty() ))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static String getServiceName()
	{
		return serviceName;
	}
	
	public static String getDBType()
	{
		return dbType;
	}
	
	public static String getDefaultSqliteFile()
	{
		return sqliteFiles[0];
	}
	
	public static String getSecondarySqliteFile()
	{
		return sqliteFiles[1];
	}

	public static String getDBDriver()
	{
		return dbDriver;
	}

	public static String getDBUrl()
	{
		return dbURL;
	}

	public static String getUseUnicode()
	{
		return useUnicode;
	}

	public static String getCharacterEncoding()
	{
		return characterEncoding;
	}

	public static String getDefaultDBName()
	{
		return dbNames[0];
	}
	
	public static String getSecondaryDBName()
	{
		return dbNames[1];
	}

	public static String getUser()
	{
		return user;
	}

	public static String getPassword()
	{
		return pass;
	}

	public static String getRootPath()
	{
		return rootPath;
	}

	public static String getDomainPath()
	{
		return domainPath;
	}
	
	public static String getArffPath()
	{
		return arffPath;
	}

	public static String getSqlPath()
	{
		return sqlPath;
	}
	
	public static String getOutPath()
	{
		return outPath;
	}
	
	public static String getLogPath()
	{
		return logPath;
	}
	
	public static String getArffFile()
	{
		return arffFile;
	}

	public static String getTestArffFile()
	{
		return testArffFile;
	}

	public static void setDBType(String pType)
	{
		dbType = pType;
	}
	
	public static void setDefaultSqliteFile(String pFn)
	{
		sqliteFiles[0] = pFn;
	}
	
	public static void setSecondarySqliteFile(String pFn)
	{
		sqliteFiles[1] = pFn;
	}

	public static void setDBDriver(String pDriver)
	{
		dbDriver = pDriver;
	}

	public static void setDBUrl(String pUrl)
	{
		dbURL = pUrl;
	}

	public static void setUseUnicode(String pUseUnicode)
	{
		useUnicode = pUseUnicode;
	}

	public static void setCharacterEncoding(String pCharEnc)
	{
		characterEncoding = pCharEnc;
	}

	public static void setDefaultDBName(String pName)
	{
		dbNames[0] = pName;
	}
	
	public static void setSecondaryDBName(String pName)
	{
		dbNames[1] = pName;
	}

	public static void setUser(String pUser)
	{
		user = pUser;
	}

	public static void setPassword(String pPass)
	{
		pass = pPass;
	}

	public static void setDomainPath(String pPath)
	{
		domainPath = pPath;
	}
	
	public static void setArffPath(String pPath)
	{
		arffPath = pPath;
	}

	public static void setSqlPath(String pPath)
	{
		sqlPath = pPath;
	}		
	
	public static void setArffFile(String fn)
	{
		arffFile = domainPath + "cases" + File.separator + fn; 
	}
	
	public static void setTestArffFile(String fn)
	{
		testArffFile = domainPath + "cases" + File.separator + fn; 
	}
	
	public static ArrayList<String> getNullValues()
	{
		return nullValues;
	}
	
	public static boolean isNullValueString(String str)
	{
		for (int i = 0; i < nullValues.size(); i++)
		{
			if (str.equals(nullValues.get(i)))
				return true;
		}
		return false;
	}
	
	public static String getRepNullValueString()
	{
		if (nullValues.isEmpty())
		{
			return null;
		}
		else
		{
			return nullValues.get(0);
		}
	}
	
	public static double getRangeRatio()
	{
		return rangeRatio;
	}
	
	public static boolean isStrictValidationSCRDR()
	{
		return bStrictValidationSCRDR;
	}
	
	public static boolean isDebugInputCase()
	{
		return bDebugInputCase;
	}
	
	public static boolean isDebugRequest()
	{
		return bDebugRequest;
	}
	
	public static boolean isDebugInference()
	{
		return bDebugInference;
	}
	
	public static boolean isDebugSql()
	{
		return bDebugSql;
	}

}
