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
 * 药品资料
 * @author songqi
 * @tel 13256247773
 * 2017年4月18日 上午10:10:12
 */
public class DrugInformation extends BaseBean implements Action  {
	private String ZMAKTX_SPM = "";
		
	@Override
	public String execute(RequestInfo requestInfo) {
		Property[] properties = requestInfo.getMainTableInfo().getProperty();// 获取表单主字段信息
		String name = "",value = "";
		for (int i = 0; i < properties.length; i++) {
			name = properties[i].getName();// 主字段名称
			value = Util.null2String(properties[i].getValue());// 主字段对应的值
			if(name.equals("spbh"))
				setZMAKTX_SPM(value);
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
		sb.append("<lifnr></lifnr>");
		sb.append("<matnr></matnr>");
		sb.append("<ZMAKTX_GYM></ZMAKTX_GYM>");
		sb.append("<ZMAKTX_SPM>"+getZMAKTX_SPM()+"</ZMAKTX_SPM>");
		sb.append("<ZCPJIX></ZCPJIX>");
		sb.append("<ZBZGG></ZBZGG>");
		sb.append("<ZSCQY></ZSCQY>");
		sb.append("<ZZCSB></ZZCSB>");
		sb.append("<ZPZWH></ZPZWH>");
		sb.append("<ZGMP_BH></ZGMP_BH>");
		sb.append("<ZGMP_FW></ZGMP_FW>");
		sb.append("<ZZCTJ></ZZCTJ>");
		sb.append("<ZYHZQ></ZYHZQ>");
		sb.append("<ZYXQ_CP></ZYXQ_CP>");
		sb.append("<ZTS_JXQSD></ZTS_JXQSD>");
		sb.append("<ZJYFW></ZJYFW>");
		sb.append("<ZDZJGM></ZDZJGM>");
		sb.append("<ZYFBS_BZ></ZYFBS_BZ>");
		sb.append("<ZLENGTH></ZLENGTH>");
		sb.append("<ZHIGH></ZHIGH>");
		sb.append("<ZDBJS_BZ></ZDBJS_BZ>");
		sb.append("<ZRY_ZGY></ZRY_ZGY>");
		sb.append("<ZRY_BGY></ZRY_BGY>");
		sb.append("<ZSRYS></ZSRYS>");
		sb.append("<ZCSJG></ZCSJG>");
		sb.append("<ZZHWL></ZZHWL>");
		sb.append("<ZZHWL_CW></ZZHWL_CW>");
		sb.append("<ZZHXS_CW></ZZHXS_CW>");
		sb.append("<BNAME></BNAME>");
		sb.append("<ZCPMC></ZCPMC>");
		sb.append("<MAKTX></MAKTX>");
		sb.append("<ZMAKTX_YWM></ZMAKTX_YWM>");
		sb.append("<ZGUIGE></ZGUIGE>");
		sb.append("<ZXINGZ></ZXINGZ>");
		sb.append("<ZBWTSCDW></ZBWTSCDW>");
		sb.append("<ZZLBZ></ZZLBZ>");
		sb.append("<ZPZWH_YXQ></ZPZWH_YXQ>");
		sb.append("<ZGMP_YQX></ZGMP_YQX>");
		sb.append("<LGORT></LGORT>");
		sb.append("<ZBZPSL></ZBZPSL>");
		sb.append("<ZSXBJTS></ZSXBJTS>");
		sb.append("<ZLSJG></ZLSJG>");
		sb.append("<ZWIDE></ZWIDE>");
		sb.append("<ZWEIGHT></ZWEIGHT>");
		sb.append("<ZFPBM></ZFPBM>");
		sb.append("<ZRY_YSY></ZRY_YSY>");
		sb.append("<ZRY_FHY></ZRY_FHY>");
		sb.append("<ZLCP></ZLCP>");
		sb.append("<ZSRFH></ZSRFH>");
		sb.append("<ZSTATE></ZSTATE>");
		sb.append("<ZZHXS></ZZHXS>");
		sb.append("<ZZHWL_MC></ZZHWL_MC>");
		sb.append("<ZDWJG_CW></ZDWJG_CW>");
		sb.append("<ZSDYY></ZSDYY>");
		sb.append("<ZDATE></ZDATE>");

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

	public String getZMAKTX_SPM() {
		return ZMAKTX_SPM;
	}
	public void setZMAKTX_SPM(String zMAKTX_SPM) {
		ZMAKTX_SPM = zMAKTX_SPM;
	}

	
}
	