package general.webinterface;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.swing.JOptionPane.showMessageDialog;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rdr.api.JSONConverter;
import rdr.api.RDRInterface;
import rdr.api.RDRService;
import general.restapi.RESTApiRequester;
import rdr.apps.Main;
import rdr.rules.Conclusion;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseStructure;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.db.RDRDBManager;
import rdr.domain.Domain;
import rdr.learner.Learner;
import rdr.logger.Logger;
import rdr.model.Value;
import rdr.rules.ConclusionSet;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;
import rdr.rules.RuleSet;
import rdr.utils.RDRConfig;
import rdr.utils.StringUtil;
import rdr.utils.Utility;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ucciri@gmail.com
 */
@WebServlet("/ka/*")
public class KAServlet extends HttpServlet 
{
	@Override
    protected void doPut(HttpServletRequest request, 
    		             HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg/");

		System.out.println("KAServlet doPut start");
    	
    	RDRDBManager.getInstance().connectDataBase(0);
    	
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	
    	//for initialize domain 
    	RDRInterface aRDRInf = RDRInterface.getInstance();
    	        
    	//-------------------------------------------------------------------
    	// /ka/init/ARFF
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("init"))
    	{
    		String domainName = request.getParameter("domain");
    		String conclusionIdStr = request.getParameter("conclusionId");
    		String kaModeStr = request.getParameter("kaMode");
        	String remoteAddr = request.getRemoteAddr();
        	
        	response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
        	if (domainName == null ||
        		kaModeStr == null ||
                (conclusionIdStr != null && StringUtil.isNumeric(conclusionIdStr) == false))
            {
        		Logger.info("RestAPI param invalid, " +
	    				    "domain[" + domainName + "] " +
	                        "conclusionId[" + conclusionIdStr + "] " +
	                        "kaMode[" + kaModeStr + "] ");
        		
            	JSONObject errJSONObj = new JSONObject();
        		errJSONObj.put("validity", "error");
        		errJSONObj.put("msg", "parameter is missing or invalid");
    	        response.getWriter().write(errJSONObj.toString());
    	        return;
            }

            int conclusionId = -1;
            if (conclusionIdStr != null)
            	conclusionId = Integer.parseInt(conclusionIdStr);
            
    		if (folders[2].trim().equals("ARFF"))
    		{
    			String syncStr = request.getParameter("sync");
    			if (StringUtil.isNumeric(syncStr) == false)
    			{
    				Logger.info("RestAPI param invalid, sync[" + syncStr + "] ");
        		
	            	JSONObject errJSONObj = new JSONObject();
	        		errJSONObj.put("validity", "error");
	        		errJSONObj.put("msg", "sync parameter is invalid");
	    	        response.getWriter().write(errJSONObj.toString());
	    	        return;
    			}
    			
    			Logger.info("RestAPI KAInitByARFF(" + remoteAddr + ") " +
	    				    "domain[" + domainName + "] " +
	    				    "sync[" + syncStr + "] " +
	                        "conclusionId[" + conclusionIdStr + "] " +
	                        "kaMode[" + kaModeStr + "] ");
    			
	    		int syncFlag = Integer.parseInt(syncStr);
	    		System.out.println("syncFlag : " + syncFlag);
	    		
	    		JSONObject rtnJSONObj = new JSONObject();
	    		response.setContentType("application/json");
    	        response.setCharacterEncoding("UTF-8");
	    		
	    		String reqBodyStr = Utility.parseString(request);
	    		if (reqBodyStr == null || reqBodyStr.isEmpty())
	    		{
	    			Logger.error("request body is empty");
	    			
	            	rtnJSONObj.put("validity", "error");
	    			rtnJSONObj.put("msg", "request body is empty");
	    	        response.getWriter().write(rtnJSONObj.toString());
	    	        return;
	    		}
	    		
	    		if (RDRConfig.isDebugRequest())
	            	Logger.info("request : " + reqBodyStr);
	    		
	    		if (reqBodyStr == null || reqBodyStr.isEmpty())
	    		{
	    			Logger.error("request body is empty");
	    			
	            	rtnJSONObj.put("validity", "error");
	    			rtnJSONObj.put("msg", "request body is empty");
	    	        response.getWriter().write(rtnJSONObj.toString());
	    	        return;
	    		}

	    		rtnJSONObj = RDRService.initializeByARFF(domainName, 
	    				                                 reqBodyStr, 
	    				                                 conclusionId, 
	    				                                 kaModeStr, 
	    				                                 syncFlag, 
	    				                                 remoteAddr);
	    		
	    		response.getWriter().write(rtnJSONObj.toString());
	    	}
	    	else if (folders[2].trim().equals("JSON"))
	    	{
	    		Logger.info("RestAPI KAInitByJSON(" + remoteAddr + ") " +
    				    "domain[" + domainName + "] " +
                        "conclusionId[" + conclusionIdStr + "] " +
                        "kaMode[" + kaModeStr + "] ");
	    		
	    		JSONObject rtnJSONObj = new JSONObject();
	    		response.setContentType("application/json");
    	        response.setCharacterEncoding("UTF-8");
	    		
    	        String reqBodyStr = Utility.parseString(request);
    	        if (reqBodyStr == null || reqBodyStr.isEmpty())
        		{
        			Logger.error("request body is empty");
        			
                	rtnJSONObj.put("validity", "error");
        			rtnJSONObj.put("msg", "request body is empty");
        	        response.getWriter().write(rtnJSONObj.toString());
        	        return;
        		}
	    		
	    		if (RDRConfig.isDebugRequest())
	            	Logger.info("request : " + reqBodyStr);
	    		
	    		rtnJSONObj = RDRService.initializeByJSON(domainName, 
	    				                                 reqBodyStr, 
								                         conclusionId, 
								                         kaModeStr, 
								                         remoteAddr);

	    		response.getWriter().write(rtnJSONObj.toString());
	    	}
    	} 
    	//-------------------------------------------------------------------
    	// /ka/conclusion, selectConclusion
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("conclusion"))
    	{
    		String domainName = request.getParameter("domain");
        	String remoteAddr = request.getRemoteAddr();
        	
    		Logger.info("RestAPI selectConclusion(" + remoteAddr + ") " +
				        "domain[" + domainName + "] ");
    		
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
        	
        	JSONObject rtnJSONObj = new JSONObject();
    		
            JSONObject recvJSONObj = Utility.parseJSONObject(request);
            if (recvJSONObj == null)
    		{
    			Logger.error("request body parsing failed");

            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", "request body parsing failed");
    	        response.getWriter().write(rtnJSONObj.toString());
    	        return;
    		}
                		
    		String selectedConclusionStr = (String)recvJSONObj.get("conclusion");
            Logger.info("RestAPI selectedConclusion[" + selectedConclusionStr + "] ");
            
            rtnJSONObj = RDRService.selectConclusion(domainName, 
            		                                 selectedConclusionStr, 
            		                                 remoteAddr);
            response.getWriter().write(rtnJSONObj.toString());
    	}
    }
	
	@Override
    protected void doPost(HttpServletRequest request, 
    		              HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg");

		System.out.println("KAServlet doPost start");
    	
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	
    	//for initialize domain 
    	RDRInterface aRDRInf = RDRInterface.getInstance();
    	    
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
    	// /ka/conclusion, addConclusion
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("conclusion"))
    	{
    		Logger.info("RestAPI addConclusion(" + remoteAddr + ") " +
    					"domain[" + domainName + "] ");
    		
    		JSONObject rtnJSONObj = new JSONObject();
    		
            JSONObject recvJSONObj = Utility.parseJSONObject(request);
            if (recvJSONObj == null)
    		{
    			Logger.error("request body parsing failed");

            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", "request body parsing failed");
    	        response.getWriter().write(rtnJSONObj.toString());
    	        return;
    		}
                		
    		String newConclusionStr = (String)recvJSONObj.get("conclusion");
            Logger.info("RestAPI newConclusion[" + newConclusionStr + "] ");
            
            rtnJSONObj = RDRService.addConclusion(domainName, newConclusionStr, remoteAddr);
            response.getWriter().write(rtnJSONObj.toString());
    	}
    	//-------------------------------------------------------------------
    	// /ka/condition, addCondition
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("condition"))
    	{
    		Logger.info("RestAPI addCondition(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		JSONObject rtnJSONObj = new JSONObject();
    		
            JSONObject recvJSONObj = Utility.parseJSONObject(request);
            if (recvJSONObj == null)
    		{
    			Logger.error("request body parsing failed");
    			
            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", "request body parsing failed");
    	        response.getWriter().write(rtnJSONObj.toString());
    	        return;
    		}
            
            String newConAttrStr = (String)recvJSONObj.get("attributeName");
            String newConOperStr = (String)recvJSONObj.get("operator");
            String newConValStr = (String)recvJSONObj.get("value");
            
            Logger.info("RestAPI newConAttrStr : " + newConAttrStr);
            Logger.info("RestAPI newConOperStr : " + newConOperStr);
            Logger.info("RestAPI newConValStr : " + newConValStr);
                        
            rtnJSONObj = RDRService.addCondition(domainName, 
            		                             newConAttrStr, 
            		                             newConOperStr,
            		                             newConValStr,
            		                             remoteAddr);
            
            response.getWriter().write(rtnJSONObj.toString());
    	}
    	//-------------------------------------------------------------------
    	// /ka/rule, addRule
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("rule"))
    	{
    		Logger.info("RestAPI addRule(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		JSONObject rtnJSONObj = new JSONObject();
            rtnJSONObj = RDRService.adddRule(domainName, remoteAddr);
            response.getWriter().write(rtnJSONObj.toString());
    	}
    }
	
	@Override
    protected void doDelete(HttpServletRequest request, 
    		                HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg");

		System.out.println("KAServlet doDelete start");
    	
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	
    	//for initialize domain 
    	RDRInterface aRDRInf = RDRInterface.getInstance();
    	        
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
    	// /ka/condition, deleteCondition
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("condition"))
    	{
    		Logger.info("RestAPI deleteCondition(" + remoteAddr + ") " +
					   	"domain[" + domainName + "] ");
    		
    		JSONObject rtnJSONObj = new JSONObject();
    		
            JSONObject recvJSONObj = Utility.parseJSONObject(request);
            if (recvJSONObj == null)
    		{
    			Logger.error("request body parsing failed");

            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", "request body parsing failed");
    	        response.getWriter().write(rtnJSONObj.toString());
    	        return;
    		}
            
            String newConAttrStr = (String)recvJSONObj.get("attributeName");
            String newConOperStr = (String)recvJSONObj.get("operator");
            String newConValStr = (String)recvJSONObj.get("value");

            Logger.info("RestAPI delConAttrStr : " + newConAttrStr);
            Logger.info("RestAPI delConOperStr : " + newConOperStr);
            Logger.info("RestAPI delConValStr : " + newConValStr);
            
            rtnJSONObj = RDRService.deleteCondition(domainName, 
            		                                newConAttrStr, 
            		                                newConOperStr, 
            		                                newConValStr, 
            		                                remoteAddr);
            response.getWriter().write(rtnJSONObj.toString());
    	}
    }
	
	@Override
    protected void doGet(HttpServletRequest request, 
    		             HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg");

		System.out.println("KAServlet doGet start");
    	
    	// /kb/a/b -> pathInfo : /a/b
    	//         -> folders : "", a, b (3)
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	//for ( int i = 0; i < folders.length; i++ )
    	//	System.out.println( i + " : " + folders[i]);
    	
    	//for initialize domain 
    	RDRInterface aRDRInf = RDRInterface.getInstance();
    	
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
    	// /kb/validation
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("validation"))
    	{
    		Logger.info("RestAPI getValidationCases(" + remoteAddr + ") " +
				   		"domain[" + domainName + "] ");
   
    		JSONObject rtnJSONObj = RDRService.getValidationCases(domainName, remoteAddr);
            response.getWriter().write(rtnJSONObj.toString());
    	}
    	//-------------------------------------------------------------------
    	// /kb/otherConclusions
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("otherConclusions"))
    	{
    		String ccidStr = request.getParameter("cornerstoneCaseId");
    		Logger.info("RestAPI getOtherConclusions(" + remoteAddr + ") " +
    					"domain[" + domainName + "]" +
				   		"cornerstoneCaseId[" + ccidStr + "] ");
    		
    		if (ccidStr == null || StringUtil.isNumeric(ccidStr) == false)
    		{
    			JSONObject errJSONObj = new JSONObject();
        		errJSONObj.put("validity", "error");
        		errJSONObj.put("msg", "param cornerstoneCaseId is invalid");
    	        response.getWriter().write(errJSONObj.toString());
    	        return;
    		}
    		
        	int cornerstoneCaseId = Integer.parseInt(request.getParameter("cornerstoneCaseId"));
        	System.out.println("cornerstoneCaseId : " + cornerstoneCaseId);
        	
        	JSONObject rtnJSONObj 
        		= RDRService.getOtherConclusions(domainName, cornerstoneCaseId, remoteAddr);
        	
	        response.getWriter().write(rtnJSONObj.toString());
    	}
    }
}
