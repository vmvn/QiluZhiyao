package weaverjn.crm.main;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by zhaiyaqi on 2017/4/21.
 */
public class GouhuoDanweiShouhuoren extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        String billId = requestInfo.getRequestid();
        String modId = requestInfo.getWorkflowid();
        String sql = "select b.tablename,b.id from modeinfo a,workflow_bill b where a.formid = b.id and a.id = " + modId;
        RecordSet recordSet = new RecordSet();
        RecordSetDataSource rsds = new RecordSetDataSource("crm_db");
        recordSet.executeSql(sql);
        recordSet.next();
        String tableName = recordSet.getString("tablename");
        sql = "select * from " + tableName + " where id=" + billId;
        String YXOASHR_BGRQ = JnUtils.getDate("");
        String YXOASHR_BGSJ = JnUtils.getDate("datetime");
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            String YXOASHR_KHBH = Util.null2String(recordSet.getString("ghdwbh"));//购货单位编号
            String YXOASHR_KHMC = Util.null2String(recordSet.getString("ghdwmc"));//购货单位名称
            String YXOASHR_SHRBH = Util.null2String(recordSet.getString("shrbh"));//收货人编号
            String YXOASHR_SHRMC = Util.null2String(recordSet.getString("shrxm"));//收货人姓名
            String YXOASHR_KSRQ = Util.null2String(recordSet.getString("sqqrq")).replace("-", "");//授权起日期
            String YXOASHR_JSRQ = Util.null2String(recordSet.getString("sqzrq")).replace("-", "");//授权止日期
            String YXOASHR_SFZH = Util.null2String(recordSet.getString("shrsfz"));//收货人身份证号
            String YXOASHR_SFZXQ = Util.null2String(recordSet.getString("sfzyxq")).replace("-", "");//身份证有效期
            String YXOASHR_SFZFYJ = Util.null2String(recordSet.getString("ywsfzfyj")).equals("0") ? "1" : "0";//有无身份证复印件
            String YXOASHR_SFZFYJQZ = Util.null2String(recordSet.getString("sfzfyj")).equals("0") ? "1" : "0";//身份证复印件有无公章
            String YXOASHR_FDSQR = Util.null2String(recordSet.getString("fdsqr"));//法定授权人
            String YXOASHR_FRQZ = Util.null2String(recordSet.getString("frqz")).equals("0") ? "1" : "0";//法人签章
            String YXOASHR_DWQZ = Util.null2String(recordSet.getString("ywghdw")).equals("0") ? "1" : "0";//有无购货单位公章
            String YXOASHR_HWJSHYJ = Util.null2String(recordSet.getString("hwjsh")).equals("0") ? "1" : "0";//货物接收函是否原件
            String YXOASHR_SQQY = Util.null2String(recordSet.getString("sqqy")).equals("0") ? "1" : "0";//授权区域
            String YXOASHR_SHFW = getSApMultiValue(tableName + "_dt2", "shfw", "mainid", billId);//收货范围

            sql = "select * from YXOASHR where YXOASHR_SHRBH='" + YXOASHR_SHRBH + "'";
            rsds.executeSql(sql);
            if (rsds.next()) {
                sql = "update YXOASHR set " +
                        "YXOASHR_BGRQ='" + YXOASHR_BGRQ + "', " +
                        "YXOASHR_BGSJ='" + YXOASHR_BGSJ + "', " +
                        "YXOASHR_KHBH='" + YXOASHR_KHBH + "', " +
                        "YXOASHR_KHMC='" + YXOASHR_KHMC + "', " +
                        "YXOASHR_SHRMC='" + YXOASHR_SHRMC + "', " +
                        "YXOASHR_KSRQ='" + YXOASHR_KSRQ + "', " +
                        "YXOASHR_JSRQ='" + YXOASHR_JSRQ + "', " +
                        "YXOASHR_SFZH='" + YXOASHR_SFZH + "', " +
                        "YXOASHR_SFZXQ='" + YXOASHR_SFZXQ + "', " +
                        "YXOASHR_SFZFYJ='" + YXOASHR_SFZFYJ + "', " +
                        "YXOASHR_SFZFYJQZ='" + YXOASHR_SFZFYJQZ + "', " +
                        "YXOASHR_FDSQR='" + YXOASHR_FDSQR + "', " +
                        "YXOASHR_FRQZ='" + YXOASHR_FRQZ + "', " +
                        "YXOASHR_DWQZ='" + YXOASHR_DWQZ + "', " +
                        "YXOASHR_HWJSHYJ='" + YXOASHR_HWJSHYJ + "', " +
                        "YXOASHR_SQQY='" + YXOASHR_SQQY + "', " +
                        "YXOASHR_SHFW='" + YXOASHR_SHFW + "' where  YXOASHR_SHRBH='" + YXOASHR_SHRBH + "'";
            } else {
                sql = "insert into YXOASHR(" +
                        "YXOASHR_BGRQ, " +
                        "YXOASHR_BGSJ, " +
                        "YXOASHR_KHBH, " +
                        "YXOASHR_KHMC, " +
                        "YXOASHR_SHRMC, " +
                        "YXOASHR_KSRQ, " +
                        "YXOASHR_JSRQ, " +
                        "YXOASHR_SFZH, " +
                        "YXOASHR_SFZXQ, " +
                        "YXOASHR_SFZFYJ, " +
                        "YXOASHR_SFZFYJQZ, " +
                        "YXOASHR_FDSQR, " +
                        "YXOASHR_FRQZ, " +
                        "YXOASHR_DWQZ, " +
                        "YXOASHR_HWJSHYJ, " +
                        "YXOASHR_SQQY, " +
                        "YXOASHR_SHRBH, " +
                        "YXOASHR_SHFW) values(" +
                        "'" + YXOASHR_BGRQ + "', " +
                        "'" + YXOASHR_BGSJ + "', " +
                        "'" + YXOASHR_KHBH + "', " +
                        "'" + YXOASHR_KHMC + "', " +
                        "'" + YXOASHR_SHRMC + "', " +
                        "'" + YXOASHR_KSRQ + "', " +
                        "'" + YXOASHR_JSRQ + "', " +
                        "'" + YXOASHR_SFZH + "', " +
                        "'" + YXOASHR_SFZXQ + "', " +
                        "'" + YXOASHR_SFZFYJ + "', " +
                        "'" + YXOASHR_SFZFYJQZ + "', " +
                        "'" + YXOASHR_FDSQR + "', " +
                        "'" + YXOASHR_FRQZ + "', " +
                        "'" + YXOASHR_DWQZ + "', " +
                        "'" + YXOASHR_HWJSHYJ + "', " +
                        "'" + YXOASHR_SQQY + "', " +
                        "'" + YXOASHR_SHRBH + "'," +
                        "'" + YXOASHR_SHFW + "')";
            }
            if (rsds.executeSql(sql)) {
                syncDetailTable(billId, tableName + "_dt1", YXOASHR_SHRBH);
            } else {
                writeLog(sql);
            }
        }
        return null;
    }

    private void syncDetailTable(String mainId, String table, String YXOASHR_SHRBH) {
        RecordSet recordSet = new RecordSet();
        RecordSetDataSource rsds = new RecordSetDataSource("crm_db");
        String sql = "delete from YXOASHRPZ where YXOASHRPZ_SHRBH='" + YXOASHR_SHRBH + "'";
        if (rsds.executeSql(sql)) {
            sql = "select * from " + table + " where mainid=" + mainId;
            recordSet.executeSql(sql);
            while (recordSet.next()) {
                String YXOASHRPZ_BGRQ = JnUtils.getDate("");
                String YXOASHRPZ_BGSJ = JnUtils.getDate("datetime");
                String YXOASHRPZ_WLBH = Util.null2String(recordSet.getString("pzbh"));
                String YXOASHRPZ_TYM = Util.null2String(recordSet.getString("tymc"));
                String YXOASHRPZ_SPM = Util.null2String(recordSet.getString("spm"));
                String YXOASHRPZ_GG = Util.null2String(recordSet.getString("gg"));
                String YXOASHRPZ_BZGG = Util.null2String(recordSet.getString("bzgg"));
                String YXOASHRPZ_XDGG = Util.null2String(recordSet.getString("sfxdgg")).equals("0") ? "1" : "0";

                sql = "insert into YXOASHRPZ(" +
                        "YXOASHRPZ_BGRQ, " +
                        "YXOASHRPZ_BGSJ, " +
                        "YXOASHRPZ_SHRBH, " +
                        "YXOASHRPZ_WLBH, " +
                        "YXOASHRPZ_TYM, " +
                        "YXOASHRPZ_SPM, " +
                        "YXOASHRPZ_GG, " +
                        "YXOASHRPZ_BZGG, " +
                        "YXOASHRPZ_XDGG) values(" +
                        "'" + YXOASHRPZ_BGRQ + "', " +
                        "'" + YXOASHRPZ_BGSJ + "', " +
                        "'" + YXOASHR_SHRBH + "', " +
                        "'" + YXOASHRPZ_WLBH + "', " +
                        "'" + YXOASHRPZ_TYM + "', " +
                        "'" + YXOASHRPZ_SPM + "', " +
                        "'" + YXOASHRPZ_GG + "', " +
                        "'" + YXOASHRPZ_BZGG + "', " +
                        "'" + YXOASHRPZ_XDGG + "')";
                if (!rsds.executeSql(sql)) {
                    writeLog(sql);
                }
            }
        }
    }

    private String getMultiFieldName(String table, String field, String cField, String cValue) {
        String sql = "select " + field + " from " + table + " where " + cField + "='" + cValue + "'";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        StringBuilder s = new StringBuilder();
        while (recordSet.next()) {
            if (s.length() > 0) {
                s.append(",");
            }
            s.append(Util.null2String(recordSet.getString(field)));
        }
        return s.toString();
    }

    private String getSApMultiValue(String table, String field, String cField, String cValue) {
        String sql = "select " + field + " from " + table + " where " + cField + "='" + cValue + "'";
        RecordSet recordSet = new RecordSet();
        recordSet.executeSql(sql);
        StringBuilder s = new StringBuilder();
        while (recordSet.next()) {
            if (s.length() > 0) {
                s.append(",");
            }
            s.append(recordSet.getString(field));
        }
        return s.toString();
    }
}
