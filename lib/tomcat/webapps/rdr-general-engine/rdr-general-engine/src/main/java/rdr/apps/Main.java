/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.apps;

import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.PropertyConfigurator;
import rdr.cases.CaseSet;
import rdr.cases.CornerstoneCaseSet;
import rdr.db.RDRDBManager;
import rdr.gui.StartupFrame;
import rdr.mysql.MysqlConnection;
import rdr.domain.Domain;
import rdr.domain.DomainLoader;
import rdr.gui.MainFrame;
import rdr.rules.Rule;
import rdr.rules.RuleBuilder;
import rdr.rules.RuleSet;
import rdr.utils.RDRConfig;
import rdr.workbench.Workbench;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class Main 
{
    public static Domain domain;
    public static Workbench workbench;
    public static CaseSet allCaseSet = new CaseSet();
    public static CornerstoneCaseSet allCornerstoneCaseSet = new CornerstoneCaseSet();       
    public static RuleSet KB;        
    public static String domainName; 
    public static String userid = "unknown";
    
    public static File loadedFile;
    public static String[] missingValueArray = new String[] {"NULL","", "na", "NA"};
    public static CaseSet testingCaseSet;
    
    public static void main(String[] args) throws Exception {
    	
	    RDRConfig.init();
		//RDRDBManager.getInstance();
		
	    System.out.println("Welcome. This is RDR engine for EKP ver1.0");
                
        String log4jConfPath = "./log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
        
        //Define doamin
        domainName = "stomach";
        
        domain = new Domain (domainName, "", "");
        allCaseSet = new CaseSet();
        testingCaseSet = new CaseSet();
        KB = new RuleSet();
        Rule rootRule = RuleBuilder.buildRootRule();
        KB.setRootRule(rootRule);
        
//        StartupFrame.execute();
        DomainLoader.openDomainFileWithCaseImport(domainName, true, 100);
        MainFrame.execute(false, domainName,Main.domain.getReasonerType());
    }
}
