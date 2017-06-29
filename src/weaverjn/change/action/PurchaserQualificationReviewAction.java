package weaverjn.change.action;

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
import weaverjn.crm.main.JnUtils;
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
	private RecordSetDataSource rsds = new RecordSetDataSource("crm_db");

    private static final String tablename = "YXOAXFSQR";
//	private static final String tablename2 = "YXOAXFSQRPZ";

    private String vkorg;
    private String uf;
    private String ghdwtable;
    @Override
    public String execute(RequestInfo requestInfo) {
    	writeLog("采购员资料变更run");
    	
    	
    	RequestManager requestManager = requestInfo.getRequestManager();
    	Map<String, String> map = utils.getMainTableData(requestInfo.getMainTableInfo());
        RecordSet rs = new RecordSet();
        
//        String cgy = Util.null2String(map.get("cgy"));
//        String cgyxm = Util.null2String(map.get("cgyxm"));
        
        // 修改建模
        String sql = "update " + uf + " set sfzyxq='"+map.get("bghrq")+"',cgyxm='"+Util.null2String(map.get("cgyxm"))+"'  where id='"+map.get("sfzh")+"'";
        writeLog("采购员变更修改建模sql： " + sql);
        boolean f = rs.execute(sql);
        if(f){
        	writeLog("修改建模成功！");
        }else{
        	writeLog("建模修改失败，不在传输sap");
        	return Action.SUCCESS;
        }
        String sql2 = "select * from " + uf + " where id='"+map.get("sfzh")+"'";
        writeLog("查询采购员变更建模中的sql： " + sql2);
        rs.executeSql(sql2);
        rs.next();
        RecordSet rss = rs;
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
                "         <ZSQ_END>" + Util.null2String(rs.getString("z")) + "</ZSQ_END>\n" +
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
//                "         <JY>Y</JY>\n" +
                "         <LAND1>CN</LAND1>\n" +
                "      </erp:MT_PurchaserQualificationReview>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        writeLog("请求sap信息：" + soapHttpRequest);
        String username = utils.getUsername();
        String password = utils.getPassword();
        String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
        writeLog("请求sap 的 URL： " + endpoint);
        String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, username, password);
        writeLog("sap返回信息：" + soapHttpResponse);
        RET_MSG msg = parse(soapHttpResponse);
        if (msg != null) {
            if (msg.getMSG_TYPE().equals("E")) {
                requestManager.setMessageid("SAP Response Message");
                requestManager.setMessagecontent(msg.getMESSAGE());
            }
        } else {
            requestManager.setMessageid("ERROR");
            requestManager.setMessagecontent(soapHttpResponse);
        }
        
        String editsql = editSqlGroup(rss,map);
        rsds.execute(editsql);
		writeLog("CRM采购员修改sql： " + editsql);
//        addSql(rss,map);
        /*  */
        return SUCCESS;
    }
    private String editSqlGroup(RecordSet rss, Map<String, String> map) {
    	String gsid = "";
    	if(vkorg.equals("1610")){
    		gsid = "01001";
    	}else if(vkorg.equals("1620")){
    		gsid = "01002";
    	}else if(vkorg.equals("1630")){
    		gsid = "01003";
    	}
		RecordSet rs = new RecordSet();
		String sql = "select ghfmc from " + ghdwtable + " where id='"+rss.getString("ghdwmc")+"'";
		rs.executeSql(sql);
		rs.next();
		String ghfmc = Util.null2String(rs.getString("ghfmc"));
		String jyfwstr = "";
		String jyfwsql = "select jyfw from " + uf + "_dt2 where mainid='"+map.get("sfzh")+"'";
		rs.executeSql(jyfwsql);
		writeLog("查询经营范围sql： " + jyfwsql);
		while(rs.next()){
			jyfwstr += "," + Util.null2String(rs.getString("jyfw"));
		}
		jyfwstr = jyfwstr.length() == 0 ? jyfwstr : jyfwstr.substring(1);
		StringBuffer key = new StringBuffer("update " + tablename + " set ");
		key.append("YXOAXFSQR_BGRQ='"+JnUtils.getDate("date")+"',").append("YXOAXFSQR_BGSJ='"+JnUtils.getDate("datetime")+"',");
		key.append("YXOAXFSQR_KHBH='"+rss.getString("ghdwbh")+"',");
		key.append("YXOAXFSQR_KHMC='"+ghfmc+"',");
		key.append("YXOAXFSQR_RYBH='"+rss.getString("cgybh")+"',");
		key.append("YXOAXFSQR_RYXM='"+rss.getString("cgyxm")+"',");
		key.append("YXOAXFSQR_SFZH='"+rss.getString("cgysfz")+"',");
		key.append("YXOAXFSQR_SFZYXQ='"+rss.getString("sfzyxq").replace("-", "")+"',");
		key.append("YXOAXFSQR_FDSQR='"+rss.getString("fdsqr")+"',");
		key.append("YXOAXFSQR_SFYFRQZ='"+(rss.getString("sfyfrqz").equals("0") ? "1" : "0")+"',");
		key.append("YXOAXFSQR_SFYGHDWZ='"+(rss.getString("sfyghd").equals("0") ? "1" : "0")+"',");
		key.append("YXOAXFSQR_SFYSFZFYJ='"+(rss.getString("sfysfz").equals("0") ? "1": "0")+"',");
		key.append("YXOAXFSQR_SFYSFZFYJGZ='"+(rss.getString("sfzfyz").equals("0")?"1": "0")+"',");
		key.append("YXOAXFSQR_SQQY='"+rss.getString("sqqy")+"',");
		key.append("YXOAXFSQR_SQJE='"+(Util.null2String(rss.getString("sqcgje")).isEmpty()?"0":Util.null2String(rss.getString("sqcgje")))+"',");
		key.append("YXOAXFSQR_SGZH='"+rss.getString("sgz ")+"',");
		key.append("YXOAXFSQR_SGZQX='"+rss.getString("sgzyxq").replace("-", "")+"',");
		key.append("YXOAXFSQR_SQKSRQ='"+rss.getString("sqqzrq").replace("-", "")+"',");
		key.append("YXOAXFSQR_SQJSRQ='"+rss.getString("z").replace("-", "")+"',");
		key.append("YXOAXFSQR_JYFW='"+jyfwstr+"', ");
		key.append("YXOAXFSQR_GSID='"+gsid+"', ");
		key.append("YXOAXFSQR_OARYBH='"+rss.getString("lsh")+"' ");
		key.append(" where YXOAXFSQR_OARYBH='"+rss.getString("lsh")+"' ");
		return key.toString();
	}
    
//    private void addSql(RecordSet rss, Map<String, String> map) {
//    	String gsid = "";
//    	if(vkorg.equals("1610")){
//    		gsid = "01001";
//    	}else if(vkorg.equals("1620")){
//    		gsid = "01002";
//    	}else if(vkorg.equals("1630")){
//    		gsid = "01003";
//    	}
//    	String delsql = "delete from " + tablename2 + " where YXOAXFSQRPZ_RYBH='"+rss.getString("bpbm")+"' and YXOAXFSQRPZ_GSID='"+gsid+"'";
//    	rsds.execute(delsql);
//    	writeLog("删除CRM中间表中数据： " + delsql);
//    	RecordSet rs = new RecordSet();
//		String sql = "select * from " + uf + "_dt1 where mainid='"+map.get("sfzh")+"'";
//		writeLog("查询药品明细sql: " + sql);
//		rs.executeSql(sql);
//		while(rs.next()){
//	    	StringBuffer key = new StringBuffer("insert into " + tablename + "(");
//			key.append("YXOAXFSQRPZ_BGRQ, YXOAXFSQRPZ_BGSJ, YXOAXFSQRPZ_RYBH, YXOAXFSQRPZ_WLBH, YXOAXFSQRPZ_TYM, YXOAXFSQRPZ_SPM, YXOAXFSQRPZ_GG, YXOAXFSQRPZ_BZGG, YXOAXFSQRPZ_SFXDGG,YXOAXFSQRPZ_GSID) ");
//			key.append(" values(");
//			key.append("'" + JnUtils.getDate("date") + "',").append("'" + JnUtils.getDate("datetime") + "',");
//			key.append("'"+rss.getString("bpbm")+"',");
//			key.append("'"+rs.getString("pzbh")+"',");
//			key.append("'"+rs.getString("tymc")+"',");
//			key.append("'"+rs.getString("spm")+"',");
//			key.append("'"+rs.getString("gg")+"',");
//			key.append("'"+rs.getString("bzgg")+"',");
//			key.append("'"+rs.getString("sfxdgg")+"', ");
//			key.append("'"+gsid+"') ");
//			writeLog("CRM新增采购员明细表sql: " + key.toString());
//			rsds.execute(key.toString());
//		}
//	}
    
    
//    private void updateCRM(String bp,String sfzh,String sfzyxqz) {
//    	String datetime = JnUtils.getDate("datetime");
//    	String date = JnUtils.getDate("date");
//    	String sql = "update YXOAXFSQR set YXOAXFSQR_BGRQ='"+date+"',YXOAXFSQR_BGSJ='"+datetime+"',YXOAXFSQR_RYBH='"+bp+"',YXOAXFSQR_SFZYXQ='"+sfzyxqz+"' where YXOAXFSQR_SFZH='"+sfzh+"'";
//    	RecordSetDataSource rs = new RecordSetDataSource("crm_db");
//    	writeLog("修改CRM的sql：" + sql);
//    	boolean f = rs.execute(sql);
//    	if(f){
//    		writeLog("CRM内容采购员身份正有效期修改<成功>!编码为：" + bp);
//    	}else{
//    		writeLog("CRM内容采购员身份正有效期修改<失败>!编码为：" + bp);
//    	}
//	}

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

	public String getGhdwtable() {
		return ghdwtable;
	}

	public void setGhdwtable(String ghdwtable) {
		this.ghdwtable = ghdwtable;
	}

}
