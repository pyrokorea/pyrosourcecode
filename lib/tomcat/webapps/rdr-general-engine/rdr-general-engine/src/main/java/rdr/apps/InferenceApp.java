package rdr.apps;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import rdr.api.RDRInterface;
import rdr.api.RDRService;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseStructure;
import rdr.cases.CaseSynchroniser;
import rdr.logger.Logger;
import rdr.rules.Rule;
import rdr.utils.RDRConfig;
import rdr.utils.RDRConstants;
import rdr.utils.Utility;

public class InferenceApp 
{
	private javax.swing.JFileChooser importFileChooser;
	
	private String rootPath = "c:\\Dev\\eclipse_ws\\rdr-general-web\\src\\main\\webapp\\WEB-INF\\cfg\\";
	private String domainName = "special_immunology";
	private String serviceName = "rdr-ep";
	private String arffFile;
	
	public static void main(String[] args) 
	{
		InferenceApp tApp = new InferenceApp();
		boolean flag = tApp.run();
		
		if (flag)
			Logger.info("finished successfully");
		else 
			Logger.error("learning failed");
	}
	
	public boolean run()
	{
		boolean flag = true;
		flag = RDRService.init(serviceName, rootPath);
		
		if (!flag)
		{
			Logger.error("RDRService init failed");
			return false;
		}
		
		//choose arff file --------------------------------------------------
		javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
		FileNameExtensionFilter filter 
			= new FileNameExtensionFilter("arff", new String[] {"arff","ARFF"});
		chooser.setCurrentDirectory(new File(RDRConfig.getArffPath()));
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		int ret = chooser.showOpenDialog(null);  //열기창 정의

		if (ret != JFileChooser.APPROVE_OPTION) 
		{
			JOptionPane.showMessageDialog(null, "파일을 선택하지않았습니다.",
			                              "경고", JOptionPane.WARNING_MESSAGE);
			return false;
		}
				
		File selectedFile = chooser.getSelectedFile();
		arffFile = selectedFile.getPath();
		Logger.info("ARFF : " + arffFile);
		
		try 
		{
			String fn = RDRConfig.getArffFile();
			Utility.copyFile(arffFile, fn);
			
			if (this.checkCaseStructure() == false)
			{
				Logger.error("ARFF, DB CaseStructure is not same");
				return false;
			}
		
			CaseStructure aCaseStructure = CaseLoader.getArffCaseStructure();
			
			CaseLoader.caseImport(aCaseStructure, 0, arffFile);
			Set cases = Main.allCaseSet.getBase().entrySet();
	        Iterator caseIterator = cases.iterator();
	        int index = 0;
	        int matchCnt = 0;
	        while (caseIterator.hasNext())
	        {
	        	Map.Entry me = (Map.Entry)caseIterator.next();
		    	Case emptyCase = (Case) me.getValue();
		    	
		    	index++;
   	
		    	Case aCase = CaseLoader.caseLoad(emptyCase.getCaseId(), arffFile);
		    	String conclusionStr = aCase.getValue(RDRConstants.RDRClassAttributeName).toString();
		    	
		    	aCase.removeKey(RDRConstants.RDRClassAttributeName);
		    	
		    	Rule inferenceResult = (Rule)RDRInterface.getInstance().getInferenceResult(aCase);
		    	
		    	if (inferenceResult == null)
		    	{
		    		Logger.warn("# " + index + " Case : (diff) null, (" + conclusionStr + ")");
		    	}
		    	else
		    	{
		    		if (inferenceResult.getConclusion().getConclusionName().equals(conclusionStr))
		    		{
		    			Logger.info("# " + index + " Case : (same) " + inferenceResult);
		    			matchCnt++;
		    		}
		    		else
		    		{
		    			Logger.info("# " + index + " Case : (diff) " + inferenceResult + " (" + conclusionStr + ")");
		    		}
		    	}
	        }
	        
	        Logger.info("total count   : " + index);
	        Logger.info("matched count : " + matchCnt);
	        Logger.info("diff count    : " + (index - matchCnt));
		}
		catch (Exception ex)
		{
			Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
			return false;
		}
		    
		return true;
	}
	
	public boolean checkCaseStructure()
	{
		try
		{
			CaseStructure arffCaseStructure = CaseLoader.getArffCaseStructure();
			CaseStructure dbCaseStructure = CaseLoader.loadCaseStructureFromDB();
	
			CaseSynchroniser tSync = new CaseSynchroniser();
			int rtn = tSync.compare(arffCaseStructure, dbCaseStructure);
			
			return (rtn == 0);
		}
		catch (Exception ex)
		{
			Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
			return false;
		}
	}
	
}
