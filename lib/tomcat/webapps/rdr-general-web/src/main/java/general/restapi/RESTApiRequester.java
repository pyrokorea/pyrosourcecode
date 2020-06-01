/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.restapi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rdr.apps.DomainSetupApp;
import rdr.logger.Logger;

public class RESTApiRequester 
{
	public static void main(String[] args) 
	{
		RESTApiRequester tApp = new RESTApiRequester();
		
		for (int i = 0; i < 50; i++)
		{
			JSONArray jsonArray 
				= tApp.requestArray(//"http://localhost:8080/rdr-pbs/smf/pathToConclusion?domain=pbs&testCode=11052&userId=ucciri", 
						            "http://localhost:8080/rdr-pbs/smf/pathToConclusion?domain=pbs&testCode=11052&comment=PBSP152&userId=ucciri",
				               "GET", null);
			System.out.println(jsonArray.toJSONString());
		}
	}
	
    public HttpURLConnection getHttpConnection(String url, String type)
    {
        URL uri = null;
        HttpURLConnection con = null;
        try
        {
            uri = new URL(url);
            con = (HttpURLConnection) uri.openConnection();
            con.setRequestMethod(type); //type: POST, PUT, DELETE, GET
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setConnectTimeout(60000); //60 secs
            con.setReadTimeout(60000);    //60 secs
            con.setRequestProperty("Accept-Encoding", "UTF-8");
            con.setRequestProperty("Content-Type", "UTF-8");
        }
        catch(Exception e)
        {
            System.err.println("connection i/o failed" );
        }


        return con;
    }   
    
    public JSONObject requestObject(String url, String type, String reqbody)
    {
        HttpURLConnection con = null;
        JSONObject aJSONObject = null;
        try 
        {
            con = getHttpConnection( url , type);
        
            //you can add any request body here if you want to post
            if( reqbody != null)
            {  
                con.setDoInput(true);
                con.setDoOutput(true);
                DataOutputStream out = new  DataOutputStream(con.getOutputStream());
                out.writeBytes(reqbody);
                out.flush();
                out.close();
            }
            con.connect();
            
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8")); 
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            
            JSONParser jsonParser = new JSONParser();
            aJSONObject = (JSONObject)jsonParser.parse(responseStrBuilder.toString());
            
            aJSONObject.put("status", con.getResponseCode());
            streamReader.close();
            
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        catch (ParseException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        
        return aJSONObject;
    }
    
    public JSONArray requestArray(String url, String type, String reqbody)
    {
        HttpURLConnection con = null;
        JSONArray aJSONArray = null;
        try 
        {
            con = getHttpConnection( url , type);
        
            //you can add any request body here if you want to post
            if( reqbody != null)
            {  
                con.setDoInput(true);
                con.setDoOutput(true);
                DataOutputStream out = new  DataOutputStream(con.getOutputStream());
                out.writeBytes(reqbody);
                out.flush();
                out.close();
            }
            con.connect();
            
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8")); 
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            
            JSONParser jsonParser = new JSONParser();
            aJSONArray = (JSONArray)jsonParser.parse(responseStrBuilder.toString());
            
            System.out.println("status : " + con.getResponseCode());
            streamReader.close();
            
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        catch (ParseException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        
        return aJSONArray;
    }
}
