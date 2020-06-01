/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.workbench;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import rdr.apps.Main;
import rdr.rules.RuleSet;
import rdr.utils.RDRConfig;
import rdr.cases.Case;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.learner.ILearner;
import rdr.learner.Learner;
import rdr.learner.LearnerFactory;
import rdr.logger.Logger;
import rdr.rules.Rule;
import rdr.reasoner.IReasoner;
import rdr.reasoner.MCRDRStackResultInstance;
import rdr.reasoner.MCRDRStackResultSet;
import rdr.reasoner.ReasonerFactory;
import rdr.rules.Conclusion;
import rdr.rules.ConclusionSet;
import rdr.rules.Condition;

/**
 * This class is used to maintain resources for inference and knowledge
 * acquisition
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class Workbench {

    /**
     * Reasoner
     */
    protected IReasoner reasoner;
    
    /**
     * Learner
     */
    protected ILearner learner;
    
    /**
     * Fired Rules
     */
    protected RuleSet firedRules = new RuleSet();
    
    /**
     * KA mode
     */
    protected int kaMode;
    
    /**
     * initial infrenced result for ka or inference
     */
    private Object initialInferenceResult;
    
    /**
     * infrenced result
     */
    private Object inferenceResult;
    
    /**
     * stacked infrenced result
     */
    private MCRDRStackResultSet stackedInferenceResult = new MCRDRStackResultSet();
    
    
    /**
     * stacked infrenced result for temporary
     */
    private MCRDRStackResultSet tmpStackedInferenceResult = new MCRDRStackResultSet();
    
    
    /**
     * Default Constructor.
     */
    public Workbench() {
        this.inferenceResult = null;
        this.reasoner = (IReasoner) ReasonerFactory.createReasoner("SCRDR");
        this.learner = (ILearner) LearnerFactory.createLearner("SCRDR");
        this.initialInferenceResult = null;
        this.stackedInferenceResult = new MCRDRStackResultSet();
    }

    /**
     * Constructor inference engine
     *
     * @param method
     */
    public Workbench(String method) {
        this.reasoner = (IReasoner) ReasonerFactory.createReasoner(method);
        this.learner = (ILearner) LearnerFactory.createLearner(method);
        switch (method) {
            case "SCRDR":
                this.inferenceResult = null;
                this.initialInferenceResult = null;
                this.stackedInferenceResult = new MCRDRStackResultSet();
                break;
            case "MCRDR":
                this.inferenceResult = new RuleSet();
                this.initialInferenceResult = null;
                this.stackedInferenceResult = new MCRDRStackResultSet();
                break;
        }
    }
    

    /**
     * Set reasoner
     * @param method 
     */
    public void setReasoner(String method) {
        this.reasoner = (IReasoner) ReasonerFactory.createReasoner(method);
    }
        
    /**
     * Get reasoner
     * @return 
     */
    public IReasoner getReasoner() {
        return this.reasoner;
    }
        
    /**
     * Get reasoner type i.e. SCRDR, MCRDR
     * @return 
     */
    public String getReasonerType() {
        return this.reasoner.getReasonerMethod();
    }

    /**
     * Set learner
     * @param method 
     */
    public void setLearner(String method) {
        this.learner = (ILearner) LearnerFactory.createLearner(method);
    }
        
    /**
     * Get learner
     * @return 
     */
    public ILearner getLearner() {
        return this.learner;
    }
    
    /**
     * initialise learner.
     * 
     */
    public void initLearner(Case currentCase){
        
    }
    
    /**
     * initialise reasoner.
     * 
     */
    public void initReasoner(Case currentCase){
        
    }
    
    /**
     * Set case for inference
     * @param currentCase 
     */
    public void setCaseForInference(Case currentCase) {
        this.reasoner.setCurrentCase(currentCase);
    }
        
    /**
     * Get current case in the work bench
     * @return 
     */
    public Case getCurrentCase() {
        return this.reasoner.getCurrentCase();
    }

    /**
     * Set current cornerstone case
     * @param currentCornerstoneCase 
     */
    public void setCurrentCornerstoneCase(CornerstoneCase currentCornerstoneCase) {
        this.learner.setCornerstoneCase(currentCornerstoneCase);
    }

    /**
     * Set validating case
     * @param validatingCase 
     */
    public void setValidatingCase(Case validatingCase) {
        this.reasoner.setCurrentCase(validatingCase);
    }
        
    /**
     * Get validating case in the work bench
     * @return 
     */
    public Case getValidatingCase() {
        return this.reasoner.getCurrentCase();
    }
    
    /**
     * Set rule in this work bench
     * @param ruleSet 
     */
    public void setRuleSet(RuleSet ruleSet) {
        this.reasoner.setRuleSet(ruleSet);
        this.learner.setRuleSet(ruleSet);
        this.initialInferenceResult = ruleSet.getRootRule();
        MCRDRStackResultInstance aMCRDRStackResultInstance = new MCRDRStackResultInstance();
        aMCRDRStackResultInstance.setCaseId(0);
        aMCRDRStackResultInstance.setProcessingId(0);
        aMCRDRStackResultInstance.setInferenceResult(new RuleSet(this.reasoner.getRuleSet().getRootRule()));
        this.stackedInferenceResult.addMCRDRStackResultInstance(aMCRDRStackResultInstance);
    }
    
    /**
     * Get ruleset in this work bench
     * @return 
     */
    public RuleSet getRuleSet() {
        return this.reasoner.getRuleSet();
    }
    
    /**
     * Inference for validation (inference cornerstonecases and see new rule is satisfied)
     */
    public void inferenceForValidation(){
        //get cornerstone cases
        this.reasoner.clearFiredRules();
        Rule rootRule = this.reasoner.getRuleSet().getRootRule();
        this.reasoner.clearStartingRule();
        
//        RuleSet addingInferenceResultForStack = new RuleSet();
        switch (this.reasoner.getReasonerMethod()) {
            case "SCRDR":
                this.inferenceResult=null;
                this.reasoner.inference(rootRule);
                this.inferenceResult = ((Rule) this.reasoner.getInferenceResult());
//                this.learner.setInferenceResult((Rule) this.reasoner.getInferenceResult());
//                Rule tmpInferenceRule = (Rule) this.inferenceResult;
//                if(tmpInferenceRule.getConclusion()!=null){
//                    this.learner.getCornerstoneCase().addConclusion(tmpInferenceRule.getConclusion());
//                }
                
//                addingInferenceResultForStack.addRule(tmpInferenceRule);
                
                break;
            case "MCRDR":
                this.reasoner.clearInferenceResult();
                this.reasoner.inference(rootRule);
                this.inferenceResult = ((RuleSet) this.reasoner.getInferenceResult());
                this.learner.setInferenceResult((RuleSet) this.reasoner.getInferenceResult());
                RuleSet tmpInferenceRuleSet = (RuleSet) this.inferenceResult;
                RuleSet finalInferenceRuleSet = new RuleSet();
                
//                addingInferenceResultForStack = tmpInferenceRuleSet;
                if(this.kaMode==Learner.KA_STOPPING_MODE){  
                    // getting wrong ruleset and compare with inference result
                    // getting wrong ruleset
                    RuleSet wrongRuleSet = this.learner.getWrongRuleSet();
                    Set rules = wrongRuleSet.getBase().entrySet();
                    Iterator iterator = rules.iterator();
                    while (iterator.hasNext()) {
                        Map.Entry me = (Map.Entry) iterator.next();
                        Rule wrongRule = (Rule)me.getValue();
                        
                        // getting inference result
                        Set inferenceResults = tmpInferenceRuleSet.getBase().entrySet();
                        Iterator iterator2 = inferenceResults.iterator();
                        while (iterator2.hasNext()) {
                            Map.Entry me2 = (Map.Entry) iterator2.next();
                            Rule inferenceResultRule = (Rule)me2.getValue();
                            finalInferenceRuleSet.addRule(inferenceResultRule);
                            
                            //comparing wrong rule with inference result
                            if(wrongRule.equals(inferenceResultRule)){
                                //if wrong rule and inference result is equal delete wrong rule and add new rule
                                if(this.learner.getNewRule().isSatisfied(this.learner.getCornerstoneCase())){
                                    finalInferenceRuleSet.deleteRule(wrongRule);
                                    finalInferenceRuleSet.addRule(this.learner.getNewRule());
                                }
                            }                                    
                        }                        
                    }
                    // if there exists inference results, set conclusion set to validating case
                    if(!finalInferenceRuleSet.isEmpty()){
                        this.learner.getCornerstoneCase().setConclusionSet(finalInferenceRuleSet.getConclusionSet());
                    }
                } else if (this.kaMode == Learner.KA_EXCEPTION_MODE) {
                    
                    // if there exists inference results,
                    if(!tmpInferenceRuleSet.isEmpty()){
                        
                        // getting wrong ruleset and compare with inference result
                        // getting wrong ruleset
                        RuleSet wrongRuleSet = this.learner.getWrongRuleSet();
                        Set rules = wrongRuleSet.getBase().entrySet();
                        Iterator iterator = rules.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry me = (Map.Entry) iterator.next();
                            Rule wrongRule = (Rule)me.getValue();

                            // getting inference result
                            Set inferenceResults = tmpInferenceRuleSet.getBase().entrySet();
                            Iterator iterator2 = inferenceResults.iterator();
                            while (iterator2.hasNext()) {
                                Map.Entry me2 = (Map.Entry) iterator2.next();
                                Rule inferenceResultRule = (Rule)me2.getValue();
                                finalInferenceRuleSet.addRule(inferenceResultRule);
                                
                                //comparing wrong rule with inference result
                                if(wrongRule.equals(inferenceResultRule)){
                                    //if wrong rule and inference result is equal delete wrong rule and add new rule
                                    if(this.learner.getNewRule().isSatisfied(this.learner.getCornerstoneCase())){
                                        finalInferenceRuleSet.deleteRule(wrongRule);
                                        finalInferenceRuleSet.addRule(this.learner.getNewRule());
                                    }
                                }                                    
                            }                        
                        }
                        // set conclusion set to validating case
                        this.learner.getCornerstoneCase().setConclusionSet(finalInferenceRuleSet.getConclusionSet());
                    }
                    
                } else {
                    
                }                
                break;
        }
        this.firedRules = this.reasoner.getFiredRules();
        this.firedRules.setRootRuleTree();
//        this.initialInferenceResult = this.inferenceResult;
//        this.addStackedInferenceResult(this.validatingCase.getCaseId(), addingInferenceResultForStack);
    }
    
    /**
     * Inference
     */
    public void inference(){
        this.reasoner.clearFiredRules();
        Rule aRule = this.reasoner.getRuleSet().getRootRule();
        this.reasoner.clearStartingRule();
        RuleSet addingInferenceResultForStack = new RuleSet();
        switch (this.reasoner.getReasonerMethod()) {
            case "SCRDR":
                this.inferenceResult=null;
                this.reasoner.inference(aRule);
                this.inferenceResult = ((Rule) this.reasoner.getInferenceResult());
                this.learner.setInferenceResult((Rule) this.reasoner.getInferenceResult());
                
                if (RDRConfig.isDebugInference())
                {
	                if (this.inferenceResult != null)
	                {
	                	Rule irRule = (Rule)this.inferenceResult;
	                	String conclusionStr = "";
	                	if (irRule.getConclusion() != null)
	                		conclusionStr = irRule.getConclusion().getConclusionId() +
	                			             ", " + irRule.getConclusion().getConclusionName();
	                	
	                	Logger.info("Inference : ruleId[" + irRule.getRuleId() +
	                			    "] conclusion[" + conclusionStr + "]");
	                }
                }
                
                // set the current inference result 
                Rule tmpInferenceRule = (Rule) this.inferenceResult;
                if(tmpInferenceRule.getConclusion()!=null){
                    this.reasoner.getCurrentCase().addConclusion(tmpInferenceRule.getConclusion());
                }
                
                addingInferenceResultForStack.addRule(tmpInferenceRule);
                
                break;
            case "MCRDR":
                this.reasoner.clearInferenceResult();
                this.reasoner.inference(aRule);
                this.inferenceResult = ((RuleSet) this.reasoner.getInferenceResult());
                this.learner.setInferenceResult((RuleSet) this.reasoner.getInferenceResult());
                
                if (RDRConfig.isDebugInference())
                {
	                LinkedHashMap<Integer, Rule> tInfResults = ((RuleSet)this.inferenceResult).getBase();
	                Iterator<Integer> irItr = tInfResults.keySet().iterator();
	                while (irItr.hasNext())
	                {
	                	Integer key = irItr.next();
	                	Rule irRule = tInfResults.get(key);
	                	
	                	String conclusionStr = "";
	                	if (irRule.getConclusion() != null)
	                		conclusionStr = irRule.getConclusion().getConclusionId() +
	                			             ", " + irRule.getConclusion().getConclusionName();
	                	
	                	Logger.info("Inference : ruleId[" + irRule.getRuleId() +
	                			    "] conclusion[" + conclusionStr + "]");
	                }
                }
                
                // set the current inference result to
                RuleSet tmpInferenceRuleSet = (RuleSet) this.inferenceResult;
                if(!tmpInferenceRuleSet.isEmpty()){
                    this.reasoner.getCurrentCase().setConclusionSet(tmpInferenceRuleSet.getConclusionSet());
                }
                addingInferenceResultForStack = tmpInferenceRuleSet;
                
                break;
        }
            
        this.firedRules = this.reasoner.getFiredRules();
        this.firedRules.setRootRuleTree();
        this.initialInferenceResult = this.inferenceResult;
        this.addStackedInferenceResult(this.reasoner.getCurrentCase().getCaseId(), addingInferenceResultForStack);
        
        //20170912 added by ucciri
        if (RDRConfig.isDebugInference())
        {
	        LinkedHashMap<Integer, Rule> tFiredRules = this.firedRules.getBase();
	        Iterator<Integer> keyItr = tFiredRules.keySet().iterator();
	        while (keyItr.hasNext())
	        {
	        	Integer key = keyItr.next();
	        	Rule fRule = tFiredRules.get(key);
	        	
	        	int parentId = -1;
	        	Rule pRule = fRule.getParent();
	        	if ( pRule != null) parentId = pRule.getRuleId();
	        	
	        	Logger.info("fired rule : parent[" + parentId + "] " + fRule.toString());
	        }
        }
    }
    
    /**
     * Inference with rule
     * @param aRule
     */
    public void inference(Rule aRule){        
        this.reasoner.clearFiredRules();
        this.reasoner.clearStartingRule();
        
        RuleSet addingInferenceResultForStack = new RuleSet();
        
        switch (this.reasoner.getReasonerMethod()) {
            case "SCRDR":
                this.inferenceResult=null;
                this.reasoner.inference(aRule);
                this.inferenceResult = ((Rule) this.reasoner.getInferenceResult());
                this.learner.setInferenceResult((Rule) this.reasoner.getInferenceResult());
                Rule tmpInferenceRule = (Rule) this.inferenceResult;
                if(tmpInferenceRule.getConclusion()!=null){
                    this.reasoner.getCurrentCase().addConclusion(tmpInferenceRule.getConclusion());
                }
                addingInferenceResultForStack.addRule(tmpInferenceRule);
                break;
            case "MCRDR":
                this.reasoner.clearInferenceResult();
                this.reasoner.inference(aRule);
                this.inferenceResult = ((RuleSet) this.reasoner.getInferenceResult());
                this.learner.setInferenceResult((RuleSet) this.reasoner.getInferenceResult());
                RuleSet tmpInferenceRuleSet = (RuleSet) this.inferenceResult;
                if(!tmpInferenceRuleSet.isEmpty()){
                    this.reasoner.getCurrentCase().setConclusionSet(tmpInferenceRuleSet.getConclusionSet());
                }
                addingInferenceResultForStack = tmpInferenceRuleSet;
                break;
        }
            
        this.firedRules = this.reasoner.getFiredRules();
        this.firedRules.setRootRuleTree();
        this.initialInferenceResult = this.inferenceResult;
        this.addStackedInferenceResult(this.reasoner.getCurrentCase().getCaseId(), addingInferenceResultForStack);
    }
    
    /**
     * Inference with initialResult
     */
    public void inferenceWithInitialResult(){    
//        this.tmpStackedInferenceResult = this.stackedInferenceResult.cloneMCRDRStackResultSet();
        
        RuleSet addingInferenceResultForStack = new RuleSet();
                
        Set inferenceResults = this.stackedInferenceResult.getBaseSet().keySet();
        int stackSize = this.stackedInferenceResult.getBaseSet().size();
        
        // Get a liat of iterator for backward iterating
        ListIterator<Integer> iterator = new ArrayList(inferenceResults).listIterator(stackSize);
        while (iterator.hasPrevious()){ 
            Integer key = iterator.previous();
            
            RuleSet applyingInitialInferenceResult = this.stackedInferenceResult.getMCRDRStackResultInstanceById(key).getInferenceResult();
            // skip root rule stack
            if(applyingInitialInferenceResult.getLastRule().getRuleId()!=Rule.ROOT_RULE_ID || key == Rule.ROOT_RULE_ID){
                switch (this.reasoner.getReasonerMethod()) {
                    case "SCRDR":           
                        // set target rule
                        Rule startingRule = this.reasoner.getRuleSet().getRule((Rule) applyingInitialInferenceResult.getLastRule());
                        this.reasoner.clearInferenceResult();
                        if(startingRule.getRuleId()!=Rule.ROOT_RULE_ID){
                            this.reasoner.setStartingRule(startingRule);
                        } else {
                            this.reasoner.clearStartingRule();
                        }
                        this.reasoner.inferenceWithStartingRule(startingRule);
                        Rule tmpInferenceResultRule = (Rule) this.reasoner.getInferenceResult();

                        if(tmpInferenceResultRule.isRootRule(this.learner.getNewRule())){
                            this.reasoner.inferenceWithStartingRule(this.reasoner.getRuleSet().getRootRule());
                            tmpInferenceResultRule = (Rule) this.reasoner.getInferenceResult();
                        }
                        this.inferenceResult = tmpInferenceResultRule;
                        this.reasoner.setInferenceResult((Rule) this.inferenceResult);
                        this.learner.setInferenceResult(tmpInferenceResultRule);

                        addingInferenceResultForStack.addRule(tmpInferenceResultRule);
                        break;
                    case "MCRDR":    
                        RuleSet initInferResult = (RuleSet) applyingInitialInferenceResult;
                        this.reasoner.clearInferenceResult();
                        // set target rule
                        startingRule = this.reasoner.getRuleSet().getRule(initInferResult.getLastRule());
                        if(startingRule.getRuleId()==Rule.ROOT_RULE_ID){
                            this.reasoner.clearStartingRule();
                        } else {
                            this.reasoner.setStartingRule(startingRule);
                        }
                        this.reasoner.inferenceWithStartingRule(this.reasoner.getRuleSet().getRule(startingRule));
                        RuleSet tmpInferenceResultRuleSet = (RuleSet) this.reasoner.getInferenceResult();
                        
                        this.inferenceResult = tmpInferenceResultRuleSet;
                        this.reasoner.setInferenceResult((RuleSet) this.inferenceResult);
                        this.learner.setInferenceResult((RuleSet) this.reasoner.getInferenceResult());

                        addingInferenceResultForStack = tmpInferenceResultRuleSet;

                        break;
                }
                if(addingInferenceResultForStack.isEmpty()){
    //                inferenceResultsIterator.remove();
                } else {
                    if(addingInferenceResultForStack.getLastRule().getRuleId()==Rule.ROOT_RULE_ID){
    //                    inferenceResultsIterator.remove();
                    } else {
                        this.tmpStackedInferenceResult.clearSet();
                        break;
                    }
                }
            }
        }
        
        this.firedRules = this.reasoner.getFiredRules();
        this.firedRules.setRootRuleTree();
        this.addStackedInferenceResult(this.reasoner.getCurrentCase().getCaseId(), addingInferenceResultForStack);
    }
    
    /**
     * Set rule in this work bench
     * @param rootRule 
     */
    public void setRootRule(Rule rootRule) {        
        this.reasoner.getRuleSet().setRootRule(rootRule);
        this.firedRules.setRootRule(rootRule);
    }
    
    /**
     * Set initial inferenced results
     * @param inferenceResult 
     */
    public void setInitialInferenceResult(Rule inferenceResult) {        
        this.initialInferenceResult = inferenceResult;
    }
    
    
    /**
     * Set initial inferenced results
     * @param inferenceResult
     */
    public void setInitialInferenceResult(RuleSet inferenceResult) {        
        this.initialInferenceResult = inferenceResult;
    }
    
    /**
     * Get initial inferenced results
     * @return 
     */
    public Object getInitialInferenceResult() {        
        return this.initialInferenceResult;
    }
    
    
    /**
     * Set inferenced results
     * @param inferenceResult
     */
    public void setInferenceResult(Rule inferenceResult) {        
        this.inferenceResult = inferenceResult;
        this.reasoner.setInferenceResult(inferenceResult);
        this.learner.setInferenceResult(inferenceResult);
    }
    
    
    /**
     * Set inferenced results
     * @param inferenceResult
     */
    public void setInferenceResult(RuleSet inferenceResult) {        
        this.inferenceResult = inferenceResult;
        this.reasoner.setInferenceResult(inferenceResult);
        this.learner.setInferenceResult(inferenceResult);
    }
    
    
    /**
     * Get inferenced results
     * @return 
     */
    public Object getInferenceResult() {
        switch (this.reasoner.getReasonerMethod()) {
            case "SCRDR":
                return (Rule) this.inferenceResult;                
            case "MCRDR":
                return (RuleSet) this.inferenceResult;
        }
        throw new UnsupportedOperationException("No inference result."); 
    }
    
    /**
     * Get stacked inference result 
     * @param caseId
     * @param inferenceResultInstance
     */
    public void addStackedInferenceResult(int caseId, RuleSet inferenceResultInstance) {
        MCRDRStackResultInstance aMCRDRStackResultInstance = new MCRDRStackResultInstance();
        aMCRDRStackResultInstance.setCaseId(caseId);
        aMCRDRStackResultInstance.setProcessingId(this.stackedInferenceResult.getSize());
        aMCRDRStackResultInstance.setInferenceResult(inferenceResultInstance);
        if(inferenceResultInstance.getLastRule().getRuleId()!=Rule.ROOT_RULE_ID){
            aMCRDRStackResultInstance.setIsRuleFired(true);
        }
        
        this.stackedInferenceResult.addMCRDRStackResultInstance(aMCRDRStackResultInstance);
    }
    
    /**
     * Get stacked inference result 
     * @return 
     */
    public MCRDRStackResultSet getStackedInferenceResult() {
        return this.stackedInferenceResult;
    }
    
    /**
     * Get stacked inference result 
     * @return 
     */
    public MCRDRStackResultInstance getLastMCRDRStackResultInstance() {
        return this.stackedInferenceResult.getLastMCRDRStackResultInstance();
    }
    
    
    /**
     * Clear stacked inference result.      
     */
    public void clearStackedInferenceResult() {
        this.stackedInferenceResult = new MCRDRStackResultSet();
        
        MCRDRStackResultInstance aMCRDRStackResultInstance = new MCRDRStackResultInstance();
        aMCRDRStackResultInstance.setCaseId(0);
        aMCRDRStackResultInstance.setProcessingId(0);
        aMCRDRStackResultInstance.setInferenceResult(new RuleSet(this.reasoner.getRuleSet().getRootRule()));
        this.stackedInferenceResult.addMCRDRStackResultInstance(aMCRDRStackResultInstance);
        
    }
    
    /**
     * Get previous MCRDRStackResultInstance
     * @param stackKey
     * @return 
     */
    public MCRDRStackResultInstance getPreviousStackedResultInstance(int stackKey) {
        int curId = stackKey;
        curId--;
        while(!this.stackedInferenceResult.getMCRDRStackResultInstanceById(curId).getIsRuleFired()){            
            curId--;
        }
        return this.stackedInferenceResult.getMCRDRStackResultInstanceById(curId);
    }
    
    /**
     * Get previous stacked inference result key id
     * @param stackKey
     * @return 
     */
    public int getPreviousStackedInferenceResultKeyId(int stackKey) {
        int curId = stackKey;
        curId--;
        while(!this.stackedInferenceResult.getMCRDRStackResultInstanceById(curId).getIsRuleFired()){             
            curId--;
        }
        return curId;
    }
    
    /**
     * Get previous stacked inference result 
     * @param stackKey
     * @return 
     */
    public RuleSet getPreviousStackedInferenceResult(int stackKey) {
        int curId = stackKey;
        
        while(!this.stackedInferenceResult.getMCRDRStackResultInstanceById(curId).getIsRuleFired()){             
            curId--;
            if(curId==0){
                return new RuleSet(Main.KB.getRootRule());
            }
        }
        return this.stackedInferenceResult.getMCRDRStackResultInstanceById(curId).getInferenceResult(); 
    }
    
    
    /**
     * Get fired rules
     * @return 
     */
    public RuleSet getFiredRules() {
        return this.firedRules;
    }

    /**
     * Delete wrong conclusion for the knowledge acquisition
     */
    public void deleteWrongConclusion() {
        this.learner.deleteWrongConclusion();
    }

    /**
     * Set wrong conclusion for the knowledge acquisition
     * @param conclusion 
     */
    public void setWrongConclusion(Conclusion conclusion) {
        this.learner.setWrongConclusion(conclusion);
    }
        
    /**
     * Get wrong conclusion for the knowledge acquisition
     * @return 
     */
    public Conclusion getWrongConclusion() {
        return this.learner.getWrongConclusion();
    }

    
    /**
     * Set new rule conclusion for the knowledge acquisition
     * @param conclusion 
     *
     */
    public void setNewRuleConclusion(Conclusion conclusion) {
        this.learner.getNewRule().setConclusion(conclusion);
    }

    /**
     * Add new condition to new Rule
     * @param condition 
     * 
     * @return  
     */
    public boolean addConditionToNewRule(Condition condition) {
        if(this.learner.getNewRule().getConditionSet().addCondition(condition)){
            this.learner.getNewRule().setConditionSet(this.learner.getNewRule().getConditionSet());
            return true;
        }        
        return false;
    }

    
    /**
     * Set KA mode the knowledge acquisition
     * @param kaMode 
     *
     */
    public void setKaMode(int kaMode) {
        this.kaMode = kaMode;
        this.learner.setKaMode(kaMode);
    }
    
    
    /**
     * Get KA mode for the knowledge acquisition
     * @return 
     * 
     */
    public int getKaMode() {
        return this.learner.getKaMode();
    }

    
    /**
     * Set new rule for the knowledge acquisition
     * @param rule 
     *
     */
    public void setNewRule(Rule rule) {
        this.learner.setNewRule(rule);
    }
    
    
    /**
     * Get rule for the knowledge acquisition
     * @return 
     * 
     */
    public Rule getNewRule() {
        return this.learner.getNewRule();
    }
    
    
    /**
     * Returns true if new rule is ready for the knowledge acquisition
     * @return Returns true if new rule is ready for the knowledge acquisition
     * 
     */
    public boolean isNewRuleReady() {
        if(this.learner.getNewRule().getConclusion() != null){
            return this.learner.getNewRule().getConditionSet().getConditionAmount()>0;
        } else {
            return false;
        }
    }

    /**
     * Execute adding rule.
     */
    public boolean executeAddingRule(StringBuilder sb)
    {
        this.learner.setRuleSet(Main.KB);
        boolean flag = this.learner.executeAddingRule(sb);
        
        if (flag) this.reasoner.setRuleSet(this.learner.getRuleSet());
        
        return flag;
    }
    
}
