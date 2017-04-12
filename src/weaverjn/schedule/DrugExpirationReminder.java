package weaverjn.schedule;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;
import weaver.general.BaseBean;
import weaver.interfaces.schedule.BaseCronJob;
import weaverjn.utils.Workflow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhaiyaqi on 2017/4/6.
 */
public class DrugExpirationReminder extends BaseCronJob {
    private BaseBean baseBean = new BaseBean();

    public static void main(String[] args) {
        new DrugExpirationReminder().execute();
    }

    public void execute() {
        reminder("","","3110");//万润
//        reminder("","","");//万和
//        reminder("","","");//鲁海
    }

    private Properties getLogonProperties() {
        Properties properties = new Properties();
        properties.put("jco.client.ashost", "192.168.95.20"); // 系统的IP地址
        properties.put("jco.client.client", "310"); // 要登录的客户端
        properties.put("jco.client.sysnr", "00"); // 系统编号
        properties.put("jco.client.langu", "ZH"); // 系统语言
        properties.put("jco.client.user", "qilu-abap03"); // 登录用户名
        properties.put("jco.client.passwd", "lvdx2019"); // 用户登录口令
        return properties;
    }

    private void reminder(String company, String users, String workflowId) {
        reminder(company, users.split(","), workflowId);
    }

    private void reminder(String company, String[] users, String workflowId) {
        JCO.Client myConnection = JCO.createClient(this.getLogonProperties());
        myConnection.connect();
        JCO.Repository myRepository = new JCO.Repository("Repository", myConnection);
        String strFunc = "ZFM_QM_JXQ01";
        IFunctionTemplate ft = myRepository.getFunctionTemplate(strFunc.toUpperCase());
        JCO.Function funGetList = ft.getFunction();
//        JCO.ParameterList input = funGetList.getImportParameterList();
//        input.setValue("", "I_DATE");
//        input.setValue("", "I_DAYS");
        myConnection.execute(funGetList);

        JCO.Table T_JXQ = funGetList.getTableParameterList().getTable("T_JXQ");

        for (int i = 0; i < T_JXQ.getNumRows(); i++) {
            T_JXQ.setRow(i);
            String MATNR = T_JXQ.getString("MATNR");//编号
            String ZTYMC = T_JXQ.getString("ZTYMC");//产品名称
            String CHARG = T_JXQ.getString("CHARG");//批号
            String VFDAT = T_JXQ.getString("VFDAT");//日期

            if (!ZTYMC.isEmpty() && !VFDAT.isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(simpleDateFormat.parse(VFDAT));
                    calendar.add(Calendar.DATE, -180);
                    Calendar today = Calendar.getInstance();
                    if (calendar.equals(today)) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("ypbh", MATNR);
                        map.put("tymc", ZTYMC);
                        map.put("ph", CHARG);
                        map.put("yxqz", VFDAT);
                        for (String user : users) {
                            try {
                                Workflow workflow = Workflow.createInstance(user, workflowId, ZTYMC + "-到期提醒", map);
                                workflow.next();
                            } catch (Exception e) {
                                logger(T_JXQ.getString("MATNR") + "创建提醒流程失败");
                            }
                        }
                    }
                } catch (ParseException e) {
                    logger(T_JXQ.getString("MATNR") + "日期格式错误");
                }
            } else {
                String log = T_JXQ.getString("MATNR") + ";" +
                        T_JXQ.getString("ZTYMC") + ";" +
                        T_JXQ.getString("ZSPBM") + ";" +
                        T_JXQ.getString("ZCPJX") + ";" +
                        T_JXQ.getString("ZMEINS") + ";" +
                        T_JXQ.getString("ZGUIG") + ";" +
                        T_JXQ.getString("ZSCQY") + ";" +
                        T_JXQ.getString("ZGHDW") + ";" +
                        T_JXQ.getString("CHARG") + ";" +
                        T_JXQ.getString("VFDAT") + ";" +
                        T_JXQ.getString("CLABS") + ";";
                logger(log);
            }
        }
    }

    private void logger(Object o) {
        this.baseBean.writeLog(o);
    }
}
