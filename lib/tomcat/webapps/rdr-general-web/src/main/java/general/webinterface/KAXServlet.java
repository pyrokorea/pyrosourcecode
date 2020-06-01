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
import rdr.apimsg.KARequest;
import rdr.apimsg.KAResponse;
import rdr.apimsg.RDRResponse;
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
import rdr.learner.IncrementalLearner;
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
@WebServlet("/kax/*")
public class KAXServlet extends HttpServlet 
{
	@Override
    protected void doPost(HttpServletRequest request, 
    		             HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg/");

		System.out.println("KAXServlet doPost start");
    	
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
    	// /kax/validation
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("validation"))
    	{
    		boolean bSemiAuto = false;
            if (folders.length == 3 && folders[2].trim().equals("semiAuto"))
            {
            	bSemiAuto = true;
            	Logger.info("RestAPI kax/getValidationCasesSemiAuto(" + remoteAddr + ") " +
    					"domain[" + domainName + "] ");
            }
            else
            {
	    		Logger.info("RestAPI kax/getValidationCasesBatch(" + remoteAddr + ") " +
	    					"domain[" + domainName + "] ");
            }
            
            JSONObject rtnJSONObj = new JSONObject();
            
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
            
            JSONParser jsonParser = new JSONParser();
            JSONObject recvJSONObj = new JSONObject();
            
            try 
            {
            	recvJSONObj = (JSONObject)jsonParser.parse(reqBodyStr);
            }
            catch (Exception ex ) 
            {
            	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
            	rtnJSONObj.put("validity", "error");
            	rtnJSONObj.put("msg", ex.getClass().getName() + " : " + ex.getMessage());
            	response.getWriter().write(rtnJSONObj.toString());
    	        return;
            }
            
            if (recvJSONObj == null)
    		{
    			Logger.error("request json string parsing failed");

            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", "request json string parsing failed");
    			response.getWriter().write(rtnJSONObj.toString());
    	        return;
    		}
    		
            KARequest kaReq = new KARequest();
            StringBuilder sb = new StringBuilder();
            if (kaReq.buildFromJSON(recvJSONObj, bSemiAuto, sb) == false)
            {
            	Logger.error("request info building failed");

            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", sb.toString());
    			response.getWriter().write(rtnJSONObj.toString());
    	        return;
            }
            
            KAResponse tResponse
            	= RDRService.getValidationCasesBatch(domainName, 
            		                                 kaReq, 
            		                                 remoteAddr);
            rtnJSONObj = tResponse.getJSON();
            
            response.getWriter().write(rtnJSONObj.toString());
    	}
    	
    	//-------------------------------------------------------------------
    	// /kax/rule, addRule
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("rule"))
    	{
    		boolean bSemiAuto = false;
            if (folders.length == 3 && folders[2].trim().equals("semiAuto"))
            {
            	bSemiAuto = true;
            	Logger.info("RestAPI addRuleSemiAuto(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
            }
            else
            {
            	Logger.info("RestAPI addRuleBatch(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
            }
            
            JSONObject rtnJSONObj = new JSONObject();
            
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
            
            JSONParser jsonParser = new JSONParser();
            JSONObject recvJSONObj = new JSONObject();
            
            try 
            {
            	recvJSONObj = (JSONObject)jsonParser.parse(reqBodyStr);
            }
            catch (Exception ex ) 
            {
            	Logger.error(ex.getClass().getName() + " : " + ex.getMessage());
            	rtnJSONObj.put("validity", "error");
            	rtnJSONObj.put("msg", ex.getClass().getName() + " : " + ex.getMessage());
            	response.getWriter().write(rtnJSONObj.toString());
    	        return;
            }
            
            if (recvJSONObj == null)
    		{
    			Logger.error("request json string parsing failed");

            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", "request json string parsing failed");
    			response.getWriter().write(rtnJSONObj.toString());
    	        return;
    		}
    		
            KARequest kaReq = new KARequest();
            StringBuilder sb = new StringBuilder();
            if (kaReq.buildFromJSON(recvJSONObj, bSemiAuto, sb) == false)
            {
            	Logger.error("request info building failed");

            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", sb.toString());
    			response.getWriter().write(rtnJSONObj.toString());
    	        return;
            }
            
            KAResponse tResponse = RDRService.addRuleBatch(domainName, 
            		                                       kaReq, 
			            		                           remoteAddr);
            rtnJSONObj = tResponse.getJSON();
            
            response.getWriter().write(rtnJSONObj.toString());
    	}
    	//-------------------------------------------------------------------
    	// /kax/incremental, incrementalLearning
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("incremental"))
    	{
    		JSONObject rtnJSONObj = new JSONObject();
    		
//    		IncrementalLearner iLearner = new IncrementalLearner("biopsy_stomach_pseudo.arff");
//            if (iLearner.run())
//            {
//            	rtnJSONObj.put("validity", "valid");
//            }
//            else
//            {
//            	rtnJSONObj.put("validity", "error");
//            }
    		
    		response.getWriter().write(rtnJSONObj.toString());
    	}
    }
	
	
	
	
}
