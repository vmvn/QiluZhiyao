package weaverjn.webservices;

import weaver.conn.RecordSet;

/**
 * Created by zhaiyaqi on 2017/2/20.
 */
public class DBUtilImpl implements DBUtilService {
    @Override
    public boolean executeSql(String sql) {
        RecordSet recordSet = new RecordSet();
        return recordSet.executeSql(sql);
    }
}
