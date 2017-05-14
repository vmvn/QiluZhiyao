package weaverjn.action.update;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;
import weaverjn.action.integration.utils;

import java.util.Map;

/**
 * Created by zhaiyaqi on 2017/5/11.
 */
public class UpdateModule extends BaseBean implements Action {
    private String _dt;//MT or DT
    private String table;
    private String WFieldK;//流程字段
    private String MFieldK;//建模字段
    private String WField;
    private String MField;

    @Override
    public String execute(RequestInfo requestInfo) {
        if (!Util.null2String(this.WFieldK).isEmpty()
                && !Util.null2String(this.MFieldK).isEmpty()
                && !Util.null2String(this.table).isEmpty()
                && !Util.null2String(this.WField).isEmpty()
                && !Util.null2String(this.MField).isEmpty()) {
            RecordSet recordSet = new RecordSet();
            if (Util.null2String(this._dt).isEmpty()) {
                Map<String, String> MtData = utils.getMainTableData(requestInfo.getMainTableInfo());
                String sql = "update " + this.table + " set " + this.MField + "='" + MtData.get(this.WField) + "' where " + this.MFieldK + "='" + MtData.get(this.WFieldK) + "'";
                if (!recordSet.executeSql(sql)) {
                    writeLog(sql);
                }
            } else {
                RequestManager requestManager = requestInfo.getRequestManager();
                String tableName = requestManager.getBillTableName();
                String dt = tableName + "_dt" + this._dt;
                String id = "(select id from " + tableName + " where requestid='" + requestInfo.getRequestid() + "')";

                String sql = "select " + this.WField + " from " + dt + " where mainid=" + id;
                recordSet.executeSql(sql);
                RecordSet recordSet1 = new RecordSet();
                while (recordSet.next()) {
                    sql = "update " + this.table + " set " + this.MField + "='" + Util.null2String(recordSet.getString(this.WField)) + "' where " + this.MFieldK + "='" + this.WFieldK + "'";
                    if (!recordSet1.executeSql(sql)) {
                        writeLog(sql);
                    }
                }
            }

        }
        return SUCCESS;
    }

    public String get_dt() {
        return _dt;
    }

    public void set_dt(String _dt) {
        this._dt = _dt;
    }

    public String getWFieldK() {
        return WFieldK;
    }

    public void setWFieldK(String WFieldK) {
        this.WFieldK = WFieldK;
    }

    public String getMFieldK() {
        return MFieldK;
    }

    public void setMFieldK(String MFieldK) {
        this.MFieldK = MFieldK;
    }

    public String getWField() {
        return WField;
    }

    public void setWField(String WFields) {
        this.WField = WFields;
    }

    public String getMField() {
        return MField;
    }

    public void setMField(String MField) {
        this.MField = MField;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
