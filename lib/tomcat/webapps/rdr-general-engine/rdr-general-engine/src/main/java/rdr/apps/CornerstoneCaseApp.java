package rdr.apps;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import rdr.api.RDRInterface;
import rdr.api.RDRService;
import rdr.cases.Case;
import rdr.cases.CaseLoader;
import rdr.cases.CaseSet;
import rdr.cases.CaseStructure;
import rdr.cases.CaseSynchroniser;
import rdr.cases.CornerstoneCase;
import rdr.db.RDRDBManager;
import rdr.logger.Logger;
import rdr.utils.RDRConfig;
import rdr.utils.RDRConstants;
import rdr.utils.Utility;

public class CornerstoneCaseApp 
{
	private javax.swing.JFileChooser importFileChooser;
	
	private String rootPath = "c:\\Dev\\eclipse_ws\\rdr-general-web\\src\\main\\webapp\\WEB-INF\\cfg\\";
	private String domainName = "pbs";
	private String serviceName = "rdr-pbs";
	private String arffFile;
	
	public static void main(String[] args) 
	{
		CornerstoneCaseApp tApp = new CornerstoneCaseApp();
		
		boolean flag = tApp.addCornerstoneCases();
		
		if (flag)
			Logger.info("finished successfully");
		else 
			Logger.error("learning failed");
	}
	
	public boolean addCornerstoneCases()
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
		chooser.setCurrentDirectory(new File(RDRConfig.getDomainPath()));
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
		
		String fn = RDRConfig.getArffFile();
		try
		{
			Utility.copyFile(arffFile, fn);
		}
		catch (IOException ex)
		{
			Logger.error("aff file copy failed", ex);
			return false;
		}
		
		if (this.checkArffCaseStructure() == false)
		{
			Logger.error("ARFF and DB CaseStructure is not same");
			return false;
		}
		
		flag = true;
		RDRDBManager.getInstance().setAutoCommit(false);
        
		try
		{
			CaseStructure caseStructure = CaseLoader.loadCaseStructureFromDB();
			CaseLoader.caseImport(caseStructure, RDRConstants.INT_MAX, fn);
			CaseSet caseSet = new CaseSet(Main.allCaseSet);
			
			HashMap<Integer, Case> cases = caseSet.getBase();
			Iterator<Integer> caseIterator = cases.keySet().iterator();
			while (caseIterator.hasNext())
			{
				Integer caseId = caseIterator.next();
				Case aCase = cases.get(caseId);
				
				aCase.setCaseId(RDRInterface.getInstance().getCornerstoneCaseId(aCase));
				Logger.info("case id : " + aCase.getCaseId());
				
				//String conclusionStr = aCase.getValue(RDRConstants.RDRClassAttributeName).toString();
		    	//this.rdrClassMap.put(caseId, conclusionStr);
		    	aCase.removeKey(RDRConstants.RDRClassAttributeName);
		    	
		    	CornerstoneCase aCornerstoneCase = new CornerstoneCase(aCase);
		    	if (RDRDBManager.getInstance().insertCornerstoneValue(aCornerstoneCase))
	            {
	            	Logger.info("cornerstone case value inserted, tb_cornerstone_case");
	            }
	            else
	            {
	            	Logger.error("cornerstone case value insert failed, tb_cornerstone_case");
	            	flag = false;
	            	break;
	            }

			}
		}
		catch (Exception ex)
		{
			Logger.error( ex.getClass().getName() + ": " + ex.getMessage(), ex );
			flag = false;
		}
		
		RDRDBManager.getInstance().doCommit(flag);
        RDRDBManager.getInstance().setAutoCommit(true);
		
		return flag;
	}
	
	public boolean checkArffCaseStructure()
	{
		try
		{
			CaseStructure caseStructure = CaseLoader.getArffCaseStructure();
			CaseStructure dbCaseStructure = CaseLoader.loadCaseStructureFromDB();
	
			CaseSynchroniser tSync = new CaseSynchroniser();
			int rtn = tSync.compare(caseStructure, dbCaseStructure);
			
			return (rtn == 0);
		}
		catch (Exception ex)
		{
			Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
			return false;
		}
	}
}
