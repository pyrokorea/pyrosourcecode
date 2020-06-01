package com.smf.ep;

import rdr.workbench.ReportRuleTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rdr.utils.RDRConstants;
import rdr.workbench.InferencePathItem;

public class EPReportRuleTree extends ReportRuleTree
{
	public EPReportRuleTree()
	{
		super();
	}
	
	public void addTestCodeFiltering(String pTestCode)
	{
		addFiltering("TEST_CODE", pTestCode);
	}
	
	
}
