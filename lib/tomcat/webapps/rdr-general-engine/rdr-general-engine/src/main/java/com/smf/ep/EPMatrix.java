package com.smf.ep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import rdr.logger.Logger;

public class EPMatrix 
{
	/** LMB 코드 */
	String lmbCode;
	
	/** 검사결과 */
	EPTestResult testResult;
	
	/** constructor */
	public EPMatrix()
	{
		testResult = null;
	}
	
	public String getLmbCode()
	{
		return this.lmbCode;
	}
	
	public EPTestResult getTestResult()
	{
		return this.testResult;
	}
	
	public boolean buildInternalData(String pLmbCode,
			                         int pReceiptDate,
						             int pReceiptNo,
						             String pTestCode)
	{
		this.testResult = null;
		
		try
		{
			//ArrayList<EPReceiptDTO> rctList = new ArrayList<EPReceiptDTO>();
			LinkedHashMap<String, String> rctMap = new LinkedHashMap<String, String>();
			EPDBManager.loadReceiptData(pLmbCode, pReceiptDate, pReceiptNo, rctMap);
		
			ArrayList<EPResultDTO> rstList = new ArrayList<EPResultDTO>();
			EPDBManager.loadTestResultData(pLmbCode, pReceiptDate, pReceiptNo, pTestCode, rstList);
			
			this.testResult = new EPTestResult();
			return this.testResult.set(rctMap, rstList);
			
		}
		catch(Exception ex)
		{
			Logger.error(ex.getClass().getName() + ":" + ex.getMessage());
			return false;
		}
		
	}
	
	
	
}
