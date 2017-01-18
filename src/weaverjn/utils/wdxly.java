package weaverjn.utils;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

/**
 * Created by zhaiyaqi on 2017/1/9.
 */
public class wdxly extends BaseBean {
    public boolean canDelete(String billids) {
        boolean b = true;
        RecordSet recordSet = new RecordSet();
        String[] billida = billids.split(",");
        for (String billid : billida) {
            String sql = "select jszt from uf_yjyly where id=" + billid;
            log("----<wdxly>" + sql);
            recordSet.executeSql(sql);
            recordSet.next();
            String jszt = Util.null2String(recordSet.getString("jszt"));
            b = b && !jszt.equals("1");
        }
        return b;
    }

    public boolean canEdit(String billid, int userid) {
        RecordSet recordSet = new RecordSet();
        String sql = "select jszt,modedatacreater from uf_yjyly where id=" + billid;
        log("----<wdxly><canedit>" + sql);
        recordSet.executeSql(sql);
        recordSet.next();
        String jszt = Util.null2String(recordSet.getString("jszt"));
        int modedatacreater = recordSet.getInt("modedatacreater");
        return userid != modedatacreater || !jszt.equals("1");
    }

    private void log(Object o) {
        System.out.println(o);
        writeLog(o);
    }
}
