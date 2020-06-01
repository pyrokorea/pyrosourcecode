/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rdr.cases.Case;
import rdr.cases.CaseStructure;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.logger.Logger;
import rdr.model.Attribute;
import rdr.model.AttributeFactory;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.model.ValueType;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;
import rdr.utils.DateUtil;
import rdr.utils.StringUtil;


public class JSONConverter {
    
	//----------------------------------------------------------------
	// Case to JSONArray with CaseStructure, Case
	//----------------------------------------------------------------
    public static JSONArray convertCaseToJSONArray( CaseStructure aCaseStructure, 
    		                                        Case aCase) 
    {
        JSONArray aJSONArray = new JSONArray();
        
        HashMap<String, Value> valueHashMap = aCase.getValues();
        Map<String, IAttribute> map = new TreeMap<String, IAttribute>(aCaseStructure.getBase()); 
        Set attributes = map.entrySet();
        
        try 
        {
            // Get an iterator
            Iterator i = attributes.iterator();

            while (i.hasNext()) 
            {
                Map.Entry me = (Map.Entry) i.next();
                //attribute name
                String attributeName = (String) me.getKey();
                IAttribute attribute =  (IAttribute) me.getValue();
                Value aValue = (Value) valueHashMap.get(attributeName);

                if (aValue == null || aValue.isNullValue())
                	continue;
                
                JSONObject aJSONObject = new JSONObject();
                aJSONObject.put("name", attributeName);
                aJSONObject.put("type", attribute.getValueType().getTypeName());
                aJSONObject.put("value", aValue.toString());
                
                aJSONArray.add(aJSONObject);
            }   
            
            return aJSONArray;
            
        } 
        catch (Exception ex) 
        {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex);
        }
        
        return null;
    }

    //----------------------------------------------------------------
  	// Case to JSONObject with CaseStructure, Case
  	//----------------------------------------------------------------
	public static JSONObject convertCaseToJSONObject(CaseStructure aCaseStructure, 
													 Case aCase) 
	{
		JSONObject aJSONObject = new JSONObject();
          
        HashMap<String, Value> valueHashMap = aCase.getValues();
        Map<String, IAttribute> map = new TreeMap<String, IAttribute>(aCaseStructure.getBase()); 
        Set attributes = map.entrySet();
          
        try 
        {
            // Get an iterator
            Iterator i = attributes.iterator();

            while (i.hasNext()) 
            {
                Map.Entry me = (Map.Entry) i.next();
                //attribute name
                String attributeName = (String) me.getKey();
                IAttribute attribute =  (IAttribute) me.getValue();
                Value aValue = (Value) valueHashMap.get(attributeName);
                
                if (aValue == null || aValue.isNullValue())
                	continue;
                  
                aJSONObject.put(attributeName, aValue.toString());
            }   
              
            return aJSONObject;
              
        }
        catch (Exception ex) 
        {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        }
          
        return null;
	}
	
	//----------------------------------------------------------------
  	// Case to JSONObject
  	//----------------------------------------------------------------
//    public static JSONObject convertCaseToJSONObject(Case aCase)
//    {
//        HashMap<String, Value> valueHashMap = aCase.getValues();
//
//        JSONObject aJSONObject = new JSONObject();
//        Set set = valueHashMap.entrySet();
//        // Get an iterator
//        Iterator i = set.iterator();
//        
//        try {
//            while (i.hasNext()) {
//                Map.Entry me = (Map.Entry) i.next();
//                //attribute name
//                String attributeName = (String) me.getKey();
//                Value aValue = (Value) me.getValue();
//
//                aJSONObject.put(attributeName, aValue.toString());
//            }   
//            
//            return aJSONObject;
//            
//        } catch (Exception ex) {
//            Logger.getLogger(JSONConverter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return null;
//    }
    
	//----------------------------------------------------------------
  	// JSONObject to valueMap(key,value)
  	//----------------------------------------------------------------
	public static LinkedHashMap<String, String> convertJSONObjectToValueMap(JSONObject aJSONObject)
	{
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		
		try
		{
			Set<Object> set = aJSONObject.keySet();
			Iterator<Object> iterator = set.iterator();
			while (iterator.hasNext())
			{
				Object obj = iterator.next();
				String key = obj.toString();
				String value = aJSONObject.get(obj).toString();
				
				valueMap.put(key, value);
			}
			
			return valueMap;
		}
		catch (Exception ex) 
        {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        }
		
		return null;
	}
	
    //----------------------------------------------------------------
  	// CaseStructure to JSONArray
  	//----------------------------------------------------------------
    public static JSONArray convertCaseStructureToJSONArray(CaseStructure aCaseStructure, String attrName) 
    {
        JSONArray aJSONArray = new JSONArray();
        
        try
        {
	        LinkedHashMap<String, IAttribute> attrMap = aCaseStructure.getBase();
	        Iterator<String> keyItr = attrMap.keySet().iterator();
	        while (keyItr.hasNext())
	        {
	        	String key = keyItr.next();
	        	IAttribute tAttribute = attrMap.get(key);
	        	
	        	if (attrName != null && 
	                attrName.isEmpty() == false && 
	                attrName.equals(tAttribute.getName()) == false)
	        		continue;
	        	
	        	JSONObject aJSONObj = new JSONObject();
	        	aJSONObj.put("id", tAttribute.getAttributeId());
	        	aJSONObj.put("name", tAttribute.getName());
	        	aJSONObj.put("desc", tAttribute.getDescription());
	        	aJSONObj.put("type", tAttribute.getValueType().getTypeName());
	        	
	        	if ( tAttribute.getValueType().getTypeCode() == ValueType.CATEGORICAL )
	        	{
		        	JSONArray cvJSONArray = new JSONArray();
		        	ArrayList<String> cvList = tAttribute.getCategoricalValues();
		        	for (int i = 0; i < cvList.size(); ++i)
		        		cvJSONArray.add(cvList.get(i));
	        	
		        	aJSONObj.put("list", cvJSONArray );
	        	}
	        	aJSONArray.add(aJSONObj);
	        }
	        
	        return aJSONArray;
        }
        catch (Exception ex) 
        {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        }
        
        return null;
    }
    
    //----------------------------------------------------------------
  	// JSONArray to CaseStructure
  	//----------------------------------------------------------------
    public static CaseStructure convertJSONArrayToCaseStructure(JSONArray aJSONArray) 
    {
    	CaseStructure aCaseStructure = new CaseStructure();
    	
    	for (int i = 0; i < aJSONArray.size(); ++i)
    	{
    		JSONObject aJSONObj = (JSONObject)aJSONArray.get(i);
    		
    		IAttribute attribute = convertJSONObjectToAttribute(aJSONObj);
    		
    		if (!aCaseStructure.isAttributeExist(attribute))
    			aCaseStructure.addAttribute(attribute);
    	}
    	
    	return aCaseStructure;
    }
    
    //----------------------------------------------------------------
  	// JSONObject to Attribute (id는 setting 되지 않음)
  	//---------------------------------------------------------------
    public static IAttribute convertJSONObjectToAttribute(JSONObject aJSONObject)
    {
    	String attrName = aJSONObject.get("name").toString();
    	String valueType = aJSONObject.get("type").toString();
    	
    	String attrDesc = "";
    	if (aJSONObject.get("desc") != null)
    		attrDesc = aJSONObject.get("desc").toString();
    	
    	IAttribute attribute = AttributeFactory.createAttribute(valueType);
    	
    	if (attribute.getValueType().getTypeCode() == ValueType.CATEGORICAL )
    	{
    		JSONArray aJSONArray = (JSONArray)aJSONObject.get("list");
    		
    		if (aJSONArray != null)
    		{
	    		for (int i = 0; i < aJSONArray.size(); ++i)
	    		{
	    			String listItem = aJSONArray.get(i).toString();
	    			attribute.addCategoricalValue(listItem);
	    		}
	    		
	    		if (attribute.getCategoricalValues().size() == 2)
	    		{
	    			if (attribute.getCategoricalValues().contains("true") &&
	    			    attribute.getCategoricalValues().contains("false"))
	    			{
	    				valueType = "Boolean";
	    				attribute = AttributeFactory.createAttribute(valueType);
	    			}
	    		}
    		}
    	}
    	
    	attribute.setName(attrName);
    	attribute.setDescription(attrDesc);
    	attribute.setAttributeType(Attribute.CASE_TYPE);
    	attribute.setValueType(new ValueType(valueType));
    	
    	return attribute;
    }
    
    
    //----------------------------------------------------------------
  	// ConditionSet to JSONArray
  	//----------------------------------------------------------------
    public static JSONArray convertConditionSetToJSONArray(ConditionSet aConditionSet)
    {
        JSONArray returnJSONArray = new JSONArray();
        
        if (aConditionSet == null) return returnJSONArray;

        // Get an iterator
        Iterator i = aConditionSet.getBase().iterator();
        
        while (i.hasNext()) {
            JSONObject aJSONObject = new JSONObject();
            Condition aCondition = (Condition) i.next();
            aJSONObject.put("attribute", aCondition.getAttribute().getName());
            aJSONObject.put("operator", aCondition.getOperator().getOperatorName());
            aJSONObject.put("value", aCondition.getValue().toString());
            
            returnJSONArray.add(aJSONObject);
        }
        
        return returnJSONArray;
    }
    
    //------------------------------------------------------------------------
  	// CornerstoneCaseSet to JSONArray with CaseStructure, CornerstoneCaseSet
  	//------------------------------------------------------------------------
    public static JSONArray convertCornerstoneCaseSetToJSONArray(CaseStructure aCaseStructure, 
    		                                                     CornerstoneCaseSet aCornerstoneCaseSet) 
    {
        JSONArray caseSetJSONArray = new JSONArray();
        
        Set cornerstoneCases = aCornerstoneCaseSet.getBase().entrySet();
        
        // Get an iterator
        Iterator i = cornerstoneCases.iterator();
        while (i.hasNext()) 
        {
            Map.Entry me = (Map.Entry) i.next();
                
            JSONObject caseJSONObject = new JSONObject();
                
            //case id
            int caseId = (int) me.getKey();
                
            try 
            {
                caseJSONObject.put("caseId", caseId);
            } 
            catch (Exception ex) 
            {
                rdr.logger.Logger.error("" + caseId, ex );
            }
                
            JSONObject dataJSONObj = new JSONObject();
            CornerstoneCase aCornerstoneCase = (CornerstoneCase) me.getValue();
            Map<String, IAttribute> map = new TreeMap<String, IAttribute>(aCaseStructure.getBase());
            Set attributes = map.entrySet();
                
            HashMap<String, Value> valueHashMap = aCornerstoneCase.getValues();
                
            Iterator i2 = attributes.iterator();
            while (i2.hasNext()) 
            {
                Map.Entry me2 = (Map.Entry) i2.next();
                    
                //attribute name
                String attributeName = (String) me2.getKey();
                Value aValue = (Value) valueHashMap.get(attributeName);

                if (aValue == null || aValue.isNullValue())
                	continue;
                
                try 
                {
                    dataJSONObj.put(attributeName, aValue.toString());
                } 
                catch (Exception ex) 
                {
                	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
                }
            }
                
            caseJSONObject.put("data", dataJSONObj);
            caseSetJSONArray.add(caseJSONObject);
        }   

        return caseSetJSONArray;
    }
    
    //------------------------------------------------------------------------
  	// Rule to JSONObject
  	//------------------------------------------------------------------------
    public static JSONObject convertRuleToJSONObject(Rule aRule, 
    		                                         boolean bWriteCornerstone, 
    		                                         boolean bWriteDate) 
    {
    	JSONObject aJSONObj = new JSONObject();

		try
		{
			int parentId = -1;
			if (aRule.getParent() != null)
				parentId = aRule.getParent().getRuleId();
						
			aJSONObj.put("id", aRule.getRuleId());
			aJSONObj.put("parentId", (parentId < 0 ? null : parentId));
			aJSONObj.put("conclusionId", aRule.getConclusion().getConclusionId());
			aJSONObj.put("conclusion", aRule.getConclusion().getConclusionName());
			aJSONObj.put("condition", aRule.getConditionSet().toString());
			
			JSONArray conditionJsonArray = JSONConverter.convertConditionSetToJSONArray(aRule.getConditionSet());
			aJSONObj.put("conditionSet", conditionJsonArray);
			aJSONObj.put("ruleStatement", aRule.toString());
			
			if (bWriteCornerstone)
			{
				int cornerstoneCaseId = -1;
				if (aRule.getCornerstoneCase() != null) 
					cornerstoneCaseId = aRule.getCornerstoneCase().getCaseId();
				
				aJSONObj.put("cornerstoneCaseId", (cornerstoneCaseId < 0 ? null : cornerstoneCaseId));
			}
			
			if (bWriteDate)
			{
				aJSONObj.put("creationDate", DateUtil.convert(aRule.getCreationDate()));
				aJSONObj.put("updateDate", DateUtil.convert(aRule.getUpdateDate()));
			}
			
			return aJSONObj;
		}
		catch (Exception ex) 
        {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        }
		
		return null;
    }
    
}
