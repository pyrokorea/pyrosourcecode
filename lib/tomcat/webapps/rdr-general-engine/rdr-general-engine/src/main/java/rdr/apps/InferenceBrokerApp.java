package rdr.apps;

import rdr.api.RDRBrokerParameters;
import rdr.api.RDRService;
import rdr.apimsg.RDRResponse;
import rdr.logger.Logger;

public class InferenceBrokerApp 
{
	public static void main(String[] args) 
	{
		InferenceBrokerApp tApp = new InferenceBrokerApp();
		boolean flag = tApp.run();
		
		if (flag)
			Logger.info("finished successfully");
		else 
			Logger.error("inference application failed");
	}
	
	public boolean run()
	{
		String rootPath = "c:\\Dev\\eclipse_ws\\rdr-general-web\\src\\main\\webapp\\WEB-INF\\cfg\\";
		String domainName = "special_immunology";
		String serviceName = "rdr-ep";
		String userId = "ucciri";
		String lmbCode = "0000";
		int rctDate = 20190430;
		int rctNo = 1511;
		String testCode = "00309";
		String specimenCode = "A01";
		
		RDRBrokerParameters params = new RDRBrokerParameters();
		params.add(lmbCode);
		params.add(rctDate);
		params.add(rctNo);
		params.add(testCode);
		params.add(specimenCode);
		
		RDRService.init(serviceName, rootPath);
		RDRResponse tResponse = RDRService.getInferenceByBroker(domainName, params, userId);
		tResponse.printLog();
		
		return true;
	}

}
