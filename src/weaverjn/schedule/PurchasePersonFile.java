package weaverjn.schedule;
import weaver.interfaces.schedule.BaseCronJob;
/*
import java.util.Date;

import com.weaver.TestInit;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.system.SysRemindWorkflow;
import weaverjn.action.integration.util;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.util.PropertiesUtil;

/**
 * 购货单位收货人资料定时任务，到期提醒指定人，不传sap
 * ========废弃，不再使用
 * @author songqi
 * @tel 13256247773
 * 2017年4月26日 下午3:09:12
 */
public class PurchasePersonFile extends BaseCronJob{
/*
	private static final String t_name = "购货单位收货人资料";
	
	@Override
	public void execute() {
		doit();
	}

	public static void main(String[] args) {
		TestInit.init();
		PurchasePersonFile sf = new PurchasePersonFile();
		sf.doit();
		System.out.println(">>>>>ok");
	}
	
	private void doit() {
//		String date = JnUtil.date2String(new Date());
		remind(new Date());
	}

	private void remind(Date date) {
		RecordSet rs = new RecordSet();
		//许可证有效期至 yxqz 认证证书有效期至 yxqz1 质保协议有效期至 yxqz2
		String t = "uf_wrghdwshrzl";
		String sql = "select txr,sqzrq,sfzyxq from " + t + " ";
		rs.executeSql(sql);
		String sqzrq = "",sfzyxq = "",txr = "";
		String content = "您好，"+t_name+"即将到期，截止日期为：";
		String title = "到期提醒:"+JnUtil.date2String(new Date());
		String cl = "  请及时处理！";
		while(rs.next()){
			sqzrq = Util.null2String(rs.getString("sqzrq"));
			sfzyxq = Util.null2String(rs.getString("sfzyxq"));
			txr = Util.null2String(rs.getString("txr"));
			Date d1 = JnUtil.string2Date(sqzrq);
			Date d2 = JnUtil.string2Date(sfzyxq);
			boolean flag1 = JnUtil.compare(date, d1, 30);
			boolean flag2 = JnUtil.compare(date,d2 , 30);
			if(flag1)
				workflowRemind(title,txr,(content + sqzrq + cl));
			if(flag2){
				workflowRemind(title, txr, content + sfzyxq + cl);
			}
			// 状态更新
//			if(sqzrq.equals(JnUtil.date2String(date)) || sfzyxq.equals(JnUtil.date2String(date))){
//				updateStatus();
//			}
		}
	}


	private void updateStatus() {
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
		String username = util.getUsername();
		String password = util.getPassword();
		String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
		WSClientUtils.callWebService(request, endpoint, username, password);
	}

	private String getLine() {
		StringBuffer sb = new StringBuffer();
		sb.append("<lifnr></lifnr>");
		sb.append("<matnr></matnr>");
		sb.append("<ZMAKTX_GYM></ZMAKTX_GYM>");
		sb.append("<ZMAKTX_SPM></ZMAKTX_SPM>");
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
		sb.append("<ZSTATE>N</ZSTATE>");	// N锁定
		sb.append("<ZZHXS></ZZHXS>");
		sb.append("<ZZHWL_MC></ZZHWL_MC>");
		sb.append("<ZDWJG_CW></ZDWJG_CW>");
		sb.append("<ZSDYY></ZSDYY>");
		sb.append("<ZDATE></ZDATE>");

		return sb.toString();
	}

	/**
	 * 系统自动提醒
	 * <br/>2017年5月10日 上午11:02:40<br/>
	 * @param title
	 * @param hrmid
	 * @param content
	 */
	/*
	private void workflowRemind(String title,String hrmid,String content) {
		int fqr = 1;	// 发起人
		SysRemindWorkflow workflow = new SysRemindWorkflow();
		try {
			workflow.make(title, 0, 0, 0, 0, fqr, hrmid, content);
		} catch (Exception e) {
			JnUtil.writeLog(getClass().getName(), "定时任务给提醒人发消息报错：" + e.getMessage());
//			e.printStackTrace();
		}
	}

	*/
	
}
