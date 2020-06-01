package rdr.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;

import rdr.logger.Logger;

public class StopWatch {

	private static LinkedHashMap<String, Long> laps = new LinkedHashMap<String, Long>();
	
	public static void initialize()
	{
		laps.clear();
		StopWatch.lap("0-initialize");
	}
	
	public static void lap(String msg)
	{
		String keyStr = String.valueOf(laps.size()) + "-" + msg;
		laps.put(keyStr, new Long(System.nanoTime()));
	}
	
	public static void printLog()
	{
		Logger.info("StopWatch (sec)------------------------");
		long tStart = 0;
		long tPrev = 0;
		Iterator<String> keys = laps.keySet().iterator();
		while (keys.hasNext())
		{
			String keyStr = keys.next();
			long value = laps.get(keyStr).longValue();
			if (tPrev == 0)
			{
				tPrev = value;
				tStart = value;
				continue;
			}
			
			Logger.info(" " + keyStr + " : " + 
			            String.format("%.3f", (double)(value - tPrev)/1000000000.0));
		}
		
		Logger.info(" total : " + 
	            String.format("%.3f", (double)(System.nanoTime() - tStart)/1000000000.0));
		
		Logger.info("-------------------------------------");
	}
	
}
