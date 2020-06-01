/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.learner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.db.RDRDBManager;
import rdr.model.Value;
import rdr.rules.Conclusion;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;
import rdr.rules.RuleBuilder;
import rdr.rules.RuleLoader;
import rdr.rules.RuleSet;

/**
 * This class is used to define knowledge acquisition process
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class Learner implements ILearner{
    public static int KA_NEW_MODE = 0;
    public static int KA_ALTER_MODE = 1;
    public static int KA_EXCEPTION_MODE = 2;
    public static int KA_STOPPING_MODE = 3;
    
    public static final String[] kaNames = {"add", "alter", "edit", "delete"};
    
    public static int getKaModeByName(String strKaMode)
    {
    	int kaMode = -1;
    	strKaMode = strKaMode.toLowerCase();
    	for (int i = 0; i < kaNames.length; i++)
    	{
    		if (strKaMode.equals(kaNames[i]))
    		{
    			kaMode = i;
    			break;
    		}
    	}
    	return kaMode;
    }
    
    public static String getKaModeString(int kaMode)
    {
    	if (kaMode >= 0 && kaMode <= 3)
    	{
    		return kaNames[kaMode];
    	}
    	else return "";
    }
    
    /**
     * Rule set
     */
    protected RuleSet ruleSet = new RuleSet();
    
    /**
     * wrong conclusion 
     */
    protected Conclusion wrongConclusion;
    
    /**
     * New rule
     */
    protected Rule newRule;
    
    /**
     * KA mode
     */
    protected int kaMode;
    
    /**
     * Validating case set for new rule
     */
    protected CornerstoneCaseSet validatingCornerstoneCaseSet = new CornerstoneCaseSet();
    
    /**
     * Full difference list
     */
    protected HashMap<String, DiffElement> fullDifferenceList;
    
    /**
     * Partial difference list
     */
    protected HashMap<String, DiffElement> partialDifferenceList;
    /**
     * Constructor
     */
    public Learner() {
        this.ruleSet = null;
        this.wrongConclusion = null;
        this.newRule = new Rule();
    }

    /**
     * Constructor
     * @param ruleSet
     * @param aCase 
     * @param wrongConclusion 
     * @param rule 
     */
    public Learner(RuleSet ruleSet, Case aCase, 
            Conclusion wrongConclusion, Rule rule) {
        this.ruleSet = ruleSet;
        this.wrongConclusion = wrongConclusion;
        this.newRule = rule;
        this.newRule.setCornerstoneCase(new CornerstoneCase(aCase));
    }    
    
    /**
     * Set cornerstone case by case that is used for knowledge acquisition
     * @param aCase 
     */
    @Override
    public void setCornerstoneCaseByCase(Case aCase) {
        this.newRule.setCornerstoneCase(new CornerstoneCase(aCase));
    }
    
    /**
     * Set cornerstone case that is used for knowledge acquisition
     * @param aCornerstoneCase 
     */
    @Override
    public void setCornerstoneCase(CornerstoneCase aCornerstoneCase) {
        this.newRule.setCornerstoneCase(aCornerstoneCase);
    }
    
    /**
     * Get current case that is used for knowledge acquisition
     * @return 
     */
    @Override
    public CornerstoneCase getCornerstoneCase() {
        return this.newRule.getCornerstoneCase();
    }
    
    /**
     * Set new rule that will be acquired
     * @param rule 
     */
    @Override
    public void setNewRule(Rule rule) {
        this.newRule = rule;
    }
    
    /**
     * Get current new rule
     * @return 
     */
    @Override
    public Rule getNewRule() {
        return this.newRule;
    }
    
    /**
     * Set new rule type
     * @param kaMode
     */
    @Override
    public void setKaMode(int kaMode) {
        this.kaMode = kaMode;
    }
    
    /**
     * Get new rule type
     * @return i.e. new, alter, exception
     */
    @Override
    public int getKaMode() {
        return this.kaMode;
    }
    
    /**
     * Set new rule that will be acquired 
     * @param rule
     */
    @Override
    public void setInferenceResult(Rule rule) {
        throw new UnsupportedOperationException("Only supported in SCRDR."); 
    }
    
    /**
     * Set new rule that will be acquired
     * @param ruleSet 
     */
    @Override
    public void setInferenceResult(RuleSet ruleSet) {
        throw new UnsupportedOperationException("Only supported in MCRDR."); 
    }
    
    /**
     * Get current new rule
     * @return 
     */
    @Override
    public Object getInferenceResult() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    /**
     * Set new rule condition set
     * @param conditionSet 
     * 
     */
    @Override
    public void setConditionSetToNewRule(ConditionSet conditionSet) {
        this.newRule.setConditionSet(conditionSet);
    }

    /**
     * Add new condition
     * @param condition 
     * 
     * @return  
     */
    @Override
    public boolean addConditionToNewRule(Condition condition) {
        return this.newRule.getConditionSet().addCondition(condition);        
    }

    /**
     * Delete a condition
     * @param condition 
     * 
     * @return  
     */
    @Override
    public boolean deleteConditionFromNewRule(Condition condition) {
        return this.newRule.getConditionSet().deleteCondition(condition);
    }

    /**
     * Delete all conditions of new rule
     */
    @Override
    public void deleteAllConditionFromNewRule() {
        this.newRule.getConditionSet().deleteAllCondition();
    }    

    /**
     * Get the number of conditions in new rule
     * @return 
     */
    @Override
    public int getConditionAmountFromNewRule() {
        return this.newRule.getConditionSet().getConditionAmount();
    }

    /**
     * Get the number of conditions in new rule
     * @return 
     */
    @Override
    public ConditionSet getConditionSetFromNewRule() {
        return this.newRule.getConditionSet();
    }
    
    
    /**
     * Set new conclusion
     * @param conclusion 
     */
    @Override
    public void setConclusionToNewRule(Conclusion conclusion) {
        this.newRule.setConclusion(conclusion);
    }

    /**
     * Get new conclusion
     * @return 
     */
    @Override
    public Conclusion getConclusionFromNewRule() {
        return this.newRule.getConclusion();
    }

    /**
     * Add new rule
     * @return 
     */
    @Override
    public boolean addNewRule(StringBuilder sb) 
    {
        if (!this.ruleSet.getRuleById(0).isRuleChild(this.newRule))
        {        
        	boolean flag = true;
        	RDRDBManager.getInstance().setAutoCommit(false);
        	
            Rule addingRule = RuleBuilder.copyRule(this.newRule);
            addingRule.setRuleId(Main.KB.getNewRuleId());
            
            int conclusionId = addingRule.getConclusion().getConclusionId();
            
            if(this.ruleSet.isNewConclusion(addingRule.getConclusion()))
            {
                conclusionId = this.ruleSet.getConclusionSet().getNewConclusionId();
                flag &= RuleLoader.insertRuleConclusions(conclusionId, addingRule.getConclusion());
                addingRule.getConclusion().setConclusionId(conclusionId);
            }
            addingRule.setParent(this.ruleSet.getRuleById(0));
            this.ruleSet.getRuleById(0).addChildRule(addingRule);
            
            flag &= RuleLoader.insertRule(addingRule.getRuleId(), addingRule, conclusionId);
            
            if (flag) this.ruleSet.addRule(addingRule);
            
            RDRDBManager.getInstance().doCommit(flag);
            RDRDBManager.getInstance().setAutoCommit(true);
            
            if (flag == false) sb.append("add rule failed in database");
            return flag;
        } 
        else 
        {
        	sb.append("new rule is already child of root rule");
            return false;
        }
    }

    @Override
    public boolean addAlternativeRule(StringBuilder sb) {
        throw new UnsupportedOperationException("Only supported for MCRDR Learner."); 
    }
    
    @Override
    public boolean addExceptionRule(StringBuilder sb) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean addStoppingRule(StringBuilder sb) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    /**
     * Set rule set
     * @param ruleSet 
     */
    @Override
    public void setRuleSet(RuleSet ruleSet){
        this.ruleSet = ruleSet;
    }

    /**
     * Return rule set
     * @return 
     */
    @Override
    public RuleSet getRuleSet(){
        return this.ruleSet;
    }
    
    /**
     * Set wrong conclusion 
     * @param wrongConclusion
     */
    @Override
    public void setWrongConclusion(Conclusion wrongConclusion) {
        this.wrongConclusion = wrongConclusion;
    }
    
    /**
     * Delete wrong conclusion 
     */
    @Override
    public void deleteWrongConclusion() {
        this.wrongConclusion = null;
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
     * Returns true if wrong conclusion exists
     * @return 
     */
    @Override
    public boolean isWrongConclusionExist() {
        return this.wrongConclusion!=null;
    }    
    
    /**
     * Set wrong rule 
     * @param wrongRule
     */
    @Override
    public void setWrongRule(Rule wrongRule) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    /**
     * Get wrong rule 
     * @return 
     */
    @Override
    public Rule getWrongRule() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }    
    
    /**
     * Set wrong rule set
     * @param wrongRuleSet 
     */
    @Override
    public void setWrongRuleSet(RuleSet wrongRuleSet) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    /**
     * Get wrong rule set
     * @return 
     */
    @Override
    public RuleSet getWrongRuleSet() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    /**
     * Retrieve validation case set
     */
    @Override
    public void retrieveValidatingCaseSet(int kaMode) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }      
    
    /**
     * Get validation case set
     * @return 
     */
    @Override
    public CornerstoneCaseSet getValidatingCaseSet() {
        return this.validatingCornerstoneCaseSet;
    }      
    
    /**
     * Generate full difference list - differences between the current case and
     * all validation cases
     */
    @Override
    public void generateFullDifferenceList() {
        this.fullDifferenceList = new HashMap<>();
        
        //added by ucciri
        if (this.newRule.getCornerstoneCase() == null)
        	return;
        
        HashMap<String, Value> currentCaseValues = this.newRule.getCornerstoneCase().getValues();
        Set valueSet = currentCaseValues.entrySet();
        Iterator resultIterator = valueSet.iterator();

        while (resultIterator.hasNext()) {
            Map.Entry me = (Map.Entry) resultIterator.next();
            String attributeName = (String) me.getKey();
            Value currentCaseValue = (Value) me.getValue();

            Set valSet = this.validatingCornerstoneCaseSet.getBase().entrySet();
            Iterator resultIterator2 = valSet.iterator();
            boolean isPositiveDifferentAtrribute = true;
            boolean isNegativeDifferentAtrribute = true;

            HashMap<Integer, Value> validationCaseValue = new HashMap<>();

            Value previousValidationCaseValue = null;
            while (resultIterator2.hasNext()) {
                Map.Entry me2 = (Map.Entry) resultIterator2.next();
                Case valCase = (Case) me2.getValue();
                validationCaseValue.put(valCase.getCaseId(), (Value) valCase.getValue(attributeName));
                if (valCase.getValue(attributeName).equals(currentCaseValue)) {
                    isPositiveDifferentAtrribute = false;
                } else {
                    if (previousValidationCaseValue == null) {
                        previousValidationCaseValue = (Value) valCase.getValue(attributeName);
                    } else {
                        if (!valCase.getValue(attributeName).equals(previousValidationCaseValue)) {
                            isNegativeDifferentAtrribute = false;
                        }
                    }
                }
            }

            //Register 
            if (isPositiveDifferentAtrribute) {
                this.fullDifferenceList.put(attributeName,
                        new DiffElement(attributeName,
                                currentCaseValue, validationCaseValue, DiffElement.POSITIVE_FULL_DIFF));
            }
            
            //Register negative different attribute
            if (isNegativeDifferentAtrribute) {
                this.fullDifferenceList.put(attributeName,
                        new DiffElement(attributeName,
                                previousValidationCaseValue,
                                validationCaseValue, DiffElement.NEGATIVE_FULL_DIFF));
            }
        }

    }

    /**
     * Generate partial difference list - given the selected validation cases
     * the system finds attributes that have difference values between the 
     * current case and the subset of the validation cases
     * 
     * @param subsetOfValidatingCaseSet 
     */
    @Override
    public void generatePartialDifferenceList(HashMap<Integer, Case> subsetOfValidatingCaseSet) 
    {
       	this.partialDifferenceList = new HashMap<>();
       	
       	//added by ucciri
       	if (this.newRule.getCornerstoneCase() == null)
       		return;
                
        HashMap<String, Value> currentCaseValues = this.newRule.getCornerstoneCase().getValues();
        Set valueSet = currentCaseValues.entrySet();
        Iterator resultIterator = valueSet.iterator();

        while (resultIterator.hasNext()) {
            Map.Entry me = (Map.Entry) resultIterator.next();
            String attributeName = (String) me.getKey();
            Value currentCaseValue = (Value) me.getValue();

            Set valSet = subsetOfValidatingCaseSet.entrySet();
            Iterator resultIterator2 = valSet.iterator();
            boolean isPositiveDifferentAtrribute = true;
            boolean isNegativeDifferentAtrribute = true;

            HashMap<Integer, Value> validationCaseValue = new HashMap<>();

            Value previousValidationCaseValue = null;
            while (resultIterator2.hasNext()) {
                Map.Entry me2 = (Map.Entry) resultIterator.next();
                Case valCase = (Case) me2.getValue();
                validationCaseValue.put(valCase.getCaseId(),
                        valCase.getValue(attributeName));
                if (valCase.getValue(attributeName).equals(currentCaseValue)) {
                    isPositiveDifferentAtrribute = false;
                } else {
                    if (previousValidationCaseValue == null) {
                        previousValidationCaseValue = valCase.getValue(attributeName);
                    } else {
                        if (!valCase.getValue(attributeName).equals(previousValidationCaseValue)) {
                            isNegativeDifferentAtrribute = false;
                        }
                    }
                }
            }

            //Register 
            if (isPositiveDifferentAtrribute) {
                this.partialDifferenceList.put(attributeName,
                        new DiffElement(attributeName,
                                currentCaseValue, validationCaseValue, DiffElement.POSITIVE_FULL_DIFF));
            }
            
            //Register negative different attribute
            if (isNegativeDifferentAtrribute) {
                this.partialDifferenceList.put(attributeName,
                        new DiffElement(attributeName,
                                previousValidationCaseValue,
                                validationCaseValue, DiffElement.NEGATIVE_FULL_DIFF));
            }
        }
    }
    
    @Override
    public HashMap<String, DiffElement> getPartialDifferenceList(){
        return partialDifferenceList;
    }
    
    @Override
    public HashMap<String, DiffElement> getFullDifferenceList(){
        return fullDifferenceList;
    }
    
    @Override
    public boolean executeAddingRule(StringBuilder sb){
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    @Override
    public void executeAddingRuleForValidation(){
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
