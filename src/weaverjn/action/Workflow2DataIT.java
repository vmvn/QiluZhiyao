package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaiyaqi on 2017/1/24.
 */
public class Workflow2DataIT extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo requestInfo) {
        String requestid = requestInfo.getRequestid();
        String t = "formtable_main_719";
        int FORMMODEID = 841;
        int MODEDATACREATER = 1;
        String sql = "select id,lcbh from " + t + " where requestid=" + requestid;
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        recordSet.next();

        String id = Util.null2String(recordSet.getString("id"));
        String lcbh = Util.null2String(recordSet.getString("lcbh"));

        String sbzl = getID("uf_sapjcsj_sbzl", "sbzldm", "C");
        String sbfl = getID("uf_sapjcsj_sbfl", "sbfldm", "0901");

        sql = "select sbbh,zcbh from " + t + "_dt1 where sbzl=" + sbzl + " and sbfl=" + sbfl + "  and mainid=" + id;
        log(sql);
        recordSet.executeSql(sql);
        while (recordSet.next()) {
            String zcbh = Util.null2String(recordSet.getString("zcbh"));
            String sbbh = Util.null2String(recordSet.getString("sbbh"));
            int newid = insertData(lcbh, zcbh, sbbh, FORMMODEID, MODEDATACREATER);
            if (newid > -1) {
                ModeRightInfo modeRightInfo = new ModeRightInfo();
                modeRightInfo.editModeDataShare(MODEDATACREATER, FORMMODEID, newid);
            }
        }

        return SUCCESS;
    }

    private void log(Object o) {
        String prefix = "<" + this.getClass().getName() + ">";
        System.out.println(prefix + o);
        writeLog(prefix + o);
    }

    private String getID(String table, String field, String fieldvalue) {
        RecordSet recordSet = new RecordSet();
        String sql = "select id from " + table + " where " + field + "='" + fieldvalue + "'";
        recordSet.executeSql(sql);
        String id = "-1";
        if (recordSet.next()) {
            id = recordSet.getString("id");
        }
        return id;
    }

    private int insertData(String lcbh, String zcbh, String sbbh, int FORMMODEID, int MODEDATACREATER) {
        RecordSet recordSet = new RecordSet();
        String sql = "insert into uf_itsbgl(lcbh,zcbh,sbh,FORMMODEID,MODEDATACREATER,MODEDATACREATERTYPE,MODEDATACREATEDATE,MODEDATACREATETIME) values(" +
                "'" + lcbh + "'," +
                "'" + zcbh + "'," +
                "'" + sbbh + "'," +
                "'" + FORMMODEID + "'," +
                "'" + MODEDATACREATER + "'," +
                "'" + 0 + "'," +
                "'" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "'," +
                "'" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "'" +
                ")";
        log(sql);
        int newid = -1;
        if (recordSet.executeSql(sql)) {
            sql = "select max(id) as newid from uf_itsbgl";
            recordSet.executeSql(sql);
            recordSet.next();
            newid = Util.getIntValue(recordSet.getString("newid"), -1);
        }
        return newid;
    }
}
