package rdr.workbench;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rdr.api.RDRInterface;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseStructure;
import rdr.logger.Logger;
import rdr.rules.Condition;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;
import rdr.utils.DateUtil;
import rdr.utils.RDRConfig;
import rdr.utils.RDRConstants;
import rdr.utils.StringUtil;

public class ReportRuleTree 
{
	protected ArrayList<InferencePathItem> pathItems;
	
	protected ArrayList<String> header;
	
	private LinkedHashMap<String, String> filteringValue;
	
	private String comment;
	
	private String fn;
	
	/** constructor */
	public ReportRuleTree()
	{
		pathItems = new ArrayList<InferencePathItem>();
		header = new ArrayList<String>();
		filteringValue = new LinkedHashMap<String, String>();
		comment = "";
		fn = null;
	}

	public String getFileName() 
	{
		return fn;
	}

	public void setFileName(String fn) 
	{
		this.fn = fn;
	}
	
	public void clearFiltering()
	{
		this.filteringValue.clear();
	}
	
	public void addFiltering(String attrName, String value)
	{
		this.filteringValue.put(attrName, value);
	}
	
	public void setFiltering(LinkedHashMap<String, String> pMap)
	{
		this.filteringValue.clear();
		
		if (pMap != null && pMap.isEmpty() == false)
			this.filteringValue.putAll(pMap);
	}
	
	public void setComment(String pComment)
	{
		this.comment = pComment;
	}

	public boolean reportRuleSummary(StringBuilder sbFileName)
	{
		this.buildPathItemsTraverse();
		this.setHeader();
		sbFileName.setLength(0);
		
		String fileName = "";
		
		if (fn == null)
		{
			StringBuilder sb = new StringBuilder();
			if (filteringValue.isEmpty() == false)
			{
				sb.append("_");
				Iterator<String> fitr = filteringValue.keySet().iterator();
				while (fitr.hasNext())
				{
					String attrName = fitr.next();
					String value = filteringValue.get(attrName);
					sb.append("(" + attrName + "=" + value + ")");
				}
			}
			fileName = RDRConfig.getOutPath() + File.separator + "report_ruletree_" +
				       DateUtil.convert(new Date(), RDRConstants.DATETIME_FORMAT2) + sb.toString() + ".csv";
		}
		else
			fileName = RDRConfig.getOutPath() + File.separator + this.fn;
		
		sbFileName.append(fileName);
						
		try
		{
			FileWriter writer = new FileWriter(new File(fileName), true);
	
			writer.append("소견" + RDRConstants.Delimeter);
			writer.append(StringUtil.arrayStringToString(header, RDRConstants.Delimeter));
			writer.append(System.lineSeparator());

			for (int i = 0; i < pathItems.size(); i++)
			{
				writer.append(pathItems.get(i).getConclusion() + RDRConstants.Delimeter);
				writer.append(makeData(pathItems.get(i)));
				writer.append(System.lineSeparator());
			}
			
			writer.close();
			Logger.info("report rule tree, fn[" + fileName + "]");
			
			return true;
		}
		catch (Exception ex)
		{
			Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
			return false;
		}
	}
		
	private String makeData(InferencePathItem pItem)
	{
		String tmp = "";
		
		for (int i = 0; i < header.size(); i++)
		{
			String name = header.get(i);
			String data = pItem.getValueString(name);
			
			if (i > 0) tmp += RDRConstants.Delimeter;
			tmp += data;
		}
		
		return tmp;
	}
	
	public void buildPathItems()
	{
		this.pathItems.clear();
		LinkedHashMap<Integer, Rule> aRuleSet = Main.KB.getBase();
		Iterator<Integer> keyItr = aRuleSet.keySet().iterator();
		while (keyItr.hasNext())
		{
			Integer key = keyItr.next();
			Rule aRule = aRuleSet.get(key);
			
			//if (aRule.isLeaf() == false)
			//	continue;
			
			if (aRule.getRuleId() == Rule.ROOT_RULE_ID)
				continue;

			if (this.filteringValue.isEmpty() == false && this.filter(aRule) == false)
				continue;
			
			if (this.comment.isEmpty() == false && 
				this.comment.equals(aRule.getConclusion().getConclusionName()) == false)
				continue;
			
			HashSet<Condition> conditionSet
				= aRule.getPathRuleConditionSet(false).getBase();
			
			InferencePathItem item = new InferencePathItem();
			item.addConditions(aRule.getPathRuleConditionSet(false));
			item.setConclusion(aRule.getConclusion().getConclusionName());
			this.pathItems.add(item);
		}
	}
	
	public void buildPathItemsTraverse()
	{
		this.pathItems.clear();
		Rule rootRule = Main.KB.getRootRule();
		traverse(rootRule);
	}
	
	public void traverse(Rule currentRule)
	{
		boolean bReport = true;

		while (true)
		{
			if (currentRule.getRuleId() == Rule.ROOT_RULE_ID)
			{
				bReport = false;
				break;
			}
	
			if (this.filteringValue.isEmpty() == false && this.filter(currentRule) == false)
			{
				bReport = false;
				break;
			}
			
			if (this.comment.isEmpty() == false && 
				this.comment.equals(currentRule.getConclusion().getConclusionName()) == false)
			{
				bReport = false;
				break;
			}
			
			if (currentRule.isDeprecated())
			{
				bReport = false;
				break;
			}
			
			break;
		}
		
		if (bReport)
		{
			HashSet<Condition> conditionSet
				= currentRule.getPathRuleConditionSet(false).getBase();
		
			InferencePathItem item = new InferencePathItem();
			item.addConditions(currentRule.getPathRuleConditionSet(false));
			item.setConclusion(currentRule.getConclusion().getConclusionName());
			this.pathItems.add(item);
		}
		
		int childCount = currentRule.getChildRuleCount();
		for (int i = 0; i < childCount; i++)
		{
			this.traverse(currentRule.getChildAt(i));
		}
	}
	
	/** 집계된 조건에 사용된 항목으로 header를 만든다. 순서는 case structure의 id 순서를 따름 */
	public void setHeader()
	{
		header.clear();
		HashSet<String> attrSet = new HashSet<String>();
		
		for (int i = 0; i < pathItems.size(); i++)
		{
			attrSet.addAll(pathItems.get(i).getKeySet());
		}
		
		CaseStructure aCaseStructure = Main.domain.getCaseStructure();
		String[] names = aCaseStructure.getAttributeNameArraySortedById();
		for (int j = 0; j < names.length; j++)
		{
			if (attrSet.contains(names[j]))
				header.add(names[j]);
		}
	}
	
	/** report 대상여부, true 이면 대상임 */
	public boolean filter(Rule pRule)
	{
		//HashMap<String, String> filteringValue;
		
		HashSet<Condition> conditionSet
			= pRule.getPathRuleConditionSet(false).getBase();
		
		Iterator<String> fitr = filteringValue.keySet().iterator();
		while (fitr.hasNext())
		{
			String attrName = fitr.next();
			String value = filteringValue.get(attrName);
			
			LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
			valueMap.put(attrName, value);
			
			String[] msg = new String[1];
	    	Case aCase 
	    		= RDRInterface.getInstance().getCaseFromValueMap(Main.domain.getCaseStructure(), 
	    				                                         valueMap, msg);
	    	if (aCase == null)
	    	{
	    		Logger.error("case creation failed, " + valueMap);
	    		continue;
	    	}
	    	
	    	/** Rule Path의 조건중 하나라도 만족하면 Report 대상임 */
	    	boolean bSatisfied = false;
	    	Iterator<Condition> citr = conditionSet.iterator();
	    	while (citr.hasNext())
	    	{
	    		Condition tCondition = citr.next();
	    		if (tCondition.isSatisfied(aCase))
	    		{
	    			bSatisfied = true;
	    		}
	    	}
	    	
	    	/** filteringValue 중 하나라도 불만족이면 Report 대상이 아님 */
	    	if (bSatisfied == false) 
	    		return false;
		}
		
		return true;
	}
	
	public String getReportJSON()
	{
		buildPathItemsTraverse();
		setHeader();
		
		TreeMap<String, ArrayList<InferencePathItem>> piMap 
			= new TreeMap<String, ArrayList<InferencePathItem>>();
		
		for (int i = 0; i < pathItems.size(); i++)
		{
			String cn = pathItems.get(i).getConclusion();
			if (piMap.containsKey(cn) == false)
				piMap.put(cn, new ArrayList<InferencePathItem>());
				
			piMap.get(cn).add(pathItems.get(i));
		}
		
		JSONArray rtnJSONArr = new JSONArray();
		
		Iterator<String> iter = piMap.keySet().iterator();
		while (iter.hasNext())
		{
			String cn = iter.next();
			ArrayList<InferencePathItem> tList = piMap.get(cn);
			
			JSONObject tJSONObj = new JSONObject();
			tJSONObj.put("conclusion", cn);
			
			TreeSet<String> tcSet = new TreeSet<String>();
			JSONArray arr = new JSONArray();
			for (int ti = 0; ti < tList.size(); ti++)
			{
				arr.add(tList.get(ti).getJSON());
				
				String tc = tList.get(ti).getValueString("TEST_CODE");
				if (tc.equals(RDRConstants.EmptyString) == false)
					tcSet.add(tc);
			}
			tJSONObj.put("data", arr);
			
			JSONArray arr2 = new JSONArray();
			Iterator<String> tciter = tcSet.iterator();
			while (tciter.hasNext())
			{
				arr2.add(tciter.next());
			}
			
			tJSONObj.put("testCode",  arr2);
			
			rtnJSONArr.add(tJSONObj);
			
		}
		
		return rtnJSONArr.toString();
	}
	
	
	
}
