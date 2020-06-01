package rdr.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public interface IAttribute {
    public int getAttributeId();
    public void setAttributeId(int attributeId);
    public String getAttributeType();
    public void setAttributeType(String attributeType);
    public Value getValue(); 
    public void setValue(Value value);
    public String getName();
    public void setName(String name);
    public String getDescription();
    public void setDescription(String pDesc);
    public ValueType getValueType();
    public void setValueType(ValueType type);
    public String[] getPotentialOperators(); 
    public boolean getIsBasic();
    public void setIsBasic(boolean isBasic);
    public boolean isThisType(String typeName);
    public boolean isThisType(int typeCode);
    public boolean isAcceptableValue(String value);
    public ArrayList<String> getAttributeList();
    public void setAttributeList(ArrayList<String> attributeList);
    public Value getDerivedValue(HashMap<String, Value> attributeValues);
    public void setCategoricalValues(ArrayList<String> values);
    public boolean addCategoricalValue(String value);
    public ArrayList<String> getCategoricalValues();
    public boolean isValidCategoricalValue(String valStr);
    @Override
    public String toString();
}
