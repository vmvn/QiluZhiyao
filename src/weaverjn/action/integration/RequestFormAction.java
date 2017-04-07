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
import weaverjn.utils.WSClientUtils;

/**
 * Created by zhaiyaqi on 2017/4/7.
 */
public class RequestFormAction extends BaseBean implements Action{
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if (!src.equals("reject")) {
            String workflowId = requestInfo.getWorkflowid();
            String requestId = requestInfo.getRequestid();
            String t = utils.getTableName(workflowId);

            RecordSet recordSet = new RecordSet();
            String sql = "select wlpzhm from " + t + " where requestid=" + requestId;
            recordSet.executeSql(sql);
            recordSet.next();

            String wlpzhm = Util.null2String(recordSet.getString("wlpzhm"));

            String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_RequestForm>\n" +
                    "         <ZKCYP>" + wlpzhm + "</ZKCYP>\n" +
                    "         <ZAPP01>Y</ZAPP01>\n" +
                    "      </erp:MT_RequestForm>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_RequestForm_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
            String username = utils.getUsername();
            String password = utils.getPassword();
            String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, url, username, password);
            MT_RequestForm_Msg msg = parse(soapHttpResponse);
            if (msg != null) {
                if (msg.getMESSAGE_TYPE().equals("E")) {
                    requestManager.setMessageid(msg.getMESSAGE_TYPE());
                    requestManager.setMessagecontent(msg.getMESSAGE());
                }
            } else {
                requestManager.setMessageid("E");
                requestManager.setMessagecontent(soapHttpResponse);
            }
        }
        return SUCCESS;
    }

    private MT_RequestForm_Msg parse(String response) {
        Document document;
        MT_RequestForm_Msg msg = null;
        try{
            document = DocumentHelper.parseText(response);
            Element root = document.getRootElement();
            Element e = root.element("Body").element("MT_RequestForm_Msg");
            msg = new MT_RequestForm_Msg();
            msg.setMESSAGE(e.elementText("MESSAGE"));
            msg.setMESSAGE_TYPE(e.elementText("MESSAGE_TYPE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    class MT_RequestForm_Msg {
        private String MESSAGE;
        private String MESSAGE_TYPE;

        public String getMESSAGE() {
            return MESSAGE;
        }

        public void setMESSAGE(String MESSAGE) {
            this.MESSAGE = MESSAGE;
        }

        public String getMESSAGE_TYPE() {
            return MESSAGE_TYPE;
        }

        public void setMESSAGE_TYPE(String MESSAGE_TYPE) {
            this.MESSAGE_TYPE = MESSAGE_TYPE;
        }
    }
}
