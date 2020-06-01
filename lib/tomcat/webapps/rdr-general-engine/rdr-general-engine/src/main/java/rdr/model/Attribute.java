package rdr.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import rdr.logger.Logger;
import rdr.utils.RDRConfig;

/**
 * This class is used to define an attribute
 * 
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class Attribute implements IAttribute {
    
    /**
     * case attribute type
     */
    public static final String CASE_TYPE = "Case Attribute";
    
    /**
     * Class attribute type
     */
    public static final String CLASS_TYPE = "Class Attribute";

    /**
     * Attribute id
     */
    protected int attributeId = -1;
    
    /**
     * Attribute class - case or class attribute
     */
    protected String attributeType = Attribute.CASE_TYPE;
    
    /**
     * Attribute name
     */
    protected String name; // the attribute attributeName
    
    /**
     * Attribute description
     */
    protected String description; 
    
    /**
     * Attribute type
     */
    protected ValueType valueType; 
    
    /**
     * Attribute value
     */
    protected Value value; 
    
    /**
     * basic means?
     */
    protected boolean isBasic = true;
    
    protected ArrayList<String> attributeList;

    /**
     * Constructor - used to assert an attribute
     */
    public Attribute() {
        this.attributeType = Attribute.CASE_TYPE;
        this.name = null;
        this.description = null;
        this.value = null;
        this.valueType = null;
    }    
    
    /**
     * Constructor - used to assert an attribute
     * @param valueTypeCode
     */
    public Attribute(int valueTypeCode) {
        this.attributeType = Attribute.CASE_TYPE;
        this.name = null;
        this.description = null;
        this.value = null;
        this.valueType = new ValueType(valueTypeCode);
    }    
    
    /**
     * Constructor - used to define attribute definition
     * @param attributeType
     * @param name
     * @param valueType
     */
    public Attribute(String attributeType, String name, ValueType valueType) {
        this.attributeType = attributeType;
        this.name = name;
        this.valueType = valueType;
        this.description = null;
    }    

    /**
     * Constructor
     * @param attributeType
     * @param name
     * @param value
     */
    public Attribute(String attributeType, String name, Value value) {
        this.attributeType = attributeType;
        this.name = name;
        this.value = value;
        this.valueType = value.getValueType();
        this.description = null;
    }  

    /**
     * Constructor
     * @param attributeType
     * @param name
     * @param valueTypeCode
     */
    public Attribute(String attributeType, String name, int valueTypeCode) {
        this.attributeType = attributeType;
        this.name = name;
        this.valueType = new ValueType(valueTypeCode);
        this.description = null;
    }  
    
    
    /**
     * Constructor
     * @param attributeType
     * @param names
     * @param valueType
     */
    public Attribute(String attributeType, ArrayList<String> names, 
            ValueType valueType){
        this.attributeType = attributeType;
        this.attributeList = names;
        this.valueType = valueType;    
        this.description = null;
    }
    
    /**
     * Constructor
     * @param attributeType
     * @param names
     * @param valueTypeCode
     */
    public Attribute(String attributeType, ArrayList<String> names, 
            int valueTypeCode){
        this.attributeType = attributeType;
        this.attributeList = names;
        
        this.valueType = new ValueType(valueTypeCode);   
        this.description = null;
    }
    
    @Override
    public int getAttributeId() {
        return this.attributeId;
    }

    @Override
    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }
    
    @Override
    public String getAttributeType() {
        return this.attributeType;
    }

    @Override
    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }
    
    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String pDesc) {
        this.description = pDesc;
    }

    @Override
    public ValueType getValueType() {
        return this.valueType;
    }

    @Override
    public void setValueType(ValueType type) {
        this.valueType = type;
    }

    @Override
    public String[] getPotentialOperators() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean getIsBasic() {
        return this.isBasic;
    }

    @Override
    public void setIsBasic(boolean isBasic) {
        this.isBasic = isBasic;
    }

    @Override
    public Value getDerivedValue(HashMap<String, Value> attributeValues) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ArrayList<String> getAttributeList() {
        return this.attributeList;
    }

    @Override
    public void setAttributeList(ArrayList<String> attributeList) {
        this.attributeList = attributeList;
    }

    @Override
    public void setCategoricalValues(ArrayList<String> values) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean addCategoricalValue(String value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ArrayList<String> getCategoricalValues() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    @Override
    public boolean isValidCategoricalValue(String valStr)
    {
    	throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    @Override
    public boolean isAcceptableValue(String str) 
    {
    	//null value를 나타내는 string인 경우 처리, 20180223
    	if (RDRConfig.isNullValueString(str))
    		return true;
    	
        Object o = null;
        try {
            switch (this.valueType.getTypeCode()) {
                case ValueType.NULL_TYPE:
                    o = (String) str;
                    break;
                case ValueType.CONTINUOUS:
                    if(str.equals("?")){
                        return true;
                    }
                    o = (Double) Double.parseDouble(str);
                    break;
                case ValueType.CATEGORICAL:
                    o = (String) str;
                    break;
                case ValueType.TEXT:
                    o = (String) str;
                    break;
                case ValueType.DATE:
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
                    Date d;
                    try {
                        d = df.parse(str);
                        o = d;
                    } catch (ParseException e) {
                        Logger.error("Could not parse the date element "
                                + "- set to default (2001-01-01 00:00:00.000)!", e);
                        return false;
                    }
                    break;
                case ValueType.BOOLEAN:
                    str=str.toUpperCase();
                    if(str.equals("FALSE") || str.equals("0")){
                        o = "FALSE";
                    } else {
                        o = "TRUE";
                    }
                    break;
                default:
                    o = str;
                    break;
            }
        } catch (NumberFormatException e) {
            Logger.error("Could not parse value "
                    + "[" + str + "] into correct type.", e);
            Logger.info(e.getMessage());
            return false;
        }
        return true;
    }
    
     /**
     * Returns true if typeName 
     *
     * @param typeName the type name
     * @return 
     */
    @Override
    public boolean isThisType(String typeName) {
        typeName = typeName.toUpperCase();
        return this.valueType.getTypeName().equals(typeName);
    }
    
     /**
     * Returns true if typeName 
     *
     * @param typeCode the type code
     * @return 
     */
    @Override
    public boolean isThisType(int typeCode) {
        return this.valueType.getTypeCode()==typeCode;
    }
    
    @Override
    public String toString() {
        String str = this.name;
        
        return str;
    }
}
