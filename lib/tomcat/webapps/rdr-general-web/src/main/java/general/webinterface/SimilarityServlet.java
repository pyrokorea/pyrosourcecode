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
import rdr.apimsg.RDRResponse;
import general.restapi.RESTApiRequester;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseStructure;
import rdr.db.RDRDBManager;
import rdr.logger.Logger;
import rdr.similarity.SimilarityElement;
import rdr.similarity.SimilaritySolution;
import rdr.utils.RDRConfig;
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
@WebServlet("/similarity/*")
public class SimilarityServlet extends HttpServlet 
{
	@Override
    protected void doPut(HttpServletRequest request, 
    		             HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg/");

		System.out.println("Similarity doPost start");
    	
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
    	// /similarity/case
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("case"))
    	{
    		JSONObject rtnJSONObj = new JSONObject();
        	
        	String reqBodyStr = Utility.parseString(request);
        	
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
    	        return;
            }
            
            if (recvJSONObj == null ||
               	recvJSONObj.get("case") == null ||
               	recvJSONObj.get("compareCases") == null ||
               	recvJSONObj.get("compareAttributes") == null)
        	{
        		Logger.error("request body parsing failed or body contents is missing");

               	rtnJSONObj.put("validity", "error");
        		rtnJSONObj.put("msg", "request body parsing failed or body contents is missing");
        		return;
        	}
    		
            //case --------------------------------------------------------
            boolean flag = true;
            LinkedHashMap<String, String> valueMap;
            String[] msg;
            
            JSONObject caseJsonObj = (JSONObject)recvJSONObj.get("case");
            valueMap = JSONConverter.convertJSONObjectToValueMap(caseJsonObj);
            
            msg = new String[1];
    		Case aCase 
    			= RDRInterface.getInstance().getCaseFromValueMap(Main.domain.getCaseStructure(), 
    					                                         valueMap, msg);
    		if (aCase == null)
    		{
    			Logger.error("case creation failed, " + msg[0]);
    			flag = false;
    		}
    		
    		//compareCases ------------------------------------------------
    		ArrayList<Case> compareCases = new ArrayList<Case>();
    		JSONArray caseJsonArray = (JSONArray)recvJSONObj.get("compareCases");
    		Iterator<JSONObject> iterator = caseJsonArray.iterator();
    		while (iterator.hasNext())
    		{
    			JSONObject compareCaseJsonObj = (JSONObject)iterator.next();
    			
    			//Object tmp = compareCaseJsonObj.get("caseId");
    			//Integer tmpi = (Integer)tmp;
    			//int ii = tmpi.intValue();
    			
    			int caseId = ((Long)compareCaseJsonObj.get("caseId")).intValue();
    			JSONObject dataJsonObj = (JSONObject)compareCaseJsonObj.get("data");
    			
    			valueMap = JSONConverter.convertJSONObjectToValueMap(dataJsonObj);
            
                msg = new String[1];
        		Case cCase 
        			= RDRInterface.getInstance().getCaseFromValueMap(Main.domain.getCaseStructure(), 
        					                                         valueMap, msg);
        		if (cCase == null)
        		{
        			Logger.error("compare case creation failed, " + msg[0]);
        			flag = false;
        		}
        		else 
        		{
        			cCase.setCaseId(caseId);
        			compareCases.add(cCase);
        		}
    		}
            
            //compare attributes ---------------------------------------
    		ArrayList<String> compareAttributes = new ArrayList<String>();
    		JSONArray attrJsonArray = (JSONArray)recvJSONObj.get("compareAttributes");
    		Iterator<String> attrIter = attrJsonArray.iterator();
    		while (attrIter.hasNext())
    		{
    			String attrName = (String)attrIter.next();
    			compareAttributes.add(attrName);
    		}
    		
    		if (flag == false)
    		{
    			rtnJSONObj.put("validity", "error");
        		rtnJSONObj.put("msg", "request conversion failed");
        		return;
    		}
    		
            RDRResponse tResponse = RDRService.getSimilarity(domainName, 
			            		                             aCase, 
			            		                             compareCases,
			            		                             compareAttributes,
			            		                             remoteAddr);
            
	        response.getWriter().write(tResponse.getJSONSimilarity().toString());
    	}
    }

}
