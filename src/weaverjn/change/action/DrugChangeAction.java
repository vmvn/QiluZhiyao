package weaverjn.change.action;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.DetailTable;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.Row;
import weaverjn.action.integration.RET_MSG;
import weaverjn.action.integration.utils;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

/**
 * 药品有效期变更，则将药品解锁 判断变更项目是否为有效期至变更，得到变更前后内容，修改并解锁
 * 
 * @author songqi
 * @tel 13256247773 2017年5月10日 下午4:16:03
 */
public class DrugChangeAction extends BaseBean implements Action {

	private String ypbh;
	private String bgxm;
	private String bgqnr;
	private String bghnr;
	private String uf;
	@Override
	public String execute(RequestInfo requestInfo) {
		List<DrugChangeAction> list = new ArrayList<DrugChangeAction>();
		
		// 取明细数据
		DetailTable[] detailtable = requestInfo.getDetailTableInfo().getDetailTable(); // 获取所有明细表
		if (detailtable.length > 0) {
			for (int i = 0; i < detailtable.length; i++) {
				DetailTable dt = detailtable[i];// 指定明细表
				Row[] s = dt.getRow(); // 当前明细表的所有数据,按行存储
				for (int j = 0; j < s.length; j++) {
					Row r = s[j]; // 指定行
					Cell c[] = r.getCell(); // 每行数据再按列存储
					DrugChangeAction dc = new DrugChangeAction();
					for (int k = 0; k < c.length; k++) {
						Cell c1 = c[k]; // 指定列
						String name = c1.getName(); // 明细字段名称
						String value = c1.getValue(); // 明细字段的值
						if(name.equals("spbh")){
							dc.setYpbh(value);
						}
						if(name.equals("bgxm")){
							dc.setBgxm(value);
						}
						if(name.equals("bgqnr")){
							dc.setBgqnr(value);
						}
						if(name.equals("bghnr")){
							dc.setBghnr(value);
						}
					}
					if(null != dc.getYpbh() && !dc.getYpbh().equals("")){
						list.add(dc);
					}
				}
			}
		}
//		pzwhyxqz  gmpzsyxq
//		writeLog("list的大小：" + list.size() + "\n");
		
		String tag = "erp:MT_DrugInformation";
		
		RecordSet rs = new RecordSet();
		for(DrugChangeAction dc : list){
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
					getLine(dc.getYpbh()) +
					"      </" + tag + ">\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			writeLog("请求：" + request);
			String username = utils.getUsername();
			String password = utils.getPassword();
			String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", "DrugInformation");
			String response = WSClientUtils.callWebService(request, endpoint, username, password);
	        writeLog("sap返回信息： " + response);
			RET_MSG ret_msg = getRET_MSG(response);
	        if (ret_msg == null) {
	            requestInfo.getRequestManager().setMessageid("Message");
	            requestInfo.getRequestManager().setMessagecontent(response);
	        } else {
	            if (ret_msg.getMSG_TYPE().equals("E")) {
	                requestInfo.getRequestManager().setMessageid("sap报错");
	                requestInfo.getRequestManager().setMessagecontent(ret_msg.getMESSAGE());
	            }
	        }
			if(dc.getBgxm().equals("pzwhyxqz") || dc.getBgxm().equals("gmpzsyxq")){
				String sql = "update " + getUf() + " set zt='0'," + dc.getBgxm() + " = '" + dc.getBghnr() + "' where id='" + dc.getYpbh() + "'";
				rs.execute(sql);
				writeLog("药品变更sql： " + sql + "\n");
			}
		}
		return Action.SUCCESS;
	}
	
	private String getLine(String ypmc) {
		RecordSet recordSet = new RecordSet();
		String sql = "select * from "+getUf()+" where id='" + ypmc + "'";
		writeLog("查询建模中的数据： " + sql);
		recordSet.executeSql(sql);
		StringBuilder stringBuilder = new StringBuilder();
		if (recordSet.next()) {
			stringBuilder.append("<lifnr>").append(Util.null2String(recordSet.getString("gc"))).append("</lifnr>");
			stringBuilder.append("<matnr>").append(Util.null2String(recordSet.getString("ypbh"))).append("</matnr>");
			stringBuilder.append("<ZMAKTX_GYM>").append(Util.null2String(recordSet.getString("gym"))).append("</ZMAKTX_GYM>");
			stringBuilder.append("<ZMAKTX_SPM>").append(Util.null2String(recordSet.getString("spbm"))).append("</ZMAKTX_SPM>");
			stringBuilder.append("<ZCPJIX>").append(utils.getSelectName("26605", recordSet.getString("jx"))).append("</ZCPJIX>");
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
	
	public String getYpbh() {
		return ypbh;
	}
	public void setYpbh(String ypbh) {
		this.ypbh = ypbh;
	}
	public String getBgxm() {
		return bgxm;
	}
	public void setBgxm(String bgxm) {
		this.bgxm = bgxm;
	}
	public String getBgqnr() {
		return bgqnr;
	}
	public void setBgqnr(String bgqnr) {
		this.bgqnr = bgqnr;
	}
	public String getBghnr() {
		return bghnr;
	}
	public void setBghnr(String bghnr) {
		this.bghnr = bghnr;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

}
