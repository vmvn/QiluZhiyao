package weaverjn.qlzy.sap.browser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.general.BaseBean;
import weaverjn.qlzy.sap.WSClientUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by zhaiyaqi on 2017/1/4.
 */
public class ERP_Supplier2OABrowser extends BaseBean {
    public String run(String LIFNR, String LIFNR_NAME) {
        return getDatas(LIFNR, LIFNR_NAME);
    }
    private String getDatas(String LIFNR, String LIFNR_NAME) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_Supplier_Req>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID>I0051</INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <!--Optional:-->\n" +
                "         <Supplier_Req>\n" +
                "            <LIFNR>" + LIFNR + "</LIFNR>\n" +
                "            <LIFNR_NAME>" + LIFNR_NAME + "</LIFNR_NAME>\n" +
                "         </Supplier_Req>\n" +
                "      </erp:MT_Supplier_Req>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_Supplier_Req_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        String datas = parseData(response);
        datas = datas.replaceAll("&", "&amp;");
//        log(datas);
        return datas;
    }

    private String parseData(String string) {
        StringBuilder s = new StringBuilder();
        s.append("<list>");
        try {
            Document dom = DocumentHelper.parseText(string);
            Element root = dom.getRootElement();
            Element MT_KOSTL2OA = root.element("Body").element("MT_Supplier_Ret");

            Iterator iterator = MT_KOSTL2OA.elementIterator("Supplier_Ret");
            ArrayList<String> sqls = new ArrayList<String>();
            sqls.add("delete from ERP_Supplier2OA");
            while (iterator.hasNext()) {
                Element e = (Element) iterator.next();
                s.append("<bean>");
                s.append("<LIFNR>").append(e.elementText("LIFNR")).append("</LIFNR>");
                s.append("<LIFNR_NAME>").append(e.elementText("LIFNR_NAME")).append("</LIFNR_NAME>");
                s.append("</bean>");
                sqls.add(getsql(e.elementText("LIFNR"), e.elementText("LIFNR_NAME")));
            }
            exesql(sqls);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        s.append("</list>");
        return s.toString();
    }

    private void log(Object o) {
        writeLog(o);
        System.out.println(o);
    }

    private String getsql(String LIFNR, String LIFNR_NAME) {
        return "insert into ERP_Supplier2OA(LIFNR,LIFNR_NAME) values(" +
                "'" + LIFNR + "'," +
                "'" + LIFNR_NAME + "'" +
                ")";
    }

    private void exesql(ArrayList<String> arrayList) {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String username = "ecology";
        String password = "ecology";
        String url = "jdbc:oracle:thin:@192.168.1.109:1521:ecology";
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            statement = conn.createStatement();
            for (String s : arrayList) {
                statement.addBatch(s);
            }
            statement.executeBatch();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ERP_Supplier2OABrowser t = new ERP_Supplier2OABrowser();
        System.out.println(t.run("", "*"));
    }
}
