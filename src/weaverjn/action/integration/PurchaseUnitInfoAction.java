package weaverjn.action.integration;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;

/**
 * Created by zhaiyaqi on 2017/4/5.
 */
public class PurchaseUnitInfoAction extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if (!src.equals("reject")) {
            String workflowId = requestInfo.getWorkflowid();
            String requestId = requestInfo.getRequestid();
            String t = utils.getTableName(workflowId);

            RecordSet recordSet = new RecordSet();
            String sql = "select * from " + t + " where requestid=" + requestId;

            recordSet.executeSql(sql);
            recordSet.next();
            String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_PurchaseUnitInfo>\n" +
                    "         <ControlInfo>\n" +
                    "            <INTF_ID></INTF_ID>\n" +
                    "            <Src_System>OA</Src_System>\n" +
                    "            <Dest_System>SAPERP</Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time></Send_Time>\n" +
                    "         </ControlInfo>\n" +
                    "         <kunnr>?</kunnr>\n" +
                    "         <zfddbr>?</zfddbr>\n" +
                    "         <VKORG>?</VKORG>\n" +
                    "         <ZSTATE_HG>?</ZSTATE_HG>\n" +
                    "         <ZYYZZ_YXQ>?</ZYYZZ_YXQ>\n" +
                    "         <ZXKZ_FZRQ>?</ZXKZ_FZRQ>\n" +
                    "         <ZYYZZ_FZRQ>?</ZYYZZ_FZRQ>\n" +
                    "         <ZXKZ_MC>?</ZXKZ_MC>\n" +
                    "         <ZXKZ_YXQ>?</ZXKZ_YXQ>\n" +
                    "         <ZJYFW1>?</ZJYFW1>\n" +
                    "         <ZJYFW2>?</ZJYFW2>\n" +
                    "         <ZJYFW3>?</ZJYFW3>\n" +
                    "         <ZJYFW4>?</ZJYFW4>\n" +
                    "         <ZJYFW5>?</ZJYFW5>\n" +
                    "         <ZJYFW6>?</ZJYFW6>\n" +
                    "         <ZJYFW7>?</ZJYFW7>\n" +
                    "         <ZJYFW8>?</ZJYFW8>\n" +
                    "         <ZJYFW9>?</ZJYFW9>\n" +
                    "         <ZJYFW10>?</ZJYFW10>\n" +
                    "         <ZJYFW11>?</ZJYFW11>\n" +
                    "         <ZJYFW12>?</ZJYFW12>\n" +
                    "         <ZJYFW13>?</ZJYFW13>\n" +
                    "         <ZJYFW14>?</ZJYFW14>\n" +
                    "         <ZJYFW15>?</ZJYFW15>\n" +
                    "         <ZJYFW16>?</ZJYFW16>\n" +
                    "         <ZJYFW17>?</ZJYFW17>\n" +
                    "         <ZJYFW18>?</ZJYFW18>\n" +
                    "         <ZJYFW19>?</ZJYFW19>\n" +
                    "         <ZJYFW20>?</ZJYFW20>\n" +
                    "         <ZXKZ_BGJL>?</ZXKZ_BGJL>\n" +
                    "         <ZCKDZ1>?</ZCKDZ1>\n" +
                    "         <ZCKDZXQ1>?</ZCKDZXQ1>\n" +
                    "         <ZCKDZ2>?</ZCKDZ2>\n" +
                    "         <ZCKDZXQ2>?</ZCKDZXQ2>\n" +
                    "         <ZCKDZ3>?</ZCKDZ3>\n" +
                    "         <ZCKDZXQ3>?</ZCKDZXQ3>\n" +
                    "         <ZCKDZ4>?</ZCKDZ4>\n" +
                    "         <ZCKDZXQ4>?</ZCKDZXQ4>\n" +
                    "         <ZCKDZ5>?</ZCKDZ5>\n" +
                    "         <ZCKDZXQ5>?</ZCKDZXQ5>\n" +
                    "         <ZCKDZ6>?</ZCKDZ6>\n" +
                    "         <ZCKDZXQ6>?</ZCKDZXQ6>\n" +
                    "         <ZCKDZ7>?</ZCKDZ7>\n" +
                    "         <ZCKDZXQ7>?</ZCKDZXQ7>\n" +
                    "         <ZCKDZ8>?</ZCKDZ8>\n" +
                    "         <ZCKDZXQ8>?</ZCKDZXQ8>\n" +
                    "         <ZCKDZ9>?</ZCKDZ9>\n" +
                    "         <ZCKDZXQ9>?</ZCKDZXQ9>\n" +
                    "         <ZCKDZ10>?</ZCKDZ10>\n" +
                    "         <ZCKDZXQ10>?</ZCKDZXQ10>\n" +
                    "         <ZGSP_YXQ>?</ZGSP_YXQ>\n" +
                    "         <ZZBXY>?</ZZBXY>\n" +
                    "         <ZZBXY_YXQ>?</ZZBXY_YXQ>\n" +
                    "         <ZZLBZTX>?</ZZLBZTX>\n" +
                    "         <ZDTXQ>?</ZDTXQ>\n" +
                    "      </erp:MT_PurchaseUnitInfo>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

            String jyfsXML = "";
            String jyfw = Util.null2String(recordSet.getString("jyfw"));
            if (!jyfw.isEmpty()) {
                String[] arr = jyfw.split(",");
                for (int i = 0; i < Math.min(20, arr.length); i++) {
                    jyfsXML += "         <ZJYFW" + (i + 1) + ">" + getWRJYFSBH(arr[i]) + "</ZJYFW" + (i + 1) + ">\n";
                }
            }
        }
        return SUCCESS;
    }

    private String getWRJYFSBH(String id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select wrjyfsbh from uf_wrjyfs where id='" + id + "'";
        recordSet.executeSql(sql);
        String wrjyfsbh = "";
        if (recordSet.next()) {
            wrjyfsbh = Util.null2String(recordSet.getString("wrjyfsbh"));
        }
        return wrjyfsbh;
    }

    class MT_PurchaseUnitInfo_Msg {
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
