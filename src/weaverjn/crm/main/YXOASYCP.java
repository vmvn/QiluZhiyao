package weaverjn.crm.main;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
/**
 * crm接口程序：首营产品YXOASYCP
 * @author songqi
 * 2017年3月27日 上午10:09:48
 */
public class YXOASYCP extends BaseBean implements Action{
//public class YXOASYCP{
	private static final String tablename = "yxoasycp";
	private RecordSetDataSource rsds = new RecordSetDataSource("crm_db");
	private String a1_djbh = "";
	private String a2_ypbh = "";
	private String a3_spbm = "";
	private String a4_jx = "";
	private String a5_bzgg = "";
	private String a6_zjldw = "";
	private String a7_scqy = "";
	private String a8_zcsb = "";
	private String a9_pzwh = "";
	private String a10_pzwhyxq = "";
	private String a11_gmpzsh = "";
	private String a12_gmpfw = "";
	private String a13_gmpyxqz = "";
	private String a14_xty = "";
	private String a15_cctj = "";
	private String a16_yhzq = "";
	private String a17_yxqy = "";
	private String a18_sxsdts1 = "";
	private String a20_sfjdzjgm = "";
	private String a21_bzyfbs = "";
	private String a22_cd = "";
	private String a23_gd = "";
	private String a24_kd = "";
	private String a25_bzdbjs = "";
	private String a26_zlgly = "";
	private String a27_bgy = "";
	private String a28_yhy = "";
	private String a29_sfsrys = "";
	private String a30_csjgsj = "";
	private String a31_zspzh = "";
	private String a32_zsxs = "";
	private String a33_zdr = "";
	private String a34_bmmc = "";
	private String a35_zdrq = "";
	private String a36_gc = "";
	private String a37_tymc = "";
	private String a38_wlms = "";
	private String a39_ywm = "";
	private String a40_gg = "";
	private String a41_xz = "";
	private String a42_fzjld = "";
	private String a43_bwtscqy = "";
	private String a44_zlbz = "";
	private String a45_zccck = "";
	private String a46_ckdd = "";
	private String a47_bzpzxdws = "";
	private String a48_sxbjts = "";
	private String a49_sxbjts2 = "";
	private String a50_jhl = "";
	private String a51_zhhl = "";
	private String a52_xhhl = "";
	private String a53_lsj = "";
	private String a54_zl = "";
	private String a55_fpbm = "";
	private String a56_ysy = "";
	private String a57_fhy = "";
	private String a58_sflc = "";
	private String a59_sfsrfh = "";
	private String a60_zt = "";
	private String a61_zspzm = "";
	private String a62_dwjg = "";
	private String a63_sdyy = "";
	private static final String b1_djbh = "djbh";
	private static final String b2_ypbh = "ypbh";
	private static final String b3_spbm = "spbm";
	private static final String b4_jx = "jx";
	private static final String b5_bzgg = "bzgg";
	private static final String b6_zjldw = "zjldw";
	private static final String b7_scqy = "scqy";
	private static final String b8_zcsb = "zcsb";
	private static final String b9_pzwh = "pzwh";
	private static final String b10_pzwhyxq = "pzwhyxq";
	private static final String b11_gmpzsh = "gmpzsh";
	private static final String b12_gmpfw = "gmpfw";
	private static final String b13_gmpyxqz = "gmpyxqz";
	private static final String b14_xty = "xty";
	private static final String b15_cctj = "cctj";
	private static final String b16_yhzq = "yhzq";
	private static final String b17_yxqy = "yxqy";
	private static final String b18_sxsdts1 = "sxsdts1";
	private static final String b20_sfjdzjgm = "sfjdzjgm";
	private static final String b21_bzyfbs = "bzyfbs";
	private static final String b22_cd = "cd";
	private static final String b23_gd = "gd";
	private static final String b24_kd = "kd";
	private static final String b25_bzdbjs = "bzdbjs";
	private static final String b26_zlgly = "zlgly";
	private static final String b27_bgy = "bgy";
	private static final String b28_yhy = "yhy";
	private static final String b29_sfsrys = "sfsrys";
	private static final String b30_csjgsj = "csjgsj";
	private static final String b31_zspzh = "zspzh";
	private static final String b32_zsxs = "zsxs";
	private static final String b33_zdr = "zdr";
	private static final String b34_bmmc = "bmmc";
	private static final String b35_zdrq = "zdrq";
	private static final String b36_gc = "gc";
	private static final String b37_tymc = "tymc";
	private static final String b38_wlms = "wlms";
	private static final String b39_ywm = "ywm";
	private static final String b40_gg = "gg";
	private static final String b41_xz = "xz";
	private static final String b42_fzjld = "fzjld";
	private static final String b43_bwtscqy = "bwtscqy";
	private static final String b44_zlbz = "zlbz";
	private static final String b45_zccck = "zccck";
	private static final String b46_ckdd = "ckdd";
	private static final String b47_bzpzxdws = "bzpzxdws";
	private static final String b48_sxbjts = "sxbjts";
	private static final String b49_sxbjts2 = "sxbjts2";
	private static final String b50_jhl = "jhl";
	private static final String b51_zhhl = "zhhl";
	private static final String b52_xhhl = "xhhl";
	private static final String b53_lsj = "lsj";
	private static final String b54_zl = "zl";
	private static final String b55_fpbm = "fpbm";
	private static final String b56_ysy = "ysy";
	private static final String b57_fhy = "fhy";
	private static final String b58_sflc = "sflc";
	private static final String b59_sfsrfh = "sfsrfh";
	private static final String b60_zt = "zt";
	private static final String b61_zspzm = "zspzm";
	private static final String b62_dwjg = "dwjg";
	private static final String b63_sdyy = "sdyy";

	private String YPMC = "";
	private static final String ypmc = "ypmc";

public static void main(String[] args) {
	YXOASYCP a = new YXOASYCP();
	System.out.println("".equals( a.getA10_pzwhyxq()));
	
}
	
	@Override
	public String execute(RequestInfo requestInfo) {
		Property[] properties = requestInfo.getMainTableInfo().getProperty();// 获取表单主字段信息
		String t = requestInfo.getRequestManager().getBillTableName();//表单名称
		String name = "",value = "";
		for (int i = 0; i < properties.length; i++) {
			name = properties[i].getName();// 主字段名称
			value = Util.null2String(properties[i].getValue());// 主字段对应的值
			if(name.equals(ypmc)){
				setYPMC(value);
				break;
			}
		}
		RecordSet rs = new RecordSet();
		rs.executeSql("select * from uf_sdwrypzl where id='" + getYPMC() + "'");
		if(rs.next()){
			setA1_djbh(Util.null2String(rs.getString(b1_djbh)));
			setA2_ypbh(Util.null2String(rs.getString(b2_ypbh)));
			setA3_spbm(Util.null2String(rs.getString(b3_spbm)));
			setA4_jx(JnUtils.getJX(JnUtils.getFieldname(t,b4_jx,Util.null2String(rs.getString(b4_jx)))));
			setA5_bzgg(Util.null2String(rs.getString(b5_bzgg)));
			setA6_zjldw(JnUtils.getJldw(Util.null2String(rs.getString(b6_zjldw))));
			setA7_scqy(Util.null2String(rs.getString(b7_scqy)));
			setA8_zcsb(Util.null2String(rs.getString(b8_zcsb)));
			setA9_pzwh(Util.null2String(rs.getString(b9_pzwh)));
			setA10_pzwhyxq(Util.null2String(rs.getString(b10_pzwhyxq)).replace("-", ""));
			setA11_gmpzsh(Util.null2String(rs.getString(b11_gmpzsh)));
			setA12_gmpfw(Util.null2String(rs.getString(b12_gmpfw)));
			setA13_gmpyxqz(Util.null2String(rs.getString(b13_gmpyxqz)).replace("-", ""));
			setA14_xty(Util.null2String(rs.getString(b14_xty)));
			setA15_cctj(Util.null2String(rs.getString(b15_cctj)));
			setA16_yhzq(Util.null2String(rs.getString(b16_yhzq)));
			setA17_yxqy(Util.null2String(rs.getString(b17_yxqy)));
			setA18_sxsdts1(Util.null2String(rs.getString(b18_sxsdts1)));
			setA20_sfjdzjgm(JnUtils.getFieldname(t,b20_sfjdzjgm, Util.null2String(rs.getString(b20_sfjdzjgm))));
			setA21_bzyfbs(Util.null2String(rs.getString(b21_bzyfbs)));
			setA22_cd(Util.null2String(rs.getString(b22_cd)));
			setA23_gd(Util.null2String(rs.getString(b23_gd)));
			setA24_kd(Util.null2String(rs.getString(b24_kd)));
			setA25_bzdbjs(Util.null2String(rs.getString(b25_bzdbjs)));
			setA26_zlgly(JnUtils.getLastname(Util.null2String(rs.getString(b26_zlgly))));
			setA27_bgy(JnUtils.getLastname(Util.null2String(rs.getString(b27_bgy))));
			setA28_yhy(JnUtils.getLastname(Util.null2String(rs.getString(b28_yhy))));
			setA29_sfsrys(JnUtils.getFieldname(t,b29_sfsrys, Util.null2String(rs.getString(b29_sfsrys))));
			setA30_csjgsj(Util.null2String(rs.getString(b30_csjgsj)));
			setA31_zspzh(Util.null2String(rs.getString(b31_zspzh)));
			setA32_zsxs(Util.null2String(rs.getString(b32_zsxs)));
			setA33_zdr(JnUtils.getLastname(Util.null2String(rs.getString(b33_zdr))));
			setA34_bmmc(JnUtils.getDeptname(Util.null2String(rs.getString(b34_bmmc))));
			setA35_zdrq(Util.null2String(rs.getString(b35_zdrq)));
			setA36_gc(Util.null2String(rs.getString(b36_gc)));
			setA37_tymc(Util.null2String(rs.getString(b37_tymc)));
			setA38_wlms(Util.null2String(rs.getString(b38_wlms)));
			setA39_ywm(Util.null2String(rs.getString(b39_ywm)));
			setA40_gg(Util.null2String(rs.getString(b40_gg)));
			setA41_xz(Util.null2String(rs.getString(b41_xz)));
			setA42_fzjld(JnUtils.getJldw(Util.null2String(rs.getString(b42_fzjld))));
			setA43_bwtscqy(Util.null2String(rs.getString(b43_bwtscqy)));
			setA44_zlbz(Util.null2String(rs.getString(b44_zlbz)));
			setA45_zccck(Util.null2String(rs.getString(b45_zccck)));
			setA46_ckdd(Util.null2String(rs.getString(b46_ckdd)));
			setA47_bzpzxdws(Util.null2String(rs.getString(b47_bzpzxdws)));
			setA48_sxbjts(Util.null2String(rs.getString(b48_sxbjts)));
			setA49_sxbjts2(Util.null2String(rs.getString(b49_sxbjts2)));
			setA50_jhl(Util.null2String(rs.getString(b50_jhl)));
			setA51_zhhl(Util.null2String(rs.getString(b51_zhhl)));
			setA52_xhhl(Util.null2String(rs.getString(b52_xhhl)));
			setA53_lsj(Util.null2String(rs.getString(b53_lsj)));
			setA54_zl(Util.null2String(rs.getString(b54_zl)));
			setA55_fpbm(Util.null2String(rs.getString(b55_fpbm)));
			setA56_ysy(JnUtils.getLastname(Util.null2String(rs.getString(b56_ysy))));
			setA57_fhy(JnUtils.getLastname(Util.null2String(rs.getString(b57_fhy))));
			setA58_sflc(JnUtils.getFieldname(t, b58_sflc, Util.null2String(rs.getString(b58_sflc))));
			setA59_sfsrfh(JnUtils.getFieldname(t, b59_sfsrfh, Util.null2String(rs.getString(b59_sfsrfh))));
			String zt = Util.null2String(rs.getString(b60_zt));
			setA60_zt(zt.equals("0")? "1" : "0");
			setA61_zspzm(Util.null2String(rs.getString(b61_zspzm)));
			setA62_dwjg(Util.null2String(rs.getString(b62_dwjg)));
			setA63_sdyy(Util.null2String(rs.getString(b63_sdyy)));

			if(t.equals("formtable_main_849")){	//变更
				String editsql = this.editSqlGroup(tablename);
				rsds.execute(editsql);
				writeLog("修改sql: " + editsql);
			}else{
				String addsql = this.addSqlGroup(tablename);
				rsds.execute(addsql);
				writeLog("新增sql： " + addsql);
			}
			
		}
		return Action.SUCCESS;
	}
	
	

	private String editSqlGroup(String tablename2) {
		StringBuffer key = new StringBuffer("update " + tablename2 + " set ");
		key.append("YXOASYCP_BGRQ='"+JnUtils.getDate("date")+"',").append("YXOASYCP_BGSJ='"+JnUtils.getDate("datetime")+"',");
		key.append("YXOASYCP_DJBH='"+getA1_djbh()+"',");
		key.append("YXOASYCP_CPBH='"+getA2_ypbh()+"',");
		key.append("YXOASYCP_SPM='"+getA3_spbm()+"',");
		key.append("YXOASYCP_JX='"+getA4_jx()+"',");
		key.append("YXOASYCP_BZGG='"+getA5_bzgg()+"',");
		key.append("YXOASYCP_JLDW='"+getA6_zjldw()+"',");
		key.append("YXOASYCP_SCQY='"+getA7_scqy()+"',");
		key.append("YXOASYCP_ZCSB='"+getA8_zcsb()+"',");
		key.append("YXOASYCP_PZWH='"+getA9_pzwh()+"',");
		key.append("YXOASYCP_PZWHXQ='"+getA10_pzwhyxq()+"',");
		key.append("YXOASYCP_GMPBH='"+getA11_gmpzsh()+"',");
		key.append("YXOASYCP_GMPFW='"+getA12_gmpfw()+"',");
		key.append("YXOASYCP_GMPXQ='"+getA13_gmpyxqz()+"',");
		key.append("YXOASYCP_XTY='"+getA14_xty()+"',");
		key.append("YXOASYCP_CCTJ='"+getA15_cctj()+"',");
		key.append("YXOASYCP_WHZQ='"+getA16_yhzq()+"',");
		key.append("YXOASYCP_YXQ='"+getA17_yxqy()+"',");
		key.append("YXOASYCP_SXSDTS='"+getA18_sxsdts1()+"',");
		key.append("YXOASYCP_SFDZJG='"+getA20_sfjdzjgm()+"',");
		key.append("YXOASYCP_BZYFBS='"+getA21_bzyfbs()+"',");
		key.append("YXOASYCP_CD='"+getA22_cd()+"',");
		key.append("YXOASYCP_GD='"+getA23_gd()+"',");
		key.append("YXOASYCP_KD='"+getA24_kd()+"',");
		key.append("YXOASYCP_DBJS='"+getA25_bzdbjs()+"',");
		key.append("YXOASYCP_ZLGLY='"+getA26_zlgly()+"',");
		key.append("YXOASYCP_BGY='"+getA27_bgy()+"',");
		key.append("YXOASYCP_YHY='"+getA28_yhy()+"',");
		key.append("YXOASYCP_SFSRYS='"+getA29_sfsrys()+"',");
		key.append("YXOASYCP_CSJGSJ='"+getA30_csjgsj()+"',");
		key.append("YXOASYCP_ZSPZ='"+getA31_zspzh()+"',");
		key.append("YXOASYCP_ZSPZXS='"+getA32_zsxs()+"',");
		key.append("YXOASYCP_ZDR='"+getA33_zdr()+"',");
		key.append("YXOASYCP_BMMC='"+getA34_bmmc()+"',");
		key.append("YXOASYCP_ZDRQ='"+getA35_zdrq()+"',");
		key.append("YXOASYCP_GSID='"+getA36_gc()+"',");
		key.append("YXOASYCP_TYM='"+getA37_tymc()+"',");
		key.append("YXOASYCP_WLMC='"+getA38_wlms()+"',");
		key.append("YXOASYCP_YWM='"+getA39_ywm()+"',");
		key.append("YXOASYCP_GG='"+getA40_gg()+"',");
		key.append("YXOASYCP_XZ='"+getA41_xz()+"',");
		key.append("YXOASYCP_FJLDW='"+getA42_fzjld()+"',");
		key.append("YXOASYCP_BWTSCQY='"+getA43_bwtscqy()+"',");
		key.append("YXOASYCP_ZLBZ='"+getA44_zlbz()+"',");
		key.append("YXOASYCP_CKID='"+getA45_zccck()+"',");
		key.append("YXOASYCP_CKDD='"+getA46_ckdd()+"',");
		key.append("YXOASYCP_JLDW3='"+getA47_bzpzxdws()+"',");
		key.append("YXOASYCP_SXBJTS1='"+getA48_sxbjts()+"',");
		key.append("YXOASYCP_SXBJTS2='"+getA49_sxbjts2()+"',");
		key.append("YXOASYCP_JHL='"+getA50_jhl()+"',");
		key.append("YXOASYCP_ZHHL='"+getA51_zhhl()+"',");
		key.append("YXOASYCP_XHHL='"+getA52_xhhl()+"',");
		key.append("YXOASYCP_LSDJ='"+getA53_lsj()+"',");
		key.append("YXOASYCP_ZL='"+getA54_zl()+"',");
		key.append("YXOASYCP_FPBM='"+getA55_fpbm()+"',");
		key.append("YXOASYCP_YSY='"+getA56_ysy()+"',");
		key.append("YXOASYCP_FHY='"+getA57_fhy()+"',");
		key.append("YXOASYCP_SFLC='"+getA58_sflc()+"',");
		key.append("YXOASYCP_SFSYFH='"+getA59_sfsrfh()+"',");
		key.append("YXOASYCP_SYBZ='"+getA60_zt()+"',");
		key.append("YXOASYCP_ZSPZMC='"+getA61_zspzm()+"',");
		key.append("YXOASYCP_DWJG='"+getA62_dwjg()+"',");
		key.append("YXOASYCP_SDYY='"+getA63_sdyy()+"',");
		key.append(" where " + b2_ypbh + "='"+getA2_ypbh()+"' ");
		return key.toString();
	}
	
	private String addSqlGroup(String tablename){
		StringBuffer key = new StringBuffer("insert into " + tablename + "(");
		StringBuffer val = new StringBuffer(" values(");
		String sql = "";
		key.append("YXOASYCP_BGRQ,YXOASYCP_BGSJ,YXOASYCP_DJBH,YXOASYCP_CPBH,YXOASYCP_SPM,YXOASYCP_JX,YXOASYCP_BZGG,YXOASYCP_JLDW,YXOASYCP_SCQY,YXOASYCP_ZCSB,YXOASYCP_PZWH,YXOASYCP_PZWHXQ,YXOASYCP_GMPBH,YXOASYCP_GMPFW,YXOASYCP_GMPXQ,YXOASYCP_XTY,YXOASYCP_CCTJ,YXOASYCP_WHZQ,YXOASYCP_YXQ,YXOASYCP_SXSDTS,YXOASYCP_JYFW,YXOASYCP_SFDZJG,YXOASYCP_BZYFBS,YXOASYCP_CD,YXOASYCP_GD,YXOASYCP_KD,YXOASYCP_DBJS,YXOASYCP_ZLGLY,YXOASYCP_BGY,YXOASYCP_YHY,YXOASYCP_SFSRYS,YXOASYCP_CSJGSJ,YXOASYCP_ZSPZ,YXOASYCP_ZSPZXS,YXOASYCP_ZDR,YXOASYCP_BMMC,YXOASYCP_ZDRQ,YXOASYCP_GSID,YXOASYCP_WLMC,YXOASYCP_YWM,YXOASYCP_GG,YXOASYCP_XZ,YXOASYCP_FJLDW,YXOASYCP_BWTSCQY,YXOASYCP_ZLBZ,YXOASYCP_CKID,YXOASYCP_CKDD,YXOASYCP_JLDW3,YXOASYCP_SXBJTS1,YXOASYCP_SXBJTS2,YXOASYCP_JHL,YXOASYCP_ZHHL,YXOASYCP_XHHL,YXOASYCP_LSDJ,YXOASYCP_ZL,YXOASYCP_FPBM,YXOASYCP_YSY,YXOASYCP_FHY,YXOASYCP_SFLC,YXOASYCP_SFSYFH,YXOASYCP_SYBZ,YXOASYCP_ZSPZMC,YXOASYCP_DWJG,YXOASYCP_SDYY)");
		val.append("'" + JnUtils.getDate("date") + "',").append("'" + JnUtils.getDate("datetime") + "',");
		val.append("'"+this.getA1_djbh()+"',");
		val.append("'"+this.getA2_ypbh()+"',");
		val.append("'"+this.getA3_spbm()+"',");
		val.append("'"+this.getA4_jx()+"',");
		val.append("'"+this.getA5_bzgg()+"',");
		val.append("'"+this.getA6_zjldw()+"',");
		val.append("'"+this.getA7_scqy()+"',");
		val.append("'"+this.getA8_zcsb()+"',");
		val.append("'"+this.getA9_pzwh()+"',");
		val.append("'"+this.getA10_pzwhyxq()+"',");
		val.append("'"+this.getA11_gmpzsh()+"',");
		val.append("'"+this.getA12_gmpfw()+"',");
		val.append("'"+this.getA13_gmpyxqz()+"',");
		val.append("'"+this.getA14_xty()+"',");
		val.append("'"+this.getA15_cctj()+"',");
		val.append("'"+this.getA16_yhzq()+"',");
		val.append("'"+this.getA17_yxqy()+"',");
		val.append("'"+this.getA18_sxsdts1()+"',");
		val.append("'',");
		val.append("'"+this.getA20_sfjdzjgm()+"',");
		val.append("'"+this.getA21_bzyfbs()+"',");
		val.append("'"+this.getA22_cd()+"',");
		val.append("'"+this.getA23_gd()+"',");
		val.append("'"+this.getA24_kd()+"',");
		val.append("'"+this.getA25_bzdbjs()+"',");
		val.append("'"+this.getA26_zlgly()+"',");
		val.append("'"+this.getA27_bgy()+"',");
		val.append("'"+this.getA28_yhy()+"',");
		val.append("'"+this.getA29_sfsrys()+"',");
		val.append("'"+this.getA30_csjgsj()+"',");
		val.append("'"+this.getA31_zspzh()+"',");
		val.append("'"+this.getA32_zsxs()+"',");
		val.append("'"+this.getA33_zdr()+"',");
		val.append("'"+this.getA34_bmmc()+"',");
		val.append("'"+this.getA35_zdrq()+"',");
		val.append("'"+this.getA36_gc()+"',");
		val.append("'"+this.getA38_wlms()+"',");
		val.append("'"+this.getA39_ywm()+"',");
		val.append("'"+this.getA40_gg()+"',");
		val.append("'"+this.getA41_xz()+"',");
		val.append("'"+this.getA42_fzjld()+"',");
		val.append("'"+this.getA43_bwtscqy()+"',");
		val.append("'"+this.getA44_zlbz()+"',");
		val.append("'"+this.getA45_zccck()+"',");
		val.append("'"+this.getA46_ckdd()+"',");
		val.append("'"+this.getA47_bzpzxdws()+"',");
		val.append("'"+this.getA48_sxbjts()+"',");
		val.append("'"+this.getA49_sxbjts2()+"',");
		val.append("'"+this.getA50_jhl()+"',");
		val.append("'"+this.getA51_zhhl()+"',");
		val.append("'"+this.getA52_xhhl()+"',");
		val.append("'"+this.getA53_lsj()+"',");
		val.append("'"+this.getA54_zl()+"',");
		val.append("'"+this.getA55_fpbm()+"',");
		val.append("'"+this.getA56_ysy()+"',");
		val.append("'"+this.getA57_fhy()+"',");
		val.append("'"+this.getA58_sflc()+"',");
		val.append("'"+this.getA59_sfsrfh()+"',");
		val.append("'"+this.getA60_zt()+"',");
		val.append("'"+this.getA61_zspzm()+"',");
		val.append("'"+this.getA62_dwjg()+"',");
		val.append("'"+this.getA63_sdyy()+"')");
		sql = key.toString() + val.toString();
		return sql.toString();
	}



	public String getA1_djbh() {
		return a1_djbh;
	}

	public void setA1_djbh(String a1_djbh) {
		this.a1_djbh = a1_djbh;
	}

	public String getA2_ypbh() {
		return a2_ypbh;
	}

	public void setA2_ypbh(String a2_ypbh) {
		this.a2_ypbh = a2_ypbh;
	}

	public String getA3_spbm() {
		return a3_spbm;
	}

	public void setA3_spbm(String a3_spbm) {
		this.a3_spbm = a3_spbm;
	}

	public String getA4_jx() {
		return a4_jx;
	}

	public void setA4_jx(String a4_jx) {
		this.a4_jx = a4_jx;
	}

	public String getA5_bzgg() {
		return a5_bzgg;
	}

	public void setA5_bzgg(String a5_bzgg) {
		this.a5_bzgg = a5_bzgg;
	}

	public String getA6_zjldw() {
		return a6_zjldw;
	}

	public void setA6_zjldw(String a6_zjldw) {
		this.a6_zjldw = a6_zjldw;
	}

	public String getA7_scqy() {
		return a7_scqy;
	}

	public void setA7_scqy(String a7_scqy) {
		this.a7_scqy = a7_scqy;
	}

	public String getA8_zcsb() {
		return a8_zcsb;
	}

	public void setA8_zcsb(String a8_zcsb) {
		this.a8_zcsb = a8_zcsb;
	}

	public String getA9_pzwh() {
		return a9_pzwh;
	}

	public void setA9_pzwh(String a9_pzwh) {
		this.a9_pzwh = a9_pzwh;
	}

	public String getA10_pzwhyxq() {
		return a10_pzwhyxq;
	}

	public void setA10_pzwhyxq(String a10_pzwhyxq) {
		this.a10_pzwhyxq = a10_pzwhyxq;
	}

	public String getA11_gmpzsh() {
		return a11_gmpzsh;
	}

	public void setA11_gmpzsh(String a11_gmpzsh) {
		this.a11_gmpzsh = a11_gmpzsh;
	}

	public String getA12_gmpfw() {
		return a12_gmpfw;
	}

	public void setA12_gmpfw(String a12_gmpfw) {
		this.a12_gmpfw = a12_gmpfw;
	}

	public String getA13_gmpyxqz() {
		return a13_gmpyxqz;
	}

	public void setA13_gmpyxqz(String a13_gmpyxqz) {
		this.a13_gmpyxqz = a13_gmpyxqz;
	}

	public String getA14_xty() {
		return a14_xty;
	}

	public void setA14_xty(String a14_xty) {
		this.a14_xty = a14_xty;
	}

	public String getA15_cctj() {
		return a15_cctj;
	}

	public void setA15_cctj(String a15_cctj) {
		this.a15_cctj = a15_cctj;
	}

	public String getA16_yhzq() {
		return a16_yhzq;
	}

	public void setA16_yhzq(String a16_yhzq) {
		this.a16_yhzq = a16_yhzq;
	}

	public String getA17_yxqy() {
		return a17_yxqy;
	}

	public void setA17_yxqy(String a17_yxqy) {
		this.a17_yxqy = a17_yxqy;
	}

	public String getA18_sxsdts1() {
		return a18_sxsdts1;
	}

	public void setA18_sxsdts1(String a18_sxsdts1) {
		this.a18_sxsdts1 = a18_sxsdts1;
	}

	public String getA20_sfjdzjgm() {
		return a20_sfjdzjgm;
	}

	public void setA20_sfjdzjgm(String a20_sfjdzjgm) {
		this.a20_sfjdzjgm = a20_sfjdzjgm;
	}

	public String getA21_bzyfbs() {
		return a21_bzyfbs;
	}

	public void setA21_bzyfbs(String a21_bzyfbs) {
		this.a21_bzyfbs = a21_bzyfbs;
	}

	public String getA22_cd() {
		return a22_cd;
	}

	public void setA22_cd(String a22_cd) {
		this.a22_cd = a22_cd;
	}

	public String getA23_gd() {
		return a23_gd;
	}

	public void setA23_gd(String a23_gd) {
		this.a23_gd = a23_gd;
	}

	public String getA24_kd() {
		return a24_kd;
	}

	public void setA24_kd(String a24_kd) {
		this.a24_kd = a24_kd;
	}

	public String getA25_bzdbjs() {
		return a25_bzdbjs;
	}

	public void setA25_bzdbjs(String a25_bzdbjs) {
		this.a25_bzdbjs = a25_bzdbjs;
	}

	public String getA26_zlgly() {
		return a26_zlgly;
	}

	public void setA26_zlgly(String a26_zlgly) {
		this.a26_zlgly = a26_zlgly;
	}

	public String getA27_bgy() {
		return a27_bgy;
	}

	public void setA27_bgy(String a27_bgy) {
		this.a27_bgy = a27_bgy;
	}

	public String getA28_yhy() {
		return a28_yhy;
	}

	public void setA28_yhy(String a28_yhy) {
		this.a28_yhy = a28_yhy;
	}

	public String getA29_sfsrys() {
		return a29_sfsrys;
	}

	public void setA29_sfsrys(String a29_sfsrys) {
		this.a29_sfsrys = a29_sfsrys;
	}

	public String getA30_csjgsj() {
		return a30_csjgsj;
	}

	public void setA30_csjgsj(String a30_csjgsj) {
		this.a30_csjgsj = a30_csjgsj;
	}

	public String getA31_zspzh() {
		return a31_zspzh;
	}

	public void setA31_zspzh(String a31_zspzh) {
		this.a31_zspzh = a31_zspzh;
	}

	public String getA32_zsxs() {
		return a32_zsxs;
	}

	public void setA32_zsxs(String a32_zsxs) {
		this.a32_zsxs = a32_zsxs;
	}

	public String getA33_zdr() {
		return a33_zdr;
	}

	public void setA33_zdr(String a33_zdr) {
		this.a33_zdr = a33_zdr;
	}

	public String getA34_bmmc() {
		return a34_bmmc;
	}

	public void setA34_bmmc(String a34_bmmc) {
		this.a34_bmmc = a34_bmmc;
	}

	public String getA35_zdrq() {
		return a35_zdrq;
	}

	public void setA35_zdrq(String a35_zdrq) {
		this.a35_zdrq = a35_zdrq;
	}

	public String getA36_gc() {
		return a36_gc;
	}

	public void setA36_gc(String a36_gc) {
		this.a36_gc = a36_gc;
	}

	public String getA37_tymc() {
		return a37_tymc;
	}

	public void setA37_tymc(String a37_tymc) {
		this.a37_tymc = a37_tymc;
	}

	public String getA38_wlms() {
		return a38_wlms;
	}

	public void setA38_wlms(String a38_wlms) {
		this.a38_wlms = a38_wlms;
	}

	public String getA39_ywm() {
		return a39_ywm;
	}

	public void setA39_ywm(String a39_ywm) {
		this.a39_ywm = a39_ywm;
	}

	public String getA40_gg() {
		return a40_gg;
	}

	public void setA40_gg(String a40_gg) {
		this.a40_gg = a40_gg;
	}

	public String getA41_xz() {
		return a41_xz;
	}

	public void setA41_xz(String a41_xz) {
		this.a41_xz = a41_xz;
	}

	public String getA42_fzjld() {
		return a42_fzjld;
	}

	public void setA42_fzjld(String a42_fzjld) {
		this.a42_fzjld = a42_fzjld;
	}

	public String getA43_bwtscqy() {
		return a43_bwtscqy;
	}

	public void setA43_bwtscqy(String a43_bwtscqy) {
		this.a43_bwtscqy = a43_bwtscqy;
	}

	public String getA44_zlbz() {
		return a44_zlbz;
	}

	public void setA44_zlbz(String a44_zlbz) {
		this.a44_zlbz = a44_zlbz;
	}

	public String getA45_zccck() {
		return a45_zccck;
	}

	public void setA45_zccck(String a45_zccck) {
		this.a45_zccck = a45_zccck;
	}

	public String getA46_ckdd() {
		return a46_ckdd;
	}

	public void setA46_ckdd(String a46_ckdd) {
		this.a46_ckdd = a46_ckdd;
	}

	public String getA47_bzpzxdws() {
		return a47_bzpzxdws;
	}

	public void setA47_bzpzxdws(String a47_bzpzxdws) {
		this.a47_bzpzxdws = a47_bzpzxdws;
	}

	public String getA48_sxbjts() {
		return a48_sxbjts;
	}

	public void setA48_sxbjts(String a48_sxbjts) {
		this.a48_sxbjts = a48_sxbjts;
	}

	public String getA49_sxbjts2() {
		return a49_sxbjts2;
	}

	public void setA49_sxbjts2(String a49_sxbjts2) {
		this.a49_sxbjts2 = a49_sxbjts2;
	}

	public String getA50_jhl() {
		return a50_jhl;
	}

	public void setA50_jhl(String a50_jhl) {
		this.a50_jhl = a50_jhl;
	}

	public String getA51_zhhl() {
		return a51_zhhl;
	}

	public void setA51_zhhl(String a51_zhhl) {
		this.a51_zhhl = a51_zhhl;
	}

	public String getA52_xhhl() {
		return a52_xhhl;
	}

	public void setA52_xhhl(String a52_xhhl) {
		this.a52_xhhl = a52_xhhl;
	}

	public String getA53_lsj() {
		return a53_lsj;
	}

	public void setA53_lsj(String a53_lsj) {
		this.a53_lsj = a53_lsj;
	}

	public String getA54_zl() {
		return a54_zl;
	}

	public void setA54_zl(String a54_zl) {
		this.a54_zl = a54_zl;
	}

	public String getA55_fpbm() {
		return a55_fpbm;
	}

	public void setA55_fpbm(String a55_fpbm) {
		this.a55_fpbm = a55_fpbm;
	}

	public String getA56_ysy() {
		return a56_ysy;
	}

	public void setA56_ysy(String a56_ysy) {
		this.a56_ysy = a56_ysy;
	}

	public String getA57_fhy() {
		return a57_fhy;
	}

	public void setA57_fhy(String a57_fhy) {
		this.a57_fhy = a57_fhy;
	}

	public String getA58_sflc() {
		return a58_sflc;
	}

	public void setA58_sflc(String a58_sflc) {
		this.a58_sflc = a58_sflc;
	}

	public String getA59_sfsrfh() {
		return a59_sfsrfh;
	}

	public void setA59_sfsrfh(String a59_sfsrfh) {
		this.a59_sfsrfh = a59_sfsrfh;
	}

	public String getA60_zt() {
		return a60_zt;
	}

	public void setA60_zt(String a60_zt) {
		this.a60_zt = a60_zt;
	}

	public String getA61_zspzm() {
		return a61_zspzm;
	}

	public void setA61_zspzm(String a61_zspzm) {
		this.a61_zspzm = a61_zspzm;
	}

	public String getA62_dwjg() {
		return a62_dwjg;
	}

	public void setA62_dwjg(String a62_dwjg) {
		this.a62_dwjg = a62_dwjg;
	}

	public String getA63_sdyy() {
		return a63_sdyy;
	}

	public void setA63_sdyy(String a63_sdyy) {
		this.a63_sdyy = a63_sdyy;
	}

	public String getYPMC() {
		return YPMC;
	}

	public void setYPMC(String yPMC) {
		YPMC = yPMC;
	}
}

