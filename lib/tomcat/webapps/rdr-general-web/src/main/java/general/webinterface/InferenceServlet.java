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
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.swing.JOptionPane.showMessageDialog;

import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rdr.api.JSONConverter;
import rdr.api.RDRInterface;
import rdr.api.RDRService;
import rdr.apimsg.CaseItem;
import rdr.apimsg.RDRResponse;
import general.restapi.RESTApiRequester;
import rdr.apps.Main;
import rdr.rules.Conclusion;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseStructure;
import rdr.cases.CaseSynchroniser;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.db.RDRDBManager;
import rdr.learner.Learner;
import rdr.logger.Logger;
import rdr.model.Value;
import rdr.mysql.MysqlConnection;
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
@WebServlet("/inference/*")
public class InferenceServlet extends HttpServlet 
{
	@Override
    protected void doPut(HttpServletRequest request, 
    		             HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg/");

		System.out.println("InferenceServlet doPut start");
    	
    	RDRDBManager.getInstance().connectDataBase(0);
    	
    	String pathInfo = request.getPathInfo();
    	//System.out.println("path info : " + pathInfo);
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
    	// /inference/ARFF
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("ARFF"))
    	{
    		String syncStr = request.getParameter("sync");
    		Logger.info("RestAPI inferenceByARFF(" + request.getRemoteAddr() + ") " +
                        "domain[" + domainName + "] " +
                        "sync[" + syncStr + "] ");
    		
    		if (syncStr == null || StringUtil.isNumeric(syncStr) == false)
    		{
    			JSONObject errJSONObj = new JSONObject();
      		  	errJSONObj.put("validity", "error");
      		  	errJSONObj.put("msg", "check sync flag");
      		  	response.getWriter().write(errJSONObj.toString());
      		  	return;
    		}
    		
    		int syncFlag = Integer.parseInt(request.getParameter("sync"));
		
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
    		
    		RDRResponse tResponse
    			= RDRService.getInferenceByARFF(domainName, reqBodyStr, syncFlag, remoteAddr);
    		
    		rtnJSONObj = tResponse.getJSON();
    		
    		response.getWriter().write(rtnJSONObj.toString());
	        return;
    	}
    	//-------------------------------------------------------------------
    	// /inference/JSON
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("JSON"))
    	{
    		Logger.info("RestAPI inferenceByJSON(" + request.getRemoteAddr() + ") " +
                        "domain[" + request.getParameter("domain") + "] ");
    		
    		JSONObject rtnJSONObj = new JSONObject();
    		
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
    		
    		CaseItem tCaseItem = new CaseItem();
    		tCaseItem.buildFromJSON(recvJSONObj);
    		
    		RDRResponse tResponse
    			= RDRService.getInferenceByJSON(domainName, tCaseItem, remoteAddr);
    		
    		rtnJSONObj = tResponse.getJSON();
    		
    		Logger.debug("@inference : " + rtnJSONObj);
    		
    		response.getWriter().write(rtnJSONObj.toString());
	        return;
    	} 
    }
	
	
}
