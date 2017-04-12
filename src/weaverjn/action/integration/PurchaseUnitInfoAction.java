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
import weaver.workflow.request.RequestManager;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

import java.util.Map;

/**
 * Created by zhaiyaqi on 2017/4/5.
 */
public class PurchaseUnitInfoAction extends BaseBean implements Action {
    private String vkorg;
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if (!src.equals("reject")) {
            Map<String, String> mainTableData = utils.getMainTableData(requestInfo.getMainTableInfo());
            String ghfmc = mainTableData.get("ghfmc");
            String sql = "select * from uf_wrghdwzl where id='" + ghfmc + "'";
            RecordSet recordSet = new RecordSet();
            recordSet.executeSql(sql);
            if (recordSet.next()) {
                String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <erp:MT_PurchaseUnitInfo>\n" +
                        "         <ControlInfo>\n" +
                        "            <INTF_ID></INTF_ID>\n" +
                        "            <Src_System>OA</Src_System>\n" +
                        "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                        "            <Company_Code></Company_Code>\n" +
                        "            <Send_Time></Send_Time>\n" +
                        "         </ControlInfo>\n" +
                        "         <kunnr>" + Util.null2String(recordSet.getString("ghfbh")) + "</kunnr>\n" +
                        "         <zfddbr>" + utils.getFieldValue("hrmresource", "lastname", recordSet.getString("fddbr")) + "</zfddbr>\n" +
                        "         <VKORG>" + this.vkorg + "</VKORG>\n" +
                        "         <ZSTATE_HG>" + Util.null2String(recordSet.getString("ghdwzt")) + "</ZSTATE_HG>\n" +
                        "         <ZYYZZ_YXQ>" + Util.null2String(recordSet.getString("zzjgyxqz")) + "</ZYYZZ_YXQ>\n" +
                        "         <ZXKZ_FZRQ>" + Util.null2String(recordSet.getString("xkzfzrq")) + "</ZXKZ_FZRQ>\n" +
                        "         <ZYYZZ_FZRQ>" + Util.null2String(recordSet.getString("zzjgfzrq")) + "</ZYYZZ_FZRQ>\n" +
                        "         <ZXKZ_MC>" + utils.getSelectName("26850", Util.null2String(recordSet.getString("xkzmclx"))) + "</ZXKZ_MC>\n" +
                        "         <ZXKZ_YXQ>" + Util.null2String(recordSet.getString("xkzyxqz")) + "</ZXKZ_YXQ>\n";

                StringBuilder jyfsXML = new StringBuilder();
                String jyfw = Util.null2String(recordSet.getString("jyfw"));
                if (!jyfw.isEmpty()) {
                    String[] arr = jyfw.split(",");
                    for (int i = 0; i < Math.min(20, arr.length); i++) {
                        jyfsXML.append("         <ZJYFW").append(i + 1).append(">").append(getWRJYFSBH(arr[i])).append("</ZJYFW").append(i + 1).append(">\n");
                    }
                }
                soapHttpRequest += jyfsXML.toString();
                soapHttpRequest += "         <ZXKZ_BGJL>" + Util.null2String(recordSet.getString("bgjl")) + "</ZXKZ_BGJL>\n" +
                        "         <ZCKDZ1></ZCKDZ1>\n" +
                        "         <ZCKDZXQ1></ZCKDZXQ1>\n" +
                        "         <ZCKDZ2></ZCKDZ2>\n" +
                        "         <ZCKDZXQ2></ZCKDZXQ2>\n" +
                        "         <ZCKDZ3></ZCKDZ3>\n" +
                        "         <ZCKDZXQ3></ZCKDZXQ3>\n" +
                        "         <ZCKDZ4></ZCKDZ4>\n" +
                        "         <ZCKDZXQ4></ZCKDZXQ4>\n" +
                        "         <ZCKDZ5></ZCKDZ5>\n" +
                        "         <ZCKDZXQ5></ZCKDZXQ5>\n" +
                        "         <ZCKDZ6></ZCKDZ6>\n" +
                        "         <ZCKDZXQ6></ZCKDZXQ6>\n" +
                        "         <ZCKDZ7></ZCKDZ7>\n" +
                        "         <ZCKDZXQ7></ZCKDZXQ7>\n" +
                        "         <ZCKDZ8></ZCKDZ8>\n" +
                        "         <ZCKDZXQ8></ZCKDZXQ8>\n" +
                        "         <ZCKDZ9></ZCKDZ9>\n" +
                        "         <ZCKDZXQ9></ZCKDZXQ9>\n" +
                        "         <ZCKDZ10></ZCKDZ10>\n" +
                        "         <ZCKDZXQ10></ZCKDZXQ10>\n" +
                        "         <ZGSP_YXQ>" + Util.null2String(recordSet.getString("gspyxqz")) + "</ZGSP_YXQ>\n" +
                        "         <ZZBXY>" + utils.getSelectName("26864", Util.null2String(recordSet.getString("zlbzxys"))) + "</ZZBXY>\n" +
                        "         <ZZBXY_YXQ>" + Util.null2String(recordSet.getString("yxqz")) + "</ZZBXY_YXQ>\n" +
                        "         <ZZLBZTX>" + utils.getSelectName("26866", Util.null2String(recordSet.getString("zltxdcb"))) + "</ZZLBZTX>\n" +
                        "         <ZDTXQ>" + Util.null2String(recordSet.getString("dtyxq")) + "</ZDTXQ>\n" +
                        "      </erp:MT_PurchaseUnitInfo>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
                String endpoint = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_PurchaseUnitInfo_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
                String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, utils.getUsername(), utils.getPassword());
                MT_PurchaseUnitInfo_Msg msg = parse(soapHttpResponse);
                if (msg != null) {
                    if (msg.getMESSAGE_TYPE().equals("E")) {
                        requestManager.setMessageid("E");
                        requestManager.setMessagecontent(msg.getMESSAGE());
                    }
                } else {
                    requestManager.setMessageid("ERROR");
                    requestManager.setMessagecontent(soapHttpResponse);
                }
            }
        }
        return SUCCESS;
    }

    private MT_PurchaseUnitInfo_Msg parse(String response) {
        MT_PurchaseUnitInfo_Msg msg = null;
        try{
            Document document = DocumentHelper.parseText(response);
            Element root = document.getRootElement();
            Element e = root.element("Body").element("MT_PurchaseUnitInfo_Msg");
            msg = new MT_PurchaseUnitInfo_Msg();
            msg.setMESSAGE_TYPE(e.elementText("MESSAGE_TYPE"));
            msg.setMESSAGE(e.elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    private String getWRJYFSBH(String id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select wrjyfsbh from uf_wrjyfs where id='" + id + "'";
        recordSet.executeSql(sql);
        String wrjyfsbh = "";
        if (recordSet.next()) {
            wrjyfsbh = Util.null2String(recordSet.getString("wrjyfsbh"));
        }
        return wrjyfsbh;
    }

    public String getVkorg() {
        return vkorg;
    }

    public void setVkorg(String vkorg) {
        this.vkorg = vkorg;
    }

    class MT_PurchaseUnitInfo_Msg {
        private String MESSAGE_TYPE;
        private String MESSAGE;

        public String getMESSAGE_TYPE() {
            return MESSAGE_TYPE;
        }

        public void setMESSAGE_TYPE(String MESSAGE_TYPE) {
            this.MESSAGE_TYPE = MESSAGE_TYPE;
        }

        public String getMESSAGE() {
            return MESSAGE;
        }

        public void setMESSAGE(String MESSAGE) {
            this.MESSAGE = MESSAGE;
        }
    }
}
