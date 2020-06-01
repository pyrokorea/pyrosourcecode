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
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import rdr.cases.Case;
import rdr.cases.CaseSet;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;

/**
 * This class is used to define rule set (kb) 
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class RuleSet {
    
    /**
     * RuleSet name
     */
    protected String ruleSetName;
    
    /**
     * RuleSet description
     */
    protected String ruleSetDesc;
    
    /**
     * Root rule
     */
    protected Rule rootRule;
    
   
    /**
     * Rule set
     */
    protected LinkedHashMap<Integer, Rule> ruleSet = new LinkedHashMap<>();
    
   
    /**
     * Constructor
     */
    public RuleSet() {
        this.ruleSet = new LinkedHashMap<>();
    }
    
    /**
     * Constructor with root rule
     * @param rule 
     */
    public RuleSet(Rule rule){
        this.ruleSet = new LinkedHashMap<>();
        this.ruleSet.put(rule.getRuleId(),rule);
        this.rootRule = rule;        
    }
    
    /**
     * Constructor
     * @param ruleSet 
     */
    public RuleSet(RuleSet ruleSet){
        this.ruleSet = ruleSet.ruleSet;
        this.rootRule = ruleSet.rootRule;
        this.ruleSetName = ruleSet.ruleSetName;
        this.ruleSetDesc = ruleSet.ruleSetDesc;
    }
    
    
    /**
     * Set rule set
     * @param ruleSet 
     */
    public void setRuleSet(RuleSet ruleSet) {
        this.ruleSet = ruleSet.getBase();
    }
    
    /**
     * Get base 
     * @return 
     */
    public LinkedHashMap<Integer, Rule> getBase(){
        return this.ruleSet;
    }    
    
    public HashSet<Integer> getRuleIds()
    {
    	return new HashSet<Integer>(this.ruleSet.keySet());
    }
    
    /**
     * Convert path to rule set
     * @param rulePath
     * @return 
     */
    public RuleSet convertRulePathToRuleSet(Rule[] rulePath){
        RuleSet result = new RuleSet();
        int pathDepth = rulePath.length;
        
        Rule parentRule = rulePath[0]; 
        
        for(int i=1; i<pathDepth; i++){
            Rule currentRule = rulePath[i];
//            result.addExceptionRule(parentRule, currentRule);
            parentRule = currentRule;
        }
                
        return result;
    }    
    
    /**
     * Convert path to rule set
     * @param rulePath 
     */
    public void storeRulePathToRuleSet(Rule[] rulePath){
        this.ruleSet = convertRulePathToRuleSet(rulePath).getBase();
    }    
    
    /**
     * Add rule 
     * @param newRule
     * @return  
     */
    public boolean addRule(Rule newRule) {
        if(this.ruleSet.containsKey(newRule.getRuleId())){
            return false;
        } else {
            this.ruleSet.put(newRule.getRuleId(), newRule);
        }        
        return true;
    }
    
    
    /**
     * Remove a rule by rule
     * @param aRule 
     * @return  
     */
    public boolean deleteRule(Rule aRule) {
        if(this.ruleSet.containsValue(aRule)){
            this.ruleSet.remove(aRule.getRuleId());
        } else {
            return false;
        }
        return true;
    }
    
    /**
     * Remove a rule by rule id
     * @param ruleId 
     * @return  
     */
    public boolean deleteRuleByRuleId(int ruleId) {
        if(this.ruleSet.containsKey(ruleId)){
            this.ruleSet.remove(ruleId);
        } else {
            return false;
        }
        return true;
    }
    
    /**
     * Get a rule by rule id
     * @param ruleId
     * @return 
     */
    public Rule getRuleById(int ruleId) {
        if(this.ruleSet.containsKey(ruleId)){
            return this.ruleSet.get(ruleId);
        } else {
            return null;
        }
    }
    
    /**
     * Get a rule by rule
     * @param aRule
     * @return 
     */
    public Rule getRule(Rule aRule) {
        if(this.ruleSet.containsKey(aRule.getRuleId())){
            return this.ruleSet.get(aRule.getRuleId());
        } else {
            return null;
        }
    }
    
    /**
     * Get a condition set
     * @return 
     */
    public ConditionSet getConditionSet(){
        ConditionSet aConditionSet = new ConditionSet();
        
        Set rules = this.ruleSet.entrySet();
        Iterator iterator = rules.iterator();

        while (iterator.hasNext()) {
            Map.Entry me = (Map.Entry) iterator.next();
            Rule aRule = (Rule)me.getValue();
            if(aRule.getConditionSet() != null){
                aConditionSet.putConditionSet(aRule.getConditionSet());
            }
        }
        
        return aConditionSet;
    }
    
    /**
     * Get a rule path condition set
     * @return 
     */
    public ConditionSet getRulePathConditionSet(boolean bExceptLeaf){
        ConditionSet aConditionSet = new ConditionSet();
        
        Set rules = this.ruleSet.entrySet();
        Iterator iterator = rules.iterator();

        while (iterator.hasNext()) {
            Map.Entry me = (Map.Entry) iterator.next();
            Rule aRule = (Rule)me.getValue();
            aConditionSet.putConditionSet(aRule.getPathRuleConditionSet(bExceptLeaf));
        }
        
        return aConditionSet;
    }
    
    /**
     * Get a bottom rule 
     * @return 
     */
    public Rule getBottomRule() {
        return this.rootRule.getBottomRule();
    }
    
    /**
     * Get a last rule 
     * @return 
     */
    public Rule getLastRule() {
        Rule rule = new Rule();
        Set rules = this.ruleSet.entrySet();
        Iterator ruleIterator = rules.iterator();
        while (ruleIterator.hasNext()) {
            Map.Entry me = (Map.Entry) ruleIterator.next();
            rule = (Rule)me.getValue();
        }
        return rule;
    }
    
    /**
     * Get a new rule id
     * @return 
     */
    public int getNewRuleId() {
        if(this.ruleSet.size()>0){
            Set rules = this.ruleSet.entrySet();
            Iterator iterator = rules.iterator();
            int maxId = 0;
            
            while (iterator.hasNext()) {
                Map.Entry me = (Map.Entry) iterator.next();
                Rule aRule = (Rule)me.getValue();
                maxId = Math.max(maxId, aRule.getRuleId());
            }
            return maxId+1;
        } else {
            return 2;
        }
    }
    
    /**
     * Set a root rule.
     * @param rootRule
     */
    public void setRootRule(Rule rootRule) {
        if(this.ruleSet.containsKey(0)){
            this.ruleSet.remove(0);
        }
        this.ruleSet.put(0, rootRule);
        this.rootRule = rootRule;
    }
    
    /**
     * Get a root rule 
     * @return 
     */
    public Rule getRootRule() {
        return this.rootRule;
    }
    
    /**
     * Set a exception rule
     */
    public void setExceptionRule(Rule parentRule, Rule childRule) {
        this.getRuleById(parentRule.getRuleId()).addChildRule(childRule);
        
    }
    
    /**
     * returns true if a rule (id) exists in Rule Set
     * @param ruleId
     * @return 
     */
    public Boolean isRuleExist(int ruleId) {
        return this.ruleSet.containsKey(ruleId);
    }
    
    /**
     * returns true if a rule exists in Rule Set
     * @param aRule
     * @return 
     */
    public Boolean isRuleExist(Rule aRule) {
        return this.ruleSet.containsValue(aRule);
    }
    
    /**
     * returns true if a conclusion is new conclusion in this kb
     * @param aConclusion
     * @return 
     */
    public Boolean isNewConclusion(Conclusion aConclusion) {
        return !this.getConclusionSet().isExist(aConclusion);
    }
    
    public boolean isNewConclusion(String aConclusionName)
    {
    	return !this.getConclusionSet().isExist(aConclusionName);
    }
    
    /**
     * Get conclusion set
     * @return return conclusion set of this rule set (knowledge base)
     */
    public ConclusionSet getConclusionSet() {
        ConclusionSet result = new ConclusionSet();
        Set rules = this.ruleSet.entrySet();
        Iterator iterator = rules.iterator();
        while (iterator.hasNext()) {
            Map.Entry me = (Map.Entry) iterator.next();    
            Rule aRule = (Rule)me.getValue();
            if(aRule.getConclusion()!=null) {
            	//20191017 blocked
                //if(!aRule.getConclusion().getConclusionName().equals("")){
                    result.addConclusion(aRule.getConclusion());
                //}
            }
        }
        return result;
    }
    
    /** get conclusion map
     * 
     * @return map(key:rule id, value:conclusionName)
     */
    public HashMap<Integer, String> getConclusionMap()
    {
    	HashMap<Integer, String> tMap = new HashMap<Integer, String>();
        Set rules = this.ruleSet.entrySet();
        Iterator iterator = rules.iterator();
        while (iterator.hasNext()) 
        {
            Map.Entry me = (Map.Entry) iterator.next();    
            Rule aRule = (Rule)me.getValue();
            if(aRule.getConclusion()!=null) 
            {
            	tMap.put(aRule.getRuleId(), aRule.getConclusion().getConclusionName());
            }
        }
        return tMap;
    }
    
    /**
     * Get rule set identifier
     * @return 
     */
    public String getRuleSetName() {
        return this.ruleSetName;
    }
    
    /**
     * Set rule set identifier 
     * @param name
     */
    public void setRuleSetName(String name) {
        this.ruleSetName = name;
    }    
    
    /**
     * Get RuleSet description
     * @return 
     */
    public String getRuleSetDesc() {
        return this.ruleSetDesc;
    }
    
    /**
     * Set RuleSet description
     * @param desc 
     */
    public void setRuleSetDesc(String desc){
        this.ruleSetDesc = desc;
    }
    
    /**
     * Retrieve base validating cornerstone case set.
     * @return 
     */
    public CornerstoneCaseSet getBaseValidatingCornerstoneCaseSet(){
        CornerstoneCaseSet baseValidatingCornerstoneCaseSet = new CornerstoneCaseSet();
        
        Set rules = this.ruleSet.entrySet();
        Iterator ruleIterator = rules.iterator();
        while (ruleIterator.hasNext()) {
            Map.Entry me = (Map.Entry) ruleIterator.next();
            Rule rule = (Rule)me.getValue();
            if(rule.getRuleId()!=Rule.ROOT_RULE_ID){
                baseValidatingCornerstoneCaseSet.putCornerstoneCaseSet(rule.getBaseValidatingCornerstoneCaseSet());
            }
        }
        
        return baseValidatingCornerstoneCaseSet;
    }
    
    /**
     * Retrieve base validating cornerstone case set for alternative mode
     * @return 
     */
    public CornerstoneCaseSet getBaseValidatingCornerstoneCaseSetAlternative()
    {
        CornerstoneCaseSet baseValidatingCornerstoneCaseSet = new CornerstoneCaseSet();
        
        Set rules = this.ruleSet.entrySet();
        Iterator ruleIterator = rules.iterator();
        while (ruleIterator.hasNext()) 
        {
            Map.Entry me = (Map.Entry) ruleIterator.next();
            Rule rule = (Rule)me.getValue();
            
            if(rule.getRuleId()!=Rule.ROOT_RULE_ID)
            {
                baseValidatingCornerstoneCaseSet.putCornerstoneCaseSet(rule.getParent().getBaseValidatingCornerstoneCaseSet());
            }
        }
        
        return baseValidatingCornerstoneCaseSet;
    }
    
    /**
     * Get the number of rules in rule set
     * @return
     */
    public int getSize(){
        return this.ruleSet.size();
    }
    
    /**
     * Check whether the rule set is empty except root rule
     * @return
     */
    public boolean isEmpty(){
        if(this.ruleSet.isEmpty()){
            return true;
        } else if(this.ruleSet.size()==1){
            if(this.ruleSet.containsKey(0)) {
                return this.ruleSet.get(0).getConclusion().getConclusionName().equals("");
            } else {
                return false;
            }
        }
        return false;
    }
    
    public RuleSet getRuleSetbyConclusion(Conclusion conclusion){
        RuleSet result = new RuleSet();
        
        Set rules = this.ruleSet.entrySet();
        Iterator ruleIterator = rules.iterator();
        while (ruleIterator.hasNext()) {
            Map.Entry me = (Map.Entry) ruleIterator.next();
            Rule rule = (Rule)me.getValue();
            if(rule.getConclusion().equals(conclusion)){
                result.addRule(rule);
            }
        }
        return result;
    }
    
    private Rule buildRuleTree (Rule parentRule) 
    {
        Set rules = this.ruleSet.entrySet();
        Iterator ruleIterator = rules.iterator();
        while (ruleIterator.hasNext()) 
        {                        
            Map.Entry me = (Map.Entry) ruleIterator.next();
            Rule currentRule = (Rule) me.getValue();
            if(currentRule.isRuleValid())
            {
                if(currentRule.isParentExist())
                {
                    if(currentRule.getParent().getRuleId()==parentRule.getRuleId())
                    {                
                        if(currentRule.getChildRuleCount()>0)
                        {
                            parentRule.addChildRule(buildRuleTree(currentRule));
                        } 
                        else 
                        {
                            parentRule.addChildRule(currentRule);
                        }                
                    }
                }
            } 
        }
        return parentRule;
    }
    
    public void setRootRuleTree (){
        this.rootRule = this.buildRuleTree(this.ruleSet.get(0));
    }
    
    public void deleteInvalidRules() {
        Set rules = this.ruleSet.entrySet();
        Iterator ruleIterator = rules.iterator();
        while (ruleIterator.hasNext()) {                        
            Map.Entry me = (Map.Entry) ruleIterator.next();
            Rule currentRule = (Rule) me.getValue();
            if(currentRule.isRuleValid()){
                this.ruleSet.remove(currentRule.getRuleId());
            }
        }
    }
    
    public void combineRuleSet(RuleSet aRuleSet){
        Set rules = aRuleSet.getBase().entrySet();
        Iterator ruleIterator = rules.iterator();
        while (ruleIterator.hasNext()) {                        
            Map.Entry me = (Map.Entry) ruleIterator.next();
            Rule currentRule = (Rule) me.getValue();
            if(currentRule.isRuleValid() && !this.ruleSet.containsKey(currentRule.getRuleId())){
                this.ruleSet.put(currentRule.getRuleId(), currentRule);
            }
        }
        
    }
    
    
    public String toStringOnlyConclusion() {
        String strRuleSet = "";
        
        Set inferenceResults = this.ruleSet.keySet();
        int rulesAmount = this.ruleSet.size();
        
        // Get a liat of iterator for backward iterating
        ListIterator<Integer> iterator = new ArrayList(inferenceResults).listIterator(rulesAmount);
        while (iterator.hasPrevious()){ 
            Integer key = iterator.previous();
            Rule aRule = this.ruleSet.get(key);
            if(!aRule.getConclusion().equals("")){
                strRuleSet += " - " + aRule.getConclusion() + "\n";
            }
        }
        return strRuleSet;
    }
    
    @Override
    public String toString() {
        String strRuleSet = "RuleSet: ";
        Set rules = this.ruleSet.entrySet();
        Iterator iterator = rules.iterator();
        while (iterator.hasNext()) {
            Map.Entry me = (Map.Entry) iterator.next();
            Rule aRule = (Rule)me.getValue();
            
            strRuleSet += aRule.toString() + "\n";
        }
        return strRuleSet;
    }
    
  
}
