package rdr.apimsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import rdr.apps.Main;
import rdr.cases.Case;
import rdr.db.RDRDBManager;
import rdr.learner.Learner;
import rdr.logger.Logger;
import rdr.model.Value;
import rdr.model.ValueType;
import rdr.rules.Operator;
import rdr.utils.RDRConfig;
import rdr.utils.StringUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rdr.api.JSONConverter;
import rdr.api.RDRInterface;

public class KARequest 
{
	/** 사례정보 */
	private Case currentCase;
	
	/** 수정대상 결론 id, add mode인 경우 불필요 */
	private int wrongConclusionId;
	
	/** 0(add), 1(alter), 2(edit), 3(delete) */
	private int kaMode;
	
	/** 새로운 rule의 결론 */
	private String selectedConclusion;
	
	/** 새로운 rule의 조건 */
	private ArrayList<ConditionItem> conditions;
	
	private boolean bSemiAuto = false;
	
	private double rangeRatio = -1.0;
	
	public KARequest()
	{
		clear();
	}
	
	public void clear()
	{
		currentCase = null;
		wrongConclusionId = -1;
		kaMode = -1;
		selectedConclusion = "";
		conditions = new ArrayList<ConditionItem>();
		bSemiAuto = false;
		rangeRatio = -1.0;
	}
	
	public boolean isValid()
	{
		if (currentCase == null)
		{
			Logger.warn("KARequest invalid, case is null");
			return false;
		}
		
		if (kaMode < 0)
		{
			Logger.warn("KARequest invalid, KA Mode is not set");
			return false;
		}
		
		if (conditions.isEmpty())
		{
			Logger.warn("KARequest invalid, condition is empty");
			return false;
		}
		
		if (kaMode != Learner.KA_STOPPING_MODE && selectedConclusion.isEmpty())
		{
			Logger.warn("KARequest invalid, conclusion is not selected");
			return false;
		}
		
		if ((kaMode == Learner.KA_EXCEPTION_MODE ||
			 kaMode == Learner.KA_STOPPING_MODE || 
			 kaMode == Learner.KA_ALTER_MODE))
		{
			if (Main.domain.isMCRDR() && wrongConclusionId < 0)
			{
				Logger.warn("KARequest invalid, MCRDR and wrong conclusion(target conclusion) is not set");
				return false;
			}
		}
		
		if (kaMode == Learner.KA_ALTER_MODE && Main.domain.isSCRDR())
		{
			Logger.warn("KARequest invalid, alter mode is not allowd for SCRDR");
			return false;
		}
		
		return true;
	}
		
	public void printLog()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("KA Request ---------------");
		sb.append(System.lineSeparator());
		
		sb.append("case : " + currentCase.toString());
		sb.append(System.lineSeparator());
		
		sb.append("wrongConclusionId : " + wrongConclusionId);
		sb.append(System.lineSeparator());
		
		String kaModeStr = Learner.getKaModeString(kaMode);
		sb.append("kaMode : " + kaModeStr);
		sb.append(System.lineSeparator());
		
		sb.append("selectedConclusion : " + selectedConclusion);
		sb.append(System.lineSeparator());
		
		for (int i = 0; i < conditions.size(); i++)
		{
			sb.append("condition : " + conditions.get(i).toString());
			sb.append(System.lineSeparator());
		}
		
		sb.append("semiAuto(create condition with only attributeName) : " + (bSemiAuto ? "true" : "false"));
		sb.append(System.lineSeparator());
		sb.append("ragneRatio : " + rangeRatio);
		
		Logger.info(sb.toString());
	}
	
	public Case getCase()
	{
		return currentCase;
	}
	
	public void setCase(Case pCase)
	{
		currentCase = pCase;
		//currentCase.setCaseId(RDRInterface.getInstance().getCornerstoneCaseId(currentCase));
	}
	
	public void setCase(HashMap<String, String> valueMap)
	{
		String[] msg = new String[1];
		Case aCase 
			= RDRInterface.getInstance().getCaseFromValueMap(Main.domain.getCaseStructure(), 
					                                         valueMap, msg);
		
		if (aCase == null)
			Logger.error("case creation failed, " + msg[0]);
		else
		{
			currentCase = aCase;
			//currentCase.setCaseId(RDRInterface.getInstance().getCornerstoneCaseId(currentCase));
		}
	}
	
	public int getWrongConclusionId()
	{
		return wrongConclusionId;
	}
	
	public void setWrongConclusionId(int id)
	{
		wrongConclusionId = id;
	}
	
	public int getKAMode()
	{
		return kaMode;
	}
	
	public void setKAMode(int pMode)
	{
		kaMode = pMode;
	}
	
	public void setKAMode(String kaModeStr)
	{
		kaMode = Learner.getKaModeByName(kaModeStr);
	}
	
	public String getSelectedConclusion()
	{
		return selectedConclusion;
	}
	
	public void setSelectedConclusion(String pConclusion)
	{
		selectedConclusion = pConclusion;
	}
	
	public boolean isConditionEmpty()
	{
		return conditions.isEmpty();
	}
	
	public int getConditionCount()
	{
		return conditions.size();
	}
	
	public void clearCondition()
	{
		conditions.clear();
	}
	
	public ArrayList<ConditionItem> getConditionItems()
	{
		return conditions;
	}
	
	/** add condition */
	public void addCondition(String attrName, String op, String value)
	{
		ConditionItem citem = new ConditionItem();
	    citem.set(attrName, op, value);
	    conditions.add(citem);
	}
	
	public void addCondition(Case pCase, ArrayList<String> attrNames, boolean bContainsNull)
	{
		for (int i = 0; i < attrNames.size(); i++)
			this.addCondition(pCase, attrNames.get(i), bContainsNull);
	}
	
	/** add condition for semi-auto 
	 *  @note currentCase가 먼저 setting되어야 함
	 * */
	public void addCondition(Case pCase, String attrName, boolean bContainsNull)
	{
		Value aValue = pCase.getValue(attrName);
		
		if (aValue.isNullValue())
		{
			if (bContainsNull)
			{
				ConditionItem citem = new ConditionItem();
				Operator op = new Operator(Operator.MISSING);
				citem.set(attrName, op.getOperatorName(), "");
				conditions.add(citem);
			}
		}
		else if (aValue.getValueType().getTypeCode() == ValueType.CONTINUOUS &&
				 rangeRatio > 0.0)
		{
			double dVal = (Double)aValue.getActualValue();
			
			if (rangeRatio < 0.0)
				rangeRatio = Math.abs(RDRConfig.getRangeRatio());

			ConditionItem minItem = new ConditionItem();
			Operator op = new Operator(Operator.GREATER_THAN_EQUALS);
			double minVal = dVal - (dVal * rangeRatio/100.0);
			minItem.set(attrName, op.getOperatorName(), String.format("%.4f", minVal));
			conditions.add(minItem);
			
			ConditionItem maxItem = new ConditionItem();
			op = new Operator(Operator.LESS_THAN_EQUALS);
			double maxVal = dVal + (dVal * rangeRatio/100.0);
			maxItem.set(attrName, op.getOperatorName(), String.format("%.4f", maxVal));
			conditions.add(maxItem);
		}
		else
		{
			ConditionItem citem = new ConditionItem();
			Operator op = new Operator(Operator.EQUALS);
			citem.set(attrName, op.getOperatorName(), aValue.toString());
			conditions.add(citem);
		}
	}
	
	public boolean getSemiAuto() 
	{
		return bSemiAuto;
	}

	public void setSemiAuto(boolean bSemiAuto) 
	{
		this.bSemiAuto = bSemiAuto;
	}

	public double getRangeRatio() 
	{
		return rangeRatio;
	}

	public void setRangeRatio(double rangeRatio) 
	{
		this.rangeRatio = rangeRatio;
	}

	public boolean buildFromJSON(JSONObject reqJsonObj, boolean isSemiAuto, StringBuilder sb)
	{
		clear();
		
		this.bSemiAuto = isSemiAuto;
		
		// case --------------------------------------------------------------
		if (reqJsonObj.get("case") == null)
		{
			sb.append("case is not found");
			Logger.error(sb.toString());
			return false;
		}
		
		JSONObject caseJsonObj = (JSONObject)reqJsonObj.get("case");
		if (caseJsonObj == null)
		{
			sb.append("case data read failed");
			Logger.error(sb.toString());
			return false;
		}
		
		LinkedHashMap<String, String> valueMap 
			= JSONConverter.convertJSONObjectToValueMap(caseJsonObj);
			
		String[] msg = new String[1];
		Case aCase 
			= RDRInterface.getInstance().getCaseFromValueMap(Main.domain.getCaseStructure(), 
					                                         valueMap, msg);
		if (aCase == null)
		{
			sb.append("case creation failed, " + msg[0]);
			Logger.error(sb.toString());
			return false;
		}
		
		//aCase.setCaseId(RDRInterface.getInstance().getCornerstoneCaseId(aCase));
		Logger.info("case id(buildFromJSON) : " + aCase.getCaseId());
		currentCase = aCase;
		
		// ka mode --------------------------------------------------------------
		if (reqJsonObj.get("kaMode") == null)
		{
			sb.append("kaMode is not found");
			Logger.error(sb.toString());
			return false;
		}
		String kaModeStr = (String)reqJsonObj.get("kaMode");
		kaMode = Learner.getKaModeByName(kaModeStr);
		
		// selectedConclusion ---------------------------------------------------
		if (kaMode != Learner.KA_STOPPING_MODE && 
		    reqJsonObj.get("selectedConclusion") == null)
		{
			sb.append("selectedConclusion is not found");
			Logger.error(sb.toString());
			return false;
		}
		selectedConclusion = (String)reqJsonObj.get("selectedConclusion");
		
		// conclusionId ---------------------------------------------------------
		if (kaMode == Learner.KA_EXCEPTION_MODE ||
		    kaMode == Learner.KA_STOPPING_MODE || 
		    kaMode == Learner.KA_ALTER_MODE)
		{
			if (Main.domain.isMCRDR() && reqJsonObj.get("conclusionId") == null)
        	{
        		sb.append("conclusionId is not found");
    			Logger.error(sb.toString());
        		return false;
        	}
            
            if (reqJsonObj.get("conclusionId") != null)
            {
            	Object tObj = reqJsonObj.get("conclusionId");
            	if (tObj instanceof String)
            	{
            		if (StringUtil.isNumeric((String)tObj) == false)
            		{
            			sb.append("conclusionId is not numeric");
            			Logger.error(sb.toString());
                		return false;
            		}
            		wrongConclusionId = Integer.parseInt((String)tObj);
            	}
            	else
            		wrongConclusionId = ((Long)reqJsonObj.get("conclusionId")).intValue();
            }
        } 
		
		if (kaMode == Learner.KA_ALTER_MODE && Main.domain.isSCRDR())
        {
       		sb.append("alter mode is not allowd for SCRDR");
   			Logger.error(sb.toString());
       		return false;
       	}
				
		// condition ------------------------------------------------------------
		if (this.bSemiAuto == true)
		{
			if (reqJsonObj.get("rangeRatio") == null)
			{
				Logger.info("rangeRatio is missing, so use value in configuration");
				rangeRatio = RDRConfig.getRangeRatio();
			}
			else
			{
				rangeRatio = (double)((Long)reqJsonObj.get("rangeRatio")).intValue();
			}
			
			if (rangeRatio < 0.0) rangeRatio = Math.abs(rangeRatio);
			
			JSONArray condJsonArray = (JSONArray)reqJsonObj.get("conditionAttributes");
			if (condJsonArray == null)
			{
				sb.append("condition attribute is not found");
    			Logger.error(sb.toString());
				return false;
			}
			Iterator<String> iterator = condJsonArray.iterator();
			while (iterator.hasNext())
			{
				String attrName = (String)iterator.next();

				this.addCondition(aCase, attrName, true); //bContainsNull
			}
		}
		else
		{
			JSONArray condJsonArray = (JSONArray)reqJsonObj.get("condition");
			if (condJsonArray == null)
			{
				sb.append("condition is not found");
    			Logger.error(sb.toString());
				return false;
			}
			Iterator<JSONObject> iterator = condJsonArray.iterator();
			while (iterator.hasNext())
			{
				JSONObject condJsonObj = (JSONObject)iterator.next(); 
				String attrStr = (String)condJsonObj.get("attributeName");
				String opStr = (String)condJsonObj.get("operator");
				String valStr = (String)condJsonObj.get("value");
				
				this.addCondition(attrStr, opStr, valStr);
			}
		}
		
		
		if (kaMode < 0 || this.getConditionCount() == 0 )
		{
			sb.append("kaMode, condition is missing");
			Logger.error(sb.toString());
			return false;
		}
		
		return true;
	}
	
	
}
