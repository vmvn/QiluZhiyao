package weaverjn.schedule;

import java.util.Date;

//import com.weaver.TestInit;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;
import weaverjn.action.integration.utils;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

/**
 * 药品资料定时任务，到期提醒指定人并更改sap接口
 * @author songqi
 * @tel 13256247773
 * 2017年4月25日 下午2:52:42
 */
public class DrugFile extends BaseCronJob{

	private static final String uf = "uf_sdwrypzl";
	private static String className = DrugFile.class.getName(); 
	@Override
	public void execute() {
		doit();
	}

	public static void main(String[] args) {
//		TestInit.init();
//		DrugFile sf = new DrugFile();
//		sf.workflowRemind("biaoti", 1453+"", "neirong");
		System.out.println(">>>>>ok： " + DrugFile.class.getSimpleName());
	}
	
	private void doit() {
//		String date = JnUtil.date2String(new Date());
		remind(new Date());
	}

	private void remind(Date date) {
		RecordSet rs = new RecordSet();
		//许可证有效期至 yxqz 认证证书有效期至 yxqz1 质保协议有效期至 yxqz2
		
		// 0 正常  1锁定
		String sql = "select id,txr,pzwhyxqz,gmpzsyxq from " + uf + " where zt='0'";
		JnUtil.writeLog(className, "查询状态为正常的记录sql: " + sql);
		rs.executeSql(sql);
		while(rs.next()){
			String pzwhyxqz = "",gmpzsyxq = "",id="";
			pzwhyxqz = Util.null2String(rs.getString("pzwhyxqz"));
			gmpzsyxq = Util.null2String(rs.getString("gmpzsyxq"));
			if(pzwhyxqz.equals("") || gmpzsyxq.equals(""))
				continue;
			id = Util.null2String(rs.getString("id"));
			Date pzwhyxqz_d = JnUtil.string2Date(pzwhyxqz);
			if(null == pzwhyxqz_d){
				JnUtil.writeLog(getClass().getName(), "把字符串转化为日期类型报错，检查字符串类型的<批准文号>");
				return ;
			}
			Date gmpzsyxq_d = JnUtil.string2Date(gmpzsyxq);
			if(null == gmpzsyxq_d){
				JnUtil.writeLog(getClass().getName(), "把字符串转化为日期类型报错，检查字符串类型的<GMP证书>");
				return ;
			}
			// 状态更新
			if(JnUtil.compare(new Date(), pzwhyxqz_d) || JnUtil.compare(new Date(), gmpzsyxq_d)){
				updateStatus(id);
				updateStatus2(id);
				updateOA(uf,id);
			}
		}
	}

	private void updateOA(String t,String id) {
		RecordSet rs = new RecordSet();
		boolean flag = rs.execute("update " + t + " set zt='1' where id='"+id+"'");
		if(flag)
			JnUtil.writeLog(getClass().getName(), "定时任务更新药品状态成功！");
		else
			JnUtil.writeLog(getClass().getName(), "数据更新药品状态失败：更新药品的表： "+t+"  主键：" + id);
	}

	private void updateStatus2(String ypmc){
		String tag = "erp:MT_Material_Qualification_Req";
		String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">" +
				"   <soapenv:Header/>\n" +
				"   <soapenv:Body>\n" +
				"      <" + tag + ">\n" +
				"         <ControlInfo>\n" +
				"            <INTF_ID></INTF_ID>\n" +
				"            <Src_System>OA</Src_System>\n" +
				"            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
				"            <Company_Code></Company_Code>\n" +
				"            <Send_Time></Send_Time>\n" +
				"         </ControlInfo>\n" +
				getLine2(ypmc) +
				"      </" + tag + ">\n" +
				"   </soapenv:Body>\n" +
				"</soapenv:Envelope>";
		JnUtil.writeLog(className,"请求sap字节码： " + request);
		String username = utils.getUsername();
		String password = utils.getPassword();
		String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", "MaterialQualification");
		JnUtil.writeLog(className, "请求sap的URL: " + endpoint);
		String response = WSClientUtils.callWebService(request, endpoint, username, password);
		JnUtil.writeLog(className,"sap返回信息" + response);
	}
	
	private void updateStatus(String ypmc) {
		String tag = "erp:MT_DrugInformation";
		String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <"+tag+">\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID></INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" + getLine(ypmc) +
                "      </"+tag+">\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
		JnUtil.writeLog(className, "请求sap的字节码： " + request);
		String username = utils.getUsername();
		String password = utils.getPassword();
		String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", "DrugInformation");
		JnUtil.writeLog(className, "sap的URL: " + endpoint);
		String resp = WSClientUtils.callWebService(request, endpoint, username, password);
		JnUtil.writeLog(className, "sap返回地址： " + resp);
	}
	
	
	private String getLine2(String ypmc) {
		StringBuilder stringBuilder = new StringBuilder();
		RecordSet recordSet = new RecordSet();
		String sql = "select * from "+uf+" where id='" + ypmc + "'";
		JnUtil.writeLog(className, "查询药品的sql: " + sql);
		recordSet.executeSql(sql);
		if (recordSet.next()) {
			stringBuilder.append("<Mater_Qual>\n")
			.append("<MATNR>").append(Util.null2String(recordSet.getString("ypbh"))).append("</MATNR>\n")
			.append("<WERKS>").append(Util.null2String(recordSet.getString("gc"))).append("</WERKS>\n")
			.append("<SPERM>").append("N").append("</SPERM>\n")
			.append("<DEAL_SCPOE>").append(Util.null2String(recordSet.getString("lb1"))).append("</DEAL_SCPOE>\n")
			.append("<EXPIRY_DATE_GMP>").append(Util.null2String(recordSet.getString("gmpzsyxq"))).append("</EXPIRY_DATE_GMP>\n")
			.append("<EXPIRY_DATE_LICENSE>").append(Util.null2String(recordSet.getString("pzwhyxqz"))).append("</EXPIRY_DATE_LICENSE>\n")
			.append("</Mater_Qual>\n");
		}
		return stringBuilder.toString();
	}

	private String getLine(String ypmc) {
		RecordSet rs = new RecordSet();
		String sql = "select * from "+uf+" where id='" + ypmc + "'";
		JnUtil.writeLog(className, "查询药品的sql: " + sql);
		rs.executeSql(sql);
		rs.next();
		StringBuffer sb = new StringBuffer();
		sb.append("<lifnr>").append(Util.null2String(rs.getString("gc"))).append("</lifnr>");
		sb.append("<matnr>").append(Util.null2String(rs.getString("ypbh"))).append("</matnr>");
		sb.append("<ZMAKTX_GYM>").append(Util.null2String(rs.getString("gym"))).append("</ZMAKTX_GYM>");
		sb.append("<ZMAKTX_SPM>").append(Util.null2String(rs.getString("spbm"))).append("</ZMAKTX_SPM>");
		sb.append("<ZCPJIX>").append(utils.getSelectName("26605", rs.getString("jx"))).append("</ZCPJIX>");
		sb.append("<ZBZGG>").append(Util.null2String(rs.getString("bzgg"))).append("</ZBZGG>");
		sb.append("<ZSCQY>").append(Util.null2String(rs.getString("scqy"))).append("</ZSCQY>");
		sb.append("<ZZCSB>").append(Util.null2String(rs.getString("zcsb"))).append("</ZZCSB>");
		sb.append("<ZPZWH>").append(Util.null2String(rs.getString("pzwh"))).append("</ZPZWH>");
		sb.append("<ZGMP_BH>").append(Util.null2String(rs.getString("gmpzsh"))).append("</ZGMP_BH>");
		sb.append("<ZGMP_FW>").append(Util.null2String(rs.getString("gmpfw"))).append("</ZGMP_FW>");
		sb.append("<ZZCTJ>").append(Util.null2String(rs.getString("cctj"))).append("</ZZCTJ>");
		sb.append("<ZYHZQ>").append(Util.null2String(rs.getString("yhzq"))).append("</ZYHZQ>");
		sb.append("<ZYXQ_CP>").append(Util.null2String(rs.getString("yxqy"))).append("</ZYXQ_CP>");
		sb.append("<ZTS_JXQSD>").append(Util.null2String(rs.getString("sxsdts1"))).append("</ZTS_JXQSD>");
		sb.append("<ZJYFW>").append(Util.null2String(rs.getString("lb1"))).append("</ZJYFW>");
		sb.append("<ZDZJGM>").append(Util.null2String(rs.getString("sfjdzjgm"))).append("</ZDZJGM>");
		sb.append("<ZYFBS_BZ>").append(Util.null2String(rs.getString("bzyfbs"))).append("</ZYFBS_BZ>");
		sb.append("<ZLENGTH>").append(Util.null2String(rs.getString("cd"))).append("</ZLENGTH>");
		sb.append("<ZHIGH>").append(Util.null2String(rs.getString("gd"))).append("</ZHIGH>");
		sb.append("<ZDBJS_BZ>").append(Util.null2String(rs.getString("bzdbjs"))).append("</ZDBJS_BZ>");
		sb.append("<ZRY_ZGY>").append(Util.null2String(rs.getString("zlgly"))).append("</ZRY_ZGY>");
		sb.append("<ZRY_BGY>").append(Util.null2String(rs.getString("bgy"))).append("</ZRY_BGY>");
		sb.append("<ZSRYS>").append(Util.null2String(rs.getString("sfsrys"))).append("</ZSRYS>");
		sb.append("<ZCSJG>").append(Util.null2String(rs.getString("csjgsj"))).append("</ZCSJG>");
		sb.append("<ZZHWL>").append(Util.null2String(rs.getString("yhzq"))).append("</ZZHWL>");
		sb.append("<ZZHWL_CW>").append(Util.null2String(rs.getString("cwzspzh"))).append("</ZZHWL_CW>");
		String ZZHXS_CW = Util.null2String(rs.getString("cwzsxs"));
		sb.append("<ZZHXS_CW>").append(ZZHXS_CW.equals("") ? "0.00" : ZZHXS_CW).append("</ZZHXS_CW>");
		sb.append("<BNAME>").append(Util.null2String(rs.getString("zdr"))).append("</BNAME>");
		sb.append("<ZCPMC>").append(Util.null2String(rs.getString("tymc"))).append("</ZCPMC>");
		sb.append("<MAKTX>").append(Util.null2String(rs.getString("wlms"))).append("</MAKTX>");
		sb.append("<ZMAKTX_YWM>").append(Util.null2String(rs.getString("ywm"))).append("</ZMAKTX_YWM>");
		sb.append("<ZGUIGE>").append(Util.null2String(rs.getString("gg"))).append("</ZGUIGE>");
		sb.append("<ZXINGZ>").append(Util.null2String(rs.getString("xz"))).append("</ZXINGZ>");
		sb.append("<ZBWTSCDW>").append(Util.null2String(rs.getString("bwtscqy"))).append("</ZBWTSCDW>");
		sb.append("<ZZLBZ>").append(Util.null2String(rs.getString("zlbz"))).append("</ZZLBZ>");
		sb.append("<ZPZWH_YXQ>").append(Util.null2String(rs.getString("pzwhyxqz"))).append("</ZPZWH_YXQ>");
		sb.append("<ZGMP_YQX>").append(Util.null2String(rs.getString("gmpzsyxq"))).append("</ZGMP_YQX>");
		sb.append("<LGORT>").append(Util.null2String(rs.getString("ckdd"))).append("</LGORT>");
		sb.append("<ZBZPSL>").append(Util.null2String(rs.getString("bzpzxdws"))).append("</ZBZPSL>");
		sb.append("<ZSXBJTS>").append(Util.null2String(rs.getString("sxbjts"))).append("</ZSXBJTS>");
		sb.append("<ZLSJG>").append(Util.null2String(rs.getString("lsj"))).append("</ZLSJG>");
		sb.append("<ZWIDE>").append(Util.null2String(rs.getString("kd"))).append("</ZWIDE>");
		sb.append("<ZWEIGHT>").append(Util.null2String(rs.getString("zl"))).append("</ZWEIGHT>");
		sb.append("<ZFPBM>").append(Util.null2String(rs.getString("fpbm"))).append("</ZFPBM>");
		sb.append("<ZRY_YSY>").append(Util.null2String(rs.getString("ysy"))).append("</ZRY_YSY>");
		sb.append("<ZRY_FHY>").append(Util.null2String(rs.getString("fhy"))).append("</ZRY_FHY>");
		sb.append("<ZLCP>").append(Util.null2String(rs.getString("sflc"))).append("</ZLCP>");
		sb.append("<ZSRFH>").append(Util.null2String(rs.getString("sfsrfh"))).append("</ZSRFH>");
		sb.append("<ZSTATE>").append("Y").append("</ZSTATE>");
		String ZZHXS =  Util.null2String(rs.getString("ywzsxs"));
		sb.append("<ZZHXS>").append(ZZHXS.equals("") ? "0.00" : ZZHXS).append("</ZZHXS>");
		sb.append("<ZZHWL_MC>").append(Util.null2String(rs.getString("cwzspzm"))).append("</ZZHWL_MC>");
		String ZDWJG_CW =  Util.null2String(rs.getString("cwdwjg"));
		sb.append("<ZDWJG_CW>").append(ZDWJG_CW.equals("")?"0.00":ZDWJG_CW).append("</ZDWJG_CW>");
		sb.append("<ZSDYY>").append(Util.null2String(rs.getString("sdyy"))).append("</ZSDYY>");
		sb.append("<ZDATE>").append(Util.null2String(rs.getString("lrsj"))).append("</ZDATE>");
		return sb.toString();
	}

	
}
