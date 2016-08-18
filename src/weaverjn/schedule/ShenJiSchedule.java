package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.interfaces.schedule.BaseCronJob;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by dzyq on 2016/8/17 16:18.
 */
public class ShenJiSchedule extends BaseCronJob {
    public void execute() {
        RecordSet recordSet = new RecordSet();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(calendar.getTime());
        String sql = "update uf_sjwtkb set zgsfcq=0 where jhwcrq<'" + today + "' and (zgsfcq is null or zgsfcq = 1) ";
        System.out.println("-------->ShenJiSchedule:" + sql);
        recordSet.executeSql(sql);
    }
}
