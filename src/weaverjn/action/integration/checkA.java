package weaverjn.action.integration;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by zhaiyaqi on 2017/3/8.
 */
public class checkA extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        String src = requestInfo.getRequestManager().getSrc();
        if (!src.equals("reject")) {
            String tableName = utils.getTableName(requestInfo.getWorkflowid());
            String requestId = requestInfo.getRequestid();
            RecordSet recordSet = new RecordSet();
            String sql = "select sbbh, gnwz from " + tableName + " where requestid=" + requestId;
            recordSet.executeSql(sql);
            recordSet.next();
            String sbbh = Util.null2String(recordSet.getString("sbbh"));
            String gnwz = Util.null2String(recordSet.getString("gnwz"));
            if (sbbh.isEmpty() && gnwz.isEmpty()) {
                requestInfo.getRequestManager().setMessageid("Message");
                requestInfo.getRequestManager().setMessagecontent("设备编号和功能位置不能同时为空！");
            }
        }
        return SUCCESS;
    }
}
