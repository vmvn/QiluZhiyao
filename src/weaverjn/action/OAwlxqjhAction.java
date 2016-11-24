package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;

/**
 * Created by zhaiyaqi on 2016/11/19.
 */
public class OAwlxqjhAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        String message = "";
        if (!src.equals("reject")) {
            String wfid = requestInfo.getWorkflowid();
            String requestid = requestInfo.getRequestid();
            RecordSet recordSet = new RecordSet();
            String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + wfid;
            recordSet.executeSql(sql);
            recordSet.next();
            String t = recordSet.getString("tablename");
            sql = "select * from " + t + " where requestid=" + requestid;
            recordSet.executeSql(sql);
            if (recordSet.next()) {
                int id = recordSet.getInt("id");
                String DEMANDPLAN_NO = Util.null2String(recordSet.getString("lcbh"));
                String MOVE_TYPE = getSelectName(recordSet.getInt("ydlx"), 23968);
                String DEPARTMENT = getDepartmentName(recordSet.getInt("sqbm"));
                String REQUISITOR = getLastName(recordSet.getInt("sqr"));
                String WERKS = recordSet.getString("gcbm");

            }
        }
        return SUCCESS;
    }

    private String getLastName(int id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select lastname from hrmresource where id=" + id;
        recordSet.executeSql(sql);
        String lastname = "";
        if (recordSet.next()) {
            lastname = recordSet.getString("lastname");
        }
        return lastname;
    }

    private String getDepartmentName(int id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select departmentname from hrmdepartment where id=" + id;
        recordSet.executeSql(sql);
        String departmentname = "";
        if (recordSet.next()) {
            departmentname = recordSet.getString("departmentname");
        }
        return departmentname;
    }

    private String getSelectName(int selectvalue, int fieldid) {
        RecordSet recordSet = new RecordSet();
        String sql = "select selectname from workflow_selectitem where fieldid=" + fieldid + " and selectvalue=" + selectvalue;
        recordSet.executeSql(sql);
        recordSet.next();
        return recordSet.getString("selectname");
    }
}
