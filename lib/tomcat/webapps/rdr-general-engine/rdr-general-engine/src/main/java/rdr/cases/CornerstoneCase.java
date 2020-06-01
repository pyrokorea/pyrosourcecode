/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.cases;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import rdr.model.Value;
import rdr.rules.Rule;
import rdr.rules.RuleSet;

/**
 * This class is used to define a cornerstone case used in RDR
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class CornerstoneCase extends Case{
    /**
     * Wrong Rule Set
     * -> rule 저장시 tb_rule_cornerstone_inference_result 저장시 사용됨
     *    RuleLoader.insertRuleCornerstones 참고
     */
    private RuleSet wrongRuleSet = new RuleSet();
    
    /**
     * Constructs a cornerstone case with case
     *
     * @param aCase
     */
    public CornerstoneCase(Case aCase) {
        super(aCase);
    }
    
      /**
     * Constructs a cornerstone case with case structure
     *
     * @param caseStructure
     */
    public CornerstoneCase(CaseStructure caseStructure) {
       super(caseStructure);
    }
    
    /**
     * Set wrong rule set
     * @param wrongRuleSet 
     */
    public void setWrongRuleSet(RuleSet wrongRuleSet){
        this.wrongRuleSet = wrongRuleSet;
    }
   
    /**
     * Get wrong rule set
     * @return 
     */
    public RuleSet getWrongRuleSet(){
        return this.wrongRuleSet;
    }
    
    /**
     * Add rule into wrong rule set
     * @param aRule 
     * @return  
     */
    public boolean addRuleToWrongRuleSet(Rule aRule){
        return this.wrongRuleSet.addRule(aRule);        
    }
    
    /**
     * Replace rule with existing rule in the wrong rule set
     * @param oldRule 
     * @param newRule 
     * @return  
     */
    public boolean replaceRuleWithExistingWrongRule(Rule oldRule, Rule newRule){
        if(this.wrongRuleSet.isRuleExist(oldRule)){
            return this.wrongRuleSet.addRule(newRule);
        } else {
            return false;
        }
    }
    
    /**
     * Clear wrong rule set.
     */
    public void clearWrongRuleSet(){
        this.wrongRuleSet = new RuleSet();
    }
    
//    public boolean equalCornerstoneCase(CornerstoneCase aCornerstoneCase)
//    {        
//        Set set = this.getValues().entrySet();
//        Iterator i = set.iterator();
//        while (i.hasNext()) {
//            Map.Entry me = (Map.Entry) i.next();
//            String attributeName = (String) me.getKey();
//            Value value = (Value) me.getValue();
//            Value checkingValue = aCornerstoneCase.getValue(attributeName);            
//            if(!value.equals(checkingValue)){
//                return false;
//            }            
//        }
//        return true;
//    }
    
    public boolean equalCornerstoneCase(CornerstoneCase aCornerstoneCase)
    {
    	String[] attrNames = this.getCaseStructure().getAttributeNameArray();
        for (int i = 0; i < attrNames.length; i++)
        {
        	String attrName = attrNames[i];
        	
        	if (this.isNullValue(attrName))
        	{
        		if (aCornerstoneCase.isNullValue(attrName) == false) return false;
        	}
        	else
        	{
        		if (aCornerstoneCase.isNullValue(attrName)) return false;
        		
        		if (this.getValue(attrName).equals(aCornerstoneCase.getValue(attrName)) == false)
        			return false;
        	}
        }
        
        return true;
    }
}
