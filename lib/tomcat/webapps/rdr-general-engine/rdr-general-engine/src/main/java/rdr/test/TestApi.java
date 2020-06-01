/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rdr.cases.CaseSet;
import rdr.cases.CornerstoneCaseSet;
import rdr.gui.StartupFrame;
import rdr.domain.Domain;
import rdr.domain.DomainLoader;
import rdr.gui.MainFrame;
import rdr.rules.Rule;
import rdr.rules.RuleBuilder;
import rdr.rules.RuleSet;
import rdr.workbench.Workbench;

/**
 *
 * @author Hyunsuk (David) Chung (DavidChung89@gmail.com)
 */
public class TestApi {
        
	private HttpURLConnection getHttpConnection(String url, String type){
        URL uri = null;
        HttpURLConnection con = null;
        try{
            uri = new URL(url);
            con = (HttpURLConnection) uri.openConnection();
            con.setRequestMethod(type); //type: POST, PUT, DELETE, GET
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setConnectTimeout(60000); //60 secs
            con.setReadTimeout(60000); //60 secs
            con.setRequestProperty("Accept-Encoding", "UTF-8");
            con.setRequestProperty("Content-Type", "UTF-8");
        }catch(Exception e){
            System.err.println("connection i/o failed" );
        }


        return con;
    }   
	
	private JSONObject RESTRequest(String url, String type, String reqbody) throws Exception{
        HttpURLConnection con = null;
        JSONObject aJSONObject = null;
        try {
            con = getHttpConnection( url , type);
        
            //you can add any request body here if you want to post
             if( reqbody != null){  
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
            
            //aJSONObject = new JSONObject(responseStrBuilder.toString());
            aJSONObject = new JSONObject();
            aJSONObject.put("data", responseStrBuilder.toString());
            aJSONObject.put("status", con.getResponseCode());
            streamReader.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        
        return aJSONObject;
    }


    public static void main(String[] args) throws Exception {
        System.out.println("TestApi Started");
        
        String file = System.getProperty("user.dir") + "/domain/cases/seegene.arff";
        BufferedReader reader = new BufferedReader(new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");
        System.out.println("ls : [" + ls.toCharArray() + "]");
        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            System.out.println( stringBuilder.toString() );
        } finally {
            reader.close();
        }

        
        
        
        
//        String url = "http://localhost:8080/bloodTest/Test";
//        String type = "GET";
//        String reqbody = "this is my body";
//        
//        TestApi tApi = new TestApi();
//        JSONObject jsonObj = tApi.RESTRequest(url, type, null);
//        
//        System.out.println("json : " + jsonObj.toString());
        
        
    }
}
