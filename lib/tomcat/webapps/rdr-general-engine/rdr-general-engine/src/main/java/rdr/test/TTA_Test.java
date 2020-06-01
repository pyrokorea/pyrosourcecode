/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseSet;
import rdr.cases.CaseStructure;
import rdr.cases.CornerstoneCaseSet;
import rdr.db.RDRDBManager;
import rdr.domain.Domain;
import rdr.domain.DomainLoader;
import rdr.gui.DomainEditorFrame;
import rdr.gui.MainFrame;
import rdr.gui.StartupFrame;
import rdr.learner.RDRLearner;
import rdr.logger.Logger;
import rdr.model.Attribute;
import rdr.model.AttributeFactory;
import rdr.model.CategoricalAttribute;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.model.ValueType;
import rdr.reasoner.MCRDRReasoner;
import rdr.reasoner.SCRDRReasoner;
import rdr.rules.Conclusion;
import rdr.rules.ConclusionSet;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Operator;
import rdr.rules.Rule;
import rdr.rules.RuleBuilder;
import rdr.rules.RuleSet;
import rdr.utils.ArffUtil;
import rdr.utils.RDRConfig;
import rdr.workbench.Workbench;

/**
 *
 * @author ucciri@gmail.com
 */
public class TTA_Test 
{
    public static void main(String[] args) {
    	
    	String log4jConfPath = "./log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
        
        //String rootPath = "C:\\Dev\\eclipse\\cfg\\";
		//RdrConfig.initWithRootPath("rdr.cfg", rootPath);
		RDRConfig.init("ekp");
		
		//checkInferenceResult();
		//checkInferenceTime();
		checkSolutionCount();
    }
    
    private static void checkInferenceResult()
    {
    	String[] domains = {"stomach", "intestine", "cervix" };
    	String[] arffs = {"biopsy_stomach.arff", "biopsy_intestine.arff", "biopsy_cervix.arff"};
    	
    	StringBuilder sbStomach = new StringBuilder();
    	StringBuilder sbIntestine = new StringBuilder();
    	StringBuilder sbCervix = new StringBuilder();
    	
    	try
    	{
	    	String fn = RDRConfig.getOutPath() + "tta03_inferenceResult.log";
	        
	        FileWriter writer = new FileWriter(new File(fn));
	        
	        int totMatchCnt = 0;
	        int totUnmatchCnt = 0;
	        
	    	for (int di = 0; di < domains.length; di++)
	    	{
		    	String domainName = domains[di];
		     
		    	StringBuilder sbTemp = new StringBuilder();
		    	if (di == 0) sbTemp = sbStomach;
		    	else if (di == 1) sbTemp = sbIntestine;
		    	else if (di == 2) sbTemp = sbCervix;
		    	
		        Main.domain = new Domain (domainName, "", "");
		        Main.allCaseSet = new CaseSet();
		        Main.testingCaseSet = new CaseSet();
		        Main.KB = new RuleSet();
		        Rule rootRule = RuleBuilder.buildRootRule();
		        Main.KB.setRootRule(rootRule);
		        
		        Main.domainName = domainName;
	        
	        	RDRConfig.setArffFile(arffs[di]);
	        	
		        if (DomainLoader.openDomainFileWithCaseImport(domainName, true, 0) == false)
		        {
		        	Logger.error("openDomainFileWithCaseImport failed");
		        	return;
		        }
		        
		        Main.workbench.setRuleSet(Main.KB);
		        
		        Set cases = Main.allCaseSet.getBase().entrySet();
		        Iterator caseIterator = cases.iterator();
		        		        
		        int matchCnt = 0;
		        int unmatchCnt = 0;
		        while (caseIterator.hasNext())
		        {
		        	Map.Entry me = (Map.Entry)caseIterator.next();
			    	Case aCase = (Case) me.getValue();
			    	
			    	Case currentCase = CaseLoader.caseLoad(aCase.getCaseId(), null);
		
			    	long startTime = System.nanoTime();
			    	
			        Main.workbench.setCaseForInference(currentCase);
			        Main.workbench.inference();
			        
			        String inferenceStr = "";
			        if ( Main.domain.getReasonerType().equals(Domain.SCRDR))
			        {
			        	Rule rule = (Rule)(Main.workbench.getInferenceResult());
			        	inferenceStr = rule.getConclusion().toString();
			        }
			        else
			        {
			        	RuleSet ruleSet = (RuleSet)(Main.workbench.getInferenceResult());
			        	ConclusionSet aConclusionSet = ruleSet.getConclusionSet();
			        	LinkedHashMap<String, Conclusion> aConclusionMap = aConclusionSet.getBase();
			        	Iterator<String> keys = aConclusionMap.keySet().iterator();
			        	while (keys.hasNext())
			        	{
			        		String key = keys.next();
			        		inferenceStr += aConclusionMap.get(key).toString();
			        		inferenceStr += ",";
			        	}
			        }
			        
			        String caseResult = currentCase.getValue("class").toString();

			       	if (caseResult.equals(inferenceStr))
			       		 //(caseResult.equals("NULL") && inferenceStr.equals("")) )
			       	{
			       		matchCnt++;
			       		totMatchCnt++;
			       	}
			       	else
			       	{
			       		unmatchCnt++;
			       		totUnmatchCnt++;
			       	}
			        	
		       		sbTemp.append("CaseID[" + currentCase.getCaseId() + "] " 
	        				      + "정답 [" + currentCase.getValue("class").toString()
			                      + "] 추론결과[" + inferenceStr
	        		              + "]" );
			        sbTemp.append(System.lineSeparator());
		        }
		        
		        writer.append("[" + domainName + "]----------------------------------------\n");
		        writer.append("총 사례수       : " + (matchCnt+unmatchCnt) + "\n");
	        	writer.append("일치 건수       : " + matchCnt + "\n");
	        	writer.append("불일치 건수     : " + unmatchCnt + "\n");
	        	writer.append("지식재현 정확도 : " + (double)matchCnt/(double)(matchCnt+unmatchCnt)*100.0 + " %\n");
	        }
	    	
	    	writer.append("[Total]----------------------------------------\n");
		    writer.append("총 사례수       : " + (totMatchCnt+totUnmatchCnt) + "\n");
	        writer.append("일치 건수       : " + totMatchCnt + "\n");
	        writer.append("불일치 건수     : " + totUnmatchCnt + "\n");
	        writer.append("지식재현 정확도 : " + (double)totMatchCnt/(double)(totMatchCnt+totUnmatchCnt)*100.0 + " %\n");
	    	
	        writer.append("[stomach infernece detail]---------------------------------\n");
	        writer.append(sbStomach.toString());
	        writer.append("[intestine infernece detail]---------------------------------\n");
	        writer.append(sbIntestine.toString());
	        writer.append("[cervix infernece detail]---------------------------------\n");
	        writer.append(sbCervix.toString());
	        
	    	writer.close();
	    	
	    	Logger.info("checkInferenceResult done");
    	}
    	catch (Exception ex) 
    	{
        	ex.printStackTrace();
        }
    }
    
    private static void checkInferenceTime()
    {
		String[] domains = {"stomach", "intestine", "cervix" };
		String[] arffs = {"biopsy_stomach.arff", "biopsy_intestine.arff", "biopsy_cervix.arff"};
		
		try
		{
	    	String fn = RDRConfig.getOutPath() + "tta04_inferenceTime.log";
	        
	        FileWriter writer = new FileWriter(new File(fn));
	        
	        int caseCnt = 0;
	        long totElapsedTime = 0;
	        int totRuleCnt = 0;
	        
	    	for (int di = 0; di < domains.length; di++)
	    	{
		    	String domainName = domains[di];
		                    
		        Main.domain = new Domain (domainName, "", "");
		        Main.allCaseSet = new CaseSet();
		        Main.testingCaseSet = new CaseSet();
		        Main.KB = new RuleSet();
		        Rule rootRule = RuleBuilder.buildRootRule();
		        Main.KB.setRootRule(rootRule);
		        
		        Main.domainName = domainName;
	        
	        	RDRConfig.setArffFile(arffs[di]);
	        	
		        if (DomainLoader.openDomainFileWithCaseImport(domainName, true, 0) == false)
		        {
		        	Logger.error("openDomainFileWithCaseImport failed");
		        	return;
		        }
		        
		        Main.workbench.setRuleSet(Main.KB);
		        
		        Set cases = Main.allCaseSet.getBase().entrySet();
		        Iterator caseIterator = cases.iterator();
		        	
		        while (caseIterator.hasNext())
		        {
		        	Map.Entry me = (Map.Entry)caseIterator.next();
			    	Case aCase = (Case) me.getValue();

			    	Case currentCase = CaseLoader.caseLoad(aCase.getCaseId(), null);
			    	caseCnt++;
		
			    	long startTime = System.nanoTime();
			    	
			        Main.workbench.setCaseForInference(currentCase);
			        Main.workbench.inference();
			        			        
			        long elapsedTime = System.nanoTime() - startTime;
			        totElapsedTime += elapsedTime;
			        
			        totRuleCnt += Main.workbench.getFiredRules().getSize();
		        }
	        }
	    	
	    	writer.append("총 사례수 : " + caseCnt + "\n");

        	writer.append("총 추론시간(ms) : " + totElapsedTime/1000000.0 + "\n" );
        	writer.append("추론에 사용된 총 Rule수 : " + totRuleCnt + "\n" );
        	writer.append("Rule당 추론시간(ms) : " + ((double)totElapsedTime/1000000.0 / (double)totRuleCnt) + "\n" );
        	
	    	writer.close();
	    	
	    	Logger.info("checkInferenceTime done");
		}
		catch (Exception ex) {
	    	ex.printStackTrace();
	    }

    }    
    
    private static void checkSolutionCount()
    {
    	StringBuilder sb = new StringBuilder();
    	
    	try
    	{
	    	String fn = RDRConfig.getOutPath() + "tta05_solutionCount.log";
	        
	        FileWriter writer = new FileWriter(new File(fn));

	        String domainName = "blood";
	        String arffFn = "bloodTest.arff";
		     	
		    Main.domain = new Domain (domainName, "", "");
		    Main.allCaseSet = new CaseSet();
		    Main.testingCaseSet = new CaseSet();
		    Main.KB = new RuleSet();
		    Rule rootRule = RuleBuilder.buildRootRule();
		    Main.KB.setRootRule(rootRule);
		        
		    Main.domainName = domainName;
	        
		    RDRConfig.setDefaultSqliteFile("bloodTest.db");
	        RDRConfig.setArffFile(arffFn);
	        	
		    if (DomainLoader.openDomainFileWithCaseImport(domainName, true, 0) == false)
		    {
		     	Logger.error("openDomainFileWithCaseImport failed");
		       	return;
		    }
		        
		    Main.workbench.setRuleSet(Main.KB);
		    
		    CornerstoneCaseSet ccSet 
		    	= RDRDBManager.getInstance().getCornerstoneCaseSet(Main.domain.getCaseStructure());
		        
		    Set cases = ccSet.getBase().entrySet();
		    Iterator caseIterator = cases.iterator();
		        		        
		    int i = 0;
		    int totSolCnt = 0;
		    while (caseIterator.hasNext())
		    {
		      	Map.Entry me = (Map.Entry)caseIterator.next();
			   	Case aCase = (Case) me.getValue();
			    	
			    Main.workbench.setCaseForInference(aCase);
			    Main.workbench.inference();
			        
			    String inferenceStr = "";
			    int solCnt = 0;
			    if ( Main.domain.getReasonerType().equals(Domain.SCRDR))
			    {
			      	Rule rule = (Rule)(Main.workbench.getInferenceResult());
			      	inferenceStr = rule.getConclusion().toString();
			    }
			    else
			    {
			      	RuleSet ruleSet = (RuleSet)(Main.workbench.getInferenceResult());
			      	
			      	if (ruleSet.isRuleExist(0))
			      	{
			      		solCnt = 0;
			      	}
			      	else
			      	{
				       	ConclusionSet aConclusionSet = ruleSet.getConclusionSet();
				       	LinkedHashMap<String, Conclusion> aConclusionMap = aConclusionSet.getBase();
				       	Iterator<String> keys = aConclusionMap.keySet().iterator();
				       	while (keys.hasNext())
				       	{
				       		String key = keys.next();
				       		inferenceStr += aConclusionMap.get(key).toString();
				       		inferenceStr += "/";
				       	}
				       	
				       	solCnt = aConclusionSet.getSize();
				       	
				       	sb.append("Case[" + (i+1) + "] solution(" + solCnt + ") : " + inferenceStr);
				       	sb.append(System.lineSeparator());
			      	}
			      	
			      	totSolCnt += solCnt;
			    }
			    
			    i++;
	        }
		    
		    String result = String.format("%.2f", (double)totSolCnt/(double)i);
	    	
		    writer.append("총 사례수 : " + i + "\n");
		    writer.append("총 Solution 수  : " + totSolCnt + "\n");
		    writer.append("사례당 제시해법 수 : " + result + "\n");
		    writer.append("[inference detail]-------------------------------------\n");
		    writer.append(sb.toString());
		    writer.close();
	    	
	    	Logger.info("checkSolutionCount done");
    	}
    	catch (Exception ex) 
    	{
        	ex.printStackTrace();
        }
    }
}
