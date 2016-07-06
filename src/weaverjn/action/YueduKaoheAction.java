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
        String sql = "select khyd2,xm from formtable_main_101 where requestid=" + requestid;
        rs.executeSql(sql);
        String err = "";
        if (rs.next()) {
            int khyd = rs.getInt("khyd2");
            int xm = rs.getInt("xm");
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
//            if (!(day < 15 && khyd + 1 == month)) {
//                err = "本月15日之后不允许提交上月的绩效考核";
//            }
            if (khyd < month) {
                if (month - khyd > 1) {
                    err = "不能提交" + (khyd + 1) + "月的绩效考核";
                } else {
                    if (day > 15) {
                        err = "本月15日之后不允许提交上月的绩效考核";
                    } else {
                        rs.executeSql("select * from formtable_main_101 where xm=" + xm + " and khyd2=" + khyd + " and rq2='" + year + "'and requestid!=" + requestid);
                        if (rs.getCounts() > 0) {
                            err = "上月绩效考核已提交过，不允许重复提交";
                        } else {
                            rs.executeSql("update formtable_main_101 set rq2='" + year + "' where requestid=" + requestid);
                        }
                    }
                }
            }else if (khyd == 11 && month == 0) {
                if (day > 15) {
                    err = "本月15日之后不允许提交上月的绩效考核";
                } else {
                    rs.executeSql("select * from formtable_main_101 where xm=" + xm + " and khyd2=" + khyd + " and rq2='" + (year - 1) + "' and requestid!=" + requestid);
                    if (rs.getCounts() > 0) {
                        err = "上月绩效考核已提交过，不允许重复提交";
                    } else {
                        rs.executeSql("update formtable_main_101 set rq2='" + (year - 1) + "' where requestid=" + requestid);
                    }
                }
            } else {
                rs.executeSql("select * from formtable_main_101 where xm=" + xm + " and khyd2=" + khyd + " and rq2='" + year + "'and requestid!=" + requestid);
                if (rs.getCounts() > 0) {
                    err = "上月绩效考核已提交过，不允许重复提交";
                } else {
                    rs.executeSql("update formtable_main_101 set rq2='" + year + "' where requestid=" + requestid);
                }
            }
        } else {
            err = "error code: 1";
        }
        if (!err.isEmpty()) {
            requestInfo.getRequestManager().setMessageid("90031");
            requestInfo.getRequestManager().setMessagecontent(err);
        }
        return SUCCESS;
    }
}
