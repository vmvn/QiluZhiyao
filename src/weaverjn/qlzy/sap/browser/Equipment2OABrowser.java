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
 * Created by zhaiyaqi on 2016/11/30.
 */
public class Equipment2OABrowser extends BaseBean {
    private String c;

    public String run(String EQUNR, String company, String EQKTX, String HERST, String TYPBZ, String KOSTL, String TIDNR) {
        setC(company);
        String datas = "";
        String WERKS = "";
        if (EQUNR.isEmpty()) {
            EQUNR = "";
        }
        if (EQKTX.isEmpty()) {
            EQKTX = "*";
        }
        if (HERST.isEmpty()) {
            HERST = "*";
        }
        if (TYPBZ.isEmpty()) {
            TYPBZ = "*";
        }
        if (KOSTL.isEmpty()) {
            KOSTL = "*";
        }
        if (TIDNR.isEmpty()) {
            TIDNR = "*";
        }
        if(company.equals("63")||company.equals("1")||company.equals("82")){
            WERKS = "1010";
            datas = getDatas(EQUNR, WERKS, EQKTX, HERST, TYPBZ, KOSTL, TIDNR);
        }else if(company.equals("62")){
            WERKS = "1030";
            datas = getDatas(EQUNR, WERKS, EQKTX, HERST, TYPBZ, KOSTL, TIDNR);
        }else if(company.equals("143")){
            WERKS = "1060";
            datas = getDatas(EQUNR, WERKS, EQKTX, HERST, TYPBZ, KOSTL, TIDNR);
        }else if(company.equals("121")){
            WERKS = "1070";
            datas = getDatas(EQUNR, WERKS, EQKTX, HERST, TYPBZ, KOSTL, TIDNR);
        }else if(company.equals("142")){
            WERKS = "1630";
            datas = getDatas(EQUNR, WERKS, EQKTX, HERST, TYPBZ, KOSTL, TIDNR);
        } else if (company.equals("61")) {
            WERKS = "1610";
            String s1 = getDatas(EQUNR, WERKS, EQKTX, HERST, TYPBZ, KOSTL, TIDNR);
            WERKS = "1620";
            String s2 = getDatas(EQUNR, WERKS, EQKTX, HERST, TYPBZ, KOSTL, TIDNR);
            datas = s1.replace("</list>", "") + s2.replace("<list>", "");
        }
        return datas;
    }

    private String getDatas(String EQUNR, String WERKS, String EQKTX, String HERST, String TYPBZ, String KOSTL, String TIDNR) {
        String datas;
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_Equi_Par>\n" +
                "         <EQUNR>" + EQUNR + "</EQUNR>\n" +
                "         <WERKS>" + WERKS + "</WERKS>\n" +
                "         <EQKTX>" + EQKTX + "</EQKTX>\n" +
                "         <HERST>" + HERST + "</HERST>\n" +
                "         <TYPBZ>" + TYPBZ + "</TYPBZ>\n" +
                "         <KOSTL>" + KOSTL + "</KOSTL>\n" +
                "         <TIDNR>" + TIDNR + "</TIDNR>\n" +
                "      </erp:MT_Equi_Par>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_Equi2OA_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        datas = parseData(response);
        datas = datas.replaceAll("&", "&amp;");
//        log(datas);
        return datas;
    }
    private String parseData(String string) {
        StringBuffer s = new StringBuffer();
        s.append("<list>");
        try {
            Document dom = DocumentHelper.parseText(string);
            Element root = dom.getRootElement();
            Element MT_Equi2OA = root.element("Body").element("MT_Equi2OA");
            Element MSG = MT_Equi2OA.element("MSG");
            String MSG_TYPE = MSG.elementText("MSG_TYPE");
            String MESSAGE = MSG.elementText("MESSAGE");
            if ("S".equals(MSG_TYPE) && "查询成功".equals(MESSAGE)) {
                Iterator iterator = MT_Equi2OA.elementIterator("Equi_List");
                ArrayList<String> sqls = new ArrayList<String>();
                sqls.add("delete from Equipment2OA where company=" + getC());
                while (iterator.hasNext()) {
                    Element e = (Element) iterator.next();
                    s.append("<bean>");
                    s.append("<EQUNR>").append(e.elementText("EQUNR")).append("</EQUNR>");
                    s.append("<EQKTX>").append(e.elementText("EQKTX")).append("</EQKTX>");
                    s.append("<EQART>").append(e.elementText("EQART")).append("</EQART>");
                    s.append("<TYPBZ>").append(e.elementText("TYPBZ")).append("</TYPBZ>");
                    s.append("<INBDT>").append(e.elementText("INBDT")).append("</INBDT>");
                    s.append("<TPLNR>").append(e.elementText("TPLNR")).append("</TPLNR>");
                    s.append("<HERST>").append(e.elementText("HERST")).append("</HERST>");
                    s.append("<TIDNR>").append(e.elementText("TIDNR")).append("</TIDNR>");
                    s.append("<KOSTL>").append(e.elementText("KOSTL")).append("</KOSTL>");
                    s.append("<ANLNR>").append(e.elementText("ANLNR")).append("</ANLNR>");
                    s.append("<ANLN2>").append(e.elementText("ANLN2")).append("</ANLN2>");
                    s.append("<SRC_VALUE>").append(e.elementText("SRC_VALUE")).append("</SRC_VALUE>");
                    s.append("<NET_VALUE>").append(e.elementText("NET_VALUE")).append("</NET_VALUE>");
                    s.append("<INGRP>").append(e.elementText("INGRP")).append("</INGRP>");
                    s.append("</bean>");
                    String a = getsql(
                            e.elementText("EQUNR"),
                            e.elementText("EQKTX"),
                            e.elementText("EQART"),
                            e.elementText("TYPBZ"),
                            e.elementText("INBDT"),
                            e.elementText("TPLNR"),
                            e.elementText("HERST"),
                            e.elementText("TIDNR"),
                            e.elementText("KOSTL"),
                            e.elementText("ANLNR"),
                            e.elementText("ANLN2"),
                            e.elementText("SRC_VALUE"),
                            e.elementText("NET_VALUE")
                    );
                    sqls.add(a);
                }
                exesql(sqls);
            }
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

    private String getsql(String EQUNR, String EQKTX, String EQART, String TYPBZ, String INBDT, String TPLNR,
                           String HERST, String TIDNR, String KOSTL, String ANLNR, String ANLN2, String SRC_VALUE, String NET_VALUE) {

        return "insert into Equipment2OA(company,EQUNR,EQKTX,EQART,TYPBZ,INBDT,TPLNR,HERST,TIDNR,KOSTL,ANLNR,ANLN2,SRC_VALUE,NET_VALUE) values(" +
                "'" + getC() + "'," +
                "'" + EQUNR + "'," +
                "'" + EQKTX + "'," +
                "'" + EQART + "'," +
                "'" + TYPBZ + "'," +
                "'" + INBDT + "'," +
                "'" + TPLNR + "'," +
                "'" + HERST + "'," +
                "'" + TIDNR + "'," +
                "'" + KOSTL + "'," +
                "'" + ANLNR + "'," +
                "'" + ANLN2 + "'," +
                "'" + SRC_VALUE + "'," +
                "'" + NET_VALUE + "'" +
                ")";
    }

    public static void main(String[] args) {
        Equipment2OABrowser t = new Equipment2OABrowser();
        String s = t.run("", "63", "", "", "", "", "");
        System.out.println(s);
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }
}
