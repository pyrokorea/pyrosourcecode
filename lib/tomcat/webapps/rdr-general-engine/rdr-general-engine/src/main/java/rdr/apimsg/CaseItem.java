package rdr.apimsg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONObject;

import rdr.api.JSONConverter;
import rdr.api.RDRInterface;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseStructure;
import rdr.logger.Logger;
import rdr.model.IAttribute;
import rdr.model.Value;

public class CaseItem 
{
	private int id; 
	
	//항목명 -> 값
	private LinkedHashMap<String, String> values; 
	
	public CaseItem()
	{
		values = new LinkedHashMap<String, String>();
	}
	
	public void set(CaseStructure aCaseStructure, 
			        Case aCase)
	{
		values = new LinkedHashMap<String, String>();
		
		HashMap<String, Value> valueHashMap = aCase.getValues();
        Map<String, IAttribute> map = new TreeMap<String, IAttribute>(aCaseStructure.getBase()); 
        Set attributes = map.entrySet();
        
        Iterator i = attributes.iterator();
        while (i.hasNext()) 
        {
            Map.Entry me = (Map.Entry) i.next();

            String attributeName = (String) me.getKey();
            IAttribute attribute =  (IAttribute) me.getValue();
            Value aValue = (Value) valueHashMap.get(attributeName);
            
            if (aValue == null || aValue.isNullValue())
            	continue;
              
            this.addValue(attributeName, aValue.toString());
        }   
	}
	
	public JSONObject getJSON()
	{
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("caseId", id);
		
		JSONObject caseObj = new JSONObject();
		Iterator<String> keys = values.keySet().iterator();
		while (keys.hasNext())
		{
			String attrName = keys.next();
			String value = values.get(attrName);
			caseObj.put(attrName, value);
		}
		jsonObj.put("data", caseObj);
		
		return jsonObj;
	}
	
	/** pJsonStr : 항목명->값을 갖는 JSONObject */
	public void buildFromJSON(JSONObject pValueJson)
	{
		values = JSONConverter.convertJSONObjectToValueMap(pValueJson);
	}
	
	/** case 생성 */
	public Case createCase(CaseStructure pCaseStructure)
	{
		String[] msg = new String[1];
    	Case aCase 
    		= RDRInterface.getInstance().getCaseFromValueMap(pCaseStructure, values, msg);
    	
    	if (aCase == null)
    		Logger.error("case creation failed, " + this.toString());
    	
    	return aCase;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the values
	 */
	public LinkedHashMap<String, String> getValues() {
		return values;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public void addValue(String attrName, String value)
	{
		values.put(attrName, value);
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(LinkedHashMap<String, String> values) {
		this.values = values;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CaseItem other = (CaseItem) obj;
		if (id != other.id)
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CaseItem [id=" + id + ", values=" + values + "]";
	}

	
	
}
