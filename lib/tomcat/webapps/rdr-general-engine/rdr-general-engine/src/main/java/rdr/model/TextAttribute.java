/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.model;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class TextAttribute extends Attribute{
    private int maxValue;
    private int minValue;
    
    public TextAttribute(){
        super(ValueType.TEXT);
    }
    
    public TextAttribute(String attributeType, 
            String name, ValueType valueType){
        super(attributeType, name, valueType);
    }
    
    public TextAttribute(String attributeType, String name, Value value){
        super(attributeType, name, value);
    } 
    
    /**
     * Get maximum value
     * @return 
     */
    public int getMax(){
        return this.maxValue;
    }
    
    /**
     * Set maximum value
     * @param max 
     */
    public void setMax(int max){
        this.maxValue = max;
    }
    
    /**
     * Get minimum value
     * @return 
     */
    public int getMin() {
        return this.minValue;
    }
    
    /**
     * Set max minimum value
     * @param min 
     */
    public void setMin(int min) {
        this.minValue = min;
    }
    
    
    @Override
    public String[] getPotentialOperators() {
        String[] operators = {"==", "!=", "CONTAIN", "NOT CONTAIN", "CONTAIN EXACT TERM", "NOT CONTAIN EXACT TERM", "MISSING", "NOT_MISSING"};
        return operators;
    }    
}
