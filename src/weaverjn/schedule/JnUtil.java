package weaverjn.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import weaver.conn.RecordSet;

public class JnUtil {

	public static String date2String(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	public static Date string2Date(String str){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
//			System.out.println(str);
			return sdf.parse(str);
		} catch (ParseException e) {
			writeLog(JnUtil.class.getName(), "日期转换报错！！！！");
			return null;
		}
	}
	/**
	 * 日期比较，参数day表示提前提醒天数。true 表示触发
	 * <br/>2017年4月25日 上午11:46:09<br/>
	 * @param currenttime
	 * @param tabletime
	 * @param day
	 * @return
	 */
	public static boolean compare(Date currenttime,Date tabletime,int day){
		// 2017-08-30 - 2017-04-30 >
		long c =currenttime.getTime();
		long t = tabletime.getTime();
		long d = day*24*3600*1000L;
		long r = (long)(t - c);
//		System.out.println("c: " + c);
//		System.out.println("t: " + t);
//		System.out.println("r: " + r);
//		System.out.println("d: " + d );
		if( r - d >0 )
			return false;
		else
			return true;
	}
	
	/**
	 * 当前时间>表中时间,返回true,修改
	 * <br/>2017年5月8日 下午2:29:17<br/>
	 * @param currenttime
	 * @param tabletime
	 * @return
	 */
	public static boolean compare(Date currenttime,Date tabletime){
		long c =currenttime.getTime();
		long t = tabletime.getTime();
		if(c > t)
			return true;
		return false;
	}
	
	/**
	 * 日期进行计算，求的差
	 * <br/>2017年5月18日 下午1:42:04<br/>
	 * @param str1 减数
	 * @param str2 被减数
	 * @return
	 */
	public static long calculateTime(Date str1,Date str2){
		return  (str1.getTime() - str2.getTime())/(3600*1000*24);
	}
	
	/**
	 * 日志信息
	 * <br/>2017年5月12日 上午9:22:02<br/>
	 * @param obj
	 */
	public static void writeLog(String className,Object obj) {
//		String className = getClass().getName();
		org.apache.commons.logging.Log jnlog = org.apache.commons.logging.LogFactory.getLog(className);
		if ((obj instanceof Exception))
			jnlog.error(className, (Exception) obj);
		else
			jnlog.error(obj);
	}
	
	/**
	 * 日志计入数据库
	 * <br/>2017年5月22日 下午2:28:42<br/>
	 * @param className
	 * @param fullName
	 * @param send
	 * @param receive
	 * @param sapurl
	 */
	public static void writeDB(String className, String fullName, String send, String receive,
			String sapurl, String sqlstr,String error) {
		sqlstr = sqlstr.replace("\n", "");
		sqlstr = sqlstr.replace("'", "\'");
		sqlstr = sqlstr.replace("", "");
		RecordSet rs = new RecordSet();
		String sql = "insert into uf_developlog(send,receive,filename,fullname,url,sql,error) "
				+ " values('" + send + "','" + receive + "','" + className + "','" + fullName
				+ "','" + sapurl + "','" + sqlstr + "','"+error+"')";
		writeLog(JnUtil.class.getName(), "日志计入数据库：" + sql);
		boolean e = rs.execute(sql);
		if (e) {
			writeLog(JnUtil.class.getName(), "日志计入数据库成功!");
		}
	}
	
	public static void main(String[] args) {
		Date d1 = JnUtil.string2Date("2017-05-10"); // biao
		Date d2 = new Date();	// dangqian
		System.out.println(calculateTime(d1, d2));
	}
}
