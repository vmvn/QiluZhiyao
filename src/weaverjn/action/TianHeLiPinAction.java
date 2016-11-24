package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by zhaiyaqi on 2016/11/2.
 */
public class TianHeLiPinAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        System.out.println("---->TianHeLiPinAction:run");
        RecordSet recordSet = new RecordSet();
        String workflowId = requestInfo.getWorkflowid();
        String requestId = requestInfo.getRequestid();
        String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowId;
        String message = "";
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            String tableName = recordSet.getString("tablename");
            sql = "select lpmc, sll from " + tableName + "_dt1 where mainid=(select id from " + tableName + " where requestid=" + requestId + ")";
            recordSet.executeSql(sql);
            int i = 0;
            boolean bool = true;
            while (recordSet.next()) {
                i++;
                int mc = recordSet.getInt("lpmc");
                int sl = recordSet.getInt("sll");
                boolean value = beforeOut(mc, sl);
                bool = bool && value;
                if (!value) {
                    message += "第" + i + "行，库存不足；";
                }
            }
            if (bool) {
                recordSet.executeSql(sql);
                while (recordSet.next()) {
                    int mc = recordSet.getInt("lpmc");
                    int sl = recordSet.getInt("sll");
                    out(mc, sl);
                }
            }
        }
        if (!message.isEmpty()) {
            requestInfo.getRequestManager().setMessageid("90031");
            requestInfo.getRequestManager().setMessagecontent(message);
        }
        return SUCCESS;
    }

    private boolean beforeOut(int mc, int sl) {
        RecordSet recordSet = new RecordSet();
        String sql = "select kcsl from uf_lpkxx where id=" + mc;
        recordSet.executeSql(sql);
        recordSet.next();
        int kcsl = Util.getIntValue(recordSet.getString("kcsl"), 0);
        if (kcsl > sl) {
            return true;
        }
        return false;
    }
    private void out(int mc, int sl) {
        RecordSet recordSet = new RecordSet();
        RecordSet update = new RecordSet();
        String sql = "select kcsl from uf_lpkxx where id=" + mc;
        recordSet.executeSql(sql);
        recordSet.next();
        int kcsl = Util.getIntValue(recordSet.getString("kcsl"), 0);
        int rest = kcsl - sl;
        sql = "update uf_lpkxx set kcsl=" + rest + " where id=" + mc;
        update.executeSql(sql);
    }
}
