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
 * Created by zhaiyaqi on 2017/4/1.
 */
public class PurchaserQualificationReviewAction extends BaseBean implements Action{
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if (!src.equals("reject")) {
            String workflowId = requestInfo.getWorkflowid();
            String requestId = requestInfo.getRequestid();
            String t = utils.getTableName(workflowId);//主表名

            RecordSet recordSet = new RecordSet();
            String sql = "select * from " + t + " where requestid=" + requestId;
            recordSet.executeSql(sql);
            recordSet.next();

            String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_PurchaserQualificationReview>\n" +
                    "         <ControlInfo>\n" +
                    "            <INTF_ID></INTF_ID>\n" +
                    "            <Src_System>OA</Src_System>\n" +
                    "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time></Send_Time>\n" +
                    "         </ControlInfo>\n" +
                    "         <KUNNER>" + Util.null2String(recordSet.getString("ghdwbm")) + "</KUNNER>\n" +
                    "         <ZXFCGY_XM>" + utils.getFieldValue("hrmresource", "lastname", Util.null2String(recordSet.getString("cgyxm"))) + "</ZXFCGY_XM>\n" +
                    "         <ZSQ_BGN>" + Util.null2String(recordSet.getString("sqqzrq")) + "</ZSQ_BGN>\n" +
                    "         <ZSQ_END>" + Util.null2String(recordSet.getString("zrq")) + "</ZSQ_END>\n" +
                    "         <ZXFCGY_SFZ>" + Util.null2String(recordSet.getString("cgysfzh")) + "</ZXFCGY_SFZ>\n" +
                    "         <ZSFZ_YXQ>" + Util.null2String(recordSet.getString("sfzyxq")) + "</ZSFZ_YXQ>\n" +
                    "         <ZFDSQR>" + utils.getFieldValue("hrmresource", "lastname", Util.null2String(recordSet.getString("fdsqr"))) + "</ZFDSQR>\n" +
                    "         <ZFRQMQZ>" + (Util.null2String(recordSet.getString("sfyfrqz")).equals("0") ? "Y" : "N") + "</ZFRQMQZ>\n" +
                    "         <ZGHDWGZ>" + (Util.null2String(recordSet.getString("ywghdw")).equals("0") ? "Y" : "N") + "</ZGHDWGZ>\n" +
                    "         <ZWTSYJ>" + (Util.null2String(recordSet.getString("wtssfyj")).equals("0") ? "Y" : "N") + "</ZWTSYJ>\n" +
                    "         <ZSFZFYJ>" + (Util.null2String(recordSet.getString("sfysfzfyj")).equals("0") ? "Y" : "N") + "</ZSFZFYJ>\n" +
                    "         <ZFYJGZ>" + (Util.null2String(recordSet.getString(" sfzfyjyw")).equals("0") ? "Y" : "N") + "</ZFYJGZ>\n" +
                    "      </erp:MT_PurchaserQualificationReview>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_PurchaserQualificationReview_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
            String username = "zappluser_oa";
            String password = "a1234567";
            String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, url, username, password);
            writeLog(soapHttpResponse);
            MT_PurchaserQualificationReview_MSg msg = parse(soapHttpResponse);
            if (msg != null) {
                if (msg.getMESSAGE_TYPE().equals("E")) {
                    requestManager.setMessageid(msg.getMESSAGE_TYPE());
                    requestManager.setMessagecontent(msg.getMESSAGE());
                }
            } else {
                requestManager.setMessageid("ERROR");
                requestManager.setMessagecontent(soapHttpResponse);
            }
        }
        return SUCCESS;
    }

    private MT_PurchaserQualificationReview_MSg parse(String response) {
        MT_PurchaserQualificationReview_MSg msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_PurchaserQualificationReview_MSg");
            msg = new MT_PurchaserQualificationReview_MSg();
            msg.setMESSAGE_TYPE(e.elementText("MESSAGE_TYPE"));
            msg.setMESSAGE(e.elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    class MT_PurchaserQualificationReview_MSg {
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
