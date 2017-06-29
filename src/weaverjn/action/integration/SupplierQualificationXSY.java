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
import weaver.workflow.request.RequestManager;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

import java.util.Map;

public class SupplierQualificationXSY extends BaseBean implements Action  {
	private String EKORG;
	private String p1;
	private String p2;
	private String _dt;
	private String uf1;//uf_ghdzl
	private String uf2;//uf_sdwrghdwxsyzl

	@Override
	public String execute(RequestInfo requestInfo) {
		writeLog("run " + this.getP1());
		RequestManager requestManager = requestInfo.getRequestManager();
		if (!Util.null2String(this.getP1()).isEmpty() && !Util.null2String(this.getEKORG()).isEmpty()) {
			Map<String, String> mainTableData = utils.getMainTableData(requestInfo.getMainTableInfo());
			String table = requestManager.getBillTableName();
			String requestId = requestInfo.getRequestid();

			String LIFNR;
			if (this.getP1().equals("1")) {
				LIFNR = utils.getFieldValue(uf1, "ghdwbm", mainTableData.get(Util.null2String(this.getP2()).equals("") ? "ghfmc" : this.getP2()));//供应商编码
			} else {
				writeLog("p2 " + this.getP2());
				LIFNR = utils.getFieldValue(uf2, "bwtrbh", mainTableData.get(Util.null2String(this.getP2()).equals("") ? "bwtrxm" : this.getP2()));//供应商编码
			}
			String id = utils.getFieldValue(table, "id", "requestid", requestId);

			String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <erp:MT_Supplier_Qualification_Req>\n" +
					"         <ControlInfo>\n" +
					"            <INTF_ID></INTF_ID>\n" +
					"            <Src_System>OA</Src_System>\n" +
					"            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
					"            <Company_Code></Company_Code>\n" +
					"            <Send_Time></Send_Time>\n" +
					"         </ControlInfo>\n" +
					"         <LIFNR_TYPE>" + this.getP1() + "</LIFNR_TYPE>\n" +
					"         <EKORG>" + this.getEKORG() + "</EKORG>\n" +
					"         <LIFNR>" + LIFNR + "</LIFNR>\n" +
					"         <SPERM>Y</SPERM>\n" +
					"         <DATE_BEG></DATE_BEG>\n" +
					"         <DATE_END>" + (this.getP1().equals("2") ? Util.null2String(this.getP2()).isEmpty() ? mainTableData.get("wtyxqz") : getDATE_END(id, table + "_dt1") : "") + "</DATE_END>\n" +
					"      </erp:MT_Supplier_Qualification_Req>\n" +
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
					requestInfo.getRequestManager().setMessageid("SAP 返回信息");
					requestInfo.getRequestManager().setMessagecontent(ret_msg.getMESSAGE());
				}
			}
		} else {
			requestManager.setMessageid("WARNING");
			requestManager.setMessagecontent("Action 参数不完整！");
		}
		return Action.SUCCESS;
	}

	private String JYFW(String id, String table) {
		String sql = "select jyfw from " + table + " where mainid='" + id + "'";
		RecordSet recordSet = new RecordSet();
		recordSet.executeSql(sql);
		StringBuilder stringBuilder = new StringBuilder();
		while (recordSet.next()) {
			stringBuilder.append("         <Deal_Scpoe>\n")
					.append("            <SCPOE_ID>").append(Util.null2String(recordSet.getString("jyfw"))).append("</SCPOE_ID>\n")
					.append("         </Deal_Scpoe>\n");
		}
		return stringBuilder.toString();
	}

	private String Material_List(String id, String table) {
		String sql = "select wtpz from " + table + " where mainid='" + id + "'";
		RecordSet recordSet = new RecordSet();
		recordSet.executeSql(sql);
		StringBuilder s = new StringBuilder();
		while (recordSet.next()) {
			s.append("         <Material_List>\n")
					.append("            <MATNR>").append(Util.null2String(recordSet.getString("wtpz"))).append("</MATNR>\n")
					.append("         </Material_List>");
		}
		return s.toString();
	}

	private String getDATE_END(String id, String table) {
		String sql = "select bghnr from " + table + " where mainid='" + id + "' and bgxm='wtyxqz'";
		RecordSet recordSet = new RecordSet();
		recordSet.executeSql(sql);
		String date = "";
		if (recordSet.next()) {
			date = Util.null2String(recordSet.getString("bghnr"));
		}
		return date;
	}

	private RET_MSG getRET_MSG(String s) {
        RET_MSG ret_msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(s);
            Element root = dom.getRootElement();
            Element MT_MAT_MDG_RET = root.element("Body").element("MT_Supplier_Qualification_Ret");
            ret_msg = new RET_MSG();
            ret_msg.setMSG_TYPE(MT_MAT_MDG_RET.element("Message_Info").elementText("MSG_TYPE"));
            ret_msg.setMESSAGE(MT_MAT_MDG_RET.element("Message_Info").elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ret_msg;
    }

	public String getEKORG() {
		return EKORG;
	}

	public void setEKORG(String EKORG) {
		this.EKORG = EKORG;
	}

	public String getP1() {
		return p1;
	}

	public void setP1(String p1) {
		this.p1 = p1;
	}

	public String getP2() {
		return p2;
	}

	public void setP2(String p2) {
		this.p2 = p2;
	}

	public String get_dt() {
		return _dt;
	}

	public void set_dt(String _dt) {
		this._dt = _dt;
	}

	public String getUf1() {
		return uf1;
	}

	public void setUf1(String uf1) {
		this.uf1 = uf1;
	}

	public String getUf2() {
		return uf2;
	}

	public void setUf2(String uf2) {
		this.uf2 = uf2;
	}
}
