/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.reasoner;

import rdr.cases.Case;
import rdr.rules.Rule;
import rdr.rules.RuleSet;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class MCRDRReasoner extends Reasoner{
    private RuleSet inferenceResult;
    
    /**
     * Constructor
     */
    public MCRDRReasoner() {
        super();
        this.inferenceResult = new RuleSet();
    }
    
    /**
     * Constructor 
     * @param ruleSet
     * @param currentCase
     */
    public MCRDRReasoner(RuleSet ruleSet,Case currentCase){
        super(ruleSet, currentCase);
        this.inferenceResult = new RuleSet();
    } 

    @Override
    public String getReasonerMethod() {
        return "MCRDR";
    }
    
    /**
     * Set inference result
     * @param inferenceResult 
     */
    @Override
    public void setInferenceResult(RuleSet inferenceResult){
        this.inferenceResult = inferenceResult;
    }
    
    /**
     * Clear inference result
     */
    @Override
    public void clearInferenceResult(){
        this.inferenceResult = new RuleSet();
    }
    
    
    /**
     * Add a rule to inference result
     *
     * @param rule
     * @return
     */
    @Override
    public boolean addRuleToInferenceResult(Rule rule) {
        return this.inferenceResult.addRule(rule);
    }

    /**
     * Delete a rule from the fired rules list
     *
     * @param rule
     * @return
     */
    @Override
    public boolean deleteRuleFromInferenceResult(Rule rule) {
        return this.inferenceResult.deleteRuleByRuleId(rule.getRuleId());
    }  
    
    /**
     * Get inference result
     * @return 
     */
    @Override
    public RuleSet getInferenceResult(){
        return this.inferenceResult;
    }
    
    /**
     * Perform inference with starting rule for multiple classifications
     * @param currentRule
     * @return 
     */
    @Override
    public RuleSet inferenceWithStartingRule(Rule currentRule) 
    {   
        //check whether the current rule is fired
        if (currentRule.isSatisfied(this.currentCase) || currentRule.getRuleId()==startingRule.getRuleId())
        {        
            if (currentRule.getRuleId()!=startingRule.getRuleId())
            {
                //add current rule to fired rule list
                this.addRuleToFiredRules(currentRule);
            }
            // get the number of rules in the decision list of the current rule
            int childCount = currentRule.getChildRuleCount();
        
            //if there is no rule in the decision list, set the current rule as conclusion
            if(childCount == 0) 
            {
                if( currentRule.getRuleId()!=startingRule.getRuleId())
                {
                    this.addRuleToInferenceResult(currentRule);
                }
            } 
            //if there rule in the decision list, test the child rules
            else 
            {
                // to check whether all the rules in the decision list are not valid
                boolean isAllChildRulesNotValid = true;

                //check all child rules
                for (int i = 0; i < childCount; i++) 
                {
                    // get child rule (rule in the decision list) of the current rule
                    Rule childRule = currentRule.getChildAt(i);

                    //check whether the child rule is fired
                    if (childRule.isSatisfied(this.currentCase)) 
                    {
                        //add child rule to fired rule list
                        this.addRuleToFiredRules(childRule);
                        // confirm there is a valid rule in the siblings
                        isAllChildRulesNotValid = false;

                        // if the child rule has its child rules (grand child of the current rule), then inference with the child rule
                        if (childRule.getChildRuleCount()> 0) 
                        {
                            this.inferenceWithStartingRule(childRule);
                        }
                        // if the child rule has no child rules (grand child of the current rule), then set the child rule as conclusion 
                        else 
                        {                             
                            this.deleteRuleFromInferenceResult(currentRule);
                            this.addRuleToInferenceResult(childRule);
                        }
                    } 
                }

                //if there is no valid child rules, set the current rule as conclusion 
                if (isAllChildRulesNotValid == true) 
                {
                    if( currentRule.getRuleId()!=startingRule.getRuleId())
                    {
                        this.addRuleToInferenceResult(currentRule);
                    }
                }        
            }
        }
        return this.inferenceResult;
        
    }
    
    
    /**
     * Perform inference for multiple classifications
     * @param currentRule
     * @return 
     */
    @Override
    public RuleSet inference(Rule currentRule) 
    {   
        //check whether the current rule is fired
        if (currentRule.isSatisfied(this.currentCase))
        {        
            //add current rule to fired rule list
            this.addRuleToFiredRules(currentRule);
            
            // get the number of rules in the decision list of the current rule
            int childCount = currentRule.getChildRuleCount();
        
            //if there is no rule in the decision list, set the current rule as conclusion
            if (childCount == 0) 
            {
                this.addRuleToInferenceResult(currentRule);
            }
            //if there rule in the decision list, test the child rules
            else
            {
                // to check whether all the rules in the decision list are not valid
                boolean isAllChildRulesNotValid = true;

                //check all child rules
                for (int i = 0; i < childCount; i++) 
                {
                    // get child rule (rule in the decision list) of the current rule
                    Rule childRule = currentRule.getChildAt(i);
                    
                    //check whether the child rule is fired
                    if (childRule.isSatisfied(this.currentCase)) 
                    {
                        //add child rule to fired rule list
                        this.addRuleToFiredRules(childRule);
                        // confirm there is a valid rule in the siblings
                        isAllChildRulesNotValid = false;

                        // if the child rule has its child rules (grand child of the current rule), then inference with the child rule
                        if (childRule.getChildRuleCount()> 0) 
                        {
                            this.inference(childRule);
                        } 
                        // if the child rule has no child rules (grand child of the current rule), then set the child rule as conclusion 
                        else 
                        {                             
                            this.deleteRuleFromInferenceResult(currentRule);
                            this.addRuleToInferenceResult(childRule);
                        }
                    } 
                }

                //if there is no valid child rules, set the current rule as conclusion 
                if (isAllChildRulesNotValid == true) 
                {
                    this.addRuleToInferenceResult(currentRule);
                }        
            }
        }
        return this.inferenceResult;
        
    }
}
