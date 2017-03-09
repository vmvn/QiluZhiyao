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
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by zhaiyaqi on 2016/11/19.
 */
public class OAsbbfspAction extends BaseBean implements Action {
    public String execute(RequestInfo requestInfo) {
        log("---->OAsbbfspAction run");
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        String message = "";
        if (!src.equals("reject")) {
            String wfid = requestInfo.getWorkflowid();
            String requestid = requestInfo.getRequestid();
            RecordSet recordSet = new RecordSet();
            String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + wfid;
            recordSet.executeSql(sql);
            recordSet.next();
            String t = recordSet.getString("tablename");
            sql = "select * from " + t + " where requestid=" + requestid;
            recordSet.executeSql(sql);
            if (recordSet.next()) {
                int id = recordSet.getInt("id");

                String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <erp:MT_Equi_Scrap_Import>\n" +
                        "         <ControlInfo>\n" +
                        "            <INTF_ID>I0012</INTF_ID>\n" +
                        "            <Src_System>OA</Src_System>\n" +
                        "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                        "            <Company_Code></Company_Code>\n" +
                        "            <Send_Time>" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "</Send_Time>\n" +
                        "         </ControlInfo>\n" +
                        "         <!--1 or more repetitions:-->\n" +
                        getDetails(id, t) +
                        "      </erp:MT_Equi_Scrap_Import>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
                log("---->" + request);
                HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
                String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_Equi_Scrap_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
                httpHeaderParm.put("instId", "10062");
                httpHeaderParm.put("repairType", "RP");
                String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
                log("---->" + response);
                message = setStatus(response, t, id);
            }
        }
        if (!message.isEmpty()) {
            requestInfo.getRequestManager().setMessageid("Returned Message");
            requestInfo.getRequestManager().setMessagecontent(message);
        }
        return SUCCESS;
    }

    private String getDetails(int mainid, String t) {
        String sql = "select * from " + t + "_dt1 where mainid=" + mainid + " and (clzt=0 or clzt=2 or clzt is null)";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        String v = "";
        while (recordSet.next()) {
            v += "<Equi_Import>\n" +
                    "<EQUNR>" + Util.null2String(recordSet.getString("sbbh")) + "</EQUNR>\n" +
                    "<KTX01>" + Util.null2String(recordSet.getString("sbmc")) + "</KTX01>\n" +
                    "</Equi_Import>\n";
        }
        return v;
    }

    private String setStatus(String response, String t, int id) {
        RecordSet recordSet = new RecordSet();
        String sql;
        Document dom;
        String v = "";
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element MT_Equi_Scrap_Export = root.element("Body").element("MT_Equi_Scrap_Export");
            Iterator iterator = MT_Equi_Scrap_Export.elementIterator("Equi_Export");
            while (iterator.hasNext()) {
                Element element = (Element) iterator.next();
                String EQUNR = element.elementTextTrim("EQUNR");
                String MESSAGE = element.elementTextTrim("MESSAGE");
                String status = element.elementTextTrim("MSG_TYPE");
                sql = "update " + t + "_dt1 set clzt=" + selectValue(status) + ",fkxx='" + MESSAGE + "' where mainid=" + id + " and sbbh='" + EQUNR + "'";
                recordSet.executeSql(sql);
                if (status.toUpperCase().equals("E")) {
                    if (!v.isEmpty()) {
                        v += "；";
                    }
                    v += "设备号：" + EQUNR + ",错误信息：" + MESSAGE;
                }
            }
        } catch (DocumentException e) {
            v = "response格式错误！";
            e.printStackTrace();
        }
        return v;
    }

    private int selectValue(String status) {
        int v = 0;
        if (status.toUpperCase().equals("S")) {
            v = 1;
        } else if (status.toUpperCase().equals("E")) {
            v = 2;
        } else if (status.toUpperCase().equals("W")) {
            v = 3;
        }
        return v;
    }

    private void log(Object o) {
        writeLog(o);
        System.out.println(o);
    }
}
