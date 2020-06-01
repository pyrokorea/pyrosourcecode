package rdr.learner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rdr.api.JSONConverter;
import rdr.api.RDRBroker;
import rdr.api.RDRBrokerFactory;
import rdr.api.RDRInterface;
import rdr.api.RDRService;
import rdr.apimsg.CaseItem;
import rdr.apimsg.KARequest;
import rdr.apimsg.KAResponse;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseSet;
import rdr.cases.CaseStructure;
import rdr.cases.CaseSynchroniser;
import rdr.cases.CornerstoneCaseSet;
import rdr.logger.Logger;
import rdr.model.Attribute;
import rdr.model.AttributeFactory;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.model.ValueType;
import rdr.rules.Conclusion;
import rdr.rules.ConditionSet;
import rdr.rules.Operator;
import rdr.rules.Rule;
import rdr.utils.RDRConfig;
import rdr.utils.RDRConstants;
import rdr.utils.StopWatch;
import rdr.utils.Utility;

public class IncrementalLearner 
{
	private String domainName;
	
	private String userId;
	
	private CaseStructure caseStructure;
	
	/** case 
	 *  caseId : ARFF는 파일내 index(1부터 시작)
	 *           JSON은 JSON파일내 case id를 사용
	 *  caseId는 learnerInit에서 저장된 cornerstoneCaseId로 setting된다.
	 * */
	private CaseSet caseSet;
	
	private HashMap<Integer, String> rdrClassMap;

	private KARequest kaRequest;
	
	private static final String ADDED = "A";
	private static final String MODIFIED = "M";
	private static final String ERROR = "E";
	private static final String CONFLICT = "C";
	private TreeMap<Integer, String> result;
		
	public IncrementalLearner(String pDomainName, String pUserId)
	{
		this.domainName = pDomainName;
		this.userId = pUserId;
		RDRService.checkDomain(pDomainName, pUserId);
		
		this.caseStructure = new CaseStructure();
		this.caseSet = new CaseSet();
		this.kaRequest = new KARequest();
		this.result = new TreeMap<Integer, String>();
		this.rdrClassMap = new HashMap<Integer, String>();
	}
	
	public JSONArray getResultJSON()
	{
		JSONArray jsonArr = new JSONArray();
		Iterator<Integer> itr = this.result.keySet().iterator();
		while (itr.hasNext())
		{
			Integer cid = itr.next();
			String status = this.result.get(cid);
			JSONObject jobj = new JSONObject();
			jobj.put("caseId", cid.intValue());
			jobj.put("status", status);
			jsonArr.add(jobj);
		}
		
		return jsonArr;
	}
	
	public boolean prepareCaseJSON(JSONArray pJsonArray)
	{
		HashSet<String> attrNames = new HashSet<String>();
		
		for (int i = 0; i < pJsonArray.size(); i++)
		{
			JSONObject jObj = (JSONObject)pJsonArray.get(i);
			
			JSONObject caseObj = (JSONObject)jObj.get("data");
			LinkedHashMap<String, String> valueMap 
				= JSONConverter.convertJSONObjectToValueMap(caseObj);
			
			attrNames.addAll(valueMap.keySet());
		}
		
		int addedCnt = this.synchronizeCaseStructure(attrNames);
		if (addedCnt > 0 )
		{
			RDRInterface.getInstance().reloadDomain();
			this.caseStructure = CaseLoader.loadCaseStructureFromDB();
		}
		else if (addedCnt < 0)
		{
			Logger.error("case attribute added, but sync(insert) failed");
			return false;
		}
		
		for (int i = 0; i < pJsonArray.size(); i++)
		{
			JSONObject jObj = (JSONObject)pJsonArray.get(i);
			
			Integer caseId = ((Long)jObj.get("caseId")).intValue();
			rdrClassMap.put(caseId, (String)jObj.get("conclusion"));
			
			JSONObject caseObj = (JSONObject)jObj.get("data");
			LinkedHashMap<String, String> valueMap 
				= JSONConverter.convertJSONObjectToValueMap(caseObj);
			
			String[] msg = new String[1];
			Case aCase 
				= RDRInterface.getInstance().getCaseFromValueMap(this.caseStructure, 
						                                         valueMap, msg);
			if (aCase == null)
			{
				Logger.error("case creation failed, " + msg[0]);
				return false;
			}
			
			aCase.setCaseId(caseId.intValue());
			this.caseSet.addCase(aCase);
		}
		
		Logger.info("case loaded from JSON, case amount : " + pJsonArray.size());
		return true;
	}
	
	public boolean prepareCaseArffFile(String fn)
	{
		String arffFile = RDRConfig.getDomainPath() + "cases" + File.separator + fn;
		return prepareCaseArffFileWithPath(arffFile);
	}
	
	public boolean prepareCaseArffFileWithPath(String pArffFile)
	{
		String fn = RDRConfig.getArffFile();
		try
		{
			Utility.copyFile(pArffFile, fn);
		}
		catch (IOException ex)
		{
			Logger.error("aff file copy failed", ex);
			return false;
		}
		
		if (this.checkArffCaseStructure() == false)
		{
			Logger.error("ARFF and DB CaseStructure is not same");
			return false;
		}
		
		try
		{
			CaseLoader.caseImport(this.caseStructure, RDRConstants.INT_MAX, fn);
			this.caseSet = new CaseSet(Main.allCaseSet);
			
			HashMap<Integer, Case> cases = this.caseSet.getBase();
			Iterator<Integer> caseIterator = cases.keySet().iterator();
			while (caseIterator.hasNext())
			{
				Integer caseId = caseIterator.next();
				Case aCase = cases.get(caseId);
				
				String conclusionStr = aCase.getValue(RDRConstants.RDRClassAttributeName).toString();
		    	this.rdrClassMap.put(caseId, conclusionStr);
		    	aCase.removeKey(RDRConstants.RDRClassAttributeName);
			}
		}
		catch (Exception ex)
		{
			Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
			return false;
		}
		
		return true;
	}
	
	
	/** only for SCRDR */
	public boolean runSCRDR(String pDomainName, String pUserId)
	{
		int addModeCnt = 0;
		int editModeCnt = 0;
		int addedRuleCnt = 0;
		
		long startTime = System.currentTimeMillis();
		StopWatch.initialize();
		
		try 
		{
			RDRBroker broker = RDRBrokerFactory.createBroker();
			
			HashMap<Integer, Case> cases = this.caseSet.getBase();
			Iterator<Integer> caseIterator = cases.keySet().iterator();
			while (caseIterator.hasNext())
			{
				Integer caseId = caseIterator.next();
				Case aCase = cases.get(caseId);
				
	        	//RDRInterface.getInstance().reloadDomain();
	        	
		    	Logger.info("# " + caseId + " Case Start ---------------------------");
		    	
		    	String conclusionStr = this.rdrClassMap.get(caseId);
		    	
		        Rule inferenceResult = (Rule)RDRInterface.getInstance().getInferenceResult(aCase);
		        
		        if (Utility.isEmptyInferenceResult(inferenceResult))
		        {
		        	Logger.info("@ add mode start");
		        	addModeCnt++;
		        	
		        	// add first level rule --------------------------------------
		        	if (RDRService.addFirstLevelRule(pDomainName, aCase, pUserId) == false)
		        	{
		        		Logger.info("failed for adding first level rule");
		        		this.result.put(caseId, this.ERROR);
		        		continue;
		        	}
		        	
		        	Logger.info("first level rule added");
		        	addedRuleCnt++;
		        	
		        	// add second level rule -------------------------------------
		        	kaRequest.clear();
		        	kaRequest.setCase(aCase);
		        	kaRequest.setKAMode(Learner.KA_EXCEPTION_MODE);
		        	kaRequest.setSelectedConclusion(conclusionStr);
		        	
		        	// 조건 : (categorical항목) - (1Level attr)
		        	ArrayList<String> attrList = new ArrayList<String>();
		        	ArrayList<String> attrFirstLevels = broker.getFirstLevelAttributes();
		        	attrList.addAll(Utility.subtraction(broker.getConditionAttributes(this.caseStructure), 
		        							            attrFirstLevels));
		        	
		        	kaRequest.clearCondition();
		        	kaRequest.addCondition(aCase, attrList, false);
		        	if (kaRequest.isConditionEmpty())
		        	{
		        		Logger.error("second level rule's condition is empty");
		        		this.result.put(caseId, this.ERROR);
		        		continue;
		        	}
		        	
		        	KAResponse tResponse
		        		= RDRService.addRuleBatch(pDomainName, kaRequest, pUserId);
		        	
		        	if (tResponse.getStatus() == false)
		        	{
		        		Logger.error(tResponse.getMessage());
		        		this.result.put(caseId, this.ERROR);
		        		continue;
		        	}
		        	
		        	addedRuleCnt++;
		        	
		        	if (tResponse.getValidatingCaseCount() > 0 )
		        	{
		        		Logger.warn("validating case count : " + tResponse.getValidatingCaseCount());
		        		kaRequest.printLog();
		        		this.result.put(caseId, this.CONFLICT);
		        	}
		        	else
		        	{
		        		this.result.put(caseId, this.ADDED);
		        	}
		        	
		        	Logger.info("second level rule added");
		        	StopWatch.lap(caseId + ", add mode rule added");
		        }
		        else
		        {
		        	Logger.info("@ edit mode start");
		        	
		        	if (inferenceResult.getConclusion().toString().equals(conclusionStr))
		        	{
		        		Logger.info("inference result is same to class string : " + conclusionStr);
		        		StopWatch.lap(caseId + ", inference equals to class string");
		        		continue;
		        	}
		        	
		        	editModeCnt++;
		        	
		        	// add modify rule --------------------------------------
		        	kaRequest.clear();
		        	kaRequest.setKAMode(Learner.KA_EXCEPTION_MODE);
		        	kaRequest.setWrongConclusionId(inferenceResult.getConclusion().getConclusionId());
		        	kaRequest.setCase(aCase);
		        	kaRequest.setSelectedConclusion(conclusionStr);
		        	
		        	ArrayList<String> attrFirstLevels = broker.getFirstLevelAttributes();
		        	
		        	// 조건 : (categorical항목) - (1Level attr)
		        	ArrayList<String> attrList = new ArrayList<String>();
		        	attrList.addAll(Utility.subtraction(broker.getConditionAttributes(this.caseStructure), 
		        							            attrFirstLevels));

		        	kaRequest.addCondition(aCase, attrList, false);
		        	if (kaRequest.isConditionEmpty())
		        	{
		        		Logger.error("modify rule's condition is empty");
		        		this.result.put(caseId, this.ERROR);
		        		continue;
		        	}
		        	
		        	KAResponse tResponse
		        		= RDRService.getValidationCasesBatch(pDomainName, kaRequest, pUserId);
		        	
		        	if (tResponse.getStatus() == false)
		        	{
		        		Logger.error(tResponse.getMessage());
		        		this.result.put(caseId, this.ERROR);
		        		continue;
		        	}
		        	
		        	if (tResponse.getValidatingCaseCount() == 0 )
		        	{
		        		tResponse
			        		= RDRService.addRuleBatch(pDomainName, kaRequest, pUserId);
			        	
			        	if (tResponse.getStatus() == false)
			        	{
			        		Logger.error(tResponse.getMessage());
			        		this.result.put(caseId, this.ERROR);
			        		continue;
			        	}
			        	else
			        	{
			        		this.result.put(caseId, this.MODIFIED);
			        	}
			        	
			        	addedRuleCnt++;
		        	}
		        	else
		        	{
		        		ArrayList<CaseItem> caseItems = tResponse.getValidatingCases();
		        		Logger.info("validating case count : " + tResponse.getValidatingCaseCount());
		        		
		        		boolean flag = true;
		        		while (true)
		        		{
		        			Case validatingCase = caseItems.get(0).createCase(this.caseStructure);
			        			
		        			// 조건 추가 : validatingCase와 값이 다른 항목
			        		ArrayList<String> diffList 
			        			= this.getDifferenceList(aCase, validatingCase, null);
			        		attrList = new ArrayList(Utility.union(attrList, diffList));
				        	
				        	kaRequest.clearCondition();
				        	kaRequest.addCondition(aCase, attrList, false);
				        	if (kaRequest.isConditionEmpty())
				        	{
				        		Logger.error("modify rule's condition is empty");
				        		this.result.put(caseId, this.ERROR);
				        		flag = false;
				        		break;
				        	}
				        	
				        	KAResponse vResponse
				        		= RDRService.getValidationCasesBatch(pDomainName, kaRequest, pUserId);	
				        	
				        	if (vResponse.getStatus() == false)
				        	{
				        		Logger.error(vResponse.getMessage());
				        		this.result.put(caseId, this.ERROR);
				        		flag = false;
				        		break;
				        	}
				        	
				        	if (vResponse.getValidatingCaseCount() == 0 )
				        	{
				        		vResponse
					        		= RDRService.addRuleBatch(pDomainName, kaRequest, pUserId);
					        	
					        	if (vResponse.getStatus() == false)
					        	{
					        		Logger.error(vResponse.getMessage());
					        		this.result.put(caseId, this.ERROR);
					        		flag = false;
					        		break;
					        	}
					        	else 
					        	{
					        		addedRuleCnt++;
					        		this.result.put(caseId, this.MODIFIED);
					        		break;
					        	}
				        	}
				        	else
				        	{
				        		if (caseItems.size() == vResponse.getValidatingCaseCount())
				        		{
				        			this.result.put(caseId, this.CONFLICT);
				        			break;
				        		}
				        			
				        		caseItems = vResponse.getValidatingCases();
				        	}
				        	
				        	if (flag == false) break;
		        		}//while
		        	}
		        	
		        	StopWatch.lap(caseId + ", modify mode rule added");
		        }
	        }//while case iterator
		}
		catch (Exception ex) 
		{
	    	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
	    	return false;
	    }		
		
		StopWatch.printLog();
		
		Logger.info("addMode(inference결과없음) : " + addModeCnt);
		Logger.info("editModeCnt(inference결과존재) : " + editModeCnt);
		Logger.info("addedRuleCnt : " + addedRuleCnt);
		
		long elapsedTime = System.currentTimeMillis() - startTime;
		Logger.info("running time : " + elapsedTime/1000.0);
		
		return true;
	}
		
	/** difference list
	 *  aCase 항목중 aValidatingCase항목과 값이 다른 것만 집계한다. (aValidatingCase=null이면 aCase의 모든 항목임)
	 *  (단, 결론항목은 무조건 제외)
	 * 
	 * @param aCase
	 * @param aValidatingCase
	 * @param aUsedConditionSet
	 * @return
	 */
	private ArrayList<String> getDifferenceList(Case aCase, 
			                                    Case aValidatingCase, 
			                                    ConditionSet aUsedConditionSet)  
	{
		ArrayList<String> attrNames = new ArrayList<String>();
		
		Iterator<String> keys = aCase.getValues().keySet().iterator();
    	while (keys.hasNext())
    	{
    		String attrName = keys.next();
    		
    		if (attrName.equals(RDRConstants.RDRClassAttributeName))
    			continue;
    		
    		Value aValue = aCase.getValues().get(attrName);
    		Value bValue = null;
    		if (aValidatingCase != null) bValue = aValidatingCase.getValues().get(attrName);
    		
    		if (aValue.isNullValue())
    		{
    			if (aValidatingCase != null)
    			{
    				if (bValue.isNullValue()) //same
    					continue;
    			}
    		}
    		else 
    		{	
    			if (aValidatingCase != null)
    			{
		    		if (aValue.equals(bValue)) //same
		    		{
		    			continue;
		    		}
    			}
    		}
    		
    		if (aUsedConditionSet == null ||
    			aUsedConditionSet.isUsedAttribute(attrName) == false)
    		{
				attrNames.add(attrName);
    		}
    	}
		
		return attrNames;
	}
	
	public boolean checkArffCaseStructure()
	{
		try
		{
			this.caseStructure = CaseLoader.getArffCaseStructure();
			CaseStructure dbCaseStructure = CaseLoader.loadCaseStructureFromDB();
	
			CaseSynchroniser tSync = new CaseSynchroniser();
			int rtn = tSync.compare(this.caseStructure, dbCaseStructure);
			
			return (rtn == 0);
		}
		catch (Exception ex)
		{
			Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
			return false;
		}
	}
	
	public int synchronizeCaseStructure(HashSet<String> pAttrNames)
	{
		this.caseStructure = CaseLoader.loadCaseStructureFromDB();
		ArrayList<String> attrNames = new ArrayList<String>(pAttrNames);
		
		RDRBroker broker = RDRBrokerFactory.createBroker();
		return broker.synchronizeCaseStructure(attrNames, this.caseStructure);
		
	}
}
