package weaverjn.change.action;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.DetailTable;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.Row;
import weaver.workflow.request.RequestManager;
import weaverjn.action.integration.utils;
import weaverjn.schedule.JnUtil;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 运输单位到期变更，校验输入的时间。
 * 
 * @author songqi
 * @tel 13256247773 2017年5月17日 下午4:45:54
 */
public class TransportChangeAction extends BaseBean implements Action {
	private static final String DATEREGEX = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
	private String VKROG;
	private String bh;
	private String bgmx;
	private String bgqnr;
	private String bghnr;
	private static String tname = "uf_wrwtcyysdwzl";
	private static String className = TransportChangeAction.class.getName(); 

	@Override
	public String execute(RequestInfo requestInfo) {
		List<TransportChangeAction> list = new ArrayList<TransportChangeAction>();
		// 主表：
		RequestManager requestManager = requestInfo.getRequestManager();
		Map<String, String> mainTableData = utils.getMainTableData(requestInfo.getMainTableInfo());
		RecordSet rs = new RecordSet();
		String sql = "select cydwbh from " + tname + " where id=";
		// 取明细数据
		DetailTable[] detailtable = requestInfo.getDetailTableInfo().getDetailTable(); // 获取所有明细表
		if (detailtable.length > 0) {
			for (int i = 0; i < detailtable.length; i++) {
				DetailTable dt = detailtable[i];// 指定明细表
				Row[] s = dt.getRow(); // 当前明细表的所有数据,按行存储
				for (int j = 0; j < s.length; j++) {
					Row r = s[j]; // 指定行
					Cell c[] = r.getCell(); // 每行数据再按列存储
					TransportChangeAction dc = new TransportChangeAction();
					sql += "'"+mainTableData.get("ysdwbh")+"'";
					writeLog("查询运输单位编号的sql: " + sql);
					rs.executeSql(sql);
					rs.next();
					dc.setBh(Util.null2String(rs.getString("cydwbh")));
					for (int k = 0; k < c.length; k++) {
						Cell c1 = c[k]; // 指定列
						String name = c1.getName(); // 明细字段名称
						String value = c1.getValue(); // 明细字段的值
						if (name.equals("bgmx")) {
							dc.setBgmx(value);
						}
						if (name.equals("bgqnr")) {
							dc.setBgqnr(value);
						}
						if (name.equals("bghnr")) {
							dc.setBghnr(value);
						}
					}
					if (null != dc.getBh() && !dc.getBh().equals("")) {
						list.add(dc);
					}
				}
			}
		}

		for(TransportChangeAction l : list){
			
			// 变更项目 yxqz  yxq1 njrq
			if(l.getBgmx().equals("yxqz")){
				if(l.getBghnr().matches(DATEREGEX)){
					updateSAP(l.getBh());
					updateOA(l.getBh());
				}else{
					requestManager.setMessageid("返回信息");
					requestManager.setMessagecontent("日期格式不正确!");
				}
			}
			
			if(l.getBgmx().equals("yxq1")){
				if(l.getBghnr().matches(DATEREGEX)){
					updateSAP(l.getBh());
					updateOA(l.getBh());
				}else{
					requestManager.setMessageid("返回信息");
					requestManager.setMessagecontent("日期格式不正确!");
				}
			}
			
			if(l.getBgmx().equals("njrq")){
				if(l.getBghnr().matches(DATEREGEX)){
					updateSAP(l.getBh());
					updateOA(l.getBh());
				}else{
					requestManager.setMessageid("返回信息");
					requestManager.setMessagecontent("日期格式不正确!");
				}
			}
			
		}
		return SUCCESS;
	}

	private void updateOA(String bh2) {
		RecordSet rs = new RecordSet();
		String sql = "update " + tname + " set zt='0' where id='"+bh2+"'";
		boolean f = rs.execute(sql);
		if(f)
			JnUtil.writeLog(className, "修改成功！运输单位主键为：" + bh2);
		else
			JnUtil.writeLog(className, "修改失败！运输单位主键为：" + bh2);
	}

	/**
	 * <br/>
	 * 2017年5月17日 下午4:44:51<br/>
	 * 
	 * @param mainTableData
	 * @return
	 */
	private void updateSAP(String bh) {
		String tag = "erp:MT_Transport_Enterprise_State";
		String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <"+tag+">\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID></INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <Transport_Enterprise_State>\n" +
                "            <KUNNR>" + bh + "</KUNNR>\n" +
                "            <VKROG>" + this.getVKROG() + "</VKROG>\n" +
                "            <STATE>Y</STATE>\n" +
                "         </Transport_Enterprise_State>\n" +
                "      </"+tag+">\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
		writeLog("请求sap信息： " + soapHttpRequest);
		String u = utils.getUsername();
		String p = utils.getPassword();
		String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", "TransportEnterpriseStateAction");
		JnUtil.writeLog(className, "sap的URL： " + endpoint);
		String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, u,p);
		writeLog("sap返回信息： " + soapHttpResponse);
	}

	

	public String getVKROG() {
		return VKROG;
	}

	public void setVKROG(String VKROG) {
		this.VKROG = VKROG;
	}

	public String getBh() {
		return bh;
	}

	public void setBh(String bh) {
		this.bh = bh;
	}

	public String getBgmx() {
		return bgmx;
	}

	public void setBgmx(String bgmx) {
		this.bgmx = bgmx;
	}

	public String getBgqnr() {
		return bgqnr;
	}

	public void setBgqnr(String bgqnr) {
		this.bgqnr = bgqnr;
	}

	public String getBghnr() {
		return bghnr;
	}

	public void setBghnr(String bghnr) {
		this.bghnr = bghnr;
	}
}
