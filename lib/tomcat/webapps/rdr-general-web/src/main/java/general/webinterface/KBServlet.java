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
import java.util.Map;
import java.util.Set;
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
import rdr.rules.Conclusion;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseStructure;
import rdr.cases.CaseSynchroniser;
import rdr.cases.CornerstoneCase;
import rdr.cases.CornerstoneCaseSet;
import rdr.db.RDRDBManager;
import rdr.domain.DomainLoader;
import rdr.learner.Learner;
import rdr.logger.Logger;
import rdr.model.IAttribute;
import rdr.model.Value;
import rdr.rules.ConclusionSet;
import rdr.rules.ConditionSet;
import rdr.rules.Rule;
import rdr.rules.RuleSet;
import rdr.utils.RDRConfig;
import rdr.utils.StringUtil;
import rdr.utils.Utility;

/**
*
* @author ucciri@gmail.com
*/
@WebServlet("/kb/*")
public class KBServlet extends HttpServlet 
{
    @Override
    protected void doGet(HttpServletRequest request, 
    		             HttpServletResponse response) throws ServletException, IOException 
    {
    	RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg/");

    	System.out.println("KBServlet doGet start");
    	
    	RDRDBManager.getInstance().connectDataBase(0);
    	
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
    	
    	response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
    	//-------------------------------------------------------------------
    	// /kb/check
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("check"))
    	{
    		Logger.info("RestAPI check(" + remoteAddr + ") ");
            response.getWriter().write("KBServlet, user dir is [" + System.getProperty("user.dir") + "]");
            response.getWriter().write("\n");
            response.getWriter().write("ContextPath : " + request.getContextPath());
            response.getWriter().write("\n");
            response.getWriter().write("realPath : " + this.getClass().getResource("/").getPath());
            response.getWriter().write("\n");
            response.getWriter().write("WEB-INF : " + request.getSession().getServletContext().getRealPath("/WEB-INF"));
            
            //URL resource = this.getClass().getClassLoader().getResource("test.txt");
            //File file = new File(resource.getFile());
            
            //response.getWriter().write("\n");
            //response.getWriter().write("file : " + file.getPath());
            //response.getWriter().write("file : " + file.getName());
            
            return;
    	}
    	
    	//-------------------------------------------------------------------
    	// /kb/domain
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("domain"))
    	{
    		Logger.info("RestAPI getAllDomains(" + remoteAddr + ")");
    		
    		RDRResponse tResponse = RDRService.getAllDomains();
    		response.getWriter().write(tResponse.getJSONDomain().toString());
    		return;
    	}
    	    	
    	//-------------------------------------------------------------------
    	// /kb/conclusion/[ID]
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("conclusion"))
    	{
    		Logger.info("RestAPI getConclusion(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
            
    		if (folders.length == 3) //has id
    		{
    			if (StringUtil.isNumeric(folders[2].trim()) == false)
    			{
    				JSONObject emptyJSONObj = new JSONObject();
    		        response.getWriter().write(emptyJSONObj.toString());
    		        return;
    			}
    			
        		int conclusionId = Integer.parseInt(folders[2].trim());
        		
        		RDRResponse tResponse 
        			= RDRService.getConclusion(domainName,  conclusionId, remoteAddr);
            	response.getWriter().write(tResponse.getJSONConclusion().toString());
    		}
    		else
    		{
    			RDRResponse tResponse 
    				= RDRService.getAllConclusions(domainName, remoteAddr);
            	response.getWriter().write(tResponse.getJSONConclusions().toString());
    		}
    	}
    	//-------------------------------------------------------------------
    	// /kb/operator
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("operator"))
    	{
    		Logger.info("RestAPI getAllOperators(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		RDRResponse tResponse
    			= RDRService.getAllOperators(domainName, remoteAddr);

        	response.getWriter().write(tResponse.getJSONOperators().toString());
    	}
    	//-------------------------------------------------------------------
    	// /kb/caseStructure
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("caseStructure"))
    	{
    		String attrName = request.getParameter("name");
    		
    		Logger.info("RestAPI getCaseStructure(" + remoteAddr + ") " +
						"domain[" + domainName + "] " +
    				    "attrName[" + attrName + "]");
    		
    		RDRResponse tResponse 
    			= RDRService.getCaseStructure(domainName, attrName, remoteAddr);

        	response.getWriter().write(tResponse.getJSONCaseStructure().toString());
    	}
    	//-------------------------------------------------------------------
    	// /kb/rule
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("rule"))
    	{
    		Logger.info("RestAPI getRule(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		if (folders.length == 3) //has id
    		{
    			if (StringUtil.isNumeric(folders[2].trim()) == false)
    			{
    				JSONObject emptyJSONObj = new JSONObject();
    		        response.getWriter().write(emptyJSONObj.toString());
    		        return;
    			}
    			
        		int ruleId = Integer.parseInt(folders[2].trim());
        		RDRResponse tResponse = RDRService.getRule(domainName, ruleId, remoteAddr);
        		
            	response.getWriter().write(tResponse.getJSONRule().toString());
    		}
    		else
    		{
    			RDRResponse tResponse = RDRService.getAllRules(domainName, remoteAddr);
            	response.getWriter().write(tResponse.getJSONRules().toString());
    		}
    	}
    	//-------------------------------------------------------------------
    	// /kb/childRules
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("childRules"))
    	{
    		Logger.info("RestAPI getChildRules(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		String ruleIdStr = request.getParameter("id");
    		
    		JSONArray aJSONArray = new JSONArray();
    		if (StringUtil.isNumeric(ruleIdStr) == false)
    		{
		        response.getWriter().write(aJSONArray.toString());
		        return;
    		}
    		
    		int ruleId = Integer.parseInt(ruleIdStr);
    		Logger.info("ruleId : " + ruleId);
    		
    		RDRResponse tResponse 
    			= RDRService.getChildRules(domainName, ruleId, remoteAddr);
       		response.getWriter().write(tResponse.getJSONRules().toString());
    	}
    	//-------------------------------------------------------------------
    	// /kb/pathRules
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("pathRules"))
    	{
    		Logger.info("RestAPI getPathRules(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		String ruleIdStr = request.getParameter("id");
    		
    		JSONArray aJSONArray = new JSONArray();
    		if (StringUtil.isNumeric(ruleIdStr) == false)
    		{
		        response.getWriter().write(aJSONArray.toString());
		        return;
    		}
    		
    		int ruleId = Integer.parseInt(ruleIdStr);
    		Logger.info("ruleId : " + ruleId);
    		
    		RDRResponse tResponse 
    			= RDRService.getPathRules(domainName, ruleId, remoteAddr);
       		response.getWriter().write(tResponse.getJSONRules().toString());
    	}
    	//-------------------------------------------------------------------
    	// /kb/cornerstone
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("cornerstone"))
    	{
    		Logger.info("RestAPI getCornerstoneCase(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		CornerstoneCaseSet aCCSet = RDRInterface.getInstance().getCornerstoneCaseSet();
    		
    		if (folders.length == 3) //has id
    		{
    			if (StringUtil.isNumeric(folders[2].trim()) == false )
    			{
    				JSONObject emptyJSONObj = new JSONObject();
    		        response.getWriter().write(emptyJSONObj.toString());
    		        return;
    			}
    			
        		int ccid = Integer.parseInt(folders[2].trim());
        		
        		RDRResponse tResponse 
        			= RDRService.getCornerstoneCase(domainName, ccid, remoteAddr);
            	response.getWriter().write(tResponse.getJSONCase().toString());
    		}
    		else
    		{
    			RDRResponse tResponse
    				= RDRService.getAllCornerstoneCases(domainName, remoteAddr);
            	response.getWriter().write(tResponse.getJSONCases().toString());
    		}
    	}
    	//-------------------------------------------------------------------
    	// /kb/nullValue
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("nullValue"))
    	{
    		Logger.info("RestAPI getNullValueString(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		ArrayList<String> nullStrs = RDRService.getNullValueString(domainName, remoteAddr);
    		
    		JSONArray aJSONArray = new JSONArray();
    		for (int ni = 0; ni < nullStrs.size(); ni++)
    			aJSONArray.add(nullStrs.get(ni));
    		
        	response.getWriter().write(aJSONArray.toString());
    	}
    }
    
    @Override
    protected void doPut(HttpServletRequest request, 
    		             HttpServletResponse response) throws ServletException, IOException 
    {
    	RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg");

    	System.out.println("KBServlet doPut start");
    	
    	// /kb/a/b -> pathInfo : /a/b
    	//         -> folders : "", a, b (3)
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	
    	//for initialize domain 
    	RDRInterface aRDRInf = RDRInterface.getInstance();
    	
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        JSONObject rtnJSONObj = new JSONObject();
        
        //-------------------------------------------------------------------
    	// /kb/createRDRTables
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("createRDRTables"))
    	{
    		Logger.info("RestAPI createRDRTable(" + request.getRemoteAddr() + ")");
    		
    		RDRResponse tResponse = RDRService.createRDRTables();
	        response.getWriter().write(tResponse.getJSON().toJSONString());
	        return;
    	}
    	//-------------------------------------------------------------------
    	// /kb/connectDB
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("connectDB"))
    	{
    		Logger.info("RestAPI connectDataBase(" + request.getRemoteAddr() + ")");
    		
    		JSONObject recvJSONObj = Utility.parseJSONObject(request);
    		String dbType = (String)recvJSONObj.get("dbType");
    		String sqliteFile = (String)recvJSONObj.get("sqliteFile");
    		String dbDriver = (String)recvJSONObj.get("dbDriver");
    		String dbURL = (String)recvJSONObj.get("dbURL");
    		String dbName = (String)recvJSONObj.get("dbName");
    		String user = (String)recvJSONObj.get("user");
    		String pass = (String)recvJSONObj.get("pass");
    		
    		RDRResponse tResponse
    			= RDRService.connectDataBase(dbType, sqliteFile, dbDriver, dbURL, dbName, user, pass);
	        response.getWriter().write(tResponse.getJSON().toJSONString());
	        
	        return;
    	}
    	
        String domainName = request.getParameter("domain");
    	String remoteAddr = request.getRemoteAddr();
    	
    	if (domainName == null)
        {
        	JSONObject errJSONObj = new JSONObject();
    		errJSONObj.put("validity", "error");
    		errJSONObj.put("msg", "domain is missing");
	        response.getWriter().write(errJSONObj.toString());
	        return;
        }
 	
    	//-------------------------------------------------------------------
    	// /kb/conclusion
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("conclusion"))
    	{
    		Logger.info("RestAPI editConclusion(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		JSONObject recvJSONObj = Utility.parseJSONObject(request);
            if (recvJSONObj == null)
    		{
    			Logger.error("request body parsing failed");

            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", "request body parsing failed");
    	        response.getWriter().write(rtnJSONObj.toString());
    	        return;
    		}
            
    		int id = ((Long)recvJSONObj.get("id")).intValue();
    		String name = (String)recvJSONObj.get("name");
    		
    		RDRResponse tResponse
    			= RDRService.editConclusion(domainName, id, name, remoteAddr);
	        response.getWriter().write(tResponse.getJSON().toString());
    	}
    	else if (folders[1].trim().equals("caseStructure"))
    	{
    		//-------------------------------------------------------------------
        	// /kb/caseStructure/ARFF
        	//-------------------------------------------------------------------
    		if (folders[2].trim().equals("ARFF"))
    		{
    			Logger.info("RestAPI setCaseStructureByARFF(" + remoteAddr + ") " +
    						"domain[" + domainName + "] ");
    			
        		String reqBodyStr = Utility.parseString(request);
        		if (reqBodyStr == null || reqBodyStr.isEmpty())
        		{
        			Logger.error("request body is empty");
        			
                	rtnJSONObj.put("validity", "error");
        			rtnJSONObj.put("msg", "request body is empty");
        	        response.getWriter().write(rtnJSONObj.toString());
        	        return;
        		}
        		
        		RDRResponse tResponse
        			= RDRService.setCaseStructureByARFF(domainName, reqBodyStr, remoteAddr);
        		response.getWriter().write(tResponse.getJSON().toString());
    		}
    		//-------------------------------------------------------------------
        	// /kb/caseStructure/JSON
        	//-------------------------------------------------------------------
    		else if (folders[2].trim().equals("JSON"))
    		{
    			Logger.info("RestAPI setCaseStructureByJSON(" + remoteAddr + ") " +
							"domain[" + domainName + "] ");
    			
    			String reqBodyStr = Utility.parseString(request);
    			if (reqBodyStr == null || reqBodyStr.isEmpty())
        		{
        			Logger.error("request body is empty");
        			
                	rtnJSONObj.put("validity", "error");
        			rtnJSONObj.put("msg", "request body is empty");
        	        response.getWriter().write(rtnJSONObj.toString());
        	        return;
        		}
    			
    			RDRResponse tResponse 
    				= RDRService.setCaseStructureByJSON(domainName, reqBodyStr, remoteAddr);
    			response.getWriter().write(tResponse.getJSON().toString());
    		}
    	}
    	//-------------------------------------------------------------------
    	// /kb/attribute
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("attribute"))
    	{
    		Logger.info("RestAPI editAttributeName(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		JSONObject recvJSONObj = Utility.parseJSONObject(request);
        	
        	String attrName = (String)recvJSONObj.get("name");
        	String newAttrName = (String)recvJSONObj.get("newName");
    		
    		RDRResponse tResponse
    			= RDRService.editAttributeName(domainName, attrName, newAttrName, remoteAddr);
    		response.getWriter().write(tResponse.getJSON().toString());
    	}
    	//-------------------------------------------------------------------
    	// /kb/attributeDesc
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("attributeDesc"))
    	{
    		Logger.info("RestAPI editAttributeDesc(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		JSONObject recvJSONObj = Utility.parseJSONObject(request);
        	
        	String attrName = (String)recvJSONObj.get("name");
        	String attrDesc = (String)recvJSONObj.get("desc");
    		
        	RDRResponse tResponse
        		= RDRService.editAttributeDesc(domainName, attrName, attrDesc, remoteAddr);
    		response.getWriter().write(tResponse.getJSON().toString());
    	}
    	
    }
    
    @Override
    protected void doPost(HttpServletRequest request, 
    		              HttpServletResponse response) throws ServletException, IOException 
    {
    	RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg");

    	System.out.println("KBServlet doPost start");
    	
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
    	// /kb/domain : add domain
    	//-------------------------------------------------------------------
        if (folders[1].trim().equals("domain"))
        {
        	String domainDesc = request.getParameter("desc");
            String domainReasoner = request.getParameter("reasoner");
            
            if (domainDesc == null || domainReasoner == null)
            {
            	JSONObject errJSONObj = new JSONObject();
        		errJSONObj.put("validity", "error");
        		errJSONObj.put("msg", "domain info is missing");
    	        response.getWriter().write(errJSONObj.toString());
    	        return;
            }
        	
        	//현재 domainName과 다를 것이므로 checkDomain으로 넘어가면 안됨
        	Logger.info("RestAPI addDomain(" + remoteAddr + ") " +
	        			"domain[" + domainName + "] " +
	        			"domainDesc[" + domainDesc + "] " +
	        			"domainReasoner[" + domainReasoner + "] ");
        	
        	RDRResponse tResponse = RDRService.addDomain(domainName, 
        			                                     domainDesc, 
        			                                     domainReasoner, 
        			                                     remoteAddr);
        	
        	response.getWriter().write(tResponse.getJSON().toString());
	        return;
        }
    	
//    	if (RDRInterface.getInstance().checkDomain(domainName, remoteAddr, false) == false)
//    	{
//    		Logger.error("RestAPI, KBServlet, check&init domain failed");
//    		
//    		JSONObject errJSONObj = new JSONObject();
//    		errJSONObj.put("validity", "error");
//    		errJSONObj.put("msg", "domain initialize failed");
//	        response.getWriter().write(errJSONObj.toString());
//	        return;
//    	}
        
        //-------------------------------------------------------------------
    	// /kb/caseStructure : add case structure
    	//-------------------------------------------------------------------
        if (folders[1].trim().equals("caseStructure"))
        {
        	Logger.info("RestAPI addCaseStructure(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
        	
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
        	
        	
        	ArrayList<String> addedAttr = new ArrayList<String>();
        	RDRResponse tResponse = RDRService.addCaseStructure(domainName, 
        			                                            reqBodyStr, 
        			                                            remoteAddr, 
        			                                            addedAttr);
        	
        	rtnJSONObj = tResponse.getJSON();
        	        	
        	JSONArray listJSONArray = new JSONArray();
			for (int ai = 0; ai < addedAttr.size(); ai++)
			{
				listJSONArray.add(addedAttr.get(ai));
			}
			rtnJSONObj.put("added", listJSONArray);
        	
        	response.getWriter().write(rtnJSONObj.toString());
	        return;
    	}
        //-------------------------------------------------------------------
    	// /kb/attribute : add attribute
    	//-------------------------------------------------------------------
        else if (folders[1].trim().equals("attribute"))
        {
        	Logger.info("RestAPI addAttribute(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
        	 
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
        	
        	JSONParser jsonParser = new JSONParser();
            JSONObject recvJSONObj = new JSONObject();
            IAttribute newAttr = null;
            
            try 
            {
            	recvJSONObj = (JSONObject)jsonParser.parse(reqBodyStr);
            	if (recvJSONObj == null)
        		{
        			Logger.error("request json string parsing failed");

                	rtnJSONObj.put("validity", "error");
        			rtnJSONObj.put("msg", "request json string parsing failed");
        			return ;
        		}
            	
            	newAttr = JSONConverter.convertJSONObjectToAttribute(recvJSONObj);
            }
            catch (Exception ex ) 
            {
            	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
            	rtnJSONObj.put("validity", "error");
            	rtnJSONObj.put("msg", ex.getClass().getName() + " : " + ex.getMessage());
    	        return ;
            }
            
        	RDRResponse tResponse
        		= RDRService.addAttribute(domainName, newAttr, remoteAddr);
        	response.getWriter().write(tResponse.getJSON().toString());
	        return;
    	}
        //-------------------------------------------------------------------
    	// /kb/categorical
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("categorical"))
    	{
    		Logger.info("RestAPI addCategorical(" + remoteAddr + ") " +
						"domain[" + domainName + "] " );
    		
    		JSONObject recvJSONObj = new JSONObject();
            JSONObject rtnJSONObj = new JSONObject();

            try 
            {
            	recvJSONObj = Utility.parseJSONObject(request);
            }
            catch (Exception ex ) 
            {
            	Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
            	rtnJSONObj = new JSONObject();
            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", "add categorical value exception failed");
    	        response.getWriter().write(rtnJSONObj.toString());
    	        return;
            }
        	
            String attrName = (String)recvJSONObj.get("name");
        	String catValue = (String)recvJSONObj.get("value");
			
        	RDRResponse tResponse = RDRService.addCategorical(domainName, 
			        			                              attrName, 
			        			                              catValue,
			        			                              remoteAddr);
        	
        	response.getWriter().write(tResponse.getJSON().toString());
	        return;
    	}
        //-------------------------------------------------------------------
    	// /kb/cornerstoneCase
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("cornerstoneCase"))
    	{
    		Logger.info("RestAPI addCornerstoneCase(" + remoteAddr + ") " +
						"domain[" + domainName + "] " );
    		
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
            	rtnJSONObj.put("addedRule", null);
    	        return;
            }
            
            if (recvJSONObj == null)
    		{
    			Logger.error("request json string parsing failed");

            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", "request json string parsing failed");
    			rtnJSONObj.put("addedRule", null);
    			return;
    		}
        	
            LinkedHashMap<String, String> valueMap 
				= JSONConverter.convertJSONObjectToValueMap(recvJSONObj);
			        	
			ArrayList<Integer> addedRules = new ArrayList<Integer>();
        	RDRResponse tResponse
        		= RDRService.addCornerstoneCase(domainName, valueMap, remoteAddr, addedRules);
        	
        	JSONArray ruleArray = new JSONArray();
        	for (int ri = 0; ri < addedRules.size(); ri++)
        	{
        		ruleArray.add(addedRules.get(ri).intValue());
        	}
        	
        	rtnJSONObj = tResponse.getJSON();
        	rtnJSONObj.put("addedRule", ruleArray);
        	
        	response.getWriter().write(rtnJSONObj.toString());
	        return;
    	}
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, 
    		                HttpServletResponse response) throws ServletException, IOException 
    {
    	RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg");

    	System.out.println("KBServlet doDelete start");
    	
    	// /kb/a/b -> pathInfo : /a/b
    	//         -> folders : "", a, b (3)
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	
    	//for initialize domain 
    	RDRInterface aRDRInf = RDRInterface.getInstance();
    	
        JSONObject rtnJSONObj = new JSONObject();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String domainName = request.getParameter("domain");
    	String remoteAddr = request.getRemoteAddr();
    	
    	if (domainName == null)
        {
        	JSONObject errJSONObj = new JSONObject();
    		errJSONObj.put("validity", "error");
    		errJSONObj.put("msg", "domain is missing");
	        response.getWriter().write(errJSONObj.toString());
	        return;
        }
    	    	     	
    	//-------------------------------------------------------------------
    	// /kb/domain
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("domain"))
    	{
    		Logger.info("RestAPI deleteDomain(" + remoteAddr + ") " +
						"domain[" + domainName + "] ");
    		
    		RDRResponse tResponse = RDRService.deleteDomain(domainName);
    		
	        response.getWriter().write(tResponse.getJSON().toString());
	        return;
    	}
    	
    	//-------------------------------------------------------------------
    	// /kb/attribute
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("attribute"))
    	{
    		String attrName = request.getParameter("name");
    		
    		Logger.info("RestAPI deleteAttribute(" + remoteAddr + ") " +
						"domain[" + domainName + "] " +
    				    "attrName[" + attrName + "]");
    		
    		if (attrName == null)
    		{
    			JSONObject errJSONObj = new JSONObject();
        		errJSONObj.put("validity", "error");
        		errJSONObj.put("msg", "attribute name is missing");
    	        response.getWriter().write(errJSONObj.toString());
    	        return;
    		}
    		
    		RDRResponse tResponse
    			= RDRService.deleteAttribute(domainName, attrName, remoteAddr);
    		response.getWriter().write(tResponse.getJSON().toString());
    	}
    	//-------------------------------------------------------------------
    	// /kb/rule
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("rule"))
    	{
    		String ruleIdStr = request.getParameter("ruleId");
    		
    		Logger.info("RestAPI deleteRule(" + remoteAddr + ") " +
						"domain[" + domainName + "] " +
    				    "attrName[" + ruleIdStr + "]");
    		
    		if (ruleIdStr == null)
    		{
    			JSONObject errJSONObj = new JSONObject();
        		errJSONObj.put("validity", "error");
        		errJSONObj.put("msg", "rule id is missing");
    	        response.getWriter().write(errJSONObj.toString());
    	        return;
    		}
    		
    		int ruleId = Integer.parseInt(ruleIdStr);
    		
    		RDRResponse tResponse
    			= RDRService.deleteRule(domainName, ruleId, remoteAddr);
    		response.getWriter().write(tResponse.getJSON().toString());
    	}
    }
}
