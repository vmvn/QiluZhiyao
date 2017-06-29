package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;
import weaverjn.action.integration.utils;

import java.util.Map;

/**
 * Created by zhaiyaqi on 2017/6/3.
 */
public class UpdateWarehouseAddress extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String tableName = requestManager.getBillTableName();
        int formId = requestManager.getFormid();
        int billId = requestManager.getBillid();

        String modTableName = utils.getModTableName("ghdwmc1", formId);
        Map<String, String> mtData = utils.getMainTableData(requestInfo.getMainTableInfo());
        String modBillId = mtData.get("ghdwmc1");

        String dt5 = tableName + "_dt5";
        String modDt1 = modTableName + "_dt1";
        String sql = "delete from " + modDt1 + " where mainid=" + modBillId;
        writeLog(sql);
        RecordSet recordSet = new RecordSet();
        if (recordSet.executeSql(sql)) {
            sql = "select * from " + dt5 + " where mainid=" + billId;
            writeLog(sql);
            recordSet.executeSql(sql);
            RecordSet recordSet1 = new RecordSet();
            while (recordSet.next()) {
                sql = "insert into " + modDt1 + "(mainid, ckdz, yxqz)" +
                        "values(" + modBillId + ",'" + Util.null2String(recordSet.getString("ckdz") + "','" + Util.null2String(recordSet.getString("ckdzyxq") + "')"));
                writeLog(sql);
                recordSet1.executeSql(sql);
            }
        }
        return SUCCESS;
    }
}
