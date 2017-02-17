package weaverjn.action;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaverjn.qlzy.sap.WSClientUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

//固定资产采购
public class GDZCCGAction extends BaseBean implements Action {
	public String execute(RequestInfo paramRequestInfo) {
		String src = paramRequestInfo.getRequestManager().getSrc();
		if (!src.equals("reject")) {
			try {
				String workflowid = paramRequestInfo.getWorkflowid();
				String requestid = paramRequestInfo.getRequestid();

				RecordSet rs = new RecordSet();
				String maintable = "";
				//查询主表表名
				String sql = "select b.tablename,b.id from workflow_base a,workflow_bill b where a.formid = b.id and a.id = " + workflowid;
				rs.executeSql(sql);
				while (rs.next()) {
					maintable = Util.null2String(rs.getString("tablename"));
				}

				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String dateStr = sdf.format(calendar.getTime());

				String paramstr = "";
				String sqlStr = "";
				String mainid = "";
				String sqr = "";
				sqlStr = "select * from " + maintable + " where requestid=" + requestid;
				rs.executeSql(sqlStr);
				while (rs.next()) {
					mainid = Util.null2String(rs.getString("id"));
					sqr = Util.null2String(rs.getString("sqr"));
				}

				sqlStr = "select * from " + maintable + "_dt1 where mainid=" + mainid + " order by id asc";
				rs.executeSql(sqlStr);
				String ASSET_DESC;
				String EQTYP;
				String EQART;
				String TYPBZ;
				String KOSTL;
				String QUANTITY;
				String DELIVERY_DATE;
				String PREIS;
				String WAERS;
				String PLANT = "";
				String OAREQ_NO = "" + requestid;
				String OAREQ_ITEM;
				String REQUISITIONER = getLastName(sqr);;

				while (rs.next()) {
					OAREQ_ITEM = rs.getString("id");
					PLANT = getCompanyCode(Util.null2String(rs.getString("gc")));
					ASSET_DESC = Util.null2String(rs.getString("sbmc")); //设备名称 资产描述

					EQTYP = getSelectName("24022", Util.null2String(rs.getString("sbzlmc")));//设备种类名称
					EQART = getSelectName("24021", Util.null2String(rs.getString("sbflmc")));//设备分类名称
					//new
					EQTYP = getEQTYP(Util.null2String(rs.getString("sbzl")));
					EQART = getEQART(Util.null2String(rs.getString("sbfl")));

					TYPBZ = Util.null2String(rs.getString("ggxh")); //规格型号
					KOSTL = Util.null2String(rs.getString("cbzxsap")); //成本中心
					QUANTITY = Util.null2String(rs.getString("sl")); //数量
					DELIVERY_DATE = Util.null2String(rs.getString("yqgjrq")); //要求购进日期
					PREIS = Util.null2String(rs.getString("ybzjey")); //单价-oa本币总金额
					WAERS = getCurrency(Util.null2String(rs.getString("bzllan"))); //币种

					String TPLNR = Util.null2String(rs.getString("gnwz"));
					String ABCKZ = getSelectName("25329", Util.null2String(rs.getString("sfgmpsb")));

					paramstr = paramstr +
							"<OAAsset_Req>" +
							"<OAREQ_NO>" + OAREQ_NO + "</OAREQ_NO>" +
							"<OAREQ_ITEM>" + OAREQ_ITEM + "</OAREQ_ITEM>" +
							"<ASSET_DESC>" + ASSET_DESC + "</ASSET_DESC>" +
							"<EQTYP>" + EQTYP + "</EQTYP>" +
							"<EQART>" + EQART + "</EQART>" +
							"<EQKTX>" + ASSET_DESC + "</EQKTX>" +
							"<TYPBZ>" + TYPBZ + "</TYPBZ>" +
							"<KOSTL>" + KOSTL + "</KOSTL>" +
							"<PLANT>" + PLANT + "</PLANT>" +
							"<QUANTITY>" + QUANTITY + "</QUANTITY>" +
							"<DELIVERY_DATE>" + DELIVERY_DATE + "</DELIVERY_DATE>" +
							"<PREIS>" + PREIS + "</PREIS>" +
							"<WAERS>" + WAERS + "</WAERS>" +
							"<REQUISITIONER>" + REQUISITIONER + "</REQUISITIONER>" +
							"<TPLNR>" + TPLNR + "</TPLNR>" +
							"<ABCKZ>" + ABCKZ + "</ABCKZ>" +
							"<NOTES></NOTES>" +
							"<AddiInfo>" +
							"<Additional1></Additional1>" +
							"<Additional2></Additional2>" +
							"<Additional3></Additional3>" +
							"<Additional4></Additional4>" +
							"</AddiInfo>" +
							"</OAAsset_Req>";
				}

				String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">" +
						"<soapenv:Header/><soapenv:Body><erp:MT_OAAsset_Req>" +
						"<ControlInfo>" +
						"<INTF_ID>I0008</INTF_ID>" +
						"<Src_System>OA</Src_System>" +
						"<Dest_System>SAPERP</Dest_System>" +
						"<Company_Code></Company_Code>" +
						"<Send_Time>" + dateStr + "</Send_Time>" +
						"</ControlInfo>" + paramstr +
						"</erp:MT_OAAsset_Req>" +
						"</soapenv:Body>" +
						"</soapenv:Envelope>";

				log(request);
				HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
				httpHeaderParm.put("instId", "10062");
				httpHeaderParm.put("repairType", "RP");
				String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request,
						"http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_OAAsset_Req_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/",
						httpHeaderParm);
				log("----<GDZCCGAction>" + response);

				//处理response
				Document dom = DocumentHelper.parseText(response);
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
							if ("E".equals(MSG_TYPE)) {
								paramRequestInfo.getRequestManager().setMessageid("90031");
								paramRequestInfo.getRequestManager().setMessagecontent(MESSAGE);
								return Action.FAILURE_AND_CONTINUE;
							} else {
								String requestID = itemEle.elementText("OAREQ_NO");
								String dt1id = itemEle.elementText("OAREQ_ITEM");
								String EQUIPMENT_NO = itemEle.elementText("EQUIPMENT_NO");
								String ASSET_NO = itemEle.elementText("ASSET_NO");
								WriteBack(maintable, requestID, dt1id, EQUIPMENT_NO, ASSET_NO);
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				paramRequestInfo.getRequestManager().setMessageid("90031");
				paramRequestInfo.getRequestManager().setMessagecontent(e.getMessage());
				return Action.FAILURE_AND_CONTINUE;
			}
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

	private void log(Object o) {
		String prefix = "<" + this.getClass().getName() + ">";
		writeLog(prefix + o);
		System.out.println(prefix + o);
	}

	private String getCompanyCode(String id) {
		RecordSet recordSet = new RecordSet();
		String sql = "select gcbm from uf_sapjcsj_gc where id='" + id + "'";
		recordSet.executeSql(sql);
		String CompanyCode = "";
		if (recordSet.next()) {
			CompanyCode = recordSet.getString("gcbm");
		}
		return CompanyCode;
	}

	private String getLastName(String id) {
		RecordSet recordSet = new RecordSet();
		String sql = "select * \n" +
				"from\n" +
				"(select id,lastname from hrmresource\n" +
				"union\n" +
				"select id,lastname from hrmresourcemanager) t\n" +
				"where t.id=" + id;
		recordSet.executeSql(sql);
		String LastName = "";
		if (recordSet.next()) {
			LastName = recordSet.getString("lastname");
		}
		return LastName;
	}

	private String getEQTYP(String id) {
		RecordSet recordSet = new RecordSet();
		String sql = "select sbzldm from uf_sapjcsj_sbzl where id='" + id + "'";
		recordSet.executeSql(sql);
		String EQTYP = "";
		if (recordSet.next()) {
			EQTYP = recordSet.getString("sbzldm");
		}
		return EQTYP;
	}

	private String getEQART(String id) {
		RecordSet recordSet = new RecordSet();
		String sql = "select sbfldm from uf_sapjcsj_sbfl where id='" + id + "'";
		recordSet.executeSql(sql);
		String EQART = "";
		if (recordSet.next()) {
			EQART = recordSet.getString("sbfldm");
		}
		return EQART;
	}

	private String getCurrency(String id) {
		RecordSet recordSet = new RecordSet();
		String sql = "select bzdm from uf_sapjcsj_bz where id='" + id + "'";
		recordSet.executeSql(sql);
		String Currency = "";
		if (recordSet.next()) {
			Currency = recordSet.getString("bzdm");
		}
		return Currency;
	}

	private void WriteBack(String t, String requestID, String dt1id, String EQUIPMENT_NO, String ASSET_NO) {
		RecordSet recordSet = new RecordSet();
		String sql = "select id from " + t + " where requestid=" + requestID;
		recordSet.executeSql(sql);
		recordSet.next();
		String mainid = recordSet.getString("id");

		sql = "update " + t + "_dt1 set sbbh='" + EQUIPMENT_NO + "',zcbh='" + ASSET_NO + "' where mainid=" + mainid + " and id=" + dt1id;
		log("----<GDZCCGAction>" + sql);
		recordSet.executeSql(sql);
	}
}
