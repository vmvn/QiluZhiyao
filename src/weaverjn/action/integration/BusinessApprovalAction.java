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

/**
 * Created by zhaiyaqi on 2017/3/28.
 */
public class BusinessApprovalAction extends BaseBean implements Action {
    private String ekorg;
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if (!src.equals("reject")) {
            String workflowId = requestInfo.getWorkflowid();
            String requestId = requestInfo.getRequestid();
            String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowId;
            RecordSet recordSet = new RecordSet();
            recordSet.executeSql(sql);
            recordSet.next();
            String t = recordSet.getString("tablename");//主表表名

            sql = "select * from " + t + " where requestid=" + requestId;
            recordSet.executeSql(sql);
            recordSet.next();
            String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_BusinessApproval>\n" +
                    "         <ControlInfo>\n" +
                    "            <INTF_ID></INTF_ID>\n" +
                    "            <Src_System>OA</Src_System>\n" +
                    "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time></Send_Time>\n" +
                    "         </ControlInfo>\n" +
                    "         <lifnr>" + Util.null2String(recordSet.getString("gysbh")) + "</lifnr>\n" +
//                    "         <ekorg>" + util.slice(Util.null2String(recordSet.getString("cgzz")), 1) + "</ekorg>\n" +
                    "         <ekorg>" + this.ekorg + "</ekorg>\n" +
                    "         <nickname>" + utils.getFieldValue("uf_ghdzl", "ghfmc", Util.null2String(recordSet.getString("ghfmc"))) + "</nickname>\n" +
                    "         <zyyzz_yxq>" + Util.null2String(recordSet.getString("njrq")) + "</zyyzz_yxq>\n" +
                    "         <zxkz_yxq>" + Util.null2String(recordSet.getString("yxqz")) + "</zxkz_yxq>\n" +
                    "         <zkhzt></zkhzt>\n" +
                    "         <zzzjgda_yxq>" + Util.null2String(recordSet.getString("yxqz1")) + "</zzzjgda_yxq>\n" +
                    "         <ZZBXY_YXQ>" + Util.null2String(recordSet.getString("yxqz2")) + "</ZZBXY_YXQ>\n" +
                    "         <ZDTXQ></ZDTXQ>\n" +
                    "         <ZNJRQ>" + Util.null2String(recordSet.getString("njrq1")) + "</ZNJRQ>\n" +
                    "         <ZSPRQ>" + utils.getCurrentDate() + "</ZSPRQ>\n" +
                    "         <ZSTATE></ZSTATE>\n" +
                    "         <ZSTATE_HG></ZSTATE_HG>\n" +
                    "      </erp:MT_BusinessApproval>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            writeLog(soapHttpRequest);
            String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_BusinessApproval_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
            String response = WSClientUtils.callWebService(soapHttpRequest, url, "zappluser_oa", "a1234567");
            writeLog(response);
            MT_BusinessApproval_Msg msg = parse(response);
            if (msg != null) {
                if (!msg.getMESSAGE_TYPE().equals("S")) {
                    requestManager.setMessageid(msg.getMESSAGE_TYPE());
                    requestManager.setMessagecontent(msg.getMESSAGE());
                }
            } else {
                requestManager.setMessageid("response error");
                requestManager.setMessagecontent(response);
            }
        }
        return SUCCESS;
    }

    private MT_BusinessApproval_Msg parse(String response) {
        MT_BusinessApproval_Msg msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_BusinessApproval_Msg");
            msg = new MT_BusinessApproval_Msg();
            msg.setMESSAGE_TYPE(e.elementText("MESSAGE_TYPE"));
            msg.setMESSAGE(e.elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public String getEkorg() {
        return ekorg;
    }

    public void setEkorg(String ekorg) {
        this.ekorg = ekorg;
    }

    class MT_BusinessApproval_Msg {
        private String MESSAGE_TYPE;
        private String MESSAGE;

        public String getMESSAGE_TYPE() {
            return MESSAGE_TYPE;
        }

        public void setMESSAGE_TYPE(String MESSAGE_TYPE) {
            this.MESSAGE_TYPE = MESSAGE_TYPE;
        }

        public String getMESSAGE() {
            return MESSAGE;
        }

        public void setMESSAGE(String MESSAGE) {
            this.MESSAGE = MESSAGE;
        }
    }
}
