package weaverjn.action.integration;

import weaver.general.BaseBean;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;
import weaverjn.utils.PropertiesUtil;
import weaverjn.utils.WSClientUtils;

import java.util.Map;

/**
 * Created by zhaiyaqi on 2017/4/10.
 */
public class QualityReviewAction extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String src = requestManager.getSrc();
        if (!src.equals("reject")) {
            Map<String, String> mainTableData = utils.getMainTableData(requestInfo.getMainTableInfo());

            String dh = mainTableData.get("dh");
            String sfhg = mainTableData.get("sfhg");
            String ly = mainTableData.get("ly");

            if (sfhg.equals("0")) {
                sfhg = "Y";
            } else {
                sfhg = "N";
            }

            String prueflos = "";
            String ZZQYH = "";
            String ZKCYP = "";
            if (ly.equals("0")) {
                prueflos = dh;
            }
            if (ly.equals("1")) {
                ZZQYH = dh;
            }
            if (ly.equals("2")) {
                ZKCYP = dh;
            }

            String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <erp:MT_QuantityReview>\n" +
                    "         <controlinfo>\n" +
                    "            <INTF_ID></INTF_ID>\n" +
                    "            <Src_System>OA</Src_System>\n" +
                    "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                    "            <Company_Code></Company_Code>\n" +
                    "            <Send_Time></Send_Time>\n" +
                    "         </controlinfo>\n" +
                    "         <prueflos>" + prueflos + "</prueflos>\n" +
                    "         <ZZQYH>" + ZZQYH + "</ZZQYH>\n" +
                    "         <ZKCYP>" + ZKCYP + "</ZKCYP>\n" +
                    "         <ZAPP01>" + sfhg + "</ZAPP01>\n" +
                    "      </erp:MT_QuantityReview>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            String endpoint = new PropertiesUtil().getPropValue("qiluEndpoint", this.getClass().getSimpleName());
            String soapHttpResponse = WSClientUtils.callWebService(soapHttpRequest, endpoint, utils.getUsername(), utils.getPassword());
            writeLog(soapHttpRequest);
            writeLog(soapHttpResponse);
        }
        return SUCCESS;
    }
}
