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
public class StaffRecords extends BaseBean implements Action {
    private String company;
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
        String YXOAYWRY_BGRQ = JnUtils.getDate("");
        String YXOAYWRY_BGSJ = JnUtils.getDate("datetime");
        String YXOAYWRY_XSGS = this.getCompany();
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            String YXOAYWRY_RYBH = Util.null2String(recordSet.getString("ygbh"));//员工编号
            String YXOAYWRY_RYXM = Util.null2String(recordSet.getString("xm"));//姓名
            String YXOAYWRY_SYBZ = Util.null2String(recordSet.getString("ygzt"));//员工状态
            String YXOAYWRY_CSRQ = Util.null2String(recordSet.getString("csrq"));//出生日期
            String YXOAYWRY_XB = Util.null2String(recordSet.getString("xb")).equals("0") ? "M" : "F";//性别
            String YXOAYWRY_BMBH = Util.null2String(recordSet.getString("ssbm"));//所属部门
            String YXOAYWRY_SFZH = Util.null2String(recordSet.getString("sfzh"));//身份证号
            String YXOAYWRY_KSRQ = Util.null2String(recordSet.getString("ldhtqs"));//劳动合同起始日期
            String YXOAYWRY_JSRQ = Util.null2String(recordSet.getString("ldhtzz"));//劳动合同终止日期
            String YXOAYWRY_ZW = Util.null2String(recordSet.getString("zw"));//职务
            String YXOAYWRY_XL = Util.null2String(recordSet.getString("xl"));//学历
            String YXOAYWRY_BYYX = Util.null2String(recordSet.getString("byyx"));//毕业学校
            String YXOAYWRY_BZ = Util.null2String(recordSet.getString("bz"));//备注
            String YXOAYWRY_GW = Util.null2String(recordSet.getString("gw"));//岗位
            String YXOAYWRY_SFJCYP = Util.null2String(recordSet.getString("sfzjjcyp")).equals("0") ? "1" : "0";//是否直接接触药品
            String YXOAYWRY_ZC = Util.null2String(recordSet.getString("zc"));//职称
            String YXOAYWRY_ZY = Util.null2String(recordSet.getString("zy"));//专业
            String YXOAYWRY_BYSJ = Util.null2String(recordSet.getString("bysj"));//毕业时间
            String YXOAYWRY_ZGZSH = Util.null2String(recordSet.getString("zgzsh"));//资格证书号
            String YXOAYWRY_ZYZG = Util.null2String(recordSet.getString("zyzg"));//职业资格
            String YXOAYWRY_FZJG = Util.null2String(recordSet.getString("fzdw"));//发证单位
            String YXOAYWRY_SGSJ = Util.null2String(recordSet.getString("sgsj")).replace("-", "");//上岗时间

            sql = "select * from YXOAYWRY where YXOAYWRY_RYBH='" + YXOAYWRY_RYBH + "'";
            rsds.executeSql(sql);
            if (rsds.next()) {
                sql = "update YXOAYWRY set " +
                        "YXOAYWRY_BGRQ='" + YXOAYWRY_BGRQ + "', " +
                        "YXOAYWRY_BGSJ='" + YXOAYWRY_BGSJ + "', " +
                        "YXOAYWRY_XSGS='" + YXOAYWRY_XSGS + "', " +
                        "YXOAYWRY_RYXM='" + YXOAYWRY_RYXM + "', " +
                        "YXOAYWRY_SYBZ='" + YXOAYWRY_SYBZ + "', " +
                        "YXOAYWRY_CSRQ='" + YXOAYWRY_CSRQ + "', " +
                        "YXOAYWRY_XB='" + YXOAYWRY_XB + "', " +
                        "YXOAYWRY_BMBH='" + YXOAYWRY_BMBH + "', " +
                        "YXOAYWRY_SFZH='" + YXOAYWRY_SFZH + "', " +
                        "YXOAYWRY_KSRQ='" + YXOAYWRY_KSRQ + "', " +
                        "YXOAYWRY_JSRQ='" + YXOAYWRY_JSRQ + "', " +
                        "YXOAYWRY_ZW='" + YXOAYWRY_ZW + "', " +
                        "YXOAYWRY_XL='" + YXOAYWRY_XL + "', " +
                        "YXOAYWRY_BYYX='" + YXOAYWRY_BYYX + "', " +
                        "YXOAYWRY_BZ='" + YXOAYWRY_BZ + "', " +
                        "YXOAYWRY_GW='" + YXOAYWRY_GW + "', " +
                        "YXOAYWRY_SFJCYP='" + YXOAYWRY_SFJCYP + "', " +
                        "YXOAYWRY_ZC='" + YXOAYWRY_ZC + "', " +
                        "YXOAYWRY_ZY='" + YXOAYWRY_ZY + "', " +
                        "YXOAYWRY_BYSJ='" + YXOAYWRY_BYSJ + "', " +
                        "YXOAYWRY_ZGZSH='" + YXOAYWRY_ZGZSH + "', " +
                        "YXOAYWRY_ZYZG='" + YXOAYWRY_ZYZG + "', " +
                        "YXOAYWRY_FZJG='" + YXOAYWRY_FZJG + "', " +
                        "YXOAYWRY_SGSJ='" + YXOAYWRY_SGSJ + "' where  YXOAYWRY_RYBH='" + YXOAYWRY_RYBH + "'";
            } else {
                sql = "insert into YXOAYWRY( " +
                        "YXOAYWRY_BGRQ, " +
                        "YXOAYWRY_BGSJ, " +
                        "YXOAYWRY_XSGS, " +
                        "YXOAYWRY_RYXM, " +
                        "YXOAYWRY_SYBZ, " +
                        "YXOAYWRY_CSRQ, " +
                        "YXOAYWRY_XB, " +
                        "YXOAYWRY_BMBH, " +
                        "YXOAYWRY_SFZH, " +
                        "YXOAYWRY_KSRQ, " +
                        "YXOAYWRY_JSRQ, " +
                        "YXOAYWRY_ZW, " +
                        "YXOAYWRY_XL, " +
                        "YXOAYWRY_BYYX, " +
                        "YXOAYWRY_BZ, " +
                        "YXOAYWRY_GW, " +
                        "YXOAYWRY_SFJCYP, " +
                        "YXOAYWRY_ZC, " +
                        "YXOAYWRY_ZY, " +
                        "YXOAYWRY_BYSJ, " +
                        "YXOAYWRY_ZGZSH, " +
                        "YXOAYWRY_ZYZG, " +
                        "YXOAYWRY_FZJG, " +
                        "YXOAYWRY_SGSJ, " +
                        "YXOAYWRY_RYBH) values( " +
                        "'" + YXOAYWRY_BGRQ + "', " +
                        "'" + YXOAYWRY_BGSJ + "', " +
                        "'" + YXOAYWRY_XSGS + "', " +
                        "'" + YXOAYWRY_RYXM + "', " +
                        "'" + YXOAYWRY_SYBZ + "', " +
                        "'" + YXOAYWRY_CSRQ + "', " +
                        "'" + YXOAYWRY_XB + "', " +
                        "'" + YXOAYWRY_BMBH + "', " +
                        "'" + YXOAYWRY_SFZH + "', " +
                        "'" + YXOAYWRY_KSRQ + "', " +
                        "'" + YXOAYWRY_JSRQ + "', " +
                        "'" + YXOAYWRY_ZW + "', " +
                        "'" + YXOAYWRY_XL + "', " +
                        "'" + YXOAYWRY_BYYX + "', " +
                        "'" + YXOAYWRY_BZ + "', " +
                        "'" + YXOAYWRY_GW + "', " +
                        "'" + YXOAYWRY_SFJCYP + "', " +
                        "'" + YXOAYWRY_ZC + "', " +
                        "'" + YXOAYWRY_ZY + "', " +
                        "'" + YXOAYWRY_BYSJ + "', " +
                        "'" + YXOAYWRY_ZGZSH + "', " +
                        "'" + YXOAYWRY_ZYZG + "', " +
                        "'" + YXOAYWRY_FZJG + "', " +
                        "'" + YXOAYWRY_SGSJ + "', " +
                        "'" + YXOAYWRY_RYBH + "')";

            }
            rsds.executeSql(sql);
        }

        return SUCCESS;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
