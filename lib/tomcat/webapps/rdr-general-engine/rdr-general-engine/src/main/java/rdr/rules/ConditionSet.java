/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import rdr.cases.Case;
import rdr.logger.Logger;
import rdr.model.Value;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class ConditionSet {
    
    /**
     * condition set <condition> 
     */
    private HashSet<Condition> conditionSet = new HashSet<>();
    
    /**
     * Constructor
     */
    public ConditionSet(){
        this.conditionSet = new HashSet<>();
    }
    
    /**
     * Constructor with condition set
     * @param conditionSet 
     */
    public ConditionSet(HashSet<Condition> conditionSet) {
        this.conditionSet = conditionSet;
    }
    
    /**
     * Get condition amount
     * @return 
     */
    public int getConditionAmount() {
        return this.conditionSet.size();
    }
    
    /**
     * Get condition set
     * @return 
     */
    public HashSet<Condition> getBase() {
        return this.conditionSet;
    }

    /**
     * Set condition set
     * @param conditionSet 
     */
    public void setConditionSet(HashSet<Condition> conditionSet){
        this.conditionSet = conditionSet;
    }
    
    
    /**
     * Add new condition to condition set
     * @param condition 
     * @return  
     */
    public boolean addCondition(Condition condition) {
        if(!this.conditionSet.contains(condition)){
            this.conditionSet.add(condition);
        } else {
            Logger.error("Condition already exists.");
            return false;
        }
        return true;
    }
    
    /**
     * Put given condition set to condition set
     * @param aConditionSet 
     */
    public void putConditionSet(ConditionSet aConditionSet) {
        this.conditionSet.addAll(aConditionSet.getBase());
    }
    
    
    /**
     * Add new condition to condition set
     * @param condition 
     * @return  
     */
    public boolean deleteCondition(Condition condition) {
        if(!this.conditionSet.contains(condition)){
            Logger.error("Cannot delete condition.");
            return false;
        } else {
            this.conditionSet.remove(condition);
            Logger.info("Condition deleted.");
        }
        return true;
    }
    
    
    /**
     * Delete all condition
     */
    public void deleteAllCondition() {
        this.conditionSet = new HashSet<>();
        Logger.info("All conditions deleted.");
    }
    
    public boolean isUsedAttribute(String attrName)
    {
    	Iterator conditionIterator = this.conditionSet.iterator();
        while (conditionIterator.hasNext()) 
        {
            Condition aCondition = (Condition) conditionIterator.next();
            if (aCondition.isUsedAttribute(attrName))
            	return true;
        }
        return false;
    }
    
    /** this가 pConditionSet의 모든 조건을 포함하고 있는지 여부 */
    public boolean contains(ConditionSet pConditionSet)
    {
    	Iterator conditionIterator = pConditionSet.getBase().iterator();
        while (conditionIterator.hasNext()) 
        {
        	Condition aCondition = (Condition) conditionIterator.next();
        	if (this.conditionSet.contains(aCondition) == false)
        		return false;
        }
    	return true;
    }
    
   /**
     * Get condition as a object for GUI
     * @return 
     */
    
    public Object[][] toObjectForGUI(int conditionAmount) {
        // new object for gui
        Object[][] newObject = new Object[conditionAmount][3];
        
        // Get an iterator
        Iterator conditionIterator = this.conditionSet.iterator();
        
        //count for condition
        int conditionCnt = 0;
        while (conditionIterator.hasNext()) {
            Condition aCondition = (Condition) conditionIterator.next();
            
            newObject[conditionCnt][0] = aCondition.getAttribute();
            newObject[conditionCnt][1] = aCondition.getOperator();
            newObject[conditionCnt][2] = aCondition.getValue();
                    
            conditionCnt++;
        }        
        return newObject;
    }
    
    public String toObjectforGUI() {
        // Get an iterator
        Iterator conditionIterator = this.conditionSet.iterator();
        // Display elements
        String strConditionSet =" " ;
        int cnt=0;
        while (conditionIterator.hasNext()) {
            if(cnt!=0){
                strConditionSet+= " & ";
            }
            Condition aCondition = (Condition) conditionIterator.next();
            
            strConditionSet += aCondition.toString();
            cnt++;
        }
        return strConditionSet;
    }
   
    /**
     * Get condition as a string
     * @return 
     */
    @Override
    public String toString() {
        String strConditionSet =" ";
        if(this.conditionSet.size() > 0 && this.conditionSet!=null){
            // Get an iterator
            Iterator conditionIterator = this.conditionSet.iterator();
            // Display elements
            int cnt=0;
            while (conditionIterator.hasNext()) {
                if(cnt!=0){
                    strConditionSet+= " & ";
                }
                Condition aCondition = (Condition) conditionIterator.next();

                strConditionSet += aCondition.toString();
                cnt++;
            }
        } else {
            strConditionSet += "null";
        }
        return strConditionSet;
    }
    
    @Override
    public boolean equals(Object o)
    {
    	ConditionSet c2;
    	if (o == null) 
    	{
            return false;
        }

        if (o.getClass() == this.getClass()) 
        {
            c2 = (ConditionSet) o;
            return (this.getBase().equals(c2.getBase()));
        } 
        else 
        {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    
    
}
