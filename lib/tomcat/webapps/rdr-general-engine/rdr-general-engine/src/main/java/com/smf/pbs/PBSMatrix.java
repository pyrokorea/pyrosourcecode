package com.smf.pbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import rdr.logger.Logger;

public class PBSMatrix 
{
	/** LMB 코드 */
	String lmbCode;
	
	/** 검사결과 */
	PBSTestResult testResult;
	
	/** constructor */
	public PBSMatrix()
	{
		testResult = null;
	}
	
	public String getLmbCode()
	{
		return this.lmbCode;
	}
	
	public PBSTestResult getTestResult()
	{
		return this.testResult;
	}
	
	public boolean buildInternalData(String pLmbCode,
			                         int pReceiptDate,
						             int pReceiptNo,
						             String pTestCode,
						             //String pSpecimenCode,
	                                 int pDecisionSeq)
	{
		this.testResult = null;
		
		try
		{
			//ArrayList<EPReceiptDTO> rctList = new ArrayList<EPReceiptDTO>();
			LinkedHashMap<String, String> rctMap = new LinkedHashMap<String, String>();
			PBSDBManager.loadReceiptData(pLmbCode, pReceiptDate, pReceiptNo, rctMap);
		
			ArrayList<PBSResultDTO> rstList = new ArrayList<PBSResultDTO>();
			PBSDBManager.loadTestResultData(pLmbCode, pReceiptDate, pReceiptNo, pTestCode, rstList);
			
			TreeMap<String, String> defaultDecision = PBSDBManager.getDefaultDecision(pLmbCode, pTestCode);
			TreeMap<String, String> commentGroup = PBSDBManager.getCommentGroup(pLmbCode, pTestCode);
			
			this.testResult = new PBSTestResult();
			return this.testResult.set(rctMap, rstList, defaultDecision, commentGroup, pDecisionSeq);
			
		}
		catch(Exception ex)
		{
			Logger.error(ex.getClass().getName() + ":" + ex.getMessage());
			return false;
		}
		
	}
	
	
	
}
