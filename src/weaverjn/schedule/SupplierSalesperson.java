package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;
import weaver.system.SysRemindWorkflow;
import weaverjn.action.integration.utils;
import weaverjn.utils.DateUtils;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

import java.util.Calendar;

/**
 * Created by zhaiyaqi on 2017/4/25.
 */
public class SupplierSalesperson extends BaseCronJob {
    private final BaseBean baseBean = new BaseBean();

    private void logger(Object o) {
        baseBean.writeLog(this.getClass().getName() + " - " + o);
    }

    public void execute() {
        logger("run");
        Calendar calendar = Calendar.getInstance();
        String today = DateUtils.Date2Str(calendar.getTime(), "yyyy-MM-dd");
        calendar.add(Calendar.DATE, 30);
        String deadline = DateUtils.Date2Str(calendar.getTime(), "yyyy-MM-dd");

        WR(today, deadline);
    }

    private void WR(String today, String deadline) {
        String sql = "select * from uf_sdwrghdwxsyzl where sfzyxq>='" + today + "' and sfzyxq<='" + deadline + "'";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        StringBuilder msg = new StringBuilder();
        String receivers = "1";
        while (recordSet.next()) {
            String sfzyxq = Util.null2String(recordSet.getString("sfzyxq"));//身份证有效期
            String bwtrxm = Util.null2String(recordSet.getString("bwtrxm"));//姓名
            String sfzh = Util.null2String(recordSet.getString("sfzh"));//身份证号
            String ghdwbh = Util.null2String(recordSet.getString("ghdwbh"));//供货单位编号

            receivers = Util.null2String(recordSet.getString("btxr"));//被提醒人

            int difDays = DateUtils.dateDifDays(sfzyxq, today);
            msg.append(bwtrxm).append("，身份证号：").append(sfzh).append("，");
            if (difDays == 0) {
                msg.append("身份证有效期今天到期。\n");
                send2SAP("1620", ghdwbh);//调用PI接口
            } else {
                msg.append("身份证有效期将于").append(difDays).append("日后到期。\n");
            }
        }

        SysRemindWorkflow sysRemindWorkflow = new SysRemindWorkflow();
        String requestName = "万润销售员身份证有效期到期提醒";
        try {
            sysRemindWorkflow.make(requestName, 0, 0, 0, 0, 1, receivers, msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send2SAP(String ekorg, String lifnr) {
        String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_Supplier_Qualification_Req>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID></INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <LIFNR_TYPE>2</LIFNR_TYPE>\n" +
                "         <EKORG>" + ekorg + "</EKORG>\n" +
                "         <LIFNR>" + lifnr + "</LIFNR>\n" +
                "         <SPERM>N</SPERM>\n" +
                "         <DATE_BEG></DATE_BEG>\n" +
                "         <DATE_END></DATE_END>\n" +
                "         <Deal_Scpoe>\n" +
                "            <SCPOE_ID></SCPOE_ID>\n" +
                "         </Deal_Scpoe>\n" +
                "         <Material_List>\n" +
                "            <MATNR></MATNR>\n" +
                "         </Material_List>\n" +
                "      </erp:MT_Supplier_Qualification_Req>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        String username = utils.getUsername();
        String password = utils.getPassword();
        String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
        String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, username, password);
        new BaseBean().writeLog(soapHttpResponse);
    }


}
