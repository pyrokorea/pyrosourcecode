/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdr.test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;

import com.smf.ep.EPReportRuleTree;

import rdr.api.RDRService;
import rdr.apimsg.KARequest;
import rdr.apimsg.KAResponse;
import rdr.learner.Learner;
import rdr.logger.Logger;
import rdr.utils.RDRConfig;

public class TestFunctionAPI 
{
	private String rootPath = "c:\\Dev\\eclipse_ws\\rdr-general-web\\src\\main\\webapp\\WEB-INF\\cfg\\";
	private String domainName = "special_immunology";
	private String serviceName = "rdr-ep";
	
	public static void main(String[] args) 
	{
		TestFunctionAPI t = new TestFunctionAPI();
		//t.readArff();
		//t.validation();
		t.reportRuleTree();
		
        System.out.println("TestFunctionAPI Done !!!");
    }
	
	public void reportRuleTree()
	{
		RDRService.init(serviceName, rootPath);
		
		EPReportRuleTree tReport = new EPReportRuleTree();
		tReport.addFiltering("TEST_CODE", "00323");
		
		String jsonStr = tReport.getReportJSON();
		System.out.println(jsonStr);
	}
	
	public void validation()
	{
		RDRService.init(serviceName, rootPath);
		KARequest req = new KARequest();
		
		HashMap<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("LMB_CODE", "0000");
		valueMap.put("TEST_CODE", "00323");
		valueMap.put("HOSPITAL_CODE", "25169");
		valueMap.put("TV_FLAG", "N");
		valueMap.put("ALBUMIN", "NM");
		valueMap.put("Total Protein", "NM");
		
		req.setCase(valueMap);
		//req.setWrongConclusionId(id);
		req.setKAMode(Learner.KA_EXCEPTION_MODE);
		req.setSelectedConclusion("SPEP002");
		
		req.addCondition("TV_FLAG", "==", "N");
		req.addCondition("ALBUMIN", "==", "NM");
		req.addCondition("Total Protein", "==", "NM");
		
		KAResponse kaResponse = RDRService.getValidationCasesBatch(domainName, req, "ucciri");
		System.out.println(kaResponse.getJSON().toString());
	}
	
	public void readArff()
	{
		RDRService.init(serviceName, rootPath);
		
		String arffFn = RDRConfig.getArffFile();
		
		try 
		{
			String arffStr = new String(Files.readAllBytes(Paths.get(arffFn)), 
					              StandardCharsets.UTF_8);
			System.out.println("File : " + arffFn);
			System.out.println("ARFFFile Start----------------------------------");
			System.out.println(arffStr);
			System.out.println("ARFFFile End------------------------------------");
//			
//			JSONObject rJsonObj 
//				= RDRService.getInferenceByARFF("stomach", arffStr, 0, "ucciri");
//			System.out.println(rJsonObj.toString());
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}
