package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;
import weaverjn.action.integration.utils;

import java.util.Map;

/**
 * Created by zhaiyaqi on 2017/5/30.
 */
public class existsCheckSYQY extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        Map<String, String> mtData = utils.getMainTableData(requestInfo.getMainTableInfo());
        RequestManager requestManager = requestInfo.getRequestManager();
        String ghfmc = mtData.get("ghfmc");
        if (ghfmc.isEmpty()) {
            requestManager.setMessagecontent("WARNING");
            requestManager.setMessagecontent("ghfmc is null");
        } else {
            String t = requestManager.getBillTableName();
            String sql = "select * from " + t + " where ghfmc=" + ghfmc;
            RecordSet recordSet = new RecordSet();
            recordSet.executeSql(sql);
            if (recordSet.next()) {
                requestManager.setMessagecontent("WARNING");
                requestManager.setMessagecontent("此企业已审批或在审批过程中");
            }
        }
        return SUCCESS;
    }
}
