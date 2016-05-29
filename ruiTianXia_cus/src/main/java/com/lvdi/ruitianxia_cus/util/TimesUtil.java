package com.lvdi.ruitianxia_cus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;

public class TimesUtil {
	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 返回当前时间格式为 "yyMMddHHmmss"的字符串
	 * 
	 * @return
	 */
	public static String getFormatCurDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
		Date date = new Date(System.currentTimeMillis());
		return format.format(date);
	}
	
	/**
	 * @param time  HHmmss
	 * @return HH
	 */
	public static String getHourFormate(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		try {
			Date date = sdf.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			int d[] = new int[3];
			d[0] = cal.get(Calendar.HOUR_OF_DAY);
			return d[0]+"";

		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(time.length()<=2){
			return time;
		}
		return "";
	}

	/**
	 * date: System.currentTimeMillis() 返回格式 yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String formatDate(String date) {
		if (date == null) {
			return "";
		}
		String temp = date;
		if (date.length() == 10) {
			temp = date + "000";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date(Long.parseLong(temp))).toString();
	}

	/**
	 * @param date
	 *            System.currentTimeMillis() 返回格式 yyyy-MM-dd HH:mm:ss
	 * @Description:设置时间显示的格式
	 */
	public static String formatTime(String date) {
		if (date == null) {
			return "";
		}
		String temp = date;
		if (date.length() == 10) {
			temp = date + "000";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(Long.parseLong(temp))).toString();
	}

	/**
	 * @param date
	 *            yyyyMMddHHmmss 返回格式 yyyy.MM.dd
	 * @Description:设置时间显示的格式
	 */
	public static String formatTime2(String date) {
		if (date == null || date.length() != 14) {
			return "";
		}
		SimpleDateFormat old = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat now = new SimpleDateFormat("yyyy.MM.dd");
		try {
			return now.format(old.parse(date)).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * @param date
	 *            yyyyMMddHHmmss   返回格式 yyyy-MM-dd HH:mm
	 * @Description:设置时间显示的格式
	 */
	public static String formatTime3(String date) {
		if (date == null || (date.length() != 14 && date.length() != 12)) {
			return "";
		}
		String tempDate = date;
		if(date.length()==12){
			tempDate = date+"00";
		}
		SimpleDateFormat old = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return now.format(old.parse(tempDate)).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * @param date
	 *            yyyyMMddHHmmss   返回格式 yyyy-MM-dd
	 * @Description:设置时间显示的格式
	 */
	public static String formatTime4(String date) {
		if (date == null || (date.length() != 14 && date.length() != 12)) {
			return "";
		}
		String tempDate = date;
		if(date.length()==12){
			tempDate = date+"00";
		}
		SimpleDateFormat old = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return now.format(old.parse(tempDate)).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * @param date
	 *            yyyyMMddHHmmss 返回格式 yyyy.MM.dd HH.mm
	 * @Description:设置时间显示的格式
	 */
	public static String formatTime5(String date) {
		if (date == null || date.length() != 14) {
			return "";
		}
		SimpleDateFormat old = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat now = new SimpleDateFormat("yyyy.MM.dd HH.mm");
		try {
			return now.format(old.parse(date)).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * @param date
	 *            yyyy-MM-dd   返回格式 yyyyMMdd
	 * @Description:设置时间显示的格式
	 */
	public static String formatTime6(String date) {
		if (date == null  ) {
			return "";
		}
		String tempDate = date;
		 
		SimpleDateFormat old = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat now = new SimpleDateFormat("yyyyMMdd");
		try {
			return now.format(old.parse(tempDate)).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * @param date
	 *            yyyyMMddHHmmss 返回格式 yyyyMMdd HH:mm:ss
	 * @Description:设置时间显示的格式
	 */
	public static String formatTime7(String date) {
		if (date == null || date.length() != 14) {
			return "";
		}
		SimpleDateFormat old = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat now = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		try {
			return now.format(old.parse(date)).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * @param date
	 *            yyyyMMddHHmmss 返回格式 yyyyMMdd HH:mm:ss
	 * @Description:设置时间显示的格式
	 */
	public static String formatTime8(String date) {
		if (date == null || date.length() != 14) {
			return "";
		}
		SimpleDateFormat old = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat now = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
			return now.format(old.parse(date)).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * @param date
	 *            yyyyMMddHHmmss 返回格式 yyyy.MM.dd HH.mm.ss
	 * @Description:设置时间显示的格式
	 */
	public static String formatTime9(String date) {
		if (date == null || date.length() != 14) {
			return "";
		}
		SimpleDateFormat old = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat now = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		try {
			return now.format(old.parse(date)).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 返回当前时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @Description:设置时间显示的格式
	 */
	public static String formatCurTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date()).toString();
	}
	/**
	 * 返回当前时间MM-dd HH:mm
	 * 
	 * @Description:设置时间显示的格式
	 */
	public static String formatCurTime2() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(new Date()).toString();
	}
	/**
	 * 年月日 比较大小
	 * 
	 * @Description:true :startTime<=endTime false:start>end
	 */
	public static boolean compareDay(int startTime[], int endTime[]) {
		Calendar cal = Calendar.getInstance();
		cal.set(startTime[0], startTime[1], startTime[2]);
		long start = cal.getTimeInMillis();

		Calendar cal2 = Calendar.getInstance();
		cal2.set(endTime[0], endTime[1], endTime[2]);
		long end = cal2.getTimeInMillis();
		return start <= end;
	}

	/**
	 * 返回格式 yyyy-MM-dd HH:mm:ss
	 * 
	 * @throws ParseException
	 * @Description:设置时间显示的格式
	 */
	public static String formatTime(int year, int month, int date) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, date);
		Date d1 = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d1).toString();
	}

	/**
	 * 返回格式 yyyy-MM-dd
	 * 
	 * @Description:设置时间显示的格式
	 */
	public static String formatDay(int year, int month, int date) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, date);
		Date d1 = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d1).toString();
	}

	/**
	 * 从 yyyy-MM-dd HH:mm:ss 格式 的日期中获取 年、月、日，出错返回null
	 * 
	 * @param str
	 * @return
	 */
	public static int[] convertTime(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(str);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			int d[] = new int[3];
			d[0] = cal.get(Calendar.YEAR);
			d[1] = cal.get(Calendar.MONTH);
			d[2] = cal.get(Calendar.DATE);
			return d;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从 yyyy-MM-dd 格式 的日期中获取 年、月、日，出错返回null
	 * 
	 * @param str
	 * @return
	 */
	public static int[] convertDay(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(str);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			int d[] = new int[3];
			d[0] = cal.get(Calendar.YEAR);
			d[1] = cal.get(Calendar.MONTH);
			d[2] = cal.get(Calendar.DATE);
			return d;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从 yyyyMMdd 格式 的日期中获取 年、月、日，出错返回null
	 * 
	 * @param str
	 * @return
	 */
	public static int[] convertDay2(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			Date date = sdf.parse(str);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			int d[] = new int[3];
			d[0] = cal.get(Calendar.YEAR);
			d[1] = cal.get(Calendar.MONTH);
			d[2] = cal.get(Calendar.DATE);
			return d;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从 yyyyMMddHHmmss 格式 的日期中获取天、 小时、分钟，出错返回null
	 * 
	 * @param str
	 * @return
	 */
	public static int[] convertDay3(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date date = sdf.parse(str);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			int d[] = new int[3];
			d[0] = cal.get(Calendar.DAY_OF_MONTH);
			d[1] = cal.get(Calendar.HOUR_OF_DAY);
			d[2] = cal.get(Calendar.MINUTE);
			return d;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param time yyyymmddhh24miss 
	 */
	public static Date convertDate(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			return sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从System.currentTimeMillis() 格式 的日期中获取 年、月、日，出错返回null
	 * 
	 * @param addtime
	 *            System.currentTimeMillis()
	 * @return
	 */
	public static int[] convertMill(String addtime) {
		String temp = addtime;
		if (addtime.length() == 10) {
			temp = addtime + "000";
		}
		Date date = new Date();
		date.setTime(Long.parseLong(temp));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int d[] = new int[3];
		d[0] = cal.get(Calendar.YEAR);
		d[1] = cal.get(Calendar.MONTH);
		d[2] = cal.get(Calendar.DATE);
		return d;
	}

	/**
	 * 格式 的日期中获取 年、月、日，出错返回null
	 * 
	 * @param str
	 *            System.currentTimeMillis()
	 * @return
	 */
	public static int[] convert(long str) {
		Date date = new Date();
		date.setTime(str);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int d[] = new int[3];
		d[0] = cal.get(Calendar.YEAR);
		d[1] = cal.get(Calendar.MONTH);
		d[2] = cal.get(Calendar.DATE);
		return d;
	}

	/**
	 * @param birth
	 *            System.currentTimeMillis()
	 * @return int[0] 年 int[1]月 int[2]日
	 * @throws ParseException
	 */
	public static int[] getBetweenTime(String birth) {
		String temp = birth;
		if (birth.length() == 10) {
			temp = birth + "000";
		}
		Date d1 = new Date(Long.parseLong(temp));
		Date d2 = new Date();
		long ss = d2.getTime() - d1.getTime();
		long totalday = ss / (24 * 60 * 60 * 1000);
		int year = (int) (totalday / 365);
		int month = (int) (totalday - year * 365) / 30;
		int day = (int) (totalday - month * 30 - year * 365);
		return new int[] { year, month, day };
	}

	/**
	 * @param birth
	 *            System.currentTimeMillis()
	 * @return int[0] 年 int[1]月 int[2]日
	 * @throws ParseException
	 */
	public static int[] getBetweenTime(String curTime, String birth) {
		if (TextUtils.isEmpty(curTime) || TextUtils.isEmpty(birth)) {
			return null;
		}
		long curtime = Long.parseLong(curTime);
		long birthtime = Long.parseLong(birth);
		long timeMill = curtime - birthtime;
		long totalday = timeMill / (24 * 60 * 60);
		int year = (int) (totalday / 365);
		int month = (int) (totalday - year * 365) / 30;
		int day = (int) (totalday - month * 30 - year * 365);
		return new int[] { year, month, day };
	}

	/**
	 * 从System.currentTimeMillis() 格式 的日期中获取 年、月、日，出错返回null
	 * 
	 * @param addtime
	 *            System.currentTimeMillis()
	 * @return
	 */
	public static int[] getCurTimeArr() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int d[] = new int[3];
		d[0] = cal.get(Calendar.YEAR);
		d[1] = cal.get(Calendar.MONTH);
		d[2] = cal.get(Calendar.DATE);
		return d;
	}

	/**
	 * 从System.currentTimeMillis() 格式 的日期中获取 小时、分，出错返回null
	 * 
	 * @param addtime
	 *            System.currentTimeMillis()
	 * @return
	 */
	public static int[] getCurHourMin() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int d[] = new int[2];
		d[0] = cal.get(Calendar.HOUR_OF_DAY);
		d[1] = cal.get(Calendar.MINUTE);
		return d;
	}
	
	/**
	 * @return yyyyMMdd
	 */
	public static String getCurDay(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
}
