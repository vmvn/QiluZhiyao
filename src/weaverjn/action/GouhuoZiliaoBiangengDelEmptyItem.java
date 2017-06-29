package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhaiyaqi on 2017/6/3.
 */
public class GouhuoZiliaoBiangengDelEmptyItem extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        int billid = requestManager.getBillid();
        String tableName = requestManager.getBillTableName();
        String dt1 = tableName + "_dt1";
        String sql = "delete from " + dt1 + " where (bghnr='' or bghnr is null) and mainid=" + billid;
        writeLog(sql);
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);

        sql = "select bgqnr,bghnr from " + dt1 + " where (bghnr='' or bghnr is null) and mainid=" + billid;
        recordSet.executeSql(sql);
        StringBuilder msg = new StringBuilder();
        while (recordSet.next()) {
            String bgqnr = Util.null2String(recordSet.getString("bgqnr"));
            String bghnr = Util.null2String(recordSet.getString("bghnr"));
            if (isRightDateFormat(bgqnr) && !isRightDateFormat(bghnr)) {
                msg.append("变更后内容：\"").append(bghnr).append("\"，日期格式不正确！示例：").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("。");
            }
        }
        if (msg.length() > 0) {
            requestManager.setMessageid("WARNING");
            requestManager.setMessagecontent(msg.toString());
        }
        return "1";
    }

    private boolean isRightDateFormat(String date) {
        String eL = "\\d{4}-\\d{2}-\\d{2}";
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(date);
        return m.matches();
    }
}
