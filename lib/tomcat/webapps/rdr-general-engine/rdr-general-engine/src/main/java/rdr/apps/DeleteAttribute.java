package rdr.apps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import rdr.api.RDRInterface;
import rdr.api.RDRService;
import rdr.apimsg.RDRResponse;
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

public class DeleteAttribute 
{
	private String rootPath = "c:\\Dev\\eclipse_ws\\rdr-general-web\\src\\main\\webapp\\WEB-INF\\cfg\\";
	private String domainName = "pbs";
	private String serviceName = "rdr-pbs";
	//private String arffFile;
	
	public static void main(String[] args) 
	{
		DeleteAttribute tApp = new DeleteAttribute();
		
		boolean flag = tApp.deleteAttr();
		
		if (flag)
			Logger.info("finished successfully");
		else 
			Logger.error("deleting failed");
	}
	
	public boolean deleteAttr()
	{
		boolean flag = true;
		flag = RDRService.init(serviceName, rootPath);
		
		if (!flag)
		{
			Logger.error("RDRService init failed");
			return false;
		}
		
		flag = true;
		//RDRDBManager.getInstance().setAutoCommit(false);
        
		ArrayList<String> attrs = new ArrayList<String>();
		attrs.add("02_Size_NUM");
		attrs.add("03_Chromicity_NUM");
		attrs.add("04_Anisocytosis_NUM");
		attrs.add("05_Poikilocytosis_NUM");
		attrs.add("06_Elliptocyte_NUM");
		attrs.add("07_burr cell_NUM");
		attrs.add("08_target cell_NUM");
		attrs.add("09_spherocyte_NUM");
		attrs.add("10_schistocyte_NUM");
		attrs.add("11_Dimorphism_NUM");
		attrs.add("12_Rouleux_NUM");
		attrs.add("13_Polychromasia_NUM");
		attrs.add("14_Basophilic stippling_NUM");
		attrs.add("15_Howell-Jolly body_NUM");
		attrs.add("16_Tear drop cell_NUM");
		//attrs.add("17_Nucleated RBC_NUM");
		attrs.add("21_Number_NUM");
		attrs.add("22_Maturation_NUM");
		attrs.add("23_Neutro-toxic changes_NUM");
		attrs.add("24_Neutro-Dohle body_NUM");
		attrs.add("25_Neutro-segmentation_NUM");
		attrs.add("29_Number_NUM");
		attrs.add("30_Size_NUM");
		attrs.add("31_Clumping_NUM");
		attrs.add("18_Hemoglobin_NUM");
		attrs.add("19_RBC Agglutination_NUM");
		attrs.add("41_Stomatocyte_NUM");
		attrs.add("42_Acanthocyte_NUM");
		attrs.add("43_Malaria_NUM");
		attrs.add("44_RBC others_NUM");
		attrs.add("51_Neutrophil_NUM");
		attrs.add("53_Lymphocyte_NUM");
		attrs.add("55_Monocyte_NUM");
		attrs.add("56_Eosinophil_NUM");
		attrs.add("57_Basophil_NUM");
		//attrs.add("61_Atypical lymphocyte_NUM");
		//attrs.add("62_Abnormal lymphocyte_NUM");
		//attrs.add("63_Blast_NUM");
		//attrs.add("64_Abnormal cells_NUM");
		attrs.add("65_WBC Others_NUM");
		attrs.add("71_PLT Others_NUM");
		attrs.add("91_Cell degeneration_NUM");
		attrs.add("92_Partial clot_NUM");

		int cnt = 0;
		RDRResponse tResponse = null;
		for (int i = 0; i < attrs.size(); i++)
		{
			tResponse = RDRService.deleteAttribute(this.domainName, attrs.get(i), "scio");
			
			if (tResponse.getStatus())
			{
				Logger.error("deleted : " + attrs.get(i) );
				cnt++;
			}
			else
			{
				Logger.error("delete failed : " + attrs.get(i) + " : " + tResponse.getMessage());
				flag = false;
				break;
			}
		}
		
		//deleteAttribute 함수내에서 commit 함
		//RDRDBManager.getInstance().doCommit(flag);
        //RDRDBManager.getInstance().setAutoCommit(true);
		
		Logger.info("[" + cnt + "/" + attrs.size() + "] deleted");
		
		return flag;
	}
	
}
