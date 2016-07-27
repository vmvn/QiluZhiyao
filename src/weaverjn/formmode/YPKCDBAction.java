package weaverjn.formmode;

import weaver.conn.RecordSet;
import weaver.formmode.interfaces.action.BaseAction;
import weaver.formmode.setup.ModeRightInfo;
import weaver.soa.workflow.request.RequestInfo;

/**
 * Created by dzyq on 2016/7/26 11:40.
 */
public class YPKCDBAction extends BaseAction {
    public String execute(RequestInfo requestInfo) {
        String billId = requestInfo.getRequestid();
        RecordSet recordSet = new RecordSet();
        String sql = "select * from uf_kcdb where id=" + billId;
        recordSet.executeSql(sql);
        if (recordSet.next()) {
            String ypmckcId = recordSet.getString("ypmc");
//            String ssgs = recordSet.getString("ssgs");
            String sbgs = recordSet.getString("sbgs");
            int dbsl = recordSet.getInt("dbsl");

            sql = "select * from uf_ypkcgl where id=" + ypmckcId;
            RecordSet recordSet1 = new RecordSet();
            recordSet1.executeSql(sql);
            recordSet1.next();
            String ypmcId = recordSet1.getString("ypmc");
            String pc = recordSet1.getString("pc");
//            String ssgskc = recordSet1.getString("ssgs");
            int kcl = recordSet1.getInt("kcl");
            int sysl = recordSet1.getInt("sysl");

            sql = "select * from uf_ypkcgl where ypmc=" + ypmcId + " and pc='" + pc + "' and ssgs=" + sbgs;
            recordSet1.executeSql(sql);
            if (recordSet1.next()) {
                String id = recordSet1.getString("id");
                sql = "update uf_ypkcgl set sysl=sysl+" + dbsl + ", kcl=kcl+" + dbsl + " where id=" + id;
                RecordSet recordSet2 = new RecordSet();
                recordSet2.executeSql(sql);
            } else {
                sql = "insert into uf_ypkcgl(REQUESTID, YPMC, JJ, XSJ, YPGG, FORMMODEID, MODEDATACREATER, MODEDATACREATERTYPE, MODEDATACREATEDATE, MODEDATACREATETIME, PC, SSGS, RKRQ, KCL, LYSL, SYSL, YPMCWB, DW)" +
                        " select REQUESTID, YPMC, JJ, XSJ, YPGG, FORMMODEID, MODEDATACREATER, MODEDATACREATERTYPE, MODEDATACREATEDATE, MODEDATACREATETIME, PC, SSGS, RKRQ, KCL, LYSL, SYSL, YPMCWB, DW from uf_ypkcgl where id=" + ypmckcId;
                RecordSet recordSet2 = new RecordSet();
                if (recordSet2.executeSql(sql)) {
                    sql = "select max(id) as maxid from uf_ypkcgl";
                    RecordSet recordSet3 = new RecordSet();
                    recordSet3.executeSql(sql);
                    recordSet3.next();
                    int maxId = recordSet3.getInt("maxid");
                    sql = "select FORMMODEID, MODEDATACREATER from uf_ypkcgl where id=" + maxId;
                    recordSet3.executeSql(sql);
                    recordSet3.next();
                    int FORMMODEID = recordSet3.getInt("FORMMODEID");
                    int MODEDATACREATER = recordSet3.getInt("MODEDATACREATER");
                    sql = "update uf_ypkcgl set LYSL=0, SYSL=" + dbsl + ", KCL=" + dbsl + ", SSGS=" + sbgs + " where id=" + maxId;
                    System.out.println("---->YPKCDB:"+sql);
                    recordSet3.executeSql(sql);
                    ModeRightInfo modeRightInfo = new ModeRightInfo();
                    modeRightInfo.editModeDataShare(MODEDATACREATER, FORMMODEID, maxId);

                }
            }
            int num = kcl - sysl + dbsl;
            sql = "update uf_ypkcgl set sysl=sysl-" + dbsl + ", lysl=" + num + " where id=" + ypmckcId;
            RecordSet recordSet2 = new RecordSet();
            recordSet2.executeSql(sql);
        }
        return SUCCESS;
    }
}
