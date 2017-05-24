package weaverjn.crm.main;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

/**
 * 客户仓库地址:明细表
 * @author songqi
 * @tel 13256247773
 * 2017年4月20日 上午11:53:15
 */
public class YXOAKHDZ extends BaseBean implements Action{

	private static final String tablename = "YXOAKHDZ";
	private RecordSetDataSource rsds = new RecordSetDataSource("crm_db");
	
	
	@Override
	public String execute(RequestInfo requestInfo) {
		Property[] properties = requestInfo.getMainTableInfo().getProperty();// 获取表单主字段信息
//		String t = requestInfo.getRequestManager().getBillTableName();//表单名称
		String name = "",value = "";
		String ghfmc = "";
		for (int i = 0; i < properties.length; i++) {
			name = properties[i].getName();// 主字段名称
			value = Util.null2String(properties[i].getValue());// 主字段对应的值
			if(name.equals("ghfmc")){
				ghfmc = value;
				break;
			}
		}
		RecordSet rs = new RecordSet();
		String ghfbh = "",ckdz="",yxqz="";
        String sql = "select ghfbh from uf_wrghdwzl where id=" + ghfmc;
        rs.executeSql(sql);
        if(rs.next())	ghfbh = Util.null2String(rs.getString("ghfbh"));
		String tdt = "uf_wrghdwzl_dt1";
		String sql2 = "select u.ghfbh ghfbh, d.ckdz ckdz,d.yxqz yxqz from uf_wrghdwzl u left join "+tdt+" d on u.id=D.MAINID where u.id='"+ghfmc+"' ";
		rs.executeSql(sql2);
		if(rs.next())
			ghfbh = Util.null2String(rs.getString("ghfbh"));
		rsds.execute("delete from " + tablename + " where YXOAKHDZ_KHBH='"+ghfbh+"'");
		rs.executeSql(sql2);
		while(rs.next()){
			ckdz = Util.null2String(rs.getString("ckdz"));
			yxqz = Util.null2String(rs.getString("yxqz")).replace("-", "");
			String crmsql = "insert into " + tablename + "(YXOAKHDZ_KHBH,YXOAKHDZ_CKDZ,YXOAKHDZ_YXQZ) values('"+ghfbh+"','"+ckdz+"','"+yxqz+"')";
			rsds.execute(crmsql);
			writeLog("新增sql： " + crmsql);
		}
		return Action.SUCCESS;
	}

}
