/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import rdr.cases.Case;
import rdr.db.RDRDBManager;
import rdr.logger.Logger;


/**
 * This class is used to store conclusion
 * @author David Chung
 */
public class ConclusionSet {
    
    /**
     * Conclusion set <conclusionName, conclusion> 
     */
    private LinkedHashMap<String, Conclusion> conclusionSet = new LinkedHashMap<>();
    
    /**
     * Constructor
     */
    public ConclusionSet(){
        this.conclusionSet = new LinkedHashMap();        
    }
    
    /**
     * Constructor with conclusion set
     * @param conclusionSet 
     */
    public ConclusionSet(LinkedHashMap<String, Conclusion> conclusionSet) {
        this.conclusionSet = conclusionSet;
    }
    
    /**
     * Get conclusion set size
     * @return  returns the number of conclusions in conclusion set
     */
    public int getSize() {
        return this.conclusionSet.size();
    }
    
    
    /**
     * Get a new conclusion id
     * @return 
     */
    public int getNewConclusionId() 
    {
    	/** ruleSet에서 conclusionSet이 편성되고 이 함수가 호출되면 Rule에 mapping 되어 있는 결론중
    	 *  max id + 1 로 체크되는데, rule 에 mapping 되지 않은 결론이 결론테이블에 존재하는 경우
    	 *  저장시 dup 이 발생할 수 있다.
    	 *  따라서 결론테이블의 모든 결론을 대상으로 new id를 체크해야 안전함
    	 */
    	//ConclusionSet tConclusionSet = this.conclusionSet;
    	ConclusionSet tConclusionSet = RDRDBManager.getInstance().getConclusionSet();
    
        if(tConclusionSet.getBase().size()>0)
        {
            Set conclusions = tConclusionSet.getBase().entrySet();
            Iterator iterator = conclusions.iterator();
            int maxId = 0;
            while (iterator.hasNext()) {
                Map.Entry me = (Map.Entry) iterator.next();
                Conclusion aConclusion = (Conclusion)me.getValue();
                maxId = Math.max(maxId, aConclusion.getConclusionId());
            }
            return maxId+1;
        } else {
            return 1;
        }
    }
    
    /**
     * Returns true if there is conclusion in the conclusion set
     * @param aConclusion
     * @return  returns true if there is conclusion in the conclusion set
     */
    public boolean isExist(Conclusion aConclusion) {
        return this.conclusionSet.containsKey(aConclusion.getConclusionName());
    }
    
    public boolean isExist(String aConclusionName)
    {
    	return this.conclusionSet.containsKey(aConclusionName);
    }
    
    /**
     * Get conclusion set
     * @return 
     */
    public LinkedHashMap<String, Conclusion> getBase() {
        return this.conclusionSet;
    }

    /**
     * Set conclusion set
     * @param conclusionSet 
     */
    public void setConclusionSet(ConclusionSet conclusionSet){
        this.conclusionSet = conclusionSet.getBase();
    }
    
    /**
     * Get a conclusion set by conclusion name
     * @param name
     * @return 
     */
    public Conclusion getConclusionByName(String name) {        
        return this.conclusionSet.get(name);
    }
    
    /**
     * Get a conclusion set by conclusion id
     * @param conclusionId
     * @return 
     */
    public Conclusion getConclusionById(int conclusionId) {      
        Conclusion conclusion = new Conclusion();
        Set conclusions = this.conclusionSet.entrySet();
        // Get an iterator
        Iterator conclusionIterator = conclusions.iterator();
        while (conclusionIterator.hasNext()) {
            Map.Entry me = (Map.Entry) conclusionIterator.next();
            conclusion = (Conclusion)me.getValue();
            if(conclusion.getConclusionId()==conclusionId){
                return conclusion;
            }
        }
        return null;
    }
    
    /**
     * Add new conclusion to conclusion set
     * @param conclusion 
     * @return  
     */
    public boolean addConclusion(Conclusion conclusion) {
        if(!this.conclusionSet.containsKey(conclusion.getConclusionName())){
            this.conclusionSet.put(conclusion.getConclusionName(), conclusion);
        } else {
            return false;
        }
        return true;
    }
    
    
    /**
     * Add new conclusion to conclusion set
     * @param conclusion 
     * @return  
     */
    public boolean deleteConclusion(Conclusion conclusion) {
        if(!this.conclusionSet.containsKey(conclusion.getConclusionName())){
            Logger.error("This conclusion " + conclusion.getConclusionName() + " does not exist.");
            return false;
        } else {
            this.conclusionSet.remove(conclusion.getConclusionName());
        }
        return true;
    }
    
    /**
     * Add new conclusion to conclusion set
     * @param name
     * @return  
     */
    public boolean deleteConclusionByName(String name) {
        if(!this.conclusionSet.containsKey(name)){
            Logger.error("This conclusion " + name + " does not exist.");
            return false;
        } else {
            this.conclusionSet.remove(name);
        }
        return true;
    }    
    
    /**
     * Delete all conclusion
     */
    public void deleteAllConclusion() {
        this.conclusionSet = new LinkedHashMap<>();
    }
    
    /** 
     * get conclusion, id sorted
     * @return
     */
    public TreeMap<Integer, String> getIdConclusions()
    {
    	TreeMap<Integer, String> conclusionMap = new TreeMap<Integer, String>();
    	
    	Iterator<String> keys = conclusionSet.keySet().iterator();
    	while ( keys.hasNext())
    	{
    		String key = keys.next();
    		Conclusion tConclusion = conclusionSet.get(key);
    		conclusionMap.put(new Integer(tConclusion.getConclusionId()), 
                              tConclusion.getConclusionName());
    	}
    	
    	return conclusionMap;
    }
    
    
    /**
     * Get conclusion set as string array
     * @return 
     */
    public String[] toStringArrayForGUI() {
        Set conclusions = this.conclusionSet.entrySet();
        // Get an iterator
        Iterator caseIterator = conclusions.iterator();
        // Display elements
        String[] strConclusionArray = new String[conclusions.size()+1];
        int i=0;
        while (caseIterator.hasNext()) {
            Map.Entry me = (Map.Entry) caseIterator.next();
            Conclusion conclusion = (Conclusion)me.getValue();
            strConclusionArray[i] = conclusion.getConclusionName();
            i++;
        }
        if(i==0){
            strConclusionArray = new String[2];
            strConclusionArray[0] = "There is no conclusion";
            i++;
        }
        strConclusionArray[i] = "[Add Conclusion]";
        return strConclusionArray;
    }
    
    /**
     * Get conclusion set as string array
     * @return 
     */
    public String[] toStringArrayForGUIWithoutAddConclusion() {
        Set conclusions = this.conclusionSet.entrySet();
        // Get an iterator
        Iterator conclusionIterator = conclusions.iterator();
        // Display elements
        String[] strConclusionArray = new String[conclusions.size()];
        int i=0;
        while (conclusionIterator.hasNext()) {
            Map.Entry me = (Map.Entry) conclusionIterator.next();
            Conclusion conclusion = (Conclusion)me.getValue();
            strConclusionArray[i] = conclusion.toString();
            i++;
        }
        if(i==0){            
            strConclusionArray = new String[1];
            strConclusionArray[0] = "There is no conclusion";            
        }        
        return strConclusionArray;
    }
    /**
     * Get conclusion set as string array
     * @return 
     */
    public String[] toSortedStringArrayForGUIWithoutAddConclusion(boolean ascending) {
        String[] strConclusionArray = new String[this.conclusionSet.size()];
        int i=0;
        
        if(ascending){           
            Map<String, Conclusion> map = new TreeMap<String, Conclusion>(this.conclusionSet); 
            Set conclusions = map.entrySet();
            // Get an iterator
            Iterator conclusionIterator = conclusions.iterator();
            
            while (conclusionIterator.hasNext()) {
                Map.Entry me = (Map.Entry) conclusionIterator.next();
                Conclusion conclusion = (Conclusion)me.getValue();
                strConclusionArray[i] = conclusion.toString();
                i++;
            }
        } else {
            Map<String, Conclusion> map = new TreeMap<String, Conclusion>(this.conclusionSet); 
            Set conclusions = map.keySet();
            ListIterator<String> iterator = new ArrayList(conclusions).listIterator(conclusions.size());
            
            while (iterator.hasPrevious()){ 
                String key = iterator.previous();
                Conclusion conclusion = this.conclusionSet.get(key);
                strConclusionArray[i] = conclusion.toString();
                i++;
            }
        }
        if(i==0){            
            strConclusionArray = new String[1];
            strConclusionArray[0] = "There is no conclusion";            
        }        
        return strConclusionArray;
    }
    
    public Object[][] toObjectsForGUIWithFilter(String textFilter){
        int conclusionAmount = this.conclusionSet.size();
        int columnAmount = 2;
        
        Object[][] tmpReturnObject = new Object[conclusionAmount][columnAmount];
        Set conclusions = this.conclusionSet.entrySet();
        // Get an iterator
        Iterator conclusionIterator = conclusions.iterator();
        // Display elements
        int rowCnt = 0;
        while (conclusionIterator.hasNext()) {
            Map.Entry me = (Map.Entry) conclusionIterator.next();            
            Conclusion aConclusion = (Conclusion) me.getValue();
            if(textFilter != null){
                if(aConclusion.getConclusionName().toLowerCase().contains(textFilter.toLowerCase())){
                    tmpReturnObject[rowCnt][0] = aConclusion.getConclusionValue().getValueType();
                    tmpReturnObject[rowCnt][1] = aConclusion.getConclusionValue();                
                    rowCnt++;
                }
            } else {
                tmpReturnObject[rowCnt][0] = aConclusion.getConclusionValue().getValueType();
                tmpReturnObject[rowCnt][1] = aConclusion.getConclusionValue();
                rowCnt++;
            }
        }
        
        Object[][] returnObject = new Object[rowCnt][columnAmount];
        for(int i=0; i<rowCnt; i++) {
            returnObject[i][0] = tmpReturnObject[i][0];
            returnObject[i][1] = tmpReturnObject[i][1];
        }
        return returnObject;        
    }
    
    public Object[][] toObjectsForGUI(){
        int conclusionAmount = this.conclusionSet.size();
        int columnAmount = 2;
        
        Object[][] returnObject = new Object[conclusionAmount][columnAmount];
        
        Set conclusions = this.conclusionSet.entrySet();
        // Get an iterator
        Iterator conclusionIterator = conclusions.iterator();
        // Display elements
        int rowCnt = 0;
        while (conclusionIterator.hasNext()) {
            Map.Entry me = (Map.Entry) conclusionIterator.next();            
            Conclusion aConclusion = (Conclusion) me.getValue();
            returnObject[rowCnt][0] = aConclusion.getConclusionValue().getValueType();
            returnObject[rowCnt][1] = aConclusion.getConclusionValue();
            
            rowCnt++;
        }
        return returnObject;        
    }
   
    /**
     * Get conclusion as a string
     * @return 
     */
    @Override
    public String toString() {
        Set conclusions = this.conclusionSet.entrySet();
        // Get an iterator
        Iterator conclusionIterator = conclusions.iterator();
        // Display elements
        String strConclusion ="Rule id " ;
        while (conclusionIterator.hasNext()) {
            Map.Entry me = (Map.Entry) conclusionIterator.next();            
            strConclusion += me.getValue().toString()+"\n";
            
        }
        strConclusion = strConclusion+"\n";
        return strConclusion;
    }
}
 