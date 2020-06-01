package general.webinterface;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import rdr.api.RDRInterface;
import rdr.api.RDRService;
import rdr.apimsg.RDRResponse;
import rdr.logger.Logger;
import rdr.utils.RDRConfig;
import rdr.utils.StringUtil;
import rdr.utils.Utility;

@WebServlet("/delivery/*")
public class DeliveryServlet extends HttpServlet 
{
	@Override
    protected void doPost(HttpServletRequest request, 
    		              HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg/");

		System.out.println("DeliveryServlet doPost start");
    	
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	
    	//for initialize domain 
    	//RDRInterface aRDRInf = RDRInterface.getInstance();
    	    
        //String domainName = request.getParameter("domain");
    	
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        //-------------------------------------------------------------------
    	// /delivery/case
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("case"))
    	{
    		String userId = request.getParameter("userId");
        	
    		Logger.info("RestAPI deliveryCase post(" + request.getRemoteAddr() + ") " +
                        "userId[" + userId + "] ");
    		
    		JSONObject rtnJSONObj = new JSONObject();
    		
    		String reqBodyStr = Utility.parseString(request);
    		
    		//if (RDRConfig.isDebugRequest())
            //	Logger.info("request : " + reqBodyStr);
    		
    		if (reqBodyStr == null || reqBodyStr.isEmpty())
    		{
    			Logger.error("request body is empty");
    			
            	rtnJSONObj.put("validity", "error");
    			rtnJSONObj.put("msg", "request body is empty");
    			rtnJSONObj.put("timestamp", "");
    	        response.getWriter().write(rtnJSONObj.toString());
    	        return;
    		}
    		
    		String ts;
    		java.util.Date date = new java.util.Date();
    		
    		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    		
    		String fn = RDRConfig.getArffPath() + File.separator + "case_" + timestamp + "_" + userId + ".json";
			FileWriter seWriter = new FileWriter(new File(fn), true);
			seWriter.append(reqBodyStr);
			seWriter.close();
    		
			rtnJSONObj.put("validity", "valid");
			rtnJSONObj.put("msg", "");
			rtnJSONObj.put("timestamp", timestamp);
			
			//------------------------------------------------------------------------
			// 3일 이전의 파일 삭제
			File path = new File(RDRConfig.getArffPath());
			File[] files = path.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name)
				{
					return (name.startsWith("case") && name.endsWith("json"));
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
					Logger.info("case file deleted : " + files[i].getName());
					files[i].delete();
				}
			}
			//------------------------------------------------------------------------
			
    		response.getWriter().write(rtnJSONObj.toString());
	        return;
    	}
    }
	
	@Override
    protected void doGet(HttpServletRequest request, 
    		              HttpServletResponse response) throws ServletException, IOException 
    {
		RDRConfig.initWithRootPath(request.getContextPath(), 
                request.getSession().getServletContext().getRealPath("/WEB-INF") + File.separator + "/cfg/");

		System.out.println("DeliveryServlet doGet start");
    	
    	String pathInfo = request.getPathInfo();
    	System.out.println("path info : " + pathInfo);
    	String[] folders = pathInfo.split("/");
    	
    	//for initialize domain 
    	//RDRInterface aRDRInf = RDRInterface.getInstance();
    	    
        //String domainName = request.getParameter("domain");
    	
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        //-------------------------------------------------------------------
    	// /delivery/case
    	//-------------------------------------------------------------------
    	if (folders[1].trim().equals("case"))
    	{
    		String userId = request.getParameter("userId");
        	String timestamp = request.getParameter("timestamp");
        	
    		Logger.info("RestAPI deliveryCase get(" + request.getRemoteAddr() + ") " +
                        "timestamp[" + timestamp + "] userId[" + userId + "] ");
    		
    		JSONObject rtnJSONObj = new JSONObject();
    		JSONParser jsonParser = new JSONParser();    
    		
    		try 
            {
    			String fn = RDRConfig.getArffPath() + File.separator + "case_" + timestamp + "_" + userId + ".json";
    			if (Utility.isFileExist(fn) == false)
    			{
    				Logger.debug("case file not found : " + fn);
    				return;
    			}
    			String jsonStr = Utility.readFile(fn);
    			
    			
    			rtnJSONObj = (JSONObject)jsonParser.parse(jsonStr);
            }
            catch (Exception ex ) 
            {
            	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
            	response.getWriter().write(rtnJSONObj.toString());
    	        return;
            }
    		
    		response.getWriter().write(rtnJSONObj.toString());
	        return;
    	}
    }
}
