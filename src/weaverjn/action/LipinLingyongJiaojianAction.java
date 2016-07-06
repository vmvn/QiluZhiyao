package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by dzyq on 2016/6/24.
 */
public class LipinLingyongJiaojianAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        RecordSet rs1 = new RecordSet();
        RecordSet rs2 = new RecordSet();
        String requestid = requestInfo.getRequestid();
        String err = "";
        String SQL = "select id from formtable_main_38 where requestid=" + requestid;
        rs1.executeSql(SQL);
        if (rs1.next()) {
            String id = Util.null2String(rs1.getString("id"));
            SQL = "select mc,sl from formtable_main_38_dt1 where mainid=" + id;
            rs1.executeSql(SQL);
            String mc = "";
            String sl = "";
            int i = 0;
            while (rs1.next()) {
                i++;
                mc = Util.null2String(rs1.getString("mc"));
                sl = Util.null2String(rs1.getString("sl"), "0");
                String kcsl = "";
                rs2.executeSql("select kcsl from uf_lpjcxx where id=" + mc);
                if (rs2.next()) {
                    kcsl = Util.null2String(rs2.getString("kcsl"), "0");
                }
                System.out.println("---->kcsl:" + kcsl + "---->sl" + sl);
                if ((Util.getIntValue(kcsl) < Util.getIntValue(sl))) {
                    err = "第" + i + "行库存数量不足！当前库存数量：" + kcsl + "\n";
                }
            }
        }
        if (!err.isEmpty()) {
            requestInfo.getRequestManager().setMessageid("90031");
            requestInfo.getRequestManager().setMessagecontent(err);
        }
        return SUCCESS;
    }
}
