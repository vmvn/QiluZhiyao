package weaverjn.action.integration;

import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

/**
 * 供应商/销售员资质上传ERP
 * 自定义参数：p1 1表示供应商，2表示销售员
 * @author songqi
 * @tel 13256247773
 * 2017年4月18日 上午10:10:12
 */
public class SupplierQualification extends BaseBean implements Action  {
	private String LIFNR_TYPE = "";
	private String EKORG = "";
	private String LIFNR = "";
	private String SPERM = "";
	private String DATE_BEG = "";
	private String DATE_END = "";
	private String SCPOE_ID = "";
	private String MATNR = "";
		
	private String p1 = "";

	@Override
	public String execute(RequestInfo requestInfo) {
		Property[] properties = requestInfo.getMainTableInfo().getProperty();// 获取表单主字段信息
		String name = "",value = "";
		if(getP1().equals("1"))
			setLIFNR_TYPE("1");
		else 
			setLIFNR_TYPE("2");
		for (int i = 0; i < properties.length; i++) {
			name = properties[i].getName();// 主字段名称
			value = Util.null2String(properties[i].getValue());// 主字段对应的值
			if(name.equals("cgzz"))
				setEKORG(value);
			if(name.equals("dwbh") || name.equals("ghdwbm"))
				setLIFNR(value);
			if(name.equals(""))
				setSPERM(value);
			if(name.equals(""))
				setDATE_BEG(value);
			if(name.equals(""))
				setDATE_END(value);
			if(name.equals(""))
				setSCPOE_ID(value);
			if(name.equals(""))
				setMATNR(value);
		}
		String tag = "erp:MT_Supplier_Qualification_Req";
		String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">" +
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
               getLine() +
                "      </"+tag+">\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        log(request);
		String username = utils.getUsername();
		String password = utils.getPassword();
		String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
		String response = WSClientUtils.callWebService(request, endpoint, username, password);
        log(response);
        RET_MSG ret_msg = getRET_MSG(response);
        if (ret_msg == null) {
            requestInfo.getRequestManager().setMessageid("Message");
            requestInfo.getRequestManager().setMessagecontent(response);
        } else {
            if (ret_msg.getMSG_TYPE().equals("E")) {
                requestInfo.getRequestManager().setMessageid("Error Message");
                requestInfo.getRequestManager().setMessagecontent(ret_msg.getMESSAGE());
            }
        }
		return Action.SUCCESS;
	}
	/**
	 * <br/>2017年4月18日 上午11:55:53<br/>
	 * @return
	 */
	private String getLine() {
		StringBuffer sb = new StringBuffer();
		sb.append("<LIFNR_TYPE>" + getLIFNR_TYPE() + "</LIFNR_TYPE>");
		sb.append("<EKORG>" + getEKORG() + "</EKORG>");
		sb.append("<LIFNR>" + getLIFNR() + "</LIFNR>");
		sb.append("<SPERM>" + getSPERM() + "</SPERM>");
		sb.append("<DATE_BEG>" + getDATE_BEG() + "</DATE_BEG>");
		sb.append("<DATE_END>" + getDATE_END() + "</DATE_END>");
		sb.append("		<Deal_Scpoe>");
		sb.append("<SCPOE_ID>" + getSCPOE_ID() + "</SCPOE_ID>");
		sb.append("		</Deal_Scpoe>");
		sb.append("<Material_List>");
		sb.append("<MATNR>" + getMATNR() + "</MATNR>");
		sb.append("</Material_List>");
		return sb.toString();
	}
	private void log(Object o) {
		String prefix = "<" + this.getClass().getName() + ">";
		System.out.println(prefix + o);
		writeLog(prefix + o);
	}
	
    private RET_MSG getRET_MSG(String s) {
        RET_MSG ret_msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(s);
            Element root = dom.getRootElement();
            Element MT_MAT_MDG_RET = root.element("Body").element("MT_MAT_MDG_RET");
            ret_msg = new RET_MSG();
            ret_msg.setMSG_TYPE(MT_MAT_MDG_RET.element("RET_MSG").elementText("MSG_TYPE"));
            ret_msg.setMESSAGE(MT_MAT_MDG_RET.element("RET_MSG").elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return ret_msg;
    }

	public String getLIFNR_TYPE() {
		return LIFNR_TYPE;
	}
	public void setLIFNR_TYPE(String lIFNR_TYPE) {
		LIFNR_TYPE = lIFNR_TYPE;
	}
	public String getEKORG() {
		return EKORG;
	}
	public void setEKORG(String eKORG) {
		EKORG = eKORG;
	}
	public String getLIFNR() {
		return LIFNR;
	}
	public void setLIFNR(String lIFNR) {
		LIFNR = lIFNR;
	}
	public String getSPERM() {
		return SPERM;
	}
	public void setSPERM(String sPERM) {
		SPERM = sPERM;
	}
	public String getDATE_BEG() {
		return DATE_BEG;
	}
	public void setDATE_BEG(String dATE_BEG) {
		DATE_BEG = dATE_BEG;
	}
	public String getDATE_END() {
		return DATE_END;
	}
	public void setDATE_END(String dATE_END) {
		DATE_END = dATE_END;
	}
	public String getSCPOE_ID() {
		return SCPOE_ID;
	}
	public void setSCPOE_ID(String sCPOE_ID) {
		SCPOE_ID = sCPOE_ID;
	}
	public String getMATNR() {
		return MATNR;
	}
	public void setMATNR(String mATNR) {
		MATNR = mATNR;
	}
	public String getP1() {
		return p1;
	}
	public void setP1(String p1) {
		this.p1 = p1;
	}

	
}
	