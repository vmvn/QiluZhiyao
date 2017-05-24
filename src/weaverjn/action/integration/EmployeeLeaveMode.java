package weaverjn.action.integration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaverjn.schedule.JnUtil;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

/**
 * 员工离职，传sap状态
 * @author songqi
 * @tel 13256247773
 * 2017年5月23日 上午11:42:48
 */
public class EmployeeLeaveMode extends BaseBean implements Action {

	private String vkorg;
	private String PROCESS_TYPE;
	private static String className = EmployeeLeaveMode.class.getName();
	
	@Override
	public String execute(RequestInfo requestInfo) {

		String billId = requestInfo.getRequestid();
        String moduleId = requestInfo.getWorkflowid();
        String sql = "select b.tablename,b.id from modeinfo a,workflow_bill b where a.formid = b.id and a.id = " + moduleId;
        RecordSet rs = new RecordSet();
        RecordSet rs2 = new RecordSet();
        rs.executeSql(sql);
        if (rs.next()) {
            String t = Util.null2String(rs.getString("tablename"));
            String sql2 = "select * from " + t + " where id=" + billId;
            writeLog("员工离职查询sql： " + sql2);
            rs2.executeSql(sql2);
            rs2.next();
			String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n "
					+ "   <soapenv:Header/>\n"
					+ "   <soapenv:Body>\n"
					+ "      <erp:MT_Employee_Lock_Req>\n"
					+ "         <!--1 or more repetitions:-->\n"
					+ "         <Employee_Lock_Info>\n"
					+ "            <PROCESS_TYPE>"+PROCESS_TYPE+"</PROCESS_TYPE>\n"
					+ "            <ORGAN>"+vkorg+"</ORGAN>\n"
					+ "            <ZSFZ>"+Util.null2String(rs2.getString("sfzh"))+"</ZSFZ>\n"
					+ "            <STATE>X</STATE>\n"
					+ "            <Add_fields>\n"
					+ "               <Additional1></Additional1>\n"
					+ "               <Additional2></Additional2>\n"
					+ "               <Additional3></Additional3>\n"
					+ "               <Additional4></Additional4>\n"
					+ "            </Add_fields>\n"
					+ "         </Employee_Lock_Info>\n "
					+ "      </erp:MT_Employee_Lock_Req>\n"
					+ "   </soapenv:Body>\n"
					+ "</soapenv:Envelope> ";
            String username = utils.getUsername();
            String password = utils.getPassword();
            writeLog("员工离职请求sap地址： " + request);
            String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
            String soapResponse = WSClientUtils.callWebService(request, endpoint, username, password);
            JnUtil.writeDB(className, className, request, soapResponse, endpoint, "", "");
            writeLog("sap返回信息： " + soapResponse);
//            RET_MSG msg = parse(soapResponse);
        }
		return Action.SUCCESS;
	}

	private RET_MSG parse(String soapResponse) {
		RET_MSG msg = null;
		Document dom;
		try {
			dom = DocumentHelper.parseText(soapResponse);
			Element root = dom.getRootElement();
			Element e = root.element("Body").element("MT_Employee_Lock_Ret").element("Ret_Msg");
			msg = new RET_MSG();
			msg.setMSG_TYPE(e.element("Ret_Msg_Info").elementText("MSG_TYPE"));
			msg.setMESSAGE(e.element("Ret_Msg_Info").elementText("MESSAGE"));
		} catch (DocumentException e) {
			writeLog("员工离职解析xml错误： " + e.getMessage());
		}
		return msg;
	}

}
