package weaverjn.utils;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.interfaces.schedule.BaseCronJob;

/**
 * Created by zhaiyaqi on 2017/5/26.
 */
public class schedulesetting extends BaseCronJob {
    public void logger(Object o) {
        util.writeLog(this.getClass().getName(), o);
    }

    public void execute() {
        logger("run");
        RecordSetDataSource rsds = new RecordSetDataSource("local");
        RecordSet recordSet = new RecordSet();
        String sql = "select id,pointid,classpath,cronexpr from schedulesetting";
        rsds.executeSql(sql);
        while (rsds.next()) {
            String pointid = rsds.getString("pointid");
            String classpath = rsds.getString("classpath");
            String cronexpr = rsds.getString("cronexpr");

            sql = "select * from schedulesetting where pointid='" + pointid + "'";
            recordSet.executeSql(sql);
            if (!recordSet.next()) {
                sql = "insert into schedulesetting(pointid,classpath,cronexpr) " +
                        "values('" + pointid + "','" + classpath + "','" + cronexpr + "')";
                logger(sql);
                recordSet.executeSql(sql);
            }
        }
    }
}
