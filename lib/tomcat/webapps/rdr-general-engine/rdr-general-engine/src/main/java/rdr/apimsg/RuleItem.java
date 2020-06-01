package rdr.apimsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rdr.api.JSONConverter;
import rdr.api.RDRInterface;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;
import rdr.utils.DateUtil;

public class RuleItem 
{
	private int ruleId;
	
	private int parentRuleId;
	
	private int conclusionId;
	
	private String conclusion;
	
	private String conditionStatement;
	
	private ArrayList<ConditionItem> conditions;
	
	private String ruleStatement;
	
	private ArrayList<Integer> cornerstoneCaseIds;
	
	private java.util.Date creationDate;
	
	private java.util.Date updateDate;

	public RuleItem()
	{
		clear();
	}
	
	public void clear()
	{
		ruleId = -1;
		parentRuleId = -1;
		conclusionId = -1;
		conditionStatement = "";
		ruleStatement = "";
		creationDate = null;
		updateDate = null;
		conditions = new ArrayList<ConditionItem>();
		cornerstoneCaseIds = new ArrayList<Integer>();
	}
	
	public void set(Rule pRule)
	{
		conditions = new ArrayList<ConditionItem>();
		
		this.ruleId = pRule.getRuleId();
		
		int parentId = -1;
		if (pRule.getParent() != null)
			parentId = pRule.getParent().getRuleId();
		this.parentRuleId = parentId;
		
		this.conclusionId = pRule.getConclusion().getConclusionId();
		this.conclusion = pRule.getConclusion().getConclusionName();
		ConditionSet tConditionSet = pRule.getConditionSet();
		if (tConditionSet != null) this.conditionStatement = tConditionSet.toString();
		
		if (tConditionSet != null)
		{
			Iterator i = tConditionSet.getBase().iterator();
	        while (i.hasNext()) 
	        {
	            Condition aCondition = (Condition) i.next();
	            
	            ConditionItem item = new ConditionItem();
	    		item.set(aCondition);
	    		this.conditions.add(item);
	        }
		}
		
		this.ruleStatement = pRule.toString();
		
//		int cornerstoneCaseId = -1;
//		if (pRule.getCornerstoneCase() != null) 
//			cornerstoneCaseId = pRule.getCornerstoneCase().getCaseId();
//		this.cornerstoneCaseId = cornerstoneCaseId;
		
		HashMap<Integer, ArrayList<Integer>> ruleCornerstones 
			= RDRInterface.getInstance().getRuleCornerstoneCaseId();
		ArrayList<Integer> ccids = new ArrayList<Integer>();
		Integer id = new Integer(pRule.getRuleId());
		if (ruleCornerstones.containsKey(id))
			cornerstoneCaseIds.addAll(ruleCornerstones.get(id));
		
		this.creationDate = pRule.getCreationDate();
		this.updateDate = pRule.getUpdateDate();
	}
	
	public JSONObject getJSON(boolean bWriteCornerstone, 
                              boolean bWriteDate)
	{
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("id", ruleId);
		jsonObj.put("parentId", (parentRuleId < 0 ? null : parentRuleId));
		jsonObj.put("conclusionId", conclusionId);
		jsonObj.put("conclusion", conclusion);
		jsonObj.put("condition", conditionStatement);
		
		JSONArray conditionJsonArray = new JSONArray();
		for (int i = 0; i < conditions.size(); i++)
		{
			conditionJsonArray.add(conditions.get(i).getJSON());
		}
		jsonObj.put("conditionSet", conditionJsonArray);
		
		jsonObj.put("ruleStatement", ruleStatement);
		
		if (bWriteCornerstone)
		{
			JSONArray ccArray = new JSONArray();
			for (int ci = 0; ci < cornerstoneCaseIds.size(); ci++)
				ccArray.add(cornerstoneCaseIds.get(ci).intValue());
			
			jsonObj.put("cornerstoneCaseId", ccArray);
		}
		
		if (bWriteDate)
		{
			jsonObj.put("creationDate", DateUtil.convert(creationDate));
			jsonObj.put("updateDate", DateUtil.convert(updateDate));
		}
		
		return jsonObj;
	}
	
	public void addCondition(String attr, String op, String val)
	{
		ConditionItem item = new ConditionItem();
		item.set(attr, op, val);
		conditions.add(item);
	}
	
	public ArrayList<ConditionItem> getConditions()
	{
		return conditions;
	}
	
	public ArrayList<String> getConditionAttributes()
	{
		ArrayList<String> attrList = new ArrayList<String>();
		for (int i = 0; i < this.conditions.size(); i++)
		{
			attrList.add(this.conditions.get(i).getAttribute());
		}
		return attrList;
	}

	/**
	 * @return the ruleId
	 */
	public int getRuleId() {
		return ruleId;
	}

	/**
	 * @return the parentRuleId
	 */
	public int getParentRuleId() {
		return parentRuleId;
	}

	/**
	 * @return the conclusionId
	 */
	public int getConclusionId() {
		return conclusionId;
	}

	/**
	 * @return the conclusion
	 */
	public String getConclusion() {
		return conclusion;
	}

	/**
	 * @return the conditionStatement
	 */
	public String getConditionStatement() {
		return conditionStatement;
	}

	/**
	 * @return the ruleStatement
	 */
	public String getRuleStatement() {
		return ruleStatement;
	}

	/**
	 * @return the cornerstoneCaseId
	 */
	public ArrayList<Integer> getCornerstoneCaseIds() {
		return cornerstoneCaseIds;
	}
	
	public java.util.Date getCreationDate() {
		return creationDate;
	}
	
	public java.util.Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param ruleId the ruleId to set
	 */
	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	/**
	 * @param parentRuleId the parentRuleId to set
	 */
	public void setParentRuleId(int parentRuleId) {
		this.parentRuleId = parentRuleId;
	}

	/**
	 * @param conclusionId the conclusionId to set
	 */
	public void setConclusionId(int conclusionId) {
		this.conclusionId = conclusionId;
	}

	/**
	 * @param conclusion the conclusion to set
	 */
	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}

	/**
	 * @param conditionStatement the conditionStatement to set
	 */
	public void setConditionStatement(String conditionStatement) {
		this.conditionStatement = conditionStatement;
	}

	/**
	 * @param ruleStatement the ruleStatement to set
	 */
	public void setRuleStatement(String ruleStatement) {
		this.ruleStatement = ruleStatement;
	}

	/**
	 * @param cornerstoneCaseId the cornerstoneCaseId to set
	 */
	public void setCornerstoneCaseId(ArrayList<Integer> cornerstoneCaseIds) {
		this.cornerstoneCaseIds = cornerstoneCaseIds;
	}
	
	public void setCreationDate(java.util.Date pDate) {
		this.creationDate = pDate;
	}
	
	public void setUpdateDate(java.util.Date pDate) {
		this.updateDate = pDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conclusion == null) ? 0 : conclusion.hashCode());
		result = prime * result + conclusionId;
		result = prime * result + ((conditionStatement == null) ? 0 : conditionStatement.hashCode());
		result = prime * result + parentRuleId;
		result = prime * result + ruleId;
		result = prime * result + ((ruleStatement == null) ? 0 : ruleStatement.hashCode());
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
		RuleItem other = (RuleItem) obj;
		if (conclusion == null) {
			if (other.conclusion != null)
				return false;
		} else if (!conclusion.equals(other.conclusion))
			return false;
		if (conclusionId != other.conclusionId)
			return false;
		if (conditionStatement == null) {
			if (other.conditionStatement != null)
				return false;
		} else if (!conditionStatement.equals(other.conditionStatement))
			return false;
		if (parentRuleId != other.parentRuleId)
			return false;
		if (ruleId != other.ruleId)
			return false;
		if (ruleStatement == null) {
			if (other.ruleStatement != null)
				return false;
		} else if (!ruleStatement.equals(other.ruleStatement))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RuleItem [ruleId=" + ruleId + ", parentRuleId=" + parentRuleId + ", conclusionId=" + conclusionId
				+ ", conclusion=" + conclusion + ", conditionStatement=" + conditionStatement + ", ruleStatement="
				+ ruleStatement + ", cornerstoneCaseIds=" + cornerstoneCaseIds + "]";
	}
	
	
	
	
}
