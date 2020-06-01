package rdr.apps;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import rdr.api.RDRService;
import rdr.apimsg.RDRResponse;
import rdr.cases.CaseLoader;
import rdr.logger.Logger;
import rdr.utils.RDRConfig;
import rdr.utils.Utility;

public class DomainSetupApp 
{
	private javax.swing.JFileChooser importFileChooser;
	
	/** CheckThis */
	private String rootPath = "c:\\Dev\\eclipse_ws\\rdr-general-web\\src\\main\\webapp\\WEB-INF\\cfg\\";
	private String domainName = "pbs";
	private String serviceName = "rdr-pbs";
	
	public static void main(String[] args) 
	{
		DomainSetupApp tApp = new DomainSetupApp();
		boolean flag = true;
		
		flag &= tApp.init();
		if (flag == false) return;
		
		//flag &= tApp.setupDomainARFF();
		flag &= tApp.setupDomainJSON();
		if (flag == false) return;
		
		Logger.info("finished successfully");
	}
	
	public boolean init()
	{
		boolean flag = true;
		flag = RDRService.init(serviceName, rootPath);
		
		if (!flag)
		{
			Logger.error("RDRService init failed");
			return false;
		}
		
		return true;
	}
	
	public boolean setupDomainARFF()
	{
		String arffFile = "";
		
		//choose arff file --------------------------------------------------
		javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(); //객체 생성
		FileNameExtensionFilter filter 
			= new FileNameExtensionFilter("arff", new String[] {"arff","ARFF"});
		chooser.setCurrentDirectory(new File(RDRConfig.getArffPath()));
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		int ret = chooser.showOpenDialog(null);  

		if (ret != JFileChooser.APPROVE_OPTION) 
		{
			JOptionPane.showMessageDialog(null, "파일을 선택하지않았습니다.",
			                              "경고", JOptionPane.WARNING_MESSAGE);
			return false;
		}
				
		File selectedFile = chooser.getSelectedFile();
		arffFile = selectedFile.getPath();
		Logger.info("ARFF : " + arffFile);
				
		//confirm message ---------------------------------------------------
		JDialog.setDefaultLookAndFeelDecorated(true);
		int response 
		 	= JOptionPane.showConfirmDialog(null, 
		  			                        "reset for domain[" + domainName + "] continue ?",
		   			                        "Confirm",
		                                     JOptionPane.YES_NO_OPTION, 
		                                     JOptionPane.QUESTION_MESSAGE);
					    	    
		if (response != JOptionPane.YES_OPTION)
		  	return false;
				
		RDRResponse tResponse;

		tResponse = RDRService.deleteDomain(domainName);
		tResponse.printStatusMessage("deleteDomain");
		
		/** CheckThis */
		tResponse = RDRService.addDomain(domainName, "특수면역학 전기영동검사", "SCRDR", "rdr_admin");
		tResponse.printStatusMessage("addDomain");
		
		tResponse = RDRService.setCaseStructureByARFFFileName(domainName, arffFile, "rdr_admin");
		tResponse.printStatusMessage("setCaseStructureByARFF");
		
		return true;
	}
	
	public boolean setupDomainJSON()
	{
		String jsonFile = "";
		
		//choose json file --------------------------------------------------
		javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(); //객체 생성
		FileNameExtensionFilter filter 
			= new FileNameExtensionFilter("json", new String[] {"json","JSON"});
		chooser.setCurrentDirectory(new File(RDRConfig.getArffPath()));
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		int ret = chooser.showOpenDialog(null); 

		if (ret != JFileChooser.APPROVE_OPTION) 
		{
			JOptionPane.showMessageDialog(null, "파일을 선택하지않았습니다.",
			                              "경고", JOptionPane.WARNING_MESSAGE);
			return false;
		}
				
		File selectedFile = chooser.getSelectedFile();
		jsonFile = selectedFile.getPath();
		Logger.info("JSON File : " + jsonFile);
				
		//confirm message ---------------------------------------------------
		JDialog.setDefaultLookAndFeelDecorated(true);
		int response 
		 	= JOptionPane.showConfirmDialog(null, 
		  			                        "reset for domain[" + domainName + "] continue ?",
		   			                        "Confirm",
		                                     JOptionPane.YES_NO_OPTION, 
		                                     JOptionPane.QUESTION_MESSAGE);
					    	    
		if (response != JOptionPane.YES_OPTION)
		  	return false;
				
		RDRResponse tResponse;

		tResponse = RDRService.deleteDomain(domainName);
		tResponse.printStatusMessage("deleteDomain");
		
		/** CheckThis */
		tResponse = RDRService.addDomain(domainName, "말초혈액 도말검사(Peripheral Blood Smear)", "MCRDR", "rdr_admin");
		tResponse.printStatusMessage("addDomain");
		
		String jsonStr = "";
		try
		{
			//jsonStr = Utility.readEncodingFile(jsonFile, StandardCharsets.UTF_8);
			jsonStr = Utility.readFile(jsonFile);
			jsonStr = jsonStr.replace("\uFEFF",  "");
		}
		catch (IOException ex)
		{
			Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
			return false;
		}
		
		tResponse = RDRService.setCaseStructureByJSON(domainName, jsonStr, "rdr_admin");
		tResponse.printStatusMessage("setCaseStructureByJSON");
		
		return true;
	}
}
