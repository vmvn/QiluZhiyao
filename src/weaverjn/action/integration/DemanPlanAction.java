package weaverjn.action.integration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaverjn.qlzy.sap.WSClientUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by zhaiyaqi on 2017/2/27.
 */
public class DemanPlanAction extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestid = requestInfo.getRequestid();
        String workflowid = requestInfo.getWorkflowid();
        RecordSet recordSet = new RecordSet();
        String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
        recordSet.executeSql(sql);
        recordSet.next();
        String tablename = recordSet.getString("tablename");

        sql = "select sapylh from " + tablename + " where requestid=" + requestid;
        recordSet.executeSql(sql);
        recordSet.next();
        String sapylh = Util.null2String(recordSet.getString("sapylh"));

        String response = send(sapylh);
        MT_DemanPlan_Ret ret = parseHttpResponse(response);
        if (!ret.getMsg_type().equals("S") || !ret.getMessage().contains("成功")) {
            requestInfo.getRequestManager().setMessageid("Message");
            requestInfo.getRequestManager().setMessagecontent(ret.getMessage());
        } else {
            write(ret, tablename, requestid);
        }
        return SUCCESS;
    }

    private String send(String issue_no) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_DemanPlan>\n" +
                "         <control_info>\n" +
                "            <INTF_ID>I0055</INTF_ID>\n" +
                "            <Src_System></Src_System>\n" +
                "            <Dest_System></Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </control_info>\n" +
                "         <issue_no>" + issue_no + "</issue_no>\n" +
                "      </erp:MT_DemanPlan>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_Deman_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        return WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
    }

    private void write(MT_DemanPlan_Ret ret, String table, String requestid) {
        RecordSet recordSet = new RecordSet();
        String sql = "update " + table + " set " +
                "ydlx='" + Move_Type(ret.getMOVE_TYPE()) + "', " +
                "cbzx='" + ret.getKOSTL() + "', " +
                "gc='" + ret.getWERKS() + "', " +
                "dfgc='" + ret.getUMWRK() + "', " +
                "dfkcd='" + ret.getUMLGO() + "', " +
                "nbdd='" + ret.getAUFNR() + "' " +
                "where requestid" + requestid;
        recordSet.executeSql(sql);

        ArrayList<Deman_List> lists = ret.getDeman_Lists();
        for (Deman_List list : lists) {
            sql = "insert " + table + "_dt1(hxmh,wlbh,wlmsmc,gc,fckcd,ggxh,lysl,jldw,account,kcph,bz) values(" +
                    "'" + list.getZLNNBR() + "', " +
                    "'" + list.getMATNR() + "', " +
                    "'" + list.getMAKTX() + "', " +
                    "'" + list.getWERKS() + "', " +
                    "'" + list.getLGORT() + "', " +
                    "'" + "" + "', " +
                    "'" + list.getMENGE() + "', " +
                    "'" + list.getMEINS() + "', " +
                    "'" + list.getACCOUNT() + "', " +
                    "'" + list.getCHARG() + "', " +
                    "'" + "" + "' " +
                    ")";
            recordSet.executeSql(sql);
        }
    }

    private String Move_Type(String s) {
        String move = "";
        if (s.equals("Z61")) {
            move = "0";
        } else if (s.equals("201")) {
            move = "1";
        } else if (s.equals("311")) {
            move = "2";
        } else if (s.equals("Z63")) {
            move = "3";
        }
        return move;
    }

    private MT_DemanPlan_Ret parseHttpResponse(String response) {
        MT_DemanPlan_Ret ret = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_DemanPlan_Ret");
            ret = new MT_DemanPlan_Ret();
            ret.setMsg_type(e.elementText("msg_type"));
            ret.setMessage(e.elementText("message"));
            ret.setISSUE_NO(e.elementText("ISSUE_NO"));
            ret.setMOVE_TYPE(e.elementText("MOVE_TYPE"));
            ret.setWERKS(e.elementText("WERKS"));
            ret.setKOSTL(e.elementText("KOSTL"));
            ret.setAUFNR(e.elementText("AUFNR"));
            ret.setUMWRK(e.elementText("UMWRK"));
            ret.setUMLGO(e.elementText("UMLGO"));

            ArrayList<Deman_List> lists = new ArrayList<Deman_List>();
            Iterator iterator = e.elementIterator("Deman_List");
            while (iterator.hasNext()) {
                Element element = (Element) iterator.next();
                Deman_List list = new Deman_List();
                list.setZLNNBR(element.elementText("ZLNNBR"));
                list.setMATNR(element.elementText("MATNR"));
                list.setMAKTX(element.elementText("MAKTX"));
                list.setMENGE(element.elementText("MENGE"));
                list.setMEINS(element.elementText("MEINS"));
                list.setWERKS(element.elementText("WERKS"));
                list.setLGORT(element.elementText("LGORT"));
                list.setCHARG(element.elementText("CHARG"));
                list.setREQ_DATE(element.elementText("REQ_DATE"));
                list.setACCOUNT(element.elementText("ACCOUNT"));

                lists.add(list);
            }
            ret.setDeman_Lists(lists);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) {
        DemanPlanAction t = new DemanPlanAction();
        String s = t.send("2499");
        System.out.println(s);
    }

    class MT_DemanPlan_Ret {
        private String msg_type;
        private String message;
        private String ISSUE_NO;
        private String MOVE_TYPE;
        private String WERKS;
        private String KOSTL;
        private String AUFNR;
        private String UMWRK;
        private String UMLGO;
        private ArrayList<Deman_List> Deman_Lists;

        public String getMsg_type() {
            return msg_type;
        }

        public void setMsg_type(String msg_type) {
            this.msg_type = msg_type;
        }

        public String getISSUE_NO() {
            return ISSUE_NO;
        }

        public void setISSUE_NO(String ISSUE_NO) {
            this.ISSUE_NO = ISSUE_NO;
        }

        public String getMOVE_TYPE() {
            return MOVE_TYPE;
        }

        public void setMOVE_TYPE(String MOVE_TYPE) {
            this.MOVE_TYPE = MOVE_TYPE;
        }

        public String getWERKS() {
            return WERKS;
        }

        public void setWERKS(String WERKS) {
            this.WERKS = WERKS;
        }

        public String getKOSTL() {
            return KOSTL;
        }

        public void setKOSTL(String KOSTL) {
            this.KOSTL = KOSTL;
        }

        public String getAUFNR() {
            return AUFNR;
        }

        public void setAUFNR(String AUFNR) {
            this.AUFNR = AUFNR;
        }

        public String getUMWRK() {
            return UMWRK;
        }

        public void setUMWRK(String UMWRK) {
            this.UMWRK = UMWRK;
        }

        public String getUMLGO() {
            return UMLGO;
        }

        public void setUMLGO(String UMLGO) {
            this.UMLGO = UMLGO;
        }

        public ArrayList<Deman_List> getDeman_Lists() {
            return Deman_Lists;
        }

        public void setDeman_Lists(ArrayList<Deman_List> deman_Lists) {
            Deman_Lists = deman_Lists;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    class Deman_List {
        private String ZLNNBR;
        private String MATNR;
        private String MAKTX;
        private String MENGE;
        private String MEINS;
        private String WERKS;
        private String LGORT;
        private String CHARG;
        private String REQ_DATE;
        private String ACCOUNT;

        public String getZLNNBR() {
            return ZLNNBR;
        }

        public void setZLNNBR(String ZLNNBR) {
            this.ZLNNBR = ZLNNBR;
        }

        public String getMATNR() {
            return MATNR;
        }

        public void setMATNR(String MATNR) {
            this.MATNR = MATNR;
        }

        public String getMAKTX() {
            return MAKTX;
        }

        public void setMAKTX(String MAKTX) {
            this.MAKTX = MAKTX;
        }

        public String getMENGE() {
            return MENGE;
        }

        public void setMENGE(String MENGE) {
            this.MENGE = MENGE;
        }

        public String getMEINS() {
            return MEINS;
        }

        public void setMEINS(String MEINS) {
            this.MEINS = MEINS;
        }

        public String getWERKS() {
            return WERKS;
        }

        public void setWERKS(String WERKS) {
            this.WERKS = WERKS;
        }

        public String getLGORT() {
            return LGORT;
        }

        public void setLGORT(String LGORT) {
            this.LGORT = LGORT;
        }

        public String getCHARG() {
            return CHARG;
        }

        public void setCHARG(String CHARG) {
            this.CHARG = CHARG;
        }

        public String getREQ_DATE() {
            return REQ_DATE;
        }

        public void setREQ_DATE(String REQ_DATE) {
            this.REQ_DATE = REQ_DATE;
        }

        public String getACCOUNT() {
            return ACCOUNT;
        }

        public void setACCOUNT(String ACCOUNT) {
            this.ACCOUNT = ACCOUNT;
        }
    }
}
