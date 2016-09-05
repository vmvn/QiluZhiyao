package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;

/**
 * Created by dzyq on 2016/9/5 8:58.
 */
public class RemindMoreSchedule extends BaseCronJob {
    public void execute() {
        RecordSet recordSet = new RecordSet();
        String sql = "select * from workflow_currentoperator where viewtype=-1";
        recordSet.executeSql(sql);
        while (recordSet.next()) {
            String userid = Util.null2String(recordSet.getString("userid"));
            String requestid = Util.null2String(recordSet.getString("userid"));
            if (!userid.isEmpty() && !requestid.isEmpty()) {
                sql = "select * from SysPoppupRemindInfoNew where userid=" + userid + " and requestid=" + requestid;
                RecordSet recordSet1 = new RecordSet();
                recordSet1.executeSql(sql);
                if (!recordSet1.next()) {
                    sql = "insert into SysPoppupRemindInfoNew values(" + userid + ",1,0,0," + requestid + ",NULL,1,NULL,NULL)";
                    recordSet1.executeSql(sql);
                }
            }
        }
    }
}
