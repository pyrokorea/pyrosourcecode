/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseStructure;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.logger.Logger;
import rdr.model.IAttribute;
import rdr.utils.StopWatch;
import rdr.db.RDRDBManager;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class RuleLoader {
    
    public static void deleteDefaultRule(){
        RDRDBManager.getInstance().deleteQuery("tb_rule_structure", "rule_id", 0);
    }
    public static boolean insertRule(int newRuleId, Rule newRule, int conclusionId)
    {
    	boolean flag = true;
    	
        flag &= insertRuleStructure(newRule, conclusionId);
        //StopWatch.lap("insert rule structure end");
        flag &= insertRuleConditions(newRuleId, newRule);
        //StopWatch.lap("insert rule conditions end");
        if(newRuleId != 0)
        {
            flag &= insertRuleCornerstones(newRuleId, newRule);
            
        }
        
        if (flag)
        	Logger.info("Rule " + newRule.toString() + " added.");

        return true;
    }
    
    public static boolean insertRuleConclusions(int newConclusionId, Conclusion newConclusion)
    {
        return RDRDBManager.getInstance().insertRuleConclusion(newConclusionId,
        		                                               newConclusion.getConclusionValue().getValueType().getTypeCode(), 
        		                                               newConclusion.getConclusionName());
    }
    
    public static boolean insertRuleStructure(Rule newRule, int conclusionId)
    {
        int parentId = -1;
        if(newRule.getRuleId()==Rule.ROOT_RULE_ID){
            parentId = -1;
        } else {
            parentId = newRule.getParent().getRuleId();
        }
        
        return RDRDBManager.getInstance().insertRuleStructure(newRule.getRuleId(), parentId, conclusionId);
    }
    
    public static boolean insertRuleConditions(int newRuleId, Rule newRule)
    {
    	boolean flag = true;
        for (Condition aCondition : newRule.getConditionSet().getBase()) 
        {
            flag &= RDRDBManager.getInstance().insertRuleCondition(newRuleId, 
            		                                               aCondition.getAttribute().getAttributeId(), 
            		                                               aCondition.getOperator().getOperatorCode(), 
            		                                               aCondition.getValue().toString());
        }
        return flag;
    }
        
    public static boolean insertRuleCornerstones(int newRuleId, Rule newRule)
    {
    	CornerstoneCase aCornerstoneCase = newRule.getCornerstoneCase();
    	
    	if (aCornerstoneCase == null) return true;
    	
    	boolean flag = true;
        flag &= RDRDBManager.getInstance().insertRuleCornerstone(newRuleId, aCornerstoneCase.getCaseId());       
            
        aCornerstoneCase.addRuleToWrongRuleSet(newRule);
        
        //StopWatch.lap("insert rule cornerstone end");
        
        //insert cornerstone case value into db only if cornerstone case is new
        if(!Main.allCornerstoneCaseSet.isCaseExist(aCornerstoneCase))
        {
            flag &= RDRDBManager.getInstance().insertCornerstoneValue(aCornerstoneCase);
            //StopWatch.lap("insert cornerstone value end");
        }
            
        RuleSet inferenceResult = aCornerstoneCase.getWrongRuleSet();
            
        Set rules = inferenceResult.getBase().entrySet();
        // Get an iterator
        Iterator ruleIterator = rules.iterator();
        while (ruleIterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) ruleIterator.next();
            Rule aRule = (Rule) me2.getValue();                
                
            RDRDBManager.getInstance().insertRuleCornerstoneInferenceResult(aCornerstoneCase.getCaseId(), aRule.getRuleId());       
            //StopWatch.lap("insert cc inference result end");
        }
        newRule.setCornerstoneCase(aCornerstoneCase);
        Main.allCornerstoneCaseSet.addCornerstoneCase(newRule, aCornerstoneCase);
        
        //StopWatch.lap("add cornerstone case end");
        
        return flag;
    }
        
    
    public static void setRules(CaseStructure caseStructure){
        HashMap<Integer, ConditionSet> conditionHashMap = RDRDBManager.getInstance().getConditionHashMap();
        ConclusionSet conclusionSet = RDRDBManager.getInstance().getConclusionSet();
        Main.KB = RDRDBManager.getInstance().getRuleStructureSet(conditionHashMap,  conclusionSet);
        
        //getRuleStructureSet에서 addChildRule을 수행하므로 불필요함. (여기서 add되는 것이 없음을 확인함)
        //Main.KB.setRootRuleTree();

        Main.allCornerstoneCaseSet = RDRDBManager.getInstance().getCornerstoneCaseSet(caseStructure);
        RuleLoader.setCornerstoneCaseToRules(Main.allCornerstoneCaseSet);
        
    }
    
    public static void setCornerstoneCaseToRules(CornerstoneCaseSet cornerstoneCaseSet){
        //rule id , list of case ids
        HashMap<Integer, ArrayList<Integer>> cornerstoneCaseIdsHashMap = RDRDBManager.getInstance().getCornerstoneCaseIdsHashMap();

        Set rules = Main.KB.getBase().entrySet();
        // Get an iterator
        Iterator ruleIterator = rules.iterator();

        while (ruleIterator.hasNext()) {
            Map.Entry me = (Map.Entry) ruleIterator.next();
            int ruleId = (int) me.getKey();
            Rule aRule = (Rule) me.getValue();
            if(cornerstoneCaseIdsHashMap.containsKey(ruleId)){
                ArrayList<Integer> caseIdList = cornerstoneCaseIdsHashMap.get(ruleId);
                
                //rule 에 대한 cornerstone case id 가 존재하지 않음
                if ( caseIdList == null )
                {
                	continue; 
                }
                
                for(int i=0; i<caseIdList.size(); i++)
                {
                    int caseId = caseIdList.get(i);
                    
                    CornerstoneCase aCornerstoneCase = (CornerstoneCase) cornerstoneCaseSet.getCornerstoneCaseById(caseId);    
                    aCornerstoneCase.addRuleToWrongRuleSet(aRule);
                    
                    aRule.setCornerstoneCase(aCornerstoneCase);
                }
            }
        }
    }
}
