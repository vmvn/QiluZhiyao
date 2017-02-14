package weaverjn.action.integration;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by zhaiyaqi on 2017/2/10.
 */
public class GuardAction extends BaseBean implements Action {
    private String fieldname;
    @Override
    public String execute(RequestInfo requestInfo) {
        String requestid = requestInfo.getRequestid();
        String workflowid = requestInfo.getWorkflowid();
        RecordSet recordSet = new RecordSet();
        String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
        recordSet.executeSql(sql);
        recordSet.next();
        String tablename = recordSet.getString("tablename");

        sql = "select " + this.getFieldname() + " from " + tablename + " where requestid=" + requestid;
        log(sql);
        recordSet.executeSql(sql);
        recordSet.next();
        String v = Util.null2String(recordSet.getString(this.getFieldname()));
        if (v.equals("")) {
            requestInfo.getRequestManager().setMessageid("Message");
            requestInfo.getRequestManager().setMessagecontent("审批状态尚未回写，不能提交！");
        }
        return null;
    }

    private void log(Object o) {
        String prefix = "<" + this.getClass().getName() + ">";
        System.out.println(prefix + o);
        writeLog(prefix + o);
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }
}
