package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by dzyq on 2016/8/29 9:26.
 */
public class XiaoShouLiPinAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        System.out.println("---->XiaoShouLiPinAction start");
        RecordSet rs1 = new RecordSet();
        RecordSet rs2 = new RecordSet();
        String requestid = requestInfo.getRequestid();
        String workflowid = requestInfo.getWorkflowid();
        String tablename = "";
        String msg = "";
        String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
        rs1.executeSql(sql);
        if (rs1.next()) {
            tablename = rs1.getString("tablename");
            String detail = "";
            if ("formtable_main_408".equals(tablename)) {
                detail = "dt2";
            } else if ("formtable_main_410".equals(tablename) || "formtable_main_409".equals(tablename)) {
                detail = "dt1";
            } else {
                detail = "error";
            }
            if (!detail.equals("error")) {
                sql = "select * from " + tablename + "_" + detail + " where mainid = (select id from " + tablename + " where requestid=" + requestid + ")";
                rs1.executeSql(sql);
                int i = 0;
                while (rs1.next()) {
                    i++;
                    String lpmc = rs1.getString("lpmc");
                    int sll = Util.getIntValue(rs1.getString("sll"), 0);
                    sql = "select kc from uf_lpcx where id=" + lpmc;
                    rs2.executeSql(sql);
                    if (rs2.next()) {
                        int kc = Util.getIntValue(rs2.getString("kc"), 0);
                        int j;
                        if (detail.equals("dt2")) {
                            j = kc + sll;
                        } else {
                            j = kc - sll;
                        }
                        if (j < 0) {
                            msg += "第" + i + "行，库存为：" + kc + "，不足。";
                        } else {
                            rs2.executeSql("update uf_lpcx set kc=" + j + " where id=" + lpmc);
                        }
                    }
                }
            } else {
                msg = "action 配置错误";
            }
        }
        if (!msg.isEmpty()) {
            requestInfo.getRequestManager().setMessageid("90031");
            requestInfo.getRequestManager().setMessagecontent(msg);
        }
        return SUCCESS;
    }
}
