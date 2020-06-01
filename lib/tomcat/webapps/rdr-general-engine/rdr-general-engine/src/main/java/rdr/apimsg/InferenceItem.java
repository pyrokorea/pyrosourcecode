package rdr.apimsg;

import org.json.simple.JSONObject;

import rdr.rules.Rule;

public class InferenceItem 
{
	private int ruleId;
	
	private int conclusionId;
	
	private String conclusion;
	
	public InferenceItem()
	{
		;
	}
	
	public void set(Rule pRule)
	{
		ruleId = pRule.getRuleId();
		conclusionId = pRule.getConclusion().getConclusionId();
		conclusion = pRule.getConclusion().getConclusionName();
	}
	
	public JSONObject getJSON()
	{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("ruleId", ruleId);
		jsonObj.put("conclusionId", conclusionId);
		jsonObj.put("conclusion", conclusion);
		return jsonObj;
	}

	/**
	 * @return the ruleId
	 */
	public int getRuleId() {
		return ruleId;
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
	 * @param ruleId the ruleId to set
	 */
	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conclusion == null) ? 0 : conclusion.hashCode());
		result = prime * result + conclusionId;
		result = prime * result + ruleId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InferenceItem other = (InferenceItem) obj;
		if (conclusion == null) {
			if (other.conclusion != null)
				return false;
		} else if (!conclusion.equals(other.conclusion))
			return false;
		if (conclusionId != other.conclusionId)
			return false;
		if (ruleId != other.ruleId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InferenceItem [ruleId=" + ruleId + ", conclusionId=" + conclusionId + ", conclusion=" + conclusion
				+ "]";
	}

	

}
