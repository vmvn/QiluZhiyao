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
import weaverjn.utils.PropertiesUtil;

/**
 * Created by zhaiyaqi on 2017/5/5.
 */
public class NearlyEffectiveDrug extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String requestId = requestInfo.getRequestid();
        String table = requestManager.getBillTableName();

        String sql = "select id from " + table + " where requestid=" + requestId;
        writeLog(sql);
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        recordSet.next();
        String id = recordSet.getString("id");

        sql = "select pzbh,ph,WERKS from " + table + "_dt1 where mainid=" + id;
        recordSet.executeSql(sql);
        StringBuilder DATA_List = new StringBuilder();
        while (recordSet.next()) {
            DATA_List.append("<DATA_List>\n");
            DATA_List.append("<matnr>").append(Util.null2String(recordSet.getString("pzbh"))).append("</matnr>\n");
            DATA_List.append("<werks>").append(Util.null2String(recordSet.getString("WERKS"))).append("</werks>\n");
            DATA_List.append("<charg>").append(Util.null2String(recordSet.getString("ph"))).append("</charg>\n");
            DATA_List.append("</DATA_List>\n");
        }

        String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_NearlyEffective_Drug>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID></INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                DATA_List.toString() +
                "      </erp:MT_NearlyEffective_Drug>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        writeLog(soapHttpRequest);
        String username = utils.getUsername();
        String password = utils.getPassword();
        String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
        String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, username, password);
        writeLog(soapHttpResponse);
        RET_MSG msg = parse(soapHttpResponse, "MT_NearlyEffective_Drug_Msg");
        if (msg != null) {
            if (msg.getMSG_TYPE().equals("E")) {
                requestManager.setMessageid(msg.getMSG_TYPE());
                requestManager.setMessagecontent(msg.getMESSAGE());
            }
        } else {
            requestManager.setMessageid("E");
            requestManager.setMessagecontent(soapHttpResponse);
        }
        return SUCCESS;
    }

    private RET_MSG parse(String response, String qName) {
        RET_MSG ret_msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element msg = root.element("Body").element(qName);
            ret_msg = new RET_MSG();
            ret_msg.setMSG_TYPE(msg.elementText("MESSAGE_TYPE"));
            ret_msg.setMESSAGE(msg.elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ret_msg;
    }
}
