package weaverjn.action.integration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Property;
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
		String sql = "select * from uf_sdwrypzl where id='" + ypmc + "'";
		recordSet.executeSql(sql);
		StringBuilder stringBuilder = new StringBuilder();
		if (recordSet.next()) {
			stringBuilder.append("<lifnr>").append(utils.getFieldValue("uf_sapjcsj_gc", "gcbm", Util.null2String(recordSet.getString("gc")))).append("</lifnr>");
			stringBuilder.append("<matnr>").append(Util.null2String(recordSet.getString("ypbh"))).append("</matnr>");
			stringBuilder.append("<ZMAKTX_GYM>").append(Util.null2String(recordSet.getString("gym"))).append("</ZMAKTX_GYM>");
			stringBuilder.append("<ZMAKTX_SPM>").append(Util.null2String(recordSet.getString("spbm"))).append("</ZMAKTX_SPM>");
			stringBuilder.append("<ZCPJIX>").append(utils.getSelectName("26605", recordSet.getString("jx"))).append("</ZCPJIX>");
			stringBuilder.append("<ZBZGG>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZBZGG>");
			stringBuilder.append("<ZSCQY>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZSCQY>");
			stringBuilder.append("<ZZCSB>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZZCSB>");
			stringBuilder.append("<ZPZWH>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZPZWH>");
			stringBuilder.append("<ZGMP_BH>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZGMP_BH>");
			stringBuilder.append("<ZGMP_FW>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZGMP_FW>");
			stringBuilder.append("<ZZCTJ>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZZCTJ>");
			stringBuilder.append("<ZYHZQ>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZYHZQ>");
			stringBuilder.append("<ZYXQ_CP>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZYXQ_CP>");
			stringBuilder.append("<ZTS_JXQSD>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZTS_JXQSD>");
			stringBuilder.append("<ZJYFW>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZJYFW>");
			stringBuilder.append("<ZDZJGM>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZDZJGM>");
			stringBuilder.append("<ZYFBS_BZ>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZYFBS_BZ>");
			stringBuilder.append("<ZLENGTH>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZLENGTH>");
			stringBuilder.append("<ZHIGH>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZHIGH>");
			stringBuilder.append("<ZDBJS_BZ>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZDBJS_BZ>");
			stringBuilder.append("<ZRY_ZGY>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZRY_ZGY>");
			stringBuilder.append("<ZRY_BGY>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZRY_BGY>");
			stringBuilder.append("<ZSRYS>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZSRYS>");
			stringBuilder.append("<ZCSJG>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZCSJG>");
			stringBuilder.append("<ZZHWL>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZZHWL>");
			stringBuilder.append("<ZZHWL_CW>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZZHWL_CW>");
			stringBuilder.append("<ZZHXS_CW>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZZHXS_CW>");
			stringBuilder.append("<BNAME>").append(Util.null2String(recordSet.getString("bzgg"))).append("</BNAME>");
			stringBuilder.append("<ZCPMC>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZCPMC>");
			stringBuilder.append("<MAKTX>").append(Util.null2String(recordSet.getString("bzgg"))).append("</MAKTX>");
			stringBuilder.append("<ZMAKTX_YWM>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZMAKTX_YWM>");
			stringBuilder.append("<ZGUIGE>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZGUIGE>");
			stringBuilder.append("<ZXINGZ>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZXINGZ>");
			stringBuilder.append("<ZBWTSCDW>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZBWTSCDW>");
			stringBuilder.append("<ZZLBZ>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZZLBZ>");
			stringBuilder.append("<ZPZWH_YXQ>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZPZWH_YXQ>");
			stringBuilder.append("<ZGMP_YQX>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZGMP_YQX>");
			stringBuilder.append("<LGORT>").append(Util.null2String(recordSet.getString("bzgg"))).append("</LGORT>");
			stringBuilder.append("<ZBZPSL>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZBZPSL>");
			stringBuilder.append("<ZSXBJTS>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZSXBJTS>");
			stringBuilder.append("<ZLSJG>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZLSJG>");
			stringBuilder.append("<ZWIDE>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZWIDE>");
			stringBuilder.append("<ZWEIGHT>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZWEIGHT>");
			stringBuilder.append("<ZFPBM>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZFPBM>");
			stringBuilder.append("<ZRY_YSY>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZRY_YSY>");
			stringBuilder.append("<ZRY_FHY>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZRY_FHY>");
			stringBuilder.append("<ZLCP>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZLCP>");
			stringBuilder.append("<ZSRFH>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZSRFH>");
			stringBuilder.append("<ZSTATE>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZSTATE>");
			stringBuilder.append("<ZZHXS>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZZHXS>");
			stringBuilder.append("<ZZHWL_MC>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZZHWL_MC>");
			stringBuilder.append("<ZDWJG_CW>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZDWJG_CW>");
			stringBuilder.append("<ZSDYY>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZSDYY>");
			stringBuilder.append("<ZDATE>").append(Util.null2String(recordSet.getString("bzgg"))).append("</ZDATE>");
		}
		return stringBuilder.toString();
	}
	private void log(Object o) {
		String prefix = "<" + this.getClass().getName() + ">";
		System.out.println(prefix + o);
		writeLog(prefix + o);
	}
	
    private RET_MSG getRET_MSG(String s) {
        RET_MSG ret_msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(s);
            Element root = dom.getRootElement();
            Element MT_MAT_MDG_RET = root.element("Body").element("MT_MAT_MDG_RET");
            ret_msg = new RET_MSG();
            ret_msg.setMSG_TYPE(MT_MAT_MDG_RET.element("RET_MSG").elementText("MSG_TYPE"));
            ret_msg.setMESSAGE(MT_MAT_MDG_RET.element("RET_MSG").elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ret_msg;
    }
}
	