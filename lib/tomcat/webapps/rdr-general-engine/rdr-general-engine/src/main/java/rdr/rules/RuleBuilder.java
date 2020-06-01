/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.rules;

import java.util.Vector;
import rdr.cases.CaseStructure;
import rdr.model.Attribute;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.model.ValueType;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class RuleBuilder {
    
    private static String defaultConclusion = "";
    private static boolean isDefaultConclusionSet = false;
    
    public static void setDefaultConclusion(String defaultConclusion){
        RuleBuilder.defaultConclusion = defaultConclusion;
        RuleBuilder.isDefaultConclusionSet= true;
    }
        
    /**
     * build root rule     
     * @return  
     */
    public static Rule buildRootRule(){
        Rule rootRule = new Rule();
                
        //root conclusion
        if(RuleBuilder.isDefaultConclusionSet){
            Value rootValue = new Value(ValueType.TEXT, RuleBuilder.defaultConclusion);
            Conclusion rootConclusion = new Conclusion(rootValue);
            rootConclusion.setConclusionId(0);
            
            ConditionSet rootConditionSet = new ConditionSet();    

            rootRule.setRuleId(0);
            rootRule.setConditionSet(rootConditionSet);
            rootRule.setConclusion(rootConclusion);
        } else {
            Value rootValue = new Value(ValueType.TEXT, "");
            Conclusion rootConclusion = new Conclusion(rootValue);
            rootConclusion.setConclusionId(0);
            
            ConditionSet rootConditionSet = new ConditionSet();    

            rootRule.setRuleId(0);
            rootRule.setConditionSet(rootConditionSet);
            rootRule.setConclusion(rootConclusion);
        }
        return rootRule;
    }
    
    /**
     * build rule condition     
     * @param caseStructure
     * @param condAttrName
     * @param condOperStr
     * @param condValStr
     * @return  
     */
    public static Condition buildRuleCondition(CaseStructure caseStructure, String condAttrName, String condOperStr, String condValStr){
        
        IAttribute newAttr = caseStructure.getAttributeByName(condAttrName);
        condOperStr = condOperStr.toUpperCase();
        
        if(condOperStr.equals("MISSING")){
            Operator newOper = new Operator (Operator.MISSING);
            Value newValue = new Value(ValueType.NULL_TYPE);

            Condition newCondition = new Condition(newAttr, newOper, newValue);

            return newCondition;
        } else if (condOperStr.equals("NOT MISSING") ){
            Operator newOper = new Operator (Operator.NOT_MISSING);
            Value newValue = new Value(ValueType.NULL_TYPE);

            Condition newCondition = new Condition(newAttr, newOper, newValue);

            return newCondition;
        } else {
            Value newValue = new Value(newAttr.getValueType(), condValStr);
            Operator newOper = Operator.stringToOperator(condOperStr);

            Condition newCondition = new Condition(newAttr, newOper, newValue);

            return newCondition;
        }
    }
    
    /**
     * copy rule - conditionset, conclusion, childRuleList, cornerstoneCase NOT ruleId, parentRule
     * @param originRule
     * @return  
     */
    public static Rule copyRule(Rule originRule){
        Rule newRule = new Rule();
        newRule.setRuleId(originRule.getRuleId());
        newRule.setParent(originRule.getParent());
        newRule.setConditionSet(originRule.getConditionSet());
        newRule.setConclusion(originRule.getConclusion());
        newRule.setChildRuleList((Vector) originRule.getChildRuleList().clone());
        newRule.setCornerstoneCase(originRule.getCornerstoneCase());
        
        return newRule;
    }
    
    
}
