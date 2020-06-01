/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static javax.swing.JOptionPane.showMessageDialog;

import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rdr.apimsg.KARequest;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseSet;
import rdr.cases.CaseStructure;
import rdr.cases.CaseSynchroniser;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.db.RDRDBManager;
import rdr.domain.Domain;
import rdr.domain.DomainLoader;
import rdr.learner.Learner;
import rdr.logger.Logger;
import rdr.model.AttributeFactory;
import rdr.model.CategoricalAttribute;
import rdr.model.IAttribute;
import rdr.model.TextAttribute;
import rdr.model.Value;
import rdr.model.ValueType;
import rdr.mysql.MysqlConnection;
import rdr.rules.Conclusion;
import rdr.rules.ConclusionSet;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Operator;
import rdr.rules.Rule;
import rdr.rules.RuleBuilder;
import rdr.rules.RuleLoader;
import rdr.rules.RuleSet;
import rdr.utils.RDRConfig;
import rdr.utils.Utility;

/**
*
* @author Kim Woo-Cheol (ucciri@gmail.com)
*/
public class RDRInterface 
{
	/** private c'tor for singleton */
	private RDRInterface() { 

		//RdrConfig.init();
				
		initializeDomain(); 
	}
		
	private static class Singleton {
		private static final RDRInterface instance = new RDRInterface();
	}
	
	public static RDRInterface getInstance() {
		//System.out.println("create RDRInteface instance");
		return Singleton.instance;
	}
	
    private Case currentCase;
    private ConclusionSet tempConclusionSet = new ConclusionSet();
    private ConditionSet usedConditionSet = new ConditionSet();
    
    private Conclusion newConclusion = new Conclusion();
    private Condition newCondition = new Condition();
    private int kaMode =0;
    
    public Case getCurrentCase(){
        return this.currentCase;
    }
    
    public boolean connectDataBase(String dbType,
    		                		String sqliteFile,
    		                		String dbDriver,
    		                		String dbURL,
    		                		String dbName,
    		                		String user,
    		                		String pass)
    {
    	RDRConfig.setDBType(dbType);
    	RDRConfig.setDefaultSqliteFile(sqliteFile);
    	RDRConfig.setDBDriver(dbDriver);
    	RDRConfig.setDBUrl(dbURL);
    	RDRConfig.setDefaultDBName(dbName);
    	RDRConfig.setUser(user);
    	RDRConfig.setPassword(pass);
    	
    	if (RDRConfig.checkDBConfig() == false)
    	{
    		Logger.error("DB Configuration is invalid");
    		return false;
    	}
    	
    	if (RDRDBManager.connectDataBase(0) == false)
    	{
    		Logger.error("DB Connection failed");
    		return false;
    	}
    	
    	return initializeDomain();
    }
    
    public boolean isDomainChanged(String domainName, String pUserId)
    {
    	Main.userid = pUserId;
    			
    	if (Main.domainName.equals(domainName))
    		return false;
    	else
    		return true;
    }
    
    public boolean checkDomain(String domainName, String pUserId, boolean bForceInit)
    {
    	if (domainName == null || domainName.isEmpty())
    		return false;
    	
    	Main.userid = pUserId;
    	
    	if (Main.domainName.equals(domainName))
    	{
    		if (bForceInit)
    			return initializeDomain(domainName);
    	}
    	else
    	{
    		Logger.info("domain is changed : " + domainName);
    		return initializeDomain(domainName);
    	}
    	
    	return true;
    }

    public boolean initializeDomain()
    {
    	String domainName = DomainLoader.getDefaultDomainName();
    	
    	if ( domainName == null) 
    	{
    		Logger.error("default domain check failed");
    		return false;
    	}
    	else if ( domainName.isEmpty())
    	{
    		Logger.info("domain table is empty");
    		return true;
    	}
    	
    	return initializeDomain(domainName);
    }
        
    public boolean initializeDomain(String domainName) 
    {
        Logger.info("Engine initailize, domain : " + domainName);
                                
        try 
        {
        	Main.domain = new Domain (domainName, "", "");
	        Main.allCaseSet = new CaseSet();
	        Main.testingCaseSet = new CaseSet();
	        Main.domainName = domainName;
	        
	        Main.KB = new RuleSet();
	        Rule rootRule = RuleBuilder.buildRootRule();
	        Main.KB.setRootRule(rootRule);
	        
	        if (DomainLoader.openDomainFile(domainName, false) == false )
	        	return false;
	        
	        Main.workbench.setRuleSet(Main.KB);
	        tempConclusionSet = Main.KB.getConclusionSet();
        } 
        catch (Exception ex) 
        {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        	return false;
		}
        
        return true;
    }
    
    public boolean addDomain(String domainName, 
    						 String domainDesc, 
					         String domainReasoner,
					         String userid)
    {
		Main.domain = new Domain (domainName, domainDesc, domainReasoner);
		Main.allCaseSet = new CaseSet();
		Main.testingCaseSet = new CaseSet();
		Main.domainName = domainName;
		Main.userid = userid;
		
		Main.KB = new RuleSet();
		Rule rootRule = RuleBuilder.buildRootRule();
		Main.KB.setRootRule(rootRule);

		RDRDBManager.getInstance().setAutoCommit(false);
		boolean flag = RDRDBManager.getInstance().insertDomainDetails(domainName, 
																	  domainDesc, 
                                                                      domainReasoner);

		flag &= RuleLoader.insertRule(Rule.ROOT_RULE_ID, Main.KB.getRootRule(), Conclusion.DEFAULT_CONCLUSION_ID);
		flag &= RuleLoader.insertRuleConclusions(0, Main.KB.getRootRule().getConclusion());
		
		RDRDBManager.getInstance().doCommit(flag);
		RDRDBManager.getInstance().setAutoCommit(true);
		
		if (flag) Logger.info("add domain finished successfully");
		else Logger.error("add domain failed");
		
		if (flag) flag &= initializeDomain(domainName);
		
		if (flag) Logger.info("initialize domain finished successfully");
		else Logger.error("initialize domain failed");
		
		return flag;
	}
    
    public boolean deleteAllDomainData(String domainName)
    {
    	return RDRDBManager.getInstance().deleteAllDomainData(domainName);
    }
    
    public void reloadDomain()
    {
    	String domainName = Main.domain.getDomainName();
    	Logger.info("reloadDomain for [" + domainName + "]");
    	
    	try 
    	{
    		/** Main.domain set (caseStructure set)
    		 *  Main.workbench 초기화
    		 *  Main.KB set
    		 *  Main.allCornerstoneCaseSet set
    		 */
    		DomainLoader.reloadDomainFile(domainName);
    		
    		Main.workbench.setRuleSet(Main.KB);
    		tempConclusionSet = Main.KB.getConclusionSet();
    	} 
    	catch (Exception ex) 
    	{
    		Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
    	}
    }
    
    /**
     * KA Engine 초기화
     * @param aCase
     * @param kaMode
     * @param wrongConclusion
     */
    public void learnerInit(Case aCase, int kaMode, Conclusion wrongConclusion)
    {   
        this.kaMode = kaMode;
        currentCase = aCase;
        
        currentCase.setCaseId(RDRInterface.getInstance().getCornerstoneCaseId(currentCase));
        
        //clear wrong conclusion in workbench
        Main.workbench.deleteWrongConclusion();
        
        //create empty rule for new rule
        Rule newRule = new Rule();
        //set cornerstone case for new rule
        CornerstoneCase aCornerstoneCase = new CornerstoneCase(currentCase);                
        newRule.setCornerstoneCase(aCornerstoneCase);
        
        Main.workbench.setCurrentCornerstoneCase(aCornerstoneCase);
        //put the new rule into the workbench
        Main.workbench.setNewRule(newRule);
        //set ka mode for knowledge acquisition
        Main.workbench.setKaMode(kaMode);
        
        // set the possible conclusion into conclusion set
        if (kaMode==Learner.KA_NEW_MODE) 
        {
            // if KA mode is new, set the wrong conclusion as null
            tempConclusionSet.setConclusionSet(Main.KB.getConclusionSet());
            
            // disable 
            
        } 
        else if (kaMode==Learner.KA_EXCEPTION_MODE) 
        {
            // if KA mode is edit (refining), set the wrong conclusion.
            Main.workbench.setWrongConclusion(wrongConclusion);
            
            tempConclusionSet.setConclusionSet(Main.KB.getConclusionSet());
            
        }
        else if (kaMode==Learner.KA_ALTER_MODE) 
        {
        	/** (Root) -> (R1) -> (R2) 이고 R2가 inference 결과일때
        	 *  wrongConclusion은 (R2)의 결론임.
        	 *  newRule인 (R3)는 R2의 sibling 즉 R1의 child 로 생성된다.
        	 */
        	
            // if KA mode is alter (add alternative), set the wrong conclusion.
            Main.workbench.setWrongConclusion(wrongConclusion);
            
            tempConclusionSet.setConclusionSet(Main.KB.getConclusionSet());
            
        }
        else if (kaMode==Learner.KA_STOPPING_MODE) 
        {
            // if KA mode is stopping (deleting), set the wrong conclusion
            Main.workbench.setWrongConclusion(wrongConclusion);   

            //stopping rule은 결론id가 -1인 stopping rule 로 관리함
            if (false)
            {
            	newConclusion = new Conclusion();
            	newConclusion.setConclusionId(Conclusion.NULL_CONCLUSION_ID);
	            
	            Main.workbench.setNewRuleConclusion(newConclusion);
	            Main.workbench.setCaseForInference(currentCase);
	            Main.workbench.inference();
	            Main.workbench.getInferenceResult();
            }
            else
            //stopping rule은 root rule의 결론을 갖음
            {
	            tempConclusionSet = new ConclusionSet();
	            tempConclusionSet.addConclusion(Main.KB.getRootRule().getConclusion());
	            
	            newConclusion = Main.KB.getRootRule().getConclusion();
	            
	            Main.workbench.setNewRuleConclusion(newConclusion);
	            Main.workbench.setCaseForInference(currentCase);
	            Main.workbench.inference();
	            Main.workbench.getInferenceResult();
            }
        }
    }
    
    /**
     * 결론 선택
     * @param conclusionName
     * @return
     */
    public String[] selectConclusion(String conclusionName) 
    {
        String[] returnArray = new String[2];
            
        if (this.kaMode == Learner.KA_STOPPING_MODE)
    	{
        	returnArray[0] = "error";
            returnArray[1] = "delete모드에서 결론선택이 불가합니다.";
            return returnArray;
    	}
        
        returnArray[0] = "valid";
        returnArray[1] = conclusionName;            
            
        newConclusion = tempConclusionSet.getConclusionByName(conclusionName);
        Main.workbench.setNewRuleConclusion(newConclusion);
        
        return returnArray;
    }
    
    /**
     * Rule Path상의 조건 Set
     * @param aCase
     * @param aConclusion
     * @return
     */
    public ConditionSet getUsedConditionSet(Case aCase, Conclusion aConclusion) {
        Main.workbench.setCaseForInference(aCase);
        Main.workbench.inference();

        /** alter mode 인 경우 inference result의 parent의 child로 new rule이 생성되므로
         *  usedConditionSet은 inference result의 parent까지만으로 해야 한다.
         */
        if (Main.domain.isMCRDR())
        {
            RuleSet inferenceResult 
            	= ((RuleSet) Main.workbench.getInferenceResult()).getRuleSetbyConclusion(aConclusion);
            usedConditionSet = inferenceResult.getRulePathConditionSet(this.kaMode == Learner.KA_ALTER_MODE);

        } 
        else if(Main.domain.isSCRDR())
        {
            Rule inferenceResult = (Rule) Main.workbench.getInferenceResult();
            usedConditionSet = inferenceResult.getPathRuleConditionSet(this.kaMode == Learner.KA_ALTER_MODE);
        }
        return usedConditionSet;
    }

    /** 
     * 결론 추가                 
     * @param conclusionName
     * @return
     */
    public String[] addConclusion(String conclusionName) 
    {
        String[] returnArray = new String[2];
        
        if (this.kaMode == Learner.KA_STOPPING_MODE)
    	{
        	returnArray[0] = "error";
            returnArray[1] = "delete모드에서 결론추가가 불가합니다.";
            return returnArray;
    	}
        
        //int maxLen = 512;
        int maxLen = Integer.MAX_VALUE;
        
        if (conclusionName.length() > maxLen)
        {
            returnArray[0] = "error";
            returnArray[1] = "결론이 너무 깁니다 (제한: 512자).";
        } 
        else if (conclusionName.length() == 0)
        {
            returnArray[0] = "error";
            returnArray[1] = "결론을 입력해 주세요.";
        } 
        else 
        {
            Value value = new Value(new ValueType("TEXT"), conclusionName);
            Conclusion newConclusion = new Conclusion(value);            

            if (tempConclusionSet.isExist(newConclusion))
            {
                returnArray[0] = "error";
                returnArray[1] = "이 결론은 이미 추가되어있습니다.";
            } 
            else 
            {
                tempConclusionSet.addConclusion(newConclusion);
                Main.workbench.setNewRuleConclusion(newConclusion);
           
                returnArray[0] = "valid";
                returnArray[1] = conclusionName;            
            }
        }
        
        return returnArray;
    }
    
    /** 
     * 결론 수정 (in DB)                 
     * @param conclusionName
     * @return
     */
    public boolean editConclusionName(int conclusionId, String conclusionName, String[] msg) 
    {
        if (conclusionName.length() > 512)
        {
            msg[0] = "결론이 너무 깁니다 (제한: 512자).";
            return false;
        } 
        else if (conclusionName.length() == 0)
        {
        	msg[0] = "결론을 입력해 주세요.";
            return false;
        } 
        else 
        {
        	if ( RDRDBManager.getInstance().updateRuleConclusion(conclusionId, conclusionName) )
        	{
        		msg[0] = "";   
                return true;
        	}
        	else
        	{
        		msg[0] = "DB Update Failed";
        		return false;
        	}
        }

    }
    
    /**
     * 새로운 조건 생성
     * @param newConAttrStr
     * @param newConOperStr
     * @param newConValStr
     * @return
     */
    private boolean constructNewCondition(String newConAttrStr, 
    		                              String newConOperStr, 
    		                              String newConValStr) {    
        boolean isValid = false;
        
        newCondition = RuleBuilder.buildRuleCondition(currentCase.getCaseStructure(), newConAttrStr, newConOperStr, newConValStr);
        // check whether the new condition is valid for this case
        isValid = newCondition.isSatisfied(currentCase);
        
        return isValid;
    }
    
    /**
     * 조건 추가
     * @param newConAttrStr
     * @param newConOperStr
     * @param newConValStr
     * @return
     */
    public String[] addCondition(String newConAttrStr, 
    		                     String newConOperStr, 
    		                     String newConValStr) {
        String[] returnArray = new String[2];
        
        //check whether the new condition value field is empty
        if(!newConValStr.equals("") && newConValStr!=null){            
            // construct new condition and check whether the condition is valid for this case            
            if(constructNewCondition(newConAttrStr, newConOperStr, newConValStr)) {
                // add condition and check whether there is duplicating one
                if(Main.workbench.addConditionToNewRule(newCondition)){
                    // if the condition is valid to add, then update condition table
                    returnArray[0] = "valid";
                    returnArray[1] = "";
                    Logger.info("new conditoin : " + newCondition.toString());
                    return returnArray;
                } else {                    
                    returnArray[0] = "error";
                    returnArray[1] = "이미 사용되고있는 조건입니다.";
                }
            } else {
                    returnArray[0] = "error";
                    returnArray[1] = "조건이 현재 사례에 만족하지 않습니다.";
            }
            
        } else {
            if(newConOperStr.equals("MISSING") || newConOperStr.equals("NOT MISSING")){
                if(constructNewCondition(newConAttrStr, newConOperStr, newConValStr)) {
                    // add condition and check whether there is duplicating one
                    if(Main.workbench.addConditionToNewRule(newCondition)){
                        // if the condition is valid to add, return object
                        returnArray[0] = "valid";
                        returnArray[1] = "";
                        Logger.info("new conditoin : " + newCondition.toString());
                        return returnArray;
                    } else {                    
                        returnArray[0] = "error";
                        returnArray[1] = "이미 사용되고있는 조건입니다.";
                    }
                } else {
                        returnArray[0] = "error";
                        returnArray[1] = "조건이 현재 사례에 만족하지 않습니다.";
                }
            } else {
                returnArray[0] = "error";
                returnArray[1] = "value값을 지정해주세요.";
            }
        }
        return returnArray;
    }
    
    /**
     * 조건 삭제
     * @param conditionAttrName
     * @param conditionOper
     * @param conditionVal
     * @return
     */
    public String[] deleteCondition(String conditionAttrName, 
    		                        String conditionOper, 
    		                        String conditionVal) {
        String[] returnArray = new String[2];
            
        Condition deletingCondition = RuleBuilder.buildRuleCondition(currentCase.getCaseStructure(), conditionAttrName, conditionOper, conditionVal);
        System.out.println(deletingCondition);
        if(!Main.workbench.getLearner().deleteConditionFromNewRule(deletingCondition)){
            returnArray[0] = "error";
            returnArray[1] = "이 조건은 삭제할 수 없습니다.";
        } else {

            returnArray[0] = "valid";
            returnArray[1] = "";
        }
        return returnArray;
    }
    
    /**
     * 사례검증 수행
     * @return
     */
    public String[] validateRule() 
    {
        String[] returnArray = new String[2];
        
        // clear cornerstonce case set (but the current case must be remained)
        CornerstoneCase cornerstoneCase = new CornerstoneCase(currentCase);   
        
        // learner 의 newRule 의 cornerstoneCase set
        Main.workbench.setCurrentCornerstoneCase(cornerstoneCase);
        Main.workbench.getNewRule().setCornerstoneCase(cornerstoneCase);
        
        
        Main.workbench.getLearner().retrieveValidatingCaseSet(kaMode);
        CornerstoneCaseSet validatingCaseSet = Main.workbench.getLearner().getValidatingCaseSet();
        
        if(validatingCaseSet.getCaseAmount()==0){
            returnArray[0] = "confirm";
            returnArray[1] = "검증해야할 연관된 사례가 없습니다. 새로운 지식을 생성하시겠습니까?";
            
        } else {
            returnArray[0] = "required";
            returnArray[1] = "";
            
            // validate cornerstone case
            CornerstoneCase validatingCase = validatingCaseSet.getFirstCornerstoneCase();            
            Main.workbench.setValidatingCase(validatingCase);
            Main.workbench.inferenceForValidation();

        }
        return returnArray;
        
    }
    
    /**
     * validating case
     * @return
     */
    public CornerstoneCaseSet getValidationCaseSet() {
        CornerstoneCaseSet validatingCaseSet = Main.workbench.getLearner().getValidatingCaseSet();
        return validatingCaseSet;
    }
    
    /**
     * 연산자
     * @return
     * @throws JSONException
     */
    public HashMap<String, ArrayList<String>> getPotentialOperators()
    {
    	HashMap<String, ArrayList<String>> result
    		= new HashMap<String, ArrayList<String>>();
        
        String[] typeArray = new String[]{"Text", "Categorical", "Continuous"};
        
        for(int i=0; i<typeArray.length; i++) 
        {
            JSONArray aJSONArray  = new JSONArray();
            IAttribute attribute = AttributeFactory.createAttribute(typeArray[i]);
            String[] aPotentialOperators = attribute.getPotentialOperators();

            String typeStr = typeArray[i].toUpperCase();
            result.put(typeStr, new ArrayList<String>());
            
            for(int j=0; j<aPotentialOperators.length; j++)
            {
            	result.get(typeStr).add(aPotentialOperators[j]);
            }
        }
        
        return result;
    }
    
    public Case getCaseFromArff()
    {
    	Case aCase = null;
    	try {
    		aCase = CaseLoader.caseLoad(1, null);
    	} catch (Exception ex) {
    		Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
    	}
    	
    	return aCase;
    }
    
    public Case getCaseFromValueMap(CaseStructure aCaseStructure,
    		                        HashMap<String, String> valueMap,
    		                        String[] msg)
    {
    	Case aCase = null;
    	try {
    		aCase = CaseLoader.caseLoadFromValueMap(aCaseStructure, valueMap, msg);
    	} catch (Exception ex) {
    		Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
    		return null;
    	}
    	
    	return aCase;
    }
    
    public int compareCaseStructure(CaseStructure arffCaseStructure, 
    		                        CaseStructure dbCaseStructure,
    		                        String[] msg)
    {
    	CaseSynchroniser aCaseSynchroniser = new CaseSynchroniser();
        int ret = aCaseSynchroniser.compare(arffCaseStructure, dbCaseStructure);
        
        if (ret == 0)
        {
        	msg[0] = "same";
        }
        else if (ret > 0)
        {
        	msg[0] = "ARFF파일에 추가된 항목 수 : " + ret;
        }
        else if (ret < 0)
        {
        	msg[0] = "DB에는 존재하는데 ARFF에 누락된 항목이 존재합니다.";
        }
        
        return ret;
    }
    
    public ArrayList<String> syncCaseStructure(CaseStructure passedCaseStructure,
    		                                   CaseStructure dbCaseStructure)
    {
    	CaseSynchroniser aCaseSync = new CaseSynchroniser();
    	
    	CaseStructure newCaseStructure
			= aCaseSync.getNewStructureByComparingTwoStructure(passedCaseStructure,
			                                               	   dbCaseStructure);
	
    	String domainName = Main.domain.getDomainName();
    	
    	try {
    		DomainLoader.reloadDomainFile(domainName);
    	} catch (Exception ex) {
    		Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
    	}
    	
    	return aCaseSync.getAddedAttributes();
    }
    
    public Object getInferenceResult(Case aCase)
    {
    	//System.out.println("[RDRInterface]  getInferenceResult");
    	
    	Main.workbench.setCaseForInference(aCase);
        Main.workbench.inference();
        
        Object inferenceResult = Main.workbench.getInferenceResult();
        
//        System.out.println("Inference-------------------------------------");
//        
//        LinkedHashMap<Integer, Rule> firedRules = Main.workbench.getFiredRules().getBase();
//        Iterator<Integer> keyItr = firedRules.keySet().iterator();
//        while (keyItr.hasNext())
//        {
//        	Integer key = keyItr.next();
//        	Rule fRule = firedRules.get(key);
//        	
//        	int parentId = -1;
//        	Rule pRule = fRule.getParent();
//        	if ( pRule != null) parentId = pRule.getRuleId();
//        	
//        	System.out.println(" fired rule : parent[" + parentId + "] " + fRule.toString());
//        }
//        System.out.println("----------------------------------------------");
        
        return inferenceResult;
    }
    
    public boolean addCornerstoneCase(Case aCase, ArrayList<Integer> addedRules)
    {
    	Main.workbench.setCaseForInference(aCase);
        Main.workbench.inference();
        
        Object inferenceResult = Main.workbench.getInferenceResult();
        
        ArrayList<Rule> rules = new ArrayList<Rule>();
        if (Main.domain.isSCRDR())
        {
        	rules.add((Rule)inferenceResult);
        }
        else
        {
        	RuleSet aRuleSet = (RuleSet)inferenceResult;
        	LinkedHashMap<Integer, Rule> rMap = aRuleSet.getBase();
        	Iterator<Integer> keys = rMap.keySet().iterator();
        	while (keys.hasNext())
        	{
        		rules.add(rMap.get(keys.next()));
        	}
        }
        
        HashMap<Integer, ArrayList<Integer>> rule2Cornerstone
        	= RDRDBManager.getInstance().getCornerstoneCaseIdsHashMap();
        
        Integer caseId = new Integer(aCase.getCaseId());
        CornerstoneCase aCornerstoneCase = new CornerstoneCase(aCase);
        
        boolean flag = true;
        RDRDBManager.getInstance().setAutoCommit(false);
        
        Logger.info("added case id : " + caseId.intValue());
        
        for (int i = 0; i < rules.size(); i++)
        {
        	Integer ruleId = new Integer(rules.get(i).getRuleId());
        	
        	if (rule2Cornerstone.containsKey(ruleId))
        	{
        		ArrayList<Integer> ccList = rule2Cornerstone.get(ruleId);
        		if (ccList.contains(caseId) == false)
        		{
        			if (RDRDBManager.getInstance().insertRuleCornerstone(ruleId.intValue(), 
        					                                         caseId.intValue()))
        			{
        				Logger.info("inferenced rule id : " + ruleId.intValue() + " cornerstone case inserted");
        				addedRules.add(ruleId);
        			}
        			else
        			{
        				Logger.error("inferenced rule id : " + ruleId.intValue() + " cornerstone case insert failed");
        				flag = false;
        			}
        		}
        		else
        		{
        			Logger.info("inferenced rule id : " + ruleId.intValue() + " cornerstone case already exist, not inserted");;
        		}
        	}
        	else
        	{
        		Logger.error("inferenced rule id : " + ruleId.intValue() + " rule not found");;
        	}
        }
        
        if (Main.allCornerstoneCaseSet.isCaseExist(aCornerstoneCase) == false)
        {
            if (RDRDBManager.getInstance().insertCornerstoneValue(aCornerstoneCase))
            {
            	Logger.info("cornerstone case value inserted, tb_cornerstone_case");
            }
            else
            {
            	Logger.error("cornerstone case value insert failed, tb_cornerstone_case");
            	flag = false;
            }
        }
        
        RDRDBManager.getInstance().doCommit(flag);
        RDRDBManager.getInstance().setAutoCommit(true);
        
        Main.allCornerstoneCaseSet.addCornerstoneCase(null, aCornerstoneCase);
        
        return flag;
    }
    
    public boolean isValidInferneceResult(Case aCase, int aConclusionId)
    {
    	Object irObj = this.getInferenceResult(aCase);
    	if (Main.domain.isMCRDR())
    	{
    		RuleSet inferenceResult = (RuleSet)irObj;
    		return (inferenceResult.getConclusionSet().getConclusionById(aConclusionId) != null);
    	}
    	else
    	{
    		Rule inferenceResult = (Rule)irObj;
    		return (inferenceResult.getConclusion().getConclusionId() == aConclusionId);
    	}
    }
    
    public boolean isEmptyInferenceResult(Case aCase)
    {
    	Object irObj = this.getInferenceResult(aCase);
    	if (Main.domain.isMCRDR())
    	{
    		return Utility.isEmptyInferenceResult((RuleSet)irObj);
    	}
    	else
    	{
    		return Utility.isEmptyInferenceResult((Rule)irObj);
    	}
    }
    
    public boolean isValidInferneceResult(Object pInferenceResult, int aConclusionId)
    {
    	if (Main.domain.isMCRDR())
    	{
    		RuleSet inferenceResult = (RuleSet)pInferenceResult;
    		return (inferenceResult.getConclusionSet().getConclusionById(aConclusionId) != null);
    	}
    	else
    	{
    		Rule inferenceResult = (Rule)pInferenceResult;
    		return (inferenceResult.getConclusion().getConclusionId() == aConclusionId);
    	}
    }
    
    public boolean isEmptyInferenceResult(Object pInferenceResult)
    {
    	if (Main.domain.isMCRDR())
    	{
    		return Utility.isEmptyInferenceResult((RuleSet)pInferenceResult);
    	}
    	else
    	{
    		return Utility.isEmptyInferenceResult((Rule)pInferenceResult);
    	}
    }
    
    public RuleSet getFiredRules(Case aCase, boolean skipInference)
    {
    	System.out.println("[RDRWebInterface] getFiredRules");
    	
    	if (skipInference == false)
    	{
	    	Main.workbench.setCaseForInference(aCase);
	        Main.workbench.inference();
    	}
    	
        RuleSet ruleSet = Main.workbench.getFiredRules();
        
        //System.out.println("inference result rule :" + rule.getRuleId());
        
        return ruleSet;
    }
    
    public boolean createRDRTables()
    {
    	return RDRDBManager.getInstance().createRDRTables();
    }
    
    public boolean insertCaseStructure(CaseStructure aCaseStructure)
    {
    	return CaseLoader.insertCaseStructure(aCaseStructure);
    }
    
    public boolean insertAttribute(IAttribute attr, StringBuilder sb)
    {
    	CaseStructure aCaseStructure = CaseLoader.loadCaseStructureFromDB();
    	if (aCaseStructure == null) 
    	{
    		sb.append("case structure loading failed");
    		return false;
    	}
    	
    	if (aCaseStructure.isAttributeExist(attr))
    	{
    		sb.append("attribute is already exist, " + attr.getName());
    		return false;
    	}
    	
    	attr.setAttributeId(aCaseStructure.getNewAttributeId());
    	
    	ArrayList<IAttribute> attrList = new ArrayList<IAttribute>();
    	attrList.add(attr);
    	return CaseLoader.insertAttribute(attrList);
    }

    public boolean deleteAttribute(String domainName, String attrName)
    {
    	return CaseLoader.deleteAttribute(domainName, attrName);
    }
    
    public boolean editAttributeName(String domainName, String attrName, String newAttrName)
    {
    	return CaseLoader.modifyAttributeName(domainName, attrName, newAttrName);
    }
    
    public boolean editAttributeDesc(String domainName, String attrName, String attrDesc)
    {
    	return CaseLoader.modifyAttributeDesc(domainName, attrName, attrDesc);
    }
    
    public boolean addCategoricalValue(String attrName, String catValue, StringBuilder sb)
    {
    	ArrayList<String> catValues = new ArrayList<String>();
    	catValues.add(catValue);
    	
    	return CaseLoader.addCategoricalValue(attrName, catValue, sb);
    }
    
    public ArrayList<HashMap<String, String>> getAllDomainDetails()
    {
    	return RDRDBManager.getInstance().getDomainDetails(null);
    }
    
    public HashMap<Integer, ArrayList<Integer>> getRuleCornerstoneCaseId()
    {
    	return RDRDBManager.getInstance().getCornerstoneCaseIdsHashMap();
    }
    
    public CornerstoneCaseSet getCornerstoneCaseSet()
    {
    	return RDRDBManager.getInstance().getCornerstoneCaseSet(Main.domain.getCaseStructure());
    }
    
    public boolean deleteRule(String domainName, int ruleId, StringBuilder sb)
    {
    	return RDRDBManager.getInstance().deleteRule(domainName,  ruleId, sb);
    }
    
    public int getCornerstoneCaseId(Case aCase)
    {
    	//IncrementalLearner에서는 case 중복이 없다는 가정, 항상 새로운 case id로 (성능때문)
    	//return RDRDBManager.getInstance().getNewCornerstoneCaseId();
    	
    	CornerstoneCase cc = new CornerstoneCase(aCase);
    	CornerstoneCase aCornerstoneCase 
    		= Main.allCornerstoneCaseSet.getExistingCornerstonCase(cc);
    	
    	if (aCornerstoneCase == null)
    	{
    		return RDRDBManager.getInstance().getNewCornerstoneCaseId();
    	}
    	else
    	{
    		return aCornerstoneCase.getCaseId();
    	}
    }
   
}
    
    