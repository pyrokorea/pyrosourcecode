/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.rules;

import rdr.cases.Case;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rdr.logger.Logger;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.model.ValueType;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class Condition {

    /**
     * Attribute
     */
    private IAttribute attribute;
    
    /**
     * Operator
     */
    private Operator operator;
    
    /**
     * Attribute value
     */
    private Value value;

    /**
     * Constructor.
     */
    public Condition(){
        this.attribute = null;
        this.operator = null;
        this.value = null;
    }
    
    /**
     * Constructor.
     *
     * To be used when you don't yet have an actual Operator object, but do have
     * a Value.
     *
     * @param attr the attribute i.e. age
     * @param op the operator code i.e. Operator.EQUALS
     * @param value
     */
    public Condition(IAttribute attr, int op, Value value) {
        this.attribute = attr;        
        this.operator = new Operator(op);
        this.value = value;
    }

    /**
     * Constructor.
     *
     * To be used when you don't yet have an actual Operator or Value object.
     *
     * @param attr the attribute i.e. age
     * @param op the operator code i.e. Operator.EQUALS
     * @param valueType the type of the comparison value i.e. Type.NUMERIC
     * @param value the comparison value i.e. 25
     */
    public Condition(IAttribute attr, int op, int valueType, Object value) {
        this.attribute = attr;
        this.operator = new Operator(op);
        this.value = new Value(new ValueType(valueType), value);
    }

    /**
     * Constructor.
     *
     * To be used when you already have constructed or know the actual Operator
     * and Value objects.
     *
     * @param attr the attribute i.e. age
     * @param op the actual operator object
     * @param value
     */
    public Condition(IAttribute attr, Operator op, Value value) {
        this.attribute = attr;
        this.operator = op;
        this.value = value;
    }

    /**
     * Constructor.
     *
     * To be used when you have the Operator object, but don't have a Value
     * object.
     *
     * @param attr the attribute i.e. age
     * @param op the actual operator
     * @param valueType the type code of the value i.e. Type.NUMERIC
     * @param value the value i.e. 25
     */
    public Condition(IAttribute attr, Operator op, int valueType, Object value) {
        this.attribute = attr;
        this.operator = op;
        this.value = new Value(new ValueType(valueType), value);
    }
    
    /**
     * Return attribute of this class
     * @return the attribute of this condition
     */
    public IAttribute getAttribute() {
        return this.attribute;
    }

    /**
     * Set the attribute
     *
     * @param attr the attribute to set
     */
    public void setAttribute(IAttribute attr) {
        this.attribute = attr;
    }

    public boolean isUsedAttribute(String attrName)
    {
    	return (this.attribute.getName().equals(attrName));
    }
    
    /**
     * Return operator
     * @return the operator
     */
    public Operator getOperator() {
        return this.operator;
    }

    /**
     * Set the operator
     *
     * @param operator 
     */
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    /**
     * Set the operator
     *
     * @param op the operator code i.e. Operator.LESS_THAN
     */
    public void setOperatorbyCode(int op) {
        this.operator.setOperatorCode(op);
    }

    /**
     *
     * @return the comparison value of this condition
     */
    public Value getValue() {
        return this.value;
    }

    /**
     * set the value of this condition
     *
     * @param value
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * Get the type code of this attribute's value
     *
     * @return type code
     */
    public ValueType getType() {
        return this.value.getValueType();
    }

    /**
     * Sets the type of the value
     *
     * @param typeCode the type code for the comparison value
     */
    public void setValueType(int typeCode) {
        this.value.setValueType(typeCode);
    }

    /**
     * Returns true if the parameter comparingCase has the same values as this object
     *
     * @param o the condition to compare against
     * @return true if the parameter comparingCase has the same values as this object
     */
    @Override
    public boolean equals(Object o) {
        Condition c2;
        if (o == null) {
            return false;
        }

        if (o.getClass() == this.getClass()) {
            c2 = (Condition) o;
            if (!this.toString().equals(c2.toString())) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * hashCode method. Allows this object to be used in HashMaps etc.
     *
     * Constraints: If o1.equals(o2) then o1 should have same hashCode as o2.
     * therefore: hashCode should be calculated based on same parameters as are
     * used to d
     * @return etermine equality.
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * Convert this condition into a readable string
     * @return 
     */
    @Override
    public String toString() {
        String operand = operator.toString();
        String str;
        
        if (operator.getOperatorCode() == Operator.MISSING ||
        	operator.getOperatorCode() == Operator.NOT_MISSING ||
        	operator.getOperatorCode() == Operator.IS_FALSE ||
        	operator.getOperatorCode() == Operator.IS_TRUE)
        {
        	str = "(" + attribute.toString() + " " + operand + ")"; 
        }
        else
        {
        	str = "(" + attribute.toString() + " " + operand + " " + value.toString() + ")";
        }
        return str;
        
    }

    /**
     * Gets the opposite value for the operator
     *
     * @return the opposite operator code i.e. if this condition uses == then it
     * will return the code for !=
     */
    public int getOperatorOpposite() {
        return operator.getOpposite();

    }

    /**
     * Check to see if a case satisfies this condition
     *
     * @param currentCase the case
     * @return true if the case currentCase satisfies this condition, false otherwise
     */
    public boolean isSatisfied(Case currentCase) 
    {
        boolean currentAttributeSatisfied = false;
        //compare the caseValue's value to this attribute's value using this operator.
        
        Value aValue = currentCase.getValue(this.attribute);
        
        /*****************************************************************************************************************/
        /**
         * Checking missing value edited at 25-01-2016 Hyunsuk (David) Chung (davidchung89@gmail.com).
         */
        // if rule operator is checking missing value
        if (getOperator().getOperatorCode()==Operator.MISSING)
        {
        	//항목이 존재하지 않으면 missing value 로 간주함
        	if ( aValue == null)
        		return true;
        	
            //if actual case value is missing (NULL_TYPE), return TRUE
        	if (aValue != null && aValue.getValueType().getTypeCode() == ValueType.NULL_TYPE) {
                return true;
            } 

            return false;
        } 
        // if rule operator is checking not missing value
        else if (getOperator().getOperatorCode()==Operator.NOT_MISSING)
        {
        	//항목이 존재하지 않으면 missing value 로 간주함
        	if (aValue == null)
        		return false;
        	
            //if actual case value is missing (NULL_TYPE), return FALSE
            if(aValue != null && aValue.getValueType().getTypeCode() == ValueType.NULL_TYPE) {
                return false;
            } 

            return true;
        } 
        else 
        {
        	//항목이 존재하지 않으면 missing value 로 간주함
        	if (aValue == null)
        		return false;
        	
            //if rule operator is not checking missing value, but actual case value is missing (NULL_TYPE), return FALSE
            if(aValue != null && aValue.getValueType().getTypeCode() == ValueType.NULL_TYPE)
                return false;
        }
        
        /*****************************************************************************************************************/
        //break it down to functions for each value Type
        switch (this.getValue().getValueType().getTypeCode()) 
        {
            case ValueType.CONTINUOUS:
                currentAttributeSatisfied = isContinuousAttributeSatisfied(currentCase);
                break;
            case ValueType.CATEGORICAL:                
                currentAttributeSatisfied = isCategoricalAttributeSatisfied(currentCase);
                break;
            case ValueType.TEXT:
                currentAttributeSatisfied = isTextAttributeSatisfied(currentCase);
                break;
            case ValueType.DATE:
                currentAttributeSatisfied = isDateAttributeSatisfied(currentCase);
                break;                
            case ValueType.BOOLEAN:
                currentAttributeSatisfied =  isBooleanAttributeSatisfied(currentCase);
                break;
            case ValueType.NULL_TYPE:
                currentAttributeSatisfied =  false;
                break;
            default: // ?? error.
                Logger.error("Doing default for valueType in inference. "
                        + "This shouldn't happen. The type you are trying "
                        + "to use hasn't got a finished implementation yet?");
                break;
        }
        return currentAttributeSatisfied;
    }

    /**
     * is the numeric attribute in case c satisfied by this condition
     *
     * @param c the case to check satisfaction with
     * @return true if the case is satisfied, false otherwise
     */
    public boolean isContinuousAttributeSatisfied(Case c) {
        Double condValue = (Double) this.getValue().getValueObject();        
        IAttribute checkingAttr = this.getAttribute();
        Double caseValueDouble = 0d;       
        
        try {
            if(this.attribute.getIsBasic()){
                Value caseValue = c.getValue(checkingAttr);
                if(caseValue!=null){
                    caseValueDouble = (Double) c.getValue(checkingAttr).getValueObject();
                } else {
                    caseValueDouble = 0d;
                }
            } else {
                Value caseValue = c.getValue(checkingAttr);
                if(caseValue!=null){
                    caseValueDouble = (Double) c.getValue(checkingAttr).getValueObject();
                } else {
                    caseValueDouble = 0d;
                }
            }
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }
        
        switch (getOperator().getOperatorCode()) {
            case Operator.EQUALS:     
                if (Objects.equals(condValue, caseValueDouble)) {
                    return true;
                }
                break;
            case Operator.NOT_EQUALS:
                if (!Objects.equals(condValue, caseValueDouble)) {
                    return true;
                }
                break;
            case Operator.LESS_THAN:
                if (condValue > caseValueDouble) //I know this looks wrong, but look at the order
                {
                    return true;
                }
                break;
            case Operator.GREATER_THAN:
                if (condValue < caseValueDouble) {
                    return true;
                }
                break;
            case Operator.LESS_THAN_EQUALS:
                if (condValue >= caseValueDouble) {
                    return true;
                }
                break;
            case Operator.GREATER_THAN_EQUALS:
                if (condValue <= caseValueDouble) {
                    return true;
                }
                break;
        }
        return false;
    }

    
    /**
     * is the date attribute in case c satisfied by this condition
     *
     * @param c the case to check satisfaction with
     * @return true if the case is satisfied, false otherwise
     */
    public boolean isDateAttributeSatisfied(Case c) {
        Date condValue = (Date) this.getValue().getValueObject();
        Date caseValue = null;
        try {
            caseValue = (Date) c.getValue(this.attribute).getValueObject();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }
       //System.out.println("Is " + caseValue + getOperator().toString() + condValue + "?");
        switch (getOperator().getOperatorCode()) {
            case Operator.EQUALS:
                if (condValue.equals(caseValue)) {
                    return true;
                }
                break;
            case Operator.NOT_EQUALS:
                if (!condValue.equals(caseValue)) {
                    return true;
                }
                break;
            case Operator.BEFORE:
                if (condValue.before(caseValue)) {
                    return true;
                }
                break;
            case Operator.AFTER:
                if (condValue.after(caseValue)) {
                    return true;
                }
                break;
            case Operator.BEFORE_EQUALS:
                if (condValue.before(caseValue)|condValue.equals(caseValue)) {
                    return true;
                }
                break;
            case Operator.AFTER_EQUALS:
                if (condValue.after(caseValue)|condValue.equals(caseValue)) {
                    return true;
                }
                break;
            case Operator.MISSING:

                break;
        }
        //System.out.println("Found false");
        return false;
    }

    /**
     * is the numeric attribute in case c satisfied by this condition
     *
     * @param c the case to check satisfaction with
     * @return true if the case is satisfied, false otherwise
     */
    public boolean isBooleanAttributeSatisfied(Case c) {
        boolean condValue = (boolean) this.getValue().getValueObject();        
        IAttribute checkingAttr = this.getAttribute();
        boolean caseValue=true;
        try {
            if(this.attribute.getIsBasic()){
               caseValue = (boolean) c.getValue(checkingAttr).getValueObject();
            } else {
               caseValue = (boolean) c.getValue(checkingAttr).getValueObject();
            }
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }
        //System.out.println("Is " + caseValue + getOperator().toString() + condValue + "?");
        switch (getOperator().getOperatorCode()) {
            case Operator.EQUALS:                
                if (Objects.equals(condValue, caseValue)) {
                    return true;
                }
                break;
            case Operator.NOT_EQUALS:
                if (!Objects.equals(condValue, caseValue)) {
                    return true;
                }
        }
        return false;
    }

    
    
    /**
     * Is the nominal attribute in this condition satisfied by the case c TODO:
     * method is untested.
     *
     * @param c the case to test
     * @return true if the case is satisfied, false otherwise
     */
    public boolean isCategoricalAttributeSatisfied(Case c) {
        String condValue = (String) this.getValue().getValueObject();
        Value caseValue = c.getValue(this.attribute);
        
        String caseValueStr = "";
        
        if(caseValue!=null){
            caseValueStr = (String) caseValue.getValueObject();
        } else {
            caseValueStr = "";
        }
        
        switch (getOperator().getOperatorCode()) {
            case Operator.EQUALS:
                if (condValue.equals(caseValueStr) || 
                        (condValue.equals("1") && caseValueStr.equals("1.0")) || 
                        (condValue.equals("0") && caseValueStr.equals("0.0"))) {
                    return true;
                }
                break;
            case Operator.NOT_EQUALS:
                if (!(condValue.equals(caseValueStr) || 
                        (condValue.equals("1") && caseValueStr.equals("1.0")) || 
                        (condValue.equals("0") && caseValueStr.equals("0.0")))) {
                    return true;
                }
                break;
            case Operator.CONTAIN:
                if (caseValueStr.contains(condValue)) {
                    return true;
                } 
                break;
            case Operator.NOT_CONTAIN:
                if (!caseValueStr.contains(condValue)) {
                    return true;
                } 
                break;      
            case Operator.CONTAIN_EXACT_TERM:
                if (Condition.isContainExactTerm(caseValueStr, condValue)) {
                    return true;
                } 
                break;
            case Operator.NOT_CONTAIN_EXACT_TERM:
                if (!Condition.isContainExactTerm(caseValueStr, condValue)) {
                    return true;
                } 
        }
        return false;
    }
    
    
    public boolean isTextAttributeSatisfied(Case c) {        
        String condValue = (String) this.getValue().getValueObject();
        
        Value caseValue = c.getValue(this.attribute);
        
        String caseValueStr = "";
        
        if(caseValue!=null){
            caseValueStr = (String) caseValue.getValueObject();
        } else {
            caseValueStr = "";
        }
        
        switch (getOperator().getOperatorCode()) {
            case Operator.EQUALS:
                if (condValue.equals(caseValueStr)) {
                    return true;
                }
                break;
            case Operator.NOT_EQUALS:
                if (!condValue.equals(caseValueStr)) {
                    return true;
                }
                break;
            case Operator.CONTAIN:
                if (caseValueStr.contains(condValue)) {
                    return true;
                } 
                break;
            case Operator.NOT_CONTAIN:
                if (!caseValueStr.contains(condValue)) {
                    return true;
                } 
                break;      
            case Operator.CONTAIN_EXACT_TERM:
                if (Condition.isContainExactTerm(caseValueStr, condValue)) {
                    return true;
                } 
                break;
            case Operator.NOT_CONTAIN_EXACT_TERM:
                if (!Condition.isContainExactTerm(caseValueStr, condValue)) {
                    return true;
                } 
                break;            
        }
        return false;
    }

    private static boolean isContainExactTerm(String caseValue, String condValue){
         String[] caseValueArray = caseValue.split(" ");
         for(int i=0; i<caseValueArray.length; i++){
             if(caseValueArray[i].equals(condValue)){
                 return true;
             }
         }
         return false;
    }
    
}
