/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.cases;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import rdr.logger.Logger;
import rdr.model.IAttribute;
import rdr.utils.RDRConstants;
import rdr.db.RDRDBManager;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class CaseSynchroniser {
    
	public CaseSynchroniser() 
	{
		addedAttributes = new ArrayList<String>();
	}
	
	private ArrayList<String> addedAttributes;
	
	public ArrayList<String> getAddedAttributes() { return addedAttributes; }
		
    /**
     * get new structure by comparing dbCaseStructure and arffCaseStructure and insert new attributes into db
     * @param dbCaseStructure
     * @param arffCaseStructure
     * @return 
     */
    public CaseStructure getNewStructureByComparingTwoStructure(CaseStructure arffCaseStructure, 
    		                                                    CaseStructure dbCaseStructure)
    {
    	addedAttributes.clear();
    	
        Logger.info("Comparing previous case structure with arff cases tructure");
        Logger.info("The number of attributes in db case structure : " + dbCaseStructure.getAttrAmount());
        Logger.info("The number of attributes in arff case structure : " + arffCaseStructure.getAttrAmount());
        CaseStructure newCaseStructure = dbCaseStructure;
        
        Set set = arffCaseStructure.getBase().entrySet();
        Iterator i = set.iterator();
        int cnt=0;
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            IAttribute attr = (IAttribute) me.getValue();
            //attr.setAttributeId(newCaseStructure.getAttrAmount());
            attr.setAttributeId(newCaseStructure.getNewAttributeId());
            
            if(newCaseStructure.addAttribute(attr))
            {                
            	ArrayList<IAttribute> attrList = new ArrayList<IAttribute>();
            	attrList.add(attr);
                boolean flag = CaseLoader.insertAttribute(attrList);
                
                Logger.info("CaseSync, added : " + attr.toString());
                
                if (flag)
                {
                	//null value는 db에 저장하지 않음
                	//RDRDBManager.getInstance().insertAttributeToCornerStoneCaseWithNullValue(attr);
                }
                
                cnt++;
                
                addedAttributes.add(attr.getName());
            }
        }
        
        Logger.info("The number of new attributes: " + cnt);
        return newCaseStructure;        
    }
    
    /**
     * compare caseStructure
     * @param dbCaseStructure
     * @param arffCaseStructure
     * @return 양수 : arff에만 존재하는 항목수, 해당항목을 attrNameList에 setting
     *         음수 : db에만 존재하는 항목수를 음수로 변환, 해당항목을 attrNameList에 setting
     *         0  : 동일함
     */
    public int compare(CaseStructure arffCaseStructure, 
    		           CaseStructure dbCaseStructure)
    {
    	ArrayList<String> attrNameList = new ArrayList<String>();
   	
    	Iterator<String> keys = arffCaseStructure.getBase().keySet().iterator();
    	while (keys.hasNext())
    	{
    		String attrName = keys.next();
    		
    		if (attrName.equals(RDRConstants.RDRClassAttributeName))
    			continue;
    		
    		if (dbCaseStructure.isAttributeExist(attrName) == false)
    			attrNameList.add(attrName);
    	}
    	
    	if (attrNameList.isEmpty() == false)
    	{
    		Logger.info("arff, db case structure compare : arff에만 존재하는 항목 [" 
    	                + attrNameList.size() + "] " + attrNameList);
    		return attrNameList.size();
    	}
    	
    	attrNameList.clear();
    	
    	keys = dbCaseStructure.getBase().keySet().iterator();
    	while (keys.hasNext())
    	{
    		String attrName = keys.next();
    		
    		if (attrName.equals(RDRConstants.RDRClassAttributeName))
    			continue;
    		
    		if (arffCaseStructure.isAttributeExist(attrName) == false)
    			attrNameList.add(attrName);
    	}
    	
    	if (attrNameList.isEmpty() == false)
    	{
    		Logger.info("arff, db case structure compare : db 에만 존재하는 항목 ["  
    	                + attrNameList.size() + "] " + attrNameList);
    		return attrNameList.size() * -1;
    	}
    	
    	return 0;
    }
}
