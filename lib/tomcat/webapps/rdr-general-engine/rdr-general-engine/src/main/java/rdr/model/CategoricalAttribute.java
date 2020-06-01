/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.model;

import java.util.ArrayList;
import rdr.logger.Logger;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class CategoricalAttribute extends Attribute {
    
    /**
     * Allowed value list when the attribute is nominal
     */
    protected ArrayList<String> categoricalValues = new ArrayList<>();
    
    
    public CategoricalAttribute(){
        super(ValueType.CATEGORICAL);
    }
    
    public CategoricalAttribute(String attributeType, 
            String name, ValueType valueType){
        super(attributeType, name, valueType);
    }
    
    public CategoricalAttribute(String attributeType, String name, int valueTypeCode){
        super(attributeType, name, valueTypeCode);
    } 
    
    public CategoricalAttribute(String attributeType, String name, Value value){
        super(attributeType, name, value);
    } 
    
    public CategoricalAttribute(String attributeType, ArrayList<String> names, 
            ValueType valueType){
        super(attributeType, names, valueType);
        this.isBasic = false;
    }    
    
    public CategoricalAttribute(String attributeType, ArrayList<String> names, 
            int valueTypeCode){
        super(attributeType, names, valueTypeCode);
        this.isBasic = false;
    }    
    
    
    /**
     * Get value list for nominal attributes
     *
     * @return
     */
    @Override
    public ArrayList<String> getCategoricalValues() {
        return this.categoricalValues;
    }

    /**
     * Set allowable values
     *
     * @param values
     */
    @Override
    public void setCategoricalValues(ArrayList<String> values) {
        this.categoricalValues = values;
    }

    /**
     * Add Value
     *
     * @param value
     */
    @Override
    public boolean addCategoricalValue(String value) {
        if (this.categoricalValues.contains(value)) {
            return false;
        } else {
            this.categoricalValues.add(value);
        }
        return true;
    }
    
    @Override
    public boolean isValidCategoricalValue(String valStr)
    {
    	return this.categoricalValues.contains(valStr);
    }
    
    @Override
    public String[] getPotentialOperators() {
        String[] operators = {"==", "!=", "CONTAIN", "NOT CONTAIN", "CONTAIN EXACT TERM", "NOT CONTAIN EXACT TERM", "MISSING", "NOT_MISSING"};
        return operators;
    }    
}
