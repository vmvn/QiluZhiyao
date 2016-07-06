package weaverjn.schedule;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetTrans;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;

/**
 * Created by dzyq on 2016/7/5.
 */
public class PrjShareShare extends BaseCronJob {
    public void execute() {
        System.out.println("---->PrjShareShare");
        RecordSet rs = new RecordSet();
        RecordSet rs2 = new RecordSet();
        RecordSet rs3 = new RecordSet();
        String sql = "select userid, relateditemid from Prj_T_ShareInfo";
        rs.executeSql(sql);
        while (rs.next()) {
            String userId = rs.getString("userid");
            String relateditemId = rs.getString("relateditemid");
            sql = "select id from Prj_ProjectInfo where PRJTYPE=" + relateditemId;
            rs2.executeSql(sql);
            while (rs2.next()) {
                String id = rs2.getString("id");
                sql = "select * from Prj_ShareInfo where relateditemid=" + id + " and userid=" + userId + " and sharelevel=1";
                rs3.executeSql(sql);
                if (rs3.next()) {
                    System.out.println("---->" + userId + ":" + id + ":look");
                } else {
                    String ProcPara = "";
                    char flag = 2;
                    ProcPara = id;
                    ProcPara += flag+"1";
                    ProcPara += flag+"0";
                    ProcPara += flag+"0";
                    ProcPara += flag+"1";
                    ProcPara += flag+""+userId+"";
                    ProcPara += flag+"0";
                    ProcPara += flag+"0";
                    ProcPara += flag+"0";
                    ProcPara += flag+"0";

                    RecordSetTrans rst = new RecordSetTrans();
                    rst.setAutoCommit(false);
                    try{
                        rst.executeProc("Prj_ShareInfo_Insert", ProcPara);
                        rst.executeSql("select max(id) from Prj_ShareInfo ");
                        rst.next();
                        int newid= Util.getIntValue(rst.getString(1), 0);
                        if(newid>0){
                            rst.executeSql("update Prj_ShareInfo set seclevelMax='" + "100" + "' where id=" + newid);
                        }
                        rst.commit();
                        System.out.println("---->" + userId + ":" + id + ":added look");
                    }catch(Exception e){
                        rst.rollback();
                    }
                }
            }
        }
    }
}
