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
public class YunshuDanweiZiliao extends BaseCronJob {
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
        String sql = "select * from uf_wrwtcyysdwzl where (zt is null or zt = 0) and " +
                "(yxqz>='" + today + "' and yxqz<='" + deadline + "') " +//营业执照有效期
                "or (yxqz1>='" + today + "' and yxqz1<='" + deadline + "') " +//许可证有效期
                "or (njrq>='" + today + "' and njrq<='" + deadline + "') ";//年检日期
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);

        while (recordSet.next()) {

            String id = Util.null2String(recordSet.getString("id"));
            String yyzzyxq = Util.null2String(recordSet.getString("yxqz"));
            String xkzyxq = Util.null2String(recordSet.getString("yxqz1"));
            String njrq = Util.null2String(recordSet.getString("njrq"));

            String zdr = Util.null2String(recordSet.getString("zdr"));
            String cydwbh = Util.null2String(recordSet.getString("cydwbh"));
            String cydwmc = Util.null2String(recordSet.getString("cydwmc"));

            StringBuilder msg = new StringBuilder();

            msg.append(cydwmc).append("，编号：").append(cydwbh).append("，");

            int a1 = DateUtils.dateDifDays(yyzzyxq, today);
            int a2 = DateUtils.dateDifDays(xkzyxq, today);
            int a3 = DateUtils.dateDifDays(njrq, today);

            if (a1 == 0) {
                msg.append("营业执照有效期今日到期。");
                lock(id);
                send2SAP("1620", cydwbh);
            } else {
                msg.append("营业执照有效期将于").append(a1).append("日后到期。");
            }
            if (a2 == 0) {
                msg.append("许可证有效期今日到期。");
                lock(id);
                send2SAP("1620", cydwbh);
            } else {
                msg.append("许可证有效期将于").append(a2).append("日后到期。");
            }
            if (a3 == 0) {
                msg.append("年检日期今日到期。");
                lock(id);
                send2SAP("1620", cydwbh);
            } else {
                msg.append("年检日期将于").append(a3).append("日后到期。");
            }
            msg.append("\n");

            SysRemindWorkflow sysRemindWorkflow = new SysRemindWorkflow();
            String requestName = "万润运输单位证照到期提醒";
            try {
                sysRemindWorkflow.make(requestName, 0, 0, 0, 0, 1, zdr, msg.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void lock(String id) {
        String sql = "update uf_wrwtcyysdwzl set zt=1 where id='" + id + "'";
        new RecordSet().executeSql(sql);
    }

    private void send2SAP(String vkorg, String kunnr) {
        String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_Transport_Enterprise_State>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID></INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <Transport_Enterprise_State>\n" +
                "            <KUNNR>" + kunnr + "</KUNNR>\n" +
                "            <VKROG>" + vkorg + "</VKROG>\n" +
                "            <STATE>N</STATE>\n" +
                "         </Transport_Enterprise_State>\n" +
                "      </erp:MT_Transport_Enterprise_State>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        String username = utils.getUsername();
        String password = utils.getPassword();
        String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
        String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, username, password);
        new BaseBean().writeLog(soapHttpResponse);
    }
}
