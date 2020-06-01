/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.rules;

import java.util.Objects;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.utils.RDRConfig;


/**
 * This class is used to store conclusion
 * @author  David Chung
 */
public class Conclusion {
    
    /**
     * Default conclusion identifier
     */
    public static final int DEFAULT_CONCLUSION_ID = 0;
    /**
     * Default rule identifier
     */
    public static final int NULL_CONCLUSION_ID = -1;
    
    /**
     * Conclusion id.
     */
    private int conclusionId = NULL_CONCLUSION_ID;
    
    /**
     * Conclusion value.
     */
    private Value conclusionValue = null;
    
    /**
     * Default constructor. 
     */
    public Conclusion() {
        this.conclusionValue = null;
    }
    
    /**
     * Constructor
     * @param value 
     */
    public Conclusion(Value value) {
        this.conclusionValue = value;
    }
    
    
    /**
     * Set conclusion id
     * @param conclusionId 
     */
    public void setConclusionId(int conclusionId) {
        this.conclusionId = conclusionId;
    }
  
    
    /**
     * Get conclusion id
     * @return 
     */
    public int getConclusionId() {
        return this.conclusionId;
    }
    
    
    /**
     * Get attribute value
     * @return 
     */
    public Value getConclusionValue() {
        return this.conclusionValue;
    }
    
    /**
     * Set attribute value
     * @param value 
     */
    public void setConclusionValue (Value value) {
        this.conclusionValue = value;
    }
    
    /**
     * Get attribute value
     * @return 
     */
    public String getConclusionName() 
    {
    	if (this.conclusionValue == null)
    		return RDRConfig.getRepNullValueString();
    	else
    		return this.conclusionValue.toString();
    }
    
   /**
     * Check whether two conclusion is equals
     * @param o
     * @return true if the value v is equivalent to this value, false otherwise.
     */
    @Override
    public boolean equals(Object o) 
    {
        if (!(o instanceof Conclusion)) 
        {
            return false;
        } 
        else 
        {
            Conclusion comparingConclusion = (Conclusion) o;
            
            if (this.conclusionValue == null && comparingConclusion.conclusionValue == null)
            	return true;
            
            if (this.conclusionValue == null && comparingConclusion.conclusionValue != null)
            	return false;
            
            if (this.conclusionValue != null && comparingConclusion.conclusionValue == null)
            	return false;
            
            if (!this.conclusionValue.equals(comparingConclusion.conclusionValue))
            {
                return false;
            }
        }
        return true;	//found no problems, so equal.
    }    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.conclusionValue);
        return hash;
    }
    
    
    /**
     * Return string of conclusion
     * @return 
     */
    @Override
    public String toString() 
    {
    	if (this.conclusionValue == null)
    		return RDRConfig.getRepNullValueString();
    	
        String strConclusion = this.conclusionValue.getActualValue().toString();
        return strConclusion;
    }
}
 