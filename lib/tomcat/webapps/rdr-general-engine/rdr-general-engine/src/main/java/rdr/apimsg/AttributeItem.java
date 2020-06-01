package rdr.apimsg;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rdr.model.Attribute;
import rdr.model.AttributeFactory;
import rdr.model.IAttribute;
import rdr.model.ValueType;

public class AttributeItem 
{
	private int id;
	
	private String name;
	
	private String desc;
	
	private String type;
	
	private ArrayList<String> list; //categorical value list
	
	public AttributeItem()
	{
		list = new ArrayList<String>();
	}
	
	public void set(IAttribute pAttribute)
	{
		list = new ArrayList<String>();
		
		id = pAttribute.getAttributeId();
		name = pAttribute.getName();
		desc = pAttribute.getDescription();
		type = pAttribute.getValueType().getTypeName();
		if ( pAttribute.getValueType().getTypeCode() == ValueType.CATEGORICAL )
    	{
        	list = pAttribute.getCategoricalValues();
    	}
	}
	
	public IAttribute createAttribute()
	{
		IAttribute attribute = AttributeFactory.createAttribute(type);
		
		if (attribute.getValueType().getTypeCode() == ValueType.CATEGORICAL )
    	{
    		if (list.isEmpty() == false)
    		{
	    		for (int i = 0; i < list.size(); ++i)
	    		{
	    			String listItem = list.get(i);
	    			attribute.addCategoricalValue(listItem);
	    		}
    		}
    	}
		
		attribute.setAttributeId(id);
		attribute.setName(name);
    	attribute.setDescription(desc);
    	attribute.setAttributeType(Attribute.CASE_TYPE);
    	attribute.setValueType(new ValueType(type));
    	
    	return attribute;
	}
	
	public JSONObject getJSON()
	{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("id", id);
		jsonObj.put("name", name);
		jsonObj.put("desc", desc);
		jsonObj.put("type", type);
		
		if (list != null && list.size() > 0)
		{
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < list.size(); ++i)
        		jsonArray.add(list.get(i));
			
			jsonObj.put("list", jsonArray);
		}
		
		return jsonObj;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the list
	 */
	public ArrayList<String> getList() {
		return list;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + id;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		AttributeItem other = (AttributeItem) obj;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (id != other.id)
			return false;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CaseStructureItem [id=" + id + ", name=" + name + ", desc=" + desc + ", type=" + type + ", list=" + list
				+ "]";
	}
	
	

}
