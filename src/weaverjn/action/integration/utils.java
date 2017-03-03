package weaverjn.action.integration;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

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

    public static void main(String[] args) {
        String s = "123456789qwerasd";
        String[] arr = slice(s, 5, 4);
        for (String i : arr) {
            System.out.println(i);
        }
    }
}
