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
public interface IReasoner {
    public String getReasonerMethod();
    public Case getCurrentCase();
    public void setCurrentCase(Case currentCase);
    public RuleSet getRuleSet();
    public void setRuleSet(RuleSet ruleSet);
    public Rule getStartingRule();
    public void setStartingRule(Rule aRule);
    public void clearStartingRule();
    public Object inference(Rule rule);
    public Object inferenceWithStartingRule(Rule rule);
    public boolean addRuleToFiredRules(Rule rule);
    public void setFiredRules(RuleSet firedRules);
    public RuleSet getFiredRules();
    public void clearFiredRules();
    public boolean addRuleToInferenceResult(Rule rule);
    public boolean deleteRuleFromInferenceResult(Rule rule);
    public void clearInferenceResult();
    public void setInferenceResult(Rule inferenceResult);
    public void setInferenceResult(RuleSet inferenceResult);
    public Object getInferenceResult();
}
