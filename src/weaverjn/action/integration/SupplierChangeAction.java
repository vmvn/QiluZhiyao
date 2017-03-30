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

import java.util.ArrayList;

/**
 * Created by zhaiyaqi on 2017/3/28.
 */
public class SupplierChangeAction extends BaseBean implements Action {
    private String type;
    private String ekorg;
    private ArrayList<String> sqls;
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if (!src.equals("reject")) {
            String workflowId = requestInfo.getWorkflowid();
            String requestId = requestInfo.getRequestid();
            String t = utils.getTableName(workflowId);//主表表名

            String sql = "select id, ghdwbm from " + t + " where requestid=" + requestId;
            RecordSet recordSet = new RecordSet();
            recordSet.executeSql(sql);
            recordSet.next();
            String id = recordSet.getString("id");
            String ghdwbm = Util.null2String(recordSet.getString("ghdwbm"));


            String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_SupplierChange>\n" +
                    "         <ControlInfo>\n" +
                    "            <INTF_ID></INTF_ID>\n" +
                    "            <Src_System>OA</Src_System>\n" +
                    "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time></Send_Time>\n" +
                    "         </ControlInfo>\n" +
                    "         <lifnr>" + Util.null2String(recordSet.getString("ghdwbm")) + "</lifnr>\n" +
                    "         <ekorg>" + this.ekorg + "</ekorg>\n" +
                    "         <type>" + this.type + "</type>\n" +
                    "         <!--1 or more repetitions:-->\n" +
                    getLines(t, id, ghdwbm) +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_SupplierChange_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
            String response = WSClientUtils.callWebService(soapHttpRequest, url, "zappluser_oa", "a1234567");
            writeLog(soapHttpRequest);
            writeLog(response);
            MT_SupplierChange_Msg msg = parse(response);
            if (msg != null) {
                if (!msg.getMSG_TYPE().equals("S")) {
                    requestManager.setMessageid(msg.getMSG_TYPE());
                    requestManager.setMessagecontent(msg.getMESSAGE());
                } else {
                    for (String s : this.sqls) {
                        recordSet.executeSql(s);
                    }
                }
            } else {
                requestManager.setMessageid("response error");
                requestManager.setMessagecontent(response);
            }
        }
        return SUCCESS;
    }

    private String getLines(String t, String id, String ghdwbm) {
        String sql = "select * from " + t + "_dt1 where mainid=" + id;
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        StringBuilder lines = new StringBuilder();
        while (recordSet.next()) {
            String bgkhxm = recordSet.getString("bgkhxm");
            String bghnr = recordSet.getString("bghnr");
            lines.append("         <fieldlist>\n")
                    .append("            <fieldname>").append(bgkhxm).append("</fieldname>\n")
                    .append("            <value>").append(bghnr).append("</value>\n")
                    .append("         </fieldlist>\n");
            sql = "update uf_ghdzl set " + bgkhxm + "='" + bghnr + "' where ghdwbm='" + ghdwbm + "'";
            this.sqls.add(sql);
        }
        return lines.toString();
    }

    private MT_SupplierChange_Msg parse(String response) {
        MT_SupplierChange_Msg msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_SupplierChange_Msg");
            msg = new MT_SupplierChange_Msg();
            msg.setMSG_TYPE(e.elementText("MSG_TYPE"));
            msg.setMESSAGE(e.elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEkorg() {
        return ekorg;
    }

    public void setEkorg(String ekorg) {
        this.ekorg = ekorg;
    }

    class MT_SupplierChange_Msg {
        private String MSG_TYPE;
        private String MESSAGE;

        public String getMSG_TYPE() {
            return MSG_TYPE;
        }

        public void setMSG_TYPE(String MSG_TYPE) {
            this.MSG_TYPE = MSG_TYPE;
        }

        public String getMESSAGE() {
            return MESSAGE;
        }

        public void setMESSAGE(String MESSAGE) {
            this.MESSAGE = MESSAGE;
        }
    }
}
