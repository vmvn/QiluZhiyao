package weaverjn.crm.main;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaverjn.action.integration.utils;

import java.util.Map;

/**
 * 客户仓库地址:明细表
 * @author songqi
 * @tel 13256247773
 * 2017年4月20日 上午11:53:15
 */
public class YXOAKHDZ extends BaseBean implements Action{
	private String vkorg;
	private String uf;//uf_wrghdwzl
	

	private static final String tablename = "YXOAKHDZ";
	private RecordSetDataSource rsds = new RecordSetDataSource("crm_db");
	
	
	@Override
	public String execute(RequestInfo requestInfo) {
		Map<String, String> mainTableData = utils.getMainTableData(requestInfo.getMainTableInfo());
		String ghfmc = mainTableData.get("ghdwmc1") == null ? mainTableData.get("ghfmc") : mainTableData.get("ghdwmc1");
		RecordSet rs = new RecordSet();
		String ghfbh = "",ckdz="",yxqz="";
        String sql = "select ghfbh from "+uf+" where id=" + ghfmc;
        rs.executeSql(sql);
        if(rs.next())	ghfbh = Util.null2String(rs.getString("ghfbh"));
		String tdt = uf+"_dt1";
		String sql2 = "select u.ghfbh ghfbh, d.ckdz ckdz,d.yxqz yxqz from "+uf+" u left join "+tdt+" d on u.id=D.MAINID where u.id='"+ghfmc+"' ";
		rs.executeSql(sql2);
		if(rs.next())
			ghfbh = Util.null2String(rs.getString("ghfbh"));
		rsds.execute("delete from " + tablename + " where YXOAKHDZ_KHBH='"+ghfbh+"'");
		rs.executeSql(sql2);
		while(rs.next()){
			ckdz = Util.null2String(rs.getString("ckdz"));
			yxqz = Util.null2String(rs.getString("yxqz")).replace("-", "");
			String crmsql = "insert into " + tablename + "(YXOAKHDZ_KHBH,YXOAKHDZ_CKDZ,YXOAKHDZ_YXQZ,YXOAKHDZ_GSID) values('"+ghfbh+"','"+ckdz+"','"+yxqz+"','"+vkorg+"')";
			rsds.execute(crmsql);
			writeLog("CRM仓库地址新增sql： " + crmsql);
		}
		return Action.SUCCESS;
	}


	public String getVkorg() {
		return vkorg;
	}


	public void setVkorg(String vkorg) {
		this.vkorg = vkorg;
	}


	public String getUf() {
		return uf;
	}


	public void setUf(String uf) {
		this.uf = uf;
	}

}
