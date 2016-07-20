package weaverjn.formmode;

import weaver.conn.RecordSet;
import weaver.formmode.interfaces.action.BaseAction;
import weaver.general.Util;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by dzyq on 2016/7/15 16:49.
 */
public class YJYQYAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        String billid = requestInfo.getRequestid();
        RecordSet recordSet = new RecordSet();
        String sql = "select dtid, qyl, syl1, jsbj from uf_yjyqy where id=" + billid;
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            String dtid = Util.null2String(recordSet.getString("dtid"));
            int qyl = recordSet.getInt("qyl");
            int syl1 = recordSet.getInt("syl1");
            int jsbj = recordSet.getInt("jsbj");
            RecordSet recordSet1 = new RecordSet();

            int newShengYuLiang = syl1 - qyl;
            sql = "update uf_yjyqy set syl1=" + newShengYuLiang + " where dtid=" + dtid + " and id>" + billid;
            sql = "update uf_yjyqy set syl1=" + newShengYuLiang + " where dtid=" + dtid + " and id>=" + billid;
            System.out.println("-------->YJYQYAction:" + sql);
            recordSet1.executeSql(sql);
            if (jsbj == 1 || jsbj == 2) {
                sql = "update uf_yjyqy set jsbj=" + jsbj + " where dtid=" + dtid + " and id>" + billid;
                sql = "delete from uf_yjyqy where dtid=" + dtid + " and id>" + billid;
                System.out.println("-------->YJYQYAction:jsbj:" + sql);
                recordSet1.executeSql(sql);
            }
        }
        return SUCCESS;
    }
}
