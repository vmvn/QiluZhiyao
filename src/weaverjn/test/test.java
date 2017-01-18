package weaverjn.test;

import com.runqian.report4.model.expression.function.string.URLEncode;
import com.steadystate.css.parser.SACParserCSS1;
import freemarker.ext.beans.HashAdapter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.qlzy.sap.utils.SAPEquipmentInfo;
import weaverjn.qlzy.sap.utils.UpdateITEquipmentInfoFromSAP;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by zhaiyaqi on 2016/11/29.
 */
public class test {
    public static void main(String[] args) {
        String billid = "22";
        UpdateITEquipmentInfoFromSAP t = new UpdateITEquipmentInfoFromSAP();
        SAPEquipmentInfo sapEquipmentInfo = new SAPEquipmentInfo();
        String sql = "update uf_itsbgl set " +
                "pz='" + Util.null2String(sapEquipmentInfo.getEQUIPMENT_DESC()) + "', " +
                "zzs='" + Util.null2String(sapEquipmentInfo.getMANUFACTURER()) + "', " +
                "ppxh='" + Util.null2String(sapEquipmentInfo.getEQTYP()) + "', " +
                "xlh='" + Util.null2String(sapEquipmentInfo.getSERIAL_NO()) + "', " +
                "gmrq='" + Util.null2String(sapEquipmentInfo.getPURCHASE_DATA()) + "', " +
                "jhgcms='" + Util.null2String(sapEquipmentInfo.getPLANT_DESC()) + "', " +
                "jhyzms='" + Util.null2String(sapEquipmentInfo.getPLANNER_GRP_DESC()) + "', " +
                "gzzxms='" + Util.null2String(sapEquipmentInfo.getWORK_CTR_DESC()) + "' " +
                "where id=" + billid;
        RecordSet recordSet = new RecordSet();
        System.out.println(sql);
    }

    private String[] getRet_Messages(String string) {
        String[] v = new String[2];
        try {
            Document dom = DocumentHelper.parseText(string);
            Element root = dom.getRootElement();
            Element Ret_Messages = root.element("Body").element("MT_DemandPlan_RetMsg").element("Ret_Messages");
            String MSG_TYPE = Ret_Messages.elementText("MSG_TYPE");
            String MESSAGE = Ret_Messages.elementText("MESSAGE");
            v[0] = MSG_TYPE;
            v[1] = MESSAGE;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return v;
    }
}
