package weaverjn.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaverjn.qlzy.sap.WSClientUtils;

public class SBBFAction implements Action {
	private Log log;

	public SBBFAction() {
		this.log = LogFactory.getLog(SBBFAction.class.getName());
	}

	public Log getLog() {
		return this.log;
	}

	public void setLog(Log paramLog) {
		this.log = paramLog;
	}

	public String execute(RequestInfo paramRequestInfo) {
		this.log.info("SBBFAction on request:start!");
		try {
			String workflowid = paramRequestInfo.getWorkflowid();
			String requestid = paramRequestInfo.getRequestid();
			String src = paramRequestInfo.getRequestManager().getSrc();
			if("submit".equals(src)){
				src = "X";
			}else if("reject".equals(src)){
				src = "R";
			}else{
				paramRequestInfo.getRequestManager().setMessageid("90031");
				paramRequestInfo.getRequestManager().setMessagecontent("src错误，请联系管理员"+src);
				return Action.FAILURE_AND_CONTINUE;
			}
			
			RecordSet rs = new RecordSet();
			String maintable = "";
			//查询主表表名
			String strT = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
			rs.executeSql(strT);
			while (rs.next()) {
				maintable = Util.null2String(rs.getString("tablename"));
			}
			
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateStr = sdf.format(calendar.getTime());
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currtime = sdf2.format(calendar.getTime());
			currtime = currtime.replaceAll(" ", "T")+"Z";
			
			String sqlStr = "";
			String gcbm = "";
			
			sqlStr = "select * from "+maintable+" where requestid="+requestid;
			rs.executeSql(sqlStr);
			while(rs.next()){
				gcbm = Util.null2String(rs.getString("gcbm"));
			}
			
			String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">" +
					"<soapenv:Header/><soapenv:Body><erp:MT_OAAsset_Req>" +
					"<ControlInfo>" +
					"<INTF_ID>I0010</INTF_ID>" +
					"<Src_System>OA</Src_System>" +
					"<Dest_System>SAPERP</Dest_System>" +
					"<Company_Code>"+gcbm+"</Company_Code>" +
					"<Send_Time>"+dateStr+"</Send_Time>" +
					"</ControlInfo>"+
					"<OAService_Rel>"+
		            "<REQ_NO>"+requestid+"</REQ_NO>"+
		            "<PROCESS_RESULT>"+src+"</PROCESS_RESULT>"+
		            "<NOTE></NOTE>"+
		            "<RELEASE_TIME>"+currtime+"</RELEASE_TIME>"+
		            "<Addi_Fields>"+
		            "   <Additional1></Additional1>"+
		            "   <Additional2></Additional2>"+
		            "   <Additional3></Additional3>"+
		            "   <Additional4></Additional4>"+
		            "</Addi_Fields>"+
		            "</OAService_Rel>"+
					"</erp:MT_OAAsset_Req>" +
					"</soapenv:Body>" +
					"</soapenv:Envelope>";

			HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
			httpHeaderParm.put("instId", "10062");
			httpHeaderParm.put("repairType", "RP");
			String s = WSClientUtils.callWebServiceWithHttpHeaderParm(request,
					   "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_OAServiceRel_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/",
					   httpHeaderParm);
			System.out.println(s);
			Document dom=DocumentHelper.parseText(s);
			Element root=dom.getRootElement();
	    	Iterator iters = root.elementIterator("Body");
	        while (iters.hasNext()) {
	            Element recordEles = (Element) iters.next();
	            Iterator iterss = recordEles.elementIterator("MT_OAAsset_Ret");
	            while(iterss.hasNext()){
	            	Element recordEless = (Element) iterss.next();
		            Iterator itersElIterator = recordEless.elementIterator("Return_List");
		            while (itersElIterator.hasNext()) {
		                Element itemEle = (Element) itersElIterator.next();
		                String MSG_TYPE = itemEle.elementTextTrim("MSG_TYPE");
		                String MESSAGE = itemEle.elementTextTrim("MESSAGE");
		                //String REQNO = itemEle.elementTextTrim("MESSAGE");
		                if("E".equals(MSG_TYPE)){
		                	paramRequestInfo.getRequestManager().setMessageid("90031");
							paramRequestInfo.getRequestManager().setMessagecontent(MESSAGE);
							return Action.FAILURE_AND_CONTINUE;
		                }
		            }
	            }
	        }
			
			
		}  catch (Exception e) {
			e.printStackTrace();
			paramRequestInfo.getRequestManager().setMessageid("90031");
			paramRequestInfo.getRequestManager().setMessagecontent(e.getMessage());
			return Action.FAILURE_AND_CONTINUE;
		}
		return Action.SUCCESS;
	}
	
	private String getSelectName(String fieldid,String selectvalue){
		if("".equals(fieldid)||"".equals(selectvalue)){
			return "";
		}
		RecordSet rs = new RecordSet();
		rs.executeSql("select selectname from workflow_selectitem where fieldid="+fieldid+" and selectvalue="+selectvalue);
		if(rs.next()){
			return Util.null2String(rs.getString("selectname"));
		}else{
			return "";
		}
	}
	
	private String getEmpName(String id){
		if("".equals(id)){
			return "";
		}
		RecordSet rs = new RecordSet();
		rs.executeSql("select lastname from hrmresource where id="+id);
		if(rs.next()){
			return Util.null2String(rs.getString("lastname"));
		}else{
			return "";
		}
	}
	
	public static void main(String[] args) throws DocumentException {
		//Test.init();
//		String s = "<Envelope><Header/><Body><MT_OAAsset_Ret><Return_List><MSG_TYPE>W</MSG_TYPE><MESSAGE>太多了</MESSAGE><REQNO/></Return_List></MT_OAAsset_Ret></Body></Envelope>";
//		Document dom=DocumentHelper.parseText(s);
//		Element root=dom.getRootElement();
//    	Iterator iters = root.elementIterator("Body");
//        while (iters.hasNext()) {
//            Element recordEles = (Element) iters.next();
//            Iterator iterss = recordEles.elementIterator("MT_OAAsset_Ret");
//            while(iterss.hasNext()){
//            	Element recordEless = (Element) iterss.next();
//	            Iterator itersElIterator = recordEless.elementIterator("Return_List");
//	            while (itersElIterator.hasNext()) {
//	                Element itemEle = (Element) itersElIterator.next();
//	                String MSG_TYPE = itemEle.elementTextTrim("MSG_TYPE");
//	                String MESSAGE = itemEle.elementTextTrim("MESSAGE");
//	                String REQNO = itemEle.elementTextTrim("MESSAGE");
//	                System.out.println(MSG_TYPE);
//	                System.out.println(MESSAGE);
//	            }
//            }
//        }
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdf.format(calendar.getTime());
		System.out.println(dateStr.replaceAll(" ", "T")+"Z");
	}
}