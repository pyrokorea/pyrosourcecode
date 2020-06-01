package rdr.api;

import rdr.utils.RDRConfig;

public class RDRBrokerFactory 
{
	public static RDRBroker createBroker()
	{
		if (RDRConfig.getServiceName().equals("rdr-ep"))
			return new com.smf.ep.EPRDRBroker();
		else if (RDRConfig.getServiceName().equals("rdr-pbs"))
			return new com.smf.pbs.PBSRDRBroker();
		else
			return new com.smf.ep.EPRDRBroker(); //default
	}
}
