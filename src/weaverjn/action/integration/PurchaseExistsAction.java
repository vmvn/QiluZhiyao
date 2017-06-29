package weaverjn.action.integration;

import java.util.Map;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;

public class PurchaseExistsAction extends BaseBean implements Action
{
    public String execute(RequestInfo requestInfo)
    {
        Map<String, String> map = utils.getMainTableData(requestInfo.getMainTableInfo());
        RequestManager requestManager = requestInfo.getRequestManager();
        String tableName = requestManager.getBillTableName();
        RecordSet rs = new RecordSet();
        String sql = "select * from " + tableName + " where ghfbh='" + map.get("ghfbh") + "' and requestid!=" + requestInfo.getRequestid();
        writeLog("查询是否有该条记录： " + sql);
        rs.executeSql(sql);
        if (rs.next()) {
            requestManager.setMessageid("WARNING");
            requestManager.setMessagecontent("该客户已经审批通过，不能重复审批");
        }
        return SUCCESS;
    }
}