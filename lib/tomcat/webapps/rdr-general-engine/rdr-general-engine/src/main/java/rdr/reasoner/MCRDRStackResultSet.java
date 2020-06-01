/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.reasoner;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import rdr.rules.Rule;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class MCRDRStackResultSet {
    
    private LinkedHashMap<Integer, MCRDRStackResultInstance> MCRDRStackResult = new LinkedHashMap<>();
    private RulePathSet rulePathSet = new RulePathSet();
    private boolean isValidStackExist = false;
    
    
    /**
     * Get clone MCRDRStackResultSet
     * @return 
     */
    public MCRDRStackResultSet cloneMCRDRStackResultSet(){
        MCRDRStackResultSet newMCRDRStackResultSet = new MCRDRStackResultSet();
        newMCRDRStackResultSet.MCRDRStackResult = (LinkedHashMap<Integer, MCRDRStackResultInstance>) this.MCRDRStackResult.clone();
        
        return newMCRDRStackResultSet;
    }    
    
    /**
     * Get base set
     * @return 
     */
    public LinkedHashMap<Integer, MCRDRStackResultInstance> getBaseSet(){
        return this.MCRDRStackResult;
    }    
    
    /**
     * Get size of repository
     * @return 
     */
    public int getSize(){
        if(this.MCRDRStackResult.isEmpty()){
            return 0;
        } else {
            return this.MCRDRStackResult.size();
        }
        
    }    
    
    /**
     * clear stack set.
     */
    public void clearSet(){
        this.MCRDRStackResult.clear();
    }    
    
    /**
     * Set isValidStackExist.
     * @param isValidStackExist
     */
    public void setIsValidStackExist(boolean isValidStackExist){
        this.isValidStackExist = isValidStackExist;
    }    
    
    /**
     * Get isValidStackExist
     * @return 
     */
    public boolean getIsValidStackExist(){
        return this.isValidStackExist;
    }    
    
    /**
     * Add MCRDRStackResultInstance and returns true if success
     * @param aMCRDRStackResultInstance
     * @return 
     */
    public boolean addMCRDRStackResultInstance(MCRDRStackResultInstance aMCRDRStackResultInstance){
        if(!this.MCRDRStackResult.containsKey(aMCRDRStackResultInstance.getProcessingId())){
            this.MCRDRStackResult.put(aMCRDRStackResultInstance.getProcessingId(), aMCRDRStackResultInstance);
            if(this.isValidStackExist == false){
                if(aMCRDRStackResultInstance.getIsRuleFired()){
                    this.isValidStackExist = true;
                }
            }
            return true;
        } else {
            return false;
        }
    }   
    
    /**
     * Get MCRDRStackResultInstance by processing id (key of the repository)
     * @param processingId
     * @return 
     */
    public MCRDRStackResultInstance getMCRDRStackResultInstanceById(int processingId){
        if(this.MCRDRStackResult.containsKey(processingId)){
            return this.MCRDRStackResult.get(processingId);
        } else {
            return null;
        }
    }    
    
    /**
     * Get last MCRDRStackResultInstance 
     * @return 
     */
    public MCRDRStackResultInstance getLastMCRDRStackResultInstance(){
        Set inferenceResults = this.MCRDRStackResult.keySet();
        int stackSize = this.MCRDRStackResult.size();
        
        // Get a liat of iterator for backward iterating
        ListIterator<Integer> iterator = new ArrayList(inferenceResults).listIterator(stackSize);
        if(iterator.hasPrevious()){ 
            Integer key = iterator.previous();
            return this.MCRDRStackResult.get(key);
        } else {
            return null;
        }
    }    
    
    /**
     * Get Last Node of rulepaths in the set
     * @return 
     */
    public MCRDRStackResultSet getLastNodeOfRulePath(){
        MCRDRStackResultSet newMCRDRStackResultSet = new MCRDRStackResultSet();
        
        Rule[] lastNodes = this.rulePathSet.getLastNodesOfEachRulePath();
        
        Set stackResultSet = this.MCRDRStackResult.entrySet();
        Iterator iterator = stackResultSet.iterator();
        while (iterator.hasNext()) {
            Map.Entry me = (Map.Entry) iterator.next();    
            MCRDRStackResultInstance aMCRDRStackResultInstance = (MCRDRStackResultInstance)me.getValue();
            for (int i=0; i<lastNodes.length; i++) {
                if(aMCRDRStackResultInstance.getInferenceResult().getLastRule().getRuleId() == lastNodes[i].getRuleId()){
                    newMCRDRStackResultSet.addMCRDRStackResultInstance(aMCRDRStackResultInstance);
                }
            }
        }
            
        return newMCRDRStackResultSet;
    }    
    
    
    
    
    
}
