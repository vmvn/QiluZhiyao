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

import java.util.Map;

/**
 * Created by zhaiyaqi on 2017/4/24.
 */
public class TransportEnterpriseStateAction extends BaseBean implements Action {
    private String VKROG;
    private String uf;
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        Map<String, String> map = utils.getMainTableData(requestInfo.getMainTableInfo());
        RecordSet rs = new RecordSet();
        String sql = "select cydwbh from " + this.getUf() + " where id='" + map.get("cydwmc") + "'";
        writeLog("查询运输单位的sql： " + sql);
        rs.executeSql(sql);
        rs.next();
        String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_Transport_Enterprise_State>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID></INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <Transport_Enterprise_State>\n" +
                "            <KUNNR>" + Util.null2String(rs.getString("cydwbh")) + "</KUNNR>\n" +
                "            <VKROG>" + this.getVKROG() + "</VKROG>\n" +
                "            <STATE>Y</STATE>\n" +
                "         </Transport_Enterprise_State>\n" +
                "      </erp:MT_Transport_Enterprise_State>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        writeLog("请求sap内容：" + soapHttpRequest);
        String username = utils.getUsername();
        String password = utils.getPassword();
        String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
        String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, username, password);
        writeLog("sap返回信息：" + soapHttpResponse);
        MT_Transport_State_Ret msg = parse(soapHttpResponse);
        if (msg != null) {
            if (msg.getMSG_TYPE().equals("E")) {
                requestManager.setMessageid("sap 返回信息");
                requestManager.setMessagecontent(msg.getMESSAGE());
            }
        } else {
            requestManager.setMessageid("error");
            requestManager.setMessagecontent(soapHttpResponse);
        }
        return SUCCESS;
    }

    private MT_Transport_State_Ret parse(String response) {
        MT_Transport_State_Ret msg = null;
        try{
            Document document = DocumentHelper.parseText(response);
            Element root = document.getRootElement();
            Element e = root.element("Body").element("MT_Transport_State_Ret");
            msg = new MT_Transport_State_Ret();
            msg.setMSG_TYPE(e.element("Ret_Msg").elementText("MSG_TYPE"));
            msg.setMESSAGE(e.element("Ret_Msg").elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getUf() {
        return uf;
    }

    class MT_Transport_State_Ret{
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

    public String getVKROG() {
        return VKROG;
    }

    public void setVKROG(String VKROG) {
        this.VKROG = VKROG;
    }
}
