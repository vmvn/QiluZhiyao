package weaverjn.crm.main;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

/**
 * 客户
 * @author songqi
 * @tel 13256247773
 * 2017年4月20日 上午11:51:53
 */
public class YXOAKH extends BaseBean implements Action{

	private static final String tablename = "YXOAKH";
	private RecordSetDataSource rsds = new RecordSetDataSource("crm_db");
	
	private String a1_ghfbh = "";
	private String a2_fddbr = "";
	private String a3_ghdwzt = "";
	private String a4_yyzzzch = "";
	private String a5_yyzzfzjg = "";
	private String a6_yyzzfzrq = "";
	private String a7_yyzzyxqz = "";
	private String a8_zczb = "";
	private String a9_khlx = "";
	private String a10_zzjgdm = "";
	private String a11_zzjgfzrq = "";
	private String a12_yxqz = "";
	private String a13_swdjzbh = "";
	private String a14_xkzbh = "";
	private String a15_xkzmclx = "";
	private String a16_xkzfzjg = "";
	private String a17_xkzfzrq = "";
	private String a18_xkzyxqz = "";
	private String a19_jyfw = "";
	private String a20_qyfzr = "";
	private String a21_zlfzr = "";
	private String a22_jyfs = "";
	private String a23_gspzsbh = "";
	private String a24_rzzsmc = "";
	private String a25_gspfzjg = "";
	private String a26_gspfzrq = "";
	private String a27_gspyxqz = "";
	private String a28_zlbzxys = "";
	private String a29_yxqz = "";
	private String a30_zltxdcb = "";
	private String a31_dtyxq = "";

	
	private static final String b1_ghfbh= "ghfbh";
	private static final String b2_fddbr= "fddbr";
	private static final String b3_ghdwzt= "ghdwzt";
	private static final String b4_yyzzzch= "yyzzzch";
	private static final String b5_yyzzfzjg= "yyzzfzjg";
	private static final String b6_yyzzfzrq= "yyzzfzrq";
	private static final String b7_yyzzyxqz= "yyzzyxqz";
	private static final String b8_zczb= "zczb";
	private static final String b9_khlx= "khlx";
	private static final String b10_zzjgdm= "zzjgdm";
	private static final String b11_zzjgfzrq= "zzjgfzrq";
	private static final String b12_yxqz= "yxqz";
	private static final String b13_swdjzbh= "swdjzbh";
	private static final String b14_xkzbh= "xkzbh";
	private static final String b15_xkzmclx= "xkzmclx";
	private static final String b16_xkzfzjg= "xkzfzjg";
	private static final String b17_xkzfzrq= "xkzfzrq";
	private static final String b18_xkzyxqz= "xkzyxqz";
//	private static final String b19_jyfw= "jyfw";
	private static final String b20_qyfzr= "qyfzr";
	private static final String b21_zlfzr= "zlfzr";
	private static final String b22_jyfs= "jyfs";
	private static final String b23_gspzsbh= "gspzsbh";
	private static final String b24_rzzsmc= "rzzsmc";
	private static final String b25_gspfzjg= "gspfzjg";
	private static final String b26_gspfzrq= "gspfzrq";
	private static final String b27_gspyxqz= "gspyxqz";
	private static final String b28_zlbzxys= "zlbzxys";
	private static final String b29_yxqz= "yxqz";
	private static final String b30_zltxdcb= "zltxdcb";
	private static final String b31_dtyxq= "dtyxq";
	
	@Override
	public String execute(RequestInfo requestInfo) {
		Property[] properties = requestInfo.getMainTableInfo().getProperty();// 获取表单主字段信息
		String t = requestInfo.getRequestManager().getBillTableName();//表单名称
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
		RecordSet rs2 = new RecordSet();
		rs.executeSql("select * from uf_wrghdwzl where id=" + ghfmc);
		if(rs.next()){
			setA1_ghfbh(Util.null2String(rs.getString(b1_ghfbh)));
			setA2_fddbr(Util.null2String(rs.getString(b2_fddbr)));
			setA3_ghdwzt(Util.null2String(rs.getString(b3_ghdwzt)));
			setA4_yyzzzch(Util.null2String(rs.getString(b4_yyzzzch)));
			setA5_yyzzfzjg(Util.null2String(rs.getString(b5_yyzzfzjg)));
			setA6_yyzzfzrq(Util.null2String(rs.getString(b6_yyzzfzrq)).replace("-", ""));
			setA7_yyzzyxqz(Util.null2String(rs.getString(b7_yyzzyxqz)).replace("-", ""));
			setA8_zczb(Util.null2String(rs.getString(b8_zczb)));
			setA9_khlx(Util.null2String(rs.getString(b9_khlx)));
			setA10_zzjgdm(Util.null2String(rs.getString(b10_zzjgdm)));
			setA11_zzjgfzrq(Util.null2String(rs.getString(b11_zzjgfzrq)).replace("-", ""));
			setA12_yxqz(Util.null2String(rs.getString(b12_yxqz)).replace("-", ""));
			setA13_swdjzbh(Util.null2String(rs.getString(b13_swdjzbh)));
			setA14_xkzbh(Util.null2String(rs.getString(b14_xkzbh)));
			setA15_xkzmclx(Util.null2String(rs.getString(b15_xkzmclx)));
			setA16_xkzfzjg(Util.null2String(rs.getString(b16_xkzfzjg)));
			setA17_xkzfzrq(Util.null2String(rs.getString(b17_xkzfzrq)).replace("-", ""));
			setA18_xkzyxqz(Util.null2String(rs.getString(b18_xkzyxqz)).replace("-", ""));
			String jyfwsql = "select t2.jyfw jyfw from uf_wrghdwzl t1 left join uf_wrghdwzl_dt2 t2 on t1.id=t2.mainid where T1.GHFBH='"+getA1_ghfbh()+"' ";
			String jyfwstr = "";
			rs2.executeSql(jyfwsql);
			while(rs2.next()){
				jyfwstr += "," + Util.null2String(rs2.getString("jyfw"));
			}
			jyfwstr = jyfwstr.substring(1);
			setA19_jyfw(jyfwstr);
			setA20_qyfzr(Util.null2String(rs.getString(b20_qyfzr)));
			setA21_zlfzr(Util.null2String(rs.getString(b21_zlfzr)));
			setA22_jyfs(Util.null2String(rs.getString(b22_jyfs)));
			setA23_gspzsbh(Util.null2String(rs.getString(b23_gspzsbh)));
			setA24_rzzsmc(JnUtils.getFieldValue(t, b24_rzzsmc, Util.null2String(rs.getString(b24_rzzsmc))));
			setA25_gspfzjg(Util.null2String(rs.getString(b25_gspfzjg)));
			setA26_gspfzrq(Util.null2String(rs.getString(b26_gspfzrq)).replace("-", ""));
			setA27_gspyxqz(Util.null2String(rs.getString(b27_gspyxqz)).replace("-", ""));
			setA28_zlbzxys(Util.null2String(rs.getString(b28_zlbzxys)));
			setA29_yxqz(Util.null2String(rs.getString(b29_yxqz)).replace("-", ""));
			setA30_zltxdcb(Util.null2String(rs.getString(b30_zltxdcb)));
			setA31_dtyxq(Util.null2String(rs.getString(b31_dtyxq)).replace("-", ""));
		}
		
		if(!t.equals("formtable_main_851")){	//变更
			String editsql = this.editSqlGroup(tablename);
			rsds.execute(editsql);
			writeLog("修改sql: " + editsql);
		}else{
			String addsql = this.addSqlGroup(tablename);
			rsds.execute(addsql);
			writeLog("新增sql： " + addsql);
		}
		
		return Action.SUCCESS;
	}

	private String addSqlGroup(String tablename2) {
		StringBuffer key = new StringBuffer("insert into " + tablename + "(");
		StringBuffer val = new StringBuffer(" values(");
		key.append("YXOAKH_BGRQ,YXOAKH_BGSJ,YXOAKH_KHBH,YXOAKH_FR,YXOAKH_SYBZ,YXOAKH_YYZZHM,YXOAKH_YYZZ1,YXOAKH_YYZZ2,YXOAKH_YYZZ3,YXOAKH_ZCZB,YXOAKH_KHLX,YXOAKH_ZZJGHM,YXOAKH_ZZJG1,YXOAKH_ZZJG2,YXOAKH_ZZJG3,YXOAKH_XKZHM,YXOAKH_XKZMC,YXOAKH_XKZ1,YXOAKH_XKZ2,YXOAKH_XKZ3,YXOAKH_XKZ4,YXOAKH_XKZ5,YXOAKH_XKZ6,YXOAKH_XKZ7,YXOAKH_GSPHM,YXOAKH_GSPMC,YXOAKH_GSP1,YXOAKH_GSP2,YXOAKH_GSP3,YXOAKH_ZLBZSHM,YXOAKH_ZLBZS1,YXOAKH_ZLBZS2,YXOAKH_ZLBZS3)");
		val.append("'" + JnUtils.getDate("date") + "',");
		val.append("'" + JnUtils.getDate("datetime") + "',");
		val.append("'" + getA1_ghfbh() + "',");
		val.append("'" + getA2_fddbr() + "',");
		val.append("'" + getA3_ghdwzt() + "',");
		val.append("'" + getA4_yyzzzch() + "',");
		val.append("'" + getA5_yyzzfzjg() + "',");
		val.append("'" + getA6_yyzzfzrq() + "',");
		val.append("'" + getA7_yyzzyxqz() + "',");
		val.append("'" + getA8_zczb() + "',");
		val.append("'" + getA9_khlx() + "',");
		val.append("'" + getA10_zzjgdm() + "',");
		val.append("'" + getA11_zzjgfzrq() + "',");
		val.append("'" + getA12_yxqz() + "',");
		val.append("'" + getA13_swdjzbh() + "',");
		val.append("'" + getA14_xkzbh() + "',");
		val.append("'" + getA15_xkzmclx() + "',");
		val.append("'" + getA16_xkzfzjg() + "',");
		val.append("'" + getA17_xkzfzrq() + "',");
		val.append("'" + getA18_xkzyxqz() + "',");
		val.append("'" + getA19_jyfw() + "',");
		val.append("'" + getA20_qyfzr() + "',");
		val.append("'" + getA21_zlfzr() + "',");
		val.append("'" + getA22_jyfs() + "',");
		val.append("'" + getA23_gspzsbh() + "',");
		val.append("'" + getA24_rzzsmc() + "',");
		val.append("'" + getA25_gspfzjg() + "',");
		val.append("'" + getA26_gspfzrq() + "',");
		val.append("'" + getA27_gspyxqz() + "',");
		val.append("'" + getA28_zlbzxys() + "',");
		val.append("'" + getA29_yxqz() + "',");
		val.append("'" + getA30_zltxdcb() + "',");
		val.append("'" + getA31_dtyxq() + "')");
		String sql = key.toString() + val.toString();
		return sql;
	}

	private String editSqlGroup(String tablename2) {
		StringBuffer key = new StringBuffer("update " + tablename2 + " set ");
		key.append("YXOAKH_BGRQ='"+JnUtils.getDate("date")+"',").append("YXOAKH_BGSJ='"+JnUtils.getDate("datetime")+"',");
		key.append("YXOAKH_KHBH='"+getA1_ghfbh()+"',");
		key.append("YXOAKH_FR='"+getA2_fddbr()+"',");
		key.append("YXOAKH_SYBZ='"+getA3_ghdwzt()+"',");
		key.append("YXOAKH_YYZZHM='"+getA4_yyzzzch()+"',");
		key.append("YXOAKH_YYZZ1='"+getA5_yyzzfzjg()+"',");
		key.append("YXOAKH_YYZZ2='"+getA6_yyzzfzrq()+"',");
		key.append("YXOAKH_YYZZ3='"+getA7_yyzzyxqz()+"',");
		key.append("YXOAKH_ZCZB='"+getA8_zczb()+"',");
		key.append("YXOAKH_KHLX='"+getA9_khlx()+"',");
		key.append("YXOAKH_ZZJGHM='"+getA10_zzjgdm()+"',");
		key.append("YXOAKH_ZZJG1='"+getA11_zzjgfzrq()+"',");
		key.append("YXOAKH_ZZJG2='"+getA12_yxqz()+"',");
		key.append("YXOAKH_ZZJG3='"+getA13_swdjzbh()+"',");
		key.append("YXOAKH_XKZHM='"+getA14_xkzbh()+"',");
		key.append("YXOAKH_XKZMC='"+getA15_xkzmclx()+"',");
		key.append("YXOAKH_XKZ1='"+getA16_xkzfzjg()+"',");
		key.append("YXOAKH_XKZ2='"+getA17_xkzfzrq()+"',");
		key.append("YXOAKH_XKZ3='"+getA18_xkzyxqz()+"',");
		key.append("YXOAKH_XKZ4='"+getA19_jyfw()+"',");
		key.append("YXOAKH_XKZ5='"+getA20_qyfzr()+"',");
		key.append("YXOAKH_XKZ6='"+getA21_zlfzr()+"',");
		key.append("YXOAKH_XKZ7='"+getA22_jyfs()+"',");
		key.append("YXOAKH_GSPHM='"+getA23_gspzsbh()+"',");
		key.append("YXOAKH_GSPMC='"+getA24_rzzsmc()+"',");
		key.append("YXOAKH_GSP1='"+getA25_gspfzjg()+"',");
		key.append("YXOAKH_GSP2='"+getA26_gspfzrq()+"',");
		key.append("YXOAKH_GSP3='"+getA27_gspyxqz()+"',");
		key.append("YXOAKH_ZLBZSHM='"+getA28_zlbzxys()+"',");
		key.append("YXOAKH_ZLBZS1='"+getA29_yxqz()+"',");
		key.append("YXOAKH_ZLBZS2='"+getA30_zltxdcb()+"',");
		key.append("YXOAKH_ZLBZS3='"+getA31_dtyxq()+"' ");
		key.append(" where YXOAKH_KHBH='"+getA1_ghfbh()+"' ");
		return key.toString();
	}

	public String getA1_ghfbh() {
		return a1_ghfbh;
	}


	public void setA1_ghfbh(String a1_ghfbh) {
		this.a1_ghfbh = a1_ghfbh;
	}


	public String getA2_fddbr() {
		return a2_fddbr;
	}


	public void setA2_fddbr(String a2_fddbr) {
		this.a2_fddbr = a2_fddbr;
	}


	public String getA3_ghdwzt() {
		return a3_ghdwzt;
	}


	public void setA3_ghdwzt(String a3_ghdwzt) {
		this.a3_ghdwzt = a3_ghdwzt;
	}


	public String getA4_yyzzzch() {
		return a4_yyzzzch;
	}


	public void setA4_yyzzzch(String a4_yyzzzch) {
		this.a4_yyzzzch = a4_yyzzzch;
	}

	public String getA5_yyzzfzjg() {
		return a5_yyzzfzjg;
	}

	public void setA5_yyzzfzjg(String a5_yyzzfzjg) {
		this.a5_yyzzfzjg = a5_yyzzfzjg;
	}

	public String getA6_yyzzfzrq() {
		return a6_yyzzfzrq;
	}

	public void setA6_yyzzfzrq(String a6_yyzzfzrq) {
		this.a6_yyzzfzrq = a6_yyzzfzrq;
	}

	public String getA7_yyzzyxqz() {
		return a7_yyzzyxqz;
	}

	public void setA7_yyzzyxqz(String a7_yyzzyxqz) {
		this.a7_yyzzyxqz = a7_yyzzyxqz;
	}

	public String getA8_zczb() {
		return a8_zczb;
	}

	public void setA8_zczb(String a8_zczb) {
		this.a8_zczb = a8_zczb;
	}

	public String getA9_khlx() {
		return a9_khlx;
	}

	public void setA9_khlx(String a9_khlx) {
		this.a9_khlx = a9_khlx;
	}

	public String getA10_zzjgdm() {
		return a10_zzjgdm;
	}

	public void setA10_zzjgdm(String a10_zzjgdm) {
		this.a10_zzjgdm = a10_zzjgdm;
	}

	public String getA11_zzjgfzrq() {
		return a11_zzjgfzrq;
	}

	public void setA11_zzjgfzrq(String a11_zzjgfzrq) {
		this.a11_zzjgfzrq = a11_zzjgfzrq;
	}

	public String getA12_yxqz() {
		return a12_yxqz;
	}

	public void setA12_yxqz(String a12_yxqz) {
		this.a12_yxqz = a12_yxqz;
	}

	public String getA13_swdjzbh() {
		return a13_swdjzbh;
	}

	public void setA13_swdjzbh(String a13_swdjzbh) {
		this.a13_swdjzbh = a13_swdjzbh;
	}

	public String getA14_xkzbh() {
		return a14_xkzbh;
	}

	public void setA14_xkzbh(String a14_xkzbh) {
		this.a14_xkzbh = a14_xkzbh;
	}

	public String getA15_xkzmclx() {
		return a15_xkzmclx;
	}

	public void setA15_xkzmclx(String a15_xkzmclx) {
		this.a15_xkzmclx = a15_xkzmclx;
	}

	public String getA16_xkzfzjg() {
		return a16_xkzfzjg;
	}

	public void setA16_xkzfzjg(String a16_xkzfzjg) {
		this.a16_xkzfzjg = a16_xkzfzjg;
	}

	public String getA17_xkzfzrq() {
		return a17_xkzfzrq;
	}

	public void setA17_xkzfzrq(String a17_xkzfzrq) {
		this.a17_xkzfzrq = a17_xkzfzrq;
	}

	public String getA18_xkzyxqz() {
		return a18_xkzyxqz;
	}

	public void setA18_xkzyxqz(String a18_xkzyxqz) {
		this.a18_xkzyxqz = a18_xkzyxqz;
	}

	public String getA19_jyfw() {
		return a19_jyfw;
	}

	public void setA19_jyfw(String a19_jyfw) {
		this.a19_jyfw = a19_jyfw;
	}

	public String getA20_qyfzr() {
		return a20_qyfzr;
	}

	public void setA20_qyfzr(String a20_qyfzr) {
		this.a20_qyfzr = a20_qyfzr;
	}

	public String getA21_zlfzr() {
		return a21_zlfzr;
	}

	public void setA21_zlfzr(String a21_zlfzr) {
		this.a21_zlfzr = a21_zlfzr;
	}

	public String getA22_jyfs() {
		return a22_jyfs;
	}

	public void setA22_jyfs(String a22_jyfs) {
		this.a22_jyfs = a22_jyfs;
	}

	public String getA23_gspzsbh() {
		return a23_gspzsbh;
	}

	public void setA23_gspzsbh(String a23_gspzsbh) {
		this.a23_gspzsbh = a23_gspzsbh;
	}

	public String getA24_rzzsmc() {
		return a24_rzzsmc;
	}

	public void setA24_rzzsmc(String a24_rzzsmc) {
		this.a24_rzzsmc = a24_rzzsmc;
	}

	public String getA25_gspfzjg() {
		return a25_gspfzjg;
	}

	public void setA25_gspfzjg(String a25_gspfzjg) {
		this.a25_gspfzjg = a25_gspfzjg;
	}

	public String getA26_gspfzrq() {
		return a26_gspfzrq;
	}

	public void setA26_gspfzrq(String a26_gspfzrq) {
		this.a26_gspfzrq = a26_gspfzrq;
	}

	public String getA27_gspyxqz() {
		return a27_gspyxqz;
	}

	public void setA27_gspyxqz(String a27_gspyxqz) {
		this.a27_gspyxqz = a27_gspyxqz;
	}

	public String getA28_zlbzxys() {
		return a28_zlbzxys;
	}

	public void setA28_zlbzxys(String a28_zlbzxys) {
		this.a28_zlbzxys = a28_zlbzxys;
	}

	public String getA29_yxqz() {
		return a29_yxqz;
	}

	public void setA29_yxqz(String a29_yxqz) {
		this.a29_yxqz = a29_yxqz;
	}

	public String getA30_zltxdcb() {
		return a30_zltxdcb;
	}

	public void setA30_zltxdcb(String a30_zltxdcb) {
		this.a30_zltxdcb = a30_zltxdcb;
	}

	public String getA31_dtyxq() {
		return a31_dtyxq;
	}

	public void setA31_dtyxq(String a31_dtyxq) {
		this.a31_dtyxq = a31_dtyxq;
	}

	
}
