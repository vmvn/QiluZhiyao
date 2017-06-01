package weaverjn.action.integration;

import java.util.Date;

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
import weaverjn.schedule.JnUtil;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

/**
 * 采购员审核通过，传给sap，并接收bp编号，修改建模，并传crm。
 * 需要前台提供vkorg和建模名称
 * @author songqi
 * @tel 13256247773
 * 2017年5月19日 上午10:09:21
 */
public class PurchaserQualificationReviewAction extends BaseBean implements Action{
    private String vkorg;
    private String uf;
    private static String className = PurchaserQualificationReviewAction.class.getSimpleName();
    @Override
    public String execute(RequestInfo requestInfo) {
    	writeLog("run>>>>>>>>");
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if (!src.equals("reject")) {
            String workflowId = requestInfo.getWorkflowid();
            String requestId = requestInfo.getRequestid();
            String t = utils.getTableName(workflowId);//主表名
            String sfzh = "";
            RecordSet rs = new RecordSet();
            RecordSet rs2 = new RecordSet();
            String sql = "select * from " + t + " where requestid=" + requestId;
            writeLog("采购员查询的sql： " + sql);
            JnUtil.writeDB(className, className, "", "","" ,sql,"");
            rs.executeSql(sql);
            rs.next();
            sfzh = Util.null2String(rs.getString("cgysfzh"));
            int billId = requestManager.getBillid();
            this.uf = utils.getModTableName("cgysfzh", billId);
            String sql2 = "select * from uf_wrghdwzl where id='"+Util.null2String(rs.getString("ghdwbm"))+"'";
            rs2.executeSql(sql2);
            writeLog("去建模查询购货单位名称sql： " + sql2);
            rs2.next();
            String ghdwbm = Util.null2String(rs2.getString("ghfbh"));
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
                    "         <KUNNER>" +ghdwbm + "</KUNNER>\n" +
                    "         <ZXFCGY_BM></ZXFCGY_BM>\n" +
                    "         <ZSQ_BGN>" + Util.null2String(rs.getString("sqqzrq")) + "</ZSQ_BGN>\n" +
                    "         <ZSQ_END>" + Util.null2String(rs.getString("zrq")) + "</ZSQ_END>\n" +
                    "         <ZXFCGY_SFZ>" + sfzh + "</ZXFCGY_SFZ>\n" +
                    "         <ZSFZ_YXQ>" + Util.null2String(rs.getString("sfzyxq")) + "</ZSFZ_YXQ>\n" +
                    "         <ZFDSQR>" + Util.null2String(rs.getString("fdsqr")) + "</ZFDSQR>\n" +
                    "         <ZFRQMQZ>" + (Util.null2String(rs.getString("sfyfrqz")).equals("0") ? "Y" : "N") + "</ZFRQMQZ>\n" +
                    "         <ZGHDWGZ>" + (Util.null2String(rs.getString("ywghdw")).equals("0") ? "Y" : "N") + "</ZGHDWGZ>\n" +
                    "         <ZWTSYJ>" + (Util.null2String(rs.getString("wtssfyj")).equals("0") ? "Y" : "N") + "</ZWTSYJ>\n" +
                    "         <ZSFZFYJ>" + (Util.null2String(rs.getString("sfysfzfyj")).equals("0") ? "Y" : "N") + "</ZSFZFYJ>\n" +
                    "         <ZFYJGZ>" + (Util.null2String(rs.getString("sfzfyjyw")).equals("0") ? "Y" : "N") + "</ZFYJGZ>\n" +
                    "         <NAME_LAST>" + Util.null2String(rs.getString("cgyxm")) + "</NAME_LAST>\n" +
                    "         <PSTLZ>" + (Util.null2String(rs.getString("yzbm")).equals("0") ? "Y" : "N") + "</PSTLZ>\n" +
                    "         <ORT01>" + Util.null2String(rs.getString("cs")) + "</ORT01>\n" +
                    "         <LAND1>" + Util.null2String(rs.getString("gj")) + "</LAND1>\n" +
                    "      </erp:MT_PurchaserQualificationReview>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            writeLog(className + "请求sap内容： " + soapHttpRequest);
            String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_PurchaserQualificationReview_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
            String username = "zappluser_oa";
            String password = "a1234567";
            writeLog(className + "请求sap 的 URL： " + url);
            String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, url, username, password);
            writeLog(className + "sap返回信息：" + soapHttpResponse);
            JnUtil.writeDB(className, className, soapHttpRequest, soapHttpResponse, url, "","");
            MT_PurchaserQualificationReview_MSg msg = parse(soapHttpResponse);
            if (msg != null) {
                if (msg.getMESSAGE_TYPE().equals("E")) {
                    requestManager.setMessageid("sap 返回报错");
                    requestManager.setMessagecontent(msg.getMESSAGE());
                }else{
                	String id = msg.getPartnerid();
                	String s = "update " + getUf() + " set bpbm='"+id+"',cgybh='"+id+"' where cgysfz='"+sfzh+"'";
                	writeLog(className + "修改采购员资料的bp编码： " + s);
                	boolean f = rs.execute(s);
                	String mt = "update " + t + " set cgybh='"+id+"' where cgysfzh='"+sfzh+"'";
                	boolean bmt = rs.execute(mt);
                	if(f && bmt){
                		writeLog("采购员修改成功！");
                		// 传crm
                		updateCRM(id,sfzh);
                	}
                	else
                		writeLog("采购员修改失败！");
                }
            } else {
                requestManager.setMessageid("ERROR");
                requestManager.setMessagecontent(soapHttpResponse);
            }
        }
        return SUCCESS;
    }

    private void updateCRM(String bp,String sfzh) {
    	String datetime = JnUtil.date2String(new Date());
    	datetime = datetime.replace("-", "");
    	datetime = datetime.replace(":", "");
    	datetime += " 00:00:00";
    	String date = datetime.substring(0,8);
    	String sql = "update YXOAXFSQR set YXOAXFSQR_BGRQ='"+date+"',YXOAXFSQR_BGSJ='"+datetime+"',YXOAXFSQR_RYBH='"+bp+"' where YXOAXFSQR_SFZH='"+sfzh+"'";
    	RecordSetDataSource rs = new RecordSetDataSource("crm_db");
    	writeLog("修改CRM的sql：" + sql);
    	boolean f = rs.execute(sql);
    	if(f){
    		writeLog("CRM内容采购员编码修改<成功>!编码为：" + bp);
    	}else{
    		writeLog("CRM内容采购员编码修改<失败>!编码为：" + bp);
    	}
	}

	private MT_PurchaserQualificationReview_MSg parse(String response) {
        MT_PurchaserQualificationReview_MSg msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element e = root.element("Body").element("MT_PurchaserQualificationReview_MSg");
            msg = new MT_PurchaserQualificationReview_MSg();
            msg.setMESSAGE_TYPE(e.elementText("MESSAGE_TYPE"));
            msg.setMESSAGE(e.elementText("MESSAGE"));
            msg.setPartnerid(e.elementTextTrim("partnerid"));
        } catch (DocumentException e) {
//            e.printStackTrace();
        	JnUtil.writeDB(className, className, "", "", "", "",e.getMessage());
        	msg = null;
        }
        return msg;
    }

    public String getVkorg() {
        return vkorg;
    }

    public void setVkorg(String vkorg) {
        this.vkorg = vkorg;
    }

    class MT_PurchaserQualificationReview_MSg {
        private String MESSAGE_TYPE;
        private String MESSAGE;
        private String partnerid;

        public String getMESSAGE_TYPE() {
            return MESSAGE_TYPE;
        }

        public void setMESSAGE_TYPE(String MESSAGE_TYPE) {
            this.MESSAGE_TYPE = MESSAGE_TYPE;
        }

        public String getMESSAGE() {
            return MESSAGE;
        }

        public void setMESSAGE(String MESSAGE) {
            this.MESSAGE = MESSAGE;
        }

		public String getPartnerid() {
			return partnerid;
		}

		public void setPartnerid(String partnerid) {
			this.partnerid = partnerid;
		}
    }

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

}
