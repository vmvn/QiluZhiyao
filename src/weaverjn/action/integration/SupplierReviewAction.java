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
public class SupplierReviewAction extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if (!src.equals("reject")) {
            String workflowId = requestInfo.getWorkflowid();
            String requestId = requestInfo.getRequestid();
            String t = utils.getTableName(workflowId);//主表表名

            String sql = "select bh,cgzz from " + t + " where requestid=" + requestId;
            RecordSet recordSet = new RecordSet();
            recordSet.executeSql(sql);
            recordSet.next();
            String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_SupplierReview>\n" +
                    "         <ControlInfo>\n" +
                    "            <INTF_ID></INTF_ID>\n" +
                    "            <Src_System>OA</Src_System>\n" +
                    "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time></Send_Time>\n" +
                    "         </ControlInfo>\n" +
                    "         <lifnr>" + Util.null2String(recordSet.getString("bh")) + "</lifnr>\n" +
                    "         <ekorg>" + utils.slice(Util.null2String(recordSet.getString("cgzz")), 1) + "</ekorg>\n" +
                    "         <status>Y</status>\n" +
                    "      </erp:MT_SupplierReview>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_SupplierReview_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
            String response = WSClientUtils.callWebService(soapHttpRequest, url, "zappluser_oa", "a1234567");
            writeLog(soapHttpRequest);
            writeLog(response);
            MT_SupplierReview_Msg msg = parse(response);
            if (msg != null) {
                if (!msg.getMSG_TYPE().equals("S")) {
                    requestManager.setMessageid(msg.getMSG_TYPE());
                    requestManager.setMessagecontent(msg.getMESSAGE());
                }
            } else {
                requestManager.setMessageid("response error");
                requestManager.setMessagecontent(response);
            }
        }
        return SUCCESS;
    }

    private MT_SupplierReview_Msg parse(String response) {
        MT_SupplierReview_Msg msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_SupplierReview_Msg");
            msg = new MT_SupplierReview_Msg();
            msg.setMSG_TYPE(e.elementText("MSG_TYPE"));
            msg.setMESSAGE(e.elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    class MT_SupplierReview_Msg {
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

