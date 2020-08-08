package com.pl.restty.server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatetimeUtils {

	public static String dateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	public static String dateFormat(String fmt) {
		return new SimpleDateFormat(fmt).format(new Date());
	}
	
	public static String dateFormat(Date date,String fmt) {
		return new SimpleDateFormat(fmt).format(date);
	}
	
	public static Date stringDate(String dateString,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			Date date = sdf.parse(dateString);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static long timeDiffSeconds(Date start,Date end){
		long diff = end.getTime() - start.getTime();
		return diff/1000;
		
	}
	public static long timeDiffMillSeconds(Date start,Date end){
		long diff = end.getTime() - start.getTime();
		return diff;
		
	}
	
	public static Date dateAddSecond(int ms){
		Date now = new Date();
		Date afterDate = new Date(now.getTime() + ms);
		return afterDate;
	}
	
	public static Date dateAddSecond(Date start,int ms){
		Date afterDate = new Date(start.getTime() + ms);
		return afterDate;
	}
	
	public static boolean isAfterNow(Date d){
		Date now = new Date();
		return d.after(now);
	}
	
	public static boolean isMonthEnd()
	{
		Calendar calendar = Calendar.getInstance(); 
        calendar.setTime(new Date()); 
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 2)); 
        if (calendar.get(Calendar.DAY_OF_MONTH) < 3) { 
            return true; 
        } 
		return false;
	}
	
	public static String getLastDayOfMonth(int year,int month)
    {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String lastDayOfMonth = sdf.format(cal.getTime());
         
        return lastDayOfMonth;
    }
}
