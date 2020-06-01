/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.learner;

import java.util.HashMap;
import rdr.cases.Case;
import rdr.cases.CaseSet;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.rules.Conclusion;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;
import rdr.rules.RuleSet;

/**
 * This interface is for knowledge acquisition process
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public interface ILearner {
    public void setCornerstoneCaseByCase(Case aCase);
    public void setCornerstoneCase(CornerstoneCase aCase);
    public CornerstoneCase getCornerstoneCase();
    public void setRuleSet(RuleSet ruleSet);
    public RuleSet getRuleSet();
    public HashMap<String, DiffElement> getFullDifferenceList();
    public HashMap<String, DiffElement> getPartialDifferenceList();
    public void setInferenceResult(Rule rule);
    public void setInferenceResult(RuleSet ruleSet);
    public Object getInferenceResult();
    public void setWrongConclusion(Conclusion wrongConclusion);
    public void deleteWrongConclusion();
    public Conclusion getWrongConclusion();
    public boolean isWrongConclusionExist();
    public void setWrongRule(Rule wrongRule);
    public Rule getWrongRule();
    public void setWrongRuleSet(RuleSet wrongRuleSet);
    public RuleSet getWrongRuleSet();    
    public void setNewRule(Rule rule);
    public Rule getNewRule();
    public boolean addConditionToNewRule(Condition condition);
    public void setConditionSetToNewRule(ConditionSet conditionSet);
    public boolean deleteConditionFromNewRule(Condition condition);
    public void deleteAllConditionFromNewRule();
    public ConditionSet getConditionSetFromNewRule();
    public int getConditionAmountFromNewRule();
    public void setConclusionToNewRule(Conclusion conclusion);
    public Conclusion getConclusionFromNewRule();
    public void setKaMode(int kaMode);
    public int getKaMode();
    public boolean addNewRule(StringBuilder sb);
    public boolean addAlternativeRule(StringBuilder sb);
    public boolean addExceptionRule(StringBuilder sb);
    public boolean addStoppingRule(StringBuilder sb);
    public void retrieveValidatingCaseSet(int kaMode);
    public CornerstoneCaseSet getValidatingCaseSet();
    public void generateFullDifferenceList();
    public void generatePartialDifferenceList(HashMap<Integer, Case> subsetOfValidatingCaseSet);    
    public boolean executeAddingRule(StringBuilder sb);
    public void executeAddingRuleForValidation();
}
