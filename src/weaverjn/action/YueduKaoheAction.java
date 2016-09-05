package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

import java.util.Calendar;

/**
 * Created by dzyq on 2016/6/29.
 */
public class YueduKaoheAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        String requestid = requestInfo.getRequestid();
        RecordSet rs = new RecordSet();
        String sql = "select khyd2,xm, bkhdw, bkhr from formtable_main_101 where requestid=" + requestid;
        int flag = 15;
        rs.executeSql(sql);
        String msg = "";
        if (rs.next()) {
            int khyd = rs.getInt("khyd2");
            int xm = rs.getInt("xm");
            int bkhdw = rs.getInt("bkhdw");
            String bkhr = rs.getString("bkhr");
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String gh = rs.getString("gh");
            double gwzzkhbz = rs.getDouble("gwzzkhbz");
            double lszdgzkhbz = rs.getInt("lszdgzkhbz");
            double gzzfkhbz = rs.getInt("gzzfkhbz");

            double bzfz4 = rs.getDouble("bzfz4");
            double bzfz5 = rs.getDouble("bzfz5");
            double bzfz6 = rs.getDouble("bzfz6");
            if (gh.length() >= 5) {
                if (bzfz4 > gwzzkhbz) {
                    msg = "岗位职责标准分之和大于总分值";
                } else if (bzfz5 > lszdgzkhbz) {
                    msg = "临时重点工作标准分之和大于总分值";
                } else if (bzfz6 > gzzfkhbz) {
                    msg = "工作作风标准分之和大于总分值";
                } else {
                    if (khyd < month) {
                        if (month - khyd > 1) {
                            msg = "不能提交" + (khyd + 1) + "月的绩效考核";
                        } else {
                            if (day > flag) {
                                msg = "本月" + flag + "日之后不允许提交上月的绩效考核";
                            } else {
//                        rs.executeSql("select * from formtable_main_101 where xm=" + xm + " and khyd2=" + khyd + " and rq2='" + year + "'and requestid!=" + requestid);
                                rs.executeSql("select * from formtable_main_101 where bkhdw=" + bkhdw + " and bkhr='" + bkhr + "' and khyd2=" + khyd + " and rq2='" + year + "'and requestid!=" + requestid);
                                if (rs.getCounts() > 0) {
                                    msg = "上月绩效考核已提交过，不允许重复提交";
                                } else {
                                    rs.executeSql("update formtable_main_101 set rq2='" + year + "' where requestid=" + requestid);
                                }
                            }
                        }
                    } else if (khyd == 11 && month == 0) {
                        if (day > flag) {
                            msg = "本月" + flag + "日之后不允许提交上月的绩效考核";
                        } else {
//                    rs.executeSql("select * from formtable_main_101 where xm=" + xm + " and khyd2=" + khyd + " and rq2='" + (year - 1) + "' and requestid!=" + requestid);
                            rs.executeSql("select * from formtable_main_101 where bkhdw=" + bkhdw + " and bkhr='" + bkhr + "' and khyd2=" + khyd + " and rq2='" + (year - 1) + "' and requestid!=" + requestid);
                            if (rs.getCounts() > 0) {
                                msg = "上月绩效考核已提交过，不允许重复提交";
                            } else {
                                rs.executeSql("update formtable_main_101 set rq2='" + (year - 1) + "' where requestid=" + requestid);
                            }
                        }
                    } else if (khyd > month) {
                        msg = "考核月度不能超过当前月份";
                    } else if (khyd == month && day <= flag) {
                        msg = "每月1—" + flag + "号只能提交上个月";
                    } else {
//                rs.executeSql("select * from formtable_main_101 where xm=" + xm + " and khyd2=" + khyd + " and rq2='" + year + "'and requestid!=" + requestid);
                        rs.executeSql("select * from formtable_main_101 where bkhdw=" + bkhdw + " and bkhr='" + bkhr + "' and khyd2=" + khyd + " and rq2='" + year + "'and requestid!=" + requestid);
                        if (rs.getCounts() > 0) {
                            msg = "上月绩效考核已提交过，不允许重复提交";
                        } else {
                            rs.executeSql("update formtable_main_101 set rq2='" + year + "' where requestid=" + requestid);
                        }
                    }
                }
            } else {
                msg = "工号长度应不小于5位！";
            }
        } else {
            msg = "error code: 1";
        }
        if (!msg.isEmpty()) {
            requestInfo.getRequestManager().setMessageid("90031");
            requestInfo.getRequestManager().setMessagecontent(msg);
        }
        return SUCCESS;
    }
}
