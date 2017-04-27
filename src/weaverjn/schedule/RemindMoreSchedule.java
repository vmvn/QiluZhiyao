package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dzyq on 2016/9/5 8:58.
 */
public class RemindMoreSchedule extends BaseCronJob {
    private final BaseBean baseBean = new BaseBean();

    private void logger(Object o) {
        baseBean.writeLog(this.getClass().getName() + " - " + o);
    }

    public void execute() {
        logger("run");
        RecordSet recordSet = new RecordSet();
        String sql = "select * from workflow_currentoperator where viewtype=-1";
        recordSet.executeSql(sql);
        while (recordSet.next()) {
            String userid = Util.null2String(recordSet.getString("userid"));
            String requestid = Util.null2String(recordSet.getString("requestid"));

            sql = "select * from SysPoppupRemindInfoNew where userid=" + userid + " and requestid=" + requestid;
            RecordSet recordSet1 = new RecordSet();
            recordSet1.executeSql(sql);
            if (!recordSet1.next()) {
                sql = "insert into SysPoppupRemindInfoNew values(" + userid + ",1,0,0," + requestid + ",NULL,1,NULL,NULL)";
                recordSet1.executeSql(sql);
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String now = simpleDateFormat.format(date);
                logger("" + now + ";" + userid + ";" + requestid);
            }
        }
    }
}
