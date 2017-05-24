package weaverjn.schedule;

import java.util.Calendar;
import java.util.Date;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;
import weaverjn.action.integration.utils;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

/**
 * 定时任务，运输单位到期自动锁定
 * @author songqi
 * @tel 13256247773
 * 2017年5月17日 下午4:45:54
 */
public class TransportSchedule extends BaseCronJob {
    
    private String VKROG = 1620+"";

    private static final String tname = "uf_wrwtcyysdwzl";
    private static String className = TransportSchedule.class.getName();
    
	@Override
	public void execute() {
		doit();
	}

	private void doit() {
		JnUtil.writeLog(className, "定时任务运输单位开始执行！！！");
		String today = JnUtil.date2String(new Date());
		String sql = "select id,zdr,yxqz,yxq1,njrq,zt,cydwbh from " + tname 
				+ " where zt='0' and (yxq1 <= '" + today + "' or yxqz<='"+today+"' or njrq<='"+today+"')";
		RecordSet rs = new RecordSet();
		rs.executeSql(sql);
		JnUtil.writeLog(className, "查询时间到期的运输单位sql:" + sql);
		while(rs.next()){
			String id = Util.null2String(rs.getString("id"));
			String yxqz = Util.null2String(rs.getString("yxqz"));
			String yxq1 = Util.null2String(rs.getString("yxq1"));
			String njrq = Util.null2String(rs.getString("njrq"));
			String cydwbh = Util.null2String(rs.getString("cydwbh"));
			Date d = Calendar.getInstance().getTime();
			long cha1 = JnUtil.calculateTime(d, JnUtil.string2Date(yxq1));
			long cha2 = JnUtil.calculateTime(d, JnUtil.string2Date(yxqz));
			long cha3 = JnUtil.calculateTime(d, JnUtil.string2Date(njrq));
			
			if(cha1 <=0 || cha2<=0 || cha3 <= 0){
				sapStatus(cydwbh);
				lockStatus(id,cydwbh);
			}
		}
	}
	
	private void sapStatus(String cydwbh) {
		String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_Transport_Enterprise_State>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID></INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <Transport_Enterprise_State>\n" +
                "            <KUNNR>" + cydwbh + "</KUNNR>\n" +
                "            <VKROG>" + VKROG + "</VKROG>\n" +
                "            <STATE>N</STATE>\n" +
                "         </Transport_Enterprise_State>\n" +
                "      </erp:MT_Transport_Enterprise_State>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        JnUtil.writeLog(className,"定时任务运输单位传sap的报文：" + soapHttpRequest);
        String username = utils.getUsername();
        String password = utils.getPassword();
        String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", "TransportEnterpriseStateAction");
        JnUtil.writeLog(className, "定时任务运输单位传sap的URL： " + endpoint);
        String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, username, password);
        JnUtil.writeLog(className,"定时任务运输单位sap返回报文：" + soapHttpResponse);
	}

	private void lockStatus(String id,String cydwbh) {
		RecordSet rs = new RecordSet();
		boolean f = rs.execute("update " + tname + " set zt='1' where id='"+id+"'");
		if(f)
			JnUtil.writeLog(className, "定时任务运输单位锁定成功！主键为：" + id);
		else
			JnUtil.writeLog(className, "定时任务运输单位锁定失败！主键为：" + id + ",名称：" + cydwbh);
	}

}
