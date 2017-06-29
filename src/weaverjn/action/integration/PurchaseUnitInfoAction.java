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
import weaverjn.crm.main.YXOAKH;
import weaverjn.crm.main.YXOAKHDZ;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

import java.util.Map;

/**
 * Created by zhaiyaqi on 2017/4/5.
 */
public class PurchaseUnitInfoAction extends BaseBean implements Action {
    private String vkorg;
//    private String xkzmclx;//26850
//    private String zlbzxys;//26864
//    private String zltxdcb;//26866
    private String fxqd;//uf_wrfxqd
    private String xsqy;//uf_wrxsqd
    private String gjmysyjstz;//uf_wrgjmysyjstz
    private String fktj;//uf_wrfktj
    private String zhfpz;//uf_wrzhfpz
    private String tykm;//uf_wrtykm
    private String uf;//uf_wrghdwzl

    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        writeLog("购货单位资料run>>>>>>>");

        Map<String, String> mainTableData = utils.getMainTableData(requestInfo.getMainTableInfo());
        int billid = requestManager.getFormid();//流程表单Id

        String workflowType = requestManager.getWorkflowtype();
        this.vkorg = utils.org(workflowType);
        writeLog("购货单位vkorg：" + vkorg );

        String ghfmc = mainTableData.get("ghdwmc1") == null ? mainTableData.get("ghfmc") : mainTableData.get("ghdwmc1");
        String field = mainTableData.get("ghdwmc1") == null ? "ghfmc" : "ghdwmc1";

        writeLog("util.getModTableName(field, billid):" + utils.getModTableName(field, billid));
        setUf(utils.getModTableName(field, billid));
//            writeLog("购货单位资料建模表名：" + getUf() );
        int uf_billId = utils.getBillIdByTableName(getUf());//表单建模billId
        setFxqd(utils.getModTableName("fxqd", uf_billId));
//            writeLog("购货单位资料建模表名：" + "  fxqd: " + getFxqd());
        setXsqy(utils.getModTableName("xsqy", uf_billId));
//            writeLog("购货单位资料建模表名：" +"  xsqy: " + getXsqy() );
        setGjmysyjstz(utils.getModTableName("gjmysyjstz", uf_billId));
//            writeLog("购货单位资料建模表名：" +  "  gjmysyjstz: " + getGjmysyjstz());
        setFktj(utils.getModTableName("fktj", uf_billId));
//            writeLog("购货单位资料建模表名：" + "  fktj: " + getFktj() );
        setZhfpz(utils.getModTableName("zhfpz", uf_billId));
//            writeLog("购货单位资料建模表名：" + "  zhfpz: " + getZhfpz() );
        setTykm(utils.getModTableName("tykm", uf_billId));
//            writeLog("购货单位资料建模表名：" + " tykm: " + getTykm());
        String sql = "select * from " + getUf() + " where id='" + ghfmc + "'";
//            writeLog("购货单位基础资料中查询sql：" + sql);
        RecordSet rs = new RecordSet();
        rs.executeSql(sql);
        if (rs.next()) {
            String hg = Util.null2String(rs.getString("ghdwzt"));
            if(hg.equals("1")){
                hg = "Y";
            }else{
                hg = "N";
            }
            String sfl = Util.null2String(rs.getString("sfl"));
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
                    "         <kunnr>" + Util.null2String(rs.getString("ghfbh")) + "</kunnr>\n" +
//                        "         <zfddbr>" + util.getFieldValue("hrmresource", "lastname", recordSet.getString("fddbr")) + "</zfddbr>\n" +
                    "         <zfddbr>" + Util.null2String(rs.getString("fddbr")) + "</zfddbr>\n" +
                    "         <VKORG>" + this.vkorg + "</VKORG>\n" +
                    "         <ZSTATE_HG>" + hg + "</ZSTATE_HG>\n" +
                    "         <ZYYZZ_YXQ>" + Util.null2String(rs.getString("yyzzyxqz")) + "</ZYYZZ_YXQ>\n" +
                    "         <ZYYZZZCH>" + Util.null2String(rs.getString("sh")) + "</ZYYZZZCH>\n" +
                    "         <ZXKZ_FZRQ>" + Util.null2String(rs.getString("xkzfzrq")) + "</ZXKZ_FZRQ>\n" +
                    "         <ZYYZZ_QX>" + Util.null2String(rs.getString("yyzzyxqz")) + "</ZYYZZ_QX>\n" +
                    "         <ZYYZZ_FZRQ>" + Util.null2String(rs.getString("yyzzfzrq")) + "</ZYYZZ_FZRQ>\n" +
//                        "         <ZXKZ_MC>" + util.getSelectName(this.xkzmclx, Util.null2String(recordSet.getString("xkzmclx"))) + "</ZXKZ_MC>\n" +
                    "         <ZXKZ_MC>" + getSelectName(getUf(), "xkzmclx", Util.null2String(rs.getString("xkzmclx"))) + "</ZXKZ_MC>\n" +
                    "         <ZXKZ_YXQ>" + Util.null2String(rs.getString("xkzyxqz")) + "</ZXKZ_YXQ>\n" +
                    JYFW(ghfmc) +
                    "         <ZXKZ_BH>" + Util.null2String(rs.getString("xkzbh")) + "</ZXKZ_BH>\n" +
                    "         <ZXKZ_BGJL>" + Util.null2String(rs.getString("bgjl")) + "</ZXKZ_BGJL>\n" +
                    CKDZ(ghfmc) +
                    "         <ZGSP_BH>" + Util.null2String(rs.getString("gspzsbh")) + "</ZGSP_BH>\n" +
                    "         <ZGSP_MC>药品经营质量管理规范认证证书</ZGSP_MC>\n" +
                    "         <ZGSP_FZJG>" + Util.null2String(rs.getString("gspfzjg")) + "</ZGSP_FZJG>\n" +
                    "         <ZGSP_YXQ>" + Util.null2String(rs.getString("gspyxqz")) + "</ZGSP_YXQ>\n" +
                    "         <ZZZJGDM>" + Util.null2String(rs.getString("zzjgdm")) + "</ZZZJGDM>\n" +
                    "         <ZZZJGDA_YXQ>" + Util.null2String(rs.getString("zzjgyxqz")) + "</ZZZJGDA_YXQ>\n" +
//                        "         <ZZBXY>" + util.getSelectName(this.zlbzxys, Util.null2String(recordSet.getString("zlbzxys"))) + "</ZZBXY>\n" +
                    "         <ZZBXY>" + getSelectName(getUf(), "zlbzxys", Util.null2String(rs.getString("zlbzxys"))) + "</ZZBXY>\n" +
                    "         <ZZBXY_YXQ>" + Util.null2String(rs.getString("yxqz")) + "</ZZBXY_YXQ>\n" +
//                        "         <ZZLBZTX>" + util.getSelectName(this.zltxdcb, Util.null2String(recordSet.getString("zltxdcb"))) + "</ZZLBZTX>\n" +
                    "         <ZZLBZTX>" + getSelectName(getUf(), "zltxdcb", Util.null2String(rs.getString("zltxdcb"))) + "</ZZLBZTX>\n" +
                    "         <ZSWDJZ_BH>" + Util.null2String(rs.getString("swdjzbh")) + "</ZSWDJZ_BH>\n" +
                    "         <ZKHZT>" + Util.null2String(rs.getString("ghdwzt")) + "</ZKHZT>\n" +
                    "         <ZDTXQ>" + Util.null2String(rs.getString("dtyxq")) + "</ZDTXQ>\n" +
                    "         <sale_view>\n" +
                    "            <VKORG>" + getVkorg() + "</VKORG>\n" +
                    "            <VTWEG>" + utils.getFieldValue(getFxqd(), "fxqdbh", Util.null2String(rs.getString("fxqd"))) + "</VTWEG>\n" +
                    "            <SPART>" + Util.null2String(rs.getString("cpz")) + "</SPART>\n" +
                    "            <bukrs>" + getVkorg() + "</bukrs>\n" +
                    "            <BZIRK>" + utils.getFieldValue(getXsqy(), "xsqdbh", Util.null2String(rs.getString("xsqy"))) + "</BZIRK>\n" +
                    "            <KONDA>" + Util.null2String(rs.getString("jgz")) + "</KONDA>\n" +
                    "            <KALKS>" + Util.null2String(rs.getString("djgc")) + "</KALKS>\n" +
                    "            <VWERK>" + Util.null2String(rs.getString("jhgc")) + "</VWERK>\n" +
                    "            <VSBED>" + Util.null2String(rs.getString("zytj")) + "</VSBED>\n" +
                    "            <INCO1>" + utils.getFieldValue(getGjmysyjstz(), "gjmysyjstzbh", Util.null2String(rs.getString("gjmysyjstz"))) + "</INCO1>\n" +
                    "            <INCO2_L>" + Util.null2String(rs.getString("gjmysyjstz1")) + "</INCO2_L>\n" +
                    "            <ZTERM>" + utils.getFieldValue(getFktj(), "fktjbh", Util.null2String(rs.getString("fktj"))) + "</ZTERM>\n" +
                    "            <KTGRD>" + utils.getFieldValue(getZhfpz(), "zhfpzbh", Util.null2String(rs.getString("zhfpz"))) + "</KTGRD>\n" +
                    "            <AKONT>" + utils.getFieldValue(getTykm(), "tykmbh", Util.null2String(rs.getString("tykm"))) + "</AKONT>\n" +
                    "            <TAXKD>" + sfl + "</TAXKD>\n" +
                    "         </sale_view>\n" +
                    "      </erp:MT_PurchaseUnitInfo>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

            writeLog("请求sap信息： " + soapHttpRequest);
            String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
            String soapHttpResponse = WSClientUtils.callWebService(Util.delHtmlWithSpace(soapHttpRequest), endpoint, utils.getUsername(), utils.getPassword());
            writeLog("sap 返回信息: " + soapHttpResponse);
            MT_PurchaseUnitInfo_Msg msg = parse(soapHttpResponse);
            if (msg != null) {
                if (msg.getMESSAGE_TYPE().equals("E")) {
                    requestManager.setMessageid("SAP Response Message");
                    requestManager.setMessagecontent(msg.getMESSAGE());
                } else {
                    String GSID = "";
                    if (this.vkorg.equals("1610")) {
                        GSID = "01001";
                    } else if (this.vkorg.equals("1620")) {
                        GSID = "01002";
                    } else if (this.vkorg.equals("1630")) {
                        GSID = "01003";
                    }
                    YXOAKH yxoakh = new YXOAKH();
                    yxoakh.setUf(this.getUf());
                    yxoakh.setVkorg(GSID);
                    yxoakh.execute(requestInfo);

                    YXOAKHDZ yxoakhdz = new YXOAKHDZ();
                    yxoakhdz.setUf(this.getUf());
                    yxoakhdz.setVkorg(GSID);
                    yxoakhdz.execute(requestInfo);
                }
            } else {
                requestManager.setMessageid("SAP Response Message");
                requestManager.setMessagecontent(soapHttpResponse);
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

    private String CKDZ(String id) {
        String sql = "select * from "+uf+"_dt1 where mainid='" + id + "'";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        while (recordSet.next()) {
            stringBuilder.append("         <ZCKDZ").append(i).append(">").append(Util.null2String(recordSet.getString("ckdz"))).append("</ZCKDZ").append(i).append(">\n");
            stringBuilder.append("         <ZCKDZXQ").append(i).append(">").append(Util.null2String(recordSet.getString("yxqz")).equals("") ? "9999-12-31" : Util.null2String(recordSet.getString("yxqz"))).append("</ZCKDZXQ").append(i).append(">\n");
            i++;
        }
        for (; i <= 10; i++) {
            stringBuilder.append("         <ZCKDZ").append(i).append(">").append("").append("</ZCKDZ").append(i).append(">\n");
            stringBuilder.append("         <ZCKDZXQ").append(i).append(">").append("").append("</ZCKDZXQ").append(i).append(">\n");
        }
        return stringBuilder.toString();
    }

    private String JYFW(String id) {
        String sql = "select * from "+uf+"_dt2 where mainid='" + id + "'";
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

    public String getSelectName(String tablename, String fieldname, String selectvalue) {
        String sql = "SELECT SELECTNAME FROM WORKFLOW_SELECTITEM WHERE FIELDID=(SELECT ID FROM WORKFLOW_BILLFIELD WHERE BILLID=(SELECT ID FROM WORKFLOW_BILL WHERE TABLENAME='" + tablename + "') AND FIELDNAME='" + fieldname + "') AND SELECTVALUE='" + selectvalue + "'";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        String selectname = "";
        if (recordSet.next()) {
            selectname = recordSet.getString("SELECTNAME");
        }
        return selectname;
    }

    public String getVkorg() {
        return vkorg;
    }

    public void setVkorg(String vkorg) {
        this.vkorg = vkorg;
    }

//    public String getXkzmclx() {
//        return xkzmclx;
//    }
//
//    public void setXkzmclx(String xkzmclx) {
//        this.xkzmclx = xkzmclx;
//    }
//
//    public String getZlbzxys() {
//        return zlbzxys;
//    }
//
//    public void setZlbzxys(String zlbzxys) {
//        this.zlbzxys = zlbzxys;
//    }
//
//    public String getZltxdcb() {
//        return zltxdcb;
//    }
//
//    public void setZltxdcb(String zltxdcb) {
//        this.zltxdcb = zltxdcb;
//    }

    public String getFxqd() {
        return fxqd;
    }

    public void setFxqd(String fxqd) {
        this.fxqd = fxqd;
    }

    public String getXsqy() {
        return xsqy;
    }

    public void setXsqy(String xsqy) {
        this.xsqy = xsqy;
    }

    public String getGjmysyjstz() {
        return gjmysyjstz;
    }

    public void setGjmysyjstz(String gjmysyjstz) {
        this.gjmysyjstz = gjmysyjstz;
    }

    public String getFktj() {
        return fktj;
    }

    public void setFktj(String fktj) {
        this.fktj = fktj;
    }

    public String getZhfpz() {
        return zhfpz;
    }

    public void setZhfpz(String zhfpz) {
        this.zhfpz = zhfpz;
    }

    public String getTykm() {
        return tykm;
    }

    public void setTykm(String tykm) {
        this.tykm = tykm;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
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
