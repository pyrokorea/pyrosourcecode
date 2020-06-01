package com.smf.ep;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import rdr.logger.Logger;
import rdr.db.RDRDBManager;
import rdr.utils.RDRConstants;
import rdr.utils.Utility;


public class EPDBManager 
{
	/** 검사 접수정보 Loading */
	public static void loadReceiptData(String pLmbCode,
                                	   int pReceiptDate,
			                           int pReceiptNo,
			                           //ArrayList<EPReceiptDTO> dl,
			                           LinkedHashMap<String, String> pMap)
	{
		//if (dl == null || pMap == null) return;
		if (pMap == null) return;

		String sqlFn = "EPReceiptDataSelect.sql";

		Object bindObj[] = new Object[3];
		bindObj[0] = pLmbCode;
		bindObj[1] = new Integer(pReceiptDate);
		bindObj[2] = new Integer(pReceiptNo);

		LinkedHashMap<String, String> valueMap
			= RDRDBManager.getSecondaryInstance().executeSelectQueryFileToMap(sqlFn, bindObj);
				
		pMap.putAll(valueMap);
		Logger.info("검사 접수정보 receiptDate[" + pReceiptDate +
				      "] receiptNo[" + pReceiptNo + "] loaded(col-val map) : " + pMap.size());
	}
	
	/** 검사 결과정보 Loading */
	public static void loadTestResultData(String pLmbCode,
                                   int pReceiptDate,
							       int pReceiptNo,
							       String pTestCode,
							       ArrayList<EPResultDTO> dl)
	{
		if (dl == null) return;

		String sqlFn = "EPResultDataSelect.sql";

		Object bindObj[] = new Object[4];
		bindObj[0] = pLmbCode;
		bindObj[1] = new Integer(pReceiptDate);
		bindObj[2] = new Integer(pReceiptNo);
		bindObj[3] = pTestCode;

		try
		{
			ArrayList<String[]> tList
				= RDRDBManager.getSecondaryInstance().executeSelectQueryFile(sqlFn, bindObj);
			
			for (int i = 0; i < tList.size(); i++)
			{
				String[] buffer = tList.get(i);
				
				EPResultDTO dto = new EPResultDTO();
				dto.setAttributes(buffer);
				dl.add(dto);
			}
		}
		catch (Exception ex)
		{
			Logger.error(ex.getClass().getName() + " : " + ex.getMessage());
		}
			
		Logger.info("검사 결과정보 loaded : " + dl.size());
	}
	
	/** 컬럼정보 loading */
	public static TreeMap<String, String> getTableColumns(String pDBName, 
			                                              String pTableName)
	{
		TreeMap<String, String> rMap = new TreeMap<String, String>();
		
		String sqlFn = "EPReceiptColumnSelect.sql";

		Object bindObj[] = new Object[2];
		bindObj[0] = pDBName;
		bindObj[1] = pTableName;
		
		ArrayList<String[]> tList
			= RDRDBManager.getSecondaryInstance().executeSelectQueryFile(sqlFn, bindObj);
		
		for (int i = 0; i < tList.size(); i++)
		{
			String[] buffer = tList.get(i);
			if (buffer.length == 2)
			{
				String colName = buffer[0];
				String colType = buffer[1];
				rMap.put(colName, colType);
			}
		}
			
		return rMap;
	}
	
	/** 소견코드 -> 소견문장 */
	public static TreeMap<String, String> getTestComments(String pCommentCode)
	{
		TreeMap<String, String> rMap = new TreeMap<String, String>();
		
		String sqlFn = "EPTestCommentSelect.sql";

		Object bindObj[] = new Object[1];
		bindObj[0] = (pCommentCode == null || pCommentCode.isEmpty() ? "%" : pCommentCode);
		
		ArrayList<String[]> tList
			= RDRDBManager.getSecondaryInstance().executeSelectQueryFile(sqlFn, bindObj);
		
		for (int i = 0; i < tList.size(); i++)
		{
			String[] buffer = tList.get(i);
			if (buffer.length == 2)
			{
				rMap.put(buffer[0], buffer[1]);
			}
		}
			
		return rMap;
	}
		
}
