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
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaverjn.qlzy.sap.WSClientUtils;

public class GDZCCGAction extends BaseBean implements Action {
	public String execute(RequestInfo paramRequestInfo) {
		log("---->GDZCCGAction run");
		try {
			String workflowid = paramRequestInfo.getWorkflowid();
			String requestid = paramRequestInfo.getRequestid();

			RecordSet rs = new RecordSet();
			RecordSet rse = new RecordSet();
			String maintable = "";
			//查询主表表名
			String strT = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
			rs.executeSql(strT);
			while (rs.next()) {
				maintable = Util.null2String(rs.getString("tablename"));
			}

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = sdf.format(calendar.getTime());

			String paramstr = "";
			String sqlStr = "";
			String gcbm = "";
			String mainid = "";
			String sqr = "";
			sqlStr = "select * from " + maintable + " where requestid=" + requestid;
			rs.executeSql(sqlStr);
			while (rs.next()) {
				gcbm = Util.null2String(rs.getString("gcbm"));
				mainid = Util.null2String(rs.getString("id"));
				sqr = getEmpName(Util.null2String(rs.getString("sqr")));
			}

			sqlStr = "select * from " + maintable + "_dt1 where mainid=" + mainid + " order by id asc";
			rs.executeSql(sqlStr);
			String id = "";
			String ASSET_DESC = "";
			String EQTYP = "";
			String EQART = "";
			String TYPBZ = "";
			String KOSTL = "";
			String QUANTITY = "";
			String DELIVERY_DATE = "";
			String PREIS = "";
			String WAERS = "";
			int i = 1;
			String clzt = "";
			while (rs.next()) {
//				clzt = Util.null2String(rs.getString("clzt"));
//				if("1".equals(clzt)||"3".equals(clzt)){
//					i++;
//					continue;
//				}
				//id = Util.null2String(rs.getString("id"));
				ASSET_DESC = Util.null2String(rs.getString("sbmc")); //设备名称 资产描述
				EQTYP = getSelectName("24022", Util.null2String(rs.getString("sbzlmc")));//设备种类名称
				EQART = getSelectName("24021", Util.null2String(rs.getString("sbflmc")));//设备分类名称
				TYPBZ = Util.null2String(rs.getString("ggxh")); //规格型号
				KOSTL = Util.null2String(rs.getString("cbzx")); //成本中心
				QUANTITY = Util.null2String(rs.getString("sl")); //数量
				DELIVERY_DATE = Util.null2String(rs.getString("yqgjrq")); //要求购进日期
				PREIS = Util.null2String(rs.getString("ybzje")); //单价-oa本币总金额
				WAERS = Util.null2String(rs.getString("bz")); //币种
				paramstr = paramstr +
						"<OAAsset_Req>" +
						"<OAREQ_NO>" + requestid + "</OAREQ_NO>" +
						"<OAREQ_ITEM>" + i + "</OAREQ_ITEM>" +
						"<ASSET_DESC>" + ASSET_DESC + "</ASSET_DESC>" +
						"<EQTYP>" + EQTYP + "</EQTYP>" +
						"<EQART>" + EQART + "</EQART>" +
						"<EQKTX>" + ASSET_DESC + "</EQKTX>" +
						"<TYPBZ>" + TYPBZ + "</TYPBZ>" +
						"<KOSTL>" + KOSTL + "</KOSTL>" +
						"<PLANT>" + gcbm + "</PLANT>" +
						"<QUANTITY>" + QUANTITY + "</QUANTITY>" +
						"<DELIVERY_DATE>" + DELIVERY_DATE + "</DELIVERY_DATE>" +
						"<PREIS>" + PREIS + "</PREIS>" +
						"<WAERS>" + WAERS + "</WAERS>" +
						"<REQUISITIONER>" + sqr + "</REQUISITIONER>" +
						"<NOTES></NOTES>" +
						"<AddiInfo>" +
						"<Additional1></Additional1>" +
						"<Additional2></Additional2>" +
						"<Additional3></Additional3>" +
						"<Additional4></Additional4>" +
						"</AddiInfo>" +
						"</OAAsset_Req>";
				i++;
			}

			String reStr = "";
			String status = "";
			String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">" +
					"<soapenv:Header/><soapenv:Body><erp:MT_OAAsset_Req>" +
					"<ControlInfo>" +
					"<INTF_ID>I0008</INTF_ID>" +
					"<Src_System>OA</Src_System>" +
					"<Dest_System>SAPERP</Dest_System>" +
					"<Company_Code>" + gcbm + "</Company_Code>" +
					"<Send_Time>" + dateStr + "</Send_Time>" +
					"</ControlInfo>" + paramstr +
					"</erp:MT_OAAsset_Req>" +
					"</soapenv:Body>" +
					"</soapenv:Envelope>";

			log(request);
			HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
			httpHeaderParm.put("instId", "10062");
			httpHeaderParm.put("repairType", "RP");
			String s = WSClientUtils.callWebServiceWithHttpHeaderParm(request,
					"http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_OAAsset_Req_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/",
					httpHeaderParm);
			log(s);
			Document dom = DocumentHelper.parseText(s);
			Element root = dom.getRootElement();
			Iterator iters = root.elementIterator("Body");
			while (iters.hasNext()) {
				Element recordEles = (Element) iters.next();
				Iterator iterss = recordEles.elementIterator("MT_OAAsset_Ret");
				while (iterss.hasNext()) {
					Element recordEless = (Element) iterss.next();
					Iterator itersElIterator = recordEless.elementIterator("Return_List");
					while (itersElIterator.hasNext()) {
						Element itemEle = (Element) itersElIterator.next();
						String MSG_TYPE = itemEle.elementTextTrim("MSG_TYPE");
						String MESSAGE = itemEle.elementTextTrim("MESSAGE");
						//String REQNO = itemEle.elementTextTrim("MESSAGE");
						if ("E".equals(MSG_TYPE)) {
							paramRequestInfo.getRequestManager().setMessageid("90031");
							paramRequestInfo.getRequestManager().setMessagecontent(MESSAGE);
							return Action.FAILURE_AND_CONTINUE;
						}
//		                if("E".equals(MSG_TYPE)){
//		                	reStr += MESSAGE;
//		                	status = "2";
//		                }
//		                if("S".equals(MSG_TYPE)){
//		                	status = "1";
//		                }
//		                if("W".equals(MSG_TYPE)){
//		                	status = "2";
//		                }
//		                rse.execute("update "+maintable+"_dt1 set clzt="+status+",fkxx='"+MESSAGE+"' where id="+REQNO);
					}
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
			paramRequestInfo.getRequestManager().setMessageid("90031");
			paramRequestInfo.getRequestManager().setMessagecontent(e.getMessage());
			return Action.FAILURE_AND_CONTINUE;
		}
		return Action.SUCCESS;
	}

	private String getSelectName(String fieldid, String selectvalue) {
		if ("".equals(fieldid) || "".equals(selectvalue)) {
			return "";
		}
		RecordSet rs = new RecordSet();
		rs.executeSql("select selectname from workflow_selectitem where fieldid=" + fieldid + " and selectvalue=" + selectvalue);
		if (rs.next()) {
			return Util.null2String(rs.getString("selectname"));
		} else {
			return "";
		}
	}

	private String getEmpName(String id) {
		if ("".equals(id)) {
			return "";
		}
		RecordSet rs = new RecordSet();
		rs.executeSql("select lastname from hrmresource where id=" + id);
		if (rs.next()) {
			return Util.null2String(rs.getString("lastname"));
		} else {
			return "";
		}
	}

	private void log(Object o) {
		writeLog(o);
		System.out.println(o);
	}
}