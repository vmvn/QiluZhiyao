package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by dzyq on 2016/8/17 15:08.
 */
public class RebuildModeDataShareAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        String requestid = requestInfo.getRequestid();
        String workflowid = requestInfo.getWorkflowid();
        String tablename = "";
        String err = "";
        String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            tablename = recordSet.getString("tablename");
            sql = "select * from " + tablename + " where requestid=" + requestid;
            recordSet.executeSql(sql);
            if (recordSet.next()) {
                String wtms = recordSet.getString("wtms");
                sql = "select * from uf_sjwtkb where id=" + wtms;
                recordSet.executeSql(sql);
                if (recordSet.next()) {
                    int billid = recordSet.getInt("id");
                    int formmodeid = recordSet.getInt("formmodeid");
                    int modedatacreater = recordSet.getInt("modedatacreater");
                    ModeRightInfo ModeRightInfo = new ModeRightInfo();
                    ModeRightInfo.rebuildModeDataShareByEdit(modedatacreater, formmodeid, billid);
                } else {
                    err = "error code: 2";
                }
            } else {
                err = "error code: 1";
            }
        }
        if (!err.isEmpty()) {
            requestInfo.getRequestManager().setMessageid("90031");
            requestInfo.getRequestManager().setMessagecontent(err);
        }
        return SUCCESS;
    }
}
