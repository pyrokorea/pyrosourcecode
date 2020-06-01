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
import rdr.apps.Main;
import rdr.logger.Logger;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.rules.Rule;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class CornerstoneCaseSet {
    
    /**
     * case set
     */
    private HashMap<Integer, CornerstoneCase> cornerstoneCaseSet = new HashMap<>();
    
    /**
     * case structure
     */
    private CaseStructure caseStructure;    
    
    /**
     * Constructor.
     */
    public CornerstoneCaseSet(){
        cornerstoneCaseSet = new HashMap<>();
        caseStructure = new CaseStructure();
    }
    
    /**
     * Constructor with cornerstone case structure.
     * @param caseStructure 
     */
    public CornerstoneCaseSet(CaseStructure caseStructure) {
        this.caseStructure = caseStructure;
    }
    
    /**
     * Constructor.
     * @param cornerstoneCaseSet 
     */
    public CornerstoneCaseSet(CornerstoneCaseSet cornerstoneCaseSet) {
        this.cornerstoneCaseSet = cornerstoneCaseSet.getBase();
        this.caseStructure = cornerstoneCaseSet.caseStructure;
    }
    
    /**
     * Constructor.
     * @param caseSet 
     */
    public CornerstoneCaseSet(CaseSet caseSet) {
        Set cases = caseSet.getBase().entrySet();
        // Get an iterator
        Iterator caseIterator = cases.iterator();
        while (caseIterator.hasNext()) {
            Map.Entry me = (Map.Entry) caseIterator.next();
            Case aCase = (Case) me.getValue();
            CornerstoneCase aCornerstoneCase = new CornerstoneCase(aCase);
                    
            this.cornerstoneCaseSet.put(aCornerstoneCase.getCaseId(), aCornerstoneCase);
        }
        this.caseStructure = caseSet.getCaseStructure();
    }
    
    /**
     * Get cornerstone case base
     * @return 
     */
    public HashMap<Integer, CornerstoneCase> getBase() {
        return this.cornerstoneCaseSet;
    }

    /**
     * Set case base
     * @param cornerstoneCaseSet 
     */
    public void setCornerstoneCaseBase(HashMap<Integer, CornerstoneCase> cornerstoneCaseSet){
        this.cornerstoneCaseSet = cornerstoneCaseSet;
    }
    
    /**
     * Get first cornerstone case in the set
     * @return 
     */
    public CornerstoneCase getFirstCornerstoneCase() {
        if(this.cornerstoneCaseSet.size()>0){
            Set cases = this.cornerstoneCaseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();
            if(caseIterator.hasNext()){
                Map.Entry me = (Map.Entry) caseIterator.next();
                CornerstoneCase aCase = (CornerstoneCase) me.getValue();
                return aCase;
            }
        } else {
            return null;
        }
        return null;
    }
    
    /**
     * Get next case of the given cornerstone case
     * @param aCase
     * @return 
     */
    public CornerstoneCase getNextCornerstoneCase(CornerstoneCase aCase) {
        if(this.cornerstoneCaseSet.size()>0){
            Set cases = this.cornerstoneCaseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();
            while(caseIterator.hasNext()){
                Map.Entry me = (Map.Entry) caseIterator.next();
                CornerstoneCase currentCase = (CornerstoneCase) me.getValue();
                if(currentCase.equals(aCase)){
                    Map.Entry me2 = (Map.Entry) caseIterator.next();
                    CornerstoneCase resultCase = (CornerstoneCase) me2.getValue();
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
    public CornerstoneCase getPreviousCornerstoneCase(Case aCase) {
        if(this.cornerstoneCaseSet.size()>0){
            Set cases = this.cornerstoneCaseSet.entrySet();
            // Get an iterator
            int cnt=0;
            Iterator caseIterator = cases.iterator();
            CornerstoneCase previousCase =null;
            while(caseIterator.hasNext()){
                Map.Entry me = (Map.Entry) caseIterator.next();
                CornerstoneCase currentCase = (CornerstoneCase) me.getValue();
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
     * Returns true if there is next added cornerstone case
     * @param aCase
     * @return 
     */
    public boolean hasNextCornerstoneCase(Case aCase) {
        if(this.cornerstoneCaseSet.size()>0){
            Set cases = this.cornerstoneCaseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();
            while(caseIterator.hasNext()){
                Map.Entry me = (Map.Entry) caseIterator.next();
                CornerstoneCase currentCase = (CornerstoneCase) me.getValue();
                if(currentCase.equals(aCase)){
                    return caseIterator.hasNext();
                }
            }
        } 
        return false;  
    }
    
    /**
     * Returns true if this first added cornerstone case
     * @param aCase
     * @return 
     */
    public boolean isFirstCornerstoneCase(CornerstoneCase aCase) {
        if(this.cornerstoneCaseSet.size()>0){
            Set cases = this.cornerstoneCaseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();
            if(caseIterator.hasNext()){
                Map.Entry me = (Map.Entry) caseIterator.next();
                CornerstoneCase currentCase = (CornerstoneCase) me.getValue();
                if(currentCase.equals(aCase)){
                    return true;
                }
            }
        } 
        return false;  
    }
    
    /**
     * return true if cornerstone case exists in cornerstone case set
     * @param aCornerstoneCase
     * @return  
     */
    public boolean isCornerstonCaseExist(CornerstoneCase aCornerstoneCase) {
        if(this.cornerstoneCaseSet.size()>0){
            Set cases = this.cornerstoneCaseSet.entrySet();
            Iterator iterator = cases.iterator();
            while (iterator.hasNext()) {
                Map.Entry me = (Map.Entry) iterator.next();
                CornerstoneCase checkingCornerstoneCase = (CornerstoneCase)me.getValue();
                if(checkingCornerstoneCase.equalCornerstoneCase(aCornerstoneCase)){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * return matching cornerstone case exists in cornerstone case set if exists, if not return null
     * @param aCornerstoneCase
     * @return  
     */
    public CornerstoneCase getExistingCornerstonCase(CornerstoneCase aCornerstoneCase) {
        if(this.cornerstoneCaseSet.size()>0){
            Set cases = this.cornerstoneCaseSet.entrySet();
            Iterator iterator = cases.iterator();
            while (iterator.hasNext()) {
                Map.Entry me = (Map.Entry) iterator.next();
                CornerstoneCase checkingCornerstoneCase = (CornerstoneCase)me.getValue();                
                if(checkingCornerstoneCase.equalCornerstoneCase(aCornerstoneCase)){
                    return checkingCornerstoneCase;
                }
            }
        }
        return null;
    }
    
    /**
     * return true if case exists as cornerstone case in case set
     * @param aCase
     * @return  
     */
    public boolean isCaseExist(Case aCase) {
        return this.cornerstoneCaseSet.containsKey(aCase.getCaseId());            
    }
    
    /**
     * return true if case id assigned in case set
     * @param caseId
     * @return  
     */
    public boolean isCaseIdExist(int caseId) {
        return this.cornerstoneCaseSet.containsKey(caseId);            
    }
    
    /**
     * Get number of cornerstone cases in set
     * @return 
     */
    public int getCaseAmount() {
        return this.cornerstoneCaseSet.size();
    }
    
    /**
     * Get new cornerstone case id
     * @return 
     */
    public int getNewCornerstoneCaseId() {
        if(this.cornerstoneCaseSet.size()>0){
            Set cases = this.cornerstoneCaseSet.entrySet();
            Iterator iterator = cases.iterator();
            int maxId = 0;
            
            while (iterator.hasNext()) {
                Map.Entry me = (Map.Entry) iterator.next();
                CornerstoneCase aCornerstoneCase = (CornerstoneCase)me.getValue();
                maxId = Math.max(maxId, aCornerstoneCase.getCaseId());
            }
            return maxId+1;
        } else {
            return 1;
        }
    }
    
    /**
     * Get cornerstone case by Id
     * @param caseId
     * @return 
     */
    public CornerstoneCase getCornerstoneCaseById(int caseId) {
        return this.cornerstoneCaseSet.get(caseId);
    }
    
    /**
     * Add cornerstone case
     * @param aCase 
     * @return  
     */
    public boolean addCase(Case aCase) {
        CornerstoneCase aCornerstoneCase = new CornerstoneCase(aCase);
        if(this.cornerstoneCaseSet.containsKey(aCornerstoneCase.getCaseId())){
            aCase.setCaseId(aCase.getCaseId()+1);
            this.addCase(aCase);
        } else {
            this.cornerstoneCaseSet.put(aCornerstoneCase.getCaseId(), aCornerstoneCase);
        }
        
        return true;
    }
    
    /** ucciri@gmail
     *  RDRDBManager.getCornerstoneCaseSet에서는 case의 모든 value가 setting되기 전이기때문에
     *  isCornerstonCaseExist, getExistCornerstoneCase 등을 사용하면 안됨
     */
    public boolean addCornerstoneCase(CornerstoneCase aCornerstoneCase)
    {
    	if (this.isCaseIdExist(aCornerstoneCase.getCaseId()) == false)
    	{
    		this.cornerstoneCaseSet.put(aCornerstoneCase.getCaseId(), aCornerstoneCase);
    	}
    	return true;
    }
    
    /**
     * Add cornerstone case
     * @param aRule
     * @param aCornerstoneCase 
     * @return  
     */
    public boolean addCornerstoneCase(Rule aRule, CornerstoneCase aCornerstoneCase) 
    {
    	//IncrementalLearner에서는 case 중복이 없다는 가정 (성능때문)
    	//this.cornerstoneCaseSet.put(aCornerstoneCase.getCaseId(), aCornerstoneCase);
    	
    	CornerstoneCase cc = this.getExistingCornerstonCase(aCornerstoneCase);
        if(cc != null)
        {
        	if (aRule != null)
        	{
        		cc.addRuleToWrongRuleSet(aRule);
        	}
        }
        else 
        {
            this.cornerstoneCaseSet.put(aCornerstoneCase.getCaseId(), aCornerstoneCase);
            //addRuleToWrongRuleSet은 수행 안해도 되나? (ucciri)
        }
        return true;
    }
    
    /**
     * Put cornerstone case set together
     * @param aCornerstoneCaseSet 
     */
    public void putCornerstoneCaseSet(CornerstoneCaseSet aCornerstoneCaseSet) {
        cornerstoneCaseSet.putAll(aCornerstoneCaseSet.getBase());
    }
    
    /**
     * Put cornerstone case set together
     * @param aCaseSet 
     */
    public void putCornerstoneCaseSet(CaseSet aCaseSet) {
        CornerstoneCaseSet aCornerstoneCaseSet = new CornerstoneCaseSet(aCaseSet);
        cornerstoneCaseSet.putAll(aCornerstoneCaseSet.getBase());
    }
    
    /**
     * delete cornerstone case 
     * @param aCornerstoneCase
     * @return  
     */
    public boolean deleteCornerstoneCase(CornerstoneCase aCornerstoneCase) {
        if(this.cornerstoneCaseSet.containsValue(aCornerstoneCase)){
            this.cornerstoneCaseSet.remove(aCornerstoneCase.getCaseId());
        } else {
            if(this.getSameValuesCornerstoneCase(aCornerstoneCase) != null) {
                this.cornerstoneCaseSet.remove(this.getSameValuesCornerstoneCase(aCornerstoneCase).getCaseId());
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
    
    /**
     * get cornerstone case that contains same values
     * @param aCornerstoneCase
     * @return 
     */
    public CornerstoneCase getSameValuesCornerstoneCase(CornerstoneCase aCornerstoneCase) {
        Set cases = this.cornerstoneCaseSet.entrySet();
        // Get an iterator
        Iterator caseIterator = cases.iterator();
        // Display elements
        while (caseIterator.hasNext()) {
            Map.Entry me = (Map.Entry) caseIterator.next();
            CornerstoneCase comparingCornerstoneCase = (CornerstoneCase) me.getValue();
            if(aCornerstoneCase.getValues().equals(comparingCornerstoneCase.getValues())){
                return comparingCornerstoneCase;
            }
        }
        return null;
    }
    
    /**
     * delete case case id
     * @param caseId 
     * @return  
     */
    public boolean deleteCaseById(int caseId) {
        if(this.cornerstoneCaseSet.containsKey(caseId)){
            this.cornerstoneCaseSet.remove(caseId);
        } else {
            Logger.error("This case does not exist in the case set");
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        Set cases = this.cornerstoneCaseSet.entrySet();
        // Get an iterator
        Iterator caseIterator = cases.iterator();
        // Display elements
        String strCaseBase = "\n";
        strCaseBase = strCaseBase+"Cases in Casebase \n";
        strCaseBase = strCaseBase+"================= \n";
        while (caseIterator.hasNext()) {
            Map.Entry me = (Map.Entry) caseIterator.next();
            int caseId = (int) me.getKey();
            CornerstoneCase aCornerstoneCase = (CornerstoneCase) me.getValue();
            strCaseBase = strCaseBase + "Case " + caseId + ": "+ aCornerstoneCase.toString() + "\n";
        }
        strCaseBase = strCaseBase+"================= \n";
        return strCaseBase;
    }
    
    public String toStringOnlyId() {
        Set cases = this.cornerstoneCaseSet.entrySet();
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
    
    public Object[][] toObjectForGUI(int caseAmount, int colAmount) {
        // new object for gui
        Object[][] newObject = new Object[caseAmount][colAmount];
        
        if(caseAmount!=0){

            Set cases = this.cornerstoneCaseSet.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();

            //count for case
            int caseCnt = 0;
            while (caseIterator.hasNext()) {
                Map.Entry me = (Map.Entry) caseIterator.next();
                int caseId = (int) me.getKey();
                CornerstoneCase aCase = (CornerstoneCase) me.getValue();

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
                    newObject[caseCnt][attrCnt] = me2.getValue().toString();

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
            Map<Integer, Case> map = new TreeMap<Integer, Case>(this.cornerstoneCaseSet); 

            Set cases = map.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();

            //count for case
            int caseCnt = 0;
            while (caseIterator.hasNext()) {
                Map.Entry me = (Map.Entry) caseIterator.next();
                int caseId = (int) me.getKey();
                CornerstoneCase aCase = (CornerstoneCase) me.getValue();

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
                    newObject[caseCnt][attrCnt] = me2.getValue().toString();

                    attrCnt++;
                }
                caseCnt++;
            }        
        }
        
        return newObject;
    }
    
    public Object[][] toFilteredSortedObjectForGUI(String filter, int caseAmount, int colAmount) {
        // new object for gui
        Object[][] tmpNewObject = new Object[caseAmount][colAmount];
        int attrCnt=0;
        if(caseAmount!=0){
            Map<Integer, Case> map = new TreeMap<Integer, Case>(this.cornerstoneCaseSet); 

            Set cases = map.entrySet();
            // Get an iterator
            Iterator caseIterator = cases.iterator();

            //count for case
            int caseCnt = 0;
            while (caseIterator.hasNext()) {
                Map.Entry me = (Map.Entry) caseIterator.next();
                int caseId = (int) me.getKey();
                CornerstoneCase aCase = (CornerstoneCase) me.getValue();

                LinkedHashMap<String, Value> caseValues = aCase.getValues();

                Map<String, Value> map2 = new TreeMap<String, Value>(caseValues); 
                Set values = map2.entrySet();
                // Get an iterator
                Iterator valIterator = values.iterator();

                //store case id in first column
                tmpNewObject[caseCnt][0] = caseId;

                //count for attribute
                attrCnt=1;
                while (valIterator.hasNext()) {
                    Map.Entry me2 = (Map.Entry) valIterator.next();
                    String attrName = (String) me2.getKey();
                    if(attrName.contains(filter)){
                        tmpNewObject[caseCnt][attrCnt] = me2.getValue();
                        attrCnt++;
                    }
                }                
                caseCnt++;
            }
            
            Object[][] newObject = new Object[caseAmount][attrCnt];
            for(int i=0; i< caseAmount; i++){
                for(int j=0; j< attrCnt; j++){
                    newObject[i][j] = tmpNewObject[i][j];
                }
            }
            return newObject;
        }
        return null;
    }
}
