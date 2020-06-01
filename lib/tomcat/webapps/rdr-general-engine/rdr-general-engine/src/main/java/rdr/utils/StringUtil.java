package rdr.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class StringUtil 
{
	/** char 를  문자열로 변환 */
	public static String charToString(char a) throws Exception
	{
		return Character.toString(a);
	}
	
	public static String parseString(String source, String replace) 
	{
		return source == null || source == "" ? replace : source;
	}
	
	// for int/Integer ------------------------------------------------
	public static int parseInt(String source) 
	{
		return StringUtil.parseInt(source, 0);
	}

	public static int parseInt(String source, int replace) 
	{
		try {
			return Integer.parseInt(source.replaceAll(",", ""), 10); //10진수변환
		} catch (Exception e) {
			return replace;
		}
	}
	
	public static Integer parseInteger(String source) 
	{
		return  StringUtil.parseInteger(source, null);
	}

	public static Integer parseInteger(String source, Integer replace) 
	{
		try {
			return new Integer(source.replaceAll(",",  ""));
		} catch (Exception e) {
			return replace;
		}
	}
	
	// for long/Long ------------------------------------------------
	public static long parseLong(String source) 
	{
		return StringUtil.parseLong(source, 0);
	}

	public static long parseLong(String source, long replace) 
	{
		try {
			return Long.parseLong(source.replaceAll(",", ""), 10);
		} catch (Exception e) {
			return replace;
		}
	}
	
	public static Long parseLongClass(String source) 
	{
		return  StringUtil.parseLongClass(source, null);
	}
	
	public static Long parseLongClass(String source, Long replace) 
	{
		try {
			return new Long(source.replaceAll(",",  ""));
		} catch (Exception e) {
			return replace;
		}
	}
	
	// for double/Double ------------------------------------------------
	public static double parseDouble(String source) 
	{
		return StringUtil.parseDouble(source, (double)0);
	}
	
	public static double parseDouble(String source, double replace) 
	{
		try {
			return Double.parseDouble(source.replaceAll(",", ""));
		} catch (Exception e) {
			return replace;
		}
	}
	
	public static Double parseDoubleClass(String source) 
	{
		return  StringUtil.parseDoubleClass(source, null);
	}
	
	public static Double parseDoubleClass(String source, Double replace) 
	{
		try {
			return new Double(source.replaceAll(",",  ""));
		} catch (Exception e) {
			return replace;
		}
	}
	
	public static boolean isNumeric(String source) 
	{
		try {
			Double.parseDouble(source);
			return true;
		} catch (Exception e) {
			return false;
		} 
	}

	public static String randomStr(int length) 
	{
		StringBuffer rndStr = new StringBuffer();
		char[] charset = "Aa1Bb2Cc3Dd4Ee5Ff6Gg7Hh8Ii9Jj1Kk2Ll3Mm4Nn5Oo6Pp7Qq8Rr9Ss1Tt2Uu3Vv4Ww5Xx6Yy7Zz"
				.toCharArray();
		int charLen = charset.length;
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			rndStr.append(charset[random.nextInt(charset.length) % charLen]);
		}
		return rndStr.toString();
	}

	public static String toString(String[] source, String delim) 
	{
		StringBuffer ret = new StringBuffer();
		if (source != null)
			for (int i = 0; i < source.length; i++) {
				if (i > 0)
					ret.append(delim);
				ret.append(source[i]);
			}
		return ret.toString();
	}
	
	public static String[] toArray(String source, String delim)
	{
		//뒤에 null string을 포함하기 위해 -1 필요, 예) "a,b,c,,,,"
		return source.split(delim, -1);
	}

	public static List<String> toList(String[] source) 
	{
		List<String> retList = new ArrayList<String>();
		if (source != null)
			for (int i = 0; i < source.length; i++)
				retList.add(source[i]);
		return retList;
	}

	public static List<Integer> toIntList(String[] source) 
	{
		List<Integer> retList = new ArrayList<Integer>();
		if (source != null)
			for (int i = 0; i < source.length; i++)
				retList.add(StringUtil.parseInt(source[i]));
		return retList;
	}

	public static List<Long> toLongList(String[] source) 
	{
		List<Long> retList = new ArrayList<Long>();
		if (source != null)
			for (int i = 0; i < source.length; i++)
				retList.add(StringUtil.parseLong(source[i]));
		return retList;
	}

	public static String substringByte(String str, int i, String trail) 
	{
		if (str == null)
			return "";
		String tmp = str;
		int slen = 0, blen = 0;
		char c;
		if (tmp.getBytes().length > i) {
			while (blen + 1 < i) {
				c = tmp.charAt(slen);
				blen++;
				slen++;
				if (c > 127)
					blen++; // 2-byte character..
			}
			tmp = tmp.substring(0, slen) + trail;
		}
		return tmp;
	}

	public static String fileSizeFormat(long fileSize) 
	{
		double size = fileSize;
		NumberFormat formatter = new DecimalFormat("#.#");
		if (size < 1024)
			return String.format("%sbyte", formatter.format(size));
		size = size / 1024;
		if (size < 1024)
			return String.format("%sKB", formatter.format(size));
		size = size / 1024;
		if (size < 1024)
			return String.format("%sMB", formatter.format(size));
		return "";
	}

	/**
	 * <pre>
	 *  입력받은 StringA[n] 값에서 StringB[n] 값을 제거하여 String[n]를 반환한다.
	 *  &#64;param strArray1 제거될 StringA 배열
	 *  &#64;param strArray2 제거할 StringB 배열
	 *  &#64;return  String[] 중복제거된 String 배열
	 *  <b>Example)</b>
	 *      String[] strArr = StringUtil.removeStr(String[] StringA, String[] StringB);
	 * </pre>
	 */
	public static String[] removeArray(String[] strArray1, String[] strArray2) 
	{
		if (strArray1 == null || strArray1.length == 0)
			return (String[]) null;
		if (strArray2 == null || strArray2.length == 0)
			return strArray1;
		String r_str = "";
		boolean flag = true;
		for (int i = 0; i < strArray1.length; i++) {
			for (int j = 0; j < strArray2.length; j++) {
				if (strArray1[i].equals(strArray2[j])) {
					flag = false;
				}
			}
			if (flag) {
				if (!"".equals(r_str))
					r_str += ",";
				r_str += strArray1[i];
			}
			flag = true;
		}
		return "".equals(r_str) ? (String[]) null : r_str.split(",", -1);
	}

	public static boolean compareMonth(Object source, Object compare, int month) throws ParseException 
	{
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal.setTime(new SimpleDateFormat("yyyyMMdd").parse(source.toString()));;
		cal.add(cal.MONTH, month);
		cal2.setTime(new SimpleDateFormat("yyyyMMdd").parse(compare.toString()));
		// if(cal.getTime().equals(cal2.getTime())) {
		// return true;
		// }
		return cal.compareTo(cal2) >= 0;
	}

	public static String sqlError(String msg, int len) 
	{
		String error_msg = msg.substring(len, msg.length());
		int error_len = error_msg.indexOf(":");
		String error_msg2 = error_msg.substring(error_len + 1, error_msg.length());
		String result = "[{'ERROR':'','" + error_msg2.trim() + "':''}]";

		return result;
	}

	/** array to string */
	public static String arrayIntToString(ArrayList<Integer> pList, String seperator)
	{
		String str = "";

		for (int i = 0; i < pList.size(); i++)
		{
			if (i > 0) str += seperator;
			str += String.valueOf(pList.get(i));
		}
		return str;
	}

	/** array to string */
	public static String arrayStringToString(ArrayList<String> pList, String seperator)
	{
		String str = "";

		for (int i = 0; i < pList.size(); i++)
		{
			if (i > 0) str += seperator;
			str += pList.get(i);
		}
		return str;
	}
}
