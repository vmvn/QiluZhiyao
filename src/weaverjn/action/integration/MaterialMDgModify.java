package weaverjn.action.integration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.general.BaseBean;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaverjn.utils.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

import java.util.Map;

/**
 * Created by zhaiyaqi on 2017/5/11.
 */
public class MaterialMDgModify extends BaseBean implements Action {

    private Map<String, String> MtData;

    @Override
    public String execute(RequestInfo requestInfo) {
        writeLog("run");
        MtData = utils.getMainTableData(requestInfo.getMainTableInfo());
        String sqr = MtData.get("sqr");
        String company = utils.getFieldValue("hrmresource", "subcompanyid1", sqr);
        String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
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
                "         <Material_Mdg_Info>\n" +
                "            <REQ_TYPE>2</REQ_TYPE>\n" +
                "            <REQUEST></REQUEST>\n" +
                "            <DESCRIPTION>" + getDescription(company) + "</DESCRIPTION>\n" +
                "            <MATERIAL>" + MtData.get("sapwlbm") + "</MATERIAL>\n" +
                "            <MTART>" + getValue("wllx", "bgwllx") + "</MTART>\n" +
                "            <MEINS>" + getValue("jbjldw", "bgjbjldw") + "</MEINS>\n" +
                "            <MATKL>" + getValue("wlz", "bgwlz") + "</MATKL>\n" +
                "            <SPART>" + getValue("cpz", "bgcpz") + "</SPART>\n" +
                "            <BISMT>" + getValue("jwlbm", "bgjwlbm") + "</BISMT>\n" +
                "            <MBRSH></MBRSH>\n" +
                "            <GROES>" + getValue("dxlg", "bgdxlg") + "</GROES>\n" +
                "            <EXTWG></EXTWG>\n" +
                "            <NOTEBSCDA>" + getValue("jbsjwb", "bgjbsjwb") + "</NOTEBSCDA>\n" +
                "            <TXTQINSP>" + getValue("fjsj", "bgfjsj") + "</TXTQINSP>\n" +
                "            <GEWEI_MAT>" + getValue("zldw", "bgzldw") + "</GEWEI_MAT>\n" +
                "            <NORMT>" + getValue("hybzms", "bghybzms") + "</NORMT>\n" +
                "            <FERTH>" + getValue("scjybwl", "bgscjybwl") + "</FERTH>\n" +
                "            <WRKST>" + getValue("jbwl", "bgjbwl") + "</WRKST>\n" +
                "            <NTGEW>" + (getValue("jz", "bgjz").isEmpty() ? "0" : getValue("jz", "bgjz")) + "</NTGEW>\n" +
                "            <BRGEWMARA>" + (getValue("mz", "bgmz").isEmpty() ? "0" : getValue("mz", "bgmz")) + "</BRGEWMARA>\n" +
                "            <LOEKZMDMA></LOEKZMDMA>\n" +
                "            <NOTEINTCM>" + requestInfo.getRequestid() + ",-1</NOTEINTCM>\n" +
                "            <!--1 or more repetitions:-->\n" +
                "            <MAKT_LIST>\n" +
                "               <MAKTX>" + getValue("wlms", "bgwlms") + "</MAKTX>\n" +
                "               <SPRAS>CH</SPRAS>\n" +
                "            </MAKT_LIST>\n";
        String bzmc = MtData.get("bzmc");
        String bgbzmc = MtData.get("bgbzmc");
        if (!bgbzmc.isEmpty() || !bzmc.isEmpty()) {
            soapHttpRequest += "            <MAKT_LIST>\n" +
                    "               <MAKTX>" + getValue("bzmc", "bgbzmc") + "</MAKTX>\n" +
                    "               <SPRAS>Z4</SPRAS>\n" +
                    "            </MAKT_LIST>\n";
        }

        soapHttpRequest += "         </Material_Mdg_Info>\n" +
                "      </erp:MT_MAT_MDG_LIST>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        writeLog(soapHttpRequest);
        String username = utils.getUsername();
        String password = utils.getPassword();
        String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", "I0043Action");
        String response = WSClientUtils.callWebService(soapHttpRequest, endpoint, username, password);
        writeLog(response);
        RET_MSG ret_msg = getRET_MSG(response);
        if (ret_msg == null) {
            requestInfo.getRequestManager().setMessageid("Message");
            requestInfo.getRequestManager().setMessagecontent(response);
        } else {
            if (ret_msg.getMSG_TYPE().equals("E")) {
                requestInfo.getRequestManager().setMessageid("Error Message");
                requestInfo.getRequestManager().setMessagecontent(ret_msg.getMESSAGE());
            }
        }
        return SUCCESS;
    }

    private String getValue(String field1, String field2) {
        String value = MtData.get(field2);
        if (value.isEmpty()) {
            value = MtData.get(field1);
        } else {
            if (field2.equals("bgwllx")) {
                value = utils.getFieldValue("uf_sapjcsj_wllx", "wllxbm", value);
            } else if (field2.equals("bgwlz")) {
                value = utils.getFieldValue("uf_sapjcsj_wlz", "wlzdm", value);
            } else if (field2.equals("bgjbjldw")) {
                value = utils.getFieldValue("uf_sapjcsj_jldw", "nbdldw", value);
            } else if (field2.equals("bgcpz")) {
                value = utils.getFieldValue("uf_sapjcsj_cpz", "cpzdm", value);
            }
        }
        return value;
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

    private RET_MSG getRET_MSG(String s) {
        RET_MSG msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(s);
            Element root = dom.getRootElement();
            Element MT_MAT_MDG_RET = root.element("Body").element("MT_MAT_MDG_RET");
            msg = new RET_MSG();
            msg.setMSG_TYPE(MT_MAT_MDG_RET.element("RET_MSG").elementText("MSG_TYPE"));
            msg.setMESSAGE(MT_MAT_MDG_RET.element("RET_MSG").elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
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
