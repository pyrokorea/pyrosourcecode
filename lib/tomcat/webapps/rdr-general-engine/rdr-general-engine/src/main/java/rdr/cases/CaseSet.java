/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.cases;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import rdr.logger.Logger;
import rdr.model.Value;

/**
 * This class is a container for cases
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class CaseSet {
    
    /**
     * case set
     */
    private HashMap<Integer, Case> caseSet = new HashMap<>();
    
    /**
     * case structure
     */
    private CaseStructure caseStructure;    
    
    
    /**
     * Constructor.
     */
    public CaseSet(){
        caseSet = new HashMap<>();
        caseStructure = new CaseStructure();
    }
    
    /**
     * Constructor with case structure.
     * @param caseStructure 
     */
    public CaseSet(CaseStructure caseStructure) {
        this.caseStructure = caseStructure;
    }
    
    /**
     * Constructor.
     * @param caseSet 
     */
    public CaseSet(CaseSet caseSet) {
        this.caseSet = caseSet.getBase();
        this.caseStructure = caseSet.caseStructure;
    }
    
    /**
     * Get case base
     * @return 
     */
    public HashMap<Integer, Case> getBase() {
        return this.caseSet;
    }

    /**
     * Set case base
     * @param caseSet 
     */
    public void setCaseBase(HashMap<Integer, Case> caseSet){
        this.caseSet = caseSet;
    }
    
    /**
     * Get number of cases in set
     * @return 
     */
    public int getCaseAmount() {
        return this.caseSet.size();
    }
    
    /**
     * Get case structure
     * @return returns a structure of the case
     */
    public CaseStructure getCaseStructure() {
        return this.caseStructure;
    }
    
    /**
     * Get new case id
     * @return 
     */
    public int getNewCaseId() {
        if(this.caseSet.size()>0){
            Set dialogs = this.caseSet.entrySet();
            Iterator iterator = dialogs.iterator();
            int maxId = 0;
            
            while (iterator.hasNext()) {
                Map.Entry me = (Map.Entry) iterator.next();
                Case aCase = (Case)me.getValue();
                maxId = Math.max(maxId, aCase.getCaseId());
            }
            return this.caseSet.get(maxId).getCaseId()+1;
        } else {
            return 1;
        }
    }
    
    /**
     * Check whether case exist in case set
     * @param caseId
     * @return 
     */
    public boolean isCaseExist(int caseId) {
        return this.caseSet.containsKey(caseId);
    }
    
    
    /**
     * Get case by Id
     * @param caseId
     * @return 
     */
    public Case getCaseById(int caseId) {
        return this.caseSet.get(caseId);
    }
    
    
    
    /**
     * Replace case 
     * @param aCase
     * @return 
     */
    public Case replaceCase(Case aCase) {
        return this.caseSet.replace(aCase.getCaseId(), aCase);
    }
    
    
    /**
     * Add case
     * @param aCase 
     * @return  
     */
    public boolean addCase(Case aCase) {
        if(this.caseSet.containsKey(aCase.getCaseId())){
            Logger.error("Cannot add this case since it is already in the case set");
            return false;
        } else {
            this.caseSet.put(aCase.getCaseId(), aCase);
        }
        return true;
    }
    
    /**
     * Put case set together
     * @param aCaseSet 
     */
    public void putCaseSet(CaseSet aCaseSet) {
        this.caseSet.putAll(aCaseSet.getBase());
    }
    
    /**
     * Put case set together
     * @param aCornerstoneCaseSet 
     */
    public void putCornerstoneCaseSet(CornerstoneCaseSet aCornerstoneCaseSet) {
        this.caseSet.putAll(aCornerstoneCaseSet.getBase());
    }
    
    /**
     * delete case
     * @param aCase 
     * @return  
     */
    public boolean deleteCase(Case aCase) {
        if(this.caseSet.containsValue(aCase)){
            this.caseSet.remove(aCase.getCaseId());
        } else {
            Logger.error("This case does not exist in the case set");
            return false;
        }
        return true;
    }
    
    /**
     * delete case case id
     * @param caseId 
     * @return  
     */
    public boolean deleteCaseById(int caseId) {
        if(this.caseSet.containsKey(caseId)){
            this.caseSet.remove(caseId);
        } else {
            Logger.error("This case does not exist in the case set");
            return false;
        }
        return true;
    }
    
    /**
     * Get first case in the set
     * @return 
     */
    public Case getFirstCase() {
        if(this.caseSet.size()>0){
            Set cases = this.caseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();
            if(caseIterator.hasNext()){
                Map.Entry me = (Map.Entry) caseIterator.next();
                Case aCase = (Case) me.getValue();
                return aCase;
            }
        } else {
            return null;
        }
        return null;
    }
    
    /**
     * Get next case of the given case
     * @param aCase
     * @return 
     */
    public Case getNextCase(Case aCase) {
        if(this.caseSet.size()>0){
            Set cases = this.caseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();
            while(caseIterator.hasNext()){
                Map.Entry me = (Map.Entry) caseIterator.next();
                Case currentCase = (Case) me.getValue();
                if(currentCase.equals(aCase)){
                    Map.Entry me2 = (Map.Entry) caseIterator.next();
                    Case resultCase = (Case) me2.getValue();
                    return resultCase;
                }
            }
        } else {
            return null;
        }
        return null;
    }
    
    /**
     * Get previously added case of the given cornerstone case
     * @param aCase
     * @return 
     */
    public Case getPreviousCase(Case aCase) {
        if(this.caseSet.size()>0){
            Set cases = this.caseSet.entrySet();
            // Get an iterator
            int cnt=0;
            Iterator caseIterator = cases.iterator();
            Case previousCase = null;
            while(caseIterator.hasNext()){
                Map.Entry me = (Map.Entry) caseIterator.next();
                Case currentCase = (Case) me.getValue();
                if(cnt!=0){
                    if(currentCase.equals(aCase)){
                        return previousCase;
                    }
                }
                previousCase = currentCase;
                cnt++;
            }
        } else {
            return null;
        }
        return null;
    }
    
    /**
     * Returns true if there is next added case
     * @param aCase
     * @return 
     */
    public boolean hasNextCase(Case aCase) {
        if(this.caseSet.size()>0){
            Set cases = this.caseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();
            while(caseIterator.hasNext()){
                Map.Entry me = (Map.Entry) caseIterator.next();
                Case currentCase = (Case) me.getValue();
                if(currentCase.equals(aCase)){
                    return caseIterator.hasNext();
                }
            }
        } 
        return false;  
    }
    
    /**
     * Returns true if this first added case
     * @param aCase
     * @return 
     */
    public boolean isFirstCase(Case aCase) {
        if(this.caseSet.size()>0){
            Set cases = this.caseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();
            if(caseIterator.hasNext()){
                Map.Entry me = (Map.Entry) caseIterator.next();
                Case currentCase = (Case) me.getValue();
                if(currentCase.equals(aCase)){
                    return true;
                }
            }
        } 
        return false;  
    }
    
    
    @Override
    public String toString() {
        Set cases = this.caseSet.entrySet();
        // Get an iterator
        Iterator caseIterator = cases.iterator();
        // Display elements
        String strCaseBase = "\r\n";
        strCaseBase = strCaseBase+"Cases in Casebase \r\n";
        strCaseBase = strCaseBase+"================= \r\n";
        while (caseIterator.hasNext()) {
            Map.Entry me = (Map.Entry) caseIterator.next();
            strCaseBase = strCaseBase + "Case "+me.getKey()+": "+me.getValue().toString()+"\r\n";
        }
        strCaseBase = strCaseBase+"================= \r\n";
        return strCaseBase;
    }
    
    public String toStringSorted() {
        Map<Integer, Case> map = new TreeMap<Integer, Case>(this.caseSet); 
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        
        String strCaseBase = "\r\n";
        strCaseBase = strCaseBase+"Cases in Casebase \r\n";
        strCaseBase = strCaseBase+"================= \r\n";    
        
        while(iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
            strCaseBase = strCaseBase + "Case "+me2.getKey()+": "+me2.getValue().toString()+"\r\n";
        }
       
        strCaseBase = strCaseBase+"================= \r\n";
        return strCaseBase;
    }
    
    public String toStringOnlyId() {
        Set cases = this.caseSet.entrySet();
        // Get an iterator
        Iterator caseIterator = cases.iterator();
        // Display elements
        String strCaseBase = "";        
        int cnt=0;
        while (caseIterator.hasNext()) {
            if(cnt!=0){
                strCaseBase+=", ";
            }
            Map.Entry me = (Map.Entry) caseIterator.next();
            strCaseBase = strCaseBase + me.getKey();
            cnt++;
        }        
        return strCaseBase;
    }
    
    public String toStringOnlyIdSorted() {
        Map<Integer, Case> map = new TreeMap<Integer, Case>(this.caseSet); 
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        
        String strCaseBase = "";        
        int cnt=0;
        
        while(iterator2.hasNext()) {
            if(cnt!=0){
               strCaseBase+=", ";
            }
            Map.Entry me2 = (Map.Entry)iterator2.next();
            strCaseBase = strCaseBase + me2.getKey();
            cnt++;
        }
       
        return strCaseBase;
    }
    
    public Object[][] toObjectForGUI(int caseAmount, int colAmount) {
        // new object for gui
        Object[][] newObject = new Object[caseAmount][colAmount];
        
        if(caseAmount!=0){

            Set cases = this.caseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();

            //count for case
            int caseCnt = 0;
            while (caseIterator.hasNext()) {
                Map.Entry me = (Map.Entry) caseIterator.next();
                int caseId = (int) me.getKey();
                Case aCase = (Case) me.getValue();

                LinkedHashMap<String, Value> caseValues = aCase.getValues();
                Set values = caseValues.entrySet();
                // Get an iterator
                Iterator valIterator = values.iterator();

                //store case id in first column
                newObject[caseCnt][0] = caseId;

                //count for attribute
                int attrCnt=1;
                while (valIterator.hasNext()) {
                    Map.Entry me2 = (Map.Entry) valIterator.next();
                    newObject[caseCnt][attrCnt] = me2.getValue();

                    attrCnt++;
                }
                caseCnt++;
            }        
        }
        
        return newObject;
    }
    
    public Object[][] toSortedObjectForGUI(int caseAmount, int colAmount) {
        // new object for gui
        Object[][] newObject = new Object[caseAmount][colAmount];
        
        if(caseAmount!=0){

            Set cases = this.caseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();

            //count for case
            int caseCnt = 0;
            while (caseIterator.hasNext()) {
                Map.Entry me = (Map.Entry) caseIterator.next();
                int caseId = (int) me.getKey();
                Case aCase = (Case) me.getValue();

                LinkedHashMap<String, Value> caseValues = aCase.getValues();

                Map<String, Value> map = new TreeMap<String, Value>(caseValues); 
                
                Set values = map.entrySet();
                // Get an iterator
                Iterator valIterator = values.iterator();

                //store case id in first column
                newObject[caseCnt][0] = caseId;

                //count for attribute
                int attrCnt=1;
                while (valIterator.hasNext()) {
                    Map.Entry me2 = (Map.Entry) valIterator.next();
                    newObject[caseCnt][attrCnt] = me2.getValue();

                    attrCnt++;
                }
                caseCnt++;
            }        
        }
        
        return newObject;
    }
}
