/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseStructure;
import rdr.cases.CaseSynchroniser;
import rdr.db.RDRDBManager;
import rdr.logger.Logger;
import rdr.model.IAttribute;
import rdr.rules.RuleLoader;
import rdr.db.RDRDBManager;
import rdr.mysql.MysqlConnection;
import rdr.workbench.Workbench;

/**
 * This class is used to load and save domain 
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class DomainLoader {
    
	//domain loading,
	//caseStructure 비교(arff and db), 추가된 attribute를 db 에 추가
	//arff의 case data loading -> caseId
	//
	
	/**
	 * domain loading, 
	 * caseStructure 비교(arff vs. db), 추가된 attribute를 db에 반영 -> isCaseImport
	 * rule loading and set rule tree
	 * arff의 case data loading -> 1개만 존재하고 id는 caseId를 set
	 * @param domainName
	 * @param isCaseImport
	 * @param caseId
	 * @throws Exception
	 */
    public static boolean openDomainFileWithTempArff(String domainName, boolean isCaseImport, int caseId) throws Exception {
   
        if (DomainLoader.setDomainDetails(domainName) == false )
        	return false;
        
        Main.domain.setCaseStructure(CaseLoader.loadCaseStructureFromDB());
        
        Logger.info("CaseStructure from DB : " + Main.domain.getCaseStructure().getAttrAmount());
        
        //arff, db의 caseStructure를 비교하고 추가된 Attribute를 db에 추가, rule tree loading
        if(isCaseImport){            
            CaseStructure arffCaseStructure = CaseLoader.getArffCaseStructure();
            
            Logger.info("CaseStructure from arff : " + arffCaseStructure.getAttrAmount());
            Logger.info("sychronising case structure.");
            
            CaseSynchroniser aCaseSynchroniser = new CaseSynchroniser();
            CaseStructure newCaseStructure 
              = aCaseSynchroniser.getNewStructureByComparingTwoStructure(arffCaseStructure,
            		                                                     Main.domain.getCaseStructure());
            
            Main.domain.setCaseStructure(newCaseStructure);
        }
        
        RuleLoader.setRules(Main.domain.getCaseStructure());
        
        //temp arff file only has one case so case load with 1 and set the caseId later.
        Case aCase = CaseLoader.caseLoad(1, null);
        aCase.setCaseId(caseId);
        //add case in to main case set (allCaseSet)
        Main.allCaseSet.addCase(aCase);
        
        return true;
    }
    
    /**
     * domain loading, 
	 * caseStructure 비교(arff vs. db), 추가된 attribute를 db에 반영 -> isCaseImport
	 * rule loading and set rule tree
     * @param domainName
     * @param isCaseImport
     * @throws Exception
     */
    public static boolean openDomainFile(String domainName, boolean isCaseImport) throws Exception {
    
        if (DomainLoader.setDomainDetails(domainName) == false)
        	return false;
        
        Main.domain.setCaseStructure(CaseLoader.loadCaseStructureFromDB());
        
        if(isCaseImport){            
            CaseStructure arffCaseStructure = CaseLoader.getArffCaseStructure();
            CaseSynchroniser aCaseSynchroniser = new CaseSynchroniser();
            CaseStructure newCaseStructure 
              = aCaseSynchroniser.getNewStructureByComparingTwoStructure(arffCaseStructure,
            		                                                     Main.domain.getCaseStructure());
            
            Main.domain.setCaseStructure(newCaseStructure);
        }
        
        RuleLoader.setRules(Main.domain.getCaseStructure());
        return true;
    }
    
    /**
     * domain loading, 
	 * caseStructure 비교(arff vs. db), 추가된 attribute를 db에 반영 -> isCaseImport
	 * arff 파일의 모든 case data 를 loading
	 * rule loading and set rule tree
     * @param domainName
     * @param isCaseImport
     * @throws Exception
     */
    public static boolean openDomainFileWithCaseImport(String domainName, 
    		                                           boolean isCaseImport, 
    		                                           int caseCount) throws Exception {
 
        if (DomainLoader.setDomainDetails(domainName) == false)
        	return false;
        
        Main.domain.setCaseStructure(CaseLoader.loadCaseStructureFromDB());
        
        if(isCaseImport){            
            CaseStructure arffCaseStructure = CaseLoader.getArffCaseStructure();
            CaseSynchroniser aCaseSynchroniser = new CaseSynchroniser();
            CaseStructure newCaseStructure 
            	= aCaseSynchroniser.getNewStructureByComparingTwoStructure(arffCaseStructure,
            			                                                   Main.domain.getCaseStructure());
            
            Main.domain.setCaseStructure(newCaseStructure);
            
            CaseLoader.caseImport(newCaseStructure, caseCount, null);
            RuleLoader.setRules(Main.domain.getCaseStructure());
        }
        
        return true;
    }
    
    /**
     * domain loading, 
	 * rule loading and set rule tree
     * @param domainName
     * @throws Exception
     */
    public static void reloadDomainFile(String domainName) throws Exception {
  
        DomainLoader.setDomainDetails(domainName);

        Main.domain.setCaseStructure(CaseLoader.loadCaseStructureFromDB());
        RuleLoader.setRules(Main.domain.getCaseStructure());
    }
    
    public static boolean inserDomainDetails(String domainName, 
    		                                 String domainDesc,
    		                                 String domainReasoner)
    {
        return RDRDBManager.getInstance().insertDomainDetails(domainName, 
        		                                              domainDesc, 
        		                                              domainReasoner);        
    }
    
    /**
     * Set domain details - from Database
     * @param domainName 
     */
    public static boolean setDomainDetails(String domainName)
    {
    	ArrayList<HashMap<String, String>> domainList = RDRDBManager.getInstance().getDomainDetails(domainName);
        
        if ( domainList == null || domainList.size() == 0)
        {
        	Main.domain.setDomainName(domainName);
            Main.domain.setDescription(domainName);
            Main.domain.setReasonerType("SCRDR");
            Main.workbench = new Workbench("SCRDR");
            Logger.error("domainDetails loading failed");
            return false;
        }
        else
        {
        	HashMap<String, String> domainDetails = domainList.get(0);
        	
            Main.domain.setDomainName(domainDetails.get("domainName"));
            Main.domain.setDescription(domainDetails.get("domainDesc"));
            Main.domain.setReasonerType(domainDetails.get("domainReasoner"));
            Main.workbench = new Workbench(domainDetails.get("domainReasoner"));
            return true;
        } 
    }
    
    /** return null(error), empty string(domain table is empty) */
    public static String getDefaultDomainName()
    {
    	ArrayList<String> domains = RDRDBManager.getInstance().getDomainNames();
    	if (domains == null) return null;
    	if (domains.size() > 0) return domains.get(0);
    	else return "";
    }
                
    /**
     * //20170912 added by ucciri
    public static void checkArffChange( CaseStructure aCaseStructure ) throws Exception
    {
    	CaseStructure recentCaseStructure = CaseLoader.getRecentArffCaseStructure();
    	
    	boolean needCopy = false;

    	if (recentCaseStructure == null)
    	{
    		Logger.warn("recent arff file is not found");
    	    needCopy = true;
    	}
    	else
    	{
	    	{
	    		LinkedHashMap<String, IAttribute> map = aCaseStructure.getBase();
	    		Iterator<String> keys = map.keySet().iterator();
	    		while ( keys.hasNext() )
	    		{
	    			String key = keys.next();
	    			IAttribute attr = (IAttribute)map.get(key);
	    			if ( !recentCaseStructure.isAttributeExist(attr.getName()))
	    			{
	    				Logger.warn("CheckArff, [" + attr.getName() + "] is added in new arff");
	    				needCopy = true;
	    			}
	    		}
	    	}
	    	
	    	{
	    		LinkedHashMap<String, IAttribute> map = recentCaseStructure.getBase();
	    		Iterator<String> keys = map.keySet().iterator();
	    		while ( keys.hasNext() )
	    		{
	    			String key = keys.next();
	    			IAttribute attr = (IAttribute)map.get(key);
	    			if ( !aCaseStructure.isAttributeExist(attr.getName()))
	    			{
	    				Logger.warn("CheckArff, [" + attr.getName() + "] is deleted in new arff");
	    				needCopy = true;
	    			}
	    		}
	    	}
    	}
    	  
    	if ( needCopy )
    	{
	    	Calendar cal = Calendar.getInstance();
	    	String curTime = String.format("%04d%02d%02d%02d%02d%02d", cal.get(Calendar.YEAR),
	    			                                                  cal.get(Calendar.MONTH)+1,
	    			                                                  cal.get(Calendar.DAY_OF_MONTH),
	    			                                                  cal.get(Calendar.HOUR_OF_DAY),
	    			                                                  cal.get(Calendar.MINUTE),
	    			                                                  cal.get(Calendar.SECOND));
	    	String bakFn = System.getProperty("user.dir") + "/domain/cases/temp_" + curTime + ".arff";
	    	String recentFn = System.getProperty("user.dir") + "/domain/cases/recent.arff";
	    	String tempFn = System.getProperty("user.dir") + "/domain/cases/temp.arff";
	
	    	copyFile( tempFn, recentFn );
	    	copyFile( tempFn, bakFn );
	    	
	    	Logger.info("CheckArff, arff file copied to recent arff and " + bakFn);
    	}    	
    }
    
    //20170912 added by ucciri, @todo move to Utility
    private static void copyFile(String srcFn, String destFn) throws IOException {
    	
    	File source = new File(srcFn);
    	File dest = new File(destFn);
    	
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
        	Logger.error("File Copy Failed, src[" + srcFn + "] dest[" + destFn + "]");
        	Logger.error( e.getClass().getName() + ": " + e.getMessage() );
        } finally {
            is.close();
            os.close();
        }
    }
    */
       
    
}
