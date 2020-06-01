package rdr.api;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.smf.ep.EPDBManager;
import com.smf.ep.EPReportRuleTree;

import rdr.apimsg.AttributeItem;
import rdr.apimsg.CaseItem;
import rdr.apimsg.ConditionItem;
import rdr.apimsg.InferenceItem;
import rdr.apimsg.KARequest;
import rdr.apimsg.KAResponse;
import rdr.apimsg.RDRResponse;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseStructure;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.db.RDRDBManager;
import rdr.learner.Learner;
import rdr.logger.Logger;
import rdr.model.IAttribute;
import rdr.model.ValueType;
import rdr.rules.Conclusion;
import rdr.rules.ConclusionSet;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;
import rdr.rules.RuleSet;
import rdr.similarity.SimilarityElement;
import rdr.similarity.SimilaritySolution;
import rdr.utils.RDRConfig;
import rdr.utils.RDRConstants;
import rdr.utils.StringUtil;
import rdr.utils.Utility;
import rdr.workbench.ReportRuleTree;

/** 함수 제공 RDR Engine API
 * 
 * @author ucciri
 *
 */
public class RDRService
{
	/** read configuration file and connect dataBase 
	 * 
	 * @param serviceName 서비스명
	 * @param rootPath 설정파일을 관리할 경로
	 * @return true/false
	 */
	public static boolean init(String serviceName, String rootPath)
	{
		boolean flag = true;
		flag &= RDRConfig.initWithRootPath(serviceName, rootPath);
		flag &= RDRDBManager.getInstance().connectDataBase(0);
		
		if (flag) 
		{
			//for initialize domain 
			RDRInterface aRDRInf = RDRInterface.getInstance();
		}
		
		return flag;
	}
	
	/** close dataBase
	 * 
	 */
	public static void close()
	{
		RDRDBManager.getInstance().closeDataBase();
	}
	
	public static boolean checkDomain(String pDomainName,
            					      String pUserId)
	{
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("check domain failed");
			return false;
		}

		return true;
	}
	
	/** ARFF input에 의한 추론
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  RDRResponse.getInferenceResults() : 추론결과
	 *  RDRResponse.getFiredRules() : fired rule정보
	 * 
	 * @param pDomainName 도메인명
	 * @param pArffStr 문자열로 변환된 ARFF파일 내용
	 * @param pSyncFlag 사례구조 sync 여부 (1:sync함)
	 * @Param pUserId 사용자ID
	 * @return inference result(RDRResponse)
	 */
	public static RDRResponse getInferenceByARFF(String pDomainName, 
			                                    String pArffStr, 
			                                    int pSyncFlag,
			                                    String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, true) == false)
    	{
			Logger.error("API, inferenceByARFF, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("domain initialize failed");
	        return tResponse;
    	}
		
		String fn = RDRConfig.getArffFile();
		
		try 
		{
            BufferedWriter out = new BufferedWriter(new FileWriter(fn));
            out.write(pArffStr); //out.newLine();
            out.close();
        } 
		catch (IOException e) 
		{
			Logger.error( e.getClass().getName() + " : " + e.getMessage(), e );
			tResponse.setStatus(false);
			tResponse.setMessage("arff file creation failed");
	        return tResponse;
        }
        
        try 
        {
        	CaseStructure arffCaseStructure = CaseLoader.getArffCaseStructure();
        	CaseStructure dbCaseStructure = Main.domain.getCaseStructure();
        	
        	String[] msg = new String[1];
        	int cnt = RDRInterface.getInstance().compareCaseStructure(arffCaseStructure, 
        			                                                  dbCaseStructure, msg);
        	
        	boolean sync = false;
        	String addedStr = "";
        	
        	if (cnt < 0)
        	{
    			tResponse.setStatus(false);
    			tResponse.setMessage(msg[0]);
    	        return tResponse;
        	}
        	else if (cnt > 0)
        	{
        		if (pSyncFlag == 1)
        		{
        			//추가된 항목을 DB에 insert
            		ArrayList<String> addedAttr 
            			= RDRInterface.getInstance().syncCaseStructure(arffCaseStructure, dbCaseStructure);
            		
            		addedStr += "[";
            		for (int ai = 0; ai < addedAttr.size(); ai++)
            		{
            			addedStr += addedAttr.get(ai) + " ";
            		}
            		addedStr += "]";
            		
        			sync = true;
        		}
        		else
        		{
        			//sync 하지 않음, 에러처리
        			tResponse.setStatus(false);
        			tResponse.setMessage("ARFF파일의 Case구조가 RDR Engine DB와 다릅니다.");
        	        return tResponse;
        		}
        	}
        	
        	Case aCase = RDRInterface.getInstance().getCaseFromArff();
        	
        	if (aCase == null)
        	{
        		tResponse.setStatus(false);
    			tResponse.setMessage("case creation failed");
        	}
        	else
        	{
        		RDRServiceUtil serviceUtil = new RDRServiceUtil();
            	tResponse = serviceUtil.inference(aCase);
            	
            	if (tResponse == null || tResponse.getStatus() == false)
            	{
            		tResponse = new RDRResponse();
            		tResponse.setStatus(false);
        			tResponse.setMessage("inference failed");
            	}
            	else
            	{
	            	String syncMsg = "";
	            	if (sync) syncMsg = "ARFF의 추가항목이 DB에 반영됨 " + addedStr;
	            	tResponse.setMessage(syncMsg);
	            	tResponse.setStatus(true);
	            	
	            	if (tResponse.isInferenceEmpty())
            		{
            			//CaseItem tCaseItem = RDRService.getCaseByBroker(pDomainName, params, pUserId);
            			//Case tCase = tCaseItem.createCase(Main.domain.getCaseStructure());
            			
            			if (RDRService.addFirstLevelRule(pDomainName, aCase, pUserId))
            			{
            				tResponse = serviceUtil.inference(aCase);
            			}
            			else
            			{
            				tResponse.setMessage("failed for adding first level rule");
        	            	tResponse.setStatus(false);
            				Logger.error(tResponse.getMessage());
            			}
            		}
            	}
        	}

        	return tResponse;
	        
        } 
        catch (Exception ex) 
        {
        	Logger.error( ex.getClass().getName() + " : " + ex.getMessage(), ex );
			tResponse.setStatus(false);
			tResponse.setMessage(ex.getClass().getName() + " : " + ex.getMessage());
	        return tResponse;
        }
	}
	
	/** JSON input에 의한 추론
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  RDRResponse.getInferenceResults() : 추론결과
	 *  RDRResponse.getFiredRules() : fired rule정보
	 * 
	 * @param pDomainName 도메인명
	 * @param pCaseItem 사례정보
	 * @Param pUserId 사용자ID
	 * @return inference result(RDRResponse)
	 */
	public static RDRResponse getInferenceByJSON(String pDomainName, 
			                                    CaseItem pCaseItem,
			                                    String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();

		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, true) == false)
    	{
			Logger.error("API, inferenceByJSON, check&init domain failed");
	        
	        tResponse.setStatus(false);
			tResponse.setMessage("domain initialize failed");
	        return tResponse;
    	}
		
		try 
        {
        	Case aCase = pCaseItem.createCase(Main.domain.getCaseStructure());
        	
        	if (aCase == null)
        	{
        		tResponse.setStatus(false);
    			tResponse.setMessage("case creation failed");
        	}
        	else
        	{
        		RDRServiceUtil serviceUtil = new RDRServiceUtil();
            	tResponse = serviceUtil.inference(aCase);
            	
            	if (tResponse == null || tResponse.getStatus() == false)
            	{
            		tResponse = new RDRResponse();
            		tResponse.setStatus(false);
        			tResponse.setMessage("inference failed");
            	}
            	else
            	{
            		tResponse.setStatus(true);
            		tResponse.setMessage("");
            		
            		if (tResponse.isInferenceEmpty())
            		{
            			//CaseItem tCaseItem = RDRService.getCaseByBroker(pDomainName, params, pUserId);
            			//Case tCase = tCaseItem.createCase(Main.domain.getCaseStructure());
            			
            			if (RDRService.addFirstLevelRule(pDomainName, aCase, pUserId))
            			{
            				tResponse = serviceUtil.inference(aCase);
            			}
            			else
            			{
            				tResponse.setMessage("failed for adding first level rule");
        	            	tResponse.setStatus(false);
            				Logger.error(tResponse.getMessage());
            			}
            		}
            	}
        	}

	        return tResponse;
        	
        } 
		catch (Exception ex ) 
		{
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        	tResponse.setStatus(false);
			tResponse.setMessage(ex.getClass().getName() + " : " + ex.getMessage());
	        return tResponse;
        }
	}
	
	/** RDRBrokerParameters 에 의한 추론결과
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  RDRResponse.getInferenceResults() : 추론결과
	 *  RDRResponse.getFiredRules() : fired rule정보
	 *  
	 * @param pDomainName 도메인명
	 * @param params broker에서 사용할 파라메터 정보
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse getInferenceByBroker(String pDomainName, 
									              RDRBrokerParameters params,
									              String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, true) == false)
    	{
			Logger.error("API, getInferenceByBroker, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("domain initialize failed");
	        return tResponse;
    	}
		
		try 
        {
        	RDRBroker broker = RDRBrokerFactory.createBroker();
        	String[] msg = new String[1];
        	Case aCase = broker.getCase(params, msg);
        	
        	if (aCase == null)
        	{
        		tResponse.setStatus(false);
    			tResponse.setMessage(msg[0]);
        	}
        	else
        	{
        		Logger.info("Case : " + aCase.toString());
        		
        		RDRServiceUtil serviceUtil = new RDRServiceUtil();
            	tResponse = serviceUtil.inference(aCase);
            	
            	if (tResponse == null || tResponse.getInferenceResults().size() == 0)
            	{
            		tResponse = new RDRResponse();
            		tResponse.setStatus(false);
        			tResponse.setMessage("inference failed");
            	}
            	else
            	{
            		tResponse.setStatus(true);
            		tResponse.setMessage("");
            		
            		if (tResponse.isInferenceEmpty())
            		{
            			//CaseItem tCaseItem = RDRService.getCaseByBroker(pDomainName, params, pUserId);
            			//Case tCase = tCaseItem.createCase(Main.domain.getCaseStructure());
            			
            			if (RDRService.addFirstLevelRule(pDomainName, aCase, pUserId))
            			{
            				tResponse = serviceUtil.inference(aCase);
            			}
            			else
            			{
            				tResponse.setMessage("failed for adding first level rule");
        	            	tResponse.setStatus(false);
            				Logger.error(tResponse.getMessage());
            			}
            		}
            		            		
// 소견정보 저장하지 않음
//            		ArrayList<InferenceItem> irst = tResponse.getInferenceResults();
//            		String conclusion = irst.get(0).getConclusion();
//            		
//            		/** @todo check this */
//            		if (conclusion.isEmpty() == false && conclusion.length() <= 12)
//            		{
//	            		if (broker.saveInferenceResult(params, conclusion, pUserId))
//	            		{
//	            			tResponse.setStatus(true);
//	            		}
//	            		else
//	            		{
//	            			tResponse.setStatus(false);
//	            			tResponse.setMessage("save inference result failed");
//	            		}
//            		}
            	}
        	}

	        return tResponse;
        	
        } 
		catch (Exception ex ) 
		{
			Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        	tResponse.setStatus(false);
			tResponse.setMessage(ex.getClass().getName() + " : " + ex.getMessage());
	        return tResponse;
        }
	
	}
	
	/** case 반환
	 * 
	 * @param pDomainName
	 * @param params
	 * @param pUserId
	 * @return
	 */
	public static CaseItem getCaseByBroker(String pDomainName, 
									       RDRBrokerParameters params,
									       String pUserId)
	{
		CaseItem tCaseItem = new CaseItem();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, true) == false)
    	{
			Logger.error("API, getCaseByBroker, check&init domain failed");
			
	        return tCaseItem;
    	}
		
		try 
        {
        	RDRBroker broker = RDRBrokerFactory.createBroker();
        	String[] msg = new String[1];
        	Case aCase = broker.getCase(params, msg);
        	
        	if (aCase == null)
        	{
        		return tCaseItem;
        	}
        	else
        	{
        		Logger.info("Case : " + aCase.toString());
        		
        		tCaseItem.set(aCase.getCaseStructure(), aCase);
        		return tCaseItem;
        	}
        } 
		catch (Exception ex ) 
		{
			Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        	return tCaseItem;
        }
	
	}
	
	/** Case의 not null인 모든 항목으로 조건을 생성해서 반환
	 * 
	 * @param pDomainName
	 * @param params
	 * @param pUserId
	 * @return
	 */
	public static ArrayList<ConditionItem> getSuggestedConditions(String pDomainName, 
															      RDRBrokerParameters params,
															      String pKaMode,
			                                                      int pConclusionId,
															      String pUserId)
	{
		ArrayList<ConditionItem> ciList = new ArrayList<ConditionItem>();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, true) == false)
    	{
			Logger.error("API, getSuggestedConditions, check&init domain failed");
			
	        return ciList;
    	}
		
		try 
        {
        	RDRBroker broker = RDRBrokerFactory.createBroker();
        	String[] msg = new String[1];
        	Case aCase = broker.getCase(params, msg);
        	
        	if (aCase == null)
        	{
        		Logger.error("case creation failed");
        		return ciList;
        	}
        	
        	if (pKaMode.equals("add"))
        	{
            	KARequest kaRequest = new KARequest();
            	kaRequest.clear();
            	kaRequest.setCase(aCase);
            	
            	ArrayList<String> attrList = broker.getFirstLevelAttributes();
            	
            	boolean bContainsNull = false;
            	kaRequest.addCondition(aCase, attrList, bContainsNull);
            	
            	ciList = kaRequest.getConditionItems();
            	return ciList;
        	}
        	else if (pDomainName.equals(RDRConstants.Domain_PBS))
        	{
            	RDRServiceUtil serviceUtil = new RDRServiceUtil();
            	RDRResponse tResponse = serviceUtil.inference(aCase);
            	
            	if (tResponse == null || tResponse.getInferenceResults().size() == 0)
            	{
        			Logger.error("inference failed");
        			return ciList;
            	}

            	KARequest kaRequest = new KARequest();
            	kaRequest.clear();
            	kaRequest.setCase(aCase);
            	
            	ArrayList<String> attrList = tResponse.getRuleAttributes(pConclusionId);
            	
            	boolean bContainsNull = false;
            	kaRequest.addCondition(aCase, attrList, bContainsNull);
            	
            	ciList = kaRequest.getConditionItems();
            	return ciList;
        	}
        	else
        	{
            	KARequest kaRequest = new KARequest();
            	kaRequest.clear();
            	kaRequest.setCase(aCase);
            	
            	ArrayList<String> attrList = new ArrayList<String>();
            	ArrayList<String> attrFirstLevels = broker.getFirstLevelAttributes();
            	attrList.addAll(Utility.subtraction(broker.getConditionAttributes(aCase.getCaseStructure()), 
            							            attrFirstLevels));
            	
            	boolean bContainsNull = false;
            	kaRequest.addCondition(aCase, attrList, bContainsNull);
            	
            	ciList = kaRequest.getConditionItems();
            	return ciList;
        	}
        } 
		catch (Exception ex ) 
		{
			Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        	return ciList;
        }
	}
	
	
	/** Case의 not null인 모든 항목으로 조건을 생성해서 반환
	 * 
	 * @param pDomainName
	 * @param params
	 * @param pUserId
	 * @return
	 */
	public static ArrayList<ConditionItem> getSuggestedConditions(String pDomainName, 
			                                                      CaseItem pCaseItem,
			                                                      String pKaMode,
			                                                      int pConclusionId,
															      String pUserId)
	{
		ArrayList<ConditionItem> ciList = new ArrayList<ConditionItem>();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, true) == false)
    	{
			Logger.error("API, getSuggestedConditions, check&init domain failed");
			
	        return ciList;
    	}
		
		try 
        {
			Case aCase = pCaseItem.createCase(Main.domain.getCaseStructure());
        	
        	if (aCase == null)
        	{
        		Logger.error("case creation failed");
        		return ciList;
        	}
        	
        	if (pKaMode.equals("add"))
        	{
        		RDRBroker broker = RDRBrokerFactory.createBroker();
              	
            	KARequest kaRequest = new KARequest();
            	kaRequest.clear();
            	kaRequest.setCase(aCase);
            	
            	ArrayList<String> attrList = broker.getFirstLevelAttributes();
            	
            	boolean bContainsNull = false;
            	kaRequest.addCondition(aCase, attrList, bContainsNull);
            	
            	ciList = kaRequest.getConditionItems();
            	return ciList;
        	}
        	else if (pDomainName.equals(RDRConstants.Domain_PBS))
        	{
	        	RDRServiceUtil serviceUtil = new RDRServiceUtil();
	        	RDRResponse tResponse = serviceUtil.inference(aCase);
	        	
	        	if (tResponse == null || tResponse.getInferenceResults().size() == 0)
	        	{
	    			Logger.error("inference failed");
	    			return ciList;
	        	}
	        	
	        	RDRBroker broker = RDRBrokerFactory.createBroker();
              	
            	KARequest kaRequest = new KARequest();
            	kaRequest.clear();
            	kaRequest.setCase(aCase);
	        	
	        	ArrayList<String> attrList = tResponse.getRuleAttributes(pConclusionId);
	        	
	        	boolean bContainsNull = false;
            	kaRequest.addCondition(aCase, attrList, bContainsNull);
            	
            	ciList = kaRequest.getConditionItems();
            	return ciList;
        	}
        	else
        	{
        		RDRBroker broker = RDRBrokerFactory.createBroker();
              	
            	KARequest kaRequest = new KARequest();
            	kaRequest.clear();
            	kaRequest.setCase(aCase);
            	
            	ArrayList<String> attrList = new ArrayList<String>();
            	ArrayList<String> attrFirstLevels = broker.getFirstLevelAttributes();
            	attrList.addAll(Utility.subtraction(broker.getConditionAttributes(aCase.getCaseStructure()), 
            							            attrFirstLevels));
            	
            	boolean bContainsNull = false;
            	kaRequest.addCondition(aCase, attrList, bContainsNull);
            	
            	ciList = kaRequest.getConditionItems();
            	return ciList;
        	}
        } 
		catch (Exception ex ) 
		{
			Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        	return ciList;
        }
	}
	
			
	
	/** 단계별 지식획득 : 지식획득 엔진 초기화
	 * 
	 * @param pDomainName 도메인명
	 * @param pArffStr ARFF파일 contents string
	 * @param pConclusionId 결론ID (결론ID를 지정하지 않을 경우 -1 로 set, SCRDR이고 edit/delete인 경우 미지정 가능)
	 * @param pKaModeStr 지식획득모드 ("add", "alter", "edit", "delete")
	 * @param pSyncFlag 사례구조 sync 여부 (1:sync함)
	 * @param pUserId 사용자 ID
	 * @return
	 */
	public static JSONObject initializeByARFF(String pDomainName, 
								              String pArffStr, 
								              int pConclusionId,
								              String pKaModeStr,
								              int pSyncFlag,
								              String pUserId)
	{
		JSONObject rtnJSONObj = new JSONObject();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, true) == false)
    	{
    		Logger.error("API, KAInitByARFF, check&init domain failed");

    		rtnJSONObj.put("validity", "error");
    		rtnJSONObj.put("msg", "domain initialize failed");
	        return rtnJSONObj;
    	}
		
        int kaMode = Learner.getKaModeByName(pKaModeStr);
        
        //edit,delete이고 MCRDR인 경우 conclusionId 가 필수임
        if (kaMode == Learner.KA_EXCEPTION_MODE || 
            kaMode == Learner.KA_STOPPING_MODE ||
            kaMode == Learner.KA_ALTER_MODE)
        {
        	if (Main.domain.isMCRDR() && pConclusionId < 0)
        	{
        		rtnJSONObj.put("validity", "error");
        		rtnJSONObj.put("msg", "conclusionId is not found");
    	        return rtnJSONObj;
        	}
        }
        
        if (kaMode == Learner.KA_ALTER_MODE && Main.domain.isSCRDR())
        {
        	rtnJSONObj.put("validity", "error");
        	rtnJSONObj.put("msg", "alter mode is not allowd for SCRDR");
    	    return rtnJSONObj;
        }
        
        if (kaMode < 0)
        {
        	Logger.error("API, KAInit, kaMode is wierd");
        		
        	rtnJSONObj.put("validity", "error");
        	rtnJSONObj.put("msg", "kaMode is wired");
    	    return rtnJSONObj;
        }
		
        String fn = RDRConfig.getArffFile();
		
		try 
		{
            BufferedWriter out = new BufferedWriter(new FileWriter(fn));
            out.write(pArffStr); //out.newLine();
            out.close();
        } 
		catch (IOException e) 
		{
			Logger.error( e.getClass().getName() + ": " + e.getMessage(), e );
        	rtnJSONObj.put("validity", "error");
			rtnJSONObj.put("msg", "arff file creation failed");
			return rtnJSONObj;
        }
        
		try 
		{
        	CaseStructure arffCaseStructure = CaseLoader.getArffCaseStructure();
        	CaseStructure dbCaseStructure = Main.domain.getCaseStructure();
        	
        	String[] msg = new String[1];
        	int cnt = RDRInterface.getInstance().compareCaseStructure(arffCaseStructure, 
        			                                                  dbCaseStructure, msg);
        	
        	boolean sync = false;
        	String addedStr = "";
        	
        	if (cnt < 0)
        	{
    			rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", msg[0]);
    	        return rtnJSONObj;
        	}
        	else if (cnt > 0)
        	{
        		if (pSyncFlag == 1)
        		{
        			//추가된 항목을 DB에 insert
            		ArrayList<String> addedAttr 
            			= RDRInterface.getInstance().syncCaseStructure(arffCaseStructure, dbCaseStructure);
            		
            		addedStr += "[";
            		for (int ai = 0; ai < addedAttr.size(); ai++)
            		{
            			addedStr += addedAttr.get(ai) + " ";
            		}
            		addedStr += "]";
            		
        			sync = true;
        		}
        		else
        		{
        			//sync 하지 않음, 에러처리
        			rtnJSONObj.put("validity", "error");
        			rtnJSONObj.put("msg", "ARFF파일의 Case구조가 RDR Engine DB와 다릅니다.");
        	        return rtnJSONObj;
        		}
        	}
        	
        	Case aCase = RDRInterface.getInstance().getCaseFromArff();
        	//aCase.setCaseId(RDRDBManager.getInstance().getNewCornerstoneCaseId());
        	//aCase.setCaseId(RDRInterface.getInstance().getCornerstoneCaseId(aCase));
        	Logger.info("case id(initializeByARFF) : " + aCase.getCaseId());
        	
        	if (aCase == null)
        	{
        		rtnJSONObj.put("validity", "error");
        		rtnJSONObj.put("msg", "case creation failed");
        		return rtnJSONObj;
        	}
        	
        	RDRServiceUtil serviceUtil = new RDRServiceUtil();
        	
        	//edit/delete이고 SCRDR인 경우 conclusionId 가 없을때 inference결과로 conclusionId set
        	if (kaMode != Learner.KA_NEW_MODE &&
        		Main.domain.isSCRDR() && pConclusionId < 0)
        	{
        		pConclusionId = serviceUtil.checkConclusionId(kaMode, aCase);
        		if (pConclusionId < 0 )
        		{
        			rtnJSONObj.put("validity", "error");
            		rtnJSONObj.put("msg", "conclusionId not found and inference result is empty");
            		return rtnJSONObj;
        		}
        	}
        	
        	//add인 경우 SCRDR이면 inference결과가 존재하지 않아야 함
        	//edit/delete이면 conclusionId는 inference결과중 하나여야 한다.
        	StringBuilder sb = new StringBuilder();
        	if (serviceUtil.isValidKaMode(kaMode, aCase, pConclusionId, sb) == false)
        	{
        		rtnJSONObj.put("validity", "error");
        		rtnJSONObj.put("msg", sb.toString());
        		return rtnJSONObj;
        	}
        	
        	rtnJSONObj = serviceUtil.initializeLearner(aCase, kaMode, pConclusionId);
        		
        	rtnJSONObj.put("validity", "valid");
            String syncMsg = "";
            if (sync) syncMsg = "ARFF의 추가항목이 DB에 반영됨 " + addedStr;
            rtnJSONObj.put("msg", syncMsg);
        	
	        return rtnJSONObj;
	        
        } catch (Exception ex) {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
        	rtnJSONObj.put("validity", "error");
			rtnJSONObj.put("msg", "ka init failed");
	        return rtnJSONObj;
        }
	}
	
	/** 단계별 지식획득 : 지식획득 엔진 초기화
	 * 
	 * @param pDomainName 도메인명
	 * @param pArffStr ARFF파일 contents string
	 * @param pConclusionId 결론ID (결론ID를 지정하지 않을 경우 -1 로 set, SCRDR이고 edit/delete인 경우 미지정 가능)
	 * @param pKaModeStr 지식획득모드 ("add", "alter", "edit", "delete")
	 * @param pUserId 사용자 ID
	 * @return
	 */
	public static JSONObject initializeByJSON(String pDomainName, 
								              String pJsonStr, 
								              int pConclusionId,
								              String pKaModeStr,
								              String pUserId)
	{
		JSONObject rtnJSONObj = new JSONObject();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, true) == false)
    	{
    		Logger.error("API, KAInitByJSON, check&init domain failed");

    		rtnJSONObj.put("validity", "error");
    		rtnJSONObj.put("msg", "domain initialize failed");
	        return rtnJSONObj;
    	}
		
        int kaMode = Learner.getKaModeByName(pKaModeStr);
        
        //edit,delete이고 MCRDR인 경우 conclusionId 가 필수임
        if (kaMode == Learner.KA_EXCEPTION_MODE || 
            kaMode == Learner.KA_STOPPING_MODE ||
            kaMode == Learner.KA_ALTER_MODE)
        {
        	if (Main.domain.isMCRDR() && pConclusionId < 0)
        	{
        		rtnJSONObj.put("validity", "error");
        		rtnJSONObj.put("msg", "conclusionId is not found");
    	        return rtnJSONObj;
        	}
        }
        
        if (kaMode == Learner.KA_ALTER_MODE && Main.domain.isSCRDR())
        {
        	rtnJSONObj.put("validity", "error");
        	rtnJSONObj.put("msg", "alter mode is not allowd for SCRDR");
    	    return rtnJSONObj;
        }
        
        if (kaMode < 0)
        {
        	Logger.error("API, KAInit, kaMode is wierd");
        		
        	rtnJSONObj.put("validity", "error");
        	rtnJSONObj.put("msg", "kaMode is wired");
    	    return rtnJSONObj;
        }
        
        JSONParser jsonParser = new JSONParser();
        JSONObject recvJSONObj = new JSONObject();
        
        try 
        {
        	recvJSONObj = (JSONObject)jsonParser.parse(pJsonStr);
        }
        catch (Exception ex) 
        {
        	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
        	rtnJSONObj.put("validity", "error");
        	rtnJSONObj.put("msg", ex.getClass().getName() + " : " + ex.getMessage());
	        return rtnJSONObj;
        }
        
        if (recvJSONObj == null)
		{
			Logger.error("request json string parsing failed");

        	rtnJSONObj.put("validity", "error");
			rtnJSONObj.put("msg", "request json string parsing failed");
			return rtnJSONObj;
		}
        
        try 
        {
        	HashMap<String, String> valueMap 
        		= JSONConverter.convertJSONObjectToValueMap(recvJSONObj);
        	
        	String[] msg = new String[1];
        	Case aCase 
        		= RDRInterface.getInstance().getCaseFromValueMap(Main.domain.getCaseStructure(), 
        				                                         valueMap, msg);
        	//aCase.setCaseId(RDRDBManager.getInstance().getNewCornerstoneCaseId());
        	//aCase.setCaseId(RDRInterface.getInstance().getCornerstoneCaseId(aCase));
        	
        	if (aCase == null)
        	{
        		rtnJSONObj.put("validity", "error");
        		rtnJSONObj.put("msg", msg[0]);
        	    return rtnJSONObj;
        	}
        	
        	RDRServiceUtil serviceUtil = new RDRServiceUtil();
        	
        	//edit/delete이고 SCRDR인 경우 conclusionId 가 없을때 inference결과로 conclusionId set
        	if (kaMode != Learner.KA_NEW_MODE &&
        		Main.domain.isSCRDR() && pConclusionId < 0)
        	{
        		pConclusionId = serviceUtil.checkConclusionId(kaMode, aCase);
        		if (pConclusionId < 0 )
        		{
        			rtnJSONObj.put("validity", "error");
            		rtnJSONObj.put("msg", "conclusionId not found and inference result is empty");
            		return rtnJSONObj;
        		}
        	}
        	
        	//add인 경우 SCRDR이면 inference결과가 존재하지 않아야 함
        	//edit/delete이면 conclusionId는 inference결과중 하나여야 한다.
        	StringBuilder sb = new StringBuilder();
        	if (serviceUtil.isValidKaMode(kaMode, aCase, pConclusionId, sb) == false)
        	{
        		rtnJSONObj.put("validity", "error");
        		rtnJSONObj.put("msg", sb.toString());
        		return rtnJSONObj;
        	}
        	
        	rtnJSONObj = serviceUtil.initializeLearner(aCase, kaMode, pConclusionId);
            	
            rtnJSONObj.put("validity", "valid");
            rtnJSONObj.put("msg", "");
        	return rtnJSONObj;
        	
        }catch (Exception ex ) {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );

        	rtnJSONObj = new JSONObject();
        	rtnJSONObj.put("validity", "error");
			rtnJSONObj.put("msg", "ka init failed");
			return rtnJSONObj;
        }
	}
	
	/** 단계별 지식획득 : 결론정보를 선택한다.
	 * 
	 * @param pDomainName 도메인 명
	 * @param pSelectedConclusionStr 선택된 결론정보
	 * @param pUserId
	 * @return
	 */
	public static JSONObject selectConclusion(String pDomainName,
			                                  String pSelectedConclusionStr,
			                                  String pUserId)
	{
		JSONObject rtnJSONObj = new JSONObject();
		
		if (RDRInterface.getInstance().isDomainChanged(pDomainName, pUserId))
    	{
    		Logger.error("API, selectConclusion, initialized domain is different");
    		
    		rtnJSONObj.put("validity", "error");
    		rtnJSONObj.put("msg", "initialized domain is different");
	        return rtnJSONObj;
    	}    
		
		String[] returnArray 
			= RDRInterface.getInstance().selectConclusion(pSelectedConclusionStr);
		
		rtnJSONObj.put("validity", returnArray[0]);
        rtnJSONObj.put("msg", returnArray[1]);
        return rtnJSONObj;
	}
	
	/** 단계별 지식획득 : 새로운 결론을 추가한다.
	 * 
	 * @param pDomainName 도메인명
	 * @param pNewConclusionStr 새로운 결론정보
	 * @param pUserId 사용자 id
	 * @return
	 */
	public static JSONObject addConclusion(String pDomainName,
			                               String pNewConclusionStr,
			                               String pUserId)
	{
		JSONObject rtnJSONObj = new JSONObject();
		
		if (RDRInterface.getInstance().isDomainChanged(pDomainName, pUserId))
    	{
    		Logger.error("API, addConclusion, initialized domain is different");
    		
    		rtnJSONObj.put("validity", "error");
    		rtnJSONObj.put("msg", "initialized domain is different");
	        return rtnJSONObj;
    	}  
		
		String[] returnArray 
			= RDRInterface.getInstance().addConclusion(pNewConclusionStr);
	
		rtnJSONObj.put("validity", returnArray[0]);
	    rtnJSONObj.put("msg", returnArray[1]);
	    return rtnJSONObj;
	}
	
	/** 단계별 지식획득 : 새로운 Rule의 조건을 추가한다.
	 * 
	 * @param pDomainName 도메인명
	 * @param pAttrName attribute명
	 * @param pOperator 연산자
	 * @param pValue 비교값
	 * @param pUserId 사용자 id
	 * @return
	 */
	public static JSONObject addCondition(String pDomainName,
			                              String pAttrName,
			                              String pOperator,
			                              String pValue,
			                              String pUserId)
	{
		JSONObject rtnJSONObj = new JSONObject();
		
		if (RDRInterface.getInstance().isDomainChanged(pDomainName, pUserId))
    	{
    		Logger.error("API, addCondition, initialized domain is different");
    		
    		rtnJSONObj.put("validity", "error");
    		rtnJSONObj.put("msg", "initialized domain is different");
	        return rtnJSONObj;
    	}  
		
		String[] returnArray 
	    	= RDRInterface.getInstance().addCondition(pAttrName, 
	    			                                  pOperator, 
	    			                                  pValue);
	    
	    rtnJSONObj.put("validity", returnArray[0]);
	    rtnJSONObj.put("msg", returnArray[1]);
	    return rtnJSONObj;
	}
	
	/** 단계별 지식획득 : 새로운 rule을 kb(DataBase) 에 등록한다.
	 * 
	 * @param pDomainName 도메인명
	 * @param pUserId 사용자 id
	 * @return
	 */
	public static JSONObject adddRule(String pDomainName,
			                          String pUserId)
	{
		JSONObject rtnJSONObj = new JSONObject();
		
		if (RDRInterface.getInstance().isDomainChanged(pDomainName, pUserId))
    	{
    		Logger.error("API, addRule, initialized domain is different");
    		
    		rtnJSONObj.put("validity", "error");
    		rtnJSONObj.put("msg", "initialized domain is different");
	        return rtnJSONObj;
    	}  
		
		StringBuilder sb = new StringBuilder();
        if (Main.workbench.executeAddingRule(sb))
        {
            Main.KB.setRuleSet(Main.workbench.getRuleSet());
            
            //불필요함, 속도느려짐
            //Main.KB.setRootRuleTree();
            
            RDRInterface.getInstance().reloadDomain();
            
            rtnJSONObj.put("validity", "valid");
            rtnJSONObj.put("msg", "");
        }
        else
        {
        	rtnJSONObj.put("validity", "error");
        	rtnJSONObj.put("msg", sb.toString());
        }
        
	    return rtnJSONObj;
	}
		
	/** 단계별 지식획득 : 새로운 Rule에 서 해당 조건을 삭제한다.
	 * 
	 * @param pDomainName 도메이명
	 * @param pAttrName attribute명
	 * @param pOperator 연산자
	 * @param pValue 비교값
	 * @param pUserId 사용자 id
	 * @return
	 */
	public static JSONObject deleteCondition(String pDomainName,
				                             String pAttrName,
				                             String pOperator,
				                             String pValue,
				                             String pUserId)
	{
		JSONObject rtnJSONObj = new JSONObject();
		
		if (RDRInterface.getInstance().isDomainChanged(pDomainName, pUserId))
    	{
    		Logger.error("API, deleteCondition, initialized domain is different");
    		
    		rtnJSONObj.put("validity", "error");
    		rtnJSONObj.put("msg", "initialized domain is different");
	        return rtnJSONObj;
    	}  
		
		String[] returnArray 
	    	= RDRInterface.getInstance().deleteCondition(pAttrName, 
	    			                                     pOperator, 
	    			                                     pValue);
	    
	    rtnJSONObj.put("validity", returnArray[0]);
	    rtnJSONObj.put("msg", returnArray[1]);
	    return rtnJSONObj;
	}
		
	/** 단계별 지식획득 : V&V 를 수행하고 충돌이 발생하는 코너스톤사례를 반환한다.
	 * 
	 * @param pDomainName 도메인명
	 * @param pUserId 사용자 id
	 * @return
	 */
	public static JSONObject getValidationCases(String pDomainName,
				                                String pUserId)
	{
		JSONObject rtnJSONObj = new JSONObject();
		
		if (RDRInterface.getInstance().isDomainChanged(pDomainName, pUserId))
		{
			Logger.error("API, getValidationCases, initialized domain is different");
			
			rtnJSONObj.put("validity", "error");
			rtnJSONObj.put("msg", "initialized domain is different");
	        return rtnJSONObj;
		}  
		
		String[] returnArray = RDRInterface.getInstance().validateRule();
		
		CornerstoneCaseSet aCornerstoneCaseSet 
			= RDRInterface.getInstance().getValidationCaseSet();
        JSONArray ccJSONArray 
        	= JSONConverter.convertCornerstoneCaseSetToJSONArray(Main.domain.getCaseStructure(), 
        			                                             aCornerstoneCaseSet);
        
        rtnJSONObj.put("validity", "valid");
        rtnJSONObj.put("msg", "");
        rtnJSONObj.put("count", aCornerstoneCaseSet.getCaseAmount());
        rtnJSONObj.put("validatingCases", ccJSONArray);
        return rtnJSONObj;
	}
		
	/** 단계별 지식획득 : 충돌이 발생하는 사례(pCornerstoneCaseId)의 모든 결론정보를 반환한다.
	 * 
	 * @param pDomainName 도메인명
	 * @param pCornerstoneCaseId 코너스톤사례id
	 * @param pUserId 사용자 id
	 * @return
	 */
	public static JSONObject getOtherConclusions(String pDomainName,
			                                     int pCornerstoneCaseId,
				                                 String pUserId)
	{
		JSONObject rtnJSONObj = new JSONObject();
		
		if (RDRInterface.getInstance().isDomainChanged(pDomainName, pUserId))
		{
			Logger.error("API, getOtherConclusions, initialized domain is different");
			
			rtnJSONObj.put("validity", "error");
			rtnJSONObj.put("msg", "initialized domain is different");
	        return rtnJSONObj;
		}  
		
		CornerstoneCase aCornerstoneCase 
			= Main.allCornerstoneCaseSet.getCornerstoneCaseById(pCornerstoneCaseId);
	    
		if (aCornerstoneCase == null)
		{
			rtnJSONObj.put("validity", "error");
			rtnJSONObj.put("msg", "CornerstoneCase not found");
			return rtnJSONObj;
		}
		
		//reasoner 의 current case set
	    Main.workbench.setValidatingCase(aCornerstoneCase);
	    Main.workbench.inferenceForValidation();
	    
	    //modified by ucciri, consider MCRDR/SCRDR
	    ConclusionSet conclusionSet = new ConclusionSet();
	    if (Main.domain.isMCRDR())
	    {
	    	RuleSet inferenceResult = (RuleSet) Main.workbench.getInferenceResult();
	    	conclusionSet = inferenceResult.getConclusionSet();
	    }
	    else if (Main.domain.isSCRDR())
	    {
	    	Rule inferenceResult = (Rule)Main.workbench.getInferenceResult();
	    	conclusionSet.addConclusion(inferenceResult.getConclusion());
	    }
	      
	    JSONArray otherConclusionsJSONArray = new JSONArray();
	    String[] conclusionArray = conclusionSet.toStringArrayForGUIWithoutAddConclusion();
	    for(int i=0; i<conclusionArray.length; i++){
	        otherConclusionsJSONArray.add(conclusionArray[i]);
	    }
	
	    JSONArray cornerstoneCaseJSONArray 
	    	= JSONConverter.convertCaseToJSONArray(Main.domain.getCaseStructure(), aCornerstoneCase);
	
	    rtnJSONObj.put("validity", "valid");
	    rtnJSONObj.put("msg", "");
	    rtnJSONObj.put("validatingCase", cornerstoneCaseJSONArray);
	    rtnJSONObj.put("otherConclusions", otherConclusionsJSONArray);
	    return rtnJSONObj;
	}
	
	/** 지식획득에 필요한 모든정보를 kaReq로 넘겨받고 V&V 수행 수 결과를 반환한다.
	 * 
	 * @param pDomainName 도메인명
	 * @param kaReq KARequest 정보 (사례, 수정대상결론Id, kaMode, 새로운 rule의 조건/결론)
	 * @param pUserId 사용자 id
	 * @return KAResponse
	 */
	public static KAResponse getValidationCasesBatch(String pDomainName, 
										             KARequest kaReq,
										             String pUserId)
	{	
		KAResponse tResponse = new KAResponse();

		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, true) == false)
		{
			Logger.error("API, getValidationCasesBatch, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("domain initialize failed");
	        return tResponse;
		}
		
        //kaReq.printLog();
        
        RDRServiceUtil serviceUtil = new RDRServiceUtil();
        tResponse = serviceUtil.setLearner(kaReq);
        
        if (tResponse.getStatus() == false)
        {
        	Logger.error("getValidationCasesBatch, set learner failed : " + tResponse.getMessage());
        	return tResponse;
        }
        	        
        String[] returnArray = RDRInterface.getInstance().validateRule();
        CornerstoneCaseSet aCornerstoneCaseSet 
			= RDRInterface.getInstance().getValidationCaseSet();
                    		
        //Logger.info("validation case count : " + aCornerstoneCaseSet.getCaseAmount());
        
        tResponse.setValidatingCaseCount(aCornerstoneCaseSet.getCaseAmount());
        tResponse.setValidatingCases(Main.domain.getCaseStructure(), aCornerstoneCaseSet);
        
        HashMap<Integer, CornerstoneCase> ccMap = aCornerstoneCaseSet.getBase();
        Iterator<Integer> keys = ccMap.keySet().iterator();
        while (keys.hasNext())
        {
        	Integer key = keys.next();
        	HashMap<Integer, String> aConclusionMap = serviceUtil.getOtherConclusions(key.intValue());
        	
        	if (aConclusionMap == null)
        	{
    	        tResponse.setStatus(false);
    			tResponse.setMessage("getOtherConclusion failed");
    			Logger.error("getValidationCasesBatch, get other conclusion failed");
    	        return tResponse;
        	}

	        //String[] conclusionArray = aConclusionSet.toStringArrayForGUIWithoutAddConclusion();
	        //for(int i = 0; i < conclusionArray.length; i++)
	        //{
	        //	tResponse.addOtherConclusion(key.intValue(), conclusionArray[i]);
	        //}
        	
        	boolean found = false;
        	String tmp;
        	Iterator<Integer> riter = aConclusionMap.keySet().iterator();
        	while (riter.hasNext())
        	{
        		Integer ruleId = riter.next();
        		String cname = aConclusionMap.get(ruleId);
        		
        		if (tResponse.hasWrongRule(ruleId))
        		{
        			found = true;
        			if (kaReq.getKAMode() == Learner.KA_STOPPING_MODE)
        				tmp = cname + "_delete";
        			else
        				tmp = cname + "_modify";
        		}
        		else
        			tmp = cname + "_";
        		
        		tResponse.addOtherConclusion(key.intValue(), tmp);
        	}
        	
        	if (found == false)
        	{
        		tmp = kaReq.getSelectedConclusion() + "_add";
        		tResponse.addOtherConclusion(key.intValue(), tmp);
        	}
        }

        Logger.info("getValidationCasesBatch is finished successfully");
        tResponse.setStatus(true);
        return tResponse;
	}
		
	/** 지식획득에 필요한 모든정보를 kaReq로 넘겨받고 Rule을 추가한다.
	 *  V&V 결과 충돌이 발생하더라도 무시하고 저장하므로 이 함수 호출전 반드시 getValidationCasesBatch 을 
	 *  수행해서 점검해야 한다.
	 *  
	 * @param pDomainName 도메인명
	 * @param kaReq KARequest 정보 (사례, 수정대상결론Id, kaMode, 새로운 rule의 조건/결론)
	 * @param pUserId 사용자 id
	 * @return KAResponse
	 */
	public static KAResponse addRuleBatch(String pDomainName, 
			                              KARequest kaReq,
										  String pUserId)
	{	
		KAResponse tResponse = new KAResponse();
	
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, true) == false)
		{
			Logger.error("API, addRuleBatch, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("domain initialize failed");
	        return tResponse;
		}

		Logger.info("addRuleBatch");
		//kaReq.printLog();
		        
        RDRServiceUtil serviceUtil = new RDRServiceUtil();
        tResponse = serviceUtil.setLearner(kaReq);
        
        if (tResponse.getStatus() == false)
        {
        	Logger.error("addRuleBatch, set learner failed : " + tResponse.getMessage());
        	return tResponse;
        }
        
        String[] returnArray = RDRInterface.getInstance().validateRule();
        CornerstoneCaseSet aCornerstoneCaseSet 
			= RDRInterface.getInstance().getValidationCaseSet();
            		
        Logger.info("addRuleBatch, validation case count : " + aCornerstoneCaseSet.getCaseAmount());
                    
        tResponse.setValidatingCaseCount(aCornerstoneCaseSet.getCaseAmount());
        
        StringBuilder sb = new StringBuilder();
        if (Main.workbench.executeAddingRule(sb))
        {
            Main.KB.setRuleSet(Main.workbench.getRuleSet());
            
            //불필요함, 속도느려짐
            //Main.KB.setRootRuleTree();
            
            RDRInterface.getInstance().reloadDomain();
            
            tResponse.setStatus(true);
			tResponse.setMessage("");
            Logger.info("add rule finished successfully");
        }
        else
        {
        	tResponse.setStatus(false);
			tResponse.setMessage(sb.toString());
        	Logger.error("add rule failed");
        }
		
        return tResponse;	
	}
	
	/** 모든 domain 정보를 반환한다.
	 *  RDRResponse.getDomains
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @return RDRResponse
	 */
	public static RDRResponse getAllDomains()
	{
		RDRResponse tResponse = new RDRResponse();
		
		ArrayList<HashMap<String, String>> domains 
			= RDRInterface.getInstance().getAllDomainDetails();
		
		for ( int i = 0; i < domains.size(); i++)
		{
			HashMap<String, String> domainDetail = domains.get(i);
			
			tResponse.addDomain(domainDetail.get("domainName"),
					            domainDetail.get("domainDesc"),
					            domainDetail.get("domainReasoner"));
		}
		
		tResponse.setStatus(true);
		return tResponse;
	}
	
	/** pConclusionId에 해당하는 결론정보를 반환한다.
	 *  RDRResponse.getConclusions()
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 * 
	 * @param pDomainName 도메인명
	 * @param pConclusionId 결론 id
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse getConclusion(String pDomainName, 
			                                int pConclusionId, 
			                                String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
    	if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getConclusion, check&init domain failed");

    		tResponse.setStatus(false);
    		tResponse.setMessage("check&init domain failed");
	        return tResponse;
    	}
    	
    	//get all conclusion
        TreeMap<Integer, String> conclusionMap 
        	= Main.KB.getConclusionSet().getIdConclusions();
        
        Iterator<Integer> keys = conclusionMap.keySet().iterator();
    	while (keys.hasNext())
    	{
    		Integer key = keys.next();
    		String name = conclusionMap.get(key);
    		if (key.intValue() == pConclusionId)
    		{
    			tResponse.addConclusion(pConclusionId, name);
    			break;
    		}
    	}
        
    	tResponse.setStatus(true);
        return tResponse;
	}
	
	/** 모든 결론정보를 반환한다.
	 *  RDRResponse.getConclusions()
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse getAllConclusions(String pDomainName, 
			                                    String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
    	if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getAllConclusions, check&init domain failed");

    		tResponse.setStatus(false);
    		tResponse.setMessage("check&init domain failed");
	        return tResponse;
    	}
    	
    	//get all conclusion
        TreeMap<Integer, String> conclusionMap 
        	= Main.KB.getConclusionSet().getIdConclusions();
        
        Iterator<Integer> keys = conclusionMap.keySet().iterator();
    	while (keys.hasNext())
    	{
    		Integer key = keys.next();
    		String name = conclusionMap.get(key);
    		
    		tResponse.addConclusion(key.intValue(), name);
    	}
    	
    	tResponse.setStatus(true);
        return tResponse;
	}
	
	/** 모든 연산자 정보를 반환한다.
	 *  RDRResponse.getOperators()
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pUserId 사용자id
	 * @return RDRResponse
	 */
	public static RDRResponse getAllOperators(String pDomainName, 
			                                  String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
    	if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getAllOperators, check&init domain failed");

    		tResponse.setStatus(false);
    		tResponse.setMessage("check&init domain failed");
	        return tResponse;
    	}
    	
    	HashMap<String, ArrayList<String>> opMap
    		= RDRInterface.getInstance().getPotentialOperators();
    	
    	Iterator<String> keys = opMap.keySet().iterator();
    	while (keys.hasNext())
    	{
    		String key = keys.next();
    		ArrayList<String> opList = opMap.get(key);
    		for (int i = 0; i < opList.size(); i++)
    		{
    			tResponse.addOperator(key, opList.get(i));
    		}
    	}
        
    	tResponse.setStatus(true);
        return tResponse;
	}
	
	/** pAttributeName에 해당하는 사례구조 정보를 반환한다.
	 *  RDRResponse.getAttrItems()
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 * 
	 * @param pDomainName 도메인명
	 * @param pAttributeName attribute명
	 * @param pUserId 사용자id
	 * @return RDRResponse
	 */
	public static RDRResponse getCaseStructure(String pDomainName, 
			                                 String pAttributeName,
			                                 String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
	
    	if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getCaseStructure, check&init domain failed");

    		tResponse.setStatus(false);
    		tResponse.setMessage("check&init domain failed");
	        return tResponse;
    	}
    	
    	LinkedHashMap<String, IAttribute> attrMap = Main.domain.getCaseStructure().getBase();
        Iterator<String> keyItr = attrMap.keySet().iterator();
        while (keyItr.hasNext())
        {
        	String key = keyItr.next();
        	IAttribute tAttribute = attrMap.get(key);
        	
        	if (pAttributeName != null && 
        		pAttributeName.isEmpty() == false && 
        		pAttributeName.equals(tAttribute.getName()) == false)
        		continue;
        	
        	tResponse.addAttribute(tAttribute);
        }
        
        tResponse.setStatus(true);
        return tResponse;
	}
		
	/** pRuleId에 해당하는 rule 정보를 반환한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  RDRResponse.getRules()
	 *  
	 * @param pDomainName 도메인명
	 * @param pRuleId rule id
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse getRule(String pDomainName, 
			                         int pRuleId, 
			                         String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
    	if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getRule, check&init domain failed");

    		tResponse.setStatus(false);
    		tResponse.setMessage("check&init domain failed");
	        return tResponse;
    	}
    	
    	LinkedHashMap<Integer, Rule> aRuleSet = Main.KB.getBase();
		Iterator<Integer> keyItr = aRuleSet.keySet().iterator();
		while (keyItr.hasNext())
		{
			Integer key = keyItr.next();
			Rule aRule = aRuleSet.get(key);
			
			if (key.intValue() == pRuleId)
			{
				tResponse.addRule(aRule);
				break;
			}
		}
    	
		tResponse.setStatus(true);
        return tResponse;
	}
	
	/** 모든 Rule 정보를 반환한다.
	 *  RDRResponse.getRules()
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse getAllRules(String pDomainName, 
			                            String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
    	if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getAllRules, check&init domain failed");

    		tResponse.setStatus(false);
    		tResponse.setMessage("check&init domain failed");
	        return tResponse;
    	}
    	
    	LinkedHashMap<Integer, Rule> aRuleSet = Main.KB.getBase();
		Iterator<Integer> keyItr = aRuleSet.keySet().iterator();
		while (keyItr.hasNext())
		{
			Integer key = keyItr.next();
			Rule aRule = aRuleSet.get(key);
			tResponse.addRule(aRule);
		}

		tResponse.setStatus(true);
        return tResponse;
	}
	
	/** pRuleId의 모든 하위 rule 정보를 반환한다.
	 *  RDRResponse.getRules()
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pRuleId rule id
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse getChildRules(String pDomainName, 
			                               int pRuleId,
			                               String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
    	if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getChiledRules, check&init domain failed");

    		tResponse.setStatus(false);
    		tResponse.setMessage("check&init domain failed");
	        return tResponse;
    	}

		Rule aRule = Main.KB.getRuleById(pRuleId);
		if (aRule == null)
		{
			tResponse.setStatus(false);
    		tResponse.setMessage("rule not found : " + pRuleId);
	        return tResponse;
		}
		
		RuleSet childRuleSet = aRule.getAllChildRules();
		LinkedHashMap<Integer, Rule> tRuleMap = childRuleSet.getBase();
		Iterator<Integer> keyItr = tRuleMap.keySet().iterator();
		while (keyItr.hasNext())
		{
			Integer key = keyItr.next();
			Rule tRule = tRuleMap.get(key);
			
			if (tRule.getRuleId() == pRuleId)
				continue;

			tResponse.addRule(tRule);
		}

		tResponse.setStatus(true);
        return tResponse;
	}
	
	
	public static RDRResponse getPathRules(String pDomainName, 
			                               int pRuleId,
			                               String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
    	if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getPathRules, check&init domain failed");

    		tResponse.setStatus(false);
    		tResponse.setMessage("check&init domain failed");
	        return tResponse;
    	}

		Rule aRule = Main.KB.getRuleById(pRuleId);
		if (aRule == null)
		{
			tResponse.setStatus(false);
    		tResponse.setMessage("rule not found : " + pRuleId);
	        return tResponse;
		}
		
		RuleSet pathRuleSet = aRule.getPathRuleSet(false);
		LinkedHashMap<Integer, Rule> tRuleMap = pathRuleSet.getBase();
		Iterator<Integer> keyItr = tRuleMap.keySet().iterator();
		while (keyItr.hasNext())
		{
			Integer key = keyItr.next();
			Rule tRule = tRuleMap.get(key);

			tResponse.addRule(tRule);
		}

		tResponse.setStatus(true);
        return tResponse;
	}
	
	
	/** pCornerstoneCaseId 에 해당하는 사례정보를 반환한다.
	 *  RDRResponse.getCases()
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pCornerstoneCaseId 코너스톤사례id
	 * @param pUserId 사용자id
	 * @return RDRResponse
	 */
	public static RDRResponse getCornerstoneCase(String pDomainName, 
			                                   int pCornerstoneCaseId,
			                                   String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
    	if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getCornerstoneCase, check&init domain failed");

    		tResponse.setStatus(false);
    		tResponse.setMessage("check&init domain failed");
	        return tResponse;
    	}
    	
    	CornerstoneCaseSet aCCSet = RDRInterface.getInstance().getCornerstoneCaseSet();
    	
    	HashMap<Integer, CornerstoneCase> ccMap = aCCSet.getBase();
		Iterator<Integer> keyIter = ccMap.keySet().iterator();
		while (keyIter.hasNext())
		{
			Integer key = keyIter.next();
			CornerstoneCase cc = ccMap.get(key);
			
			if (key.intValue() == pCornerstoneCaseId)
			{
				tResponse.addCase(Main.domain.getCaseStructure(), cc, pCornerstoneCaseId);
				break;
			}
		}
		
		tResponse.setStatus(true);
        return tResponse;
	}
	
	/** 모든 코너스톤사례 정보를 반환한다
	 *  RDRResponse.getCases()
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pUserId 사용자id
	 * @return RDRResponse
	 */
	public static RDRResponse getAllCornerstoneCases(String pDomainName, 
			                                        String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
    	if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getAllCornerstoneCases, check&init domain failed");

    		tResponse.setStatus(false);
    		tResponse.setMessage("check&init domain failed");
	        return tResponse;
    	}
    	
    	CornerstoneCaseSet aCCSet = RDRInterface.getInstance().getCornerstoneCaseSet();
    	
    	HashMap<Integer, CornerstoneCase> ccMap = aCCSet.getBase();
		Iterator<Integer> keyIter = ccMap.keySet().iterator();
		while (keyIter.hasNext())
		{
			Integer key = keyIter.next();
			CornerstoneCase cc = ccMap.get(key);
			
			tResponse.addCase(Main.domain.getCaseStructure(), cc, key.intValue());
		}
		
		tResponse.setStatus(true);
        return tResponse;
	}
	
	/** null value 문자를 반환한다.
	 * 
	 * @param pDomainName 도메인명
	 * @param pUserId 사용자 id
	 * @return list of null value string
	 */
	public static ArrayList<String> getNullValueString(String pDomainName, String pUserId)
	{
		ArrayList<String> tList = new ArrayList<String>();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
    	{
    		Logger.error("API, getNullValueString, check&init domain failed");
    		return tList;
    	}
		
		return RDRConfig.getNullValues();
	}
	
	/** RDR Talbe을 생성한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 * 
	 * @return RDRResponse
	 */
	public static RDRResponse createRDRTables()
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().createRDRTables())
		{
			tResponse.setStatus(true);	
			tResponse.setMessage("");
		}
		else
		{
			tResponse.setStatus(false);	
			tResponse.setMessage("table creation failed");
		}
		
		tResponse.setStatus(true);
		return tResponse;
	}
	
	/** 해당 정보로 db 접속을 수행한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param dbType dataBase 유형 (mysql, mariadb, sqlite, mssql)
	 * @param sqliteFile sqlite file (database유형이 sqllite인 경우 setting)
	 * @param dbDriver db driver
	 * @param dbURL db url
	 * @param dbName db name
	 * @param user 사용자
	 * @param pass 비밀번호
	 * @return RDRResponse
	 */
	public static RDRResponse connectDataBase(String dbType,
			                                  String sqliteFile,
			                                  String dbDriver,
			                                  String dbURL,
			                                  String dbName,
			                                  String user,
			                                  String pass)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().connectDataBase(dbType, 
				                                       sqliteFile, 
				                                       dbDriver, 
                                                       dbURL, 
                                                       dbName, 
                                                       user, 
                                                       pass))
		{
			tResponse.setStatus(true);	
			tResponse.setMessage("");
		}
		else
		{
			tResponse.setStatus(false);	
			tResponse.setMessage("connectDB failed");
		}
		
		return tResponse;
	}
	
	/** 결론명을 수정한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pConclusionId 결론id
	 * @param pConclusionName 결론명
	 * @param pUserId 사용자id
	 * @return RDRResponse
	 */
	public static RDRResponse editConclusion(String pDomainName, 
                                            int pConclusionId,
                                            String pConclusionName,
                                            String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, editConclusion, check&init domain failed");
		
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
	
		String[] msg = new String[1];
		boolean flag 
			= RDRInterface.getInstance().editConclusionName(pConclusionId,
					                                        pConclusionName, msg);    
					
		if (flag)
		{
			RDRInterface.getInstance().reloadDomain();
		}
		
		tResponse.setStatus(flag);;
		tResponse.setMessage(msg[0]);
		return tResponse;
	}
	
	/** ARFF를 input으로 사례구조를 setting 한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pArffStr 사례구조가 작성된 ARFF 파일명
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse setCaseStructureByARFFFileName(String pDomainName, 
												             String pArffFn,
												             String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();

		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, setCaseStructureByARFF, check&init domain failed");
		
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		
		String fn = RDRConfig.getArffFile();
	
		try
		{
			Utility.copyFile(pArffFn, fn);
		}
		catch (IOException ex)
		{
			Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
			tResponse.setStatus(false);
			tResponse.setMessage(ex.getClass().getName() + " : " + ex.getMessage());
			return tResponse;
		}
		
		boolean flag = true;
		try
		{
			CaseStructure aCaseStructure = CaseLoader.getArffCaseStructure();
			flag = RDRInterface.getInstance().insertCaseStructure(aCaseStructure);
		}
		catch (Exception ex)
		{
			Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
			flag = false;
		}
		
		if (flag)
		{
			tResponse.setStatus(true);	
			tResponse.setMessage("");
        	
        	RDRInterface.getInstance().reloadDomain();
    		return tResponse;
		}
		else
		{
			tResponse.setStatus(false);	
			tResponse.setMessage("caseStructure insert failed");
			return tResponse;
		}
	}
	
	/** ARFF를 input으로 사례구조를 setting 한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pArffStr 사례구조가 작성된 ARFF 파일내용
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse setCaseStructureByARFF(String pDomainName, 
										            String pArffStr,
										            String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();

		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, setCaseStructureByARFF, check&init domain failed");
		
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		
		String fn = RDRConfig.getArffFile();
	
		try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fn));
            out.write(pArffStr); //out.newLine();
            out.close();
        } catch (IOException e) {
        	Logger.error(e.getClass().getName() + " : " + e.getMessage(), e);
	        tResponse.setStatus(false);	
			tResponse.setMessage(e.getClass().getName() + " : " + e.getMessage());
			return tResponse;
        }
		
		boolean flag = true;
		try
		{
			CaseStructure aCaseStructure = CaseLoader.getArffCaseStructure();
			flag = RDRInterface.getInstance().insertCaseStructure(aCaseStructure);
		}
		catch (Exception ex)
		{
			Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
			flag = false;
		}
		
		if (flag)
		{
			tResponse.setStatus(true);	
			tResponse.setMessage("");
        	
        	RDRInterface.getInstance().reloadDomain();
    		return tResponse;
		}
		else
		{
			tResponse.setStatus(false);	
			tResponse.setMessage("caseStructure insert failed");
			return tResponse;
		}
	}
	
	/** JSON을 input으로 사례구조를 setting 한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pJsonStr 사례구조가 작성된 JSON String (format은 API 문서 참고)
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse setCaseStructureByJSON(String pDomainName, 
										            String pJsonStr,
										            String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();

		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, setCaseStructureByJSON, check&init domain failed");
		
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		
		JSONParser jsonParser = new JSONParser();
		JSONArray recvJSONArray = new JSONArray();
        
        try 
        {
        	//String tmp = "[{\"name\":\"코드\", \"id\":\"1\"}, {\"name\":\"bbb\", \"id\":\"2\"}]";
        	//recvJSONArray = (JSONArray)jsonParser.parse(tmp);
        	
//        	System.out.println(pJsonStr);
//        	pJsonStr = pJsonStr.replace("\n", "");
//        	pJsonStr = pJsonStr.replace("\r", "");
//        	//pJsonStr = pJsonStr.replace(" ", "");
//        	System.out.println(pJsonStr);
        	//Object obj = jsonParser.parse(pJsonStr);
        	recvJSONArray = (JSONArray)jsonParser.parse(pJsonStr);
        }
        catch (Exception ex ) 
        {
        	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
	        tResponse.setStatus(false);	
			tResponse.setMessage(ex.getClass().getName() + " : " + ex.getMessage());
			return tResponse;
        }
        
        if (recvJSONArray == null)
		{
			Logger.error("request json string parsing failed");
			tResponse.setStatus(false);	
			tResponse.setMessage("request json string parsing failed");
			return tResponse;
		}
	
        CaseStructure recvCaseStructure
			= JSONConverter.convertJSONArrayToCaseStructure(recvJSONArray);
	    
	    if (RDRInterface.getInstance().insertCaseStructure(recvCaseStructure))
	    {
	    	tResponse.setStatus(true);	
			tResponse.setMessage("");
	    	
	    	RDRInterface.getInstance().reloadDomain();
	    	return tResponse;
	    }
	    else
	    {
			tResponse.setStatus(false);	
			tResponse.setMessage("caseStructure insert failed");
			return tResponse;
	    }
	}
    
	/** attribute 명을 수정한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pAttributeName 수정대상 attribute명
	 * @param pNewAttributeName 새로운 attribute명
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse editAttributeName(String pDomainName, 
									           String pAttributeName,
									           String pNewAttributeName,
									           String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, editAttributeName, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
	
		if (RDRInterface.getInstance().editAttributeName(pDomainName, 
				                                         pAttributeName, 
				                                         pNewAttributeName))
		{
			RDRInterface.getInstance().reloadDomain();
			
			tResponse.setStatus(true);	
			tResponse.setMessage("");
			return tResponse;
		}
		else
		{
			tResponse.setStatus(false);	
			tResponse.setMessage("attribute name edit failed");
			return tResponse;
		}
	}
		
	/** attribute description을 수정한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pAttributeName attribute명
	 * @param pAttributeDesc 수정할 attribute description
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse editAttributeDesc(String pDomainName, 
									           String pAttributeName,
									           String pAttributeDesc,
									           String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, editAttributeDesc, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
	
		if (RDRInterface.getInstance().editAttributeDesc(pDomainName, 
				                                         pAttributeName, 
				                                         pAttributeDesc))
		{
			RDRInterface.getInstance().reloadDomain();
			
			tResponse.setStatus(true);	
			tResponse.setMessage("");
			return tResponse;
		}
		else
		{
			tResponse.setStatus(false);	
			tResponse.setMessage("attribute description edit failed");
			return tResponse;
		}
	}
		
	/** domain을 추가한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pDomainDesc 도메인 설명
	 * @param pDomainReasoner resoner (SCRDR/MCRDR)
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse addDomain(String pDomainName, 
									   String pDomainDesc,
									   String pDomainReasoner,
									   String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
	
		if (RDRInterface.getInstance().addDomain(pDomainName, 
				                                 pDomainDesc, 
				                                 pDomainReasoner, 
				                                 pUserId))
    	{
			tResponse.setStatus(true);	
			tResponse.setMessage("");
    		
    		RDRInterface.getInstance().initializeDomain(pDomainName);
    		return tResponse;
    	}
    	else
    	{
    		tResponse.setStatus(false);	
			tResponse.setMessage("insert domain failed");
    		return tResponse;
    	}
	}
		
	/** 사례구조를 추가한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인 명
	 * @param pJsonStr 사례구조가 작성된 JSON String (format은 API 문서 참고)
	 * @param pUserId 사용자 id
	 * @param pAddedAttribute 추가된 attribute명을 반환
	 * @return RDRResponse
	 */
	public static RDRResponse addCaseStructure(String pDomainName, 
										      String pJsonStr,
										      String pUserId,
										      ArrayList<String> pAddedAttribute)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, addCaseStructure, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		
		JSONParser jsonParser = new JSONParser();
        JSONArray stJSONArray = new JSONArray();
        
        try 
        {
        	stJSONArray = (JSONArray)jsonParser.parse(pJsonStr);
        }
        catch (Exception ex ) 
        {
        	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
	        tResponse.setStatus(false);	
			tResponse.setMessage(ex.getClass().getName() + " : " + ex.getMessage());
			return tResponse;
        }
        
        if (stJSONArray == null)
		{
			Logger.error("request json string parsing failed");
			tResponse.setStatus(false);	
			tResponse.setMessage("request json string parsing failed");
			return tResponse;
		}
        
        CaseStructure recvCaseStructure
			= JSONConverter.convertJSONArrayToCaseStructure(stJSONArray);
	
		CaseStructure dbCaseStructure = Main.domain.getCaseStructure();
		
		//사례구조 비교
		String[] msg = new String[1];
		int cnt = RDRInterface.getInstance().compareCaseStructure(recvCaseStructure, 
				                                                  dbCaseStructure,
				                                                  msg);
		
		if (cnt > 0)
		{
			//추가된 항목을 DB에 insert
			ArrayList<String> addedAttr 
				= RDRInterface.getInstance().syncCaseStructure(recvCaseStructure, 
						                                       dbCaseStructure);
			
			tResponse.setStatus(true);	
			tResponse.setMessage("");
			
			pAddedAttribute.addAll(addedAttr);
			
			RDRInterface.getInstance().reloadDomain();
			return tResponse;
		}
		else if (cnt == 0)
		{
			tResponse.setStatus(true);	
			tResponse.setMessage("사례구조가 동일합니다. 추가한 항목이 없습니다");
			return tResponse;
		}
		else
		{
			tResponse.setStatus(false);	
			tResponse.setMessage("msg[0]");
			return tResponse;
		}
	}		
	
	/** attribute를 추가한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pAttrItem attribute 정보
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse addAttribute(String pDomainName, 
                                           AttributeItem pAttrItem,
		                                   String pUserId)
	{
		return RDRService.addAttribute(pDomainName, pAttrItem.createAttribute(), pUserId);
	}
		
	public static RDRResponse addAttribute(String pDomainName, 
			                               IAttribute pNewAttr,
									       String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, addAttribute, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		
        try 
        {
       		StringBuilder sb = new StringBuilder();
        	if (RDRInterface.getInstance().insertAttribute(pNewAttr, sb))
        	{
            	RDRInterface.getInstance().reloadDomain();
            	
            	tResponse.setStatus(true);	
    			tResponse.setMessage("");
    			return tResponse;
        	}
        	else
        	{
        		tResponse.setStatus(false);	
    			tResponse.setMessage(sb.toString());
    			return tResponse;
        	}
        }
        catch (Exception ex ) 
        {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
		
			tResponse.setStatus(false);	
			tResponse.setMessage("add attribute exception failed");
			return tResponse;
        }
	}		
	
	/** categorical value를 추가한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도에인명
	 * @param pAttributeName attribute명
	 * @param pCatValue categorical value
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse addCategorical(String pDomainName, 
									        String pAttributeName,
									        String pCatValue,
									        String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, addCategorical, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		
        try 
        {
        	StringBuilder sb = new StringBuilder();
        	if (RDRInterface.getInstance().addCategoricalValue(pAttributeName, pCatValue, sb))
        	{
            	RDRInterface.getInstance().reloadDomain();

            	tResponse.setStatus(true);	
    			tResponse.setMessage("");
    			return tResponse;
        	}
        	else
        	{
        		tResponse.setStatus(false);	
    			tResponse.setMessage(sb.toString());
    			return tResponse;
        	}
        }
        catch (Exception ex ) 
        {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );

			tResponse.setStatus(false);	
			tResponse.setMessage(ex.getClass().getName() + ": " + ex.getMessage());
			return tResponse;
        }
	}				
	
	/** 코너스톤 사례를 추가한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param valueMap 사례정보(항목명, 항목값)
	 * @param pUserId 사용자 id
	 * @param pAddedRules 코너스톤사례가 추가된 rule id
	 * @return RDRResponse
	 */
	public static RDRResponse addCornerstoneCase(String pDomainName, 
			                                     HashMap<String, String> valueMap,
									             String pUserId,
									             ArrayList<Integer> pAddedRules)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, addCornerstoneCase, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		
		String[] msg = new String[1];
		Case aCase 
			= RDRInterface.getInstance().getCaseFromValueMap(Main.domain.getCaseStructure(), 
					                                         valueMap, msg);
		if (aCase == null)
		{
        	Logger.error("case creation failed");
        	
        	tResponse.setStatus(false);	
			tResponse.setMessage(msg[0]);
			return tResponse;
		}
		
		return RDRService.addCornerstoneCase(pDomainName, aCase, pUserId, pAddedRules);
	}
		
	/** 코너스톤 사례를 추가한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pCase 사례
	 * @param pUserId 사용자 id
	 * @param pAddedRules 코너스톤사례가 추가된 rule id
	 * @return RDRResponse
	 */
	public static RDRResponse addCornerstoneCase(String pDomainName, 
									            Case pCase,
									            String pUserId,
									            ArrayList<Integer> pAddedRules)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, addCornerstoneCase, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		
        try 
        {
        	pCase.setCaseId(RDRInterface.getInstance().getCornerstoneCaseId(pCase));
			Logger.info("case id : " + pCase.getCaseId());
	    	            	
			ArrayList<Integer> addedRules = new ArrayList<Integer>();
	    	if (RDRInterface.getInstance().addCornerstoneCase(pCase, addedRules))
	    	{
	    		pAddedRules.addAll(addedRules);
	    		
	    		tResponse.setStatus(true);	
				tResponse.setMessage("");
				return tResponse;
	    	}
	    	else
	    	{
				tResponse.setStatus(false);	
				tResponse.setMessage("add cornerstoneCase failed");
				return tResponse;
	    	}
        }
        catch (Exception ex ) 
        {
        	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );

			tResponse.setStatus(false);	
			tResponse.setMessage(ex.getClass().getName() + ": " + ex.getMessage());
			return tResponse;
        }
	}				
		
	/** domain을 삭제 (domain에 해당하는 모든 kb 정보를 삭제한다)
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 삭제할 도메인명
	 * @return RDRResponse
	 */
	public static RDRResponse deleteDomain(String pDomainName)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().deleteAllDomainData(pDomainName))
		{
			tResponse.setStatus(true);	
			tResponse.setMessage("");
		}
		else
		{
			tResponse.setStatus(false);	
			tResponse.setMessage("domain data delete fail, rollback all transaction");
		}
		
        RDRInterface.getInstance().initializeDomain();
		return tResponse;
	}
	
	/** attribute를 삭제한다. 해당 attribute가 조건에 사용중이면 삭제불가 처리 함
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pAttributeName attribute 명
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse deleteAttribute(String pDomainName, 
									         String pAttributeName,
									         String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, deleteAttribute, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		
		if (RDRInterface.getInstance().deleteAttribute(pDomainName, pAttributeName))
		{
			RDRInterface.getInstance().reloadDomain();
			
			tResponse.setStatus(true);	
			tResponse.setMessage("");
			return tResponse;
		}
		else
		{
			tResponse.setStatus(false);	
			tResponse.setMessage("해당 항목을 삭제할 수 없습니다");
			return tResponse;
		}
	}
	
	/** rule을 삭제한다. (leaf rule만 삭제가능함)
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  
	 * @param pDomainName 도메인명
	 * @param pRuleId 삭제할 rule id
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse deleteRule(String pDomainName, 
									    int pRuleId,
									    String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, editAttributeDesc, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		
		StringBuilder sb = new StringBuilder();
		if (RDRInterface.getInstance().deleteRule(pDomainName, pRuleId, sb))
		{
			RDRInterface.getInstance().reloadDomain();
			
			tResponse.setStatus(true);	
			tResponse.setMessage("");
			return tResponse;
		}
		else
		{
			tResponse.setStatus(false);	
			tResponse.setMessage(sb.toString());
			return tResponse;
		}
	}
	
	/** 유사도 정보를 반환한다.
	 *  RDRResponse.getStatus() : 정상처리 여부
	 *  RDRResponse.getMessage() : 에러시 에러메세지
	 *  RDRResponse.getSimilarities() : 유사도 정보
	 *  
	 * @param pDomainName 도메인명
	 * @param pCase 기준 사례
	 * @param pCompareCases 비교사례
	 * @param pCompareAttributes 유사도를 체크할 항목명
	 * @param pUserId 사용자 id
	 * @return RDRResponse
	 */
	public static RDRResponse getSimilarity(String pDomainName,
			                               Case pCase,
			                               ArrayList<Case> pCompareCases,
			                               ArrayList<String> pCompareAttributes,
			                               String pUserId)
	{
		RDRResponse tResponse = new RDRResponse();
		
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, getSimilarity, check&init domain failed");
			
			tResponse.setStatus(false);
			tResponse.setMessage("check&init domain failed");
			return tResponse;
		}
		        
        // calculate similarity
		SimilaritySolution solution 
			= new SimilaritySolution(pCase, pCompareCases, pCompareAttributes);
		
		ArrayList<SimilarityElement> result = solution.buildSolution();
		
		tResponse.setStatus(true);	
		tResponse.setMessage("");

		for (int li =0; li < result.size(); li++)
		{
			tResponse.addSimilarity(result.get(li).getCompareCase().getCaseId(),
					                result.get(li).getValue().doubleValue());
		}
		
		return tResponse;
	}
	
	public static boolean writeRuleTreeReport(String pDomainName, 
			                                  LinkedHashMap<String, String> filter,
			                                  StringBuilder sbFileName,
			                                  String pUserId)
	{
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, writeRuleTreeReport, check&init domain failed");
			return false;
		}
		
		ReportRuleTree tReport = new ReportRuleTree();
		tReport.setFiltering(filter);
		return tReport.reportRuleSummary(sbFileName);
	}

	
	public static String getRuleTreeReportJSON(String pDomainName, 
			                                   LinkedHashMap<String, String> filter,
			                                   String pComment,
			                                   String pUserId)
	{
		if (RDRInterface.getInstance().checkDomain(pDomainName, pUserId, false) == false)
		{
			Logger.error("API, getRuleTreeReportJSON, check&init domain failed");
			return "";
		}
		
		ReportRuleTree tReport = new ReportRuleTree();
		tReport.setFiltering(filter);
		tReport.setComment(pComment);
		return tReport.getReportJSON();
	}
	
	public static boolean addFirstLevelRule(String pDomainName, Case pCase, String pUserId)
	{
		KARequest kaRequest = new KARequest();
		
		kaRequest.clear();
    	kaRequest.setKAMode(Learner.KA_NEW_MODE);
    	kaRequest.setCase(pCase);
    	kaRequest.setSelectedConclusion("no rule for this testCode");
    	RDRBroker broker = RDRBrokerFactory.createBroker();
    	ArrayList<String> attrList = broker.getFirstLevelAttributes();
    	
    	if (attrList == null || attrList.isEmpty())
    		return true;
    	
    	/** first level rule의 항목이 모두 존재 해야 함, 그렇지 않으면 에러 처리 */
    	for (int i = 0; i < attrList.size(); i++)
    	{
    		if (pCase.isNullValue(attrList.get(i)))
    		{
    			Logger.error("failed adding first level rule, [" + attrList.get(i) + "] is missing");
    			return false;
    		}
    	}
    	
    	kaRequest.addCondition(pCase, attrList, false);
    	
    	if (kaRequest.isValid() == false)
    	{
    		Logger.error("addFirstLevelRule, KA Request is invalid");
    		kaRequest.printLog();
    		return false;
    	}
    	
    	KAResponse tResponse
    		= RDRService.addRuleBatch(pDomainName, kaRequest, pUserId);
    	
    	if (tResponse.getStatus() == false)
    	{
    		Logger.error("addFirstLevelRule, " + tResponse.getMessage());
    		return false;
    	}
    	
    	//check sanity
    	if (tResponse.getValidatingCaseCount() > 0 )
    	{
    		Logger.error("validating case count : " + tResponse.getValidatingCaseCount());
    		kaRequest.printLog();
    	}
    	
    	Logger.info("first level rule added, " + tResponse.getJSON().toString());
    	
    	return true;
	}
			                             

	
	
	
	
	
	
	
	

} //end of class
