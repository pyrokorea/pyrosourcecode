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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import rdr.domain.Domain;
import rdr.domain.DomainLoader;
import rdr.gui.DomainEditorFrame;
import rdr.gui.MainFrame;
import rdr.gui.StartupFrame;
import rdr.learner.RDRLearner;
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
import rdr.utils.RDRConstants;
import rdr.utils.StringUtil;
import rdr.workbench.Workbench;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;


public class Test {
    
    
    public static void main(String[] args) {
    	
    	String log4jConfPath = "./log4j.properties";
        //PropertyConfigurator.configure(log4jConfPath);
        
    	testDouble();
    	//testSetKey();
    	//testString();
        //testHashMap();
        //testArffEncoding();
        //arffTest();
        //runInferenceForTest();
        
        //String a = "\"hellow world\"";
        //System.out.println(a);
        
        //String s = String.format("rdr_%s.cfg", "ekp");
        //System.out.println(s);
        
        //String s = "1,234,567";
        //int i = Integer.parseInt(s);
        //System.out.println(s + ":" + i);
        
    }
    
    private static void testDouble()
    {
    	double d = 2.1;
    	System.out.println(d);
    	
    	String s = String.valueOf(d);
    	System.out.println(s);
    	
    	if (d == (long)d)
    		s = String.valueOf((long)d);
    	else
    		s = String.valueOf(d);
    	
    	System.out.println(s);
    	
    }
    
    private static void testSetKey()
    {
    	HashSet<Condition> cs = new HashSet<Condition>();
    	IAttribute newAttr = AttributeFactory.createAttribute("Text");
    	Value newValue = new Value(newAttr.getValueType(), "valueText");
        Operator newOper = Operator.stringToOperator("==");
        Condition newCondition = new Condition(newAttr, newOper, newValue);
        cs.add(newCondition);
        
        IAttribute tAttr = AttributeFactory.createAttribute("Text");
    	Value tValue = new Value(newAttr.getValueType(), "valueText");
        Operator tOper = Operator.stringToOperator("==");
        Condition tCondition = new Condition(tAttr, tOper, tValue);
        
        if (cs.contains(tCondition)) System.out.println("contains");
        else System.out.println("not contains");
    }
    
    private static void testString()
    {
    	if (true)
    	{
    		String s = "a,,,b,,c,,,,";
    		String[] a = s.split(RDRConstants.Delimeter, -1);
    		System.out.println(s);
    		System.out.println(a);
    	}
    	
    	if (false)
    	{
	    	String name = "what do you know about me";
	    	System.out.println(name.contains("do you know"));
	    	System.out.println(name.contains("about"));
	    	System.out.println(name.contains("hello"));
    	}
    	
    	if (false)
    	{
    		ArrayList<String[]> x = new ArrayList<String[]>();
    		
    		String s = "a,b,c,d,e,f,g,h,i";
    		String[] a = StringUtil.toArray(s, RDRConstants.Delimeter);
    		String ss = StringUtil.toString(a, RDRConstants.Delimeter);
    		System.out.println("s : " + s);
    		for (int i = 0; i < a.length; i++)
    			System.out.println(a[i]);
    		System.out.println("ss : " + ss);
    	}
    }
    
    private static void arffTest()
    {
   	
    	JSONObject jsonObj = new JSONObject();
    	System.out.println("empty jsonObj :" + jsonObj.toString());
    	
    	/*
    	//String arffFn = "d:\\11_EKP\\06. Test\\02. BIT_EMR테스트\\test_1129\\aidata3000.arff";
    	//String arffFn = "c:\\Dev\\eclipse_ws\\rdr-biopsy-engine\\domain\\cases\\ekp_biopsy_stomach.arff";
    	String arffFn = "d:\\11_EKP\\06. Test\\03. BIT\\12060223.arff";
    	
    	ArffUtil arffUtil = new ArffUtil(arffFn);
    	*/
    }
    
    private static void testHashMap()
    {
//    	String elements[] = { "A", "B", "C", "D", "E" };
//        Set set = new HashSet(Arrays.asList(elements));
//
//        //elements = new String[] { "A", "B", "C", "D" };
//        elements = new String[] { "A", "B", "C", "D", "E" };
//        Set set2 = new HashSet(Arrays.asList(elements));
//
//        System.out.println(set.equals(set2));
    	
//    	HashMap<String, String> map = new HashMap<String, String>();
//    	map.put("a", "1");
//    	map.put("b", "2");
//    	map.put("c", "3");
//    	
//    	Set<String> tSet = map.keySet();
//    	ArrayList<String> list = new ArrayList<String>();
//    	list.addAll(tSet);
//    	
//    	System.out.println(map);
//    	System.out.println(tSet);
//    	System.out.println(list);
    	
    	HashMap<String, String> map = new HashMap<String, String>();
    	map.put("a", "1");
    	map.put("b", "2");
    	map.put("c", "3");
    	LinkedHashMap<String, String> lmap = new LinkedHashMap<String, String>();
    	lmap.put("d", "4");
    	lmap.put("e", "5");
    	lmap.put("f", "6");
    	
    	HashMap<String, String> rmap = new HashMap<String, String>();
    	rmap.putAll(map);
    	rmap.putAll(lmap);
    	
    	System.out.println(map);
    	System.out.println(lmap);
    	System.out.println(rmap);

    }
    
    private static void testArffEncoding()
    {
    	try
    	{
	    	String arffFile = "d:\\11_EKP\\01. 혈액종합\\BugFix_20180418\\temp.arff";
	        ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffFile);
	        
	        if(source.getDataSet()!=null)
	        {
	            Instances data = source.getDataSet();
	
	            int arffAttrAmount = source.getStructure().numAttributes();
	            for(int i=0; i<arffAttrAmount; i++) 
	            {
	                weka.core.Attribute arffAttr = source.getStructure().attribute(i);
	                System.out.println(arffAttr.name());
	            }
	        }
    	}
    	catch (Exception ex)
    	{
    		System.out.println(ex.getMessage());
    	}
    }

        
  //정답사전과 inference결과가 다른 것 Report, inference performance 체크
    private static void runInferenceForTest()
    {
    	//Define doamin
    	String domainName = "aidata_sc";
                    
        Main.domain = new Domain (domainName, "", "");
        Main.allCaseSet = new CaseSet();
        Main.testingCaseSet = new CaseSet();
        Main.KB = new RuleSet();
        Rule rootRule = RuleBuilder.buildRootRule();
        Main.KB.setRootRule(rootRule);

        try
        {
	        DomainLoader.openDomainFile(domainName, true);
	        Main.workbench.setRuleSet(Main.KB);
	        
	        Set cases = Main.allCaseSet.getBase().entrySet();
	        Iterator caseIterator = cases.iterator();
	        
	        String fn = System.getProperty("user.dir") + "/domain/cases/test.out";
	        System.out.println("file : " + fn);
	        FileWriter writer = new FileWriter(new File(fn));
	        
	        int matchCnt = 0;
	        int unmatchCnt = 0;
	        long totElapsedTime = 0;
	        int totRuleCnt = 0;

	        while (caseIterator.hasNext())
	        {
	        	Map.Entry me = (Map.Entry)caseIterator.next();
		    	Case aCase = (Case) me.getValue();
		    	
		    	Case currentCase = CaseLoader.caseLoad(aCase.getCaseId(), null);
	
		    	//long startTime = System.currentTimeMillis();
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
		        
		        //long elapsedTime = System.currentTimeMillis() - startTime;
		        long elapsedTime = System.nanoTime() - startTime;
		        totElapsedTime += elapsedTime;
		        
		        //String caseResult = currentCase.getValue("GNL_NM_CD").toString();
		        String caseResult = currentCase.getValue("class").toString();
		        //String inferenceResult = rule.getConclusion().toString();
		        
		        //write conclusion
		        if ( false ) 
		        {
			        writer.append("case[" + currentCase.getCaseId() + "] : " 
			                      //+ currentCase.toString() + ", " 
			        		      + inferenceStr); 
			        writer.append("\n");
		        }
		        
		        //check inference result
		        if ( true )
		        {
		        	
		        	if ( caseResult.equals(inferenceStr) ||
		        	//if ( inferenceStr.contains(caseResult) ||
		        		 (caseResult.equals("NULL") && inferenceStr.equals("")) )
		        	{
		        		matchCnt++;
		        	}
		        	else
		        	{
		        		unmatchCnt++;
		        	}
		        	
		        	{
		        		writer.append("case[" + currentCase.getCaseId() + "] " 
				                      //+ currentCase.toString() + ", " 
				                      //+ "/" + currentCase.getValue("GNL_NM_CD").toString()
		        				      + "/" + currentCase.getValue("class").toString()
				                      + "/" + inferenceStr
		        		              + "/" );
				        writer.append("\n");
		        	}
		        }
		        
		        //check inference performance
		        if ( true )
		        {
		        	int ruleCnt = Main.workbench.getFiredRules().getSize();
		        	totRuleCnt += ruleCnt;
		        }
	        }
	        
	        writer.append("Case all[" + (matchCnt+unmatchCnt) + "]\n");
        	writer.append("match[" + matchCnt + "]\n");
        	writer.append("unmatch[" + unmatchCnt + "]\n");

        	writer.append("totTimeMilli : " + totElapsedTime + "\n" );
        	writer.append("totRuleCnt : " + totRuleCnt + "\n" );
        	writer.append("timeForSingleRule : " + ((double)totElapsedTime / (double)totRuleCnt) + "\n" );
        	
	        writer.close();
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
//    //정답사전과 inference결과가 다른 것 Report, inference performance 체크
//    private static void runInferenceForTest()
//    {
//    	//Define doamin
//        //String domainName = "ekp_biopsy_stomach";
//    	String domainName = "aidata3000";
//        //String methodType = Domain.SCRDR;
//    	String methodType = Domain.MCRDR;
//        String dbName = "";
//        String description = "";
//                    
//        Main.domain = new Domain (domainName, methodType, dbName, description);
//        Main.allCaseSet = new CaseSet();
//        Main.testingCaseSet = new CaseSet();
//        Main.KB = new RuleSet();
//        Rule rootRule = RuleBuilder.buildRootRule();
//        Main.KB.setRootRule(rootRule);
//
//        try
//        {
//	        DomainLoader.openDomainFile(domainName, true);
//	        Main.workbench.setRuleSet(Main.KB);
//	        
//	        Set cases = Main.allCaseSet.getBase().entrySet();
//	        Iterator caseIterator = cases.iterator();
//	        
//	        String fn = System.getProperty("user.dir") + "/domain/cases/test.out";
//	        System.out.println("file : " + fn);
//	        FileWriter writer = new FileWriter(new File(fn));
//	        
//	        int matchCnt = 0;
//	        int unmatchCnt = 0;
//	        long totElapsedTime = 0;
//	        int totRuleCnt = 0;
//
//	        while (caseIterator.hasNext())
//	        {
//	        	Map.Entry me = (Map.Entry)caseIterator.next();
//		    	Case aCase = (Case) me.getValue();
//		    	
//		    	Case currentCase = CaseLoader.caseLoad(aCase.getCaseId());
//	
//		    	//long startTime = System.currentTimeMillis();
//		    	long startTime = System.nanoTime();
//		    	
//		        Main.workbench.setCaseForInference(currentCase);
//		        Main.workbench.inference();
//		        
//		        String inferenceStr = "";
//		        if ( Main.domain.getReasonerType().equals(Domain.SCRDR))
//		        {
//		        	Rule rule = (Rule)(Main.workbench.getInferenceResult());
//		        	inferenceStr = rule.getConclusion().toString();
//		        }
//		        else
//		        {
//		        	RuleSet ruleSet = (RuleSet)(Main.workbench.getInferenceResult());
//		        	ConclusionSet aConclusionSet = ruleSet.getConclusionSet();
//		        	LinkedHashMap<String, Conclusion> aConclusionMap = aConclusionSet.getBase();
//		        	Iterator<String> keys = aConclusionMap.keySet().iterator();
//		        	while (keys.hasNext())
//		        	{
//		        		String key = keys.next();
//		        		inferenceStr += aConclusionMap.get(key).toString();
//		        		inferenceStr += ",";
//		        	}
//		        }
//		        
//		        //long elapsedTime = System.currentTimeMillis() - startTime;
//		        long elapsedTime = System.nanoTime() - startTime;
//		        totElapsedTime += elapsedTime;
//		        
//		        String caseResult = currentCase.getValue("GNL_NM_CD").toString();
//		        //String inferenceResult = rule.getConclusion().toString();
//		        
//		        //write conclusion
//		        if ( false ) 
//		        {
//			        writer.append("case[" + currentCase.getCaseId() + "] : " 
//			                      //+ currentCase.toString() + ", " 
//			        		      + inferenceStr); 
//			        writer.append("\n");
//		        }
//		        
//		        //check inference result
//		        if ( true )
//		        {
//		        	
//		        	//if ( caseResult.equals(inferenceResult) ||
//		        	if ( inferenceStr.contains(caseResult) ||
//		        		 (caseResult.equals("NULL") && inferenceStr.equals("")) )
//		        	{
//		        		matchCnt++;
//		        	}
//		        	else
//		        	{
//		        		unmatchCnt++;
//		        	}
//		        	
//		        	{
//		        		writer.append("case[" + currentCase.getCaseId() + "] " 
//				                      //+ currentCase.toString() + ", " 
//				                      + "/" + currentCase.getValue("GNL_NM_CD").toString()
//				                      + "/" + inferenceStr
//		        		              + "/" );
//				        writer.append("\n");
//		        	}
//		        }
//		        
//		        //check inference performance
//		        if ( true )
//		        {
//		        	int ruleCnt = Main.workbench.getFiredRules().getSize();
//		        	totRuleCnt += ruleCnt;
//		        }
//	        }
//	        
//	        writer.append("Case all[" + (matchCnt+unmatchCnt) + "]\n");
//        	writer.append("match[" + matchCnt + "]\n");
//        	writer.append("unmatch[" + unmatchCnt + "]\n");
//
//        	writer.append("totTimeMilli : " + totElapsedTime + "\n" );
//        	writer.append("totRuleCnt : " + totRuleCnt + "\n" );
//        	writer.append("timeForSingleRule : " + ((double)totElapsedTime / (double)totRuleCnt) + "\n" );
//        	
//	        writer.close();
//        }
//        catch (Exception ex) {
//        	ex.printStackTrace();
//        }
//    }
    
    private static void runInferenceAll()
    {
    	//Define doamin
        String domainName = "ekp_biopsy_stomach";
                    
        Main.domain = new Domain (domainName, "", "");
        Main.allCaseSet = new CaseSet();
        Main.testingCaseSet = new CaseSet();
        Main.KB = new RuleSet();
        Rule rootRule = RuleBuilder.buildRootRule();
        Main.KB.setRootRule(rootRule);

        try
        {
	        DomainLoader.openDomainFile(domainName, true);
	        Main.workbench.setRuleSet(Main.KB);
	        
	        Set cases = Main.allCaseSet.getBase().entrySet();
	        Iterator caseIterator = cases.iterator();
	        
	        String fn = System.getProperty("user.dir") + "/domain/cases/inference_all.out";
	        System.out.println("file : " + fn);
	        FileWriter writer = new FileWriter(new File(fn));
	        
	        int matchCnt = 0;
	        int unmatchCnt = 0;
	        long totElapsedTime = 0;
	        int totRuleCnt = 0;

	        while (caseIterator.hasNext())
	        {
	        	Map.Entry me = (Map.Entry)caseIterator.next();
		    	Case aCase = (Case) me.getValue();
		    	
		    	Case currentCase = CaseLoader.caseLoad(aCase.getCaseId(), null);
	
		        Main.workbench.setCaseForInference(currentCase);
		        Main.workbench.inference();
		        Rule rule = (Rule)(Main.workbench.getInferenceResult());
		        
		        String caseResult = currentCase.getValue("class").toString();
		        String inferenceResult = rule.getConclusion().toString();
		        
		        /*
		        writer.append("case[" + currentCase.getCaseId() + "] : [" 
			                  + currentCase.getFullFeatureString() + "] infernece rule["
			      		      + rule.getRuleId() + ", " + rule.getConclusion().toString() + "]"); 
			    writer.append("\n");
			    */
	        }
	        writer.close();
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    private static void writeKB()
    {
    	//Define doamin
        String domainName = "ekp_biopsy_stomach";
                    
        Main.domain = new Domain (domainName, "", "");
        Main.allCaseSet = new CaseSet();
        Main.testingCaseSet = new CaseSet();
        Main.KB = new RuleSet();
        Rule rootRule = RuleBuilder.buildRootRule();
        Main.KB.setRootRule(rootRule);

        try
        {
	        DomainLoader.openDomainFile(domainName, true);
	        Main.workbench.setRuleSet(Main.KB);
	        
	        String fn = System.getProperty("user.dir") + "/domain/cases/kb.out";
	        System.out.println("file : " + fn);
	        FileWriter writer = new FileWriter(new File(fn));
	        
	        RuleSet ruleSet = Main.KB;
	        
	        LinkedHashMap<Integer, Rule> ruleMap = ruleSet.getBase();
	        Iterator<Integer> ruleIt = ruleMap.keySet().iterator();
	        while (ruleIt.hasNext())
	        {
	        	Integer ruleId = ruleIt.next();
	        	Rule rule = ruleMap.get(ruleId);
	        	
	        	String ccid = "null";
	        	if ( rule.getCornerstoneCase() != null) 
	        		ccid = String.valueOf(rule.getCornerstoneCase().getCaseId());
	        	
	        	String parentRuleId = "";
	        	if ( rule.getParent() != null) 
	        		parentRuleId = String.valueOf(rule.getParent().getRuleId());
	        	
	        	writer.append("Rule[" + ruleId + 
	        			      "] parent[" + parentRuleId + 
	        			      "] cc[" + ccid +
	        			      "] : " + rule.toString() + "\n");
	        }
	        
	        writer.close();
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            
        }

    }

    private static void intialiseSystem(String domainName, String methodType, String domainDesc, String defaultConclusion){
        String log4jConfPath = "./log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
        
        CaseStructure caseStructure = new CaseStructure();
        IAttribute attr = AttributeFactory.createAttribute("Text");
        attr.setAttributeId(0);
        attr.setName("Recent");
        caseStructure.addAttribute(attr);
        
        IAttribute attr2 = AttributeFactory.createAttribute("Text");
        attr2.setAttributeId(1);
        attr2.setName("History");
        caseStructure.addAttribute(attr2);
        
        Main.domain = new Domain (domainName, methodType, domainDesc);
        
        Main.domain.setCaseStructure(caseStructure);
        
        Main.allCaseSet = new CaseSet();
        Main.KB = new RuleSet();
        RuleBuilder.setDefaultConclusion(defaultConclusion);
        Rule rootRule = RuleBuilder.buildRootRule();
        Main.KB.setRootRule(rootRule);
        
        // set domainName and methodType
        Main.domain.setDomainName(domainName);
        Main.domain.setReasonerType(methodType);

        Main.workbench = new Workbench(methodType);
        Main.workbench.setRuleSet(Main.KB);
       
    }
    
}
