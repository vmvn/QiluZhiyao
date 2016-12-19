package weaverjn.qlzy.sap.webservice.oa.workflow;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaverjn.qlzy.sap.WSClientUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaiyaqi on 2016/12/15.
 */
public class ZSGDWWXSQimpl extends BaseBean implements ZGSDWWXSQ {
    @Override
    public ArrayList<ZGSDWWXSQrequestid> createWorkflow(ZGSDWWXSQparameters parameters) {
        ZGSDWWXSQparameter[] p = parameters.getZGSDWWXSQparameters();
        workflowBaseInfo w = parameters.getWorkflowBaseInfo();
        Map<String, ArrayList<ZGSDWWXSQparameter>> m = new HashMap<String, ArrayList<ZGSDWWXSQparameter>>();
        for (ZGSDWWXSQparameter i : p) {
            String REQ_NO = i.getREQ_NO();
            if (m.containsKey(REQ_NO)) {
                ArrayList<ZGSDWWXSQparameter> a = m.get(REQ_NO);
                a.add(i);
                m.put(REQ_NO, a);
            } else {
                ArrayList<ZGSDWWXSQparameter> a = new ArrayList<ZGSDWWXSQparameter>();
                a.add(i);
                m.put(REQ_NO, a);
            }
        }
        ArrayList<ZGSDWWXSQrequestid> requestids = new ArrayList<ZGSDWWXSQrequestid>();
        for (Map.Entry<String, ArrayList<ZGSDWWXSQparameter>> entry : m.entrySet()) {
            ZGSDWWXSQrequestid requestid = new ZGSDWWXSQrequestid();
            requestid.setREQ_NO(entry.getKey());
            requestid.setRequestid(doCreateWorkflow(entry.getValue(), w));
            requestids.add(requestid);
        }
        return requestids;
    }

    private String doCreateWorkflow(ArrayList<ZGSDWWXSQparameter> a, workflowBaseInfo w) {
        String createTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String creatorId = "";
        String requestLevel = "";
        String requestName = "";
        String mainTableData = "";
        String detailTableData = "";
        String workflowId = w.getWorkflowId();

        for (ZGSDWWXSQparameter i : a) {
            if (a.indexOf(i) == 0) {
                requestName = i.getRequestName();
                String AGENT = i.getAGENT();
                creatorId = getUserIDByWorkCode(AGENT);
                mainTableData = "                  <web1:WorkflowRequestTableRecord>\n" +
                        "                     <web1:recordOrder>1</web1:recordOrder>\n" +
                        "                     <web1:workflowRequestTableFields>\n" +
                        "                        <web1:WorkflowRequestTableField>\n" +
                        "                           <web1:edit>true</web1:edit>\n" +
                        "                           <web1:fieldName>jbr</web1:fieldName>\n" +
                        "                           <web1:fieldValue>" + creatorId + "</web1:fieldValue>\n" +
                        "                           <web1:view>true</web1:view>\n" +
                        "                        </web1:WorkflowRequestTableField>\n" +
                        "                        <web1:WorkflowRequestTableField>\n" +
                        "                           <web1:edit>true</web1:edit>\n" +
                        "                           <web1:fieldName>cgsqh</web1:fieldName>\n" +
                        "                           <web1:fieldValue>" + i.getREQ_NO() + "</web1:fieldValue>\n" +
                        "                           <web1:view>true</web1:view>\n" +
                        "                        </web1:WorkflowRequestTableField>\n" +
                        "                        <web1:WorkflowRequestTableField>\n" +
                        "                           <web1:edit>true</web1:edit>\n" +
                        "                           <web1:fieldName>sqrq</web1:fieldName>\n" +
                        "                           <web1:fieldValue>" + createTime + "</web1:fieldValue>\n" +
                        "                           <web1:view>true</web1:view>\n" +
                        "                        </web1:WorkflowRequestTableField>\n" +
                        "                        <web1:WorkflowRequestTableField>\n" +
                        "                           <web1:edit>true</web1:edit>\n" +
                        "                           <web1:fieldName>sqbm</web1:fieldName>\n" +
                        "                           <web1:fieldValue>" + getDepartmentIDByUserID(creatorId) + "</web1:fieldValue>\n" +
                        "                           <web1:view>true</web1:view>\n" +
                        "                        </web1:WorkflowRequestTableField>\n" +
                        "                     </web1:workflowRequestTableFields>\n" +
                        "                  </web1:WorkflowRequestTableRecord>\n";
            }
            detailTableData += getDetailData(i);
        }

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
                "               <web1:workflowId>" + workflowId + "</web1:workflowId>\n" +
                "            </web1:workflowBaseInfo>\n" +
                "            <web1:workflowDetailTableInfos>\n" +
                "               <web1:WorkflowDetailTableInfo>\n" +
                "                  <web1:workflowRequestTableRecords>\n" +
                detailTableData +
                "                  </web1:workflowRequestTableRecords>\n" +
                "               </web1:WorkflowDetailTableInfo>\n" +
                "            </web1:workflowDetailTableInfos>\n" +
                "            <web1:workflowMainTableInfo>\n" +
                "               <web1:requestRecords>\n" +
                mainTableData +
                "               </web1:requestRecords>\n" +
                "            </web1:workflowMainTableInfo>\n" +
                "         </web:in0>\n" +
                "         <web:in1>1</web:in1>\n" +
                "      </web:doCreateWorkflowRequest>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        writeLog(request);
        HashMap<String, String> httpHeaderParm = new HashMap<String, String>();
        String url = "http://192.168.1.108/services/WorkflowService";
        httpHeaderParm.put("instId", "10062");
        httpHeaderParm.put("repairType", "RP");
        String response = WSClientUtils.callWebServiceWithHttpHeaderParm(request, url, httpHeaderParm);
        writeLog(response);
        return getRequestId(response);
    }

    private String getDetailData(ZGSDWWXSQparameter n) {
        return "                     <web1:WorkflowRequestTableRecord>\n" +
                "                        <web1:recordOrder>" + n.getREQ_ITEM() + "</web1:recordOrder>\n" +
                "                        <web1:workflowRequestTableFields>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>cgsqhh</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getREQ_ITEM() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>sqr</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + getUserIDByWorkCode(n.getREQUISITIONER()) + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>jfrq</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getDELIVERY_DATE() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>fwhh</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getSERVICE_ITEM() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>fwms</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getITEM_DESC() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>sl</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getQUANTITY() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>jhjg</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getPLAN_PRICE() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>gdh</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getAUFNR() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>sbbh</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getEQUNR() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>sbms</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getEQKT() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>gnwz</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getTPLNR() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                           <web1:WorkflowRequestTableField>\n" +
                "                              <web1:edit>true</web1:edit>\n" +
                "                              <web1:fieldName>gnwzms</web1:fieldName>\n" +
                "                              <web1:fieldValue>" + n.getPLTXT() + "</web1:fieldValue>\n" +
                "                              <web1:view>true</web1:view>\n" +
                "                           </web1:WorkflowRequestTableField>\n" +
                "                        </web1:workflowRequestTableFields>\n" +
                "                     </web1:WorkflowRequestTableRecord>\n";
    }
    private String getUserIDByWorkCode(String workcode) {
        RecordSet recordSet = new RecordSet();
        String sql = "select id from hrmresource where workcode='" + workcode + "'";
        recordSet.executeSql(sql);
        String userid = "";
        if (recordSet.next()) {
            userid = Util.null2String(recordSet.getString("id"));
        }
        return userid;
    }

    private String getDepartmentIDByUserID(String userid) {
        RecordSet recordSet = new RecordSet();
        String sql = "select departmentid from hrmresource where id=" + userid;
        recordSet.executeSql(sql);
        String departmentid = "";
        if (recordSet.next()) {
            departmentid = Util.null2String(recordSet.getString("departmentid"));
        }
        return departmentid;
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
