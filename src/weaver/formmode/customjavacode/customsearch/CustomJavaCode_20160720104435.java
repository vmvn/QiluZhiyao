package weaver.formmode.customjavacode.customsearch;

import java.text.SimpleDateFormat;
import java.util.*;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.formmode.customjavacode.AbstractCustomSqlConditionJavaCode;

public class CustomJavaCode_20160720104435 extends AbstractCustomSqlConditionJavaCode {

    /**
     * 生成SQL查询限制条件
     * @param param
     *  param包含(但不限于)以下数据
     *  user 当前用户
     *
     * @return
     *  返回的查询限制条件的格式举例为: t1.a = '1' and t1.b = '3' and t1.c like '%22%'
     *  其中t1为表单主表表名的别名
     */
    public String generateSqlCondition(Map<String, Object> param) throws Exception {
        User user = (User)param.get("user");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, 3);
        String date = simpleDateFormat.format(calendar.getTime());

        String sqlCondition = "(t1.qysj<='" + date + "')";

        return sqlCondition;
    }

}

