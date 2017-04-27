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

/**
 * 物料资质信息
 * @author songqi
 * @tel 13256247773
 * 2017年4月18日 上午10:10:12
 */
public class MaterialQualification extends BaseBean implements Action  {
	@Override
	public String execute(RequestInfo requestInfo) {
		String requestId = requestInfo.getRequestid();
		String tableName = requestInfo.getRequestManager().getBillTableName();
		String sql = "select id from " + tableName + " where requestid=" + requestId;
		RecordSet recordSet = new RecordSet();
		recordSet.executeSql(sql);
		recordSet.next();
		int id = recordSet.getInt("id");
		String dt1 = tableName + "_dt1";
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
				getLine(dt1, id) +
				"      </" + tag + ">\n" +
				"   </soapenv:Body>\n" +
				"</soapenv:Envelope>";
		writeLog(request);
		String username = utils.getUsername();
		String password = utils.getPassword();
		String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
		String response = WSClientUtils.callWebService(request, endpoint, username, password);
		writeLog(response);
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

	private String getLine(String dt1, int id) {
		String sql = "select spbh from " + dt1 + " where mainid=" + id;
		RecordSet recordSet = new RecordSet();
		RecordSet recordSet1 = new RecordSet();
		recordSet.executeSql(sql);
		StringBuilder stringBuilder = new StringBuilder();
		while (recordSet.next()) {
			String spbh = Util.null2String(recordSet.getString("spbh"));
			sql = "select * from uf_sdwrypzl where ypbh='" + spbh + "'";
			recordSet1.executeSql(sql);
			if (recordSet1.next()) {
				stringBuilder.append("<Mater_Qual>\n")
						.append("<MATNR>").append(Util.null2String(recordSet1.getString("ypbh"))).append("</MATNR>\n")
						.append("<WERKS>").append(utils.getFieldValue("uf_sapjcsj_gc", "gcbm", Util.null2String(recordSet1.getString("gc")))).append("</WERKS>\n")
						.append("<SPERM>").append("N").append("</SPERM>\n")
						.append("<DEAL_SCPOE>").append("").append("</DEAL_SCPOE>\n")
						.append("<EXPIRY_DATE_GMP>").append(Util.null2String(recordSet1.getString("gmpzsyxq"))).append("</EXPIRY_DATE_GMP>\n")
						.append("<EXPIRY_DATE_LICENSE>").append(Util.null2String(recordSet.getString("pzwhyxqz"))).append("</EXPIRY_DATE_LICENSE>\n")
						.append("</Mater_Qual>\n");
			}
		}
		return stringBuilder.toString();
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
	