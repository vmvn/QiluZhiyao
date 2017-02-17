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
 * Created by zhaiyaqi on 2016/12/1.
 */
public class OAMaterialSearchBrowser extends BaseBean {
    private String com;
    public String run(String Company_Code, String MATNR, String MAKTX, String company, String MATKL) {
        setCom(company);
        String datas = "";
        String WERKS;
        if(company.equals("63")||company.equals("1")||company.equals("81")||company.equals("82")){
            WERKS = "1010";
            datas = getDatas(Company_Code, MATNR, MAKTX, WERKS, MATKL);
        }else if(company.equals("62")){
            WERKS = "1030";
            datas = getDatas(Company_Code, MATNR, MAKTX, WERKS, MATKL);
        }else if(company.equals("143")){
            WERKS = "1060";
            datas = getDatas(Company_Code, MATNR, MAKTX, WERKS, MATKL);
        }else if(company.equals("121")){
            WERKS = "1070";
            datas = getDatas(Company_Code, MATNR, MAKTX, WERKS, MATKL);
        }else if(company.equals("142")){
            WERKS = "1630";
            datas = getDatas(Company_Code, MATNR, MAKTX, WERKS, MATKL);
        } else if (company.equals("61")) {
            WERKS = "1610";
            String s1 = getDatas(Company_Code, MATNR, MAKTX, WERKS, MATKL);
            WERKS = "1620";
            String s2 = getDatas(Company_Code, MATNR, MAKTX, WERKS, MATKL);
            datas = s1.replace("</list>", "") + s2.replace("<list>", "");
        }
        return datas;
    }

    private String getDatas(String Company_Code, String MATNR, String MAKTX, String WERKS, String MATKL) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_OAMaterialSearch>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID>I0031</INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP</Dest_System>\n" +
                "            <Company_Code>" + Company_Code + "</Company_Code>\n" +
                "            <Send_Time>20161201131310</Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <Search_Condition>\n" +
                "            <MATNR>" + MATNR + "</MATNR>\n" +
                "            <MAKTX>" + MAKTX + "</MAKTX>\n" +
                "            <WERKS>" + WERKS + "</WERKS>\n" +
                "            <MATKL>" + MATKL + "</MATKL>\n" +
                "         </Search_Condition>\n" +
                "      </erp:MT_OAMaterialSearch>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_OAMaterialSearch_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        log("----<M>" + response);
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
            Element MT_Mat_Data2OA = root.element("Body").element("MT_Mat_Data2OA");

            Iterator iterator = MT_Mat_Data2OA.elementIterator("Mat_Data");
            ArrayList<String> sqls = new ArrayList<String>();
            sqls.add("delete from OAMaterialSearch where company=" + getCom());
            while (iterator.hasNext()) {
                Element e = (Element) iterator.next();
                s.append("<bean>");
                s.append("<MATNR>").append(e.elementText("MATNR")).append("</MATNR>");
                s.append("<MAKTX>").append(e.elementText("MAKTX")).append("</MAKTX>");
                s.append("<MEINS>").append(e.elementText("MEINS")).append("</MEINS>");
                s.append("<WERKS>").append(e.elementText("WERKS")).append("</WERKS>");
                s.append("<MATKL>").append(e.elementText("MATKL")).append("</MATKL>");
                s.append("<GROES>").append(e.elementText("GROES")).append("</GROES>");
                s.append("</bean>");
                sqls.add(getsql(e.elementText("MATNR"), e.elementText("MAKTX"), e.elementText("MEINS"), e.elementText("WERKS"), e.elementText("MATKL"), e.elementText("GROES")));
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

    private String getsql(String MATNR, String MAKTX, String MEINS, String WERKS, String MATKL, String GROES) {
        return "insert into OAMaterialSearch(company,MATNR,MAKTX,MEINS,WERKS,MATKL,GROES) values(" +
                "'" + getCom() + "'," +
                "'" + MATNR + "'," +
                "'" + MAKTX + "'," +
                "'" + MEINS + "'," +
                "'" + WERKS + "'," +
                "'" + MATKL + "'," +
                "'" + GROES + "'" +
                ")";
    }

    public static void main(String[] args) {
        OAMaterialSearchBrowser t = new OAMaterialSearchBrowser();
        String s = t.run("", "", "", "63", "");
        System.out.println(s);
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }
}
