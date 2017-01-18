package weaverjn.qlzy.sap.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.general.BaseBean;
import weaverjn.qlzy.sap.WSClientUtils;

import java.util.HashMap;

/**
 * Created by zhaiyaqi on 2017/1/17.
 */
public class UpdateITEquipmentInfoFromSAP extends BaseBean {
    private SAPEquipmentInfo sapEquipInfo;
    public static void main(String[] args) {
        UpdateITEquipmentInfoFromSAP t = new UpdateITEquipmentInfoFromSAP();
//        String s = "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//                "   <SOAP:Header/>\n" +
//                "   <SOAP:Body xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
//                "      <n0:MT_Equipment_Info xmlns:n0=\"http://qilu-pharma.com.cn/ERP01/\" xmlns:prx=\"urn:sap.com:proxy:DEV:/1SAI/TASF68EEF98BD305D1E0E7F:750\"/>\n" +
//                "   </SOAP:Body>\n" +
//                "</SOAP:Envelope>";
//        SAPEquipmentInfo sapEquipmentInfo = t.parse(s);
//        System.out.println(sapEquipmentInfo.getPLANT_DESC()==null);
    }
    public boolean getUpdate(String EQUIPMENT_NO, String ASSET_NO) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://qilu-pharma.com.cn/ERP01/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <erp:MT_Search_Equi>\n" +
                "         <ControlInfo>\n" +
                "            <INTF_ID>I0048</INTF_ID>\n" +
                "            <Src_System>OA</Src_System>\n" +
                "            <Dest_System>SAPERP</Dest_System>\n" +
                "            <Company_Code></Company_Code>\n" +
                "            <Send_Time></Send_Time>\n" +
                "         </ControlInfo>\n" +
                "         <Search_Condition_Equi>\n" +
                "            <EQUIPMENT_NO>" + EQUIPMENT_NO + "</EQUIPMENT_NO>\n" +
                "            <ASSET_NO>" + ASSET_NO + "</ASSET_NO>\n" +
                "         </Search_Condition_Equi>\n" +
                "      </erp:MT_Search_Equi>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://podev.qilu-pharma.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_OADEV&receiverParty=&receiverService=&interface=SI_Equipment_Info_Out&interfaceNamespace=http://qilu-pharma.com.cn/ERP01/";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        SAPEquipmentInfo sapEquipmentInfo = parse(response);
        if (sapEquipmentInfo == null) {
            return false;
        } else {
            setSapEquipInfo(sapEquipmentInfo);
        }
        return true;
    }

    private SAPEquipmentInfo parse(String response) {
        SAPEquipmentInfo sapEquipmentInfo = new SAPEquipmentInfo();
        try {
            Document dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element Equipment_Info = root.element("Body").element("MT_Equipment_Info").element("Equipment_Info");
            if (Equipment_Info != null) {
                sapEquipmentInfo.setEQUIPMENT_NO(Equipment_Info.elementText("EQUIPMENT_NO"));
                sapEquipmentInfo.setASSET_NO(Equipment_Info.elementText("ASSET_NO"));
                sapEquipmentInfo.setEQUIPMENT_DESC(Equipment_Info.elementText("EQUIPMENT_DESC"));
                sapEquipmentInfo.setEQART(Equipment_Info.elementText("EQART"));
                sapEquipmentInfo.setEQART_DESC(Equipment_Info.elementText("EQART_DESC"));
                sapEquipmentInfo.setMANUFACTURER(Equipment_Info.elementText("MANUFACTURER"));
                sapEquipmentInfo.setEQTYP(Equipment_Info.elementText("EQTYP"));
                sapEquipmentInfo.setSERIAL_NO(Equipment_Info.elementText("SERIAL_NO"));
                sapEquipmentInfo.setPURCHASE_DATA(Equipment_Info.elementText("PURCHASE_DATA"));
                sapEquipmentInfo.setPLANT(Equipment_Info.elementText("PLANT"));
                sapEquipmentInfo.setPLANT_DESC(Equipment_Info.elementText("PLANT_DESC"));
                sapEquipmentInfo.setPLANNER_GROUP(Equipment_Info.elementText("PLANNER_GROUP"));
                sapEquipmentInfo.setPLANNER_GRP_DESC(Equipment_Info.elementText("PLANNER_GRP_DESC"));
                sapEquipmentInfo.setWORK_CENTER(Equipment_Info.elementText("WORK_CENTER"));
                sapEquipmentInfo.setWORK_CTR_DESC(Equipment_Info.elementText("WORK_CTR_DESC"));
            } else {
                return null;
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sapEquipmentInfo;
    }

    public SAPEquipmentInfo getSapEquipInfo() {
        return sapEquipInfo;
    }

    private void setSapEquipInfo(SAPEquipmentInfo sapEquipInfo) {
        this.sapEquipInfo = sapEquipInfo;
    }
}
