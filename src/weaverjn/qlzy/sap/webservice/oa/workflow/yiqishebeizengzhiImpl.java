package weaverjn.qlzy.sap.webservice.oa.workflow;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.general.BaseBean;
import weaverjn.qlzy.sap.WSClientUtils;

import java.util.HashMap;

/**
 * Created by zhaiyaqi on 2016/12/5.
 */
public class yiqishebeizengzhiImpl extends BaseBean implements yiqishebeizengzhi {

    @Override
    public String createYiqishebeizengzhi(yiqishebeizengzhiParameters yiqishebeizengzhiparameters) {
        String createTime = yiqishebeizengzhiparameters.getCreateTime();
        String creatorId = yiqishebeizengzhiparameters.getCreatorId();
        String requestLevel = yiqishebeizengzhiparameters.getRequestLevel();
        String requestName = yiqishebeizengzhiparameters.getRequestName();
        workflowBaseInfo workflowBaseInfo = yiqishebeizengzhiparameters.getWorkflowBaseInfo();
        yiqishebeizengzhiMainTable yiqishebeizengzhiMainTable = yiqishebeizengzhiparameters.getMainTable();
        yiqishebeizengzhiDetailTable1[] yiqishebeizengzhiDetailTable1s = yiqishebeizengzhiparameters.getDt1();

        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"webservices.services.weaver.com.cn\" xmlns:web1=\"http://webservices.workflow.weaver\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <web:doCreateWorkflowRequest>\n" +
                "         <web:in0>\n" +
                "            <web1:createTime>" + createTime + "</web1:createTime>\n" +
                "            <web1:creatorId>" + creatorId + "</web1:creatorId>\n" +
                "            <web1:requestLevel>" + requestLevel + "</web1:requestLevel>\n" +
                "            <web1:requestName>" + requestName + "</web1:requestName>\n" +
                "            <web1:workflowBaseInfo>\n" +
                "               <web1:workflowId>" + workflowBaseInfo.getWorkflowId() + "</web1:workflowId>\n" +
                "            </web1:workflowBaseInfo>\n" +
                "            <web1:workflowDetailTableInfos>\n" +
                "               <web1:WorkflowDetailTableInfo>\n" +
                "                  <web1:workflowRequestTableRecords>\n" +
                getDetailTable1Datas(yiqishebeizengzhiDetailTable1s) +
                "                  </web1:workflowRequestTableRecords>\n" +
                "               </web1:WorkflowDetailTableInfo>\n" +
                "            </web1:workflowDetailTableInfos>\n" +
                "            <web1:workflowMainTableInfo>\n" +
                "               <web1:requestRecords>\n" +
                getMainTableData(yiqishebeizengzhiMainTable) +
                "               </web1:requestRecords>\n" +
                "            </web1:workflowMainTableInfo>\n" +
                "         </web:in0>\n" +
                "         <web:in1>1</web:in1>\n" +
                "      </web:doCreateWorkflowRequest>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://192.168.1.108/services/WorkflowService";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        return getRequestId(response);
    }

    private String getMainTableData(yiqishebeizengzhiMainTable yiqishebeizengzhiMainTable) {
        return "                  <web1:WorkflowRequestTableRecord>\n" +
                "                     <web1:recordOrder>1</web1:recordOrder>\n" +
                "                     <web1:workflowRequestTableFields>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>bt</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getBt() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>sqbm</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getSqbm() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>sqr</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getSqr() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>sqrq</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getSqrq() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>lxfs</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getLxfs() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>cgyy</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getCgyy() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>ngmsbdjscsjyq</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getNgmsbdjscsjyq() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>xgfj</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getXgfj() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>xglc</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getXglc() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>lcbh</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getLcbh() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                        <web1:WorkflowRequestTableField>\n" +
                "                           <web1:edit>true</web1:edit>\n" +
                "                           <web1:fieldName>gc</web1:fieldName>\n" +
                "                           <web1:fieldValue>" + yiqishebeizengzhiMainTable.getGc() + "</web1:fieldValue>\n" +
                "                           <web1:view>true</web1:view>\n" +
                "                        </web1:WorkflowRequestTableField>\n" +
                "                     </web1:workflowRequestTableFields>\n" +
                "                  </web1:WorkflowRequestTableRecord>\n";
    }

    private String getDetailTable1Datas(yiqishebeizengzhiDetailTable1[] yiqishebeizengzhiDetailTable1s) {
        String datas = "";
        for (int i = 0; i < yiqishebeizengzhiDetailTable1s.length; i++) {
            yiqishebeizengzhiDetailTable1 yiqishebeizengzhiDetailTable1 = yiqishebeizengzhiDetailTable1s[i];
            datas += getDetailTable1Data(yiqishebeizengzhiDetailTable1, i);
        }
        return datas;
    }

    private String getDetailTable1Data(yiqishebeizengzhiDetailTable1 yiqishebeizengzhiDetailTable1, int recordOrder) {
        return "                     <web1:WorkflowRequestTableRecord>\n" +
                "                        <web1:recordOrder>" + recordOrder + "</web1:recordOrder>\n" +
                "                        <web1:workflowRequestTableFields>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>sbmc</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getSbmc() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>sbwh</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getSbwh() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>ggxh</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getGgxh() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>sbflmc</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getSbflmc() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>sbzlmc</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getSbzlmc() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>sl</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getSl() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>yqgjrq</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getYqgjrq() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>cbzx</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getCbzx() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>bz</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getBz() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>ybzje</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getYbzje() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>bbzje</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + yiqishebeizengzhiDetailTable1.getBbzje() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                        </web1:workflowRequestTableFields>\n" +
                "                     </web1:WorkflowRequestTableRecord>\n";
    }

    private String getRequestId(String response) {
        String requestid = "-1";
        try {
            Document dom = DocumentHelper.parseText(response);
            Element root = dom.getRootElement();
            Element doCreateWorkflowRequestResponse = root.element("Body").element("doCreateWorkflowRequestResponse");
            requestid = doCreateWorkflowRequestResponse.elementText("out");
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return requestid;
    }
}
