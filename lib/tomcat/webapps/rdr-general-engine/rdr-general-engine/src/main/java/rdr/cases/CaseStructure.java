/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.cases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import rdr.model.Attribute;
import rdr.model.AttributeSet;
import rdr.model.CategoricalAttribute;
import rdr.model.IAttribute;
import rdr.model.ValueType;
import rdr.rules.Conclusion;

/**
 * This class is used to define a structure of case
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public final class CaseStructure  {
    /**
     * Case Structure.
     */
    private LinkedHashMap<String, IAttribute> caseStructure;
    
    /**
     * Attribute Name for Case Structure.
     */
    private String[] caseStructureAttrNameArray;
    
    /**
     * Constructor.
     */
    public CaseStructure(){
        caseStructure = new LinkedHashMap<>();
        
    }
    
    /**
     * Constructor with an Case Structure
     * @param attributeSet
     */
    public CaseStructure(AttributeSet attributeSet) {
        this.caseStructure = attributeSet.getBase();        
        caseStructureAttrNameArray = attributeSet.getAttributeNameArray();
    }
    
    
    /**
     * Get Case Structure
     * @return 
     */
    public LinkedHashMap<String, IAttribute>  getBase(){
        return this.caseStructure;
    }
    
    /**
     * Get Attribute Amount
     * @return 
     */
    public int getAttrAmount(){
        return this.caseStructure.size();
    }
    
    public LinkedHashMap<String, Attribute> constructStructure(HashMap<Integer, String> hashMapName, HashMap<Integer, String> hashMapType){
        
        LinkedHashMap<String, Attribute> newCaseStructure = new LinkedHashMap<>();
        int hashMapSize = hashMapName.size();
        caseStructureAttrNameArray = new String[hashMapSize];
        for(int i=0;i<hashMapSize;i++){
            String attributeName = hashMapName.get(i);
            String attributeType = hashMapType.get(i);
            ValueType valueType = new ValueType(attributeType);
            Attribute attribute = new Attribute("Case Attribute", attributeName, valueType);
            
            caseStructureAttrNameArray[i] = attributeName;
            newCaseStructure.put(attributeName, attribute);
        }
        return newCaseStructure;
    }
    
    
    /**
     * Get attribute by name
     * @param attributeName
     * @return 
     */
    public IAttribute getAttributeByName(String attributeName){
        return this.caseStructure.get(attributeName);
    }    
    
    
    /**
     * Get attribute by attribute id
     * @param attributeId
     * @return 
     */
    public IAttribute getAttributeByAttrId(int attributeId)
    {
        IAttribute attr = null;

        Set set = this.caseStructure.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            attr = (IAttribute) me.getValue();
            if(attr.getAttributeId()==attributeId)
            {
            	return attr;
            }
        }
        
        //id not found
        return null;
    }    
    
    /**
     * attribute 추가를 위한 max id 반환
     * @param 
     * @return 
     */
    public int getNewAttributeId()
    {
    	if (this.getAttrAmount() > 0)
    	{
    		int maxId = 0;
    		IAttribute attr = null;
    		Set set = this.caseStructure.entrySet();
	        Iterator i = set.iterator();
	        while (i.hasNext()) 
	        {
	            Map.Entry me = (Map.Entry) i.next();
	            attr = (IAttribute) me.getValue();
	            maxId = Math.max(maxId, attr.getAttributeId());
	        }
	        return maxId+1;
    	}
    	else
    	{
    		return 1;
    	}
    }
    
    
    /**
     * Add an attribute to attribute set
     * @param attribute
     * @return 
     */
    public boolean addAttribute(IAttribute attribute) {
        if(this.caseStructure.containsKey(attribute.getName())){
            return false;
        } else {
        	/** id를 caseStructure.size()에서 getNewAttributeId로 수정함
        	 *  그러면 초기 id를  -1로 하고 -1인 경우 id setting해야 함
        	 */
            if(attribute.getAttributeId() < 0){
            	attribute.setAttributeId(this.getNewAttributeId());
            }
            this.caseStructure.put(attribute.getName(), attribute);
        }
        return true;
    }
    
    /**
     * Delete single attribute
     * @param attribute
     * @return 
     */
    public boolean deleteAttribute(IAttribute attribute) {
        if(!this.caseStructure.containsKey(attribute.getName())){
            return false;
        } else {
            this.caseStructure.remove(attribute.getName());
        }
        return true;
    }
    
    /**
     * Delete single attribute by name
     * @param attrName
     * @return 
     */
    public boolean deleteAttributeByName(String attrName) {
        if(!this.caseStructure.containsKey(attrName)){
            return false;
        } else {
            this.caseStructure.remove(attrName);
        }
        return true;
    }
    
    /**
     * Check whether given attribute exist in case structure
     * @param attribute
     * @return 
     */
    public boolean isAttributeExist(IAttribute attribute) {
        if(this.caseStructure.containsKey(attribute.getName())){
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Check whether given attribute exist in case structure
     * @param attributeName
     * @return 
     */
    public boolean isAttributeExist(String attributeName) {
        if(this.caseStructure.containsKey(attributeName)){
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Get string array of attribute name
     * @return 
     */
    public String[] getAttributeNameArrayWithCaseId() {
        String[] attrArray = new String[this.caseStructure.size()+1];
        Set set = this.caseStructure.entrySet();
        attrArray[0] = "Case ID";
        Iterator i = set.iterator();
        int idx = 1;
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            IAttribute attr = (IAttribute) me.getValue();
            attrArray[idx] = attr.getName() + " [" + attr.getValueType().getTypeName() + "]";
            idx++;
        }        
        return attrArray;
    } 
    
    /**
     * Get string array of attribute name
     * @return 
     */
    public String[] getSortedAttributeNameArrayWithCaseId() {
        String[] attrArray = new String[this.caseStructure.size()+1];
        
        Map<String, IAttribute> map = new TreeMap<String, IAttribute>(this.caseStructure); 
        Set attributes = map.entrySet();
        
        attrArray[0] = "Case ID";
        
        // Get an iterator
        Iterator attributeIterator = attributes.iterator();
        
        int idx = 1;
        while (attributeIterator.hasNext()) {
            Map.Entry me = (Map.Entry) attributeIterator.next();
            IAttribute attr = (IAttribute)me.getValue();
            attrArray[idx] = attr.getName() + " [" + attr.getValueType().getTypeName() + "]";
            idx++;
        }
        return attrArray;
    } 
    
    /**
     * Get string array of attribute name
     * @return 
     */
    public String[] getAttributeNameArray() {
        String[] attrArray = new String[this.caseStructure.size()];
        Set set = this.caseStructure.entrySet();
        
        Iterator i = set.iterator();
        int idx = 0;
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            IAttribute attr = (IAttribute) me.getValue();
            attrArray[idx] = attr.getName();
            idx++;
        }        
        return attrArray;
    } 
    
    /**
     * Get string array of attribute name
     * @return 
     */
    public String[] getAttributeNameArraySortedById() 
    {
        String[] attrArray = new String[this.caseStructure.size()];
        
        Map<Integer, IAttribute> map = new TreeMap<Integer, IAttribute>();
        Iterator<String> keys = this.caseStructure.keySet().iterator();
        while (keys.hasNext())
        {
        	IAttribute attr = this.caseStructure.get(keys.next());
        	map.put(attr.getAttributeId(), attr);
        }

        Set set = map.entrySet();
        
        Iterator i = set.iterator();
        int idx = 0;
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            IAttribute attr = (IAttribute) me.getValue();
            attrArray[idx] = attr.getName();
            idx++;
        }        
        return attrArray;
    } 
    
    /**
     * Get sorted string array of attribute name with the given name in the front
     * @param otherName
     * @return 
     */
    public String[] getSortedAttributeNameArrayWithOtherName(String otherName) {
        String[] attrArray = new String[this.caseStructure.size()+1];
        
        Map<String, IAttribute> map = new TreeMap<String, IAttribute>(this.caseStructure); 
        Set attributes = map.entrySet();
        
        attrArray[0] = otherName;
        
        // Get an iterator
        Iterator attributeIterator = attributes.iterator();
        
        int idx = 1;
        while (attributeIterator.hasNext()) {
            Map.Entry me = (Map.Entry) attributeIterator.next();
            IAttribute attr = (IAttribute)me.getValue();
            attrArray[idx] = attr.getName() + " [" + attr.getValueType().getTypeName() + "]";
            idx++;
        }
        return attrArray;
    } 
    
    /**
     * Get filtered and sorted string array of attribute name with the given name in the front
     * @param otherName
     * @return 
     */
    public String[] getFilteredSortedAttributeNameArrayWithOtherName(String filter, String otherName) {
        String[] filterAttrArray = new String[this.caseStructure.size()+1];
        
        Map<String, IAttribute> map = new TreeMap<String, IAttribute>(this.caseStructure); 
        Set attributes = map.entrySet();
        
        filterAttrArray[0] = otherName;
        
        // Get an iterator
        Iterator attributeIterator = attributes.iterator();
        
        int idx = 1;
        while (attributeIterator.hasNext()) {
            Map.Entry me = (Map.Entry) attributeIterator.next();
            IAttribute attr = (IAttribute)me.getValue();
            if(attr.getName().contains(filter)){
                filterAttrArray[idx] = attr.getName() + " [" + attr.getValueType().getTypeName() + "]";
                idx++;
            }
        }
        
        String[] attrArray = new String[idx];
        attrArray[0] = otherName;
        for(int i=0; i<idx; i++){
            attrArray[i] = filterAttrArray[i];
        }
        
        return attrArray;
    } 
    
    public String toString(){
        String str = "";
        Set set = this.caseStructure.entrySet();
        
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            IAttribute attr = (IAttribute) me.getValue();
                        
            //20170912 modified by ucciri, add categorical value list
            //str += attr.getName() + "[" + attr.getValueType().getTypeName() + "] \n";
            str += attr.getName() + "[" + attr.getValueType().getTypeName() + "] ";
            if ( attr.isThisType("CATEGORICAL"))
            {
            	CategoricalAttribute catAttr = (CategoricalAttribute)attr;
            	ArrayList<String> cl = catAttr.getCategoricalValues();
            	for ( int idx = 0; idx < cl.size(); idx++)
            		str += cl.get(idx) + ",";
            }
            
            str += "\n";
        }        
        
        
        return str;
    }
    
}
