package weaverjn.action.integration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.general.BaseBean;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;
import weaverjn.utils.PropertiesUtil;

import java.util.Map;

/**
 * Created by zhaiyaqi on 2017/5/11.
 */
public class MaterialMDgModify extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        writeLog("run");
        RequestManager requestManager = requestInfo.getRequestManager();
        Map<String, String> MtData = utils.getMainTableData(requestInfo.getMainTableInfo());
        String soapHttpRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_MAT_MDG_LIST>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID></INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP" + new PropertiesUtil().getPropValue("saperp", "Dest_System") + "</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <!--1 or more repetitions:-->\n" +
                "         <Material_Mdg_Info>\n" +
                "            <REQ_TYPE>2</REQ_TYPE>\n" +
                "            <REQUEST></REQUEST>\n" +
                "            <DESCRIPTION>?</DESCRIPTION>\n" +
                "            <MATERIAL>?</MATERIAL>\n" +
                "            <MTART>?</MTART>\n" +
                "            <MEINS>?</MEINS>\n" +
                "            <MATKL>?</MATKL>\n" +
                "            <SPART>?</SPART>\n" +
                "            <BISMT>?</BISMT>\n" +
                "            <MBRSH>?</MBRSH>\n" +
                "            <GROES>?</GROES>\n" +
                "            <EXTWG>?</EXTWG>\n" +
                "            <NOTEBSCDA>?</NOTEBSCDA>\n" +
                "            <TXTQINSP>?</TXTQINSP>\n" +
                "            <GEWEI_MAT>?</GEWEI_MAT>\n" +
                "            <NORMT>?</NORMT>\n" +
                "            <FERTH>?</FERTH>\n" +
                "            <WRKST>?</WRKST>\n" +
                "            <NTGEW>?</NTGEW>\n" +
                "            <BRGEWMARA>?</BRGEWMARA>\n" +
                "            <LOEKZMDMA>?</LOEKZMDMA>\n" +
                "            <NOTEINTCM>?</NOTEINTCM>\n" +
                "            <!--1 or more repetitions:-->\n" +
                "            <MAKT_LIST>\n" +
                "               <MAKTX>?</MAKTX>\n" +
                "               <SPRAS>?</SPRAS>\n" +
                "            </MAKT_LIST>\n" +
                "         </Material_Mdg_Info>\n" +
                "      </erp:MT_MAT_MDG_LIST>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        return SUCCESS;
    }

    private RET_MSG getRET_MSG(String s) {
        RET_MSG msg = null;
        Document dom;
        try {
            dom = DocumentHelper.parseText(s);
            Element root = dom.getRootElement();
            Element MT_MAT_MDG_RET = root.element("Body").element("MT_MAT_MDG_RET");
            msg = new RET_MSG();
            msg.setMSG_TYPE(MT_MAT_MDG_RET.element("RET_MSG").elementText("MSG_TYPE"));
            msg.setMESSAGE(MT_MAT_MDG_RET.element("RET_MSG").elementText("MESSAGE"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return msg;
    }

    class RET_MSG {
        private String MSG_TYPE;
        private String MESSAGE;

        public String getMSG_TYPE() {
            return MSG_TYPE;
        }

        public void setMSG_TYPE(String MSG_TYPE) {
            this.MSG_TYPE = MSG_TYPE;
        }

        public String getMESSAGE() {
            return MESSAGE;
        }

        public void setMESSAGE(String MESSAGE) {
            this.MESSAGE = MESSAGE;
        }
    }
}
