package rdr.test;

import org.apache.log4j.PropertyConfigurator;

import rdr.apps.Main;
import rdr.cases.CaseStructure;
import rdr.db.RDRDBManager;
import rdr.db.SqlReader;
import rdr.utils.RDRConfig;

public class TestDB {

	public static void main(String[] args) {
    	
    	String log4jConfPath = "./log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
    
        test();
        //loadSql();
	}

	private static void test()
	{
		String rootPath = "C:\\Dev\\eclipse\\cfg\\";
		RDRConfig.initWithRootPath("rdr.cfg", rootPath);
		Main.domainName = "TestDomain";
		CaseStructure cs = RDRDBManager.getInstance().getCaseStructure();
		System.out.println(cs.toString());
	}
	
	private static void loadSql()
	{
		RDRConfig.init("ekp");
		//System.out.println(new SqlReader("test.sql").get());
	}

}
