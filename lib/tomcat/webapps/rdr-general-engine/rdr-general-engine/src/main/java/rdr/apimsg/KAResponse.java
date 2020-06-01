package rdr.apimsg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseStructure;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.learner.Learner;
import rdr.logger.Logger;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;
import rdr.rules.RuleSet;

public class KAResponse 
{
	private boolean status;
	
	private String message;
	
	private ArrayList<ConditionItem> conditions;
	
	private String wrongConclusion;
	
	private int validatingCaseCount;
	
	private ArrayList<CaseItem> validatingCases;
	
	/** value : conclusionName_status (status : modify, add, delete) */
	private LinkedHashMap<Integer, ArrayList<String>> otherConclusions;
	
	private HashSet<Integer> wrongRules;
	
	/** construtor */
	public KAResponse()
	{
		this.clear();
	}
	
	public void clear()
	{
		status = true;
		message = "";
		conditions = null;
		wrongConclusion = "";
		validatingCaseCount = 0;
		validatingCases = null;
		otherConclusions = null;
		wrongRules = new HashSet<Integer>();
	}
	
	public void printLog()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("KA Response --------------------");
		sb.append(System.lineSeparator());
		
		sb.append("status : " + (status ? "valid" : "error"));
		sb.append(System.lineSeparator());
		
		sb.append("message : " + message);
		sb.append(System.lineSeparator());
		
		sb.append("condition : ");
		for (int i = 0; i < conditions.size(); i++)
		{
			sb.append(conditions.get(i).toString() + " ");
		}
		sb.append(System.lineSeparator());
		
		sb.append("wrongConclusion : " + wrongConclusion);
		sb.append(System.lineSeparator());
		
		sb.append("validating case count : " + validatingCaseCount);
		sb.append(System.lineSeparator());
		
		sb.append("validating cases");
		sb.append(System.lineSeparator());
		for (int i = 0; i < validatingCases.size(); i++)
		{
			sb.append(validatingCases.get(i).getId());
			sb.append(" : " + validatingCases.get(i).toString());
			sb.append(System.lineSeparator());
		}
		
		sb.append("other conclusions ");
		sb.append(System.lineSeparator());
		Iterator<Integer> keys = otherConclusions.keySet().iterator();
		while (keys.hasNext())
		{
			Integer caseId = keys.next();
			ArrayList<String> conclusions = otherConclusions.get(caseId);
			sb.append(caseId);
			sb.append(" : " + conclusions);
			sb.append(System.lineSeparator());
		}
		
		sb.append("wrong rules : " + wrongRules);
		
		Logger.info(sb.toString());
	}
	
	public JSONObject getJSON()
	{
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("validity", (status ? "valid" : "error"));
		jsonObj.put("msg", message);
		
		if (conditions != null)
		{
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < conditions.size(); i++)
			{
				jsonArray.add(conditions.get(i).getJSON());
			}
			jsonObj.put("usedConditionSet", jsonArray);
		}
		
		jsonObj.put("wrongConclusion", wrongConclusion);
		jsonObj.put("count", validatingCaseCount);
		
		if (validatingCases != null)
		{
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < validatingCases.size(); i++)
			{
				jsonArray.add(validatingCases.get(i).getJSON());
			}
			jsonObj.put("validatingCases", jsonArray);
		}
		
		if (otherConclusions != null)
		{
			JSONArray jsonArray = new JSONArray();
			
			Iterator<Integer> keys = otherConclusions.keySet().iterator();
			while (keys.hasNext())
			{
				Integer caseId = keys.next();
				
				JSONObject caseObj = new JSONObject();
				caseObj.put("caseId", caseId.intValue());
				
				JSONArray conclusionArray = new JSONArray();
				ArrayList<String> conclusions = otherConclusions.get(caseId);
				for (int ci = 0; ci < conclusions.size(); ci++)
				{
					JSONObject conclusionObj = new JSONObject();
					
					//conclusionName_status
					String tmp = conclusions.get(ci);
					String[] tokens = tmp.split("_");
					
					String cn = (tokens.length > 0 ? tokens[0] : "");
					String st = (tokens.length > 1 ? tokens[1] : "");
					conclusionObj.put("conclusion", cn);
					conclusionObj.put("status", st);
					
					conclusionArray.add(conclusionObj);
				}
				caseObj.put("conclusions", conclusionArray);
				jsonArray.add(caseObj);
			}
			jsonObj.put("otherConclusions", jsonArray);
		}
		
		return jsonObj;
	}

	/**
	 * @return the status
	 */
	public boolean getStatus() {
		return status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the wrongConclusion
	 */
	public String getWrongConclusion() {
		return wrongConclusion;
	}

	/**
	 * @return the caseCount
	 */
	public int getValidatingCaseCount() {
		return validatingCaseCount;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param wrongConclusion the wrongConclusion to set
	 */
	public void setWrongConclusion(String wrongConclusion) {
		this.wrongConclusion = wrongConclusion;
	}

	/**
	 * @param caseCount the caseCount to set
	 */
	public void setValidatingCaseCount(int caseCount) {
		this.validatingCaseCount = caseCount;
	}
		
	public void addCondition(Condition pCondition)
	{
		if (conditions == null)
			conditions = new ArrayList<ConditionItem>();
		
		ConditionItem item = new ConditionItem();
		item.set(pCondition);
		conditions.add(item);
	}
	
	public void setConditions(ConditionSet pConditionSet)
	{
		conditions = new ArrayList<ConditionItem>();
		
		Iterator i = pConditionSet.getBase().iterator();
        while (i.hasNext()) 
        {
        	Condition aCondition = (Condition) i.next();
        	ConditionItem item = new ConditionItem();
        	item.set(aCondition);
        	conditions.add(item);
        }
	}
	
	public void addVaidatingCase(CaseStructure pCaseStructure, Case pCase, int pCaseId)
	{
		if (validatingCases == null)
			validatingCases = new ArrayList<CaseItem>();
		
		CaseItem item = new CaseItem();
		item.set(pCaseStructure, pCase);
		item.setId(pCaseId);
		validatingCases.add(item);
	}
	
	public void setValidatingCases(CaseStructure pCaseStructure, 
			                       CornerstoneCaseSet pCaseSet) 
	{
		validatingCases = new ArrayList<CaseItem>();
		
		Set cornerstoneCases = pCaseSet.getBase().entrySet();
		Iterator i = cornerstoneCases.iterator();
        while (i.hasNext()) 
        {
            Map.Entry me = (Map.Entry) i.next();
            int caseId = (int) me.getKey();
            CornerstoneCase aCornerstoneCase = (CornerstoneCase) me.getValue();
            
            CaseItem item = new CaseItem();
    		item.set(pCaseStructure, aCornerstoneCase);
    		item.setId(caseId);
    		validatingCases.add(item);
        }
	}
	
	public void addOtherConclusion(int pId, String pName)
	{
		if (otherConclusions == null)
			otherConclusions = new LinkedHashMap<Integer, ArrayList<String>>();
		
		Integer id = new Integer(pId);
		if (otherConclusions.containsKey(id) == false)
			otherConclusions.put(id, new ArrayList<String>());
		
		otherConclusions.get(id).add(pName);
	}

	/**
	 * @return the conditions
	 */
	public ArrayList<ConditionItem> getConditions() 
	{
		return conditions;
	}

	/**
	 * @return the validatingCases
	 */
	public ArrayList<CaseItem> getValidatingCases() 
	{
		return validatingCases;
	}

	/**
	 * @return the otherConclusions
	 */
	public LinkedHashMap<Integer, ArrayList<String>> getOtherConclusions() 
	{
		return otherConclusions;
	}
	
	public void addWrongRule(Object irObj)
	{
		if (Main.domain.isMCRDR())
    	{
    		RuleSet inferenceResult = (RuleSet)irObj;
    		
    		//wrongConclusion 과 일치하는 결론의 rule만 추가하는 것으로 수정
    		//this.wrongRules = inferenceResult.getRuleIds();
    		
    		Set rs = inferenceResult.getBase().entrySet();
    		Iterator itr = rs.iterator();
    		while (itr.hasNext())
    		{
    			Map.Entry me = (Map.Entry)itr.next();
    			Rule tRule = (Rule)me.getValue();
    			
    			if (tRule.getConclusion().getConclusionName().equals(this.wrongConclusion))
    				this.wrongRules.add(tRule.getRuleId());
    		}
    	}
    	else
    	{
    		Rule inferenceResult = (Rule)irObj;
    		this.wrongRules.add(inferenceResult.getRuleId());
    	}
	}
	
	public boolean hasWrongRule(int pRuleId)
	{
		return this.wrongRules.contains(pRuleId);
	}
	
}
