package weaverjn.qlzy.oa;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;

/**
 * Created by zhaiyaqi on 2016/12/7.
 */
public class anotherOA extends BaseBean {
    public void getAnotherOAToDoList(String dataSource, String userId) {
        RecordSetDataSource recordSetDataSource = new RecordSetDataSource(dataSource);
        RecordSet recordSet = new RecordSet();
        String sql = "select distinct req.requestname," +
                "                log.requestid," +
                "                log.receivedate," +
                "                log.receivetime," +
                "                req.createdate," +
                "                req.createtime," +
                "                (select lastname from (select id,lastname from HrmResource union select id, lastname from HrmResourceManager) where id = req.creater) lastname," +
                "                (select workflowname" +
                "                   from workflow_base" +
                "                  where id = req.workflowid) wfname" +
                "  from workflow_requestbase req, workflow_currentoperator log" +
                " where log.requestid = req.requestid" +
                "   and log.isremark in ('0', '1', '8', '9')" +
                "   and log.userid = " + userId +
                " order by receivedate desc, receivetime desc";
        recordSetDataSource.executeSql(sql);

        sql = "delete from another_oa_todolist where userid=" + userId;
        recordSet.executeSql(sql);
        while (recordSetDataSource.next()) {
            String requestid = recordSetDataSource.getString("requestid");
            String requestname = recordSetDataSource.getString("requestname");
            String createdate = recordSetDataSource.getString("createdate");
            String createtime = recordSetDataSource.getString("createtime");
            String receivedate = recordSetDataSource.getString("receivedate");
            String receivetime = recordSetDataSource.getString("receivetime");
            String lastname = recordSetDataSource.getString("lastname");
            String wfname = recordSetDataSource.getString("wfname");

            sql = "insert into another_oa_todolist(REQUESTID, REQUESTNAME, CREATEDATE, CREATETIME, RECEIVEDATE, RECEIVETIME, LASTNAME, WFNAME, USERID) values(" +
                    "" + requestid + "," +
                    "'" + requestname + "'," +
                    "'" + createdate + "'," +
                    "'" + createtime + "'," +
                    "'" + receivedate + "'," +
                    "'" + receivetime + "'," +
                    "'" + lastname + "'," +
                    "'" + wfname + "'," +
                    "" + userId + "" +
                    ")";
            recordSet.executeSql(sql);
        }
    }
}
