package weaverjn.qlzy.sap.webservice.oa.workflow;

import weaver.general.BaseBean;
import weaverjn.qlzy.sap.WSClientUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by zhaiyaqi on 2016/12/23.
 */
public class ZSGDWWXSQsendStatus extends BaseBean {
    public void sendStatus(ZGSDWWXSQrequestid[] a) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_ServiceReq_Ret>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID>I0049</INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time>" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "</Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <!--Zero or more repetitions:-->\n";
        String lines = "";
        for (ZGSDWWXSQrequestid i : a) {
            lines += "         <ServiceReq_Ret>\n" +
                    "            <REQ_NO>" + i.getREQ_NO() + "</REQ_NO>\n" +
                    "            <Message>\n" +
                    "               <MSG_TYPE>" + getMSG_TYPE(i.getRequestid()) + "</MSG_TYPE>\n" +
                    "               <MESSAGE></MESSAGE>\n" +
                    "            </Message>\n" +
                    "         </ServiceReq_Ret>\n";
        }
        request += lines;
        request += "      </erp:MT_ServiceReq_Ret>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        log("----<ZSGDWWXSQsendStatus>" + request);
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_ServiceReq_Ret_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        log("----<ZSGDWWXSQsendStatus>" + response);
    }

    private String getMSG_TYPE(String requestID) {
        int i = Integer.parseInt(requestID);
        return i > 0 ? "S" : "E";
    }

    private void log(Object o) {
        System.out.println(o);
        writeLog(o);
    }
}
