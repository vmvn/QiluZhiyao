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

import java.util.HashMap;

/**
 * Created by zhaiyaqi on 2017/3/2.
 */
public class MaintenanceApplicationAction extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo requestInfo) {
        String src = requestInfo.getRequestManager().getSrc();
        if (!src.equals("reject")) {
            String tablename = utils.getTableName(requestInfo.getWorkflowid());
            String requestid = requestInfo.getRequestid();
            RecordSet recordSet = new RecordSet();
            String sql = "select * from " + tablename + " where requestid=" + requestid;
            recordSet.executeSql(sql);
            recordSet.next();
            String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_Maintain_Order_Req>\n" +
                    "         <ControlInfo>\n" +
                    "            <INTF_ID></INTF_ID>\n" +
                    "            <Src_System>OA</Src_System>\n" +
                    "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time></Send_Time>\n" +
                    "         </ControlInfo>\n" +
                    "         <WORKFLOW_NO>" + requestid + "</WORKFLOW_NO>\n" +
                    "         <ORDER_TYPE>" + utils.getFieldValue("uf_sapjcsj_gdlx", "gdlx", Util.null2String(recordSet.getString("gdlx"))) + "</ORDER_TYPE>\n" +
                    "         <REQUISITOR>" + utils.getFieldValue("hrmresource", "lastname", Util.null2String(recordSet.getString("sqr"))) + "</REQUISITOR>\n" +
                    "         <ORDER_DESC>" + requestInfo.getRequestManager().getRequestname() + "</ORDER_DESC>\n" +
                    "         <EQUNR>" + Util.null2String(recordSet.getString("sbbh")) + "</EQUNR>\n" +
                    "         <TPLNR>" + Util.null2String(recordSet.getString("gnwz")) + "</TPLNR>\n" +
                    "         <ESTIMATED_COST>" + Util.null2String(recordSet.getString("yjfy")) + "</ESTIMATED_COST>\n" +
                    "      </erp:MT_Maintain_Order_Req>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
            String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_Maintain_Order_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
            httpHeaderParm.put("instId", "10062");
            httpHeaderParm.put("repairType", "RP");
            String soapHttpResponse = WSClientUtils.callWebServiceWithHttpHeaderParm(soapHttpRequest, url, httpHeaderParm);
            MT_Maintain_Order_Ret ret = parse(soapHttpResponse);
            if (ret != null) {
                if (ret.getMSG_TYPE().equals("S")) {
                    sql = "update " + tablename + " set gdh='" + ret.getORDER_NO() + "',cgsqh='" + ret.getREQ_NO() + "' where requestid=" + requestid;
                    recordSet.executeSql(sql);
                } else {
                    requestInfo.getRequestManager().setMessageid("Message");
                    requestInfo.getRequestManager().setMessagecontent(ret.getMESSAGE());
                }
            } else {
                requestInfo.getRequestManager().setMessageid("Message");
                requestInfo.getRequestManager().setMessagecontent("null");
            }
        }
        return SUCCESS;
    }

    private MT_Maintain_Order_Ret parse(String soapHttpResponse) {
        MT_Maintain_Order_Ret ret = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(soapHttpResponse);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_Maintain_Order_Ret");
            ret = new MT_Maintain_Order_Ret();
            ret.setMSG_TYPE(e.element("RetMsg").elementText("MSG_TYPE"));
            ret.setMESSAGE(e.element("RetMsg").elementText("MESSAGE"));
            ret.setWORKFLOW_NO(e.elementText("WORKFLOW_NO"));
            ret.setORDER_NO(e.elementText("ORDER_NO"));
            ret.setREQ_NO(e.elementText("REQ_NO"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ret;
    }

    class MT_Maintain_Order_Ret {
        private String MSG_TYPE;
        private String MESSAGE;
        private String WORKFLOW_NO;
        private String ORDER_NO;
        private String REQ_NO;

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

        public String getWORKFLOW_NO() {
            return WORKFLOW_NO;
        }

        public void setWORKFLOW_NO(String WORKFLOW_NO) {
            this.WORKFLOW_NO = WORKFLOW_NO;
        }

        public String getORDER_NO() {
            return ORDER_NO;
        }

        public void setORDER_NO(String ORDER_NO) {
            this.ORDER_NO = ORDER_NO;
        }

        public String getREQ_NO() {
            return REQ_NO;
        }

        public void setREQ_NO(String REQ_NO) {
            this.REQ_NO = REQ_NO;
        }
    }
}
