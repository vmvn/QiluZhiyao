package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by dzyq on 2016/5/5.
 */
public class Meeting2DubanAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        RecordSet rs1 = new RecordSet();
        RecordSet rs2 = new RecordSet();
        String requestid = requestInfo.getRequestid();
        String workflowid = requestInfo.getWorkflowid();
        String tablename = "";
        String message = "";
        String SQL = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
        rs1.executeSql(SQL);
        if (rs1.next()) {
            tablename = Util.null2String(rs1.getString("tablename"));
            SQL = "select id, sqr, hynr, xghy from " + tablename + " where requestid=" + requestid;
            rs1.executeSql(SQL);
            if (rs1.next()) {
                String mainid = Util.null2String(rs1.getString("id"));
                String sqr = Util.null2String(rs1.getString("sqr"));
                String hynr = Util.null2String(rs1.getString("hynr"));
                String xghy = Util.null2String(rs1.getString("xghy"));
                SQL = "select sxmc, jtnr, zrbm, zrr, yqwcsj, bz from " + tablename + "_dt1 where mainid=" + mainid;
                rs1.executeSql(SQL);
                while (rs1.next()) {
//                    String sxmc = Util.null2String(rs1.getString("sxmc"));//dbsx
//                    String jtnr = Util.null2String(rs1.getString("jtnr"));//jtnr
//                    String zrbm = Util.null2String(rs1.getString("zrbm"));//fzbm
//                    String zrr = Util.null2String(rs1.getString("zrr"));//zyfzr
//                    String yqwcsj = Util.null2String(rs1.getString("yqwcsj"));//yqwcsx
//                    String bz = Util.null2String(rs1.getString("bz"));

                    String dbsx = Util.null2String(rs1.getString("sxmc"));
                    String sxlx = "6";
                    String sbbh = "";
                    String jtnr = Util.null2String(rs1.getString("jtnr"));
                    String zyfzr = Util.null2String(rs1.getString("zrr"));
                    String fzbm = Util.null2String(rs1.getString("zrbm"));
                    String yqwcsx = Util.null2String(rs1.getString("yqwcsj"));
                    String wcjd = "0.0";
                    String zt = "1";
                    SQL = "insert into formtable_main_18 (dbsx, sxlx, sbbh, jtnr, zyfzr, fzbm, yqwcsx, wcjd, dbr， zt，xglc， xgfj, xghy) values(" +
                            "'" + dbsx + "', " + sxlx + ",'" + sbbh + "','" + jtnr + "'," + zyfzr + "," + fzbm + ",'" + yqwcsx + "'," + wcjd + "," +
                            sqr + "," + zt + "," + requestid + "," + hynr + "," + xghy + ")";
                    System.out.println("----Meeting2DubanAction---->" + SQL);
                    rs2.executeSql(SQL);
                }
            }
        }
        return Action.SUCCESS;
    }
}
