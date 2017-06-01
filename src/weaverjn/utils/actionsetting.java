package weaverjn.utils;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.interfaces.schedule.BaseCronJob;

/**
 * Created by zhaiyaqi on 2017/5/25.
 */
public class actionsetting extends BaseCronJob {
    public void logger(Object o) {
        util.writeLog(this.getClass().getName(), o);
    }

    public void execute() {
        logger("run");
        RecordSetDataSource rsds = new RecordSetDataSource("local");
        RecordSetDataSource rsds1 = new RecordSetDataSource("local");
        RecordSet recordSet = new RecordSet();
        String sql = "select id,actionname,actionclass,actionshowname from actionsetting";
        rsds.executeSql(sql);
        while (rsds.next()) {
            String id = rsds.getString("id");
            String actionname = rsds.getString("actionname");
            sql = "select * from actionsetting where actionname='" + actionname + "'";
            recordSet.executeSql(sql);
            if (!recordSet.next()) {
                String actionclass = rsds.getString("actionclass");
                String actionshowname = rsds.getString("actionshowname");
                sql = "insert into actionsetting(actionname,actionclass,actionshowname) " +
                        "values('" + actionname + "','" + actionclass + "','" + actionshowname + "')";
                if (recordSet.executeSql(sql)) {
                    logger(sql);
                    sql = "select id from actionsetting where actionname='" + actionname + "'";
                    recordSet.executeSql(sql);
                    recordSet.next();
                    String newid = recordSet.getString("id");
                    sql = "select attrname,attrvalue,isdatasource from actionsettingdetail where actionid=" + id;
                    rsds1.executeSql(sql);
                    while (rsds1.next()) {
                        sql = "insert into actionsettingdetail(actionid,attrname,attrvalue,isdatasource) " +
                                "values('" + newid + "','" + rsds1.getString("attrname") + "','" + rsds1.getString("attrvalue") + "',0)";
                        recordSet.executeSql(sql);
                        logger(sql);
                    }
                }
            }
        }
    }
}
