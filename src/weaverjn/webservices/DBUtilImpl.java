package weaverjn.webservices;

import weaver.conn.RecordSet;
import weaver.hrm.User;
import weaver.share.ShareManager;

/**
 * Created by zhaiyaqi on 2017/2/20.
 */
public class DBUtilImpl implements DBUtilService {
    @Override
    public boolean executeSql(String sql) {
        RecordSet recordSet = new RecordSet();
        return recordSet.executeSql(sql);
    }

    @Override
    public String getWfShareSql(int userid) {
        ShareManager shareManager = new ShareManager();
        User user = new User(userid);
        String sql = "select t2.id,t2.workflowname from workflow_base t2, ShareInnerWfCreate t1 where t1.workflowid=t2.id and t2.isvalid in ('1') and " + shareManager.getWfShareSqlWhere(user, "t1");
        return sql;
    }
}
