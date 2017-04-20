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
 * 物料资质信息
 * @author songqi
 * @tel 13256247773
 * 2017年4月18日 上午10:10:12
 */
public class MaterialQualification extends BaseBean implements Action  {
	private static final String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_Material_Qualification_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
	private String MATNR = "";
	private String WERKS = "";
	private String DEAL_SCPOE = "";
	
	@Override
	public String execute(RequestInfo requestInfo) {
		Property[] properties = requestInfo.getMainTableInfo().getProperty();// 获取表单主字段信息
		String name = "",value = "";
		for (int i = 0; i < properties.length; i++) {
			name = properties[i].getName();// 主字段名称
			value = Util.null2String(properties[i].getValue());// 主字段对应的值
			if(name.equals("spbh"))
				setMATNR(value);
		}
		String tag = "erp:MT_DrugInformation";
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
                "         </ControlInfo>\n" + getLine() +
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

	public String getMATNR() {
		return MATNR;
	}
	public void setMATNR(String mATNR) {
		MATNR = mATNR;
	}
	public String getWERKS() {
		return WERKS;
	}
	public void setWERKS(String wERKS) {
		WERKS = wERKS;
	}
	public String getDEAL_SCPOE() {
		return DEAL_SCPOE;
	}
	public void setDEAL_SCPOE(String dEAL_SCPOE) {
		DEAL_SCPOE = dEAL_SCPOE;
	}

	
}
	