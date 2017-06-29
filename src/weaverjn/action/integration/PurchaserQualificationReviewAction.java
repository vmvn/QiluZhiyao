package weaverjn.action.integration;

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
import weaver.soa.workflow.request.MainTableInfo;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;
import weaverjn.crm.main.JnUtils;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

/**
 * 执行顺序在传crm数据之后
 * 采购员审核通过，传给sap，并接收bp编号，修改建模，并传crm。
 * 需要前台提供vkorg和建模名称
 * @author songqi
 * @tel 13256247773
 * 2017年5月19日 上午10:09:21
 */
public class PurchaserQualificationReviewAction extends BaseBean implements Action{
    private String vkorg;
    private String uf;
    
    private static final String tablename = "YXOAXFSQR";
	private static final String tablename2 = "YXOAXFSQRPZ";
	private RecordSetDataSource rsds = new RecordSetDataSource("crm_db");
    @Override
    public String execute(RequestInfo requestInfo) {
    	writeLog("run>>>>>>>>");
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if (!src.equals("reject")) {
            String workflowId = requestInfo.getWorkflowid();
            String requestId = requestInfo.getRequestid();
            String t = utils.getTableName(workflowId);//主表名
            MainTableInfo mainTableInfo = requestInfo.getMainTableInfo();
            Map<String,String> map = utils.getMainTableData(mainTableInfo);
            String sfzh = "";
            RecordSet rs = new RecordSet();
            String sql = "select * from " + t + " where requestid=" + requestId;
            writeLog("采购员查询的sql： " + sql);
//            JnUtil.writeDB(className, className, "", "","" ,sql,"");
            rs.executeSql(sql);
            rs.next();
            String lsh = Util.null2String(rs.getString("lsh"));
            sfzh = Util.null2String(rs.getString("cgysfzh"));
            String ghdwbm = map.get("ghdwmc");
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
					"         <KUNNER>" + ghdwbm + "</KUNNER>\n" +
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
//                    "         <JY>Y</JY>\n" +
					"         <LAND1>" + (Util.null2String(rs.getString("gj")).isEmpty() ? "CN" : Util.null2String(rs.getString("gj"))) + "</LAND1>\n" +
					"      </erp:MT_PurchaserQualificationReview>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";

			writeLog("请求sap内容： " + soapHttpRequest);
            String username = utils.getUsername();
            String password = utils.getPassword();
            String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
            writeLog("请求sap 的 URL： " + endpoint);
            String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, username, password);
            writeLog("sap返回信息：" + soapHttpResponse);
            MT_PurchaserQualificationReview_MSg msg = parse(soapHttpResponse);
            if (msg != null) {
                if (msg.getMESSAGE_TYPE().equals("E")) {
                    requestManager.setMessageid("SAP Response Message");
                    requestManager.setMessagecontent(msg.getMESSAGE());
                }else{
                	String id = msg.getPartnerid();
                	// 2017-05-31 改为根据流程编号作为唯一标识
                	String s = "update " + getUf() + " set bpbm='"+id+"',cgybh='"+id+"' where cgysfz='"+sfzh+"'";
                	writeLog("修改采购员资料的bp编码： " + s);
                	boolean f = rs.execute(s);
                	String mt = "update " + t + " set cgybh='"+id+"' where cgysfzh='"+sfzh+"'";
                	writeLog("修改采购员流程的bp编码： " + s);
                	boolean bmt = rs.execute(mt);
                	if(f && bmt){
                		writeLog("采购员修改成功！");
                		// 传crm
                		String addsql = this.addSqlGroup(ghdwbm,map,id,t,requestId,lsh);
            			rsds.execute(addsql);
            			writeLog("CRM采购员新增sql： " + addsql);
            			this.addSql(id,t,requestId,lsh);
            			
//                		updateCRM(id,sfzh);
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
    
    private void addSql(String cgybh, String t, String requestId,String lsh) {
    	cgybh = cgybh.replaceAll(JnUtils.regEx, "");
    	String gsid = "";
    	if(vkorg.equals("1610")){
    		gsid = "01001";
    	}else if(vkorg.equals("1620")){
    		gsid = "01002";
    	}else if(vkorg.equals("1630")){
    		gsid = "01003";
    	}
    	rsds.execute("delete from " + tablename2 + " where YXOAXFSQRPZ_RYBH='"+cgybh+"' and YXOAXFSQRPZ_GSID='"+gsid+"' and YXOAXFSQRPZ_OARYBH='"+lsh+"'");
    	RecordSet rs = new RecordSet();
		String sql = "select * from " + t + "_dt1 where mainid=(select id from "+t+" where requestid='"+requestId+"')";
		writeLog("查询药品明细sql: " + sql);
		rs.executeSql(sql);
		while(rs.next()){
	    	StringBuffer key = new StringBuffer("insert into " + tablename2 + "(");
			key.append("YXOAXFSQRPZ_BGRQ, YXOAXFSQRPZ_BGSJ, YXOAXFSQRPZ_RYBH, YXOAXFSQRPZ_WLBH, YXOAXFSQRPZ_TYM, YXOAXFSQRPZ_SPM, YXOAXFSQRPZ_GG, YXOAXFSQRPZ_BZGG, YXOAXFSQRPZ_SFXDGG,YXOAXFSQRPZ_OARYBH,YXOAXFSQRPZ_GSID) ");
			key.append(" values(");
			key.append("'" + JnUtils.getDate("date") + "',").append("'" + JnUtils.getDate("datetime") + "',");
			key.append("'"+cgybh+"',");
			key.append("'"+rs.getString("pzbh").replaceAll(JnUtils.regEx, "")+"',");
			key.append("'"+rs.getString("tymc")+"',");
			key.append("'"+rs.getString("spm")+"',");
			key.append("'"+rs.getString("gg")+"',");
			key.append("'"+rs.getString("bzgg")+"',");
			key.append("'"+rs.getString("sfxdgg")+"', ");
			key.append("'"+lsh+"', ");
			
			key.append("'"+gsid+"') ");
			writeLog("CRM新增采购员明细表sql: " + key.toString());
			rsds.execute(key.toString());
		}
	}
    
    private String addSqlGroup(String ghdwbm, Map<String, String> map, String cgybh, String t,String requestid, String lsh) {
    	ghdwbm = ghdwbm.replaceAll(JnUtils.regEx, "");
    	cgybh = cgybh.replaceAll(JnUtils.regEx, "");
    	String gsid = "";
    	if(vkorg.equals("1610")){
    		gsid = "01001";
    	}else if(vkorg.equals("1620")){
    		gsid = "01002";
    	}else if(vkorg.equals("1630")){
    		gsid = "01003";
    	}
    	RecordSet rs =new RecordSet();
		String sql = "select jyfw from " + t + "_dt2 where mainid=(select id from "+t+" where requestid='"+requestid+"')";
		String jyfwstr = "";
		rs.executeSql(sql);
		while(rs.next()){
			jyfwstr += "," + Util.null2String(rs.getString("jyfw"));
		}
		jyfwstr = jyfwstr.length() == 0 ? jyfwstr : jyfwstr.substring(1);
		writeLog("查询经营范围sql： " + sql);
    	StringBuffer key = new StringBuffer("insert into " + tablename + "(");
		key.append("YXOAXFSQR_BGRQ, YXOAXFSQR_BGSJ, YXOAXFSQR_KHBH, YXOAXFSQR_KHMC, YXOAXFSQR_RYBH, YXOAXFSQR_RYXM, YXOAXFSQR_SFZH, YXOAXFSQR_SFZYXQ, YXOAXFSQR_FDSQR, YXOAXFSQR_SFYFRQZ, YXOAXFSQR_SFYGHDWZ, YXOAXFSQR_SFYSFZFYJ, YXOAXFSQR_SFYSFZFYJGZ, YXOAXFSQR_SQQY, YXOAXFSQR_SQJE, YXOAXFSQR_SGZH, YXOAXFSQR_SGZQX, YXOAXFSQR_JYFW,YXOAXFSQR_SQKSRQ,YXOAXFSQR_SQJSRQ,YXOAXFSQR_OARYBH,YXOAXFSQR_SYBZ,YXOAXFSQR_GSID) ");
		key.append(" values(");
		key.append("'" + JnUtils.getDate("date") + "',").append("'" + JnUtils.getDate("datetime") + "',");
		key.append("'"+ghdwbm+"',");
		key.append("'"+map.get("ghdwmc")+"',");
		key.append("'"+cgybh+"',");
		key.append("'"+map.get("cgyxm")+"',");
		key.append("'"+map.get("cgysfzh")+"',");
		key.append("'"+map.get("sfzyxq").replace("-", "")+"',");
		key.append("'"+map.get("fdsqr")+"',");
		key.append("'"+(Util.null2String(map.get("sfyfrqz")).equals("0") ? "1" : "0")+"',");
		key.append("'"+(Util.null2String(map.get("ywghdw")).equals("0") ? "1" : "0")+"',");
		key.append("'"+(Util.null2String(map.get("sfysfzfyj")).equals("0") ? "1" : "0")+"',");
		key.append("'"+(Util.null2String(map.get("sfzfyjyw")).equals("0") ? "1" : "0")+"',");
		key.append("'"+map.get("sqqy")+"',");
		key.append("'"+(Util.null2String(map.get("sqcgje")).isEmpty() ? "0" :Util.null2String(map.get("sqcgje"))) +"',");
		key.append("'"+map.get("sgz")+"',");
		key.append("'"+map.get("sgzyxq").replace("-", "")+"',");
		key.append("'"+jyfwstr+"',");
		key.append("'"+map.get("sqqzrq").replace("-", "")+"',");
		key.append("'"+map.get("zrq").replace("-", "")+"',");
		key.append("'"+lsh+"',");
		key.append("'1',");
		
		key.append("'"+gsid+"')");
		return key.toString();
	}
    
//    private void updateCRM(String bp,String sfzh) {
//    	String datetime = JnUtil.date2String(new Date());
//    	datetime = datetime.replace("-", "");
//    	datetime = datetime.replace(":", "");
//    	datetime += " 00:00:00";
//    	String date = datetime.substring(0,8);
//    	String sql = "update YXOAXFSQR set YXOAXFSQR_BGRQ='"+date+"',YXOAXFSQR_BGSJ='"+datetime+"',YXOAXFSQR_RYBH='"+bp+"' where YXOAXFSQR_SFZH='"+sfzh+"'";
//    	RecordSetDataSource rs = new RecordSetDataSource("crm_db");
//    	writeLog("修改CRM的sql：" + sql);
//    	boolean f = rs.execute(sql);
//    	if(f){
//    		writeLog("CRM内容采购员编码修改<成功>!编码为：" + bp);
//    	}else{
//    		writeLog("CRM内容采购员编码修改<失败>!编码为：" + bp);
//    	}
//	}

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
