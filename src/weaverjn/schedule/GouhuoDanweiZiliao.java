package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;
import weaverjn.utils.DateUtils;
import weaverjn.utils.util;

import java.util.Calendar;

/**
 * Created by zhaiyaqi on 2017/4/26.
 */
public class GouhuoDanweiZiliao extends BaseCronJob {
    private void logger(Object o) {
        util.writeLog(this.getClass().getName(), o);
    }
    public void execute() {
        logger("run");
        Calendar calendar = Calendar.getInstance();
        String today = DateUtils.Date2Str(calendar.getTime(), "yyyy-MM-dd");
        calendar.add(Calendar.DATE, 30);
        String deadline = DateUtils.Date2Str(calendar.getTime(), "yyyy-MM-dd");

        lockIt(today, deadline, "uf_wrghdwzl");//万润
        lockIt(today, deadline, "formtable_main_837");//万和108,formtable_main_942;formtable_main_871
        lockIt(today, deadline, "formtable_main_812");//鲁海108,formtable_main_977;formtable_main_811
    }
    private void lockIt(String today, String deadline, String table) {
        String sql = "select * from " + table + " where " +
                "(yyzzyxqz>='" + today + "' and yyzzyxqz<='" + deadline + "') " +//营业执照有效期
                "or (xkzyxqz>='" + today + "' and xkzyxqz<='" + deadline + "') " +//许可证有效期
                "or (gspyxqz>='" + today + "' and gspyxqz<='" + deadline + "') " +//GSP有效期至
                "or (yxqz>='" + today + "' and yxqz<='" + deadline + "') ";//质量保证协议书有效期至
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        while (recordSet.next()) {
            String id = Util.null2String(recordSet.getString("id"));
//            String txr = Util.null2String(recordSet.getString("txr"));

            String yyzzyxqz = Util.null2String(recordSet.getString("yyzzyxqz"));
            String xkzyxqz = Util.null2String(recordSet.getString("xkzyxqz"));
            String gspyxqz = Util.null2String(recordSet.getString("gspyxqz"));
            String yxqz = Util.null2String(recordSet.getString("yxqz"));

            int a1 = DateUtils.dateDifDays(yyzzyxqz, today);
            int a2 = DateUtils.dateDifDays(xkzyxqz, today);
            int a3 = DateUtils.dateDifDays(gspyxqz, today);
            int a4 = DateUtils.dateDifDays(yxqz, today);

//            StringBuilder msg = new StringBuilder();
            if (a1 == 0) {
//                msg.append("营业执照有效期今日到期。");
                lock(id, table);
                logger(id);
            } else {
//                msg.append("营业执照有效期将于").append(a1).append("日后到期。");
            }
            if (a2 == 0) {
//                msg.append("许可证有效期今日到期。");
                lock(id, table);
                logger(id);
            } else {
//                msg.append("许可证有效期将于").append(a2).append("日后到期。");
            }
            if (a3 == 0) {
//                msg.append("GSP有效期今日到期。");
                lock(id, table);
                logger(id);
            } else {
//                msg.append("GSP有效期将于").append(a3).append("日后到期。");
            }
            if (a4 == 0) {
//                msg.append("质量保证协议书有效期今日到期。");
                lock(id, table);
                logger(id);
            } else {
//                msg.append("质量保证协议书有效期将于").append(a4).append("日后到期。");
            }
//            msg.append("\n");
//
//            SysRemindWorkflow sysRemindWorkflow = new SysRemindWorkflow();
//            String requestName = "万润购货单位资料证照到期提醒";
//            try {
//                sysRemindWorkflow.make(requestName, 0, 0, 0, 0, 1, txr, msg.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    private void lock(String id, String table) {
        RecordSet recordSet = new RecordSet();
        String sql = "update " + table + " set ghdwzt=2 where id='" + id + "'";
        logger(sql);
        recordSet.executeSql(sql);
    }
}
