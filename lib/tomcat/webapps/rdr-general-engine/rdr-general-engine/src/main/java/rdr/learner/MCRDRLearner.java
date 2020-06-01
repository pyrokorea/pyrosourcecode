/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.learner;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.db.RDRDBManager;
import rdr.logger.Logger;
import rdr.rules.Conclusion;
import rdr.rules.Rule;
import rdr.rules.RuleBuilder;
import rdr.rules.RuleLoader;
import rdr.rules.RuleSet;

/**
 * This class is used to define MCRDR method knowledge acquisition process 
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class MCRDRLearner extends Learner{
    
    /**
     * Inference result
     */
    protected RuleSet inferenceResult = new RuleSet(); 
    
    /**
     * A rule set that suggest wrong conclusion
     */
    protected RuleSet wrongRuleSet = new RuleSet(); 
    
    /**
     * Constructor
     */
    public MCRDRLearner() {
        super();
    }

    /**
     * Constructor
     * @param ruleSet
     * @param currentCase 
     * @param wrongConclusion
     * @param rule 
     */
    public MCRDRLearner(RuleSet ruleSet, Case currentCase, 
            Conclusion wrongConclusion, Rule rule) {
        super(ruleSet, currentCase, wrongConclusion, rule);
    }     
    
    /**
     * Set new rule that will be acquired
     * @param ruleSet 
     */
    @Override
    public void setInferenceResult(RuleSet ruleSet) {
        this.inferenceResult = ruleSet;
    }
    
    /**
     * Get current new rule
     * @return 
     */
    @Override
    public RuleSet getInferenceResult() {
        return this.inferenceResult;
    }
    
    
    /**
     * Add alternative rule
     * @return 
     */
    @Override
    public boolean addAlternativeRule(StringBuilder sb) 
    {
    	if (this.wrongRuleSet.isEmpty())
    	{
    		sb.append("inference result for designated conclusion is not found");
    		return false;
    	}
    	
    	boolean flag = true;
    	RDRDBManager.getInstance().setAutoCommit(false);
    	
        /** 
         *  inferenceResult는 추론결과 Rule 이고 wrongRuleSet은 learnerInit시 지정된 결론의 Rule임.
         *  alter mode는 wrongRule의 parent의 child rule로 생성한다.
         */
    	int cnt = 0;
        Set wrongRuleSet = this.wrongRuleSet.getBase().entrySet();
        Iterator wrongRuleSetIterator = wrongRuleSet.iterator();
        while (wrongRuleSetIterator.hasNext()) 
        {
            Map.Entry me = (Map.Entry) wrongRuleSetIterator.next();
            //get alter rule
            Rule alterRule = (Rule) me.getValue();
            
            //add this rule only if there is no rule in the same level (siblings) 
            if(! this.ruleSet.getRule(alterRule.getParent()).isRuleAddedInChildRuleList(this.newRule))
            {
                Rule addingRule = RuleBuilder.copyRule(this.newRule);
                addingRule.setRuleId(Main.KB.getNewRuleId());
                
                // if this is new conclusion, add it into db
                int conclusionId = addingRule.getConclusion().getConclusionId();

                if(this.ruleSet.isNewConclusion(addingRule.getConclusion()))
                {
                    conclusionId = this.ruleSet.getConclusionSet().getNewConclusionId();
                    flag &= RuleLoader.insertRuleConclusions(conclusionId, addingRule.getConclusion());
                    addingRule.getConclusion().setConclusionId(conclusionId);
                }
                //set parent rule of the new rule as the parent rule of the alternative rule (retrieved from the wrong rule set).
                addingRule.setParent(this.ruleSet.getRuleById(alterRule.getParent().getRuleId()));

                //set the parent rule of the alternative rule (retrieved from the wrong rule set) as the new rule.
                this.ruleSet.getRuleById(alterRule.getParent().getRuleId()).addChildRule(addingRule);      
                
                flag &= RuleLoader.insertRule(addingRule.getRuleId(), addingRule, addingRule.getConclusion().getConclusionId());                
                
                if (flag) this.ruleSet.addRule(addingRule);
                
                cnt++;
            }
            else 
            {
                Logger.info("parent rule already contains same child rule, parent rule id : " + alterRule.getParent().getRuleId());
            }
        }
        
        //불필요함, 속도느려짐
        //this.ruleSet.setRootRuleTree();

        RDRDBManager.getInstance().doCommit(flag);
        RDRDBManager.getInstance().setAutoCommit(true);
        
        if (flag == false) sb.append("add rule failed in database");
        else if (cnt == 0) 
        {
        	sb.append("alternative rule is exist, rule is not added");
        	return false;
        }
        
        return flag;
    }
    
    /**
     * Add exception rule
     * @return 
     */
    @Override
    public boolean addExceptionRule(StringBuilder sb) 
    {        
    	if (this.wrongRuleSet.isEmpty())
    	{
    		sb.append("inference result for designated conclusion is not found");
    		return false;
    	}
    	
    	boolean flag = true;
    	RDRDBManager.getInstance().setAutoCommit(false);
    	
        // get wrong rule base set
        Set valueSet = this.wrongRuleSet.getBase().entrySet();
        Iterator wrongRuleSetIterator = valueSet.iterator();
        
        while (wrongRuleSetIterator.hasNext()) 
        {
            Rule addingRule = RuleBuilder.copyRule(this.newRule);
            addingRule.setRuleId(Main.KB.getNewRuleId());
            
            Map.Entry me = (Map.Entry) wrongRuleSetIterator.next();            
            Rule parentRule = (Rule) me.getValue();      
            
            // if this is new conclusion, add it into db
            int conclusionId = addingRule.getConclusion().getConclusionId();
            
            if(this.ruleSet.isNewConclusion(addingRule.getConclusion()))
            {
                conclusionId = this.ruleSet.getConclusionSet().getNewConclusionId();
                flag &= RuleLoader.insertRuleConclusions(conclusionId, addingRule.getConclusion());
                addingRule.getConclusion().setConclusionId(conclusionId);
            }
            //set parent rule of the new rule as the parent rule (retrieved from the wrong rule set)
            addingRule.setParent(this.ruleSet.getRuleById(parentRule.getRuleId()));
            
            //set exception rule of the parent rule (retrieved from the wrong rule set) as the new rule.
            this.ruleSet.getRuleById(parentRule.getRuleId()).addChildRule(addingRule);          
            
            // insert new rule into db
            flag &= RuleLoader.insertRule(addingRule.getRuleId(), addingRule, addingRule.getConclusion().getConclusionId());    
            
            if (flag) this.ruleSet.addRule(addingRule);
        }
        
        //불필요함, 속도느려짐
        //this.ruleSet.setRootRuleTree();

        RDRDBManager.getInstance().doCommit(flag);
        RDRDBManager.getInstance().setAutoCommit(true);
        
        if (flag == false) sb.append("add rule failed in database");
        return flag;
    }
    
    
    /**
     * Add exception rule
     * @return 
     */
    @Override
    public boolean addStoppingRule(StringBuilder sb) 
    {        
    	if (this.wrongRuleSet.isEmpty())
    	{
    		sb.append("inference result for designated conclusion is not found");
    		return false;
    	}
    	
    	boolean flag = true;
    	RDRDBManager.getInstance().setAutoCommit(false);
    	
        // get wrong rule base set
        Set valueSet = this.wrongRuleSet.getBase().entrySet();
        Iterator wrongRuleSetIterator = valueSet.iterator();
        
        while (wrongRuleSetIterator.hasNext()) 
        {
            Rule addingRule = RuleBuilder.copyRule(this.newRule);
            addingRule.setRuleId(Main.KB.getNewRuleId());
            
            Map.Entry me = (Map.Entry) wrongRuleSetIterator.next();
            Rule parentRule = (Rule) me.getValue();      
            if(parentRule.getRuleId() != Rule.ROOT_RULE_ID)
            {
                //set parent rule of the new rule as the parent rule (retrieved from the wrong rule set)
                addingRule.setParent(this.ruleSet.getRuleById(parentRule.getRuleId()));

                //set exception rule of the parent rule (retrieved from the wrong rule set) as the new rule.
                this.ruleSet.getRuleById(parentRule.getRuleId()).addChildRule(addingRule);          

                // insert new rule into db
                flag &= RuleLoader.insertRule(addingRule.getRuleId(), addingRule, addingRule.getConclusion().getConclusionId());    

                if (flag) this.ruleSet.addRule(addingRule);
            }
        }
        
        //불필요함, 속도느려짐
        //this.ruleSet.setRootRuleTree();

        RDRDBManager.getInstance().doCommit(flag);
        RDRDBManager.getInstance().setAutoCommit(true);
        
        if (flag == false) sb.append("add rule failed in database");
        return flag;
    }
    
    /**
     * Delete wrong conclusion.
     */
    @Override
    public void deleteWrongConclusion() {
        this.wrongConclusion = null;
        this.wrongRuleSet = new RuleSet();
    }
    
    
    /**
     * Set wrong conclusion and set wrong rule set
     * @param wrongConclusion
     */
    @Override
    public void setWrongConclusion(Conclusion wrongConclusion) {
        this.wrongConclusion = wrongConclusion;        
        this.wrongRuleSet = this.inferenceResult.getRuleSetbyConclusion(wrongConclusion);
        this.newRule.getCornerstoneCase().setWrongRuleSet(this.wrongRuleSet);
    }

    /**
     * Get wrong conclusion 
     * @return 
     */
    @Override
    public Conclusion getWrongConclusion() {
        return this.wrongConclusion;
    }
    
    /**
     * Set wrong rule set
     * @param wrongRuleSet 
     */
    @Override
    public void setWrongRuleSet(RuleSet wrongRuleSet) {
        this.wrongRuleSet = wrongRuleSet;
    }

    /**
     * Get wrong rule set
     * @return 
     */
    @Override
    public RuleSet getWrongRuleSet() {
        return this.wrongRuleSet;
    }
    
    /**
     * Retrieve validation cornerstone cases
     * @param kaMode
     */
    @Override
    public void retrieveValidatingCaseSet(int kaMode) 
    {
        this.validatingCornerstoneCaseSet = new CornerstoneCaseSet();      
        
        if(kaMode == Learner.KA_EXCEPTION_MODE || kaMode == Learner.KA_STOPPING_MODE) 
        {
            //when refining or stopping rule...
            this.wrongRuleSet  = this.inferenceResult.getRuleSetbyConclusion(this.wrongConclusion);
            
            Set cases = this.wrongRuleSet.getBaseValidatingCornerstoneCaseSet().getBase().entrySet();
            Iterator iterator = cases.iterator();
            while (iterator.hasNext()) {
                Map.Entry me = (Map.Entry) iterator.next();
                CornerstoneCase aCornerstoneCase = (CornerstoneCase)me.getValue();

                if(this.newRule.isSatisfied(aCornerstoneCase)){
                    this.validatingCornerstoneCaseSet.addCornerstoneCase(newRule, aCornerstoneCase);
                }                
            }        
            
        } 
        else if (kaMode == Learner.KA_ALTER_MODE)
        {
            //when altering rule...
            this.wrongRuleSet  = this.inferenceResult.getRuleSetbyConclusion(this.wrongConclusion);
            
            Set cases = this.wrongRuleSet.getBaseValidatingCornerstoneCaseSetAlternative().getBase().entrySet();
            Iterator iterator = cases.iterator();
            while (iterator.hasNext()) {
                Map.Entry me = (Map.Entry) iterator.next();
                CornerstoneCase aCornerstoneCase = (CornerstoneCase)me.getValue();

                if(this.newRule.isSatisfied(aCornerstoneCase)){
                    this.validatingCornerstoneCaseSet.addCornerstoneCase(newRule, aCornerstoneCase);
                }                
            }       
            
        } 
        else if (kaMode == Learner.KA_NEW_MODE)
        {
            // When new rule...
            Set cases = Main.KB.getBaseValidatingCornerstoneCaseSet().getBase().entrySet();
            Iterator iterator = cases.iterator();
            while (iterator.hasNext()) {
                Map.Entry me = (Map.Entry) iterator.next();
                CornerstoneCase aCornerstoneCase = (CornerstoneCase)me.getValue();

                if(this.newRule.isSatisfied(aCornerstoneCase)){
                    this.validatingCornerstoneCaseSet.addCornerstoneCase(newRule, aCornerstoneCase);
                }                
            }        
        }
        //remove currentCase
        this.validatingCornerstoneCaseSet.deleteCornerstoneCase(this.newRule.getCornerstoneCase());
    }    
    
    @Override
    public boolean executeAddingRule(StringBuilder sb)
    {
    	boolean flag = false;
    	
        if (this.kaMode == Learner.KA_NEW_MODE)
        {
            flag = this.addNewRule(sb);
        } 
        else if (this.kaMode == Learner.KA_ALTER_MODE)
        {
        	flag = this.addAlternativeRule(sb);
        } 
        else if (this.kaMode == Learner.KA_EXCEPTION_MODE) 
        {
        	flag = this.addExceptionRule(sb);                
        } 
        else if (this.kaMode == Learner.KA_STOPPING_MODE) 
        {
        	flag = this.addStoppingRule(sb);                
        }
        
        //불필요함, 속도느려짐
        //if (flag) this.getRuleSet().setRootRuleTree();
        
        return flag;
    }
}
