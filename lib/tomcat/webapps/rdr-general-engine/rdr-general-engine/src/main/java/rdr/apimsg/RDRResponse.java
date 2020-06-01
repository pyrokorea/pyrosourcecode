package rdr.apimsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rdr.api.RDRInterface;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseStructure;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.logger.Logger;
import rdr.model.IAttribute;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;

public class RDRResponse 
{
	private boolean status;
	
	private String message;
	
	/** for inference */
	private ArrayList<InferenceItem> inferenceResults;
	
	public boolean isInferenceEmpty()
	{
		if (this.inferenceResults == null || this.inferenceResults.isEmpty())
			return true;
		
		for (int i = 0; i < this.inferenceResults.size(); i++)
		{
			if (this.inferenceResults.get(i).getRuleId() == Rule.ROOT_RULE_ID)
				return true;
		}
		
		return false;
	}
	
	/** for inference */
	private ArrayList<RuleItem> firedRules;
	
	/** for getDomain */
	private ArrayList<DomainItem> domains;
	
	/** for getConclusion */
	private ArrayList<ConclusionItem> conclusions;
	
	/** for getRule */
	private ArrayList<RuleItem> rules;
	
	/** for getOperator */
	private HashMap<String, ArrayList<String>> operators;
	
	/** for getCaseStructure */
	private ArrayList<AttributeItem> attrItems;
	
	/** for getCornerstoneCase */
	private ArrayList<CaseItem> cases;
	
	/** for addCornerstoneCase, added rule id */
	private ArrayList<Integer> ruleIds;
	
	/** for getSimilartiy */
	private LinkedHashMap<Integer, Double> similarities;
	
	/** construtor */
	public RDRResponse()
	{
		this.clear();
	}
	
	public void clear()
	{
		status = true;
		message = "";
		inferenceResults = null;
		firedRules = null;
		domains = null;
		conclusions = null;
		rules = null;
		operators = null;
		attrItems = null;
		cases = null;
		ruleIds = null;
		similarities = null;
	}
	
	public void printStatusMessage(String title	)
	{
		Logger.info(title + " : status[" + (status ? "true" : "false") 
				    + "] msg[" + message + "]");
	}
	
	public void printLog()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("RDR Response --------------------");
		sb.append(System.lineSeparator());
		
		sb.append("status : " + (status ? "valid" : "error"));
		sb.append(System.lineSeparator());
		
		sb.append("message : " + message);
		sb.append(System.lineSeparator());
		
		if (inferenceResults != null)
		{
			sb.append("inference result");
			sb.append(System.lineSeparator());
			for (int i = 0; i < inferenceResults.size(); i++)
			{
				sb.append("  " + inferenceResults.get(i).toString());
				sb.append(System.lineSeparator());
			}
		}
		
		if (firedRules != null)
		{
			sb.append("fired rules");
			sb.append(System.lineSeparator());
			for (int i = 0; i < firedRules.size(); i++)
			{
				sb.append("  " + firedRules.get(i).toString());
				sb.append(System.lineSeparator());
			}
		}
		
		if (domains != null)
		{
			sb.append("domain");
			sb.append(System.lineSeparator());
			for (int i = 0; i < domains.size(); i++)
			{
				sb.append("  " + domains.get(i).toString());
				sb.append(System.lineSeparator());
			}
		}
		
		if (conclusions != null)
		{
			sb.append("conclusions");
			sb.append(System.lineSeparator());
			for (int i = 0; i < conclusions.size(); i++)
			{
				sb.append("  " + conclusions.get(i).toString());
				sb.append(System.lineSeparator());
			}
		}
		
		if (rules != null)
		{
			sb.append("rules");
			sb.append(System.lineSeparator());
			for (int i = 0; i < rules.size(); i++)
			{
				sb.append("  " + rules.get(i).toString());
				sb.append(System.lineSeparator());
			}
		}
		
		if (operators != null)
		{
			sb.append("operators ");
			sb.append(System.lineSeparator());
			Iterator<String> keys = operators.keySet().iterator();
			while (keys.hasNext())
			{
				String type = keys.next();
				ArrayList<String> ops = operators.get(type);
				sb.append("  " + type + " : " + ops);
				sb.append(System.lineSeparator());
			}
		}
		
		if (attrItems != null)
		{
			sb.append("attributes");
			sb.append(System.lineSeparator());
			for (int i = 0; i < attrItems.size(); i++)
			{
				sb.append("  " + attrItems.get(i).toString());
				sb.append(System.lineSeparator());
			}
		}
		
		if (cases != null)
		{
			sb.append("cases");
			sb.append(System.lineSeparator());
			for (int i = 0; i < cases.size(); i++)
			{
				sb.append("  " + cases.get(i).toString());
				sb.append(System.lineSeparator());
			}
		}
		
		if (ruleIds != null)
		{
			sb.append("ruleIds : " + ruleIds);
			sb.append(System.lineSeparator());
		}
		
		if (similarities != null)
		{   
			sb.append("similarity : " + similarities);
			sb.append(System.lineSeparator());
		}
		
		Logger.info(sb.toString());
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
	 * @return the inferenceResults
	 */
	public ArrayList<InferenceItem> getInferenceResults() {
		return inferenceResults;
	}

	/**
	 * @return the firedRules
	 */
	public ArrayList<RuleItem> getFiredRules() {
		return firedRules;
	}

	/**
	 * @return the domains
	 */
	public ArrayList<DomainItem> getDomains() {
		return domains;
	}

	/**
	 * @return the conclusions
	 */
	public ArrayList<ConclusionItem> getConclusions() {
		return conclusions;
	}

	/**
	 * @return the rules
	 */
	public ArrayList<RuleItem> getRules() {
		return rules;
	}

	/**
	 * @return the operators
	 */
	public HashMap<String, ArrayList<String>> getOperators() {
		return operators;
	}

	/**
	 * @return the attrItems
	 */
	public ArrayList<AttributeItem> getAttrItems() {
		return attrItems;
	}

	/**
	 * @return the cases
	 */
	public ArrayList<CaseItem> getCases() {
		return cases;
	}

	/**
	 * @return the ruleIds
	 */
	public ArrayList<Integer> getRuleIds() {
		return ruleIds;
	}

	/**
	 * @return the similarities
	 */
	public LinkedHashMap<Integer, Double> getSimilarities() {
		return similarities;
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

	public void addInferenceResult(Rule pRule)
	{
		if (inferenceResults == null)
			inferenceResults = new ArrayList<InferenceItem>();
		
		InferenceItem item = new InferenceItem();
		item.set(pRule);		
		inferenceResults.add(item);
	}
	
	public void addFiredRule(Rule pRule)
	{
		if (firedRules == null)
			firedRules = new ArrayList<RuleItem>();
		
		RuleItem item = new RuleItem();
		item.set(pRule);
		firedRules.add(item);
	}
	
	public void addDomain(String pDomain, String pDesc, String pReasoner)
	{
		if (domains == null)
			domains = new ArrayList<DomainItem>();
		
		DomainItem item = new DomainItem();
		item.set(pDomain, pDesc, pReasoner);
		domains.add(item);
	}
	
	public void addConclusion(int pId, String pName)
	{
		if (conclusions == null)
			conclusions = new ArrayList<ConclusionItem>();
		
		ConclusionItem item = new ConclusionItem();
		item.set(pId, pName);
		conclusions.add(item);
	}
	
	public void addRule(Rule pRule)
	{
		if (rules == null)
			rules = new ArrayList<RuleItem>();
		
		RuleItem item = new RuleItem();
		item.set(pRule);
		rules.add(item);
	}
	
	public void addOperator(String type, String op)
	{
		if (operators == null)
			operators = new HashMap<String, ArrayList<String>>();
		
		if (operators.containsKey(type) == false)
			operators.put(type, new ArrayList<String>());
		
		operators.get(type).add(op);
	}
	
	public void addAttribute(IAttribute pAttribute)
	{
		if (attrItems == null)
			attrItems = new ArrayList<AttributeItem>();
		
		AttributeItem item = new AttributeItem();
		item.set(pAttribute);
		attrItems.add(item);
	}
	
	public void addCase(CaseStructure pCaseStructure, 
			            Case pCase,
			            int pCaseId)
	{
		if (cases == null)
			cases = new ArrayList<CaseItem>();
		
		CaseItem item = new CaseItem();
		item.set(pCaseStructure, pCase);
		item.setId(pCaseId);
		cases.add(item);
	}
	
	public void addRuleId(int pRuleId)
	{
		if (ruleIds == null)
			ruleIds = new ArrayList<Integer>();
		
		ruleIds.add(new Integer(pRuleId));
	}
	
	public void addSimilarity(int caseId, double value)
	{
		if (similarities == null)
			similarities = new LinkedHashMap<Integer, Double>();
		
		similarities.put(new Integer(caseId), new Double(value));
	}
		
	public JSONObject getJSON()
	{
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("validity", (status ? "valid" : "error"));
		jsonObj.put("msg", message);
		
		if (firedRules != null)
		{
			JSONArray frJSONArray = new JSONArray();
			for (int i = 0; i < firedRules.size(); i++)
			{
				frJSONArray.add(firedRules.get(i).getJSON(true, false));
			}
			jsonObj.put("firedRules", frJSONArray);
		}
		
		if (inferenceResults != null)
		{
			JSONArray infJsonArray = new JSONArray();
			for (int i = 0; i < inferenceResults.size(); i++)
			{
				infJsonArray.add(inferenceResults.get(i).getJSON());
			}
			jsonObj.put("inference", infJsonArray);
		}
		
		if (ruleIds != null)
		{
			JSONArray ruleIdArray = new JSONArray();
			for (int i = 0; i < ruleIds.size(); i++)
			{
				ruleIdArray.add(ruleIds.get(i).intValue());
			}
			jsonObj.put("addedRule", ruleIdArray);
		}
				
		return jsonObj;
	}
	
	public JSONArray getJSONDomain()
	{
		JSONArray jsonArray = new JSONArray();
		
		if (domains != null)
		{
			for (int i = 0; i < domains.size(); i++)
			{
				jsonArray.add(domains.get(i).getJSON());
			}
		}
		
		return jsonArray;
	}
	
	public JSONObject getJSONConclusion()
	{
		JSONObject jsonObj = new JSONObject();
		
		if (conclusions == null || conclusions.size() == 0)
		{
			return jsonObj;
		}
		else
		{
			return conclusions.get(0).getJSON();
		}
	}
	
	public JSONArray getJSONConclusions()
	{
		JSONArray jsonArray = new JSONArray();
		
		if (conclusions == null || conclusions.size() == 0)
		{
			return jsonArray;
		}
		else
		{
			for (int i = 0; i < conclusions.size(); i++)
			{
				jsonArray.add(conclusions.get(i).getJSON());
			}
			return jsonArray;
		}
	}
	
	public JSONObject getJSONRule()
	{
		JSONObject jsonObj = new JSONObject();
		
		if (rules == null || rules.size() == 0)
		{
			return jsonObj;
		}
		else
		{
			return rules.get(0).getJSON(true, false);
		}
	}
	
	public JSONArray getJSONRules()
	{
		JSONArray jsonArray = new JSONArray();
		
		if (rules == null || rules.size() == 0)
		{
			return jsonArray;
		}
		else
		{
			for (int i = 0; i < rules.size(); i++)
			{
				jsonArray.add(rules.get(i).getJSON(true, false));
			}
			return jsonArray;
		}
	}
	
	public JSONObject getJSONOperators()
	{
		JSONObject jsonObj = new JSONObject();
		
		if (operators == null || operators.size() == 0)
		{
			return jsonObj;
		}
		else
		{
			//HashMap<String, ArrayList<String>> operators;
			Iterator<String> keys = operators.keySet().iterator();
			while (keys.hasNext())
			{
				String type = keys.next();
				ArrayList<String> opList = operators.get(type);
				
				JSONArray jsonArray = new JSONArray();
				for (int i = 0; i < opList.size(); i++)
				{
					jsonArray.add(opList.get(i));
				}
				
				jsonObj.put(type, jsonArray);
			}
			
			return jsonObj;
		}
	}
	
	public JSONArray getJSONCaseStructure()
	{
		JSONArray jsonArray = new JSONArray();
		
		if (attrItems == null || attrItems.size() == 0)
		{
			return jsonArray;
		}
		else
		{
			for (int i = 0; i < attrItems.size(); i++)
			{
				jsonArray.add(attrItems.get(i).getJSON());
			}
			return jsonArray;
		}
	}
	
	public JSONArray getJSONCases()
	{
		JSONArray jsonArray = new JSONArray();
		
		if (cases == null || cases.size() == 0)
		{
			return jsonArray;
		}
		else
		{
			for (int i = 0; i < cases.size(); i++)
			{
				JSONObject jsonObj = cases.get(i).getJSON();
				jsonArray.add(jsonObj);
			}
			return jsonArray;
		}
	}
	
	public JSONObject getJSONCase()
	{
		JSONObject jsonObj = new JSONObject();
		
		if (cases == null || cases.size() == 0)
		{
			return jsonObj;
		}
		else
		{
			jsonObj = cases.get(0).getJSON();
			return jsonObj;
		}
	}
	
	public JSONObject getJSONSimilarity()
	{
		JSONObject jsonObj = new JSONObject();
				
		jsonObj.put("validity", (status ? "valid" : "error"));
		jsonObj.put("msg", message);
		
		JSONArray listJsonArray = new JSONArray();
		if (similarities == null || similarities.size() == 0)
		{
			return jsonObj;
		}
		else
		{
			Iterator<Integer> keys = similarities.keySet().iterator();
			while (keys.hasNext())
			{
				Integer caseId = keys.next();
				Double value = similarities.get(caseId);
				
				JSONObject simiJsonObj = new JSONObject();
				simiJsonObj.put("caseId", caseId);
				simiJsonObj.put("similarity", value);
				listJsonArray.add(simiJsonObj);
			}
			
			jsonObj.put("list", listJsonArray);
			
			return jsonObj;
		}
	}
	
	/** @todo conclusionId 가 같은 rule 이 두개 이상일 수 있음 */
	public ArrayList<String> getRuleAttributes(int pConclusionId)
	{
		ArrayList<String> attrList = new ArrayList<String>();
		
		for (int i = 0; i < this.firedRules.size(); i++)
		{
			RuleItem rItem = this.firedRules.get(i);
			if (rItem.getConclusionId() == pConclusionId)
			{
				attrList = rItem.getConditionAttributes();
			}
		}
		
		return attrList;
	}
		
}
