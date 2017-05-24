package weaverjn.action.integration;

import java.util.Calendar;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaverjn.schedule.JnUtil;
import weaverjn.utils.WSClientUtils;

/**
 * 收货人基础资料：将收货人编号为空传给SAP，接收SAP返回值，并传给CRM
 * @author songqi
 * @tel 13256247773
 * 2017年5月23日 下午3:44:32
 */
public class ConsigneeInfoAction extends BaseBean implements Action {
    private String vkorg;
    @Override
    public String execute(RequestInfo requestInfo) {
        String billId = requestInfo.getRequestid();
        String moduleId = requestInfo.getWorkflowid();
        String sql = "select b.tablename,b.id from modeinfo a,workflow_bill b where a.formid = b.id and a.id = " + moduleId;
        RecordSet rs = new RecordSet();
        rs.executeSql(sql);
        if (rs.next()) {
            String t = rs.getString("tablename");
            sql = "select * from " + t + " where id=" + billId;
            writeLog("收货人查询sql： " + sql);
            rs.executeSql(sql);
            rs.next();
            String sfzh = Util.null2String(rs.getString("shrsfz"));
            String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_ConsigneeInfo>\n" +
                    "         <ControlInfo>\n" +
                    "            <INTF_ID></INTF_ID>\n" +
                    "            <Src_System>OA</Src_System>\n" +
                    "            <Dest_System>SAPERP</Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time></Send_Time>\n" +
                    "         </ControlInfo>\n" +
                    "         <VKORG>" + this.vkorg + "</VKORG>\n" +
                    "         <KUNNR>" + Util.null2String(rs.getString("ghdwbh")) + "</KUNNR>\n" +
                    "         <ZSHR_BM></ZSHR_BM>\n" +
                    "         <ZSQ_BGN>" + Util.null2String(rs.getString("sqqrq")) + "</ZSQ_BGN>\n" +
                    "         <ZSQ_END>" + Util.null2String(rs.getString("sqzrq")) + "</ZSQ_END>\n" +
                    "         <ZSHR_SFZ>" + sfzh + "</ZSHR_SFZ>\n" +
                    "         <ZSFZ_YXQ>" + Util.null2String(rs.getString("sfzyxq")) + "</ZSFZ_YXQ>\n" +
                    "         <ZGHDWGZ>" + (Util.null2String(rs.getString("ywghdw")).equals("0") ? "Y" : "N") + "</ZGHDWGZ>\n" +
                    "         <ZJSHYJ>" + (Util.null2String(rs.getString("hwjsh")).equals("0") ? "Y" : "N") + "</ZJSHYJ>\n" +
                    "         <ZSFZFYJ>" + (Util.null2String(rs.getString("ywsfzfyj")).equals("0") ? "Y" : "N") + "</ZSFZFYJ>\n" +
                    "         <ZFYJGZ>" + (Util.null2String(rs.getString("sfzfyj")).equals("0") ? "Y" : "N") + "</ZFYJGZ>\n" +
                    "         <NAME_LAST>"+Util.null2String(rs.getString("shrxm"))+"</NAME_LAST>\n" +
                    "         <PSTLZ></PSTLZ>\n" +
                    "         <ORT01>" + Util.null2String(rs.getString("sqqy")) + "</ORT01>\n" +
                    "         <LAND1>CN</LAND1>\n" +
                    "      </erp:MT_ConsigneeInfo>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_ConsigneeInfo_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
            String username = "zappluser_oa";
            String password = "a1234567";
            writeLog("收货人请求sap地址： " + soapRequest);
            String soapResponse = WSClientUtils.callWebService(soapRequest, url, username, password);
            writeLog("收货人sap返回信息： " + soapResponse);
            MT_ConsigneeInfo_Msg msg = parse(soapResponse);
            String reg_msg = "";
            if (msg != null) {
                reg_msg += msg.getMESSAGE_TYPE();
                reg_msg += "        " + msg.getMESSAGE();
                sql = "update " + t + " set ret_msg='" + reg_msg + "', shrbh='" + msg.getZSHR_BM() + "' where id=" + billId;
            } else {
                reg_msg += soapResponse;
                sql = "update " + t + " set ret_msg='" + reg_msg + "' where id=" + billId;
            }
            writeLog("修改收货人编号信息的sql：" + sql);
            rs.executeSql(sql);
            RecordSetDataSource rsds = new RecordSetDataSource("crm_db");
            String datetime = JnUtil.date2String(Calendar.getInstance().getTime());
        	datetime = datetime.replace("-", "");
        	datetime += " 00:00:00";
        	String date = datetime.substring(0,8);
            String sql2 = "update YXOASHR set YXOASHR_BGRQ='"+date+"',YXOASHR_BGSJ='"+datetime+"',YXOASHR_SHRBH='"+msg.getZSHR_BM()+"' where YXOASHR_SFZH='"+sfzh+"'";
            writeLog("修改收货人CRM记录sql： " + sql2);
            boolean f = rsds.execute(sql2);
            if(f){
            	writeLog("修改CRM收货人记录成功！");
            }else{
            	writeLog("修改CRM收货人记录失败！");
            }
        }
        return SUCCESS;
    }

    private MT_ConsigneeInfo_Msg parse(String response) {
        MT_ConsigneeInfo_Msg msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_ConsigneeInfo_Msg");
            msg = new MT_ConsigneeInfo_Msg();
            msg.setMESSAGE_TYPE(e.elementText("MESSAGE_TYPE"));
            msg.setMESSAGE(e.elementText("MESSAGE"));
            msg.setZSHR_BM(e.elementText("ZSHR_BM"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public String getVkorg() {
        return vkorg;
    }

    public void setVkorg(String vkorg) {
        this.vkorg = vkorg;
    }

    class MT_ConsigneeInfo_Msg {
        private String MESSAGE_TYPE;
        private String MESSAGE;
        private String ZSHR_BM;

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

        public String getZSHR_BM() {
            return ZSHR_BM;
        }

        public void setZSHR_BM(String ZSHR_BM) {
            this.ZSHR_BM = ZSHR_BM;
        }
    }

}
