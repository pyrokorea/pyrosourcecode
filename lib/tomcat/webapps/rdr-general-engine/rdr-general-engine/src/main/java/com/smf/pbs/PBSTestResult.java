package com.smf.pbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;

import rdr.logger.Logger;
import rdr.utils.StringUtil;

/**
 * @brief 전기영동검사  검사결과 클래스
 * @author Kim, Woo Cheol
 * @version 1.00
 * @date 2019.05.20
 * @section MODIFYINFO 수정정보
 *  - 수정일/수정자 : 수정내역
 *  - 2019.05.20/Kim, Woo Cheol : 최초작성
 */
public class PBSTestResult
{
	/** key(lmb코드_접수일자_접수번호_검사코드_검체유형) */
	private String key;
	
	/** 검사코드 */
	private String testCode;
	
	/** 검사명 */
	private String testName;
	
	/** key : 항목명, value : 값 */
	private LinkedHashMap<String, String> valueMap;

	/** key : 검사명, value : 판정코드 */
	private LinkedHashMap<String, String> dcMap;
	
	/** constructor */
	public PBSTestResult()
	{
		this.valueMap = new LinkedHashMap<String, String>();
		this.dcMap = new LinkedHashMap<String, String>();
	}
	
	/** build internal
	 *  pRstDataList 는 부속코드를 기준으로 정렬되어야 함
	 * @param pRctMap
	 * @param pRstDataList
	 * @return
	 */
	public boolean set(LinkedHashMap<String, String> pRctMap, 
			           ArrayList<PBSResultDTO> pRstDataList,
			           TreeMap<String, String> pDefaultDecision,
			           TreeMap<String, String> pCommentGroup,
			           int pDecisionSeq)
	{
		Logger.debug("receipt data map : " + pRctMap.size() + 
				     " result list : " + pRstDataList.size());
		
		if (pRstDataList.size() < 1)
		{
			Logger.error("result data is empty");
			return false;
		}
		
		this.valueMap = pRctMap;
		
		boolean defaultR = true;
		boolean defaultW = true;
		boolean defaultP = true;
		
		String decisionChar = "";
		Integer decisionNum = null;
		
		for (int i = 0; i < pRstDataList.size(); i++)
		{
			PBSResultDTO rData = pRstDataList.get(i);
			
			if (i == 0) this.testCode = rData.getTestCode();
			
			String subCode = rData.getTestSubCode();
			if (subCode.equals("00"))
			{
				this.testName = rData.getTestName();
			}
			
			if (pDecisionSeq == 1)
			{
				decisionChar = rData.getDecisionChar1();
				decisionNum = rData.getDecisionNumric1();
			}
			else
			{
				decisionChar = rData.getDecisionChar2();
				decisionNum = rData.getDecisionNumric2();
			}
			
		    String defaultDecision = "";
		    if (pDefaultDecision.containsKey(subCode))
		    	defaultDecision = pDefaultDecision.get(subCode);
		    
		    if (decisionChar.equals(defaultDecision) == false)
		    {
		        String cg = "";
		        if (pCommentGroup.containsKey(subCode))
		        	cg = pCommentGroup.get(subCode);
		        
		        if (cg.isEmpty())
		        	Logger.debug("comment group empty, test[" + this.testCode +
		    			     "] sub[" + subCode + "]");
		        
		        Logger.debug("decision is not default, test[" + this.testCode +
	    			     "] sub[" + subCode + "] decSeq[" + pDecisionSeq + "] decision[" + decisionChar +
	    			     "] default[" + defaultDecision + "] cg[" + cg + "]");
		        
		        if (cg.equals("R")) defaultR = false;
		        else if (cg.equals("W")) defaultW = false;
		        else if (cg.equals("P")) defaultP = false;
		    }
		}
		
		for (int i = 0; i < pRstDataList.size(); i++)
		{
			PBSResultDTO rData = pRstDataList.get(i);
			
			if (pDecisionSeq == 1)
			{
				decisionChar = rData.getDecisionChar1();
				decisionNum = rData.getDecisionNumric1();
			}
			else
			{
				decisionChar = rData.getDecisionChar2();
				decisionNum = rData.getDecisionNumric2();
			}
			
			String key = "";
			String val = "";
			
			key = rData.getTestSubCode() + "_" + rData.getTestName();
			val = decisionChar;
			
			if (val.isEmpty() == false)
				this.dcMap.put(key, val);
	
			key = rData.getTestSubCode() + "_" + rData.getTestName() + "_NUM";
			val = (decisionNum == null ? "" : String.valueOf(decisionNum.intValue()));
			
			if (val.isEmpty() == false)
				this.dcMap.put(key, val);
		}
		
		this.valueMap.put("TEST_CODE", this.testCode);
		this.valueMap.put("Default(Group_R)", (defaultR ? "Y" : "N"));
		this.valueMap.put("Default(Group_W)", (defaultW ? "Y" : "N"));
		this.valueMap.put("Default(Group_P)", (defaultP ? "Y" : "N"));
		
		return true;
	}
	
	public ArrayList<String> getAttrCandidate()
	{
		ArrayList<String> list = new ArrayList<String>();
		Set<String> tSet = this.valueMap.keySet();
    	list.addAll(tSet);
    	return list;
	}
	
	public ArrayList<String> getTestNames()
	{
		ArrayList<String> list = new ArrayList<String>();
		Set<String> tSet = this.dcMap.keySet();
    	list.addAll(tSet);
    	return list;
	}
	
	public String getValue(String keyStr)
	{
		if (this.valueMap.containsKey(keyStr))
			return this.valueMap.get(keyStr);
		
		if (this.dcMap.containsKey(keyStr))
			return this.dcMap.get(keyStr);
		
		return null;
	}
	
	public void getAllValueMap(LinkedHashMap<String, String> pMap)
	{
		pMap.putAll(this.valueMap);
		pMap.putAll(this.dcMap);
	}
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PBSTestResult other = (PBSTestResult) obj;
		if (this.key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() 
	{
		/** key : 검사명, value : 판정코드 */
		//private LinkedHashMap<String, String> dcMap;
		
		String str = "";
		Iterator<String> keys = this.dcMap.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next(); //test명 
			String code = this.dcMap.get(key); //판정코드
			str += key + "[" + code + "] ";
		}
		
		return "TestResult(" + key + ") name[" + testName + " : " + str;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	





}
