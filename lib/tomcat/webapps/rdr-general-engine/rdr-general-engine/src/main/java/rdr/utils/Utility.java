package rdr.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import rdr.utils.RDRConstants;
import rdr.utils.DateUtil;
import rdr.utils.Utility;

import rdr.logger.Logger;
import rdr.rules.Rule;
import rdr.rules.RuleSet;

public class Utility 
{
	/** double 값 동일여부 비교
	 *  lv < rv  : -1
	 *  lv == rv : 0
	 *  lv > rv  : 1
	 * */
	public static int compareDouble(double lv, double rv)
	{
		lv = Utility.round(lv, 10);
		rv = Utility.round(rv, 10);
		if (lv < rv) return -1;
		else if (lv > rv) return 1;
		else return 0;
	}

	/** (min <= value <= max) 이면 true */
	public static boolean isInRange(double value, double min, double max)
	{
		value = Utility.round(value, 10);

		if (min > RDRConstants.DBL_MIN)
		{
			min = Utility.round(min,  10);
			if (value < min) return false;
		}
		
		if (max < RDRConstants.DBL_MAX)
		{
			max = Utility.round(max,  10);
			if (value > max) return false;
		}
		
		return true;
	}

	/** 반올림
	 *  v = 123.4567;
	 *  dec = 0  : 123.0
	 *  dec = 1  : 123.5
	 *  dec = -1 : 120.0
	 * */
	public static double round(double v, int dec)
	{
		double n = Math.pow((double)10, (double)dec);
		return Math.round(v * n) / n;
	}

	/** hamming distance */
	public static int getHammingDistance( String a, String b)
	{
		int distance = Math.abs(a.length() - b.length());
		int count = ( a.length() < b.length() ? a.length() : b.length());
		for ( int i = 0 ; i < count ; i++ )
		{
			char ac = a.charAt(i);
			char bc = b.charAt(i);
			if ( ac != bc ) distance++;
		}
		return distance;
	}
	
	/** remove file */
	public static void removeFile( String fn ) throws Exception
	{
		java.io.File file = new java.io.File(fn);
		if ( file.exists() )
		{
			file.delete();
		}
	}
	
	public static String readFile(String file) throws IOException 
	{
	    BufferedReader reader = new BufferedReader(new FileReader (file));
	    String line = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");

	    try 
	    {
	        while((line = reader.readLine()) != null)
	        {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }

	        return stringBuilder.toString();
	    } 
	    finally 
	    {
	        reader.close();
	    }
	}
	
	/** read file 
	 *  encoding : StandardCharsets.UTF_8 ...
	 *  */
	public static String readEncodingFile(String path, Charset encoding) throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	/** read file 
	 *  encoding : StandardCharsets.UTF_8 ...
	 *  */
	public static String readEncodingFile(String path) throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}
	
	/** ResultSet Object를 문자열 배열로 변환 처리 */
	public static String [] convertResultSetToArray(ResultSet rs) throws Exception
	{
	    int columnCnt = rs.getMetaData().getColumnCount();
		String buffer[] = new String[columnCnt];

		for ( int i = 0 ; i < columnCnt ; i++ )
		{
			Object obj = rs.getObject(i+1);

			if (obj == null || obj.toString().equals(""))
			{
				buffer[i] = RDRConstants.EmptyString;
			}
			else if (rs.getMetaData().getColumnType(i+1) == Types.DATE)
			{
				buffer[i] = DateUtil.convert(rs.getDate(i+1));
			}
			else if (rs.getMetaData().getColumnType(i+1) == Types.TIMESTAMP)
			{
				buffer[i] = DateUtil.convert(rs.getTimestamp(i+1));
			}
			else
			{
				buffer[i] = obj.toString();
			}
		}
		return buffer;
	}
	
	
	/** ResultSet Object를 Map으로 변환, key:컬럼ID, value:항목값 */
	public static LinkedHashMap<String, String> convertResultSetToMap(ResultSet rs) throws Exception
	{
	    int columnCnt = rs.getMetaData().getColumnCount();
	    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

	    String value;
		for ( int i = 0 ; i < columnCnt ; i++ )
		{
			Object obj = rs.getObject(i+1);

			if (obj == null || obj.toString().equals(""))
			{
				value = RDRConstants.EmptyString;
			}
			else if (rs.getMetaData().getColumnType(i+1) == Types.DATE)
			{
				value = DateUtil.convert(rs.getDate(i+1));
			}
			else if (rs.getMetaData().getColumnType(i+1) == Types.TIMESTAMP)
			{
				value = DateUtil.convert(rs.getTimestamp(i+1));
			}
			else
			{
				value = obj.toString();
			}

			map.put(rs.getMetaData().getColumnName(i+1), value);
		}

		return map;
	}
	
	public static boolean isFileExist(String fn)
	{
		File file = null;
		try
		{
			file = new File(fn);
			return file.exists();
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public static void copyFile(String srcFn, String destFn) throws IOException {
    	
    	File source = new File(srcFn);
    	File dest = new File(destFn);
    	
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
        	Logger.error("File Copy Failed, src[" + srcFn + "] dest[" + destFn + "]", e);
        	Logger.error( e.getClass().getName() + ": " + e.getMessage() );
        } finally {
            is.close();
            os.close();
        }
    }
	
	public static JSONObject parseJSONObject(HttpServletRequest request)
	{
		StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = null;

        try
        {
            bufferedReader =  request.getReader() ; 
            if ( bufferedReader == null)
            {
            	Logger.error("request body read faied");
            	return null;
            }
            else
            {
	            char[] charBuffer = new char[128];
	            int bytesRead;
	            while ( (bytesRead = bufferedReader.read(charBuffer)) != -1 ) {
	                sb.append(charBuffer, 0, bytesRead);
	            }
            }
        } catch (IOException ex) {
        	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
        	return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
                	return null;
                }
            }
        }
        
        System.out.println("request body : " + sb.toString());
        
        JSONParser jsonParser = new JSONParser();
        JSONObject recvJSONObj = new JSONObject();
        
        try 
        {
        	recvJSONObj = (JSONObject)jsonParser.parse(sb.toString());
        }
        catch (Exception ex ) 
        {
        	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
        	return null;
        }
        
        return recvJSONObj;
	}
	
	public static JSONArray parseJSONArray(HttpServletRequest request)
	{
		StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = null;

        try
        {
            bufferedReader =  request.getReader() ; 
            if ( bufferedReader == null)
            {
            	Logger.error("request body read faied");
            	return null;
            }
            else
            {
	            char[] charBuffer = new char[128];
	            int bytesRead;
	            while ( (bytesRead = bufferedReader.read(charBuffer)) != -1 ) {
	                sb.append(charBuffer, 0, bytesRead);
	            }
            }
        } catch (IOException ex) {
        	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
        	return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
                	return null;
                }
            }
        }
        
        System.out.println("request body : " + sb.toString());
        
        JSONParser jsonParser = new JSONParser();
        JSONArray recvJSONArray = new JSONArray();
        
        try 
        {
        	recvJSONArray = (JSONArray)jsonParser.parse(sb.toString());
        }
        catch (Exception ex ) 
        {
        	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
        	return null;
        }
        
        return recvJSONArray;
	}
	
	public static String parseString(HttpServletRequest request)
	{
		StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = null;

        try
        {
            bufferedReader =  request.getReader() ; 
            if ( bufferedReader == null)
            {
            	Logger.error("request body read faied");
            	return "";
            }
            else
            {
	            char[] charBuffer = new char[128];
	            int bytesRead;
	            while ( (bytesRead = bufferedReader.read(charBuffer)) != -1 ) {
	                sb.append(charBuffer, 0, bytesRead);
	            }
            }
        } catch (IOException ex) {
        	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
        	return "";
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                	Logger.error(ex.getClass().getName() + " : " + ex.getMessage(), ex);
                	return "";
                }
            }
        }
        
        System.out.println("request body : " + sb.toString());
        return sb.toString();
	}

	public static ArrayList<String> convertCSVStringToArray(String s, String delimiter) 
	{
		/** split함수 결과 예시
		 * ,null  : [] [null]
		 *  ,,null : [] [] [null]
		 *  null,  : [null][]
		 *  null,, : [null][][]
		 *         : []
		 */
		ArrayList<String> list = new ArrayList<String>();
		
		if (s.isEmpty()) return list;
		
	    String[] tokens = s.split(delimiter, -1); //-1을 set해야 뒤의 empty string을 읽음
	    for (int i = 0; i < tokens.length; i++)
	    {
	    	String token = tokens[i];
	    	list.add(token);
	    }
	    return list;
		
	    /** StringTokenizer는 empty string은 제외함
	     * ,null  : [null]
		 *  ,,null : [null]
		 *  null,  : [null]
		 *  na,NA,null,,,NULL : [na] [NA] [null] [NULL]
		 */
//	    ArrayList<String> list = new ArrayList<String>();
//	    StringTokenizer st = new StringTokenizer(s, delimiter);
//	    
//	    while (st.hasMoreTokens())
//	    {
//	      String token = st.nextToken();
//	      list.add(token);
//	    }
//	    return (String[]) list.toArray(new String[list.size()]);
		
	}
	  
	public static String convertArrayToCSVString(ArrayList<String> s, String delimiter) 
	{
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < s.size(); i++) {
	      if (i > 0) {
	        sb.append(delimiter);
	      }
	      sb.append(s.get(i));
	    }
	    return sb.toString();
	}
	
	public static boolean isEmptyInferenceResult(Rule inferenceResult)
	{
		//inference result 가 null 이거나 root rule 인 경우 
		return (inferenceResult == null || inferenceResult.getRuleId() == Rule.ROOT_RULE_ID);
	}
	
	public static boolean isEmptyInferenceResult(RuleSet inferenceResult)
	{
		//inference result에 root rule을 포함하면 다른 Rule이 존재하지 않음
		return (inferenceResult == null || inferenceResult.isRuleExist(Rule.ROOT_RULE_ID));
	}
	
	public static <T> List<T> union(List<T> list1, List<T> list2) 
	{
        Set<T> set = new HashSet<T>();
 
        set.addAll(list1);
        set.addAll(list2);
 
        return new ArrayList<T>(set);
    }
 
    public static <T> List<T> intersection(List<T> list1, List<T> list2) 
    {
        List<T> list = new ArrayList<T>();
 
        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }
 
        return list;
    }
    
    public static <T> List<T> subtraction(List<T> list1, List<T> list2) 
    {
        List<T> list = new ArrayList<T>();
 
        for (T t : list1) {
            if (list2.contains(t) == false) {
                list.add(t);
            }
        }
 
        return list;
    }
	
}
