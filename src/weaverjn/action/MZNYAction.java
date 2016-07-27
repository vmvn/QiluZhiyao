package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by dzyq on 2016/7/26 15:02.
 */
public class MZNYAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String workflowId = requestInfo.getWorkflowid();
        String tableMain = getMainTableName(workflowId);
        String msg = "";
        if (!tableMain.isEmpty()) {
            if (tableMain.equals("formtable_main_377")) {
                String sql = "select id from " + tableMain + " where requestid=" + requestId;
                RecordSet recordSet = new RecordSet();
                recordSet.executeSql(sql);
                if (recordSet.next()) {
                    String id = recordSet.getString("id");
                    sql = "select * from " + tableMain + "_dt1 where mainid=" + id;
                    RecordSet recordSet1 = new RecordSet();
                    recordSet1.executeSql(sql);
                    int i = 0;
                    while (recordSet1.next()) {
                        i++;
                        String ypmckcId = recordSet1.getString("ypmc");
                        int sl = recordSet1.getInt("sl");
                        sql = "select * from uf_ypkcgl where id=" + ypmckcId;
                        RecordSet recordSet2 = new RecordSet();
                        recordSet2.executeSql(sql);
                        recordSet2.next();
                        int kcl = recordSet2.getInt("kcl");
                        int sysl = recordSet2.getInt("sysl");
                        if (sl > sysl) {
                            msg = msg + ("第" + i + "行，库存数量不足；");
                            continue;
                        }
                        int lysl = kcl - sysl + sl;
                        sql = "update uf_ypkcgl set sysl=sysl-" + sl + ", lysl=" + lysl + " where id=" + ypmckcId;
                        recordSet2.executeSql(sql);
                    }
                }
            }
        }
        if (!msg.isEmpty()) {
            requestInfo.getRequestManager().setMessageid("90031");
            requestInfo.getRequestManager().setMessagecontent(msg);
        }
        return SUCCESS;
    }
    private String getMainTableName(String workflowId) {
        RecordSet recordSet = new RecordSet();
        String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowId;
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            return recordSet.getString("tablename");
        }
        return "";
    }
}
