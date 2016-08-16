package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by dzyq on 2016/5/6.
 */
public class LipinAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        System.out.println("---->LipinAction start");
        RecordSet rs1 = new RecordSet();
        RecordSet rs2 = new RecordSet();
        String requestid = requestInfo.getRequestid();
        String workflowid = requestInfo.getWorkflowid();
        String tablename = "";
        String err = "";
        String SQL = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
        rs1.executeSql(SQL);
        if (rs1.next()) {
            tablename = Util.null2String(rs1.getString("tablename"));
            String operator;
            if (tablename.equals("formtable_main_38")) {
                operator = "-";
            }else if (tablename.equals("formtable_main_37")) {
                operator = "+";
            } else {
                operator = "!";
            }
            if (!operator.equals("!")) {
                SQL = "select id from " + tablename + " where requestid=" + requestid;
                rs1.executeSql(SQL);
                if (rs1.next()) {
                    String id = Util.null2String(rs1.getString("id"));
                    SQL = "select mc,sll from " + tablename + "_dt1 where mainid=" + id;
                    rs1.executeSql(SQL);
                    String mc = "";
                    String sll = "";
                    while (rs1.next()) {
                        mc = Util.null2String(rs1.getString("mc"));
                        sll = Util.null2String(rs1.getString("sll"), "0");
                        String kcsll = "";
                        rs2.executeSql("select kcsll from uf_lpjcxx where id=" + mc);
                        if (rs2.next()) {
                            kcsll = Util.null2String(rs2.getString("kcsll"), "0");
                        }
                        if ((Util.getIntValue(kcsll) < Util.getIntValue(sll)) && operator.equals("-")) {
                            err = "库存数量不足！当前库存数量：" + kcsll;
                        }else{
                            System.out.println("---->LipinAction: " + "update uf_lpjcxx set kcsll=kcsll" + operator + sll + " where id=" + mc);
                            int result;
                            if (operator.equals("+")) {
                                result = Integer.parseInt(kcsll) + Integer.parseInt(sll);
                            } else {
                                result = Integer.parseInt(kcsll) - Integer.parseInt(sll);
                            }
                            rs2.executeSql("update uf_lpjcxx set kcsll=" + result + " where id=" + mc);
                        }
                    }
                }
            } else {
                requestInfo.getRequestManager().setMessageid("90031");
                requestInfo.getRequestManager().setMessagecontent("配错Action了！");
            }
            if (!err.isEmpty()) {
                requestInfo.getRequestManager().setMessageid("90031");
                requestInfo.getRequestManager().setMessagecontent(err);
            }
        }
        return Action.SUCCESS;
    }
}
