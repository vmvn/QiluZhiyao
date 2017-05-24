package weaverjn.schedule;

import java.util.Date;

import com.weaver.TestInit;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;
import weaverjn.action.integration.utils;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

/**
 * 供应商资料定时任务，到期提醒指定人并更改sap接口
 * @author songqi
 * @tel 13256247773
 * 2017年4月25日 下午2:52:42
 */
public class SupplierFile extends BaseCronJob{

	private String LIFNR_TYPE = "";
	private String EKORG = "1620";
	private String LIFNR = "";
	private String SPERM = "";
	private String DATE_BEG = "";
	private String DATE_END = "";
	private String SCPOE_ID = "";
	private String MATNR = "";
	private String uf = "uf_ghdzl";
	private static String className = SupplierFile.class.getName();
	@Override
	public void execute() {
		JnUtil.writeLog(className, "供应商：run》》》》》");
		doit();
		JnUtil.writeLog(className, "供应商：end《《《《《");
		
	}

	public static void main(String[] args) {
		TestInit.init();
		SupplierFile sf = new SupplierFile();
		sf.doit();
	}
	
	private void doit() {
//		String date = JnUtil.date2String(new Date());
		remind(new Date());
	}

	private void remind(Date date) {
		RecordSet rs = new RecordSet();
		//许可证有效期至 yxqz 认证证书有效期至 yxqz1 质保协议有效期至 yxqz2
		String endtime = JnUtil.date2String(date);
		String sql = "select id,zdr,yxqz,yxqz1,yxqz2 from " + uf + " "
				+ "where yxqz < '"+endtime+"' or yxqz1 < '"+endtime+"' or yxqz2 < '"+endtime+"' ";

		JnUtil.writeLog(className, "查询所有供应商的sql： " + sql);
		rs.executeSql(sql);
		while(rs.next()){
			String xkz = "",rzzs = "",zbxy = "",id = "";
			id = Util.null2String(rs.getString("id"));
//			xkz = Util.null2String(rs.getString("yxqz"));
//			rzzs = Util.null2String(rs.getString("yxqz1"));
//			zbxy = Util.null2String(rs.getString("yxqz2"));
//			if(xkz.equals("") || rzzs.equals("") || zbxy.equals(""))
//				continue;
//			Date xkz_d = JnUtil.string2Date(xkz);
//			Date rzzs_d = JnUtil.string2Date(rzzs);
//			Date zbxy_d = JnUtil.string2Date(zbxy);
			// 状态更新
//			long c1 = JnUtil.calculateTime(date, xkz_d);
//			long c2 = JnUtil.calculateTime(date, rzzs_d);
//			long c3 = JnUtil.calculateTime(date, zbxy_d);
//			if (c1 > 0 || c2 > 0 || c3 > 0) {
				updateStatus(id);
//			}
		}
	}

	private void updateStatus(String id) {
		String tag = "erp:MT_Supplier_Qualification_Req";
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
                "         </ControlInfo>\n" +
               getLine(id) +
                "      </"+tag+">\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
		JnUtil.writeLog(className, "请求信息：" + request);
		String username = utils.getUsername();
		String password = utils.getPassword();
		String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", "SupplierQualification");
		String resp = WSClientUtils.callWebService(request, endpoint, username, password);
		JnUtil.writeLog(className, "sap返回信息： " + resp);
		
	}

	private String getLine(String id) {
		StringBuffer sb = new StringBuffer();
		RecordSet rs = new RecordSet();
		String sql = "select * from " + uf + " where id='"+id+"'";
		JnUtil.writeLog(className, "根据id查询供应商资料的sql： " + sql);
		rs.executeSql(sql);
		rs.next();
		sb.append("<LIFNR_TYPE>" + 1 + "</LIFNR_TYPE>");
		sb.append("<EKORG>" + EKORG + "</EKORG>");
		sb.append("<LIFNR>" + Util.null2String(rs.getString("ghdwbm")) + "</LIFNR>");
		sb.append("<SPERM>" + "N" + "</SPERM>");
		sb.append("<DATE_BEG></DATE_BEG>");
		sb.append("<DATE_END></DATE_END>");
		sb.append("		<Deal_Scpoe>");
		sb.append(getjyfw(Util.null2String(rs.getString("id"))));
		sb.append("		</Deal_Scpoe>");
		sb.append("<Material_List>");
		sb.append("<MATNR></MATNR>");
		sb.append("</Material_List>");
		return sb.toString();
	}

	private String getjyfw(String id) {
		StringBuffer sb = new StringBuffer();
		RecordSet rs = new RecordSet();
		String sql = "select * from " + uf + "_dt1 where mainid='"+id+"'";
		JnUtil.writeLog(className, "查询经营范围的sql： " + sql);
		rs.executeSql(sql);
		while(rs.next()){
			sb.append("<SCPOE_ID>" + Util.null2String(rs.getString("jyfw")) + "</SCPOE_ID>").append("\n");
		}
		return sb.toString();
	}

	public String getLIFNR_TYPE() {
		return LIFNR_TYPE;
	}

	public void setLIFNR_TYPE(String lIFNR_TYPE) {
		LIFNR_TYPE = lIFNR_TYPE;
	}

	public String getEKORG() {
		return EKORG;
	}

	public void setEKORG(String eKORG) {
		EKORG = eKORG;
	}

	public String getLIFNR() {
		return LIFNR;
	}

	public void setLIFNR(String lIFNR) {
		LIFNR = lIFNR;
	}

	public String getSPERM() {
		return SPERM;
	}

	public void setSPERM(String sPERM) {
		SPERM = sPERM;
	}

	public String getDATE_BEG() {
		return DATE_BEG;
	}

	public void setDATE_BEG(String dATE_BEG) {
		DATE_BEG = dATE_BEG;
	}

	public String getDATE_END() {
		return DATE_END;
	}

	public void setDATE_END(String dATE_END) {
		DATE_END = dATE_END;
	}

	public String getSCPOE_ID() {
		return SCPOE_ID;
	}

	public void setSCPOE_ID(String sCPOE_ID) {
		SCPOE_ID = sCPOE_ID;
	}

	public String getMATNR() {
		return MATNR;
	}

	public void setMATNR(String mATNR) {
		MATNR = mATNR;
	}
	
	
}
