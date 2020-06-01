package com.smf.ep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

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
public class EPTestResult
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
	
	public static String [] decisionCodes = {"DE", "NM", "SI", "IN"};
	
	/** constructor */
	public EPTestResult()
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
			           ArrayList<EPResultDTO> pRstDataList)
	{
		Logger.debug("receipt data map : " + pRctMap.size() + 
				     " result list : " + pRstDataList.size());
		
		if (pRstDataList.size() < 1)
		{
			Logger.error("result data is empty");
			return false;
		}
		
		this.valueMap = pRctMap;
		
		double totalVolume = 0.;
		if (pRctMap.containsKey("TOTAL_VOLUME"))
			totalVolume = StringUtil.parseDouble(pRctMap.get("TOTAL_VOLUME"));
		
		for (int i = 0; i < pRstDataList.size(); i++)
		{
			EPResultDTO rData = pRstDataList.get(i);
			
			if (i == 0) this.testCode = rData.getTestCode();
			
			if (rData.getTestSubCode().equals("00"))
			{
				this.testName = rData.getTestName();
				//totalVolume = rData.getTotalVolume();
			}
			else
			{
				/** decision code 가 없는(empty string)인 경우 제외 */
				if (rData.getDecisionCode().isEmpty() == false)
					this.dcMap.put(rData.getTestName(), rData.getDecisionCode());
			}
		}
		
		this.valueMap.put("TEST_CODE", this.testCode);
		this.valueMap.put("TV_FLAG", (totalVolume > 0 ? "Y" : "N"));
				
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
		EPTestResult other = (EPTestResult) obj;
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
