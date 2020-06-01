package com.smf.pbs;

import rdr.workbench.ReportRuleTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rdr.utils.RDRConstants;
import rdr.workbench.InferencePathItem;

public class PBSReportRuleTree extends ReportRuleTree
{
	public PBSReportRuleTree()
	{
		super();
	}
	
	public void addTestCodeFiltering(String pTestCode)
	{
		addFiltering("TEST_CODE", pTestCode);
	}
	
	public String getReportJSON()
	{
		buildPathItemsTraverse();
		setHeader();
		
		TreeMap<String, ArrayList<InferencePathItem>> piMap 
			= new TreeMap<String, ArrayList<InferencePathItem>>();
		
		for (int i = 0; i < pathItems.size(); i++)
		{
			String cn = pathItems.get(i).getConclusion();
			if (piMap.containsKey(cn) == false)
				piMap.put(cn, new ArrayList<InferencePathItem>());
				
			piMap.get(cn).add(pathItems.get(i));
		}
		
		JSONArray rtnJSONArr = new JSONArray();
		
		Iterator<String> iter = piMap.keySet().iterator();
		while (iter.hasNext())
		{
			String cn = iter.next();
			ArrayList<InferencePathItem> tList = piMap.get(cn);
			
			JSONObject tJSONObj = new JSONObject();
			tJSONObj.put("conclusion", cn);
			
			TreeSet<String> tcSet = new TreeSet<String>();
			JSONArray arr = new JSONArray();
			for (int ti = 0; ti < tList.size(); ti++)
			{
				arr.add(tList.get(ti).getJSON());
				
				String tc = tList.get(ti).getValueString("TEST_CODE");
				if (tc.equals(RDRConstants.EmptyString) == false)
					tcSet.add(tc);
			}
			tJSONObj.put("data", arr);
			
			JSONArray arr2 = new JSONArray();
			Iterator<String> tciter = tcSet.iterator();
			while (tciter.hasNext())
			{
				arr2.add(tciter.next());
			}
			
			tJSONObj.put("testCode",  arr2);
			
			rtnJSONArr.add(tJSONObj);
			
		}
		
		return rtnJSONArr.toString();
	}
}
