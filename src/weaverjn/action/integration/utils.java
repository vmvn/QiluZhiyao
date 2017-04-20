package weaverjn.action.integration;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.soa.workflow.request.MainTableInfo;
import weaver.soa.workflow.request.Property;
import weaverjn.utils.PropertiesUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaiyaqi on 2017/2/14.
 */
public class utils extends BaseBean{
    public static String[] slice(String s, int range, int n) {
        String[] arr = new String[n];
        int len = s.length();
        int a = len / range;
        int b = len % range;
        int max = a > n ? n : a;
        for (int i = 0; i < max; i++) {
            arr[i] = s.substring(i * range, (i + 1) * range);
        }
        if (max < n) {
            arr[max] = s.substring(len - b, len);
            for (int i = max + 1; i < n; i++) {
                arr[i] = "";
            }
        }
        return arr;
    }

    public static String getTableName(String workflowid) {
        RecordSet recordSet = new RecordSet();
        String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;

        recordSet.executeSql(sql);
        recordSet.next();
        return Util.null2String(recordSet.getString("tablename"));
    }

    public static String getFieldValue(String table, String field, String conditionValue) {
        return getFieldValue(table, field, "id", conditionValue);
    }

    public static String getFieldValue(String table, String field, String conditionField, String conditionValue) {
        RecordSet recordSet = new RecordSet();
        String sql = "select " + field + " from " + table + " where " + conditionField + "='" + conditionValue + "'";
        recordSet.executeSql(sql);
        String value = "";
        if (recordSet.next()) {
            value = recordSet.getString(field);
        }
        return value;
    }

    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }


    public static String slice(String s, int n) {
        return s.length() > n ? s.substring(0, n) : s;
    }

    public static String getUsername() {
        String username = new PropertiesUtil().getPropValue("qiluSAPauth", "username");
        if (username.isEmpty()) {
            username = "zappluser_oa";
        }
        return username;
    }

    public static String getPassword() {
        String password = new PropertiesUtil().getPropValue("qiluSAPauth", "password");
        if (password.isEmpty()) {
            password = "a1234567";
        }
        return password;
    }

    public static Map<String, String> getMainTableData(MainTableInfo mainTableInfo) {
        Map<String, String> mainTableData = new HashMap<String, String>();
        Property[] properties = mainTableInfo.getProperty();
        for (Property property : properties) {
            String name = property.getName();
            String value = Util.null2String(property.getValue());
            mainTableData.put(name, value);
        }
        return mainTableData;
    }

    public static String getSelectName(String fieldid, String selectvalue) {
        String sql = "select selectname from workflow_selectitem where fieldid='" + fieldid + "' and selectvalue='" + selectvalue + "'";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        String selectname = "";
        if (recordSet.next()) {
            selectname = recordSet.getString("selectname");
        }
        return selectname;
    }

    public static void main(String[] args) {
        String s = "123456789qwerasd";
        String[] arr = slice(s, 5, 4);
        for (String i : arr) {
            System.out.println(i);
        }
    }
}
