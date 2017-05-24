package weaverjn.schedule;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;
import weaver.interfaces.schedule.BaseCronJob;
import weaverjn.action.integration.utils;
import weaverjn.utils.Workflow;
import weaverjn.utils.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhaiyaqi on 2017/4/6.
 */
public class DrugExpirationReminder extends BaseCronJob {

    public static void main(String[] args) {
        new DrugExpirationReminder().execute();
    }

    public void execute() {
        reminder("1620","3110");//万润
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

    private void reminder(String company, String workflowId) {
        JCO.Client myConnection = JCO.createClient(this.getLogonProperties());
        myConnection.connect();
        JCO.Repository myRepository = new JCO.Repository("Repository", myConnection);
        String strFunc = "ZFM_QM_JXQ01";
        IFunctionTemplate ft = myRepository.getFunctionTemplate(strFunc.toUpperCase());
        JCO.Function funGetList = ft.getFunction();
        JCO.ParameterList input = funGetList.getImportParameterList();
        input.setValue(company, "I_WERKS");
        input.setValue(utils.getCurrentDate(), "I_DATE");
        input.setValue("180", "I_DAYS");
        myConnection.execute(funGetList);

        JCO.Table T_JXQ = funGetList.getTableParameterList().getTable("T_JXQ");

        System.out.println(T_JXQ.getNumRows());
        /*for (int i = 0; i < T_JXQ.getNumRows(); i++) {
            T_JXQ.setRow(i);
            String MATNR = T_JXQ.getString("MATNR");//编号
            String ZTYMC = T_JXQ.getString("ZTYMC");//产品名称
            String CHARG = T_JXQ.getString("CHARG");//批号
            String VFDAT = T_JXQ.getString("VFDAT");//日期

            if (!ZTYMC.isEmpty() && !VFDAT.isEmpty()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("ypbh", MATNR);
                map.put("tymc", ZTYMC);
                map.put("ph", CHARG);
                map.put("yxqz", VFDAT);
                try {
                    Workflow workflow = Workflow.createInstance("1", workflowId, ZTYMC + "-到期提醒", map);
                    workflow.next();
                } catch (Exception e) {
                    logger(T_JXQ.getString("MATNR") + "创建提醒流程失败");
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
        }*/
    }

    /*private boolean compare(Calendar today, Calendar c1, int n) {
        Calendar c2 = Calendar.getInstance();
        c2.setTime(c1.getTime());
        c2.add(Calendar.DATE, 0 - n);
        return (today.equals(c2) || today.after(c2)) && (today.equals(c1) || today.before(c1));
    }*/
    private void logger(Object o) {
        util.writeLog(this.getClass().getName(), o);
    }
}
