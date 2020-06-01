package rdr.apimsg;

import org.json.simple.JSONObject;

import rdr.rules.Condition;

public class ConditionItem 
{
	private String attribute;
	
	private String operator;
	
	private String value;
	
	public ConditionItem()
	{
		;
	}
	
	public boolean isValid()
	{
		if (attribute == null || attribute.isEmpty() ||
		    operator == null || operator.isEmpty())
		{
			return false;
		}
		else return true;
	}
	
	public void set(Condition pCondition)
	{
		attribute = pCondition.getAttribute().getName();
        operator = pCondition.getOperator().getOperatorName();
        value = pCondition.getValue().toString();
	}
	
	public JSONObject getJSON()
	{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("attribute", attribute);
		jsonObj.put("operator", operator);
		jsonObj.put("value", value);
		return jsonObj;
	}
	
	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	public void set(String attribute, String operator, String value)
	{
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
	}

	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ConditionItem other = (ConditionItem) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + attribute + " " + operator + " " + value + ")";
	}
	
}
