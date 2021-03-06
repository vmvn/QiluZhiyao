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
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

/**
 * Created by zhaiyaqi on 2017/2/9.
 */
public class I0043Action extends BaseBean implements Action {
    private String REQ_TYPE;
    private String company;
    private String requestid;
    @Override
    public String execute(RequestInfo requestInfo) {
        writeLog("run");
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        RET_MSG ret_msg = null;
        String message = "";
        if (!src.equals("reject")) {
            String requestId = requestInfo.getRequestid();
            this.setRequestid(requestId);
            String table = requestManager.getBillTableName();

            RecordSet recordSet = new RecordSet();
            String sql = "select id,sqr from " + table + " where requestid=" + requestId;
            recordSet.executeSql(sql);
            if (recordSet.next()) {
                int id = recordSet.getInt("id");
                int sqr = recordSet.getInt("sqr");
                this.setCompany(getFieldValue("subcompanyid1", "hrmresource", "id", sqr + ""));
                String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <erp:MT_MAT_MDG_LIST>\n" +
                        "         <ControlInfo>\n" +
                        "            <INTF_ID></INTF_ID>\n" +
                        "            <Src_System>OA</Src_System>\n" +
                        "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                        "            <Company_Code></Company_Code>\n" +
                        "            <Send_Time></Send_Time>\n" +
                        "         </ControlInfo>\n" +
                        "         <!--1 or more repetitions:-->\n" +
                        getLines(id, table) +
                        "      </erp:MT_MAT_MDG_LIST>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
                writeLog(request);
                String username = utils.getUsername();
                String password = utils.getPassword();
                String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
                String response = WSClientUtils.callWebService(request, endpoint, username, password);
                writeLog(response);
                ret_msg = getRET_MSG(response);
                message = response;
            }
        }
        if (ret_msg == null) {
            requestInfo.getRequestManager().setMessageid("Message");
            requestInfo.getRequestManager().setMessagecontent(message);
        } else {
            if (ret_msg.getMSG_TYPE().equals("E")) {
                requestInfo.getRequestManager().setMessageid("Error Message");
                requestInfo.getRequestManager().setMessagecontent(ret_msg.getMESSAGE());
            }
        }
        return SUCCESS;
    }

    private String getLines(int id, String t) {
        RecordSet recordSet = new RecordSet();
        String sql = "select * from " + t + "_dt1 where mainid=" + id;
        recordSet.executeSql(sql);
        String lines = "";
        while (recordSet.next()) {
            lines += "         <Material_Mdg_Info>\n" +
                    "            <REQ_TYPE>" + this.getREQ_TYPE() + "</REQ_TYPE>\n" +
                    "            <REQUEST></REQUEST>\n" +
                    "            <DESCRIPTION>" + getDescription(this.getCompany()) + "</DESCRIPTION>\n" +
                    "            <MATERIAL>" + Util.null2String(recordSet.getString("gyl_sapwlbm")) + "</MATERIAL>\n" +
                    "            <MTART>" + getFieldValue("wllxbm", "uf_sapjcsj_wllx", "id", Util.null2String(recordSet.getString("jhy_wllx"))) + "</MTART>\n" +
                    "            <MEINS>" + getFieldValue("nbdldw", "uf_sapjcsj_jldw", "id", Util.null2String(recordSet.getString("jhy_jbjldw"))) + "</MEINS>\n" +
                    "            <MATKL>" + getFieldValue("wlzdm", "uf_sapjcsj_wlz", "id", Util.null2String(recordSet.getString("jhy_wlz"))) + "</MATKL>\n" +
                    "            <SPART>" + getFieldValue("cpzdm", "uf_sapjcsj_cpz", "id", Util.null2String(recordSet.getString("jhy_cpzdm"))) + "</SPART>\n" +
                    "            <BISMT>" + Util.null2String(recordSet.getString("jhywlms_jwlbm")) + "</BISMT>\n" +
                    "            <MBRSH>" + Util.null2String(recordSet.getString("jhy_hyly")) + "</MBRSH>\n" +
                    "            <GROES>" + Util.null2String(recordSet.getString("jhy_dxgl")) + "</GROES>\n" +
                    "            <EXTWG>" + Util.null2String(recordSet.getString("gxb_wbwlz")) + "</EXTWG>\n" +
                    "            <NOTEBSCDA>" + Util.null2String(recordSet.getString("jhy_jbsjwb")) + "</NOTEBSCDA>\n" +
//                    "            <TXTQINSP>" + Util.null2String(recordSet.getString("jhywlms_zlbz")) + "</TXTQINSP>\n" +//质量标准
                    "            <TXTQINSP>" + Util.null2String(recordSet.getString("jhy_bzgg")) + "</TXTQINSP>\n" +//质量标准
                    "            <GEWEI_MAT>" + Util.null2String(recordSet.getString("jhy_zldw")) + "</GEWEI_MAT>\n" +
                    "            <NORMT>" + Util.null2String(recordSet.getString("jhy_hybzms")) + "</NORMT>\n" +
                    "            <FERTH>" + Util.null2String(recordSet.getString("jhy_scjybwl")) + "</FERTH>\n" +
                    "            <WRKST>" + Util.null2String(recordSet.getString("jhy_jbwl")) + "</WRKST>\n" +
                    "            <NTGEW>" + Util.null2String(recordSet.getString("jhy_jz")) + "</NTGEW>\n" +
                    "            <BRGEWMARA>" + Util.null2String(recordSet.getString("jhy_mz")) + "</BRGEWMARA>\n" +
                    "            <LOEKZMDMA>" + (this.getREQ_TYPE().equals("2") ? Util.null2String(recordSet.getString("jhy_scbj")) : "") + "</LOEKZMDMA>\n" +
                    "            <NOTEINTCM>" + this.getRequestid() + "," + recordSet.getString("id") + "</NOTEINTCM>\n" +
                    "            <!--1 or more repetitions:-->\n" +
                    "            <MAKT_LIST>\n" +
                    "               <MAKTX>" + Util.null2String(recordSet.getString("jhy_wlms")) + "</MAKTX>\n" +
//                    "               <SPRAS>" + getFieldValue("yydmbm", "uf_sapjcsj_yydm", "id", Util.null2String(recordSet.getString("jhy_yydm"))) + "</SPRAS>\n" +
                    "               <SPRAS>CH</SPRAS>\n" +
                    "            </MAKT_LIST>\n";
            String jhy_sapbzmc = Util.null2String(recordSet.getString("jhy_sapbzmc"));
            if (!jhy_sapbzmc.isEmpty()) {
                lines += "            <MAKT_LIST>\n" +
                        "               <MAKTX>" + jhy_sapbzmc + "</MAKTX>\n" +
                        "               <SPRAS>Z4</SPRAS>\n" +
                        "            </MAKT_LIST>\n";
            }
            lines += "         </Material_Mdg_Info>\n";
        }
        return lines;
    }

    private RET_MSG getRET_MSG(String s) {
        RET_MSG ret_msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(s);
            Element root = dom.getRootElement();
            Element MT_MAT_MDG_RET = root.element("Body").element("MT_MAT_MDG_RET");
            ret_msg = new RET_MSG();
            ret_msg.setMSG_TYPE(MT_MAT_MDG_RET.element("RET_MSG").elementText("MSG_TYPE"));
            ret_msg.setMESSAGE(MT_MAT_MDG_RET.element("RET_MSG").elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ret_msg;
    }

    private String getDescription(String company) {
        String code = "";
        if(company.equals("63")||company.equals("1")||company.equals("82")||company.equals("81")){
            code = "1010";
            code += "总厂";
        } else if(company.equals("62")){
            code = "1030";
            code += "安替";
        } else if(company.equals("143")){
            code = "1060";
            code += "天和";
        } else if(company.equals("121")){
            code = "1070";
            code += "黄河";
        } else if(company.equals("142")){
            code = "1020";
            code += "海南";
        } else if (company.equals("61")) {
            code = "1610";
            code = "1620";
        } else if (company.equals("122")) {
            code = "1050";
            code += "临邑";
        }
        return "OA" + code;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    class RET_MSG {
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
