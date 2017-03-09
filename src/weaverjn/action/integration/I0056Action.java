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

import java.util.HashMap;

/**
 * Created by zhaiyaqi on 2017/3/2.
 */
public class I0056Action extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        String src = requestInfo.getRequestManager().getSrc();
        if (!src.equals("reject")) {
            String requestid = requestInfo.getRequestid();
            String workflowid = requestInfo.getWorkflowid();
            RecordSet recordSet = new RecordSet();
            String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
            recordSet.executeSql(sql);
            recordSet.next();
            String tablename = recordSet.getString("tablename");

            sql = "select * from " + tablename + " where requestid=" + requestid;
            recordSet.executeSql(sql);
            recordSet.next();
            String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_Picking_List>\n" +
                    "         <control_info>\n" +
                    "            <INTF_ID>I0056</INTF_ID>\n" +
                    "            <Src_System></Src_System>\n" +
                    "            <Dest_System></Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time></Send_Time>\n" +
                    "         </control_info>\n" +
                    "         <issue_no>" + Util.null2String(recordSet.getString("sapylh")) + "</issue_no>\n" +
                    "         <move_type>" + Move_Type(recordSet.getString("ydlx")) + "</move_type>\n" +
                    "         <werks>" + Util.null2String(recordSet.getString("gc")) + "</werks>\n" +
                    "         <DEPARTMENT>" + getDepartmentName(Util.null2String(recordSet.getString("sqbm"))) + "</DEPARTMENT>\n" +
                    "         <REQUISITOR>" + getLastname(Util.null2String(recordSet.getString("sqr"))) + "</REQUISITOR>\n" +
                    "         <kostl>" + Util.null2String(recordSet.getString("cbzx")) + "</kostl>\n" +
                    "         <aufnr>" + Util.null2String(recordSet.getString("nbdd")) + "</aufnr>\n" +
                    "         <umwrk>" + Util.null2String(recordSet.getString("dfgc")) + "</umwrk>\n" +
                    "         <umlgo>" + Util.null2String(recordSet.getString("dfkcd")) + "</umlgo>\n" +
                    "         <req_date>" + Util.null2String(recordSet.getString("xqrq")) + "</req_date>\n" +
                    "         <!--1 or more repetitions:-->\n" +
                    lines(tablename, recordSet.getString("id")) +
                    "      </erp:MT_Picking_List>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            writeLog(soapHttpRequest);
            HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
            String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_Picking_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
            httpHeaderParm.put("instId", "10062");
            httpHeaderParm.put("repairType", "RP");
            String soapHttpResponse = WSClientUtils.callWebServiceWithHttpHeaderParm(soapHttpRequest, url, httpHeaderParm);
            writeLog(soapHttpResponse);
            MT_Picking_Ret ret = parse(soapHttpResponse);
            if (ret != null) {
                if (ret.getMSG_TYPE().equals("S")) {
                    sql = "update " + tablename + " set ylh='" + ret.getISSUE_NO_NEW() + "' where requestid=" + requestid;
                    writeLog(sql);
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

    private String lines(String tablename, String id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select * from " + tablename + "_dt1 where mainid=" + id;
        recordSet.executeSql(sql);
        String lines = "";
        while (recordSet.next()) {
            lines += "         <picking_list>\n" +
                    "            <ZLNNBR>" + Util.null2String(recordSet.getString("hxmh")) + "</ZLNNBR>\n" +
                    "            <MATNR>" + Util.null2String(recordSet.getString("wlbh")) + "</MATNR>\n" +
                    "            <MAKTX>" + Util.null2String(recordSet.getString("wlmsmc")) + "</MAKTX>\n" +
                    "            <MENGE>" + Util.null2String(recordSet.getString("lysl")) + "</MENGE>\n" +
                    "            <MEINS>" + Util.null2String(recordSet.getString("jldw")) + "</MEINS>\n" +
                    "            <WERKS>" + Util.null2String(recordSet.getString("gc")) + "</WERKS>\n" +
                    "            <LGORT>" + Util.null2String(recordSet.getString("fckcd")) + "</LGORT>\n" +
                    "            <CHARG>" + Util.null2String(recordSet.getString("kcph")) + "</CHARG>\n" +
                    "            <ACCOUNT>" + Util.null2String(recordSet.getString("account")) + "</ACCOUNT>\n" +
                    "         </picking_list>\n";
        }
        return lines;
    }

    private MT_Picking_Ret parse(String soapHttpResponse) {
        MT_Picking_Ret ret = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(soapHttpResponse);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_Picking_Ret");

            ret = new MT_Picking_Ret();

            ret.setISSUE_NO_NEW(e.elementText("ISSUE_NO_NEW"));
            ret.setMSG_TYPE(e.elementText("MSG_TYPE"));
            ret.setMESSAGE(e.elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String Move_Type(String s) {
        String move = "";
        if (s.equals("0")) {
            move = "Z61";
        } else if (s.equals("1")) {
            move = "201";
        } else if (s.equals("2")) {
            move = "311";
        } else if (s.equals("3")) {
            move = "Z63";
        }
        return move;
    }

    private String getDepartmentName(String id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select departmentname from hrmdepartment where id='" + id + "'";
        String name = "";
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            name = recordSet.getString("lastname");
        }
        return name;
    }

    private String getLastname(String id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select lastname from hrmresource where id='" + id + "'";
        String name = "";
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            name = recordSet.getString("lastname");
        }
        return name;
    }

    class MT_Picking_Ret {
        private String ISSUE_NO_NEW;
        private String MSG_TYPE;
        private String MESSAGE;

        public String getISSUE_NO_NEW() {
            return ISSUE_NO_NEW;
        }

        public void setISSUE_NO_NEW(String ISSUE_NO_NEW) {
            this.ISSUE_NO_NEW = ISSUE_NO_NEW;
        }

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
