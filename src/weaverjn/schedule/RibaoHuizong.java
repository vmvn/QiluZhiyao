package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dzyq on 2016/5/14.
 */
public class RibaoHuizong extends BaseCronJob {
    public void execute() {
        Date today = new Date();
        int dayOfWeek = getDayOfWeek(today);
        if (dayOfWeek != 6) {
            System.out.println("---->RibaoHuizong:Not Friday");
        } else {
            System.out.println("---->RibaoHuizong:Start");
            RecordSet rs = new RecordSet();
            SimpleDateFormat SDFDATE = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -4);
            String monday = SDFDATE.format(calendar.getTime());
            String friday = SDFDATE.format(today);
            rs.executeSql("select distinct cjr from uf_jhrz where rq>='" + monday + "' and rq<='" + friday + "'");
            while (rs.next()) {
                String uid = Util.null2String(rs.getString("cjr"));
                RecordSet rs1 = new RecordSet();
                RecordSet rs2 = new RecordSet();
                String SQL = "select * from uf_jhrz where rq>='" + monday + "' and rq<='" + friday + "' and cjr='" + uid + "' order by rq";
                rs1.executeSql(SQL);
                String brgz = "";
                String bmzdgz = "";
                String gzsl = "";
                String gzzdyjh = "";
                String gzzczdwtjgjcs = "";
                String jy = "";
                String cjr = uid;
                while (rs1.next()) {
                    brgz += Util.null2String(rs1.getString("brgz")) + "\n";
                    bmzdgz += Util.null2String(rs1.getString("bmzdgz")) + "\n";
                    gzsl += Util.null2String(rs1.getString("gzsl")) + "\n";
                    gzzdyjh += Util.null2String(rs1.getString("gzzdyjh")) + "\n";
                    gzzczdwtjgjcs += Util.null2String(rs1.getString("zzzczdwtjgjcs")) + "\n";
                    jy += Util.null2String(rs1.getString("jy")) + "\n";
                }
                String dqzs = getYear(today) + "年" + "第" + getWeekOfYear(today) + "周";
                SQL = "insert into uf_zzj(dqzs, cjr, rq, brgz, bmzdgz, gzsl, gzzdyjh, gzzczdwtjgjcs, jy, FORMMODEID, MODEDATACREATER, MODEDATACREATERTYPE, MODEDATACREATEDATE, MODEDATACREATETIME) " +
                        "values(" +
                        "'" + dqzs + "', " +
                        "'" + cjr + "', " +
                        "'" + friday + "', " +
                        "'" + brgz + "', " +
                        "'" + bmzdgz + "', " +
                        "'" + gzsl + "', " +
                        "'" + gzzdyjh + "', " +
                        "'" + gzzczdwtjgjcs + "', " +
                        "'" + jy + "', " +
                        "'" + 462 + "', " +
                        "'" + cjr + "', " +
                        "'" + 0 + "', " +
                        "'" + friday + "', " +
                        "'23:00' " +
                        ") ";
                System.out.println("---->SQL:" + SQL);
                if (rs1.executeSql(SQL)) {
                    SQL = "select max(id) maxid from uf_zzj";
                    rs2.executeSql(SQL);
                    int maxid = -1;
                    if (rs2.next()) {
                        maxid = rs2.getInt("maxid");
                    }
                    ModeRightInfo modeRightInfo = new ModeRightInfo();
                    modeRightInfo.editModeDataShare(Util.getIntValue(cjr), 462, maxid);
                    System.out.println("---->RibaoHuizong:OK:" + uid);
                } else {
                    System.out.println("---->RibaoHuizong:NOT OK:" + uid);
                }
            }
        }
    }

    private int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private int getWeekOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    private int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -4);
        System.out.println(calendar.getTime());
    }
}
