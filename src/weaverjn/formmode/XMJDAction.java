package weaverjn.formmode;


import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by dzyq on 2016/7/14 16:41.
 */
public class XMJDAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        String billid = requestInfo.getRequestid();
        RecordSet recordSet = new RecordSet();
        String sql = "select * from uf_xmjzqk where id=" + billid;
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            String gznr = Util.null2String(recordSet.getString("gznr"));
            String jhjd = Util.null2String(recordSet.getString("jhjd"));
            String jkcs = Util.null2String(recordSet.getString("jkcs"));
            String mqzt = Util.null2String(recordSet.getString("mqzt"));
            String sfajhjx = Util.null2String(recordSet.getString("sfajhjx"));
            String gzjz = Util.null2String(recordSet.getString("gzjz"));
            String gzjh = Util.null2String(recordSet.getString("gzjh"));
            String czwt = Util.null2String(recordSet.getString("czwt"));
            String jjcs = Util.null2String(recordSet.getString("jjcs"));
            String xgxm = Util.null2String(recordSet.getString("xgxm"));
            String ztjz = Util.null2String(recordSet.getString("ztjz"));

            sql = "update Prj_ProjectInfo set " +
                    "gznr='" + gznr + "', " +
                    "jhjd='" + jhjd + "', " +
                    "jkcs='" + jkcs + "', " +
                    "mqzt='" + mqzt + "', " +
                    "sfajhwc='" + sfajhjx + "', " +
                    "bzgzjz='" + gzjz + "', " +
                    "xzgzjh='" + gzjh + "', " +
                    "czdwt='" + czwt + "', " +
                    "xmztjz='" + ztjz + "', " +
                    "jjcs='" + jjcs + "' " +
                    "where id=" + xgxm;
            if(!recordSet.executeSql(sql)){
                System.out.println("------------>" + sql);
            }
        }
        return SUCCESS;
    }
}
