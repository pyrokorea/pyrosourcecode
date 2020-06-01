/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.reasoner;

import rdr.rules.RuleSet;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class MCRDRStackResultInstance {
    
    private int processingId;
    private int caseId;
    private RuleSet inferenceResult;
    private boolean isRuleFired = false;
    
    
    public void setProcessingId(int processingId){
        this.processingId = processingId;
    }
    
    public int getProcessingId(){
        return this.processingId;
    }
    
    public void setCaseId(int caseId){
        this.caseId = caseId;
    }
    
    public int getCaseId(){
        return this.caseId;
    }
    
    public void setInferenceResult(RuleSet inferenceResult){
        this.inferenceResult = inferenceResult;
    }
    
    public RuleSet getInferenceResult(){
        return this.inferenceResult;
    }
    
    public void setIsRuleFired(boolean isRuleFired){
        this.isRuleFired = isRuleFired;
    }
    
    public boolean getIsRuleFired(){
        return this.isRuleFired;
    }
    
}
