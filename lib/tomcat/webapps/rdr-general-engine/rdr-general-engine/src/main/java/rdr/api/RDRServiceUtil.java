package rdr.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rdr.apimsg.ConditionItem;
import rdr.apimsg.KARequest;
import rdr.apimsg.KAResponse;
import rdr.apimsg.RDRResponse;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CornerstoneCase;
import rdr.learner.Learner;
import rdr.logger.Logger;
import rdr.rules.Conclusion;
import rdr.rules.ConclusionSet;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;
import rdr.rules.RuleSet;
import rdr.utils.Utility;

public class RDRServiceUtil 
{
	public RDRServiceUtil()
	{
		;
	}
	
	/** get infernece result for aCase
	 * 
	 * @param aCase case for inference
	 * @return inference result
	 */
	public RDRResponse inference(Case aCase)
	{
		RDRResponse tResponse = new RDRResponse();
		
    	Object inferenceResult = RDRInterface.getInstance().getInferenceResult(aCase);
    	
    	if (inferenceResult == null)
    	{
    		tResponse.setStatus(false);
    		tResponse.setMessage("inference failed");
    		return tResponse;
    	}
    	
    	if (Main.domain.isSCRDR())
    	{
    		Rule aRule = (Rule)inferenceResult;
	    	RuleSet aFiredRuleSet = RDRInterface.getInstance().getFiredRules(aCase, true);
	    	
	    	LinkedHashMap<Integer, Rule> fRules = aFiredRuleSet.getBase();
	    	Iterator<Integer> keyItr = fRules.keySet().iterator();
	    	while (keyItr.hasNext())
	    	{
	    		Integer key = keyItr.next();
	    		Rule tRule = fRules.get(key);
	    		tResponse.addFiredRule(tRule);
	    	}
	    	
	    	tResponse.addInferenceResult(aRule);
	    	tResponse.setStatus(true);
	    	
	    	return tResponse;
    	}
    	else if (Main.domain.isMCRDR())
    	{
    		RuleSet aRuleSet = (RuleSet)inferenceResult;
	    	RuleSet aFiredRuleSet = RDRInterface.getInstance().getFiredRules(aCase, true);
	    	
	    	LinkedHashMap<Integer, Rule> fRules = aFiredRuleSet.getBase();
	    	Iterator<Integer> keyItr = fRules.keySet().iterator();
	    	while (keyItr.hasNext())
	    	{
	    		Integer key = keyItr.next();
	    		Rule tRule = fRules.get(key);
	    		tResponse.addFiredRule(tRule);
	    	}
	    	
	    	LinkedHashMap<Integer, Rule> infRules = aRuleSet.getBase();
	    	Iterator<Integer> keyInfItr = infRules.keySet().iterator();
	    	while (keyInfItr.hasNext())
	    	{
	    		Integer key = keyInfItr.next();
	    		Rule tRule = infRules.get(key);
	    		tResponse.addInferenceResult(tRule);
	    	}
	    	
	    	tResponse.setStatus(true);
	    	
	    	return tResponse;
    	}
    	
		return null;
	}
	
	
	public JSONObject initializeLearner(Case aCase, int kaMode, int conclusionId) throws Exception
	{
		JSONObject rtnJSONObj = new JSONObject();
    	
	    Conclusion wrongConclusion = null;
	    String wrongConclusionName = "";
	    if(kaMode != Learner.KA_NEW_MODE) {
	        wrongConclusion = Main.KB.getConclusionSet().getConclusionById(conclusionId);
	        wrongConclusionName = wrongConclusion.getConclusionName();
	    }
	        
	    RDRInterface.getInstance().learnerInit(aCase, kaMode, wrongConclusion);
	
	    /** learner의 inferenceResult를 setting하기 위해 Workbench.inference()를 호출해야하는데
	     *  여기서 수행된다. */
	    ConditionSet usedConditionSet 
	    	= RDRInterface.getInstance().getUsedConditionSet(aCase, wrongConclusion);
	    JSONArray usedConditionJSONArray 
	      	= JSONConverter.convertConditionSetToJSONArray(usedConditionSet);
	        
	    rtnJSONObj.put("usedConditionSet", usedConditionJSONArray);
	    rtnJSONObj.put("wrongConclusion", wrongConclusionName);

        return rtnJSONObj;
	}
	
	public int checkConclusionId(int kaMode, Case aCase)
	{
		if (Main.domain.isSCRDR())
		{
			Rule inferenceResult 
				= (Rule)RDRInterface.getInstance().getInferenceResult(aCase);
			
			if (Utility.isEmptyInferenceResult(inferenceResult))
	    		return -1;
			
			int wrongConclusionId = inferenceResult.getConclusion().getConclusionId();
			Logger.info("wrongConclusionId by inference : " + wrongConclusionId);
			return wrongConclusionId;
		}
		else return -1;
	}
	
	public boolean isValidKaMode(int kaMode, 
			                             Case aCase, 
			                             int aConclusionId, 
			                             StringBuilder sb)
	{
		//add
	    if (kaMode == Learner.KA_NEW_MODE) 
	    {
	    	//SCRDR인 경우, inference 결과가 존재하면 add mode 를 사욯할 수 없음
	    	if (Main.domain.isSCRDR() &&
	    	    RDRInterface.getInstance().isEmptyInferenceResult(aCase) == false)
	    	{
	    		sb.append("SCRDR and ka add mode, but inferenceResult is exist");
	    		return false;
	    	}
	    }
	    else //edit, delete
	    {
    		if (RDRInterface.getInstance().isValidInferneceResult(aCase,
    				                                              aConclusionId) == false)
    		{
    			sb.append("conclusionId[" + aConclusionId + "] is not inference result");
	    		return false;
    		}
    	}

	    return true;
	}
	
	public KAResponse setLearner(KARequest kaReq)
	{
		KAResponse tResponse = new KAResponse();
		
		//----------------------------------------------------------------------
		// init
		//----------------------------------------------------------------------
		int wrongConclusionId = kaReq.getWrongConclusionId();
		int kaMode = kaReq.getKAMode();
		String kaModeStr = Learner.getKaModeString(kaMode);
		
	    Conclusion wrongConclusion = null;
	    String wrongConclusionName = "";
	    
	    Object irObj = RDRInterface.getInstance().getInferenceResult(kaReq.getCase());
	    
	    //add
	    if (kaMode == Learner.KA_NEW_MODE) 
	    {
	    	//SCRDR인 경우, inference 결과가 존재하면 add mode 를 사욯할 수 없음
	    	if (Main.domain.isSCRDR() &&
	    	    RDRInterface.getInstance().isEmptyInferenceResult(irObj) == false)
	    	{
	    		tResponse.setStatus(false);
	    		tResponse.setMessage("SCRDR, " + kaModeStr + " mode는 inference 결과가 존재하는 경우 사용할수 없습니다.");
	    		return tResponse;
	    	}
	    }
	    else //edit, delete
	    {
	    	//wrongConclusionId 가 지정되지 않으면 inference 결과로 setting.
	    	if (wrongConclusionId < 0)
	    	{
	    		if (Main.domain.isSCRDR())
	    		{
		    		//SCRDR인 경우만 가능함
		    		if (RDRInterface.getInstance().isEmptyInferenceResult(irObj))
		    		{
			    		tResponse.setStatus(false);
			    		tResponse.setMessage(kaModeStr + " mode, conclusionId가 지정되지 않았고 inference결과가 존재하지 않습니다.");
			    		return tResponse;
		    		}
		    		wrongConclusionId = ((Rule)irObj).getConclusion().getConclusionId();
		    		Logger.info("wrongConclusionId by inference : " + wrongConclusionId);
	    		}
	    	}
	    	//wrongConclusionId 가 inference 결과에 포함되는지 체크
	    	else
	    	{
	    		if (RDRInterface.getInstance().isValidInferneceResult(irObj, 
	    				                                              wrongConclusionId) == false)
	    		{
		    		tResponse.setStatus(false);
		    		tResponse.setMessage("conclusionId[" + wrongConclusionId + "] 는 해당사례의 결론이 아닙니다.");
		    		return tResponse;
	    		}
	    	}
	    	
	        wrongConclusion = Main.KB.getConclusionSet().getConclusionById(wrongConclusionId);
	        wrongConclusionName = wrongConclusion.getConclusionName();
	        Logger.info("wrongConclusionName : " + wrongConclusionName);
	    }
	        
	    RDRInterface.getInstance().learnerInit(kaReq.getCase(), kaMode, wrongConclusion);
	
	    ConditionSet usedConditionSet 
	    	= RDRInterface.getInstance().getUsedConditionSet(kaReq.getCase(), wrongConclusion);
	    
	    tResponse.setConditions(usedConditionSet);
	    tResponse.setWrongConclusion(wrongConclusionName);
	    tResponse.addWrongRule(irObj);
	    
	    //Logger.info("usedConditionSet : " + usedConditionSet.toString());
	    
	    String[] returnArray = null;
	    if (kaMode != Learner.KA_STOPPING_MODE)
	    {
		    //----------------------------------------------------------------------
		  	// add conclusion
		  	//----------------------------------------------------------------------
		    String selectedConclusion = kaReq.getSelectedConclusion();
		    if (Main.KB.isNewConclusion(selectedConclusion))
		    {
		    	returnArray = RDRInterface.getInstance().addConclusion(selectedConclusion);
		    	
		    	if (returnArray[0].equals("error"))
		    	{
		    		tResponse.setStatus(false);
		    		tResponse.setMessage(returnArray[1]);
		    		return tResponse;
		    	}
		    	Logger.info("add conclusion finished");
		    }
		    
		    //----------------------------------------------------------------------
		  	// select conclusion
		  	//----------------------------------------------------------------------
		    returnArray = RDRInterface.getInstance().selectConclusion(selectedConclusion);
		    if (returnArray[0].equals("error"))
	    	{
		    	tResponse.setStatus(false);
	    		tResponse.setMessage(returnArray[1]);
	    		return tResponse;
	    	}
		    Logger.info("select conclusion finished");
	    }
	    
	    //----------------------------------------------------------------------
	  	// add condition
	  	//----------------------------------------------------------------------
	    ArrayList<ConditionItem> conditionItems = kaReq.getConditionItems();
	    for (int ci = 0; ci < conditionItems.size(); ci++)
	    {
	    	ConditionItem citem = conditionItems.get(ci);
	    	
	    	if (citem.isValid() == false)
	    	{
	    		tResponse.setStatus(false);
	    		tResponse.setMessage("condition has null or empty string");
	    		return tResponse;
	    	}
	    	
	    	returnArray = RDRInterface.getInstance().addCondition(citem.getAttribute(), 
	    			                                              citem.getOperator(), 
	    			                                              citem.getValue());
	    	if (returnArray[0].equals("error"))
	    	{
	    		tResponse.setStatus(false);
	    		tResponse.setMessage(returnArray[1]);
	    		return tResponse;
	    	}
	    }
	    Logger.info("add condition finished");

        tResponse.setStatus(true);
		tResponse.setMessage("");
		return tResponse;
	}
	
	/** key : rule id, value : conclusionName */
	public HashMap<Integer, String> getOtherConclusions(int cornerstoneCaseId)
	{
		CornerstoneCase aCornerstoneCase 
			= Main.allCornerstoneCaseSet.getCornerstoneCaseById(cornerstoneCaseId);
    
		if (aCornerstoneCase == null)
		{
			Logger.error("getOtherConclusions, cornerstone not found : " + cornerstoneCaseId);
			return null;
		}
		
		//reasoner의 current case set
		Main.workbench.setValidatingCase(aCornerstoneCase);
        Main.workbench.inferenceForValidation();
        
        //ConclusionSet conclusionSet = new ConclusionSet();
        HashMap<Integer, String> tMap = new HashMap<Integer, String>();
        if (Main.domain.isMCRDR())
        {
        	RuleSet inferenceResult = (RuleSet) Main.workbench.getInferenceResult();
        	//conclusionSet = inferenceResult.getConclusionSet();
        	tMap = inferenceResult.getConclusionMap();
        }
        else if (Main.domain.isSCRDR())
        {
        	Rule inferenceResult = (Rule)Main.workbench.getInferenceResult();
        	//conclusionSet.addConclusion(inferenceResult.getConclusion());
        	tMap.put(inferenceResult.getRuleId(), 
        			 inferenceResult.getConclusion().getConclusionName());
        }
		
        return tMap;
	}
	
	
	
	
	
	
	
	
	
}
