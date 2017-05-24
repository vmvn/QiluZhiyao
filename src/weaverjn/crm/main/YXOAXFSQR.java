package weaverjn.crm.main;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

/**
 * 采购员YXOAXFSQR
 * @author songqi
 * @tel 13256247773
 * 2017年4月20日 上午11:53:15
 */
public class YXOAXFSQR extends BaseBean implements Action{

	private static final String tablename = "YXOAXFSQR";
	private RecordSetDataSource rsds = new RecordSetDataSource("crm_db");
	
	private String ghdwbh = "";
	private String ghdwmc = "";
	private String cgybh = "";
	private String cgyxm = "";
	private String cgysfz = "";
	private String sfzyxq = "";
	private String fdsqr = "";
	private String sfyfrqz = "";
	private String sfyghd = "";
	private String sfysfz = "";
	private String sfzfyz = "";
	private String sqqy = "";
	private String sqcgje = "";
	private String sgz = "";
	private String sgzyxq = "";
	private String jyfw = "";

	
	// 使用表单建模
	@Override
	public String execute(RequestInfo requestInfo) {
		RecordSet rs = new RecordSet();
		RecordSet rs2 = new RecordSet();
		String t = "";
		String billid = requestInfo.getRequestid();
		String modId = requestInfo.getWorkflowid();
        String sql = "select b.tablename tablename,b.id from modeinfo a,workflow_bill b where a.formid = b.id and a.id = " + modId;
		rs.executeSql(sql);
		if(rs.next())
			t = Util.null2String(rs.getString("tablename"));
		String sql2 = "select * from " + t + " where id=" + billid;
		rs.executeSql(sql2);
		if(rs.next()){
			setCgybh(Util.null2String(rs.getString("cgybh")));
			setGhdwbh(Util.null2String(rs.getString("ghdwbh")));
			setGhdwmc(Util.null2String(rs.getString("ghdwmc")));
			setCgybh(Util.null2String(rs.getString("cgybh")));
			setCgyxm(JnUtils.getLastname(Util.null2String(rs.getString("cgyxm"))));
			setCgysfz(Util.null2String(rs.getString("cgysfz")));
			setSfzyxq(Util.null2String(rs.getString("sfzyxq")).replace("-", ""));
			setFdsqr(JnUtils.getLastname(Util.null2String(rs.getString("fdsqr"))));
			String sfyfrqz = Util.null2String(rs.getString("sfyfrqz"));
			setSfyfrqz(sfyfrqz.equals("0") ? "1" : "0");
			String sfyghd = Util.null2String(rs.getString("sfyghd"));
			setSfyghd(sfyghd.equals("0") ? "1" : "0");
			String sfysfz = Util.null2String(rs.getString("sfysfz"));
			setSfysfz(sfysfz.equals("0") ? "1" : "0");
			String sfzfyz = Util.null2String(rs.getString("sfzfyz"));
			setSfzfyz(sfzfyz.equals("0") ? "1" : "0");
			setSqqy(Util.null2String(rs.getString("sqqy")));
			setSqcgje(Util.null2String(rs.getString("sqcgje")));
			setSgz(Util.null2String(rs.getString("sgz")));
			setSgzyxq(Util.null2String(rs.getString("sgzyxq")).replace("-", ""));
			String jyfwsql = "select t2.jyfw jyfw from uf_wrghdwcgyzl t1 left join uf_wrghdwcgyzl_dt2 t2 on t1.id=t2.mainid where T1.CGYBH='"+getCgybh()+"' ";
			String jyfwstr = "";
			rs2.executeSql(jyfwsql);
			while(rs2.next()){
				jyfwstr += "," + Util.null2String(rs2.getString("jyfw"));
			}
			jyfwstr = jyfwstr.substring(1);
			setJyfw(jyfwstr);
		}
		String sql3 = "select * from " + tablename + " where YXOAXFSQR_RYBH='"+getCgybh()+"'";
		rsds.executeSql(sql3);
		if(rsds.next()){	// 修改
			String editsql = this.editSqlGroup(tablename);
			rsds.execute(editsql);
			writeLog("修改sql: " + editsql);
		}else{	// 新增
			String addsql = this.addSqlGroup(tablename);
			rsds.execute(addsql);
			writeLog("新增sql： " + addsql);
		}
		return Action.SUCCESS;
	}
	private String addSqlGroup(String tablename2) {
		StringBuffer key = new StringBuffer("insert into " + tablename + "(");
		key.append("YXOAXFSQR_BGRQ, YXOAXFSQR_BGSJ, YXOAXFSQR_KHBH, YXOAXFSQR_KHMC, YXOAXFSQR_RYBH, YXOAXFSQR_RYXM, YXOAXFSQR_SFZH, YXOAXFSQR_SFZYXQ, YXOAXFSQR_FDSQR, YXOAXFSQR_SFYFRQZ, YXOAXFSQR_SFYGHDWZ, YXOAXFSQR_SFYSFZFYJ, YXOAXFSQR_SFYSFZFYJGZ, YXOAXFSQR_SQQY, YXOAXFSQR_SQJE, YXOAXFSQR_SGZH, YXOAXFSQR_SGZQX, YXOAXFSQR_JYFW) ");
		key.append(" values(");
		key.append("'" + JnUtils.getDate("date") + "',").append("'" + JnUtils.getDate("datetime") + "',");
		key.append("'"+getGhdwbh()+"',");
		key.append("'"+getGhdwmc()+"',");
		key.append("'"+getCgybh()+"',");
		key.append("'"+getCgyxm()+"',");
		key.append("'"+getCgysfz()+"',");
		key.append("'"+getSfzyxq()+"',");
		key.append("'"+getFdsqr()+"',");
		key.append("'"+getSfyfrqz()+"',");
		key.append("'"+getSfyghd()+"',");
		key.append("'"+getSfysfz()+"',");
		key.append("'"+getSfzfyz()+"',");
		key.append("'"+getSqqy()+"',");
		key.append("'"+getSqcgje()+"',");
		key.append("'"+getSgz()+"',");
		key.append("'"+getSgzyxq()+"',");
		key.append("'"+getJyfw()+"')");
		return key.toString();
	}
	private String editSqlGroup(String tablename2) {
		StringBuffer key = new StringBuffer("update " + tablename2 + " set ");
		key.append("YXOAXFSQR_BGRQ='"+JnUtils.getDate("date")+"',").append("YXOAXFSQR_BGSJ='"+JnUtils.getDate("datetime")+"',");
		key.append("YXOAXFSQR_KHBH='"+getGhdwbh()+"',");
		key.append("YXOAXFSQR_KHMC='"+getGhdwmc()+"',");
		key.append("YXOAXFSQR_RYBH='"+getCgybh()+"',");
		key.append("YXOAXFSQR_RYXM='"+getCgyxm()+"',");
		key.append("YXOAXFSQR_SFZH='"+getCgysfz()+"',");
		key.append("YXOAXFSQR_SFZYXQ='"+getSfzyxq()+"',");
		key.append("YXOAXFSQR_FDSQR='"+getFdsqr()+"',");
		key.append("YXOAXFSQR_SFYFRQZ='"+getSfyfrqz()+"',");
		key.append("YXOAXFSQR_SFYGHDWZ='"+getSfyghd()+"',");
		key.append("YXOAXFSQR_SFYSFZFYJ='"+getSfysfz()+"',");
		key.append("YXOAXFSQR_SFYSFZFYJGZ='"+getSfzfyz()+"',");
		key.append("YXOAXFSQR_SQQY='"+getSqqy()+"',");
		key.append("YXOAXFSQR_SQJE='"+getSqcgje()+"',");
		key.append("YXOAXFSQR_SGZH='"+getSgz()+"',");
		key.append("YXOAXFSQR_SGZQX='"+getSgzyxq()+"',");
		key.append("YXOAXFSQR_JYFW='"+getJyfw()+"' ");
		key.append(" where YXOAXFSQR_RYBH='"+getCgybh()+"' ");
		return key.toString();
	}
	public String getGhdwbh() {
		return ghdwbh;
	}
	public void setGhdwbh(String ghdwbh) {
		this.ghdwbh = ghdwbh;
	}
	public String getGhdwmc() {
		return ghdwmc;
	}
	public void setGhdwmc(String ghdwmc) {
		this.ghdwmc = ghdwmc;
	}
	public String getCgybh() {
		return cgybh;
	}
	public void setCgybh(String cgybh) {
		this.cgybh = cgybh;
	}
	public String getCgyxm() {
		return cgyxm;
	}
	public void setCgyxm(String cgyxm) {
		this.cgyxm = cgyxm;
	}
	public String getCgysfz() {
		return cgysfz;
	}
	public void setCgysfz(String cgysfz) {
		this.cgysfz = cgysfz;
	}
	public String getSfzyxq() {
		return sfzyxq;
	}
	public void setSfzyxq(String sfzyxq) {
		this.sfzyxq = sfzyxq;
	}
	public String getFdsqr() {
		return fdsqr;
	}
	public void setFdsqr(String fdsqr) {
		this.fdsqr = fdsqr;
	}
	public String getSfyfrqz() {
		return sfyfrqz;
	}
	public void setSfyfrqz(String sfyfrqz) {
		this.sfyfrqz = sfyfrqz;
	}
	public String getSfyghd() {
		return sfyghd;
	}
	public void setSfyghd(String sfyghd) {
		this.sfyghd = sfyghd;
	}
	public String getSfysfz() {
		return sfysfz;
	}
	public void setSfysfz(String sfysfz) {
		this.sfysfz = sfysfz;
	}
	public String getSfzfyz() {
		return sfzfyz;
	}
	public void setSfzfyz(String sfzfyz) {
		this.sfzfyz = sfzfyz;
	}
	public String getSqqy() {
		return sqqy;
	}
	public void setSqqy(String sqqy) {
		this.sqqy = sqqy;
	}
	public String getSqcgje() {
		return sqcgje;
	}
	public void setSqcgje(String sqcgje) {
		this.sqcgje = sqcgje;
	}
	public String getSgz() {
		return sgz;
	}
	public void setSgz(String sgz) {
		this.sgz = sgz;
	}
	public String getSgzyxq() {
		return sgzyxq;
	}
	public void setSgzyxq(String sgzyxq) {
		this.sgzyxq = sgzyxq;
	}
	public String getJyfw() {
		return jyfw;
	}
	public void setJyfw(String jyfw) {
		this.jyfw = jyfw;
	}

}
