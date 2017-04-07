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
import weaverjn.utils.WSClientUtils;

/**
 * Created by zhaiyaqi on 2017/4/1.
 */
public class ConsigneeInfoAction extends BaseBean implements Action {
    private String vkorg;
    @Override
    public String execute(RequestInfo requestInfo) {
        String billId = requestInfo.getRequestid();
        String moduleId = requestInfo.getWorkflowid();
        String sql = "select b.tablename,b.id from modeinfo a,workflow_bill b where a.formid = b.id and a.id = " + moduleId;
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            String t = recordSet.getString("tablename");
            sql = "select * from " + t + " where id=" + billId;
            recordSet.executeSql(sql);

            String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_ConsigneeInfo>\n" +
                    "         <ControlInfo>\n" +
                    "            <INTF_ID></INTF_ID>\n" +
                    "            <Src_System>OA</Src_System>\n" +
                    "            <Dest_System>SAPERP</Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time></Send_Time>\n" +
                    "         </ControlInfo>\n" +
                    "         <VKORG>" + this.vkorg + "</VKORG>\n" +
                    "         <KUNNR>" + Util.null2String(recordSet.getString("ghdwbh")) + "</KUNNR>\n" +
                    "         <ZSQ_BGN>" + Util.null2String(recordSet.getString("sqqrq")) + "</ZSQ_BGN>\n" +
                    "         <ZSQ_END>" + Util.null2String(recordSet.getString("sqzrq")) + "</ZSQ_END>\n" +
                    "         <ZSHR_SFZ>" + Util.null2String(recordSet.getString("shrsfz")) + "</ZSHR_SFZ>\n" +
                    "         <ZSFZ_YXQ>" + Util.null2String(recordSet.getString("sfzyxq")) + "</ZSFZ_YXQ>\n" +
                    "         <ZGHDWGZ>" + (Util.null2String(recordSet.getString(" ywghdw")).equals("0") ? "Y" : "N") + "</ZGHDWGZ>\n" +
                    "         <ZJSHYJ>" + (Util.null2String(recordSet.getString(" hwjsh")).equals("0") ? "Y" : "N") + "</ZJSHYJ>\n" +
                    "         <ZSFZFYJ>" + (Util.null2String(recordSet.getString(" ywsfzfyj")).equals("0") ? "Y" : "N") + "</ZSFZFYJ>\n" +
                    "         <ZFYJGZ>" + (Util.null2String(recordSet.getString(" sfzfyj")).equals("0") ? "Y" : "N") + "</ZFYJGZ>\n" +
                    "         <NAME_LAST></NAME_LAST>\n" +
                    "         <PSTLZ></PSTLZ>\n" +
                    "         <ORT01>" + Util.null2String(recordSet.getString("sqqy")) + "</ORT01>\n" +
                    "         <LAND1>CN</LAND1>\n" +
                    "      </erp:MT_ConsigneeInfo>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_ConsigneeInfo_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
            String username = "zappluser_oa";
            String password = "a1234567";
            String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, url, username, password);
            MT_ConsigneeInfo_Msg msg = parse(soapHttpResponse);
            String reg_msg = "";
            if (msg != null) {
                reg_msg += msg.getMESSAGE_TYPE();
                reg_msg += "\n" + msg.getMESSAGE();
                sql = "update " + t + " set reg_msg='" + reg_msg + "' shrbh='" + msg.getZSHR_BM() + "' where id=" + billId;
            } else {
                reg_msg += soapHttpResponse;
                sql = "update " + t + " set reg_msg='" + reg_msg + "' where id=" + billId;
            }
            recordSet.executeSql(sql);
        }
        return SUCCESS;
    }

    private MT_ConsigneeInfo_Msg parse(String response) {
        MT_ConsigneeInfo_Msg msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_ConsigneeInfo_Msg");
            msg = new MT_ConsigneeInfo_Msg();
            msg.setMESSAGE_TYPE(e.elementText("MESSAGE_TYPE"));
            msg.setMESSAGE(e.elementText("MESSAGE"));
            msg.setZSHR_BM(e.elementText("ZSHR_BM"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public String getVkorg() {
        return vkorg;
    }

    public void setVkorg(String vkorg) {
        this.vkorg = vkorg;
    }

    class MT_ConsigneeInfo_Msg {
        private String MESSAGE_TYPE;
        private String MESSAGE;
        private String ZSHR_BM;

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

        public String getZSHR_BM() {
            return ZSHR_BM;
        }

        public void setZSHR_BM(String ZSHR_BM) {
            this.ZSHR_BM = ZSHR_BM;
        }
    }

}
