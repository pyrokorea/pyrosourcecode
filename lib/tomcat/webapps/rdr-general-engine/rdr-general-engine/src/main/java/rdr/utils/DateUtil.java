package rdr.utils;

import java.util.Calendar;
import rdr.utils.RDRConstants;

public class DateUtil 
{
	/** 생성자 */
	private DateUtil() {}
	
	/**
	 * 현재 시각을 문자열로 제공(YYYY/MM/DD HH:MM:SS)
	 */
	public static String now()
	{
		Calendar cal = Calendar.getInstance();
		return RDRConstants.DATETIME_FORMAT1.format(cal.getTime());
	}
	
	/**
	 * 문자열을 Date 형식으로 변환(YYYY/MM/DD HH:MM:SS)
	 */
	public static java.util.Date convert(String a)
	{
		try { return RDRConstants.DATETIME_FORMAT1.parse(a); }
		catch (Exception ex) { return null; }
	}
	
	/**
	 * Date형식을 문자열로 변환(YYYY/MM/DD HH:MM:SS)
	 */
	public static String convert(java.util.Date a)
	{
		return convert(a, RDRConstants.DATETIME_FORMAT1);
	}
	
	/**
	 * Date형식 변수를 문자열로 변환
	 * @param a 변환할 Date
	 * @param sf Data 포맷 지정
	 */
	public static String convert(java.util.Date a, java.text.SimpleDateFormat sf )
	{
		if ( a == null ) return RDRConstants.EmptyString;
	  
		try { return sf.format(a); }
		catch (Exception ex) { return RDRConstants.EmptyString; }
	}
		
	/**
	 * DateTime 형식을 문자열로 변환
	 */
	public static String convert(java.sql.Timestamp a) throws Exception
	{
		return convert(a, RDRConstants.DATETIME_FORMAT1);
	}
	
	/**
	 * DateTime 형식 변수를 문자열로 변환
	 * @param a 변환할 DateTime
	 * @param sf Data 포맷 지정
	 */
	public static String convert(java.sql.Timestamp a, java.text.SimpleDateFormat sf )
	{
		if ( a == null ) return RDRConstants.EmptyString;

		try { return sf.format(a); }
		catch (Exception ex) { return RDRConstants.EmptyString; }
	}
	
	/**
	 * 문자열을 Date 형식으로 변환
	 * @param a 변환할 문자열
	 * @param sf Data 포맷 지정
	 */
	public static java.util.Date convert(String a, java.text.SimpleDateFormat sf )
	{
		if ( a == null ) return null;
		if ( a.isEmpty() ) return null;
		
		try { return sf.parse(a); }
		catch (Exception ex) { return null; }
	}
	
	/**
	 * java util.Date to sql.Date
	 */
	public static java.sql.Date convertToSqlDate(java.util.Date a) throws Exception
	{
		java.sql.Date dt = new java.sql.Date(a.getTime());
		return dt;
	}
	
	/**
	 * java sql.Date to util.Date
	 */
	public static java.util.Date convertToUtilDate(java.sql.Date a) throws Exception
	{
		java.util.Date dt = new java.util.Date(a.getTime());
		return dt;
	}
	
	/** 초단위 시간을 일,시간,분,초로 환산한 String으로 반환
	 *  @param sec 초
	 *  @return 일,시간,분,초를 나타내는 String 예)1일 12시간 3분 18초
	 */
	public static String secondToString( long sec )
	{
		long day = sec / (3600 * 24);
		long hr = (sec % (3600 * 24)) / 3600;
		long mi = (sec % 3600) / 60;
		long ss = (sec % 3600) % 60;
		String str = Integer.toString((int)day) + "d " +
					Integer.toString((int)hr) + "h " +
					Integer.toString((int)mi) + "m " +
					Integer.toString((int)ss) + "s";
		return str;
	}
}


