package weaverjn.qlzy.sap.browser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaverjn.qlzy.sap.WSClientUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by zhaiyaqi on 2016/12/1.
 */
public class INT_ORDER2OABrowser extends BaseBean {
    private String com;
    public String run(String Company_Code, String AUFNR, String KTEXT, String company) {
        setCom(company);
        String datas = "";
        String BUKRS = "";
        if(company.equals("63")){
            BUKRS = "1010";
            datas = getDatas(Company_Code, AUFNR, KTEXT, BUKRS);
        }else if(company.equals("62")){
            BUKRS = "1030";
            datas = getDatas(Company_Code, AUFNR, KTEXT, BUKRS);
        }else if(company.equals("143")){
            BUKRS = "1060";
            datas = getDatas(Company_Code, AUFNR, KTEXT, BUKRS);
        }else if(company.equals("121")){
            BUKRS = "1070";
            datas = getDatas(Company_Code, AUFNR, KTEXT, BUKRS);
        }else if(company.equals("142")){
            BUKRS = "1630";
            datas = getDatas(Company_Code, AUFNR, KTEXT, BUKRS);
        } else if (company.equals("61")) {
            BUKRS = "1610";
            String s1 = getDatas(Company_Code, AUFNR, KTEXT, BUKRS);
            BUKRS = "1620";
            String s2 = getDatas(Company_Code, AUFNR, KTEXT, BUKRS);
            datas = s1.replace("</list>", "") + s2.replace("<list>", "");
        }
        return datas;
    }

    private String getDatas(String Company_Code, String AUFNR, String KTEXT, String BUKRS) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_IntOrder_OAReq>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID>I0034</INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP</Dest_System>\n" +
                "            <Company_Code>" + Company_Code + "</Company_Code>\n" +
                "            <Send_Time>" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "</Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <Search_Condition>\n" +
                "            <AUFNR>" + AUFNR + "</AUFNR>\n" +
                "            <KTEXT>" + KTEXT + "</KTEXT>\n" +
                "            <BUKRS>" + BUKRS + "</BUKRS>\n" +
                "         </Search_Condition>\n" +
                "      </erp:MT_IntOrder_OAReq>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_IntOrder_OAReq_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        String datas = parseData(response);
        log(datas);
        return datas;
    }

    private String parseData(String string) {
        StringBuffer s = new StringBuffer();
        s.append("<list>");
        try {
            Document dom = DocumentHelper.parseText(string);
            Element root = dom.getRootElement();
            Element MT_IntOrder2OA = root.element("Body").element("MT_IntOrder2OA");

            Iterator iterator = MT_IntOrder2OA.elementIterator("ListOfIntOrders");
            ArrayList<String> sqls = new ArrayList<String>();
            sqls.add("delete from INT_ORDER2OA where company=" + getCom());
            while (iterator.hasNext()) {
                Element e = (Element) iterator.next();
                s.append("<bean>");
                s.append("<AUFNR>").append(e.elementText("AUFNR")).append("</AUFNR>");
                s.append("<KTEXT>").append(e.elementText("KTEXT")).append("</KTEXT>");
                s.append("</bean>");
                sqls.add(getsql(e.elementText("AUFNR"), e.elementText("KTEXT")));
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

    private String getsql(String AUFNR, String KTEXT) {
        return "insert into INT_ORDER2OA(company,AUFNR,KTEXT) values(" +
                "'" + getCom() + "'," +
                "'" + AUFNR + "'," +
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
        INT_ORDER2OABrowser t = new INT_ORDER2OABrowser();
        String s = t.run("", "", "", "63");
        System.out.println(s);
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }
}
