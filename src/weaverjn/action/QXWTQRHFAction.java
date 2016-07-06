package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by dzyq on 2016/7/4.
 */
public class QXWTQRHFAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        System.out.println("---->QXWTQRHFAction");
        String requestId = requestInfo.getRequestid();
        RecordSet recordSet = new RecordSet();
        String sql = "select id from formtable_main_195 where requestid=" + requestId;
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            String mainId = recordSet.getString("id");
            sql = "delete from formtable_main_195_dt1 where sfhf=0 and mainid=" + mainId;
            System.out.println("---->sql:" + sql);
            recordSet.executeSql(sql);
        }
        return SUCCESS;
    }
}
