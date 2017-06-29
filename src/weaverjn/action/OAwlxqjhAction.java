package weaverjn.action;

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
import weaverjn.action.integration.utils;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaiyaqi on 2016/11/19.
 */
public class OAwlxqjhAction extends BaseBean implements Action {
    private String company;
    private String department;
    public String execute(RequestInfo requestInfo) {
        writeLog("run");
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if ((this.getCompany() == null || "".equals(this.getCompany())) && (this.getDepartment() == null || "".equals(this.getDepartment()))) {
            requestManager.setMessageid("Info");
            requestManager.setMessagecontent("请配置Action 参数");
        } else {
            writeLog("<company>" + this.getCompany());
            writeLog("<department>" + this.getDepartment());
            String workflowId = requestInfo.getWorkflowid();
            String requestId = requestInfo.getRequestid();
            RecordSet recordSet = new RecordSet();
            String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowId;
            recordSet.executeSql(sql);
            recordSet.next();
            String t = recordSet.getString("tablename");
            sql = "select * from " + t + " where requestid=" + requestId;
            writeLog(sql);
            recordSet.executeSql(sql);
            recordSet.next();
            int id = recordSet.getInt("id");
            String MOVE_TYPE = getMVTYPE(Util.null2String(recordSet.getString("ydlx")));
            String DEPARTMENT = getDepartmentName(recordSet.getInt("sqbm"));
            String REQUISITOR = getLastName(recordSet.getInt("sqr"));
            String WERKS = getCompanyCode(Util.null2String(recordSet.getString("gcbm")));
            String KOSTL = Util.null2String(recordSet.getString("xqbm"));
            String UMLGO = Util.null2String(recordSet.getString("jskcd1"));
            String AUFNR = Util.null2String(recordSet.getString("nbdd2"));
            String sqr = Util.null2String(recordSet.getString("sqr"));

            String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\" xmlns:gdt=\"http://sap.com/xi/SAPGlobal/GDT\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_DemandPlan>\n" +
                    "         <ControlInfo>\n" +
                    "            <INTF_ID>I0032</INTF_ID>\n" +
                    "            <Src_System>OA</Src_System>\n" +
                    "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time>" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "</Send_Time>\n" +
                    "         </ControlInfo>\n" +
                    "         <DEMANDPLAN_NO>" + requestId + "</DEMANDPLAN_NO>\n" +
                    "         <MV_TYPE>" + MOVE_TYPE + "</MV_TYPE>\n" +
                    "         <GRUND></GRUND>\n" +
                    "         <DEPARTMENT>" + DEPARTMENT + "</DEPARTMENT>\n" +
                    "         <REQUISITOR>" + REQUISITOR + "</REQUISITOR>\n" +
                    "         <WERKS>" + WERKS + "</WERKS>\n" +
                    "         <KOSTL>" + KOSTL + "</KOSTL>\n" +
                    "         <AUFNR>" + AUFNR + "</AUFNR>\n" +
                    "         <ACCOUNT></ACCOUNT>\n" +
                    "         <UMWRK></UMWRK>\n" +
                    "         <UMLGO>" + UMLGO + "</UMLGO>\n" +
                    "         <!--1 or more repetitions:-->\n" +
                    getDetails(id, t, sqr) +
                    "      </erp:MT_DemandPlan>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            writeLog(request);
            String username = utils.getUsername();
            String password = utils.getPassword();
            String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
            String response = WSClientUtils.callWebService(request, endpoint, username, password);
            writeLog(response);
            Ret_Messages ret_messages = getRet_Messages(response);

            writeLog("ret_messages:" + ret_messages);
            if (ret_messages == null) {
                writeLog("oa1");
//                if (!response.contains("Read timed out")) {
                    writeLog("oa2");
                    requestManager.setMessageid("SAP Response Message");
                    requestManager.setMessagecontent(response);
//                }
            } else {
                if (ret_messages.getMSG_TYPE().equals("E")) {
                    writeLog("oa3");
                    requestManager.setMessageid("SAP Response Message");
                    requestManager.setMessagecontent(ret_messages.getMESSAGE());
                } else {
                    sql = "update " + t + " set ylh='" + (ret_messages.getSAP_NO() + "/" + ret_messages.getREQ_NO()) + "' where requestid=" + requestId;
                    writeLog(sql);
                    recordSet.executeSql(sql);
                }
            }
        }
        return SUCCESS;
    }

    private String getLastName(int id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select lastname from hrmresource where id=" + id;
        recordSet.executeSql(sql);
        String lastname = "";
        if (recordSet.next()) {
            lastname = recordSet.getString("lastname");
        }
        return lastname;
    }

    private String getDepartmentName(int id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select departmentname from hrmdepartment where id=" + id;
        recordSet.executeSql(sql);
        String departmentname = "";
        if (recordSet.next()) {
            departmentname = recordSet.getString("departmentname");
        }
        if (departmentname.length() > 12) {
            departmentname = departmentname.substring(0, 12);
        }
        return departmentname;
    }

    /*private String getSelectName(int selectvalue, int fieldid) {
        RecordSet recordSet = new RecordSet();
        String sql = "select selectname from workflow_selectitem where fieldid=" + fieldid + " and selectvalue=" + selectvalue;
        recordSet.executeSql(sql);
        recordSet.next();
        return recordSet.getString("selectname");
    }*/

    private String getDetails(int mainid, String t, String sqr) {
        String sql = "select * from " + t + "_dt1 where mainid=" + mainid + " and (clzt=0 or clzt=2 or clzt is null)";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        String v = "";
        int i = 0;
        while (recordSet.next()) {
            i++;
            v += "         <Lines>\n" +
                    "            <ZLNNBR>" + i + "</ZLNNBR>\n" +
                    "            <MATNR>" + Util.null2String(recordSet.getString("wlbh1")) + "</MATNR>\n" +
//                    "            <MAKTX>" + slice(Util.null2String(recordSet.getString("wlmsmc")), 40) + "</MAKTX>\n" +
                    "            <MAKTX></MAKTX>\n" +
                    "            <MENGE>" + Util.null2String(recordSet.getString("xqsl")) + "</MENGE>\n" +
                    "            <MEINS>" + Util.null2String(recordSet.getString("jldw")) + "</MEINS>\n" +
                    "            <REQ_DATE>" + Util.null2String(recordSet.getString("xqrq")) + "</REQ_DATE>\n" +
                    "            <LGORT>" + Util.null2String(recordSet.getString("fckcd")) + "</LGORT>\n" +
                    "            <CHARG></CHARG>\n" +
                    "            <ZBEIZ>" + Util.null2String(recordSet.getString("bz")) + "</ZBEIZ>\n" +
                    "            <ZFLG>" + X(sqr, Util.null2String(recordSet.getString("bz"))) + "</ZFLG>\n" +
                    "            <LIFNR>" + Util.null2String(recordSet.getString("LIFNR")) + "</LIFNR>\n" +
                    "         </Lines>\n";
        }
        return v;
    }

    private String getMVTYPE(String string) {
        String v = "";
        if (string.equals("0")) {
            v = "Z61";
        } else if (string.equals("1")) {
            v = "201";
        } else if (string.equals("2")) {
            v = "311";
        } else if (string.equals("3")) {
            v = "101";
        } else if (string.equals("4")) {
            v = "Z63";
        }
        return v;
    }
    private Ret_Messages getRet_Messages(String string) {
        Ret_Messages ret_messages = null;
        try {
            Document dom = DocumentHelper.parseText(string);
            Element root = dom.getRootElement();
            Element Ret_Messages = root.element("Body").element("MT_DemandPlan_RetMsg").element("Ret_Messages");
            String MSG_TYPE = Ret_Messages.elementText("MSG_TYPE");
            String MESSAGE = Ret_Messages.elementText("MESSAGE");
            String SAP_NO = root.element("Body").element("MT_DemandPlan_RetMsg").elementText("SAP_NO");
            String REQ_NO = root.element("Body").element("MT_DemandPlan_RetMsg").elementText("REQ_NO");
            ret_messages = new Ret_Messages();
            ret_messages.setMSG_TYPE(MSG_TYPE);
            ret_messages.setMESSAGE(MESSAGE);
            ret_messages.setSAP_NO(SAP_NO);
            ret_messages.setREQ_NO(REQ_NO);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ret_messages;
    }

    private String getCompanyCode(String id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select gcbm from uf_sapjcsj_gc where id=" + id;
        String gcbm = "";
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            gcbm = recordSet.getString("gcbm");
        }
        return gcbm;
    }

    private String slice(String s, int n) {
        return s.length() > n ? s.substring(0, n) : s;
    }

    private String X(String sqr, String bz) {
        RecordSet recordSet = new RecordSet();
        String sql = "select subcompanyid1,departmentid from hrmresource where id=" + sqr;
        recordSet.executeSql(sql);
        recordSet.next();
        int company = recordSet.getInt("subcompanyid1");
        int department = recordSet.getInt("departmentid");
        return (containsCompany(String.valueOf(company)) || containsDepartment(String.valueOf(department))) && !bz.isEmpty() ? "X" : "";
    }

    private boolean containsCompany(String subcomid) {
        if (this.getCompany() == null) {
            this.setCompany("");
        }
        String[] arr = this.getCompany().split(",");
        for (String s : arr) {
            if (s.equals(subcomid)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDepartment(String department) {
        if (this.getDepartment() == null) {
            this.setDepartment("");
        }
        String[] arr = this.getDepartment().split(",");
        for (String s : arr) {
            if (s.equals(department)) {
                return true;
            }
        }
        return false;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    class Ret_Messages{
        private String MSG_TYPE;
        private String MESSAGE;
        private String SAP_NO;
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

        public String getSAP_NO() {
            return SAP_NO;
        }

        public void setSAP_NO(String SAP_NO) {
            this.SAP_NO = SAP_NO;
        }

        public String getREQ_NO() {
            return REQ_NO;
        }

        public void setREQ_NO(String REQ_NO) {
            this.REQ_NO = REQ_NO;
        }
    }
}
