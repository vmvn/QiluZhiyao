package weaverjn.crm.main;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

/**
 * 采购员:明细表YXOAXFSQRPZ
 * @author songqi
 * @tel 13256247773
 * 2017年4月21日 下午3:50:31
 */
public class YXOAXFSQRPZ extends BaseBean implements Action{
	private static final String tablename = "YXOAXFSQRPZ";
	private RecordSetDataSource rsds = new RecordSetDataSource("crm_db");
	
	private String cgybh = "";
	private String pzbh = "";
	private String tymc = "";
	private String spm = "";
	private String gg = "";
	private String bzgg = "";
	private String sfxdgg = "";
	
	
	@Override
	public String execute(RequestInfo requestInfo) {
		
		RecordSet rs = new RecordSet();
		String t = "";
		String billid = requestInfo.getRequestid();
		String modId = requestInfo.getWorkflowid();
        String sql = "select b.tablename tablename,b.id from modeinfo a,workflow_bill b where a.formid = b.id and a.id = " + modId;
		rs.executeSql(sql);
		if(rs.next())
			t = Util.null2String(rs.getString("tablename"));
		String tdt = t + "_dt1";
		String sql2 = "select u.cgybh cgybh,D.BZGG bzgg,D.GG gg,D.PZBH pzbh,D.SFXDGG SFXDGG,D.SPM SPM,D.TYMC TYMC from "+t+" u left join "+tdt+" d on u.id=D.MAINID where u.id='"+billid+"'";
		rs.executeSql(sql2);
		if(rs.next()){
			setCgybh(Util.null2String(rs.getString("cgybh")));
		}
		rsds.execute("delete from " + tablename + " where YXOAXFSQRPZ_RYBH='"+getCgybh()+"'");
		rs.executeSql(sql2);
		while(rs.next()){
			setCgybh(Util.null2String(rs.getString("cgybh")));
			setPzbh(Util.null2String(rs.getString("pzbh")));
			setTymc(Util.null2String(rs.getString("tymc")));
			setSpm(Util.null2String(rs.getString("spm")));
			setGg(Util.null2String(rs.getString("gg")));
			setBzgg(Util.null2String(rs.getString("bzgg")));
			setSfxdgg(Util.null2String(rs.getString("sfxdgg")));
			String addsql = addSql();
			rsds.execute(addsql);
			writeLog("新增采购员明细表sql： " + addsql);
		}
		return Action.SUCCESS;
	}


	private String addSql() {
		StringBuffer key = new StringBuffer("insert into " + tablename + "(");
		key.append("YXOAXFSQRPZ_BGRQ, YXOAXFSQRPZ_BGSJ, YXOAXFSQRPZ_RYBH, YXOAXFSQRPZ_WLBH, YXOAXFSQRPZ_TYM, YXOAXFSQRPZ_SPM, YXOAXFSQRPZ_GG, YXOAXFSQRPZ_BZGG, YXOAXFSQRPZ_SFXDGG) ");
		key.append(" values(");
		key.append("'" + JnUtils.getDate("date") + "',").append("'" + JnUtils.getDate("datetime") + "',");
		key.append("'"+getCgybh()+"',");
		key.append("'"+getPzbh()+"',");
		key.append("'"+getTymc()+"',");
		key.append("'"+getSpm()+"',");
		key.append("'"+getGg()+"',");
		key.append("'"+getBzgg()+"',");
		key.append("'"+getSfxdgg()+"') ");
		return key.toString();
	}


	public String getCgybh() {
		return cgybh;
	}


	public void setCgybh(String cgybh) {
		this.cgybh = cgybh;
	}




	public String getTymc() {
		return tymc;
	}


	public void setTymc(String tymc) {
		this.tymc = tymc;
	}


	public String getSpm() {
		return spm;
	}


	public void setSpm(String spm) {
		this.spm = spm;
	}


	public String getGg() {
		return gg;
	}


	public void setGg(String gg) {
		this.gg = gg;
	}


	public String getPzbh() {
		return pzbh;
	}


	public void setPzbh(String pzbh) {
		this.pzbh = pzbh;
	}


	public String getBzgg() {
		return bzgg;
	}


	public void setBzgg(String bzgg) {
		this.bzgg = bzgg;
	}


	public String getSfxdgg() {
		return sfxdgg;
	}


	public void setSfxdgg(String sfxdgg) {
		this.sfxdgg = sfxdgg;
	}


	
}
