package com.smf.ep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import rdr.api.RDRBroker;
import rdr.api.RDRBrokerParameters;
import rdr.api.RDRInterface;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseStructure;
import rdr.db.RDRDBManager;
import rdr.logger.Logger;
import rdr.model.Attribute;
import rdr.model.AttributeFactory;
import rdr.model.IAttribute;
import rdr.model.ValueType;
import rdr.utils.RDRConstants;

public class EPRDRBroker extends RDRBroker
{
	public EPRDRBroker()
	{
		;
	}
	
	/** 
	 * params : lmb코드, 접수일자, 접수번호, 검사코드
	 * 
	 */
	@Override
	public Case getCase(RDRBrokerParameters params, String[] msg)
	{
		EPMatrix matrix = new EPMatrix();
		
		if (params.size() < 4)
		{
			msg[0] = "broker params not enouph(need 4) : " + params.toString();
			Logger.error(msg[0]);
			return null;
		}
		
		String lmbCode = params.get(0).toString();
		int rctDate = Integer.parseInt(params.get(1).toString());
		int rctNo = Integer.parseInt(params.get(2).toString());
		String testCode = params.get(3).toString();
		//String specimenCode = params.get(4).toString();
		
		boolean flag = true;
		flag &= matrix.buildInternalData(lmbCode, rctDate, rctNo, testCode);
		
		if (flag = false)
		{
			msg[0] = "matrix data loading failed : " + params.toString();
			Logger.error(msg[0]);
			return null;
		}
		
		ArrayList<String> attrNames = matrix.getTestResult().getTestNames();
		int addedCnt = this.synchronizeCaseStructure(attrNames, Main.domain.getCaseStructure());
		if (addedCnt > 0 )
			RDRInterface.getInstance().reloadDomain();
		else if (addedCnt < 0)
		{
			msg[0] = "case attribute added, but sync(insert) failed";
			return null;
		}
		
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		matrix.getTestResult().getAllValueMap(valueMap);
		
		if (valueMap.isEmpty())
		{
			msg[0] = "test result is not found";
			return null;
		}
			
    	Case aCase 
    		= RDRInterface.getInstance().getCaseFromValueMap(Main.domain.getCaseStructure(), 
    				                                         valueMap, msg);
		if (aCase == null)
			Logger.error(msg[0]);
		
		return aCase;
	}
	
	/** 
	 *  검사결과중 추가된 부속코드(분획)명이 존재하면 사례구조에 등록한다.
	 * @param pAttrNames 추가필요여부를 판단할 항목명
	 * @param pCaseStructure
	 * @return db에 추가된 attribute 개수, 에러시 -1
	 */
	@Override
	public int synchronizeCaseStructure(ArrayList<String> pAttrNames,
			                             CaseStructure pCaseStructure)
	{
		ArrayList<IAttribute> newAttrList = new ArrayList<IAttribute>();
		
		int newAttrId = pCaseStructure.getNewAttributeId();
		for (int i = 0; i < pAttrNames.size(); i++)
		{
			String attrName = pAttrNames.get(i);
			if (pCaseStructure.isAttributeExist(attrName) == false)
			{
				IAttribute newAttr = AttributeFactory.createAttribute(ValueType.CATEGORICAL);
				newAttr.setAttributeId(newAttrId++);
				newAttr.setName(attrName);
				newAttr.setDescription(attrName + " 판정코드");
				newAttr.setAttributeType(Attribute.CASE_TYPE);
				newAttr.setValueType(new ValueType(ValueType.CATEGORICAL));
				
				for (int ci = 0; ci < EPTestResult.decisionCodes.length; ci++)
				{
					newAttr.addCategoricalValue(EPTestResult.decisionCodes[ci]);
				}
				
				newAttrList.add(newAttr);
				Logger.info("sync, newAttribute : " + attrName);
			}
		}
		
		if (newAttrList.isEmpty())
			return 0;
		else
		{
			if (CaseLoader.insertAttribute(newAttrList))
				return newAttrList.size();
			else 
			{
				Logger.error("insert attribute failed");
				return -1;
			}
		}
	}
	 
	@Override
	public ArrayList<String> getFirstLevelAttributes()
	{
		ArrayList<String> attrList = new ArrayList<String>();
		attrList.add("LMB_CODE");
		attrList.add("TEST_CODE");
		return attrList;
	}
	
	/** categorical type 항목 (tv_flag, 판정코드)만 반환 */
	@Override
	public ArrayList<String> getConditionAttributes(CaseStructure pCaseStructure)
	{
		ArrayList<String> attrList = new ArrayList<String>();

		LinkedHashMap<String, IAttribute> attrMap = pCaseStructure.getBase();
		Iterator<String> keys = attrMap.keySet().iterator();
		while (keys.hasNext())
		{
			IAttribute tAttribute = attrMap.get(keys.next());
			String name = tAttribute.getName();
			
			if (name.equals(RDRConstants.RDRClassAttributeName))
				continue;
			
			if (tAttribute.getValueType().getTypeCode() == ValueType.CATEGORICAL)
			{
				attrList.add(name);
			}
		}
		
		return attrList;
	}
	
	/**
	@Override
	public boolean saveInferenceResult(RDRBrokerParameters params, 
			                           String pInferenceResult, 
			                           String pUserId)
	{
		if (params.size() < 5)
		{
			Logger.error("broker params not enouph(need 5) : " + params.toString());
			return false;
		}
		
		String lmbCode = params.get(0).toString();
		int rctDate = Integer.parseInt(params.get(1).toString());
		int rctNo = Integer.parseInt(params.get(2).toString());
		String testCode = params.get(3).toString();
		String specimenCode = params.get(4).toString();
		
		Object[] bindObj = new Object[10];
		bindObj[0] = lmbCode;
		bindObj[1] = new Integer(rctDate);
		bindObj[2] = new Integer(rctNo);
		bindObj[3] = testCode;
		bindObj[4] = specimenCode;
		bindObj[5] = pInferenceResult;
		bindObj[6] = RDRConstants.EmptyString;
		bindObj[7] = pUserId;
		bindObj[8] = pInferenceResult;
		bindObj[9] = pUserId;
							
		boolean flag
			= RDRDBManager.getSecondaryInstance().executeQueryFile("EPTestCommentUpsert.sql", bindObj);
		
		if (flag == false)
		{
			Logger.error("Test Comment Upsert Failed, params : " + params.toString());
		}
		
		return flag;
	}
	*/
	
	
	
	
	
	
	
	
	
	
	
	
}
