/** 
 * FileName AppCommonController.java
 * 
 * Version 1.0
 *
 * Create by yangwr 2014/8/9
 * 
 * Copyright 2000-2001 Bluemobi. All Rights Reserved.
 */
package com.ec.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



/**
 * FileName AppCommonController.java
 * 
 * Version 1.0
 * 
 * Create by yangwr 2014/8/9
 * 
 * APP公共处理控制器
 */
public class DateUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	// 变量声明 //
	private static Calendar calObject;

	public static final String DATE_FORMAT_FULL = "yyyy/MM/dd HH:mm:ss";
	public static final String DATE_FORMAT_SHORT = "yyyy/MM/dd";
	public static final String DATE_FORMAT_SHORT01 = "yyyy-MM-dd";
	public static final String DATE_FORMAT_YEAR = "yyyy";
	public final static String DEFAULT_FORMAT = "yyyyMMdd";
	public static final String DATE_FORMAT_CN = "yyyy年MM月dd日HH时mm分ss秒";
	public static final String DATE_FORMAT_CN_SHORT = "yyyy年MM月dd日";

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH");
	private static SimpleDateFormat sdfymd = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdfymdLine = new SimpleDateFormat(DATE_FORMAT_SHORT01);

	//天
	public static String getymd(long key) {
		return sdfymd.format(key);
	}

	//天
	public static String getTymdLine(long key) {
		try {
			return sdfymdLine.format(key);

		}catch (Exception e){
			logger.error("getTymdhms exception:{}",e);
		}
		return null;
	}

	/**
	 * 年计算年的加法
	 * 
	 * 
	 * @param dteDate
	 *            对象日期
	 * @param lngNumber
	 *            加的年数（负数：过去；正数：未来）
	 * @return Date 计算后日期
	 */
	public static Date getAddYear(Date dteDate, long lngNumber) {

		// 变量声明 //
		Date dteRet = null; // 返回日期保存用

		// /1.取得计算对象日期计算结果
		try {
			// //1.1.生成Canlendar对象
			calObject = Calendar.getInstance();
			calObject.setTime(dteDate);

			// //1.2.日期年的加法
			calObject.add(Calendar.YEAR, (int) lngNumber);

			// //1.3.计算后日期的取得
			dteRet = calObject.getTime();

			// /2.发生异常时处理
		} catch (Exception e) {
			// //2.1. 返回NULL
			dteRet = null;
		}

		// /3.返回计算后的日期
		return dteRet;

	}

	/**
	 * 计算月
	 * 
	 * @param dteDate
	 *            计算对象日期
	 * @param lngNumber
	 *            加的月数（负数：过去；正数：未来）
	 * @return Date 计算后日期
	 */
	public static Date getAddMonth(Date dteDate, long lngNumber) {

		// 变量声明 //
		Date dteRet = null; // 返回日期保存用

		// /1.取得计算对象日期计算结果
		try {
			// //1.1.生成Canlendar对象
			calObject = Calendar.getInstance();
			calObject.setTime(dteDate);

			// //1.2.日期月的加法
			calObject.add(Calendar.MONTH, (int) lngNumber);

			// //1.3.计算后日期的取得
			dteRet = calObject.getTime();

			// /2.发生异常时处理
		} catch (Exception e) {
			// //2.1. 返回NULL
			dteRet = null;
		}

		// /3.返回计算后的日期
		return dteRet;

	}

	/**
	 * 计算日
	 * 
	 * @param dteDate
	 *            计算对象日期
	 * @param lngNumber
	 *            加的天数（负数：过去；正数：未来））
	 * @return Date 计算后日期
	 */
	public static Date getAddDay(Date dteDate, long lngNumber) {

		// 变量声明 //
		Date dteRet = null; // 返回日期保存用

		// /1.取得计算对象日期计算结果
		try {
			// //1.1.生成Canlendar对象
			calObject = Calendar.getInstance();
			calObject.setTime(dteDate);

			// //1.2.日期日的加法
			calObject.add(Calendar.DATE, (int) lngNumber);

			// //1.3.计算后日期的取得
			dteRet = calObject.getTime();

			// /2.发生异常时处理
		} catch (Exception e) {
			// //2.1. 返回NULL
			dteRet = null;
		}

		// /3.返回计算后的日期
		return dteRet;

	}
	
	
	/**
	 * 计算日
	 * 
	 * @param dteDate
	 *            计算对象日期
	 * @param lngNumber
	 *            加的天数（负数：过去；正数：未来））
	 * @return Date 计算后日期
	 */
	public static Date getAddMinute(Date dteDate, long lngNumber) {

		// 变量声明 //
		Date dteRet = null; // 返回日期保存用

		// /1.取得计算对象日期计算结果
		try {
			// //1.1.生成Canlendar对象
			calObject = Calendar.getInstance();
			calObject.setTime(dteDate);

			// //1.2.日期日的加法
			calObject.add(Calendar.MINUTE, (int) lngNumber);

			// //1.3.计算后日期的取得
			dteRet = calObject.getTime();

			// /2.发生异常时处理
		} catch (Exception e) {
			// //2.1. 返回NULL
			dteRet = null;
		}

		// /3.返回计算后的日期
		return dteRet;

	}

	/**
	 * 计算秒
	 * 
	 * @param dteDate
	 *            计算对象日期
	 * @param lngNumber
	 *            加的秒数（负数：过去；正数：未来））
	 * @return Date 计算后日期
	 */
	public static Date getAddSecond(Date dteDate, long lngNumber) {

		// 变量声明 //
		Date dteRet = null; // 返回日期保存用

		// /1.取得计算对象日期计算结果
		try {
			// //1.1.生成Canlendar对象
			calObject = Calendar.getInstance();
			calObject.setTime(dteDate);

			// //1.2.日期日的加法
			calObject.add(Calendar.SECOND, (int) lngNumber);

			// //1.3.计算后日期的取得
			dteRet = calObject.getTime();

			// /2.发生异常时处理
		} catch (Exception e) {
			// //2.1. 返回NULL
			dteRet = null;
		}

		// /3.返回计算后的日期
		return dteRet;

	}

	/**
	 * 比较两个日期
	 * 
	 * @param dteDate1
	 *            计算对象日期1
	 * @param dteDate2
	 *             计算对象日期2
	 * @return int 比较的结果
	 */
	public static int compareDate(Date dteDate1, Date dteDate2) {

		// 变量声明 //
		int comparRet = 0; // 返回比较结果保存用

		if (dteDate1 == null || dteDate2 == null) {
			return -1;
		}

		comparRet = dteDate1.compareTo(dteDate2);

		// 返回比较的结果
		return comparRet;

	}

	/**
	 * 判断两个日期距离
	 * 
	 * @param dteDate1
	 *            计算对象日期1
	 * @param dteDate2
	 *             计算对象日期2
	 * @return int 比较的结果
	 */
	public static int compareDateDistance(Date dteDate1, Date dteDate2) {

		// 变量声明 //
		int comparRet = 0; // 返回比较结果保存用

		if (dteDate1 == null || dteDate2 == null) {
			return 0;
		}

		comparRet = dteDate1.compareTo(dteDate2);

		// 返回比较的结果
		return comparRet;

	}

	/**
	 * 根据给定的格式化参数，将字符串转换为日期
	 * 
	 * @param dateString
	 * @param dateFormat
	 * @return java.util.Date
	 */
	public static java.util.Date parse(String dateString, String dateFormat) {
		if (dateString == null || "".equals(dateString.trim())  ) {
			return null;
		}
		DateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = null;
		try {
			date = sdf.parse(dateString);

		} catch (Exception e) {
			logger.error("parse exception,e.getMessage():{}",e.getMessage());
		}

		return date;
	}

	/**
	 * 
	 * 默认将字符串转换为日期，格式(yyyy-MM-dd)
	 * 
	 * @param dateString
	 * @return
	 */
	public static java.util.Date parse(String dateString) {
		return parse(dateString, DEFAULT_FORMAT);
	}

	/**
	 * 根据给定的格式化参数，将日期转换为字符串
	 * 
	 * @param date
	 * @param dateFormat
	 * @return String
	 */
	public static String toString(java.util.Date date, String dateFormat) {
		if ("".equals(date) || date == null) {
			return "bug: date is null";
		}
		DateFormat sdf = new SimpleDateFormat(dateFormat);
		String str = sdf.format(date);

		return str;
	}

	/**
	 * 默认将日期转换为字符串，格式(yyyy-MM-dd)
	 * 
	 * @param date
	 * @return String
	 */
	public static String toString(java.util.Date date) {
		return toString(date, DEFAULT_FORMAT);
	}

	/**
	 * 将日期转换为长整型?
	 * 
	 * @param date
	 * @return long
	 */
	public static long toLong(java.util.Date date) {
		if (date == null) {
			return 0;
		}
		long d = date.getTime();

		return d;
	}

	/**
	 * 将长整型转换为日期对象
	 * 
	 * @param time
	 * @return date
	 */
	public static java.util.Date toDate(long time) {
		if ("".equals(time)) {
			return new Date();
		}
		Date date = new Date(time);

		return date;
	}

	/**
	 * 获得系统当前时间
	 * 
	 * @return java.util.Date
	 */
    public static String currentStringDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式


        String now = dateFormat.format( date ); 
        return now;
    }
    /**
	 * 日期格式化
	 * 
	 * @return java.util.Date
	 */
    public static String StringYourDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式


        String now = dateFormat.format( date ); 
        return now;
    }
    /**
	 * 日期格式化
	 * 
	 * @return java.util.Date
	 */
    public static String longDateToString(long now) {
    	
    	if(now==0)
    		return "";
    	Date date = DateUtil.toDate(now);
    	
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        
        return dateFormat.format( date );
    }
	/**
	 * 获得系统当前时间(按用户自己格式)
	 * 
	 * @return java.util.Date
	 */
	public static String currentYourDate(String formate) {
		Date date = new Date();

		return toString(date, formate);
	}

	/**
	 * 获得系统当前时间
	 * 
	 * @return java.util.Date
	 */
	public static java.util.Date currentDate() {
		Date date = new Date();

		return date;
	}

	/**
	 * 根据日历的规则，为给定的日历字段添加或减去指定的时间�?
	 * 
	 * @param field
	 *            指定的日历字段
	 * @param date
	 *            需要操作的日期对象
	 * @param value
	 *            更改的时间值
	 * @return java.util.Date
	 */
	public static Date add(int field, Date date, int value) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(field, value);
		Date newDate = ca.getTime();

		return newDate;
	}

	/**
	 * 返回给定日历字段的值
	 * 
	 * @param field
	 *            指定的日历字段
	 * @param date
	 *            给定的日期对象
	 * @return java.util.Date
	 */
	public static int get(int field, Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		int value = ca.get(field);

		return value;
	}

	/**
	 * 返回前N个月的日期值
	 * 
	 * @param month
	 * @return
	 */
	public static Date getLastMonth(String month) {
		Calendar ca = Calendar.getInstance();
		int m = 0;
		try {
			m = Integer.parseInt(month);
		} catch (NumberFormatException e) {
			logger.error("getLastMonth exception,e.getMessage():{}",e.getMessage());
		}
		ca.add(Calendar.MONTH, -m);
		return ca.getTime();
	}
	
	/** 
	* @Title: getSeconds 
	* @Description: TODO(返回毫秒数) 
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public static String getSeconds(){
	    Calendar ca = Calendar.getInstance();
	    return ca.getTimeInMillis()+"";
	}
	
	public static String toDateFormat(Date dteDate, String strDateFormat) {

		// 变量声明 //
		String strRet = null;

		try {

			//if (StringUtils.isEmpty(dteDate)) {

			//	strRet = "";

			//} else {
				strRet = new SimpleDateFormat(strDateFormat).format(dteDate);
			//}

		} catch (Exception e) {
			strRet = "";
		}

		return strRet;

	}
    public static long getCurrentSeconds()
	{
		java.util.Date dt = new Date();
		return dt.getTime() / 1000; 
	}


	
	
	public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                        .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }
	
	public static long getExpireSecondsOfCurDay(){
		Calendar calendar1 = Calendar.getInstance();  
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),  
                0, 0, 0); 
        return getCurrentSeconds()- (calendar1.getTimeInMillis()/1000) ;
	}
	
	public static long getRemainSecondsOfCurDay()
	{
		Calendar calendar1 = Calendar.getInstance();
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),  
                23, 59, 59);  
   
        return (calendar1.getTimeInMillis()/1000)- getCurrentSeconds();
      }
	
	
	public static void main(String[] args) throws ParseException {
		  //当前0点时间戳
		  System.out.println("getExpireSecondsOfCurDay:"+getExpireSecondsOfCurDay()+"\n");
		  
		  System.out.println("getRemainSecondsOfCurDay:"+getRemainSecondsOfCurDay()+"\n");
			
	}

}
