package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;

/**
 * Created by zhaiyaqi on 2017/4/27.
 */
public class UpdateForm extends BaseBean implements Action {
    private String table;//建模表单
    private String keyType;//主键类型MT主表DT明细表
    private String keyField;//主键字段名
    private String fieldItem;//变更项目字段
    private String fieldValue;//变更后内容
    private String uf_field;//建模表单主键字段名

    @Override
    public String execute(RequestInfo requestInfo) {
        writeLog("run");
        RequestManager requestManager = requestInfo.getRequestManager();
        if (Util.null2String(this.getTable()).isEmpty()
                || Util.null2String(this.getKeyType()).isEmpty()
                || Util.null2String(this.getKeyField()).isEmpty()
                || Util.null2String(this.getFieldItem()).isEmpty()
                || Util.null2String(this.getFieldValue()).isEmpty()
                || Util.null2String(this.getUf_field()).isEmpty()) {
            requestManager.setMessageid("warning");
            requestManager.setMessagecontent("action 参数不完整！");
        } else {
            writeLog(this.getTable());
            String tableName = requestManager.getBillTableName();
            String requestId = requestInfo.getRequestid();
            RecordSet recordSet = new RecordSet();
            String sql;
            String uf_value = "";
            if (this.getKeyType().equals("MT")) {
                sql = "select " + this.getKeyField() + " from " + tableName + " where requestid=" + requestId;
                writeLog(sql);
                recordSet.executeSql(sql);
                recordSet.next();
                uf_value = Util.null2String(recordSet.getString(this.getKeyField()));
            }
            sql = "select id from " + tableName + " where requestid=" + requestId;
            writeLog(sql);
            recordSet.executeSql(sql);
            recordSet.next();
            String id = recordSet.getString("id");

            sql = "select * from " + tableName + "_dt1 where mainid=" + id;
            recordSet.executeSql(sql);
            while (recordSet.next()) {
                if (this.getKeyType().equals("DT")) {
                    uf_value = Util.null2String(recordSet.getString(this.getKeyField()));
                }
                String field = Util.null2String(recordSet.getString(this.getFieldItem()));
                String value = Util.null2String(recordSet.getString(this.getFieldValue()));

                updateForm(uf_value, field, value);
            }
        }
        return SUCCESS;
    }

    private void updateForm(String uf_value, String field, String value) {
        RecordSet recordSet = new RecordSet();
        String sql = "update " + this.getTable() + " set " + field + "='" + value + "' where " + this.getUf_field() + "='" + uf_value + "'";
        writeLog(sql);
        recordSet.executeSql(sql);
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    public String getFieldItem() {
        return fieldItem;
    }

    public void setFieldItem(String fieldItem) {
        this.fieldItem = fieldItem;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getUf_field() {
        return uf_field;
    }

    public void setUf_field(String uf_field) {
        this.uf_field = uf_field;
    }
}
