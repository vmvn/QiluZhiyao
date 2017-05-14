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
        if (!Util.null2String(this.vkorg).isEmpty()) {
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
                        "         <ZYYZZ_YXQ>" + Util.null2String(recordSet.getString("yyzzyxqz")) + "</ZYYZZ_YXQ>\n" +
                        "         <ZYYZZZCH>" + Util.null2String(recordSet.getString("yyzzzch")) + "</ZYYZZZCH>\n" +
                        "         <ZXKZ_FZRQ>" + Util.null2String(recordSet.getString("xkzfzrq")) + "</ZXKZ_FZRQ>\n" +
                        "         <ZYYZZ_FZRQ>" + Util.null2String(recordSet.getString("yyzzfzrq")) + "</ZYYZZ_FZRQ>\n" +
                        "         <ZXKZ_MC>" + utils.getSelectName("26850", Util.null2String(recordSet.getString("xkzmclx"))) + "</ZXKZ_MC>\n" +
                        "         <ZXKZ_YXQ>" + Util.null2String(recordSet.getString("xkzyxqz")) + "</ZXKZ_YXQ>\n" +
                        JYFW(ghfmc) +
                        "         <ZXKZ_BGJL>" + Util.null2String(recordSet.getString("bgjl")) + "</ZXKZ_BGJL>\n" +
                        CKDZ(ghfmc) +
                        "         <ZGSP_YXQ>" + Util.null2String(recordSet.getString("gspyxqz")) + "</ZGSP_YXQ>\n" +
                        "         <ZZBXY>" + utils.getSelectName("26864", Util.null2String(recordSet.getString("zlbzxys"))) + "</ZZBXY>\n" +
                        "         <ZZBXY_YXQ>" + Util.null2String(recordSet.getString("yxqz")) + "</ZZBXY_YXQ>\n" +
                        "         <ZZLBZTX>" + utils.getSelectName("26866", Util.null2String(recordSet.getString("zltxdcb"))) + "</ZZLBZTX>\n" +
                        "         <ZDTXQ>" + Util.null2String(recordSet.getString("dtyxq")) + "</ZDTXQ>\n" +
                        "         <sale_view>\n" +
                        "            <VKORG>" + this.vkorg + "</VKORG>\n" +
                        "            <VTWEG>" + utils.getFieldValue("uf_wrfxqd", "fxqdbh", Util.null2String(recordSet.getString("fxqd"))) + "</VTWEG>\n" +
                        "            <SPART>" + utils.getFieldValue("uf_sapjcsj_cpz", "cpzdm", Util.null2String(recordSet.getString("cpz"))) + "</SPART>\n" +
                        "            <BZIRK>" + utils.getFieldValue("uf_wrxsqd", "xsqdbh", Util.null2String(recordSet.getString("xsqy"))) + "</BZIRK>\n" +
                        "            <KONDA>" + Util.null2String(recordSet.getString("jgz")) + "</KONDA>\n" +
                        "            <KALKS>" + Util.null2String(recordSet.getString("djgc")) + "</KALKS>\n" +
                        "            <VWERK>" + utils.getFieldValue("uf_sapjcsj_gc", "gcbm", Util.null2String(recordSet.getString("jhgc"))) + "</VWERK>\n" +
                        "            <VSBED>" + Util.null2String(recordSet.getString("zytj")) + "</VSBED>\n" +
                        "            <INCO1>" + utils.getFieldValue("uf_wrgjmysyjstz", "gjmysyjstzbh", Util.null2String(recordSet.getString("gjmysyjstz"))) + "</INCO1>\n" +
                        "            <INCO2_L>" + Util.null2String(recordSet.getString("gjmysyjstz1")) + "</INCO2_L>\n" +
                        "            <ZTERM>" + utils.getFieldValue("uf_wrfktj", "fktjbh", Util.null2String(recordSet.getString("fktj"))) + "</ZTERM>\n" +
                        "            <KTGRD>" + utils.getFieldValue("uf_wrzhfpz", "zhfpzbh", Util.null2String(recordSet.getString("zhfpz"))) + "</KTGRD>\n" +
                        "            <AKONT>" + utils.getFieldValue("uf_wrtykm", "tykmbh", Util.null2String(recordSet.getString("tykm"))) + "</AKONT>\n" +
                        "            <TAXKD></TAXKD>\n" +
                        "         </sale_view>\n" +
                        "      </erp:MT_PurchaseUnitInfo>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
                writeLog(soapHttpRequest);
                String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
                String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, utils.getUsername(), utils.getPassword());
                writeLog(soapHttpResponse);
                MT_PurchaseUnitInfo_Msg msg = parse(soapHttpResponse);
                if (msg != null) {
                    if (msg.getMESSAGE_TYPE().equals("E")) {
                        requestManager.setMessageid("E");
                        requestManager.setMessagecontent(msg.getMESSAGE());
                    }
                } else {
                    requestManager.setMessageid("SAP 返回信息");
                    requestManager.setMessagecontent(soapHttpResponse);
                }
            }
        }else{
            writeLog("vkorg is null");
            requestManager.setMessageid("WARNING");
            requestManager.setMessagecontent("vkorg is null");
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

    private String CKDZ(String id) {
        String sql = "select * from uf_wrghdwzl_dt1 where mainid='" + id + "'";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        while (recordSet.next()) {
            stringBuilder.append("         <ZCKDZ").append(i).append(">").append(Util.null2String(recordSet.getString("ckdz"))).append("</ZCKDZ").append(i).append(">\n");
            stringBuilder.append("         <ZCKDZXQ").append(i).append(">").append(Util.null2String(recordSet.getString("yxqz"))).append("</ZCKDZXQ").append(i).append(">\n");
            i++;
        }
        for (; i <= 10; i++) {
            stringBuilder.append("         <ZCKDZ").append(i).append(">").append("").append("</ZCKDZ").append(i).append(">\n");
            stringBuilder.append("         <ZCKDZXQ").append(i).append(">").append("").append("</ZCKDZXQ").append(i).append(">\n");
        }
        return stringBuilder.toString();
    }

    private String JYFW(String id) {
        String sql = "select * from uf_wrghdwzl_dt2 where mainid='" + id + "'";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        while (recordSet.next()) {
            stringBuilder.append("         <ZJYFW").append(i).append(">").append(Util.null2String(recordSet.getString("jyfw"))).append("</ZJYFW").append(i).append(">\n");
            i++;
        }
        for (; i <= 20; i++) {
            stringBuilder.append("         <ZJYFW").append(i).append(">").append("").append("</ZJYFW").append(i).append(">\n");
        }
        return stringBuilder.toString();
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
