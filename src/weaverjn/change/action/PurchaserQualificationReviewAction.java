package weaverjn.change.action;

import java.util.Calendar;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;
import weaverjn.action.integration.RET_MSG;
import weaverjn.action.integration.utils;
import weaverjn.schedule.JnUtil;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

/**
 * 采购员变更审核通过，传给sap bp编号和身份证有效期至，修改建模，并传crm。
 * 需要前台提供vkorg和建模名称
 * @author songqi
 * @tel 13256247773
 * 2017年5月19日 上午10:09:21
 */
public class PurchaserQualificationReviewAction extends BaseBean implements Action{
    private String vkorg;
    private String uf;
    @Override
    public String execute(RequestInfo requestInfo) {
    	RequestManager requestManager = requestInfo.getRequestManager();
    	Map<String, String> map = utils.getMainTableData(requestInfo.getMainTableInfo());
        RecordSet rs = new RecordSet();
        // 修改建模
        String sql = "update " + uf + " set sfzyxq='"+map.get("bghrq")+"' where id='"+map.get("sfzh")+"'";
        writeLog("修改建模sql： " + sql);
        boolean f = rs.execute(sql);
        if(f){
        	writeLog("修改建模成功！");
        }else{
        	writeLog("建模修改失败，不在传输sap");
        	return Action.SUCCESS;
        }
        String sql2 = "select * from " + uf + " where bpbm='"+map.get("cgybh")+"'";
        writeLog("查询采购员变更建模中的sql： " + sql2);
        rs.executeSql(sql2);
        rs.next();
        String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_PurchaserQualificationReview>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID></INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <VKORG>" + this.vkorg + "</VKORG>\n" +
                "         <KUNNER>" + Util.null2String(rs.getString("ghdwbh")) + "</KUNNER>\n" +
                "         <ZXFCGY_BM>"+Util.null2String(rs.getString("bpbm"))+"</ZXFCGY_BM>\n" +
                "         <ZSQ_BGN>" + Util.null2String(rs.getString("sqqzrq")) + "</ZSQ_BGN>\n" +
                "         <ZSQ_END>" + map.get("bghrq") + "</ZSQ_END>\n" +
                "         <ZXFCGY_SFZ>" + Util.null2String(rs.getString("cgysfz")) + "</ZXFCGY_SFZ>\n" +
                "         <ZSFZ_YXQ>" + Util.null2String(rs.getString("sfzyxq")) + "</ZSFZ_YXQ>\n" +
                "         <ZFDSQR>" + Util.null2String(rs.getString("fdsqr")) + "</ZFDSQR>\n" +
                "         <ZFRQMQZ>" + (Util.null2String(rs.getString("sfyfrqz")).equals("0") ? "Y" : "N") + "</ZFRQMQZ>\n" +
                "         <ZGHDWGZ>" + (Util.null2String(rs.getString("ywghdw")).equals("0") ? "Y" : "N") + "</ZGHDWGZ>\n" +
                "         <ZWTSYJ>" + (Util.null2String(rs.getString("wtssfyj")).equals("0") ? "Y" : "N") + "</ZWTSYJ>\n" +
                "         <ZSFZFYJ>" + (Util.null2String(rs.getString("sfysfz")).equals("0") ? "Y" : "N") + "</ZSFZFYJ>\n" +
                "         <ZFYJGZ>" + (Util.null2String(rs.getString("sfzfyz")).equals("0") ? "Y" : "N") + "</ZFYJGZ>\n" +
                "         <NAME_LAST>" + Util.null2String(rs.getString("cgyxm")) + "</NAME_LAST>\n" +
                "         <PSTLZ></PSTLZ>\n" +
                "         <ORT01></ORT01>\n" +
                "         <LAND1>CN</LAND1>\n" +
                "      </erp:MT_PurchaserQualificationReview>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        writeLog("请求sap信息：" + soapHttpRequest);
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_PurchaserQualificationReview_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
        String username = "zappluser_oa";
        String password = "a1234567";
        writeLog("请求sap 的 URL： " + url);
        String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, url, username, password);
        writeLog("sap返回信息：" + soapHttpResponse);
        RET_MSG msg = parse(soapHttpResponse);
        if (msg != null) {
            if (msg.getMSG_TYPE().equals("E")) {
                requestManager.setMessageid("sap 返回报错");
                requestManager.setMessagecontent(msg.getMESSAGE());
            }
        } else {
            requestManager.setMessageid("ERROR");
            requestManager.setMessagecontent(soapHttpResponse);
        }
        
		updateCRM(Util.null2String(rs.getString("bpbm")), Util.null2String(rs.getString("cgysfz")),
				map.get("bghrq"));        
        return SUCCESS;
    }

    private void updateCRM(String bp,String sfzh,String sfzyxqz) {
    	String datetime = JnUtil.date2String(Calendar.getInstance().getTime());
    	datetime = datetime.replace("-", "");
    	datetime = datetime.replace(":", "");
    	datetime += " 00:00:00";
    	String date = datetime.substring(0,8);
    	String sql = "update YXOAXFSQR set YXOAXFSQR_BGRQ='"+date+"',YXOAXFSQR_BGSJ='"+datetime+"',YXOAXFSQR_RYBH='"+bp+"',YXOAXFSQR_SFZYXQ='"+sfzyxqz+"' where YXOAXFSQR_SFZH='"+sfzh+"'";
    	RecordSetDataSource rs = new RecordSetDataSource("crm_db");
    	writeLog("修改CRM的sql：" + sql);
    	boolean f = rs.execute(sql);
    	if(f){
    		writeLog("CRM内容采购员身份正有效期修改<成功>!编码为：" + bp);
    	}else{
    		writeLog("CRM内容采购员身份正有效期修改<失败>!编码为：" + bp);
    	}
	}

	private RET_MSG parse(String response) {
		RET_MSG msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_PurchaserQualificationReview_MSg");
            msg = new RET_MSG();
            msg.setMSG_TYPE(e.elementText("MESSAGE_TYPE"));
            msg.setMESSAGE(e.elementText("MESSAGE"));
//            msg.setPartnerid(e.elementTextTrim("partnerid"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public String getVkorg() {
        return vkorg;
    }

    public void setVkorg(String vkorg) {
        this.vkorg = vkorg;
    }

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

}
