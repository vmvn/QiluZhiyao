package weaverjn.crm.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import weaver.conn.RecordSet;
import weaver.general.Util;
/**
 * 
 * @author songqi
 * @tel 13256247773
 * 2017年4月21日 上午10:12:43
 */
public class JnUtils {
	
	/**
	 * 获得日期或日期时间
	 * <br/>2017年4月21日 上午10:20:57<br/>
	 * @param type datetime表示日期时间
	 * @return
	 */
	public static String getDate(String type){
		Calendar c = Calendar.getInstance();
		Date time = c.getTime();
		String pattern = "yyyyMMdd";
		if("datetime".equals(type))
			pattern = "yyyyMMdd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(time);
	}

	
	/**
	 * 获得下拉框对应的selectname值
	 * <br/>2017年4月21日 上午9:41:27<br/>
	 * @param table 对应表名
	 * @param key 列的key  jx
	 * @param value 列的value 0 1 2 3
	 * @return
	 */
	public static String getFieldname(String table,String key,String value) {
		RecordSet rs = new RecordSet();
		String sql = "select SELECTNAME from WORKFLOW_SELECTITEM where fieldid=(select id from workflow_billfield where billid=(select id from workflow_bill where tablename = '"+table+"') and fieldname='"+key+"') and SELECTVALUE='"+value+"'";
		rs.executeSql(sql);
		if(rs.next())
			return Util.null2String(rs.getString("selectname"));
		return "";
	}

	/**
	 * 根据用户id取得用户姓名
	 * <br/>2017年4月21日 上午9:55:59<br/>
	 * @param userid
	 * @return
	 */
	public static String getLastname(String userid){
		RecordSet rs = new RecordSet();
		rs.executeSql("select lastname from hrmresource where id=" + userid);
		if(rs.next())
			return Util.null2String(rs.getString("lastname"));
		return "";
	}
	
	public static String getDeptname(String deptid) {
		RecordSet rs = new RecordSet();
		rs.executeSql("select departmentname from hrmdepartment where id='"+deptid+"' ");
		if(rs.next())
			return Util.null2String(rs.getString("departmentname"));
		return "";
	}
	
	public static String getJX(String str){
		str = str.trim();
		if("注射剂".equals(str))
			return "01";
		if("片剂".equals(str))
			return "02";
		if("乳膏剂".equals(str))
			return "03";
		if("粉针剂".equals(str))
			return "04";
		if("冻干剂".equals(str))
			return "05";
		if("注射液".equals(str))
			return "06";
		if("滴眼剂".equals(str))
			return "07";
		if("胶囊剂".equals(str))
			return "08";
		if("颗粒剂".equals(str))
			return "09";
		if("凝胶剂".equals(str))
			return "10";
		if("气雾剂".equals(str))
			return "11";
		if("软膏剂".equals(str))
			return "12";
		if("水针剂".equals(str))
			return "13";
		if("眼膏剂".equals(str))
			return "14";
		return "";
	}
	
	public static String getScqy(String str){
		str = str.trim();
		if("齐鲁制药有限公司".equals(str))
			return "GY000001";
		if("齐鲁天和惠世制药有限公司".equals(str))
			return "GY000002";
		if("齐鲁制药（海南）有限公司".equals(str))
			return "GY000003";
		return "";
	}
	
	public static String getJldw(String str){
		str = str.trim();
		if("瓶".equals(str))	return "BOT";
		if("粒".equals(str))	return "Z01";
		if("支".equals(str))	return "Z02";
		if("片".equals(str))	return "Z03";
		if("板".equals(str))	return "Z04";
		if("比旋度度(°)".equals(str))	return "Z05";
		if("cfu/克".equals(str))	return "Z06";
		if("cfu/毫升".equals(str))	return "Z07";
		if("cfu/10cm2".equals(str))	return "Z08";
		if("cfu/100cm2".equals(str))	return "Z09";
		if("g/cm2.24h".equals(str))	return "Z10";
		if("厘米的倒数（cm-1）".equals(str))	return "Z11";
		if("小盒".equals(str))	return "Z12";
		if("中盒".equals(str))	return "Z13";
		if("副".equals(str))	return "Z14";
		if("张".equals(str))	return "Z15";
		if("枚".equals(str))	return "Z16";
		if("盒".equals(str))	return "Z17";
		if("桶".equals(str))	return "Z18";
		if("卷".equals(str))	return "Z19";
		if("种".equals(str))	return "Z20";
		if("次".equals(str))	return "Z21";
		if("令".equals(str))	return "Z22";
		if("套".equals(str))	return "Z23";
		if("本".equals(str))	return "Z24";
		if("捆".equals(str))	return "Z25";
		if("丝".equals(str))	return "Z26";
		if("μg/mg".equals(str))	return "Z27";
		if("万个".equals(str))	return "Z28";
		if("万张".equals(str))	return "Z29";
		if("万片".equals(str))	return "Z30";
		if("万支".equals(str))	return "Z31";
		if("万瓶".equals(str))	return "Z32";
		if("万粒".equals(str))	return "Z33";
		if("万袋".equals(str))	return "Z34";
		if("双".equals(str))	return "Z35";
		if("盘".equals(str))	return "Z36";
		if("份".equals(str))	return "Z37";
		if("株".equals(str))	return "Z38";
		if("条".equals(str))	return "Z39";
		if("万套".equals(str))	return "Z40";
		if("小包装".equals(str))	return "Z41";
		if("中包装".equals(str))	return "Z42";
		if("打包数".equals(str))	return "Z43";
		if("台".equals(str))	return "Z44";
		if("组".equals(str))	return "Z45";
		if("亿".equals(str))	return "Z46";
		if("根".equals(str))	return "Z47";
		if("亿每公斤".equals(str))	return "Z98";
		if("十亿每公斤".equals(str))	return "Z99";
		return "";
	}

	
	
	
}
