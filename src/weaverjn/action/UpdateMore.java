package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;

/**
 * Created by zhaiyaqi on 2017/5/5.
 */
public class UpdateMore extends BaseBean implements Action {
    private String table;//建模表单
    private String keyField;//主键字段名
    private String _dt;
    private String workflowFields;
    private String moduleFields;
    @Override
    public String execute(RequestInfo requestInfo) {
        writeLog("run");
        RequestManager requestManager = requestInfo.getRequestManager();
        if (Util.null2String(this.getTable()).isEmpty()
                || Util.null2String(this.getKeyField()).isEmpty()
                || Util.null2String(this.get_dt()).isEmpty()
                || Util.null2String(this.getWorkflowFields()).isEmpty()
                || Util.null2String(this.getModuleFields()).isEmpty()) {
            requestManager.setMessageid("warning");
            requestManager.setMessagecontent("action 参数不完整！");
        } else {
            String[] fields1 = this.getWorkflowFields().split(",");
            String[] fields2 = this.getModuleFields().split(",");
            if (fields1.length == fields2.length) {
                String t = requestManager.getBillTableName();
                String requestId = requestInfo.getRequestid();
                String sql = "select id," + this.getKeyField() + " from " + t + " where requestid=" + requestId;
                writeLog(sql);
                RecordSet recordSet = new RecordSet();
                recordSet.executeSql(sql);
                recordSet.next();
                String id = recordSet.getString("id");
                String keyValue = Util.null2String(recordSet.getString(this.getKeyField()));

                if (deleteFrom(this.getTable(), keyValue)) {
                    sql = "select " + this.getWorkflowFields() + " from " + t + "_dt" + this.get_dt() + " where mainid=" + id;
                    writeLog(sql);
                    recordSet.executeSql(sql);
                    RecordSet recordSet1 = new RecordSet();
                    while (recordSet.next()) {
                        StringBuilder newSql = new StringBuilder("insert into " + this.getTable() + " (mainid," + this.getModuleFields() + ") values(" + keyValue + ",");
                        for (int i = 0; i < fields1.length; i++) {
                            newSql.append("'").append(Util.null2String(recordSet.getString(fields1[i]))).append("'");
                            if (i + 1 < fields1.length) {
                                newSql.append(",");
                            }
                        }
                        newSql.append(")");
                        writeLog(newSql.toString());
                        recordSet1.executeSql(newSql.toString());
                    }
                }
            } else {
                requestManager.setMessageid("warning");
                requestManager.setMessagecontent("workflowFields 和 moduleFields 字段数量不一致！");
            }
        }
        return SUCCESS;
    }

    private boolean deleteFrom(String table, String id) {
        RecordSet recordSet = new RecordSet();
        String sql = "delete from " + table + " where mainid='" + id + "'";
        writeLog(sql);
        return recordSet.executeSql(sql);
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    public String get_dt() {
        return _dt;
    }

    public void set_dt(String _dt) {
        this._dt = _dt;
    }

    public String getWorkflowFields() {
        return workflowFields;
    }

    public void setWorkflowFields(String workflowFields) {
        this.workflowFields = workflowFields;
    }

    public String getModuleFields() {
        return moduleFields;
    }

    public void setModuleFields(String moduleFields) {
        this.moduleFields = moduleFields;
    }
}
