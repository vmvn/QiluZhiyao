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
 * Created by zhaiyaqi on 2017/2/9.
 */
public class I0051Action extends BaseBean implements Action {
    private String REQ_TYPE;

    @Override
    public String execute(RequestInfo requestInfo) {
        String workflowid = requestInfo.getWorkflowid();
        String requestid = requestInfo.getRequestid();
        String request = this.getXmlHttpRequest(workflowid, requestid);
        log(request);
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_CRM&receiverParty=&receiverService=&interface=SI_Vendor_List_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        log(response);
        MT_Vendor_Msg_List mt_vendor_msg_list = this.getMT_Vendor_Msg_List(response);
        if (mt_vendor_msg_list == null) {
            requestInfo.getRequestManager().setMessageid("Message");
            requestInfo.getRequestManager().setMessagecontent(response);
        } else {
            if (mt_vendor_msg_list.getMSG_TYPE().equals("E")) {
                requestInfo.getRequestManager().setMessageid("Error Message");
                requestInfo.getRequestManager().setMessagecontent(mt_vendor_msg_list.getMESSAGE());
            }
        }
        return null;
    }

    private String getXmlHttpRequest(String workflowid, String requestid) {
        RecordSet recordSet = new RecordSet();
        String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
        recordSet.executeSql(sql);
        recordSet.next();
        String t = Util.null2String(recordSet.getString("tablename"));

        sql = "select * from " + t + " where requestid=" + requestid;
        recordSet.executeSql(sql);
        recordSet.next();
        String[] name = utils.slice(Util.null2String(recordSet.getString("ywhzhbmc")), 35, 4);
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_Vendor_List>\n" +
                "         <control_info>\n" +
                "            <INTF_ID>I0051</INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </control_info>\n" +
                "         <Vendor_List>\n" +
                "            <req_type>" + this.getREQ_TYPE() + "</req_type>\n" +
                "            <request></request>\n" +
                "            <description>" + this.getDescription(Util.null2String(recordSet.getString("gsmc"))) + "</description>\n" +
                "            <bp_header>" + Util.null2String(recordSet.getString("ywhzhbbm")) + "</bp_header>\n" +
                "            <bu_type>2</bu_type>\n" +
                "            <bu_group>" + bu_group(Util.null2String(recordSet.getString("ywhzhbfz"))) + "</bu_group>\n" +
                "            <xblck>" + (Util.null2String(recordSet.getString("jtdj")).equals("1") ? "X" : "") + "</xblck>\n" +
                "            <xdele>" + (Util.null2String(recordSet.getString("gdbz")).equals("1") ? "X" : "") + "</xdele>\n" +
                "            <name_org1>" + name[0] + "</name_org1>\n" +
                "            <name_org2>" + name[1] + "</name_org2>\n" +
                "            <name_org3>" + name[2] + "</name_org3>\n" +
                "            <name_org4>" + name[3] + "</name_org4>\n" +
                "            <bu_sort1>" + Util.null2String(recordSet.getString("ssx1")) + "</bu_sort1>\n" +
                "            <bu_sort2>" + Util.null2String(recordSet.getString("ssx2")) + "</bu_sort2>\n" +
                "            <mc_street>" + Util.null2String(recordSet.getString("jd1mph")) + "</mc_street>\n" +
                "            <house_nr1>" + Util.null2String(recordSet.getString("mph")) + "</house_nr1>\n" +
                "            <post_cod1>" + Util.null2String(recordSet.getString("yzbm")) + "</post_cod1>\n" +
                "            <ref_posta>" + this.getFieldValue("bm", "uf_gj", "id", Util.null2String(recordSet.getString("gj"))) + "</ref_posta>\n" +
                "            <ref_post>" + this.getFieldValue("bm", "uf_szxs", "id", Util.null2String(recordSet.getString("szxs"))) + "</ref_post>\n" +
                "            <city1>" + Util.null2String(recordSet.getString("cs")) + "</city1>\n" +
                "            <t_flgdeft>" + Util.null2String(recordSet.getString("dhhm")) + "</t_flgdeft>\n" +
                "            <T_EXTENS>" + Util.null2String(recordSet.getString("dhfj")) + "</T_EXTENS>\n" +
                "            <F_FLGDEFT>" + Util.null2String(recordSet.getString("czhm")) + "</F_FLGDEFT>\n" +
                "            <FLGMOB>" + Util.null2String(recordSet.getString("czhm")) + "</FLGMOB>\n" +
                "            <E_ADDERSS>" + Util.null2String(recordSet.getString("dzyxdz")) + "</E_ADDERSS>\n" +
                "            <REMARK></REMARK>\n" +
                "            <NATPERS>" + Util.null2String(recordSet.getString("fr")) + "</NATPERS>\n" +
                "            <CONTACT>" + Util.null2String(recordSet.getString("lxr")) + "</CONTACT>\n" +
                "            <TAXNUMXL>" + Util.null2String(recordSet.getString("nsrdjh")) + "</TAXNUMXL>\n" +
                "            <!--1 or more repetitions:-->\n";

        int id = recordSet.getInt("id");
        sql = "select * from " + t + "_dt1 where mainid=" + id;
        RecordSet recordSet1 = new RecordSet();
        recordSet1.executeSql(sql);
        while (recordSet1.next()) {
            xml += "            <bp_bkdtl>\n" +
                    "               <banks>" + this.getFieldValue("bm", "uf_yhgjdm", "id", Util.null2String(recordSet1.getString("yhgjdm"))) + "</banks>\n" +
                    "               <bankn>" + this.getFieldValue("yhdm", "uf_yhdm", "id", Util.null2String(recordSet1.getString("yhdm"))) + "</bankn>\n" +
                    "               <bankl>" + this.getFieldValue("bm", "uf_yhzh", "id", Util.null2String(recordSet1.getString("yhzh"))) + "</bankl>\n" +
                    "            </bp_bkdtl>\n";
        }

        xml += "            <bu_LANGU>" + Util.null2String(recordSet.getString("yy")) + "</bu_LANGU>\n" +
                "         </Vendor_List>\n" +
                "      </erp:MT_Vendor_List>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return xml;
    }

    private MT_Vendor_Msg_List getMT_Vendor_Msg_List(String response) {
        MT_Vendor_Msg_List mt_vendor_msg_list = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element MT_Vendor_Msg_List = root.element("Body").element("MT_Vendor_Msg_List");
            mt_vendor_msg_list = new MT_Vendor_Msg_List();
            mt_vendor_msg_list.setREQUEST(MT_Vendor_Msg_List.elementText("REQUEST"));
            mt_vendor_msg_list.setMSG_TYPE(MT_Vendor_Msg_List.elementText("MSG_TYPE"));
            mt_vendor_msg_list.setMESSAGE(MT_Vendor_Msg_List.elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return mt_vendor_msg_list;
    }

    private void log(Object o) {
        String prefix = "<" + this.getClass().getName() + ">";
        System.out.println(prefix + o);
        writeLog(prefix + o);
    }

    private String getDescription(String company) {
        String code = "";
        if(company.equals("63")||company.equals("1")||company.equals("82")){
            code = "1010";
        }else if(company.equals("62")){
            code = "1030";
        }else if(company.equals("143")){
            code = "1060";
        }else if(company.equals("121")){
            code = "1070";
        }else if(company.equals("142")){
            code = "1630";
        }
        return "OA" + code;
    }

    private String bu_group(String fieldvalue) {
        String type;
        if (fieldvalue.equals("0")) {
            type = "QL01";
        } else if (fieldvalue.equals("1")) {
            type = "QL02";
        } else if (fieldvalue.equals("2")) {
            type = "QL03";
        } else if (fieldvalue.equals("3")) {
            type = "QL04";
        } else if (fieldvalue.equals("4")) {
            type = "QL13";
        }else{
            type = "";
        }
        return type;
    }

    private String getFieldValue(String field1, String tablename, String field2, String value2) {
        RecordSet recordSet = new RecordSet();
        String sql = "select " + field1 + " from " + tablename + " where " + field2 + "='" + value2 + "'";
        recordSet.executeSql(sql);
        String value1 = "";
        if (recordSet.next()) {
            value1 = recordSet.getString(field1);
        }
        return value1;
    }

    public String getREQ_TYPE() {
        return REQ_TYPE;
    }

    public void setREQ_TYPE(String REQ_TYPE) {
        this.REQ_TYPE = REQ_TYPE;
    }

    class MT_Vendor_Msg_List {
        private String REQUEST;
        private String MESSAGE;
        private String MSG_TYPE;

        public String getREQUEST() {
            return REQUEST;
        }

        public void setREQUEST(String REQUEST) {
            this.REQUEST = REQUEST;
        }

        public String getMESSAGE() {
            return MESSAGE;
        }

        public void setMESSAGE(String MESSAGE) {
            this.MESSAGE = MESSAGE;
        }

        public String getMSG_TYPE() {
            return MSG_TYPE;
        }

        public void setMSG_TYPE(String MSG_TYPE) {
            this.MSG_TYPE = MSG_TYPE;
        }
    }
}
