package general.webinterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rdr.api.JSONConverter;
import rdr.api.RDRInterface;
import general.restapi.RESTApiRequester;
import rdr.apps.Main;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseStructure;
import rdr.utils.RDRConfig;

@WebServlet("/test/*")
public class TestServlet extends HttpServlet {
	    
    @Override
    protected void doGet(HttpServletRequest request, 
    		             HttpServletResponse response) throws ServletException, IOException 
    {
    	System.out.println("TestServlet doGet Called");
    	
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");

    	RDRConfig.init();
    	
    	//for initialize domain 
    	RDRInterface aRDRInf = RDRInterface.getInstance();
    	
    	//-------------------------------------------------------------------
    	// /test/genCaseStructureJSON : read arff, write json (CaseStructure)
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("genCaseStructureJSON"))
    	{
    		JSONArray csJSONArray = new JSONArray();
    		
    		String csJsonLocation = RDRConfig.getOutPath() + "caseStructure.json";
    		      
    		boolean flag = true;
    		try {
    			CaseStructure arffCaseStructure = CaseLoader.getArffCaseStructure();
    			csJSONArray = JSONConverter.convertCaseStructureToJSONArray(arffCaseStructure, null);
    			
    			BufferedWriter out = new BufferedWriter(new FileWriter(csJsonLocation));
                out.write(csJSONArray.toString()); //out.newLine();
                out.close();
   			
    		} catch (Exception ex) {
    			ex.printStackTrace();
    			flag = false;
    		}
    		
    		JSONObject rtnJSONObj = new JSONObject();
    		rtnJSONObj.put("validity", (flag ? "valid" : "error"));
	    	rtnJSONObj.put("caseStructure JSON File", csJsonLocation);
    		
    		response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(rtnJSONObj.toString());
    	}
    	//-------------------------------------------------------------------
    	// /test/genCaseJSON : read arff, write json (Case)
    	//-------------------------------------------------------------------
    	else if (folders[1].trim().equals("genCaseJSON"))
    	{
    		String domainName = request.getParameter("domain");
    		if (domainName == null || domainName.isEmpty())
    		{
    			JSONObject errJSONObj = new JSONObject();
    			errJSONObj.put("validity", "error");
    			errJSONObj.put("msg", "domain is missing");
    	  		
    	  		response.setContentType("application/json");
    	        response.setCharacterEncoding("UTF-8");
    	        response.getWriter().write(errJSONObj.toString());
    	        return;
    		}
    		
    		RDRInterface.getInstance().initializeDomain(domainName);
    		String caseJsonLocation = RDRConfig.getOutPath() + "case.json";
 		
    		//caseId는 arff파일에 순서를 의미하고 1부터 시작함
    		//int caseId = 1; //default
    		//if (request.getParameter("caseId") != null)
    		//	caseId = Integer.parseInt(request.getParameter("caseId"));
    		
    		JSONArray caseJsonArray = new JSONArray();
    		
    		for (int caseId = 1; caseId <= 1000; caseId++)
    		{
    			JSONObject caseJsonObj = new JSONObject();
    			caseJsonObj.put("caseId", caseId);
    			
	    		JSONObject dataJsonObj = null;
	    		
		  		try 
		  		{
		  			CaseStructure aCaseStructure = Main.domain.getCaseStructure();
		  			Case aCase = CaseLoader.caseLoad(caseId, null);
		  			if (aCase != null)
		  			{
		  				dataJsonObj = JSONConverter.convertCaseToJSONObject(aCaseStructure, aCase);
			  			
		  				caseJsonObj.put("data", dataJsonObj);
		  			}
		  		} 
		  		catch (Exception ex) 
		  		{
		  			ex.printStackTrace();
		  		}
		  		
		  		caseJsonArray.add(caseJsonObj);
    		}
    		
    		
    		BufferedWriter out = new BufferedWriter(new FileWriter(caseJsonLocation));
            out.write(caseJsonArray.toJSONString()); //out.newLine();
            out.close();
            
            
	  		JSONObject rtnJSONObj = new JSONObject();
	  		rtnJSONObj.put("validity", (true ? "valid" : "error"));
	  		rtnJSONObj.put("caseStructure JSON File", caseJsonLocation);
	  		//rtnJSONObj.put("caseId", caseId);
	  		
	  		response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(rtnJSONObj.toString());
    	}
    	
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	System.out.println("TestServlet doPost Called");
    	
    	StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = null;
        String content = "";

        try {
            //InputStream inputStream = request.getInputStream();
            //inputStream.available();
            //if (inputStream != null) {
            bufferedReader =  request.getReader() ; //new BufferedReader(new InputStreamReader(inputStream));
            
            char[] charBuffer = new char[128];
            int bytesRead;
            while ( (bytesRead = bufferedReader.read(charBuffer)) != -1 ) {
                sb.append(charBuffer, 0, bytesRead);
            }
            //} else {
            //        sb.append("");
            //}

        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        
        System.out.println(sb.toString());

    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("It's TestServlet POST, user dir is [" + System.getProperty("user.dir") + "]");
           
    }
}
