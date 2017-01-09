package weaverjn.utils;

import weaver.conn.RecordSet;
import weaver.general.Util;

import java.net.URLEncoder;
import java.util.Calendar;

/**
 * Created by dzyq on 2016/6/23.
 */
public class tableStringUtils {
    public static String getCarNumber(String id) {
        RecordSet rs = new RecordSet();
        String sql = "select clhm from uf_cljbxx where id=" + id;
        String carNumber = id;
        rs.executeSql(sql);
        if (rs.next()) {
            carNumber = Util.null2String(rs.getString("clhm"));
        }
        return carNumber;
    }

    public static String getWXFY(String id) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        RecordSet rs = new RecordSet();
        String SQL = "select wxfy from uf_wxfygl where clhm=" + id + " and wxrq like '" + year + "%'";
        rs.executeSql(SQL);
        Double wxfy = 0.0;//本年维修费用
        while(rs.next()){
            wxfy += rs.getDouble("wxfy");
        }
        String tag = "<span id='WXFY_" + id + "span'>" + wxfy + "</span>" + "<input type='hidden' id='WXFY_" + id + "' value='" + wxfy + "'></input>";
        return tag;
    }

    public static String getBXFY(String id) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        RecordSet rs = new RecordSet();
        String SQL = "select tbje from uf_bxgl where clhm=" + id + " and tbrq like '" + year + "%'";
        rs.executeSql(SQL);
        Double tbje = 0.0;//本年保险费用
        while(rs.next()){
            tbje += rs.getDouble("tbje");
        }
        String tag = "<span id='BXFY_" + id + "span'>" + tbje + "</span>" + "<input type='hidden' id='BXFY_" + id + "' value='" + tbje + "'></input>";
        return tag;
    }

    public static String getYF(String id) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        RecordSet rs = new RecordSet();
        String SQL = "select je from uf_yfgl where clhm=" + id + " and jyrq like '" + year + "%'";
        rs.executeSql(SQL);
        Double yfje = 0.0;//本年油费
        while(rs.next()){
            yfje += rs.getDouble("je");
        }
        String tag = "<span id='YF_" + id + "span'>" + yfje + "</span>" + "<input type='hidden' id='YF_" + id + "' value='" + yfje + "'></input>";
        return tag;
    }

    public static String getGLF(String id) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        RecordSet rs = new RecordSet();
        String SQL = "select tcglfje from uf_glgqfygl where clhm=" + id + " and rq like '" + year + "%'";
        rs.executeSql(SQL);
        Double tcglfje = 0.0;//本年过路过桥费用
        while(rs.next()){
            tcglfje += rs.getDouble("tcglfje");
        }
        String tag = "<span id='GLF_" + id + "span'>" + tcglfje + "</span>" + "<input type='hidden' id='GLF_" + id + "' value='" + tcglfje + "'></input>";
        return tag;
    }

    public static String getLC(String id) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        RecordSet rs = new RecordSet();
        String SQL = "select xslc from uf_clsyjl where clhm=" + id + " and cfrq like '" + year + "%'";
        rs.executeSql(SQL);
        Double xslc = 0.0;//本年里程
        while(rs.next()){
            xslc += rs.getDouble("xslc");
        }
        String tag = "<span id='LC_" + id + "span'>" + xslc + "</span>" + "<input type='hidden' id='LC_" + id + "' value='" + xslc + "'></input>";
        return tag;
    }

    public static String getZJtag(String id) {
        String tag = "<input id='ZJ_" + id + "' style='width:80px;'></input>";
        return tag;
    }

    public static String getGZtag(String id) {
        String tag = "<input id='GZ_" + id + "' style='width:80px;'></input>";
        return tag;
    }

    public static String getResult(String id) {
        String tag = "<span id='JG_" + id + "span'></span>" + "<input type='hidden' id='JG_" + id + "' style='width:80px;'></input>";
        return tag;
    }

    public static String getDepartmentName(String id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select departmentname from hrmdepartment where id=" + id;
        recordSet.executeSql(sql);
        String departmentName = "";
        if (recordSet.next()) {
            departmentName = Util.null2String(recordSet.getString("departmentname"));
        }
        String labelA = "<a href=\"javascript:openFullWindowForXtable('/hrm/HrmTab.jsp?_fromURL=HrmDepartmentDsp&id=" + id + "&hasTree=false')\">" + departmentName + "</a>";
        return labelA;
    }

    public static String getDepartmentNameByUserID(String id) {
        RecordSet recordSet = new RecordSet();
        String sql = "select departmentname from hrmdepartment where id=(select departmentid from hrmresource where id=" + id + ")";
        recordSet.executeSql(sql);
        String departmentName = "";
        if (recordSet.next()) {
            departmentName = Util.null2String(recordSet.getString("departmentname"));
        }
        String labelA = "<a href=\"javascript:openFullWindowForXtable('/hrm/HrmTab.jsp?_fromURL=HrmDepartmentDsp&id=" + id + "&hasTree=false')\">" + departmentName + "</a>";
        return labelA;
    }

    public static String getURL(String requestid, String userid) {
        RecordSet recordSet = new RecordSet();
        String sql = "select * from all_todolist where requestid=" + requestid + " and userid=" + userid;
        recordSet.executeSql(sql);
        recordSet.next();
        String ao = recordSet.getString("anotheroa");
        String requestname = recordSet.getString("requestname");
        String tourl = "";
        if (ao.equals("0")) {
            tourl = "/workflow/request/ViewRequest.jsp?requestid=" + requestid + "&isovertime=0";
        } else {
            tourl = "http://192.168.1.20/weaverjn/qiluzhiyao/sso/ssobyuserid.jsp?tourl=" + URLEncoder.encode("http://192.168.1.20/workflow/request/ViewRequest.jsp?requestid=" + requestid + "&isovertime=0") + "&userid=" + userid;
        }
        return "<a href='" + tourl + "' target='_blank'>" + requestname + "</a>";
    }
}
