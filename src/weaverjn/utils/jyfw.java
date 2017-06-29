package weaverjn.utils;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;

/**
 * Created by zhaiyaqi on 2017/6/5.
 */
public class jyfw extends BaseCronJob {
    private void logger(Object o) {
        util.writeLog(this.getClass().getName(), o);
    }

    public void execute() {
        String sql = "SELECT ID,GHFMC,GHFBH FROM formtable_main_957";
        RecordSet recordSet = new RecordSet();
        RecordSet recordSet1 = new RecordSet();
        RecordSet recordSet2 = new RecordSet();
        recordSet.executeSql(sql);
        while (recordSet.next()) {
            int ID = recordSet.getInt("ID");
            String GHFMC = Util.null2String(recordSet.getString("GHFMC"));
            String GHFBH = Util.null2String(recordSet.getString("GHFBH"));

            sql = "SELECT * FROM FORMTABLE_MAIN_957_DT1 WHERE mainid=" + ID;
            logger(sql);
            recordSet1.executeSql(sql);
            if (!recordSet1.next()) {
                sql = "SELECT JYFW, MC FROM formtable_main_812_dt2 WHERE MAINID=(SELECT id FROM formtable_main_812 WHERE GHFBH='" + GHFBH + "' AND ID='" + GHFMC + "')";
                logger(sql);
                recordSet1.executeSql(sql);
                while (recordSet1.next()) {
                    String JYFW = Util.null2String(recordSet1.getString("JYFW"));
                    String MC = Util.null2String(recordSet1.getString("MC"));
                    sql = "insert into FORMTABLE_MAIN_957_DT1(MAINID, jyfw, mc) " +
                            "values('" + ID + "','" + JYFW + "','" + MC + "')";
                    logger(sql);
                    recordSet2.executeSql(sql);
                }
            }
        }
    }
}
