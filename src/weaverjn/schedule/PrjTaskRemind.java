package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;
import weaver.system.SysRemindWorkflow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by dzyq on 2016/7/7 13:53.
 */
public class PrjTaskRemind extends BaseCronJob {
    public void execute() {
        Calendar now = Calendar.getInstance();
        String sNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
        System.out.println("---->PrjTaskRemind:" + sNow);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sToday = simpleDateFormat.format(now.getTime());
        RecordSet recordSet = new RecordSet();
        String sql = "select * from Prj_TaskProcess where parentid=0 and finish<100";
        recordSet.executeSql(sql);
        while (recordSet.next()) {
            String enddate = Util.null2String(recordSet.getString("enddate"));
            String subject = Util.null2String(recordSet.getString("subject"));
            String prjid = Util.null2String(recordSet.getString("prjid"));
            String id = Util.null2String(recordSet.getString("id"));
            String hrmid = Util.null2String(recordSet.getString("hrmid"));
            if (!enddate.isEmpty()) {
                String title = "项目任务到期提醒";
                String content = "";
                try {
                    Calendar calendarEnd = Calendar.getInstance();
                    calendarEnd.setTime(simpleDateFormat.parse(enddate));

                    Calendar calendarAMonthAgo = Calendar.getInstance();
                    Calendar calendar20DaysAgo = Calendar.getInstance();
                    Calendar calendar10DaysAgo = Calendar.getInstance();

                    calendarAMonthAgo.setTime(simpleDateFormat.parse(enddate));
                    calendar20DaysAgo.setTime(simpleDateFormat.parse(enddate));
                    calendar10DaysAgo.setTime(simpleDateFormat.parse(enddate));

                    calendarAMonthAgo.add(Calendar.MONTH, -1);
                    calendar20DaysAgo.add(Calendar.DATE, -20);
                    calendar10DaysAgo.add(Calendar.DATE, -10);

                    String sAMonthAgo = simpleDateFormat.format(calendarAMonthAgo.getTime());
                    String s20DaysAgo = simpleDateFormat.format(calendar20DaysAgo.getTime());
                    String s10DaysAgo = simpleDateFormat.format(calendar10DaysAgo.getTime());

                    if (sToday.equals(sAMonthAgo)) {
                        content = "提醒：任务-" + subject + " 将于一个月后结束！";
                    } else if (sToday.equals(s20DaysAgo)) {
                        content = "提醒：任务-" + subject + " 将于20天后结束！";
                    } else if (sToday.equals(s10DaysAgo)) {
                        content = "提醒：任务-" + subject + " 将于10天后结束！";
                    } else if (Integer.parseInt(sToday.replace("-", "")) > Integer.parseInt(enddate.replace("-", ""))) {
                        int days = Integer.parseInt(sToday.replace("-", "")) - Integer.parseInt(enddate.replace("-", ""));
                        content = "提醒：任务-" + subject + " 已到期" + days + "天！";
                    } else {
                        System.out.println("---->Task:" + id + " is OK");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!content.isEmpty()) {
                    SysRemindWorkflow sysRemindWorkflow = new SysRemindWorkflow();
                    try {
                        sysRemindWorkflow.make(title, 0, 0, Util.getIntValue(prjid), 0, 1, hrmid, content);
                    } catch (Exception e) {
                        System.out.println("---->Task:" + id + " workflow error!");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
