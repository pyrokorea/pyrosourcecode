package rdr.utils;

import java.text.SimpleDateFormat;

public class RDRConstants 
{
	/** empty string */
	public static final String EmptyString = "";

	/** null character */
	public static final char NullChar = '\u0000';
	/** empty character */
	public static final char EmptyChar = ' ';

	/** int min */
	public static final int INT_MIN = Integer.MIN_VALUE;
	/** int max */
	public static final int INT_MAX = Integer.MAX_VALUE;
	/** double min */
	public static final double DBL_MIN = -Double.MAX_VALUE;
	/** doube max */
	public static final double DBL_MAX = Double.MAX_VALUE;
	
	/** csv dilimeter */
	public static final String Delimeter = ",";
	
	/** 결론 attirbute name */
	public static final String RDRClassAttributeName = "rdr_class";

	/** 날짜,시간 포맷 상수 1 */
	public static final SimpleDateFormat DATETIME_FORMAT1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	/** 날짜,시간 포맷 상수 2 */
	public static final SimpleDateFormat DATETIME_FORMAT2 = new SimpleDateFormat("yyyyMMddHHmmss");
	/** 날짜,시간 포맷 상수 3 */
	public static final SimpleDateFormat DATETIME_FORMAT3 = new SimpleDateFormat("yyyyMMddHHmm");

	/** 날짜 포맷 상수 1 */
	public static final SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat("yyyy/MM/dd");
	/** 날짜 포맷 상수 2 */
	public static final SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyyMMdd");
	/** 날짜 포맷 상수 3 */
	public static final SimpleDateFormat DATE_FORMAT3 = new SimpleDateFormat("yyyy-MM-dd");
	/** 날짜 포맷 상수 4 */
	public static final SimpleDateFormat DATE_FORMAT5 = new SimpleDateFormat("yyyyMM");

	/** 시간 포맷 상수 4 */
	public static final SimpleDateFormat TIME_FORMAT1 = new SimpleDateFormat("HH:mm:ss");
	/** 시간 포맷 상수 4 */
	public static final SimpleDateFormat TIME_FORMAT2 = new SimpleDateFormat("HHmmss");
	
	public static final String Domain_PBS = "pbs";

}
