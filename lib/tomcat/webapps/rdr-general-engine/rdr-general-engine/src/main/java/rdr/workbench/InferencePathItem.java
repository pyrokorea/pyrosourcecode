package rdr.workbench;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONObject;

import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Operator;
import rdr.utils.RDRConstants;

public class InferencePathItem 
{
	/** key : attributeName 
	 *  value : operator + value
	 */
	private HashMap<String, String> attrValue;
	
	private String conclusion;
	
	public InferencePathItem()
	{
		clear();
	}
	
	public void clear()
	{
		attrValue = new HashMap<String, String>();
	}
	
	public HashSet<String> getKeySet()
	{
		return new HashSet(attrValue.keySet());
	}
	
	public void setConclusion(String pConclusion)
	{
		conclusion = pConclusion;
	}
	
	public String getConclusion()
	{
		return conclusion;
	}
	
	public String getValueString(String attrName)
	{
		if (attrValue.containsKey(attrName))
			return attrValue.get(attrName);
		else
			return RDRConstants.EmptyString;
	}
	
	public void addConditions(ConditionSet pConditionSet)
	{
		//key : attributeName
		HashMap<String, ArrayList<Condition>> conditionMap 
			= new HashMap<String, ArrayList<Condition>>();
		
		HashSet<Condition> tSet = pConditionSet.getBase();
		Iterator<Condition> iter = tSet.iterator();
		while (iter.hasNext())
		{
			Condition tCondition = iter.next();
			String attrName = tCondition.getAttribute().getName();
			if (conditionMap.containsKey(attrName) == false)
			{
				ArrayList<Condition> tmp = new ArrayList<Condition>();
				conditionMap.put(attrName, tmp);
			}
				
			conditionMap.get(attrName).add(tCondition);
		}
		
		Iterator<String> keys = conditionMap.keySet().iterator();
		while (keys.hasNext())
		{
			String attrName = keys.next();
			ArrayList<Condition> cList = conditionMap.get(attrName);
			String opVal = "";
			for (int i = 0; i < cList.size(); i++)
			{
				if (i > 0) opVal += " & ";
				
				Condition tCondition = cList.get(i);
				if (tCondition.getOperator().getOperatorCode() == Operator.EQUALS)
				{
					opVal += tCondition.getValue().toString();
				}
				else
				{
					opVal += (tCondition.getOperator().getOperatorName() + " " +
							  tCondition.getValue().toString());
				}
			}
			
			attrValue.put(attrName, opVal);
		}
	}
	
	public JSONObject getJSON()
	{
		JSONObject jsonObj = new JSONObject();
		Iterator<String> iter = attrValue.keySet().iterator();
		while (iter.hasNext())
		{
			String attr = iter.next();
			String val = attrValue.get(attr); 
			jsonObj.put(attr, val);
		}
		
		return jsonObj;
	}
	
	
	
	
}
