package weaverjn.schedule;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.weaver.TestInit;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.schedule.BaseCronJob;
import weaver.system.SysRemindWorkflow;
import weaverjn.action.integration.utils;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.utils.PropertiesUtil;

/**
 * 购货单位采购员资料定时任务，到期提醒指定人，不传sap
 * @author songqi
 * @tel 13256247773
 * 2017年4月26日 下午3:32:43
 */
public class BuyerFile extends BaseCronJob{

	private static final String t_name = "采购员资料";
	
	@Override
	public void execute() {
		doit();
	}

	public static void main(String[] args) {
		
		TestInit.init();
		BuyerFile sf = new BuyerFile();
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
		String t = "uf_wrghdwcgyzl";
		String sql = "select txr,z,sfzyxq from " + t + " ";
		rs.executeSql(sql);
		String z = "",sfzyxq = "",txr = "";
		String content = "您好，"+t_name+"即将到期，截止日期为：";
		String title = "到期提醒:"+JnUtil.date2String(new Date());
		String cl = "  请及时处理！";
		while(rs.next()){
			z = Util.null2String(rs.getString("z"));
			sfzyxq = Util.null2String(rs.getString("sfzyxq"));
			if(sfzyxq.equals(""))
				continue;
			txr = Util.null2String(rs.getString("txr"));
			Date d1 = JnUtil.string2Date(z);
			Date d2 = JnUtil.string2Date(sfzyxq);
			boolean flag1 = JnUtil.compare(date, d1, 30);
			boolean flag2 = JnUtil.compare(date,d2 , 30);
			if(flag1)
				workflowRemind(title,txr,(content + z + cl));
			if(flag2){
				workflowRemind(title, txr, content + sfzyxq + cl);
			}
			// 状态更新
//			if(z.equals(JnUtil.date2String(date)) || sfzyxq.equals(JnUtil.date2String(date))){
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
		String username = utils.getUsername();
		String password = utils.getPassword();
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

	private void workflowRemind(String title,String hrmid,String content) {
		int fqr = 1;	// 发起人
		SysRemindWorkflow workflow = new SysRemindWorkflow();
		try {
			workflow.make(title, 0, 0, 0, 0, fqr, hrmid, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
}
