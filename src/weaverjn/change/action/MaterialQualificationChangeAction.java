package weaverjn.change.action;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.DetailTable;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.Row;
import weaverjn.action.integration.RET_MSG;
import weaverjn.action.integration.utils;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;
/**
 * 物料资质信息
 * @author songqi
 * @tel 13256247773
 * 2017年4月18日 上午10:10:12
 */
public class MaterialQualificationChangeAction extends BaseBean implements Action  {
	private String ypbh;
	private String bgxm;
	private String bgqnr;
	private String bghnr;
	private String uf;
	@Override
	public String execute(RequestInfo requestInfo) {
		List<MaterialQualificationChangeAction> list = new ArrayList<MaterialQualificationChangeAction>();
		
		// 取明细数据
		DetailTable[] detailtable = requestInfo.getDetailTableInfo().getDetailTable(); // 获取所有明细表
		if (detailtable.length > 0) {
			for (int i = 0; i < detailtable.length; i++) {
				DetailTable dt = detailtable[i];// 指定明细表
				Row[] s = dt.getRow(); // 当前明细表的所有数据,按行存储
				for (int j = 0; j < s.length; j++) {
					Row r = s[j]; // 指定行
					Cell c[] = r.getCell(); // 每行数据再按列存储
					MaterialQualificationChangeAction dc = new MaterialQualificationChangeAction();
					for (int k = 0; k < c.length; k++) {
						Cell c1 = c[k]; // 指定列
						String name = c1.getName(); // 明细字段名称
						String value = c1.getValue(); // 明细字段的值
						if(name.equals("spbh")){
							dc.setYpbh(value);
						}
						if(name.equals("bgxm")){
							dc.setBgxm(value);
						}
						if(name.equals("bgqnr")){
							dc.setBgqnr(value);
						}
						if(name.equals("bghnr")){
							dc.setBghnr(value);
						}
					}
					if(null != dc.getYpbh() && !dc.getYpbh().equals("")){
						list.add(dc);
					}
				}
			}
		}
		
		String tag = "erp:MT_Material_Qualification_Req";
		String username = utils.getUsername();
		String password = utils.getPassword();
		String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
		for(MaterialQualificationChangeAction dc : list){
			String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <" + tag + ">\n" +
					"         <ControlInfo>\n" +
					"            <INTF_ID></INTF_ID>\n" +
					"            <Src_System>OA</Src_System>\n" +
					"            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
					"            <Company_Code></Company_Code>\n" +
					"            <Send_Time></Send_Time>\n" +
					"         </ControlInfo>\n" +
					getLine(dc.getYpbh()) +
					"      </" + tag + ">\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			writeLog("物料资质请求" + request);
			String response = WSClientUtils.callWebService(request, endpoint, username, password);
			writeLog("物料资质返回" + response);
			RET_MSG ret_msg = getRET_MSG(response);
			if (ret_msg == null) {
				requestInfo.getRequestManager().setMessageid("Message");
				requestInfo.getRequestManager().setMessagecontent(response);
				break;
			} else {
				if (ret_msg.getMSG_TYPE().equals("E")) {
					requestInfo.getRequestManager().setMessageid("sap 返回信息");
					requestInfo.getRequestManager().setMessagecontent(ret_msg.getMESSAGE());
					break;
				}
			}
			
		}
		
		return Action.SUCCESS;
	}

	private String getLine(String ypmc) {
		StringBuilder stringBuilder = new StringBuilder();
		RecordSet recordSet = new RecordSet();
		String sql = "select * from "+getUf()+" where id='" + ypmc + "'";
		recordSet.executeSql(sql);
		if (recordSet.next()) {
			stringBuilder.append("<Mater_Qual>\n")
					.append("<MATNR>").append(Util.null2String(recordSet.getString("ypbh"))).append("</MATNR>\n")
					.append("<WERKS>").append(Util.null2String(recordSet.getString("gc"))).append("</WERKS>\n")
					.append("<SPERM>").append("Y").append("</SPERM>\n")
					.append("<DEAL_SCPOE>").append(Util.null2String(recordSet.getString("lb1"))).append("</DEAL_SCPOE>\n")
					.append("<EXPIRY_DATE_GMP>").append(Util.null2String(recordSet.getString("gmpzsyxq"))).append("</EXPIRY_DATE_GMP>\n")
					.append("<EXPIRY_DATE_LICENSE>").append(Util.null2String(recordSet.getString("pzwhyxqz"))).append("</EXPIRY_DATE_LICENSE>\n")
					.append("</Mater_Qual>\n");
		}
		return stringBuilder.toString();
	}
	
    private RET_MSG getRET_MSG(String s) {
        RET_MSG ret_msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(s);
            Element root = dom.getRootElement();
            Element MT_MAT_MDG_RET = root.element("Body").element("MT_Material_Qualification_Ret");
            ret_msg = new RET_MSG();
            ret_msg.setMSG_TYPE(MT_MAT_MDG_RET.element("Message_Info").elementText("MSG_TYPE"));
            ret_msg.setMESSAGE(MT_MAT_MDG_RET.element("Message_Info").elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ret_msg;
    }

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getYpbh() {
		return ypbh;
	}

	public void setYpbh(String ypbh) {
		this.ypbh = ypbh;
	}

	public String getBgxm() {
		return bgxm;
	}

	public void setBgxm(String bgxm) {
		this.bgxm = bgxm;
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