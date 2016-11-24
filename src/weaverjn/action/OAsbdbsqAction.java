package weaverjn.action;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;
import weaverjn.qlzy.sap.WSClientUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by zhaiyaqi on 2016/11/19.
 */
public class OAsbdbsqAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
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
                String gcbm = Util.null2String(recordSet.getString("gcbm"));

                String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <erp:MT_Equi_Allot_Import>\n" +
                        "         <ControlInfo>\n" +
                        "            <INTF_ID>I0011</INTF_ID>\n" +
                        "            <Src_System>OA</Src_System>\n" +
                        "            <Dest_System>SAPERP\n</Dest_System>\n" +
                        "            <Company_Code>" + gcbm + "</Company_Code>\n" +
                        "            <Send_Time>" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "</Send_Time>\n" +
                        "         </ControlInfo>\n" +
                        "         <!--1 or more repetitions:-->\n" +
                        getDetails(id, t) +
                        "      </erp:MT_Equi_Allot_Import>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
                HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
                String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_Equi_Allot_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
                httpHeaderParm.put("instId", "10062");
                httpHeaderParm.put("repairType", "RP");
                String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
                message = setStatus(response, t, id);
            }
        }
        if (!message.isEmpty()) {
            requestInfo.getRequestManager().setMessageid("90031");
            requestInfo.getRequestManager().setMessagecontent(message);
        }
        return SUCCESS;
    }

    private String getDetails(int mainid, String t) {
        String sql = "select * from " + t + "_dt1 where mainid=" + mainid + " and (clzt=0 or clzt=2)";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        String v = "";
        while (recordSet.next()) {
            v += "<Equi_Import>\n" +
                    "<EQUNR>" + Util.null2String(recordSet.getString("sbbh")) + "</EQUNR>\n" +
                    "<KTX01>" + Util.null2String(recordSet.getString("sbmc")) + "</KTX01>\n" +
                    "<TPLNR>" + Util.null2String(recordSet.getString("drgnwzdm")) + "</TPLNR>\n" +
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
            Element MT_Equi_Allot_Export = root.element("Body").element("MT_Equi_Allot_Export");
            Iterator iterator = MT_Equi_Allot_Export.elementIterator("Equi_Export");
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

    public static void main(String[] args) {

    }
}
