package rdr.apps;

import java.util.LinkedHashMap;

import rdr.api.RDRBrokerParameters;
import rdr.api.RDRService;
import rdr.apimsg.RDRResponse;
import rdr.logger.Logger;

public class ReportRuleApp 
{
	public static void main(String[] args) 
	{
		ReportRuleApp tApp = new ReportRuleApp();
		
		boolean flag = tApp.run();
		
		if (flag)
			Logger.info("finished successfully");
		else 
			Logger.error("report application failed");
	}
	
	public boolean run()
	{
		String rootPath = "c:\\Dev\\eclipse_ws\\rdr-general-web\\src\\main\\webapp\\WEB-INF\\cfg\\";
		String domainName = "special_immunology";
		String serviceName = "rdr-ep";
				
		RDRService.init(serviceName, rootPath);
		
		LinkedHashMap<String, String> filter = new LinkedHashMap<String, String>();
		filter.put("TEST_CODE", "00310");
		StringBuilder sb = new StringBuilder();
		return RDRService.writeRuleTreeReport(domainName, filter, sb, "ucciri");

	}

}
