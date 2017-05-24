package weaverjn.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;
import weaverjn.action.integration.utils;
import weaverjn.utils.Workflow;
/**
 * 每年3月1号自动发起流程：购货单位评审表
 * 购货单位状态(ghdwzt):不是禁用的，全部发起
 * @author songqi
 * @tel 13256247773
 * 2017年5月10日 上午11:37:42
 */
public class PurchaseCorpReview extends BaseCronJob{

	@Override
	public void execute() {
		doit();
	}
	public void doit(){
		Map<String,String> table = new HashMap<String,String>();
		// 主表个数，发起次数
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		// dt1：仓库 dt2:经营范围
		String tname = "uf_wrghdwzl",dt2name = "uf_wrghdwzl_dt2";
		String isNot = "3";	// 
		String sql0 = "select id,pjksrq,pjjsrq,pjmd,GHFBH,ghfmc,YYZZZCH,KHLX,FDDBR,QYFZR,dz,xkzbh,XKZYXQZ,GSPZSBH,GSPYXQZ,yxqz from " + tname
			+ " where ghdwzt<>'" + isNot + "' ";
		writeLog("查询主表的sql： " + sql0);
		RecordSet rs = new RecordSet();
		rs.executeSql(sql0);
		String date = utils.getCurrentDate().substring(0, 5);
		String start = date + "03-01";
		String end = date + "04-30" ;
		while(rs.next()){
			table.put("id", Util.null2String(rs.getString("id")));
			table.put("pjksrq", Util.null2String(start));
			table.put("pjjsrq", Util.null2String(end));
			table.put("pjmd", Util.null2String(rs.getString("pjmd")));
			String bh = Util.null2String(rs.getString("GHFBH"));
			table.put("GHFBH", bh);
			table.put("ghfmc", Util.null2String(rs.getString("ghfmc")));
			table.put("YYZZZCH", Util.null2String(rs.getString("YYZZZCH")));
			table.put("KHLX", Util.null2String(rs.getString("KHLX")));
			table.put("FDDBR", Util.null2String(rs.getString("FDDBR")));
			table.put("QYFZR", Util.null2String(rs.getString("QYFZR")));
			table.put("dz", Util.null2String(rs.getString("dz")));
			table.put("xkzbh", Util.null2String(rs.getString("xkzbh")));
			table.put("XKZYXQZ", Util.null2String(rs.getString("XKZYXQZ")));
			table.put("GSPZSBH", Util.null2String(rs.getString("GSPZSBH")));
			table.put("GSPYXQZ", Util.null2String(rs.getString("GSPYXQZ")));
			table.put("yxqz", Util.null2String(rs.getString("yxqz")));
			
			// 根据编号查询RFC数据
			String strFunc = "ZRFC_CUSTOMER_REPUTATION";
			JCO.Client myConnection = JCO.createClient(this.getLogonProperties());
	        myConnection.connect();
	        JCO.Repository myRepository = new JCO.Repository("Repository", myConnection);
	        IFunctionTemplate ft = myRepository.getFunctionTemplate(strFunc.toUpperCase());
	        JCO.Function funGetList = ft.getFunction();
	        JCO.ParameterList input = funGetList.getImportParameterList();
	        
	        input.setValue(bh, "I_KUNNR");
	        input.setValue(utils.getCurrentDate(), "I_DATE");
	        input.setValue("", "I_VKORG");
	       
	        myConnection.execute(funGetList);
	        JCO.Table T_JXQ = funGetList.getTableParameterList().getTable("T_JXQ");
	        for (int i = 0; i < T_JXQ.getNumRows(); i++) {
	        	
	        	table.put("NXSE1", Util.null2String(T_JXQ.getString("ZXSE_ND")));
	        	table.put("NHKJE", Util.null2String(T_JXQ.getString("ZHKE_ND")));
	        	table.put("NXSPGS", Util.null2String(T_JXQ.getString("ZXSPGSL")));
	        	table.put("NCGPZP", Util.null2String(T_JXQ.getString("ZCGPZPC")));
	        	table.put("ZKHDJ", Util.null2String(T_JXQ.getString("KHDJ")));
	        	
	        }
			list.add(table);
			writeLog("table大小应为21，实际为：" + table.size() + "  list大小：" + list.size());
		}
		
		String sql1 = "select t2.jyfw jyfw,t2.mc mc from " + tname + " t1 left join " + dt2name
				+ " t2 on t1.id=t2.mainid where t1.ghdwzt<>'" + isNot + "' ";
		writeLog("明细表的sql： " + sql1);
		try {
			for(int i=0;i<list.size();i++){
				List<List<Map<String, String>>> lists = new ArrayList<List<Map<String,String>>>();
				List<Map<String, String>> l = new ArrayList<Map<String,String>>();
				String id = list.get(i).get("id");
				sql1 += " and t1.id='" + id + "'";
				rs.executeSql(sql1);
				while(rs.next()){
					Map<String,String> map = new HashMap<String, String>();
					map.put("jyfw", Util.null2String(rs.getString("jyfw")));
					map.put("mc", Util.null2String(rs.getString("mc")));
					l.add(map);
				}
				lists.add(l);
				Workflow workflow = Workflow.createInstance("1", "3094", "购货单位评审表", list.get(i),lists);
				workflow.next();
				writeLog("事项已发起：  购货单位评审表>>>>>>>>");
			}
		} catch (Exception e) {
//			e.printStackTrace();
			writeLog("catch信息： " + e.getMessage());
		}
	}

	private Properties getLogonProperties() {
		Properties properties = new Properties();
		properties.put("jco.client.ashost", "192.168.95.20"); // 系统的IP地址
		properties.put("jco.client.client", "310"); // 要登录的客户端
		properties.put("jco.client.sysnr", "00"); // 系统编号
		properties.put("jco.client.langu", "ZH"); // 系统语言
		properties.put("jco.client.user", "qilu-abap03"); // 登录用户名
		properties.put("jco.client.passwd", "lvdx2019"); // 用户登录口令
		return properties;
	}
	
	
	
	public void writeLog(Object obj) {
		String str = getClass().getName();
		org.apache.commons.logging.Log jnlog = org.apache.commons.logging.LogFactory.getLog(str);
		if ((obj instanceof Exception))
			jnlog.error(str, (Exception) obj);
		else
			jnlog.error(obj);
	}
	
	public static void main(String[] args) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("1", "111");
		map.put("2", "222");
		map.put("3", "333");
		System.out.println(map.get("1"));
	}
	
	
}
