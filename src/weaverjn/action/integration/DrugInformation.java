package weaverjn.action.integration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

import java.util.Map;

/**
 * 药品资料
 * @author songqi
 * @tel 13256247773
 * 2017年4月18日 上午10:10:12
 */
public class DrugInformation extends BaseBean implements Action  {
	private String uf;
	@Override
	public String execute(RequestInfo requestInfo) {
		Map<String, String> mainTableData = utils.getMainTableData(requestInfo.getMainTableInfo());
		String ypmc = mainTableData.get("ypmc");
		String tag = "erp:MT_DrugInformation";
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
				getLine(ypmc) +
				"      </" + tag + ">\n" +
				"   </soapenv:Body>\n" +
				"</soapenv:Envelope>";
		log(request);
		String username = utils.getUsername();
		String password = utils.getPassword();
		String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
		String response = WSClientUtils.callWebService(request, endpoint, username, password);
        log(response);
        RET_MSG ret_msg = getRET_MSG(response);
        if (ret_msg == null) {
            requestInfo.getRequestManager().setMessageid("Message");
            requestInfo.getRequestManager().setMessagecontent(response);
        } else {
            if (ret_msg.getMSG_TYPE().equals("E")) {
                requestInfo.getRequestManager().setMessageid("Error Message");
                requestInfo.getRequestManager().setMessagecontent(ret_msg.getMESSAGE());
            }
        }
		return Action.SUCCESS;
	}

	private String getLine(String ypmc) {
		RecordSet recordSet = new RecordSet();
//		RecordSet rs2 = new RecordSet();
		String sql = "select * from "+getUf()+" where id='" + ypmc + "'";
//		String sql2 = "select b.jyfwbh jyfw from uf_sdwrypzl a left join uf_sdwrypzl b on ";
		writeLog("药品首营sql： " + sql);
		recordSet.executeSql(sql);
		StringBuilder stringBuilder = new StringBuilder();
		if (recordSet.next()) {
			stringBuilder.append("<lifnr>").append(Util.null2String(recordSet.getString("gc"))).append("</lifnr>");
			stringBuilder.append("<matnr>").append(Util.null2String(recordSet.getString("ypbh"))).append("</matnr>");
			stringBuilder.append("<ZMAKTX_GYM>").append(Util.null2String(recordSet.getString("gym"))).append("</ZMAKTX_GYM>");
			stringBuilder.append("<ZMAKTX_SPM>").append(Util.null2String(recordSet.getString("spbm"))).append("</ZMAKTX_SPM>");
			stringBuilder.append("<ZCPJIX>").append(getJx(recordSet.getString("jx"))).append("</ZCPJIX>");
			stringBuilder.append("<ZBZGG>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZBZGG>");
			stringBuilder.append("<ZSCQY>").append(Util.null2String(recordSet.getString("scqy"))).append("</ZSCQY>");
			stringBuilder.append("<ZZCSB>").append(Util.null2String(recordSet.getString("zcsb"))).append("</ZZCSB>");
			stringBuilder.append("<ZPZWH>").append(Util.null2String(recordSet.getString("pzwh"))).append("</ZPZWH>");
			stringBuilder.append("<ZGMP_BH>").append(Util.null2String(recordSet.getString("gmpzsh"))).append("</ZGMP_BH>");
			stringBuilder.append("<ZGMP_FW>").append(Util.null2String(recordSet.getString("gmpfw"))).append("</ZGMP_FW>");
			stringBuilder.append("<ZZCTJ>").append(Util.null2String(recordSet.getString("cctj"))).append("</ZZCTJ>");
			stringBuilder.append("<ZYHZQ>").append(Util.null2String(recordSet.getString("yhzq"))).append("</ZYHZQ>");
			stringBuilder.append("<ZYXQ_CP>").append(Util.null2String(recordSet.getString("yxqy"))).append("</ZYXQ_CP>");
			stringBuilder.append("<ZTS_JXQSD>").append(Util.null2String(recordSet.getString("sxsdts1"))).append("</ZTS_JXQSD>");
			stringBuilder.append("<ZJYFW>").append(Util.null2String(recordSet.getString("lb1"))).append("</ZJYFW>");
			stringBuilder.append("<ZDZJGM>").append(Util.null2String(recordSet.getString("sfjdzjgm"))).append("</ZDZJGM>");
			stringBuilder.append("<ZYFBS_BZ>").append(Util.null2String(recordSet.getString("bzyfbs"))).append("</ZYFBS_BZ>");
			stringBuilder.append("<ZLENGTH>").append(Util.null2String(recordSet.getString("cd"))).append("</ZLENGTH>");
			stringBuilder.append("<ZHIGH>").append(Util.null2String(recordSet.getString("gd"))).append("</ZHIGH>");
			stringBuilder.append("<ZDBJS_BZ>").append(Util.null2String(recordSet.getString("bzdbjs"))).append("</ZDBJS_BZ>");
			stringBuilder.append("<ZRY_ZGY>").append(Util.null2String(recordSet.getString("zlgly"))).append("</ZRY_ZGY>");
			stringBuilder.append("<ZRY_BGY>").append(Util.null2String(recordSet.getString("bgy"))).append("</ZRY_BGY>");
			stringBuilder.append("<ZSRYS>").append(Util.null2String(recordSet.getString("sfsrys"))).append("</ZSRYS>");
			stringBuilder.append("<ZCSJG>").append(Util.null2String(recordSet.getString("csjgsj"))).append("</ZCSJG>");
			stringBuilder.append("<ZZHWL>").append(Util.null2String(recordSet.getString("yhzq"))).append("</ZZHWL>");
			stringBuilder.append("<ZZHWL_CW>").append(Util.null2String(recordSet.getString("cwzspzh"))).append("</ZZHWL_CW>");
			String ZZHXS_CW = Util.null2String(recordSet.getString("cwzsxs"));
			stringBuilder.append("<ZZHXS_CW>").append(ZZHXS_CW.equals("") ? "0.00" : ZZHXS_CW).append("</ZZHXS_CW>");
			stringBuilder.append("<BNAME>").append(Util.null2String(recordSet.getString("zdr"))).append("</BNAME>");
			stringBuilder.append("<ZCPMC>").append(Util.null2String(recordSet.getString("tymc"))).append("</ZCPMC>");
			stringBuilder.append("<MAKTX>").append(Util.null2String(recordSet.getString("wlms"))).append("</MAKTX>");
			stringBuilder.append("<ZMAKTX_YWM>").append(Util.null2String(recordSet.getString("ywm"))).append("</ZMAKTX_YWM>");
			stringBuilder.append("<ZGUIGE>").append(Util.null2String(recordSet.getString("gg"))).append("</ZGUIGE>");
			stringBuilder.append("<ZXINGZ>").append(Util.null2String(recordSet.getString("xz"))).append("</ZXINGZ>");
			stringBuilder.append("<ZBWTSCDW>").append(Util.null2String(recordSet.getString("bwtscqy"))).append("</ZBWTSCDW>");
			stringBuilder.append("<ZZLBZ>").append(Util.null2String(recordSet.getString("zlbz"))).append("</ZZLBZ>");
			stringBuilder.append("<ZPZWH_YXQ>").append(Util.null2String(recordSet.getString("pzwhyxqz"))).append("</ZPZWH_YXQ>");
			stringBuilder.append("<ZGMP_YQX>").append(Util.null2String(recordSet.getString("gmpzsyxq"))).append("</ZGMP_YQX>");
			stringBuilder.append("<LGORT>").append(Util.null2String(recordSet.getString("ckdd"))).append("</LGORT>");
			stringBuilder.append("<ZBZPSL>").append(Util.null2String(recordSet.getString("bzpzxdws"))).append("</ZBZPSL>");
			stringBuilder.append("<ZSXBJTS>").append(Util.null2String(recordSet.getString("sxbjts"))).append("</ZSXBJTS>");
			stringBuilder.append("<ZLSJG>").append(Util.null2String(recordSet.getString("lsj"))).append("</ZLSJG>");
			stringBuilder.append("<ZWIDE>").append(Util.null2String(recordSet.getString("kd"))).append("</ZWIDE>");
			stringBuilder.append("<ZWEIGHT>").append(Util.null2String(recordSet.getString("zl"))).append("</ZWEIGHT>");
			stringBuilder.append("<ZFPBM>").append(Util.null2String(recordSet.getString("fpbm"))).append("</ZFPBM>");
			stringBuilder.append("<ZRY_YSY>").append(Util.null2String(recordSet.getString("ysy"))).append("</ZRY_YSY>");
			stringBuilder.append("<ZRY_FHY>").append(Util.null2String(recordSet.getString("fhy"))).append("</ZRY_FHY>");
			stringBuilder.append("<ZLCP>").append(Util.null2String(recordSet.getString("sflc"))).append("</ZLCP>");
			stringBuilder.append("<ZSRFH>").append(Util.null2String(recordSet.getString("sfsrfh"))).append("</ZSRFH>");
			String status = Util.null2String(recordSet.getString("zt"));
			stringBuilder.append("<ZSTATE>").append(status.equals("0") ? "Y" : "N").append("</ZSTATE>");
			String ZZHXS =  Util.null2String(recordSet.getString("ywzsxs"));
			stringBuilder.append("<ZZHXS>").append(ZZHXS.equals("") ? "0.00" : ZZHXS).append("</ZZHXS>");
			stringBuilder.append("<ZZHWL_MC>").append(Util.null2String(recordSet.getString("cwzspzm"))).append("</ZZHWL_MC>");
			String ZDWJG_CW =  Util.null2String(recordSet.getString("cwdwjg"));
			stringBuilder.append("<ZDWJG_CW>").append(ZDWJG_CW.equals("")?"0.00":ZDWJG_CW).append("</ZDWJG_CW>");
			stringBuilder.append("<ZSDYY>").append(Util.null2String(recordSet.getString("sdyy"))).append("</ZSDYY>");
			stringBuilder.append("<ZDATE>").append(Util.null2String(recordSet.getString("lrsj"))).append("</ZDATE>");
		}
		return stringBuilder.toString();
	}
	private void log(Object o) {
		String prefix = "<" + this.getClass().getName() + ">";
		System.out.println(prefix + o);
		writeLog(prefix + o);
	}
	
//	private String JYFW(String ypmc) {
//		String sql = "select jyfwbh from uf_sdwrypzl_dt1 where mainid=" + ypmc;
//		RecordSet recordSet = new RecordSet();
//		recordSet.executeSql(sql);
//		String jyfw = "";
//		while (recordSet.next()) {
//			jyfw = "," + Util.null2String(recordSet.getString("jyfwbh"));
//		}
//		jyfw = jyfw.substring(1);

	public DrugInformation() {
		super();
	}
//		return jyfw;
//	}
	
	private String getJx(String jx){
		String name = "";
		RecordSet rs = new RecordSet();
		String sql = "select selectname from workflow_selectitem where (selectvalue='"+jx+"' and fieldid = (select id from workflow_billfield where fieldname='jx' and billid=(select id from workflow_bill where tablename='"+getUf()+"')))";
		rs.executeSql(sql);
		if(rs.next()){
			name = Util.null2String(rs.getString(1));
		}
		return name;
	}

	private RET_MSG getRET_MSG(String s) {
		RET_MSG ret_msg = null;
		Document dom;
		try {
			dom = DocumentHelper.parseText(s);
			Element root = dom.getRootElement();
			Element ele = root.element("Body").element("MT_DrugInformation_Msg");
			ret_msg = new RET_MSG();
			ret_msg.setMSG_TYPE(ele.elementText("MESSAGETYPE"));
			ret_msg.setMESSAGE(ele.elementText("MESSAGE"));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return ret_msg;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}
}
	