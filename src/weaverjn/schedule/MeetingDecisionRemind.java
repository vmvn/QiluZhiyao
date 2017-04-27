package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;
import weaver.system.SysRemindWorkflow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dzyq on 2016/6/20.
 */
public class MeetingDecisionRemind extends BaseCronJob {
    private final BaseBean baseBean = new BaseBean();

    private void logger(Object o) {
        baseBean.writeLog(this.getClass().getName() + " - " + o);
    }

    public void execute() {
        logger("run");
        Calendar now = Calendar.getInstance();
        Calendar twoDaysAgo = Calendar.getInstance();
        twoDaysAgo.add(Calendar.DATE, -2);
        RecordSet rs = new RecordSet();
        String sql = "select * " +
                "from meeting m " +
                "where m.isdecision != 2 " +
                "and cancel is null " +
                "and meetingstatus != 0 " +
                "and (not exists " +
                "(select 1 from meeting_decision_remind where id = m.id) or exists " +
                "(select 1 " +
                "from meeting_decision_remind " +
                "where id = m.id " +
                "and isremind = 0))";
        rs.executeSql(sql);
        while (rs.next()) {
            String enddate = Util.null2String(rs.getString("enddate"));
            String endtime = Util.null2String(rs.getString("endtime"));
            logger("---->enddate,endtime:" + enddate + "," + endtime);
            if (enddate.isEmpty() || endtime.isEmpty()) {
                continue;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date enddatetime = sdf.parse(enddate + " " + endtime);
                Calendar calenddatetime = Calendar.getInstance();
                calenddatetime.setTime(enddatetime);
                if (calenddatetime.after(twoDaysAgo) && calenddatetime.before(now)) {
                    logger("---->twoDaysAgo:" + sdf.format(twoDaysAgo.getTime()));
                    logger("---->结束时间:" + sdf.format(calenddatetime.getTime()));
                    logger("---->now:" + sdf.format(now.getTime()));
                    String contacter = Util.null2String(rs.getString("contacter"));
                    String jlr = Util.null2String(rs.getString("jlr"));
                    String name = Util.null2String(rs.getString("name"));
                    String meetingid = Util.null2String(rs.getString("id"));
                    String accessorys = Util.null2String(rs.getString("accessorys"));
                    String projectid = Util.null2String(rs.getString("projectid"));
                    logger("-------->id:" + meetingid);
                    SysRemindWorkflow sysRemindWorkflow = new SysRemindWorkflow();
                    try {
                        int requestid = 0;
                        requestid = sysRemindWorkflow.make("请填写会议：" + name + " 的决议", Util.getIntValue(accessorys), 0, Util.getIntValue(projectid), Util.getIntValue(meetingid), 1, contacter, "请填写会议：" + name + " 的决议");
                        if (requestid != 0) {
                            sql = "insert into meeting_decision_remind(id, isremind) values(" + meetingid + ",1)";
                            RecordSet rs1 = new RecordSet();
                            if (rs1.executeSql(sql)) {
                                logger("---->插入状态成功" + meetingid);
                            } else {
                                sql = "update meeting_decision_remind set isremind=1 where id=" + meetingid;
                                if (rs1.executeSql(sql)) {
                                    logger("---->更新状态成功" + meetingid);
                                } else {
                                    logger("---->失败" + meetingid);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger("---->会议决议填写提醒流程创建失败！");
                        e.printStackTrace();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
