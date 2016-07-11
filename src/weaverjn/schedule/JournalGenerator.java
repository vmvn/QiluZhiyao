package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by dzyq on 2016/7/11 8:55.
 */
public class JournalGenerator extends BaseCronJob {
    public void execute(){
        System.out.println("---->JournalGenerator start");
        RecordSet recordSet = new RecordSet();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DATE, -1);
        String yesterday = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        String today = simpleDateFormat.format(calendar.getTime());
        String sql = "select * from uf_gzrqnew where tbrq='" + yesterday + "'";
        recordSet.executeSql(sql);
        while (recordSet.next()) {
            String id = Util.null2String(recordSet.getString("id"));
            String tbr = Util.null2String(recordSet.getString("tbr"));
            String bm = Util.null2String(recordSet.getString("bm"));
            String ssgs = Util.null2String(recordSet.getString("ssgs"));
            Calendar calendarNow = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss");
            String now = simpleDateFormat1.format(calendarNow.getTime());
            sql = "insert into uf_gzrqnew(bt, bm, tbr, tbrq, ssgs, FORMMODEID, MODEDATACREATER, MODEDATACREATERTYPE, MODEDATACREATEDATE, MODEDATACREATETIME) " +
                    "values('" + today + " 工作日志" + "', " + bm + ", " + tbr + ", '" + today + "', " + ssgs + ", 661, " + tbr + ", 0, '" + today + "', '" + now + "')";
            RecordSet recordSet1 = new RecordSet();
            if (recordSet1.executeSql(sql)) {
                sql = "select max(id) maxid from uf_gzrqnew";
                RecordSet recordSet2 = new RecordSet();
                recordSet2.executeSql(sql);
                recordSet2.next();
                int newid = recordSet2.getInt("maxid");
                ModeRightInfo modeRightInfo = new ModeRightInfo();
                modeRightInfo.editModeDataShare(Util.getIntValue(tbr), 661, newid);
                sql = "select * from uf_gzrqnew_dt3 where mainid=" + id;
                recordSet2.executeSql(sql);
                while (recordSet2.next()) {
                    String gzfl = Util.null2String(recordSet2.getString("gzfl"));
                    String gznr = Util.null2String(recordSet2.getString("gznr"));
                    String sfwc = Util.null2String(recordSet2.getString("sfwc"));
                    String yddwt = Util.null2String(recordSet2.getString("yddwt"));
                    String gjyj = Util.null2String(recordSet2.getString("gjyj"));

                    sql = "insert into uf_gzrqnew_dt1(mainid, gzfl, gznr, sfwc, yddwt, gjyj) " +
                            "values(" + newid + ", " + gzfl + ", '" + gznr + "', " + sfwc + ", '" + yddwt + "', '" + gjyj + "')";
                    RecordSet recordSet3 = new RecordSet();
                    recordSet3.executeSql(sql);
                }
                System.out.println("---->Journal OK:" + today + ":" + tbr);
            } else {
                System.out.println("---->Journal not OK:" + today + ":" + tbr);
            }
        }
    }
}
