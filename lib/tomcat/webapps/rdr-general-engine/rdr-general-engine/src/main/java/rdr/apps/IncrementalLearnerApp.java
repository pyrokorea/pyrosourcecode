package rdr.apps;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import rdr.api.RDRService;
import rdr.learner.IncrementalLearner;
import rdr.logger.Logger;
import rdr.utils.RDRConfig;

public class IncrementalLearnerApp 
{
	private javax.swing.JFileChooser importFileChooser;
	
	private String rootPath = "c:\\Dev\\eclipse_ws\\rdr-general-web\\src\\main\\webapp\\WEB-INF\\cfg\\";
	private String domainName = "special_immunology";
	private String serviceName = "rdr-ep";
	private String arffFile;
	
	public static void main(String[] args) 
	{
		IncrementalLearnerApp tApp = new IncrementalLearnerApp();
		boolean flag = tApp.runARFF();
		
		if (flag)
			Logger.info("finished successfully");
		else 
			Logger.error("learning failed");
	}
	
	public boolean runARFF()
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
		
		IncrementalLearner iLearner = new IncrementalLearner(domainName, "sciouser");
		flag &= iLearner.prepareCaseArffFileWithPath(arffFile);
		flag &= iLearner.runSCRDR(domainName, "sciouser");
		
		return flag;
	}
	
}
