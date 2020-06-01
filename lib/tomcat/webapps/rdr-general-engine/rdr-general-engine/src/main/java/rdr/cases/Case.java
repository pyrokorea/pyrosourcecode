/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.cases;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import rdr.logger.Logger;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.model.ValueType;
import rdr.rules.Conclusion;
import rdr.rules.ConclusionSet;
import rdr.rules.RuleSet;

/**
 * This class is used to define a case used in RDR
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class Case {
    
    /**
     * Processed case identifier
     */
    public static final int Processed = 1;  
    
    /**
     * NOT Processed case identifier
     */
    public static final int NotProcessed = 0;  
    
    /**
     * Case identifier
     */
    private int caseId;
    
    /**
     * case structure
     */
    private CaseStructure caseStructure;    
    
    /**
     * Value set for each attribute
     */
    private LinkedHashMap<String, Value> attributeValues = new LinkedHashMap<String, Value>(); 
    
    /**
     * Case status - processed (1) or not (0)
     */
    private int caseStatus;
    
    /**
     * Inference Result
     */
    private RuleSet inferenceResult = new RuleSet();
    
    /**
     * conclusion set of inference result
     */
    private ConclusionSet conclusionSet = new ConclusionSet();

    
    /**
     * Constructor.
     * @param aCase
     */
    public Case(Case aCase){
        this.caseStructure = aCase.caseStructure;
        this.attributeValues = (LinkedHashMap<String, Value>) aCase.attributeValues.clone();
        this.caseId = aCase.caseId;
        this.caseStatus = aCase.caseStatus;
        this.conclusionSet = aCase.conclusionSet;
    }
    
    /**
     * Constructs a case object
     *
     * @param caseStructure
     */
    public Case(CaseStructure caseStructure) {
        this.caseStructure = caseStructure;
        this.attributeValues = new LinkedHashMap<>();
        this.caseStatus = Case.NotProcessed;
    }
    
    /**
     * Constructs a case object
     *
     * @param values
     * @param caseStructure
     */
    public Case(CaseStructure caseStructure, LinkedHashMap<String, Value> values) {
        this.caseStructure = caseStructure;
        this.attributeValues = values;
        this.caseStatus = Case.NotProcessed;
    }    
    /**
     * Constructs a case object
     *
     * @param id the case id
     * @param caseStructure
     * @param values
     */
    public Case(int id, CaseStructure caseStructure, LinkedHashMap<String, Value> values) {
        this.caseId = id;
        this.caseStructure = caseStructure;
        this.attributeValues = values;
        this.caseStatus = Case.NotProcessed;
    }

    /**
     * Get case id
     * @return 
     */
    public int getCaseId() {
        return this.caseId;
    }    
    
    /**
     * Set case id
     * @param id 
     */
    public void setCaseId(int id) {
        this.caseId = id;
    }
    
    /**
     * Get case values
     * @return 
     */
    public LinkedHashMap<String, Value> getValues() {
        return this.attributeValues;
    }
    
    public void removeKey(String key)
    {
    	this.attributeValues.remove(key);
    }
    
    /**
     * Set case values
     * @param values
     * @return 
     */
    public boolean setValues(LinkedHashMap<String, Value> values) {
        Set set = values.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            String attributeName = (String) me.getKey();
            Value value = (Value) me.getValue();
            if(this.caseStructure.getAttributeByName(attributeName).getValueType()
                    != value.getValueType()) {
                return false;
            } else {
                this.addValue(attributeName, value);
            }
        }
        return true;
    }

    /**
     * Set a case attribute value specified by attribute name and value
     * @param name
     * @param value
     */
    public void setValue(String name, Value value) {
        if(this.attributeValues.containsKey(name)){
            this.attributeValues.replace(name, value);
        } else {
            this.attributeValues.put(name, value);
        }
    }

    /**
     * Get a case attribute value specified by attribute name
     * @param name
     * @return 
     */
    public Value getValue(String name) {
        if(this.attributeValues.containsKey(name)){
            return this.attributeValues.get(name);
        } else {
            return null;
        }
    }
    
    public boolean isNullValue(String name)
    {
    	Value val = this.getValue(name);
    	if (val == null) return true;
    	if (val.isNullValue()) return true;
    	
    	return false;
    }
 
    /**
     * Get a case attribute value specified by attribute name
     * @param attribute
     * @return 
     */
    public Value getValue(IAttribute attribute) {
        if(attribute.getIsBasic()){
            return attributeValues.get(attribute.getName());
        } else {
            return attribute.getDerivedValue(attributeValues);
        }
    }
    
    /**
     * Add new value
     * @param name
     * @param value
     * @return 
     */
    public boolean addValue(String name, Value value) {
        if(this.caseStructure.getAttributeByName(name).getValueType() 
                == value.getValueType()) {
            this.attributeValues.put(name, value);
        } else {
            return false;
        }
        return true;
    }
    
    /**
     * Set case status
     * @param status 
     */
    public void setCaseStatus(int status) {
        this.caseStatus = status;
    }
    
    /**
     * Get case status
     * @return 
     */
    public int getCaseStatus() {
        return this.caseStatus;
    }
    
    /**
     * Get case structure
     * @return returns a structure of the case
     */
    public CaseStructure getCaseStructure() {
        return this.caseStructure;
    }
    
    /**
     * Get case status as String
     * @return 
     */
    public String getCaseStatusString() {
        String result = "";
        if (caseStatus == 0) {
            result = "Not Processed";
        } else if (caseStatus == 1) {
            result = "Processed";
        }
        return result;
    }
    /**
     * Add conclusion
     * @param conclusion
     */
    public void addConclusion(Conclusion conclusion){
        this.conclusionSet.addConclusion(conclusion);
    }
    
    
    /**
     * Delete conclusion
     * @param conclusion
     */
    public void deleteConclusion(Conclusion conclusion){
        this.conclusionSet.deleteConclusion(conclusion);
    }
    
    
    /**
     * Clear conclusion set.
     */
    public void clearConclusionSet(){
        this.conclusionSet = new ConclusionSet();
    }
    
    
    /**
     * Get conclusion set
     * @return  
     */
    public ConclusionSet getConclusionSet(){
        return this.conclusionSet;
    }
    
    
    /**
     * Set conclusion set
     * @param conclusionSet
     */
    public void setConclusionSet(ConclusionSet conclusionSet){
        this.conclusionSet = conclusionSet;
    }
    
    
    /**
     * Get the case values for setting up a JTable with the data
     *
     * @return an array of strings which represent this case's data
     */
    public Object[] getValuesArray() {
        Object[] caseValuesArray = new String[this.attributeValues.size() + 1];
        //build the array full of case values
        caseValuesArray[0] = this.caseId;
        // Get a set of the entries
        Set set = this.attributeValues.entrySet();
        // Get an iterator
        Iterator i = set.iterator();
        // Display elements
        int idx = 1;
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            Value current = (Value) me.getValue();
            String currentString = current.toString();
            caseValuesArray[idx] = currentString;
            idx++;
        }
        return caseValuesArray;
    }
    
    public Case getRemovingValueFilteredCase(String[] valueFilterArray){
        CaseStructure newCaseStructure = new CaseStructure();
        LinkedHashMap<String, Value> newCaseValues = new LinkedHashMap();
        
        LinkedHashMap<String, Value> caseValues = this.attributeValues;
        Map<String, Value> map = new TreeMap<String, Value>(caseValues); 
        Set values = map.entrySet();
        // Get an iterator
        Iterator valIterator = values.iterator();
        
        while (valIterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) valIterator.next();
            //attribute name
            String attributeName = (String) me2.getKey();
            IAttribute attr =  this.caseStructure.getAttributeByName(attributeName);

            Value aValue = (Value) me2.getValue();
            String valStr = aValue.toString();
            
            boolean filtered = false;
            for (String filterItem : valueFilterArray) {
                if(filterItem.equals(valStr)){        
                    filtered = true;
                    break;
                }
            }
            
            if(!filtered){
                newCaseStructure.addAttribute(attr);
                newCaseValues.put(attributeName, aValue);
            }
        }        
        Case newCase = new Case(this.caseId, newCaseStructure, newCaseValues);
        
        return newCase;
    }
    
    public Case getShowingAttrNameFilteredCase(String attrNameFilter){
        CaseStructure newCaseStructure = new CaseStructure();
        LinkedHashMap<String, Value> newCaseValues = new LinkedHashMap();
        
        LinkedHashMap<String, Value> caseValues = this.attributeValues;
        Map<String, Value> map = new TreeMap<String, Value>(caseValues); 
        Set values = map.entrySet();
        // Get an iterator
        Iterator valIterator = values.iterator();
        
        while (valIterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) valIterator.next();
            //attribute name
            String attributeName = (String) me2.getKey();
            IAttribute attr =  this.caseStructure.getAttributeByName(attributeName);

            Value aValue = (Value) me2.getValue();
            String valStr = aValue.toString();
            
            boolean filtered = false;
            
            if(attributeName.contains(attrNameFilter)){
                filtered = true;
            }
            
            if(filtered){
                newCaseStructure.addAttribute(attr);
                newCaseValues.put(attributeName, aValue);
            }
        }        
        Case newCase = new Case(this.caseId, newCaseStructure, newCaseValues);
        
        return newCase;
    }
    
//    /**
//     * Check whether this instance equals with another instance
//     * @param other
//     * @return 
//     */
//    @Override
//    public boolean equals(Object other)
//    {
//        if (!(other instanceof Case)) 
//        {
//            return false;
//        } 
//        else 
//        {
//            Case c = (Case) other;
//            if (c.getValues().size() != this.getValues().size()) 
//            {
//                return false;	//sizes are different, definitely not equal.
//            }
//            Set set = this.attributeValues.entrySet();
//            Iterator i = set.iterator();
//            while (i.hasNext()) 
//            {
//                Map.Entry me = (Map.Entry) i.next();
//                Value source = (Value) me.getValue(); 
//                Value target = (Value) c.getValue((String)me.getKey()); 
//                if(!source.equals(target)){
//                    return false;
//                } 
//            }
//        }
//        return true;	//found no differences, so equal.
//    }

    /**
     * Check whether this instance equals with another instance
     * @param other
     * @return 
     */
    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Case)) 
        {
            return false;
        } 
        else 
        {
            Case c = (Case) other;
            
            String[] attrNames = this.caseStructure.getAttributeNameArray();
            for (int i = 0; i < attrNames.length; i++)
            {
            	String attrName = attrNames[i];
            	
            	if (this.isNullValue(attrName))
            	{
            		if (c.isNullValue(attrName) == false) return false;
            	}
            	else
            	{
            		if (c.isNullValue(attrName)) return false;
            		
            		if (this.getValue(attrName).equals(c.getValue(attrName)) == false)
            			return false;
            	}
            }
        }
        return true;	//found no differences, so equal.
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.attributeValues);
        return hash;
    }
    
    /**
     * Convert the case into an easily readable text representation
     * @return 
     */
    @Override
    public String toString() {
        String str = new String();        // Get a set of the entries
        Set set = this.attributeValues.entrySet();
        // Get an iterator
        Iterator i = set.iterator();
        str = "Case (" + this.caseId + "): ";
        while (i.hasNext()) 
        {
            Map.Entry me = (Map.Entry) i.next();
            //attribute name
            String attributeName = (String) me.getKey();
            IAttribute attr =  this.caseStructure.getAttributeByName(attributeName);
            
            //attribute value type
            String attributeTypeName = attr.getValueType().getTypeName();
            
            //attribute value
            Value attributeValue = (Value) me.getValue();
            
            if (attributeValue.isNullValue()) continue;
            
            str += attributeName + "(" + attributeTypeName + ") :"+ attributeValue.toString()+"\t";
        }        
        return str;
    }    
    
    public Object[][] toObjectOnlyValue(int attributeAmount) {
        
        // new object for gui
        Object[][] newObject = new Object[attributeAmount][1];
        
        LinkedHashMap<String, Value> caseValues = this.attributeValues;
        Set values = caseValues.entrySet();
        // Get an iterator
        Iterator valIterator = values.iterator();
            
        //count for attribute
        int attrCnt=0;
        while (valIterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) valIterator.next();
            newObject[attrCnt][1] = me2.getValue().toString();

            attrCnt++;
        }
        return newObject;
    }
    
    public Object[][] toObjectOnlyValueWithCaseId(int attributeAmount) {
        
        // new object for gui
        Object[][] newObject = new Object[1][attributeAmount+1];
        
        LinkedHashMap<String, Value> caseValues = this.attributeValues;
        Set values = caseValues.entrySet();
        // Get an iterator
        Iterator valIterator = values.iterator();
            
        //count for attribute
        int attrCnt=1;
        newObject[0][0] = this.caseId;
        while (valIterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) valIterator.next();
            newObject[0][attrCnt] = me2.getValue().toString();

            attrCnt++;
        }
        return newObject;
    }
    
    public Object[][] toObjectForGUIRow(int attributeAmount) {
        
        // new object for gui
        Object[][] newObject = new Object[attributeAmount][2];
        
        LinkedHashMap<String, Value> caseValues = this.attributeValues;
        Set values = caseValues.entrySet();
        // Get an iterator
        Iterator valIterator = values.iterator();
            
        //count for attribute
        int attrCnt=0;
        while (valIterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) valIterator.next();
            //attribute name
            String attributeName = (String) me2.getKey();
            IAttribute attr =  this.caseStructure.getAttributeByName(attributeName);
            
            newObject[attrCnt][0] = attributeName;
            newObject[attrCnt][1] = me2.getValue().toString();

            attrCnt++;
        }
        return newObject;
    }
    
    public Object[][] toObjectForGUIRowWithType(int attributeAmount) {
        // new object for gui
        Object[][] newObject = new Object[attributeAmount][3];
        
        LinkedHashMap<String, Value> caseValues = this.attributeValues;
        Set values = caseValues.entrySet();
        // Get an iterator
        Iterator valIterator = values.iterator();
            
        //count for attribute
        int attrCnt=0;
        while (valIterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) valIterator.next();
            //attribute name
            String attributeName = (String) me2.getKey();
            IAttribute attr =  this.caseStructure.getAttributeByName(attributeName);
            
            newObject[attrCnt][0] = attr.getName();
            //attribute value type
            newObject[attrCnt][1] = attr.getValueType().getTypeName();
            //attribute value
            Value aValue = (Value) me2.getValue();
            newObject[attrCnt][2] = aValue.toString();
            attrCnt++;
        }
        return newObject;
    }
    
    public Object[][] toSortedObjectForGUIRowWithType() {
        int attributeAmount = this.caseStructure.getAttrAmount();
        
        // new object for gui
        Object[][] newObject = new Object[attributeAmount][3];
        
        LinkedHashMap<String, Value> caseValues = this.attributeValues;
        
        Map<String, Value> map = new TreeMap<String, Value>(caseValues); 
        
        Set values = map.entrySet();
        // Get an iterator
        Iterator valIterator = values.iterator();
            
        //count for attribute
        int attrCnt=0;
        while (valIterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) valIterator.next();
            //attribute name
            String attributeName = (String) me2.getKey();
            IAttribute attr =  this.caseStructure.getAttributeByName(attributeName);
            
            newObject[attrCnt][0] = attr.getName();
            //attribute value type
            newObject[attrCnt][1] = attr.getValueType().getTypeName();
            //attribute value
            Value aValue = (Value) me2.getValue();
            newObject[attrCnt][2] = aValue.toString();
            attrCnt++;
        }
        return newObject;
    }
    
    
    public Object[][] toObjectForGUICol(int attributeAmount) {
        
        // new object for gui
        Object[][] newObject = new Object[attributeAmount][2];
        
        LinkedHashMap<String, Value> caseValues = this.attributeValues;
        Set values = caseValues.entrySet();
        // Get an iterator
        Iterator valIterator = values.iterator();
            
        //count for attribute
        int attrCnt=0;
        while (valIterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) valIterator.next();
            newObject[attrCnt][0] = me2.getKey();
            newObject[attrCnt][1] = me2.getValue().toString();

            attrCnt++;
        }
        return newObject;
    }
    
    /** ucciri@gmail.com */
    public void initNullValue()
    {
    	String[] attrNames = this.caseStructure.getAttributeNameArray();
    	
    	for (int i = 0; i < attrNames.length; i++)
    	{
    		String attrName = attrNames[i];
    		ValueType valType = new ValueType(ValueType.NULL_TYPE);
    		Value value = new Value(valType);
    		this.setValue(attrName, value);
    	}
    }
    
    
}
