package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.mobile.webservices.workflow.soa.RequestInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by dzyq on 2016/7/13 13:57.
 */
public class WDXLYAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String workflowId = requestInfo.getWorkflowid();
        String tableMain = getMainTableName(workflowId);
        if (!tableMain.isEmpty()) {
            RecordSet recordSet = new RecordSet();
            String sql = "select * from " + tableMain + " where requestid=" + requestId;
            recordSet.executeSql(sql);
            if (recordSet.next()) {
                String id = recordSet.getString("id");

                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

                sql = "select * from " + tableMain + "_dt1 where mainid=" + id;
                RecordSet recordSet1 = new RecordSet();
                recordSet1.executeSql(sql);
                while (recordSet1.next()) {
                    String fztj = recordSet1.getString("fztj");
                    String syl = recordSet1.getString("syl");
                    String dw = recordSet1.getString("dw");

                    sql = "select * from uf_lyfztjwh where id=" + fztj;
                    RecordSet recordSet2 = new RecordSet();
                    recordSet2.executeSql(sql);
                    if (recordSet2.next()) {
                        String fzqj = recordSet2.getString("fzqj");
                        int dw1 = recordSet2.getInt("dw") == 0 ? Calendar.DATE : Calendar.MONTH;
                        String[] strings = fzqj.split(",");

                        for (String s : strings) {
                            int i = Integer.parseInt(s);
                            Calendar calendar = Calendar.getInstance();
                            String today = sdfDate.format(calendar.getTime());
                            calendar.add(dw1, i);
                            String date = sdfDate.format(calendar.getTime());
                            String time = sdfTime.format(calendar.getTime());
                        }
                    }
                }
            }
        }
        return SUCCESS;
    }

    private String getMainTableName(String workflowId) {
        RecordSet recordSet = new RecordSet();
        String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowId;
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            return recordSet.getString("tablename");
        }
        return "";
    }
}
