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
 * Created by zhaiyaqi on 2016/12/2.
 */
public class KOSTL2OABrowser extends BaseBean {
    private String com;
    public String run(String Company_Code, String KOSTL, String KTEXT, String company) {
        setCom(company);
        String datas = "";
        String BUKRS = "";
        if(company.equals("63")||company.equals("1")||company.equals("81")||company.equals("82")){
            BUKRS = "1010";
            datas = getDatas(Company_Code, KOSTL, KTEXT, BUKRS);
        }else if(company.equals("62")){
            BUKRS = "1030";
            datas = getDatas(Company_Code, KOSTL, KTEXT, BUKRS);
        }else if(company.equals("143")){
            BUKRS = "1060";
            datas = getDatas(Company_Code, KOSTL, KTEXT, BUKRS);
        }else if(company.equals("121")){
            BUKRS = "1070";
            datas = getDatas(Company_Code, KOSTL, KTEXT, BUKRS);
        }else if(company.equals("142")){
            BUKRS = "1630";
            datas = getDatas(Company_Code, KOSTL, KTEXT, BUKRS);
        } else if (company.equals("61")) {
            BUKRS = "1610";
            String s1 = getDatas(Company_Code, KOSTL, KTEXT, BUKRS);
            BUKRS = "1620";
            String s2 = getDatas(Company_Code, KOSTL, KTEXT, BUKRS);
            datas = s1.replace("</list>", "") + s2.replace("<list>", "");
        }
        return datas;
    }
    private String getDatas(String Company_Code, String KOSTL, String KTEXT, String BUKRS) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_KOSTL_OAReq>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID>I0033</INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP</Dest_System>\n" +
                "            <Company_Code>" + Company_Code + "</Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <Search_Condition>\n" +
                "            <KOSTL>" + KOSTL + "</KOSTL>\n" +
                "            <KTEXT>" + KTEXT + "</KTEXT>\n" +
                "            <BUKRS>" + BUKRS + "</BUKRS>\n" +
                "         </Search_Condition>\n" +
                "      </erp:MT_KOSTL_OAReq>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_KOSTL_OAReq_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
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
            Element MT_KOSTL2OA = root.element("Body").element("MT_KOSTL2OA");

            Iterator iterator = MT_KOSTL2OA.elementIterator("ListOfKostls");
            ArrayList<String> sqls = new ArrayList<String>();
            sqls.add("delete from KOSTL2OA where company=" + getCom());
            while (iterator.hasNext()) {
                Element e = (Element) iterator.next();
                s.append("<bean>");
                s.append("<KOSTL>").append(e.elementText("KOSTL")).append("</KOSTL>");
                s.append("<KTEXT>").append(e.elementText("KTEXT")).append("</KTEXT>");
                s.append("</bean>");
                sqls.add(getsql(e.elementText("KOSTL"), e.elementText("KTEXT")));
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

    private String getsql(String KOSTL, String KTEXT) {
        return "insert into KOSTL2OA(company,KOSTL,KTEXT) values(" +
                "'" + getCom() + "'," +
                "'" + KOSTL + "'," +
                "'" + KTEXT + "'" +
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
        KOSTL2OABrowser t = new KOSTL2OABrowser();
        t.run("", "", "", "63");
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }
}
