/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.model;

import java.util.ArrayList;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class BooleanAttribute extends Attribute {
    
    public BooleanAttribute(){
        super(ValueType.BOOLEAN);
    }
    
    public BooleanAttribute(String attributeType, 
            String name, ValueType valueType){
        super(attributeType, name, valueType);
    }
    
    public BooleanAttribute(String attributeType, String name, Value value){
        super(attributeType, name, value);
    } 
    
    public BooleanAttribute(String attributeType, ArrayList<String> names, 
            ValueType valueType){
        super(attributeType, names, valueType);
        this.isBasic = false;
    }
    
    
    @Override
    public String[] getPotentialOperators() {
        String[] operators = {"==", "MISSING", "NOT_MISSING"};
        return operators;
    }    

    
    
}
