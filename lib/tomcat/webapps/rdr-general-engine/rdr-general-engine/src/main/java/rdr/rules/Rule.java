/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.rules;

import rdr.api.RDRBroker;
import rdr.api.RDRBrokerFactory;
import rdr.api.RDRInterface;
import rdr.api.RDRServiceUtil;
import rdr.apimsg.ConditionItem;
import rdr.apimsg.KARequest;
import rdr.apimsg.RDRResponse;
import rdr.apps.Main;
import rdr.cases.Case;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import javax.swing.tree.TreeNode;
import rdr.cases.CaseSet;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.logger.Logger;
import rdr.utils.RDRConstants;
import rdr.utils.Utility;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class Rule  {
    /**
     * Default rule identifier
     */
    public static final int ROOT_RULE_ID = 0;
    
    /**
     * NULL rule identifier
     */
    public static final int NULL_RULE_ID = -1;
    
    /**
     * Unique rule identifier
     */
    private int ruleId = Rule.NULL_RULE_ID;
    
    /**
     * Parent rule 
     */
    private Rule parentRule = null;  
    
    /**
     * List of child rules
     */
    private Vector<Rule> childRuleList;
    
    /**
     * cornerstone case
     */
    private CornerstoneCase cornerstoneCase;
    
    /**
     * List of conditions
     */
    private ConditionSet conditionSet = new ConditionSet();
    /**
     * List of conditions
     */
    private Conclusion conclusion;
    /**
     * custom object
     */
    private Object customObject;
    
    /**
     * creation date
     */
    private Date creationDate = null;
    
    /**
     * update date
     */
    private Date updateDate = null;
    
    /**
     * Constructor
     */
    public Rule() {
        this.ruleId = Rule.NULL_RULE_ID;
        this.conditionSet = new ConditionSet();
        this.conclusion = null;
        this.childRuleList = new Vector<Rule>();
    }
    
    
    /**
     * Constructor
     * @param aRule
     */
    public Rule(Rule aRule) {
        this.ruleId = aRule.getRuleId();
        this.parentRule = aRule.getParent();
        this.conditionSet = aRule.getConditionSet();
        this.conclusion = aRule.getConclusion();
        this.cornerstoneCase = aRule.getCornerstoneCase();
        this.childRuleList = aRule.childRuleList;
        this.customObject = aRule.customObject;
    }

    /**
     * Constructor
     * @param ruleId
     * @param parentRule
     * @param conditionSet
     * @param conclusion
     * @param cornerstoneCase 
     */
    public Rule(int ruleId, Rule parentRule, ConditionSet conditionSet, Conclusion conclusion, 
            CornerstoneCase  cornerstoneCase) {
        this.ruleId = ruleId; 
        this.conditionSet = conditionSet;
        this.conclusion = conclusion;
        this.cornerstoneCase = cornerstoneCase;
        this.childRuleList = new Vector<Rule>();
        this.parentRule = parentRule;
    }     
  
    /**
     * Get rule id
     * @return 
     */
    public int getRuleId() {
        return this.ruleId;
    }
    
    /**
     * Set rule id
     * @param id 
     */
    public void setRuleId(int id) {
        this.ruleId = id;
    }
    
    public boolean isStoppingRule()
    {
    	if (this.conclusion != null)
    	{
    		if (this.conclusion.getConclusionId() < 0)
    			return true;
    	}
    	
    	return false;
    }
    
    /**
     * Returns true if the rule is valid to be used (Checks whether the rule has all the required components).
     * @return
     */
    public boolean isRuleValid() {      
        if(this.conditionSet==null || this.conditionSet.getConditionAmount()==0){
            return false;
        }
        if(this.conclusion==null){
            return false;
        }
        if(this.parentRule==null || this.parentRule.getRuleId()==NULL_RULE_ID){
            return false;
        }
        return true;
    }
    
    /**
     * Set parent rule
     * @param parentRule
     */
    public void setParent(Rule parentRule) {
        this.parentRule=parentRule;
    }
    
    /**
     * Get parent rule
     * @return 
     */
    public Rule getParent() {
        if(this.parentRule!=null){
            return this.parentRule;
        } else {
            return null;
        }                
    }
    
    
    /**
     * Returns true if parent rule is exist
     * @return 
     */
    public boolean isParentExist() {
        if(this.parentRule==null){
            return false;
        } else {
            return this.parentRule.getRuleId()!=NULL_RULE_ID;
        }
    }
    
    
    /**
     * Set child rule list
     * @param childRuleList 
     */
    public void setChildRuleList(Vector childRuleList) {
        this.childRuleList =childRuleList;        
    }
    
    /**
     * Get child rule list
     */
    public Vector<Rule> getChildRuleList() {
        return this.childRuleList;
    }
    
    
    /**
     * Add rule to child rule list
     * @param childRule
     */
    public void addChildRule(Rule childRule) 
    {
        if(!this.isRuleAddedInChildRuleList(childRule))
        {
        	//Logger.info("added !!!");
            this.childRuleList.add(childRule);
        }
    }
    
    /**
     * Returns true if given rule is in the child list
     * @param childRule
     * @return 
     */
    public boolean isRuleAddedInChildRuleList(Rule childRule) {
        boolean result = false;
        for(Rule aRule: this.childRuleList){
            if(aRule.getConclusion() != null && childRule.getConclusion() != null){
                if(aRule.getConditionSet() != null && childRule.getConditionSet() != null){
                    if(aRule.getConditionSet().equals(childRule.getConditionSet()) && aRule.getConclusion().equals(childRule.getConclusion())){
                        result = true;
                    } 
                } else {
                    if(aRule.getConclusion().equals(childRule.getConclusion())){
                        result = true;
                    }
                }
            } else {
            }
        }
        return result;
    }
    
    
    /**
     * Get a rule (at index) in child rule list 
     * @param index
     * @return 
     */
    public Rule getChildAt(int index) {
        if (childRuleList == null) {
            throw new ArrayIndexOutOfBoundsException("rule has no children");
        }
        return (Rule)childRuleList.elementAt(index);
    }

    /**
     * Get the number of child rules 
     *
     * @return an int giving the number of child rules
     */
    public int getChildRuleCount() {
        if (childRuleList == null) {
            return 0;
        } else {
            return childRuleList.size();
        }
    }

    /**
     * clear the child rule list
     *
     */
    public void clearChildRuleList() {
        this.childRuleList = new Vector<Rule>();
    }

    /**
     * Get the index of rule in the child rule list of parent rule
     *
     * @param aRule
     * @return 
     */
    public int getIndex(Rule aRule) {
        if (aRule == null) {
            throw new IllegalArgumentException("this rule is null");
        }

        if (!isRuleChild(aRule)) {
            return -1;
        }
        return aRule.getParent().getChildIndex(aRule);
    }

    /**
     * Get the index of the child rule in the child rule list of the current rule.
     *
     * @return 
     */
    public int getChildIndex(Rule aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("this rule  is null");
        }

        if (!isRuleChild(aChild)) {
            return -1;
        }
        return childRuleList.indexOf(aChild);
    }
    
    /**
     * Get path rule condition set
     * @return the set of conditions that make up the rule
     */
    public ConditionSet getPathRuleConditionSet(boolean bExceptLeaf) {
        return this.getPathRuleSet(bExceptLeaf).getConditionSet();
    }
    
    /**
     * Get condition set
     * @return the set of conditions that make up the rule
     */
    public ConditionSet getConditionSet() {
        return this.conditionSet;
    }

    /**
     * Set the condition set.
     *
     * @param conditonSet
     */
    public void setConditionSet(ConditionSet conditonSet) {
        this.conditionSet = conditonSet;
    }
    
    /**
     * Get conclusion 
     * @return the set of conditions that make up the rule
     */
    public Conclusion getConclusion() {
        return this.conclusion;
    }

    /**
     * Set a conclusion
     * @param conclusion
     */
    public void setConclusion(Conclusion conclusion) {
        this.conclusion = conclusion;
    }

    /**
     * Delete a conclusion
     */
    public void deleteConclusion() {
        this.conclusion = null;
    }
    
    /**
     * Get a cornerstone case
     * @return 
     */
    public CornerstoneCase getCornerstoneCase() {
        return this.cornerstoneCase;
    }

    /**
     * Set a cornerstone case
     * @param cornerstoneCase
     */
    public void setCornerstoneCase(CornerstoneCase cornerstoneCase) {
        this.cornerstoneCase = cornerstoneCase;
    }
    
    public void setCreationDate(java.util.Date pDate)
    {
    	creationDate = pDate;
    }
    
    public void setUpdateDate(java.util.Date pDate)
    {
    	updateDate = pDate;
    }
    
    public java.util.Date getCreationDate()
    {
    	return creationDate;
    }

    public java.util.Date getUpdateDate()
    {
    	return updateDate;
    }
    
    /**
     * Returns true if <code>aRule</code> is a child of this rule.  If
     * <code>aRule</code> is null, this method returns false.
     *
     * @return  true if <code>aRule</code> is a child of this rule; false if
     *                  <code>aRule</code> is null
     */
    public boolean isRuleChild(Rule aRule) {
        boolean result;

        if (aRule == null) {
            result = false;
        } else {
            if (getChildRuleCount() == 0) {
                result = false;
            } else {                
                if(aRule.isParentExist()){
                    result = (aRule.getParent().getRuleId() == this.getRuleId());
                } else {
                    result = false;
                }
            }
        }

        return result;
    }
    
    /**
     * Returns true if <code>aRule</code> is a root of this rule
     * -- if it is this rule, this rule's parent, or an root of this
     * rule's parent.  (Note that a rule is considered an root of itself.)
     * If <code>aRule</code> is null, this method returns false.  This
     * operation is at worst O(h) where h is the distance from the root to
     * this rule.
     * @param   aRule     rule to test as a root of this rule
     * @return  true if this rule is a descendant of <code>aRule</code>
     */
    public boolean isRootRule(Rule aRule) {
        if (aRule == null) {
            return false;
        }

        Rule root = this;

        do {
            if (root == aRule) {
                return true;
            }
        } while((root = root.getParent()) != null);

        return false;
    }
    
    /**
     * Returns true if this rule has no rules in the decision list.  
     *
     * @return  true if this node has no rules in the decision list.  
     */
    public boolean isLeaf() {
        return (getChildRuleCount() == 0);
    }

    
    public void setCustomObject(Object customObject) {
        this.customObject = customObject;
    }
    
    /**
     * Returns the number of levels above this rule -- the distance from
     * the root to this rule.  If this rule is the root, returns 1.
     *
     * @return  the number of levels above this rule
     */
    public int getLevel() {
        Rule ancestor;
        int levels = 1;

        ancestor = this;
        while((ancestor = ancestor.getParent()) != null){
            levels++;
        }

        return levels;
    }
    
    /**
      * Returns the path from the root, to get to this rule.  The last
      * element in the path is this rule.
      *
      * @return an array of rules giving the path, where the
      *         first element in the path is the root and the last
      *         element is this rule.
      */
    public Rule[] getPath() {        
        int pathDepth = this.getLevel();
        
        Rule[] path = new Rule[pathDepth];
        Rule currentRule = (Rule)this;
        for(int i=0;i<pathDepth;i++) {
            path[i] = currentRule;
            currentRule = currentRule.getParent();
        }
        return path;
    }
    
    /**
      * Returns the path Rule Set from the root, to get to this rule.  The last
      * element in the path is this rule.
      *
      * @return a set of rules giving the path, where the
      *         first element in the path is the root and the last
      *         element is this rule.
      */
    public RuleSet getPathRuleSet(boolean bExceptLeaf) 
    {        
        int pathDepth = this.getLevel();
        
        RuleSet pathRuleSet = new RuleSet();
        Rule currentRule = null;
        
        if (bExceptLeaf) 
        {
        	if (this.isParentExist() == false) return pathRuleSet;
        	currentRule = this.getParent();
        }
        else currentRule = (Rule)this;
        
        Rule childRule = null;
        for(int i=0;i<pathDepth;i++) {            
            Rule addingParentRule = RuleBuilder.copyRule(currentRule);
            addingParentRule.setRuleId(currentRule.getRuleId());
            
            addingParentRule.clearChildRuleList();
            if(childRule!=null){
                addingParentRule.addChildRule(childRule);
            }
            pathRuleSet.addRule(addingParentRule);
            
            if(currentRule.isParentExist()){
                childRule = addingParentRule;
                currentRule = currentRule.getParent();                
            } 
        }
        pathRuleSet.setRootRule(pathRuleSet.getBase().get(0));
        return pathRuleSet;
    }
    
    /**
     * Getting base validating case set which is retrieved from all child rules of current rule including current rule
     * @return 
     */
    public CornerstoneCaseSet getBaseValidatingCornerstoneCaseSet() {
        CornerstoneCaseSet baseValidatingCaseSet = new CornerstoneCaseSet();
        
        Set rules = this.getRuleSetToBottom().getBase().entrySet();
        Iterator ruleIterator = rules.iterator();
        while (ruleIterator.hasNext()) {
            Map.Entry me = (Map.Entry) ruleIterator.next();
            Rule rule = (Rule)me.getValue();
            if(rule.getRuleId()!=Rule.ROOT_RULE_ID) {
                if(rule.getCornerstoneCase()!=null){
                    baseValidatingCaseSet.addCornerstoneCase(rule, rule.getCornerstoneCase());
                }
            }
        }
        
        return baseValidatingCaseSet;
    }
    
    /**
     * Getting all child rules of current rule including current rule
     * @return 
     */
    public RuleSet getRuleSetToBottom() {
        RuleSet allChildRuleSet = new RuleSet();
        
        allChildRuleSet.addRule(this);
        allChildRuleSet.combineRuleSet(this.getAllChildRules());
        
        return allChildRuleSet;
    }
    
    /**
     * Getting all child rules of current rule excluding current rule
     * @return 
     */
    public RuleSet getAllChildRules(){
        RuleSet childRuleSet = new RuleSet();
        childRuleSet.addRule(this);
        
        for(Rule childRule :this.childRuleList){
            childRuleSet.addRule(childRule);
            if(!childRule.isLeaf()){
                childRuleSet.combineRuleSet(childRule.getAllChildRules());
            }
        }
        return childRuleSet;
    }

    /**
     * Finds and returns the bottom rule that is a descendant of this rule
     *
     * @return  the bottom rule of this rule
     */
    public Rule getBottomRule() {
        Rule currentRule = this;

        while (!currentRule.isLeaf()) {
            int childRuleCount = currentRule.getChildRuleCount();
            currentRule = (Rule)currentRule.getChildAt(childRuleCount-1);
        }

        return currentRule;
    }
    
    /**
     * Check to see is this rule satisfied by the case c?
     *
     * @param currentCase
     * @return 
     */
    public boolean isSatisfied(Case currentCase) {
        // root rule is always true
        if(this.ruleId==0){
            return true;
        } else {     
            //TODO if there is no condition, returns true
            if(this.conditionSet == null || this.conditionSet.getConditionAmount()==0){
                return true;
            }
            Iterator iterator = this.conditionSet.getBase().iterator();      
            while (iterator.hasNext()) {
                Condition condition = (Condition)iterator.next();
                if(!condition.isSatisfied(currentCase))
                    return false;
            }
            return true;
        }
    }    
    
    /**
     * Returns true if <code>anotherRule</code> is an ancestor of this rule
     * -- if it is this rule, this rule's parent, or an ancestor of this
     * rule's parent.  (Note that a rule is considered an ancestor of itself.)
     * If <code>anotherRule</code> is null, this method returns false.  This
     * operation is at worst O(h) where h is the distance from the root to
     * this rule.
     *
     * @param   anotherRule     rule to test as an ancestor of this rule
     * @return  true if this rule is a descendant of <code>anotherRule</code>
     */
    public boolean isRuleAncestor(Rule anotherRule) {
        if (anotherRule == null) {
            return false;
        }

        Rule ancestor = this;
        do {
            if (ancestor == anotherRule) {
                return true;
            }
        } while((ancestor = ancestor.getParent()) != null);

        return false;
    }

    /**
     * Returns true if <code>anotherRule</code> is a descendant of this rule
     * -- if it is this rule, one of this rule's children, or a descendant of
     * one of this rule's children.  Note that a rule is considered a
     * descendant of itself.  If <code>anotherRule</code> is null, returns
     * false.  This operation is at worst O(h) where h is the distance from the
     * root to <code>anotherRule</code>.
     *
     * @param   anotherRule     rule to test as descendant of this rule
     * @return  true if this rule is an ancestor of <code>anotherRule</code>
     */
    public boolean isNRuleDescendant(Rule anotherRule) {
        if (anotherRule == null)
            return false;

        return anotherRule.isRuleAncestor(this);
    }
    
    /** child의 모든 조건을 포함하는 경우 true
     *  leaf이거나 child의 모든 조건을 포함하지 않으면 false
     *  true이면 이 rule에 만족하는 case는 반드시 child에 만족하므로 이 rule에 서 결론이 나올수 없음
     * @return
     */
    public boolean isDeprecated()
    {
    	if (this.isLeaf()) return false;
    	if (this.conditionSet == null) return true;
    	
    	for (Rule aRule: this.childRuleList)
    	{
    		if (aRule.getConditionSet() == null) continue;
    		if (this.conditionSet.contains(aRule.getConditionSet()) == false)
    			return false;
    	}
    	return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rule)) {
            return false;
        } else {
            Rule comparingRule = (Rule) o;
            if(this.isRuleValid() && comparingRule.isRuleValid()){
                if(!this.conditionSet.equals(comparingRule.conditionSet)|
                        !this.conclusion.equals(comparingRule.conclusion)){
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;	//found no problems, so equal.
    }        

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.conditionSet);
        hash = 79 * hash + Objects.hashCode(this.conclusion);
        return hash;
    }

  
    /**
     * Convert the rule to readable text.
     * @return 
     */
    @Override
    public String toString() 
    {
        String strRule = "";
        if(this!=null)
        {
            if(this.ruleId==0 )
            {
                if(this.conclusion==null)
                {
                    strRule = "Root";
                } 
                else 
                {
                    strRule = "Root";
                    strRule += " THEN ";
                    strRule +=  this.getConclusion().toString();
                
//                    if(this.isRuleValid()){
//                        if(this.getConclusion().getConclusionName().equals("")){
//                            strRule += " THEN ";
//                            strRule +=  this.getConclusion().toString();
//                        }                        
//                    }
                }
            } 
            else 
            {
                strRule += "["+ this.ruleId + "] " +" IF ";
                if(this.conditionSet!=null)
                {
                    strRule +=  this.conditionSet.toString();
                }
                strRule +=  " THEN ";
                if (this.getConclusion().getConclusionId() < 0)
                {
                	strRule += "Stopping Rule";
                }
                else if (this.conclusion!=null)
                {
                    strRule +=  this.conclusion.toString();
                }
            }
        }
        return strRule;
    }


}
