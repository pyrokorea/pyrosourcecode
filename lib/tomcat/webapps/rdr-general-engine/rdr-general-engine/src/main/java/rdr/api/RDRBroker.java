package rdr.api;

import java.util.ArrayList;

import rdr.cases.Case;
import rdr.cases.CaseStructure;

public abstract class RDRBroker 
{
	public RDRBroker()
	{
		;
	}
	
	public Case getCase(RDRBrokerParameters params, String []msg)
	{
		return null;
	}
	
	public int synchronizeCaseStructure(ArrayList<String> pAttrNames,
            							CaseStructure pCaseStructure)
	{
		return -1;
	}
	
	public ArrayList<String> getFirstLevelAttributes()
	{
		return null;
	}
	
	public ArrayList<String> getConditionAttributes(CaseStructure pCaseStructure)
	{
		return null;
	}
	
	public boolean saveInferenceResult(RDRBrokerParameters params, 
							           String pInferenceResult, 
							           String pUserId)
	{
		return true;
	}
	
}
