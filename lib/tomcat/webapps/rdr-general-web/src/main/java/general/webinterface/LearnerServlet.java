package general.webinterface;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import rdr.learner.IncrementalLearner;
import rdr.logger.Logger;
import rdr.utils.RDRConfig;
import rdr.utils.Utility;

@WebServlet("/learner/*")
public class LearnerServlet extends HttpServlet 
{
	@Override
    protected void doPost(HttpServletRequest request, 
    		              HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg/");

		System.out.println("Learner doPost start");
    	
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	    
        String domainName = request.getParameter("domain");
    	
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        //-------------------------------------------------------------------
    	// /learner/scrdr
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("scrdr"))
    	{
    		String userId = request.getParameter("userId");
        	
    		Logger.info("RestAPI incrementalLearner post(" + request.getRemoteAddr() + ") " +
                        "domain[" + domainName + "] userId[" + userId + "] ");
    		
    		JSONArray rtnJSONArr = new JSONArray();
    		
    		String reqBodyStr = Utility.parseString(request);
    		
    		//if (RDRConfig.isDebugRequest())
            //	Logger.info("request : " + reqBodyStr);
    		
    		if (reqBodyStr == null || reqBodyStr.isEmpty())
    		{
    			Logger.error("request body is empty");
    	        response.getWriter().write(rtnJSONArr.toString());
    	        return;
    		}
    		
    		JSONParser jsonParser = new JSONParser();
            JSONArray recvJSONArr = new JSONArray();
            
            try 
            {
            	recvJSONArr = (JSONArray)jsonParser.parse(reqBodyStr);
            }
            catch (Exception ex ) 
            {
            	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
            	response.getWriter().write(rtnJSONArr.toString());
    	        return;
            }
            
            if (recvJSONArr == null)
    		{
    			Logger.error("request json string parsing failed");
    			response.getWriter().write(rtnJSONArr.toString());
    	        return;
    		}
    		
            IncrementalLearner iLearner = new IncrementalLearner(domainName, userId);
    		boolean flag = true;
    		flag &= iLearner.prepareCaseJSON(recvJSONArr);
    		flag &= iLearner.runSCRDR(domainName, userId);
            
    		rtnJSONArr = iLearner.getResultJSON();
            
    		response.getWriter().write(rtnJSONArr.toString());
	        return;
    	}
    }
    		
}
