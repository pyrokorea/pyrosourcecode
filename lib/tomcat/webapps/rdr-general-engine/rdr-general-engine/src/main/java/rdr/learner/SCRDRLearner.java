/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.learner;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseSet;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.db.RDRDBManager;
import rdr.logger.Logger;
import rdr.rules.Conclusion;
import rdr.rules.Rule;
import rdr.rules.RuleBuilder;
import rdr.rules.RuleLoader;
import rdr.rules.RuleSet;
import rdr.utils.RDRConfig;
import rdr.utils.StopWatch;

/**
 * This class is used to define SCRDR method knowledge acquisition process
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class SCRDRLearner extends Learner {
    
    protected Rule inferenceResult;
    
    /**
     * A rule set that suggest wrong conclusion
     */
    protected Rule wrongRule = new Rule(); 
    
    
    /**
     * Constructor
     */
    public SCRDRLearner() {
        super();
    }

    /**
     * Constructor
     * @param ruleSet
     * @param currentCase 
     * @param wrongConclusion
     * @param rule 
     */
    public SCRDRLearner(RuleSet ruleSet, Case currentCase, 
            Conclusion wrongConclusion, Rule rule) {
        super(ruleSet, currentCase, wrongConclusion, rule);
    }    
    
    /**
     * Set inference result
     * @param rule 
     */
    @Override
    public void setInferenceResult(Rule rule) {
        this.inferenceResult = rule;
    }
    
    /**
     * Get inference result
     * @return 
     */
    @Override
    public Rule getInferenceResult() {
        return this.inferenceResult;
    }
    
    /**
     * Add exception rule
     * @return 
     */
    @Override
    public boolean addExceptionRule(StringBuilder sb) 
    {
    	if (this.wrongRule == null)
    	{
    		sb.append("inference result for designated conclusion is not found");
    		return false;
    	}
    	
    	boolean flag = true;
    	RDRDBManager.getInstance().setAutoCommit(false);
    	
    	// get wrong rule base set
        Rule addingRule = RuleBuilder.copyRule(this.newRule);
        addingRule.setRuleId(Main.KB.getNewRuleId());

        Rule parentRule = this.wrongRule;

        // if this is new conclusion, add it into db
        int conclusionId = addingRule.getConclusion().getConclusionId();

        if(this.ruleSet.isNewConclusion(addingRule.getConclusion()))
        {
            conclusionId = this.ruleSet.getConclusionSet().getNewConclusionId();
            flag &= RuleLoader.insertRuleConclusions(conclusionId, addingRule.getConclusion());
            addingRule.getConclusion().setConclusionId(conclusionId);
            //StopWatch.lap("insert new conclusion end");
        }
        //set parent rule of the new rule as the parent rule (retrieved from the wrong rule set)
        addingRule.setParent(this.ruleSet.getRuleById(parentRule.getRuleId()));

        //set exception rule of the parent rule (retrieved from the wrong rule set) as the new rule.
        this.ruleSet.getRuleById(parentRule.getRuleId()).addChildRule(addingRule);          

        //System.out.println("parentRule=> " + parentRule);
        //System.out.println("addingRule=> " + addingRule);
        
        //StopWatch.lap("before insert rule");
        
        // insert new rule into db
        flag &= RuleLoader.insertRule(addingRule.getRuleId(), addingRule, addingRule.getConclusion().getConclusionId());    

        if (flag) this.ruleSet.addRule(addingRule);
        
        //this.ruleSet.setRootRuleTree();
        //StopWatch.lap("set root rule tree end");
        RDRDBManager.getInstance().doCommit(flag);
        RDRDBManager.getInstance().setAutoCommit(true);
        //StopWatch.lap("commit end");
        
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
    	if (this.wrongRule == null)
    	{
    		sb.append("inference result for designated conclusion is not found");
    		return false;
    	}
    	
    	boolean flag = true;
    	RDRDBManager.getInstance().setAutoCommit(false);
    	
    	// get wrong rule base set
        Rule addingRule = RuleBuilder.copyRule(this.newRule);
        addingRule.setRuleId(Main.KB.getNewRuleId());

        Rule parentRule = this.wrongRule;
        
        if(parentRule.getRuleId() != Rule.ROOT_RULE_ID)
        {
            //set parent rule of the new rule as the parent rule (retrieved from the wrong rule set)
            addingRule.setParent(this.ruleSet.getRuleById(parentRule.getRuleId()));

            //set exception rule of the parent rule (retrieved from the wrong rule set) as the new rule.
            this.ruleSet.getRuleById(parentRule.getRuleId()).addChildRule(addingRule);          

            // insert new rule into db
            flag &= RuleLoader.insertRule(addingRule.getRuleId(), addingRule, addingRule.getConclusion().getConclusionId());    

            if (flag) this.ruleSet.addRule(addingRule);
            
            //불필요함, 속도느려짐
            //this.ruleSet.setRootRuleTree();
        }
        
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
        this.wrongRule = null;
    }
    
    
    
    /**
     * Set wrong conclusion and set wrong rule
     * @param wrongConclusion
     */
    @Override
    public void setWrongConclusion(Conclusion wrongConclusion) {
    	this.wrongConclusion = wrongConclusion;        
        this.wrongRule = this.inferenceResult;
        RuleSet wrongRuleSet = new RuleSet();
        wrongRuleSet.addRule(this.wrongRule);
        this.newRule.getCornerstoneCase().setWrongRuleSet(wrongRuleSet);
    }
    
    /**
     * Set wrong rule
     * @param wrongRule
     */
    @Override
    public void setWrongRule(Rule wrongRule) {
        this.wrongRule = wrongRule;
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
     * Retrieve validation cornerstone cases
     */
    @Override
    public void retrieveValidatingCaseSet(int kaMode) 
    {
        this.validatingCornerstoneCaseSet = new CornerstoneCaseSet();      
        
        /** 엄격한 validation,
         *  newRule의 parent와 parent의 모든 child rule을 validation 대상으로 한다.
         *  newRule의 sibling인 rule의 cornerstone case는 SCRDR이므로 newRule에 만족하더라도 결론이 바뀌지 않지만
         *  rule tree 전체에 단 한개의 결론으로 관리하고자하는 경우 사용
         */
        if (RDRConfig.isStrictValidationSCRDR())
        {
	        if(kaMode == Learner.KA_EXCEPTION_MODE || kaMode == Learner.KA_STOPPING_MODE) 
	        {
	            //when refining or stopping rule...
	            this.wrongRule  = this.inferenceResult;
	            
	            //Logger.info("retrieveValidatingCaseSet, wrong rule : " + inferenceResult.getConclusion().toString());
	            
	            Set cases = this.wrongRule.getBaseValidatingCornerstoneCaseSet().getBase().entrySet();
	            Iterator iterator = cases.iterator();
	            while (iterator.hasNext()) {
	                Map.Entry me = (Map.Entry) iterator.next();
	                CornerstoneCase aCornerstoneCase = (CornerstoneCase)me.getValue();
	
	                if(this.newRule.isSatisfied(aCornerstoneCase))
	                {
	                	//Logger.info("retrieveValidatingCaseSet, V&V Target Case(satisfied to newRule) : case["+aCornerstoneCase.getCaseId()+"]");
	                   
	                    this.validatingCornerstoneCaseSet.addCornerstoneCase(newRule, aCornerstoneCase);
	                }                
	            }        
	            
	        } 
	        else if (kaMode == Learner.KA_ALTER_MODE)
	        {
	            //when altering rule...
	            this.wrongRule  = this.inferenceResult;
	            
	            Set cases = this.wrongRule.getBaseValidatingCornerstoneCaseSet().getBase().entrySet();
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
        }
        /** new rule의 parent만 validation 대상으로 함
         */
        else
        {
        	if(kaMode == Learner.KA_EXCEPTION_MODE || kaMode == Learner.KA_STOPPING_MODE) 
	        {
	            //when refining or stopping rule...
	            this.wrongRule  = this.inferenceResult;
	            
	            CornerstoneCase aCornerstoneCase = this.wrongRule.getCornerstoneCase();
	            
	            if (aCornerstoneCase != null)
	            {
	            	if (this.newRule.isSatisfied(aCornerstoneCase))
	            	{
	            		this.validatingCornerstoneCaseSet.addCornerstoneCase(newRule, aCornerstoneCase);
	            	}
	            }
	        } 
	        else if (kaMode == Learner.KA_NEW_MODE)
	        {
	           ; //parent는 root이므로 validation 대상없음  
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
