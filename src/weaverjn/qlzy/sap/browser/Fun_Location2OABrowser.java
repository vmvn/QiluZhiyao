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
 * Created by zhaiyaqi on 2016/12/27.
 */
public class Fun_Location2OABrowser extends BaseBean {
    public String run(String TPLNR, String PLTXT) {
        return getDatas(TPLNR, PLTXT);
    }
    private String getDatas(String TPLNR, String PLTXT) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_Fun_Location_Req>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID>I0050</INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <SerachInfo>\n" +
                "            <TPLNR>" + TPLNR + "</TPLNR>\n" +
                "            <PLTXT>" + PLTXT + "</PLTXT>\n" +
                "         </SerachInfo>\n" +
                "      </erp:MT_Fun_Location_Req>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_Fun_Location_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        String datas = parseData(response);
//        log(datas);
        return datas;
    }

    private String parseData(String string) {
        StringBuilder s = new StringBuilder();
        s.append("<list>");
        try {
            Document dom = DocumentHelper.parseText(string);
            Element root = dom.getRootElement();
            Element MT_KOSTL2OA = root.element("Body").element("MT_Fun_Location_Ret");

            Iterator iterator = MT_KOSTL2OA.elementIterator("Fun_Location_List");
            ArrayList<String> sqls = new ArrayList<String>();
            sqls.add("delete from Fun_Location2OA");
            while (iterator.hasNext()) {
                Element e = (Element) iterator.next();
                s.append("<bean>");
                s.append("<TPLNR>").append(e.elementText("TPLNR")).append("</TPLNR>");
                s.append("<PLTXT>").append(e.elementText("PLTXT")).append("</PLTXT>");
                s.append("</bean>");
                sqls.add(getsql(e.elementText("TPLNR"), e.elementText("PLTXT")));
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

    private String getsql(String TPLNR, String PLTXT) {
        return "insert into Fun_Location2OA(TPLNR,PLTXT) values(" +
                "'" + TPLNR + "'," +
                "'" + PLTXT + "'" +
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
        Fun_Location2OABrowser t = new Fun_Location2OABrowser();
        System.out.println(t.run("", "*"));
    }
}