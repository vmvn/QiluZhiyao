package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.interfaces.schedule.BaseCronJob;
import weaverjn.utils.Workflow;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dzyq on 2016/8/1 9:54.
 */
public class DuBanSchedule extends BaseCronJob {
    private final BaseBean baseBean = new BaseBean();

    private void log(Object o) {
        baseBean.writeLog(this.getClass().getName() + " - " + o);
    }

    public void execute() {
        log("run");
        RecordSet recordSet = new RecordSet();
        String sql = "select * from formtable_main_18 where (remindflag1 is null or remindflag1=1) and wcjd=100 and zt=3";
        recordSet.executeSql(sql);
        while (recordSet.next()) {
            String id = recordSet.getString("id");
            String dbsx = recordSet.getString("dbsx");//督办事项
            String zyfzr1 = recordSet.getString("zyfzr1");//承办单位负责人
            String fzbm = recordSet.getString("fzbm");//承办单位
            String zyfzr = recordSet.getString("zyfzr");//协办人
            String jtnr = recordSet.getString("jtnr");//办理事项
            String wcjd = recordSet.getString("wcjd");//完成进度
            String jbr = recordSet.getString("jbr");//完成进度

            Map<String, String> map = new HashMap<String, String>();
            map.put("dbsx", id);
            map.put("jbr", jbr);
            map.put("cbdw", fzbm);
            map.put("cbdwfzr", zyfzr1);
            map.put("xbr", zyfzr);
            map.put("blsx", jtnr);
            map.put("dbwcjd", wcjd);

//            String db = "<a href=\"javascript:modeopenFullWindowHaveBar(''/formmode/view/AddFormMode.jsp?type=0&modeId=101&formId=-18&billid=" + id + "&opentype=0&customid=101&viewfrom=fromsearchlist'',''" + id + "'')\">" + dbsx + "</a>";
//            String content = "督察督办：" + db + "<br/>请提交里程碑资料。";
//            SysRemindWorkflow sysRemindWorkflow = new SysRemindWorkflow();
            try {
//                sysRemindWorkflow.make("督察督办：" + dbsx + " 提交里程碑资料提醒", 0, 0, 0, 0, 1, zyfzr1, content);
                Workflow workflow = Workflow.createInstance("1", "8467", "督察督办：" + dbsx + " 提交里程碑资料提醒", map);
                workflow.next();
                sql = "update formtable_main_18 set remindflag1=0 where id=" + id;
                RecordSet recordSet1 = new RecordSet();
                recordSet1.executeSql(sql);
            } catch (Exception e) {
                log("Duban:" + id + " SysRemindWorkflow error!");
                e.printStackTrace();
            }
        }

        //到期未完成状态变为延期
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(calendar.getTime());
        sql = "update formtable_main_18 set zt=0 where zt=3 and (wcjd<100 or wcjd is null) and yqwcsx<'" + today + "'";
        recordSet.executeSql(sql);
    }
}
