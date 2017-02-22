package weaverjn.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaverjn.utils.WSClientUtils;

import java.util.HashMap;

/**
 * Created by zhaiyaqi on 2017/2/22.
 */
public class WorkflowShare {
    public static String getWfShareSql(String userid) {
        return getWfShareSql(Integer.parseInt(userid));
    }
    public static String getWfShareSql(int userid) {
        String sql = "";
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:weav=\"weaverjn.webservices\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <weav:getWfShareSql>\n" +
                "         <weav:in0>" + userid + "</weav:in0>\n" +
                "      </weav:getWfShareSql>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://192.168.1.108/services/DBUtilService";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            sql = root.element("Body").element("getWfShareSqlResponse").elementText("out");
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sql;
    }

    public static void main(String[] args) {
        System.out.println(getWfShareSql(1));
    }
}
