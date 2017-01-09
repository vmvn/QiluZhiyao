package weaverjn.qlzy.sap.webservice.oa.workflow;

/**
 * Created by zhaiyaqi on 2016/12/5.
 */
public class yiqishebeizengzhiParameters {
    private String createTime;
    private String creatorId;
    private String requestLevel;
    private String requestName;
    private workflowBaseInfo workflowBaseInfo;
    private yiqishebeizengzhiMainTable mainTable;
    private yiqishebeizengzhiDetailTable1[] dt1;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getRequestLevel() {
        return requestLevel;
    }

    public void setRequestLevel(String requestLevel) {
        this.requestLevel = requestLevel;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public weaverjn.qlzy.sap.webservice.oa.workflow.workflowBaseInfo getWorkflowBaseInfo() {
        return workflowBaseInfo;
    }

    public void setWorkflowBaseInfo(weaverjn.qlzy.sap.webservice.oa.workflow.workflowBaseInfo workflowBaseInfo) {
        this.workflowBaseInfo = workflowBaseInfo;
    }

    public yiqishebeizengzhiMainTable getMainTable() {
        return mainTable;
    }

    public void setMainTable(yiqishebeizengzhiMainTable mainTable) {
        this.mainTable = mainTable;
    }

    public yiqishebeizengzhiDetailTable1[] getDt1() {
        return dt1;
    }

    public void setDt1(yiqishebeizengzhiDetailTable1[] dt1) {
        this.dt1 = dt1;
    }
}
