package com.smf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.smf.ep.EPDBManager;
import com.smf.pbs.PBSDBManager;

import general.webinterface.DownloadUtil;
import rdr.api.RDRBrokerParameters;
import rdr.api.RDRService;
import rdr.apimsg.CaseItem;
import rdr.apimsg.ConditionItem;
import rdr.apimsg.RDRResponse;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.db.RDRDBManager;
import rdr.logger.Logger;
import rdr.utils.RDRConfig;
import rdr.utils.StringUtil;
import rdr.utils.Utility;

@WebServlet("/smf/*")
public class SmfServlet extends HttpServlet
{
	@Override
    protected void doGet(HttpServletRequest request, 
    		             HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                                   request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg");
    	
		System.out.println("SmfServlet doGet start");
    	
    	RDRDBManager.getInstance().connectDataBase(0);
    	
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	
    	String domainName = request.getParameter("domain");
    	String remoteAddr = request.getRemoteAddr();
    	
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        //-------------------------------------------------------------------
    	// getComment
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("comment"))
    	{
    		String commentCode = request.getParameter("commentCode");
    		Logger.info("contextPath [" + request.getContextPath() + "]");
        	
    		Logger.info("RestAPI getComment(" + request.getRemoteAddr() + ") " +
                        "commentCode[" + commentCode + "]");
            
    		TreeMap<String, String> cm = new TreeMap<String, String>();
    		if (request.getContextPath().contains("rdr-ep"))
    			cm = EPDBManager.getTestComments(commentCode);
    		else if (request.getContextPath().contains("rdr-pbs"))
    			cm = PBSDBManager.getTestComments(commentCode);
    		
    		JSONArray jsonArr = new JSONArray();
    		Iterator<String> iter = cm.keySet().iterator();
    		while (iter.hasNext())
    		{
    			String tCode = iter.next();
    			String tComment = cm.get(tCode);
    			JSONObject jsonObj = new JSONObject();
        		jsonObj.put("commentCode", tCode);
        		jsonObj.put("comment", tComment);
        		jsonArr.add(jsonObj);
    		}
    		
    		response.getWriter().write(jsonArr.toString());
	        return;
    	} 
    	//-------------------------------------------------------------------
    	// getCandidateAttributes
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("getCandidateAttributes"))
    	{
    		//String dbName = request.getParameter("dbName");
        	
    		Logger.info("RestAPI getCandidateAttributes(" + request.getRemoteAddr() + ") ");
                        
    		TreeMap<String, String> candidates = new TreeMap<String, String>();
    		
    		String dbName = RDRConfig.getSecondaryDBName();
    		if (request.getContextPath().contains("rdr-ep"))
    			candidates = EPDBManager.getTableColumns(dbName, "TB_EP_RECEIPT");
    		else if (request.getContextPath().contains("rdr-pbs"))
    			candidates = PBSDBManager.getTableColumns(dbName, "TB_PBS_RECEIPT");
    		
    		JSONArray jsonArr = new JSONArray();
    		
    		Iterator<String> iter = candidates.keySet().iterator();
    		while (iter.hasNext())
    		{
    			String name = iter.next();
    			String type = candidates.get(name);
    			
    			JSONObject jsonObj = new JSONObject();
    			jsonObj.put("name", name);
    			jsonObj.put("type", type);
    			jsonArr.add(jsonObj);
    		}

    		response.getWriter().write(jsonArr.toString());
	        return;
    	} 
    	
        if (domainName == null)
        {
        	JSONObject errJSONObj = new JSONObject();
    		errJSONObj.put("validity", "error");
    		errJSONObj.put("msg", "domain is missing");
	        response.getWriter().write(errJSONObj.toString());
	        return;
        }
    	
    	//-------------------------------------------------------------------
    	// inference by broker
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("inference"))
    	{
    		String lmbCode = request.getParameter("lmbCode");
        	int receiptDate = StringUtil.parseInt(request.getParameter("receiptDate"));
        	int receiptNo = StringUtil.parseInt(request.getParameter("receiptNo"));
        	String testCode = request.getParameter("testCode");
        	//String specimenCode = request.getParameter("specimenCode");
        	String userId = request.getParameter("userId");
        	
        	int decisionSeq = -1;
        	String tParam = request.getParameter("decisionSeq");
        	if ( tParam != null)
        		decisionSeq = StringUtil.parseInt(tParam);
        	
    		Logger.info("RestAPI inferenceByBroker(" + request.getRemoteAddr() + ") " +
                        "domain[" + domainName + "] lmb[" + lmbCode +
                        "] rctDt[" + receiptDate + "] rctNo[" + receiptNo + 
                        "] testCode[" + testCode + //"] specimen[" + specimenCode +
                        "] decSeq[" + decisionSeq +
                        "] userId[" + userId +"]");
                        
    		RDRBrokerParameters params = new RDRBrokerParameters();
    		params.add(lmbCode);
    		params.add(receiptDate);
    		params.add(receiptNo);
    		params.add(testCode);
    		//params.add(specimenCode);
    		params.add(decisionSeq);
    		
    		RDRResponse tResponse
    			= RDRService.getInferenceByBroker(domainName, params, userId);
    		
//    		if (tResponse.isInferenceEmpty())
//    		{
//    			CaseItem tCaseItem = RDRService.getCaseByBroker(domainName, params, userId);
//    			Case tCase = tCaseItem.createCase(Main.domain.getCaseStructure());
//    			
//    			if (RDRService.addFirstLevelRule(domainName, tCase, userId))
//    			{
//    				tResponse = RDRService.getInferenceByBroker(domainName, params, userId);
//    			}
//    			else
//    			{
//    				Logger.error("failed for adding first level rule");
//    			}
//    		}
    		
    		JSONObject jsonObj = tResponse.getJSON();
    		Logger.debug("@inference : " + jsonObj);
    		response.getWriter().write(jsonObj.toString());
	        return;
    	} 
    	//-------------------------------------------------------------------
    	// getCase
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("case"))
    	{
    		String lmbCode = request.getParameter("lmbCode");
        	int receiptDate = StringUtil.parseInt(request.getParameter("receiptDate"));
        	int receiptNo = StringUtil.parseInt(request.getParameter("receiptNo"));
        	String testCode = request.getParameter("testCode");
        	//String specimenCode = request.getParameter("specimenCode");
        	String userId = request.getParameter("userId");
        	
        	int decisionSeq = -1;
        	String tParam = request.getParameter("decisionSeq");
        	if ( tParam != null)
        		decisionSeq = StringUtil.parseInt(tParam);
        	
    		Logger.info("RestAPI getCase(" + request.getRemoteAddr() + ") " +
                        "domain[" + domainName + "] lmb[" + lmbCode +
                        "] rctDt[" + receiptDate + "] rctNo[" + receiptNo + 
                        "] testCode[" + testCode + //"] specimen[" + specimenCode +
                        "] decSeq[" + decisionSeq +
                        "] userId[" + userId +"]");
                        
    		RDRBrokerParameters params = new RDRBrokerParameters();
    		params.add(lmbCode);
    		params.add(receiptDate);
    		params.add(receiptNo);
    		params.add(testCode);
    		//params.add(specimenCode);
    		params.add(decisionSeq);
    		
    		CaseItem tCaseItem
    			= RDRService.getCaseByBroker(domainName, params, userId);
    		response.getWriter().write(tCaseItem.getJSON().toString());
	        return;
    	} 
    	//-------------------------------------------------------------------
    	// getSuggestedConditions
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("suggestedConditions"))
    	{
    		String lmbCode = request.getParameter("lmbCode");
        	int receiptDate = StringUtil.parseInt(request.getParameter("receiptDate"));
        	int receiptNo = StringUtil.parseInt(request.getParameter("receiptNo"));
        	String testCode = request.getParameter("testCode");
        	//String specimenCode = request.getParameter("specimenCode");
        	String userId = request.getParameter("userId");
        	
        	int conclusionId = -1;
        	String tParam = request.getParameter("conclusionId");
        	if ( tParam != null)
        		conclusionId = StringUtil.parseInt(tParam);
        	
        	String kaMode = request.getParameter("kaMode");
        	
        	int decisionSeq = -1;
        	tParam = request.getParameter("decisionSeq");
        	if ( tParam != null)
        		decisionSeq = StringUtil.parseInt(tParam);
        	        	
    		Logger.info("RestAPI getSuggestedConditions(" + request.getRemoteAddr() + ") " +
                        "domain[" + domainName + "] lmb[" + lmbCode +
                        "] rctDt[" + receiptDate + "] rctNo[" + receiptNo + 
                        "] testCode[" + testCode + //"] specimen[" + specimenCode +
                        "] decSeq[" + decisionSeq +
                        "] kaMode[" + kaMode +
                        "] conclusionId[" + conclusionId +
                        "] userId[" + userId +"]");
                        
    		RDRBrokerParameters params = new RDRBrokerParameters();
    		params.add(lmbCode);
    		params.add(receiptDate);
    		params.add(receiptNo);
    		params.add(testCode);
    		//params.add(specimenCode);
    		params.add(decisionSeq);
    		
    		ArrayList<ConditionItem> ciList
    			= RDRService.getSuggestedConditions(domainName, params, kaMode, conclusionId, userId);
    		
    		JSONArray jsonArr = new JSONArray();
    		for (int i = 0; i < ciList.size(); i++)
    		{
    			jsonArr.add(ciList.get(i).getJSON());
    		}
    		    		
    		response.getWriter().write(jsonArr.toString());
	        return;
    	} 
    	//-------------------------------------------------------------------
    	// report
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("report"))
    	{
    		String testCode = request.getParameter("testCode");
    		String userId = request.getParameter("userId");
    		
    		Logger.info("RestAPI reportRuleTree(" + request.getRemoteAddr() + ") " +
                    "domain[" + domainName + "] testCode[" + testCode  +
                    "] userId[" + userId +"]");
    		
    		LinkedHashMap<String, String> filter = new LinkedHashMap<String, String>();
    		if (testCode != null)
    			filter.put("TEST_CODE", testCode);
    		
    		StringBuilder sbFileName = new StringBuilder();
    		boolean flag = RDRService.writeRuleTreeReport(domainName, filter, sbFileName, userId);
    		
    		String tmp = sbFileName.toString();
    		String path = tmp.substring(0, tmp.lastIndexOf(File.separator) + 1);
    		String fn = tmp.substring(tmp.lastIndexOf(File.separator) + 1);
    		
    		DownloadUtil downUtil = new DownloadUtil();
    		downUtil.download(request, response, path, fn);
    		
    		//response.getWriter().write("report : " + sbFileName.toString());
    		
    		//------------------------------------------------------------------------
			// 3일 이전의 파일 삭제
			File targetPath = new File(RDRConfig.getOutPath());
			File[] files = targetPath.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name)
				{
					return (name.startsWith("report_ruletree") && name.endsWith("csv"));
				}
			});
			
			LocalDateTime timeLimit = LocalDateTime.now();
			timeLimit = timeLimit.minusDays(3);
			
			for (int i = 0; i < files.length; i++)
			{
				LocalDateTime fileTime
			       	= LocalDateTime.ofInstant(Instant.ofEpochMilli(files[i].lastModified()), 
			                                TimeZone.getDefault().toZoneId());  
				
				if (fileTime.isBefore(timeLimit))
				{
					Logger.info("report file deleted : " + files[i].getName());
					files[i].delete();
				}
			}
			//------------------------------------------------------------------------
			
    		return;
    	}
    	//-------------------------------------------------------------------
    	// getPathToConclusion
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("pathToConclusion"))
    	{
    		String testCode = request.getParameter("testCode");
    		String comment = request.getParameter("comment");
    		String userId = request.getParameter("userId");
    		
    		Logger.info("RestAPI getPathToConclusion(" + request.getRemoteAddr() + ") " +
                    "domain[" + domainName + "] testCode[" + testCode  +
                    "] comment[" + comment + "] userId[" + userId +"]");
    		
    		LinkedHashMap<String, String> filter = new LinkedHashMap<String, String>();
    		if (testCode != null)
    			filter.put("TEST_CODE", testCode);
    		
    		if (comment == null) comment = "";
    		
    		response.getWriter().write(RDRService.getRuleTreeReportJSON(domainName, filter, comment, userId));
	        return;
    	} 
    }
	
	@Override
    protected void doPut(HttpServletRequest request, 
    		             HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg/");

		System.out.println("SmfServlet doPut start");
    	
    	RDRDBManager.getInstance().connectDataBase(0);
    	
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	
    	String domainName = request.getParameter("domain");
    	String remoteAddr = request.getRemoteAddr();
    	
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if (domainName == null)
        {
        	JSONObject errJSONObj = new JSONObject();
    		errJSONObj.put("validity", "error");
    		errJSONObj.put("msg", "domain is missing");
	        response.getWriter().write(errJSONObj.toString());
	        return;
        }
    	
    	//-------------------------------------------------------------------
    	// suggestedConditions
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("suggestedConditions"))
    	{
    		int conclusionId = -1;
        	String tParam = request.getParameter("conclusionId");
        	if ( tParam != null)
        		conclusionId = StringUtil.parseInt(tParam);
        	
        	String kaMode = request.getParameter("kaMode");
    		String userId = request.getParameter("userId");
    		
    		Logger.info("RestAPI getSuggestedConditions(" + request.getRemoteAddr() + ") " +
                        "domain[" + request.getParameter("domain") +
                        "]kaMode[" + kaMode +
                        "]conclusionId[" + conclusionId +
                        "] userId[" + userId + "] ");
    		
    		JSONArray rtnJSONArray = new JSONArray();
    		
    		String jsonStr = Utility.parseString(request);
            
    		if (RDRConfig.isDebugRequest())
            	Logger.info("request : " + jsonStr);
    		
    		JSONParser jsonParser = new JSONParser();
            JSONObject recvJSONObj = new JSONObject();
            
            try 
            {
            	recvJSONObj = (JSONObject)jsonParser.parse(jsonStr);
            }
            catch (Exception ex ) 
            {
            	Logger.error(ex.getClass().getName() + " : " + ex.getMessage());
            	response.getWriter().write(rtnJSONArray.toString());
    	        return;
            }
            
            if (recvJSONObj == null)
    		{
    			Logger.error("request json string parsing failed");
            	response.getWriter().write(rtnJSONArray.toString());
    	        return;
    		}
    		
    		CaseItem tCaseItem = new CaseItem();
    		tCaseItem.buildFromJSON(recvJSONObj);
    		
    		ArrayList<ConditionItem> ciList
				= RDRService.getSuggestedConditions(domainName, tCaseItem, kaMode, conclusionId, userId);
		
			for (int i = 0; i < ciList.size(); i++)
			{
				rtnJSONArray.add(ciList.get(i).getJSON());
			}
			    		
			response.getWriter().write(rtnJSONArray.toString());
	        return;
    	} 
    }
	
}
