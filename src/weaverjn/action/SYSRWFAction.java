package weaverjn.action;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;

/**
 * Created by dzyq on 2016/6/21.
 */
public class SYSRWFAction extends BaseAction{
    public String execute(RequestInfo requestInfo) {
        RequestManager requestManager = requestInfo.getRequestManager();
        String name = requestManager.getRequestname();
        String requestid = requestInfo.getRequestid();
        System.out.println("---->" + name);
        if (name.startsWith("会议通知:")) {
            RecordSet rs = new RecordSet();
            String sql = "select relatmeeting from workflow_form where requestid=" + requestid;
            String meetingid = "";
            rs.executeSql(sql);
            if (rs.next()) {
                meetingid = Util.null2String(rs.getString("relatmeeting"));
            }
            String script = "<script language=javascript>" +
                    "var diag_vote;\n" +
                    "function showDialog(url, title, w,h){\n" +
                    "\tif(window.top.Dialog){\n" +
                    "\t\tdiag_vote = new window.top.Dialog();\n" +
                    "\t} else {\n" +
                    "\t\tdiag_vote = new Dialog();\n" +
                    "\t}\n" +
                    "\tdiag_vote.currentWindow = window;\n" +
                    "\tdiag_vote.Width = w;\n" +
                    "\tdiag_vote.Height = h;\n" +
                    "\tdiag_vote.Modal = true;\n" +
                    "\tdiag_vote.Title = title;\n" +
                    "\tdiag_vote.maxiumnable = true;\n" +
                    "\tdiag_vote.URL = url;\n" +
                    "\tdiag_vote.show();\n" +
                    "}\n" +
                    "\n" +
                    "function onShowReHrm(recorderid,meetingid){\n" +
                    "\tshowDialog(''/meeting/data/MeetingOthTab.jsp?toflag=ReHrm&recorderid=''+recorderid+''&meetingid=''+meetingid,''会议参与回执'', 600, 500);\n" +
                    "}\n" +
                    "\n" +
                    "function doreplay(){\n" +
                    "\tjQuery.ajax({\n" +
                    "\t\turl:''/weaverjn/meeting/getRecordid.jsp'',\n" +
                    "\t\ttype:''post'',\n" +
                    "\t\tdata:{\n" +
                    "\t\t\tmeetingid:"+meetingid+",\n" +
                    "\t\t\tmemberid:window.__userid\n" +
                    "\t\t},\n" +
                    "\t\tbeforeSend:function(){\n" +
                    "\t\t\ttry{\n" +
                    "\t\t\t\te8showAjaxTips(''请稍后...'',true);\n" +
                    "\t\t\t}catch(e){}\n" +
                    "\t\t},\n" +
                    "\t\tcomplete:function(){\n" +
                    "\t\t\te8showAjaxTips('''',false);\n" +
                    "\t\t},\n" +
                    "\t\tsuccess:function(data){\n" +
                    "\t\t\tif(data!=0){\n" +
                    "\t\t\t\tonShowReHrm(data,"+meetingid+")\n" +
                    "\t\t\t}else{\n" +
                    "\t\t\t\ttop.Dialog.alert(''读取recordid失败！'');\n" +
                    "\t\t\t}\n" +
                    "\t\t}\n" +
                    "\t});\n" +
                    "}" +
                    "</script>";
            String remark = "<a href=''javascript:void(0)'' onclick=''doreplay()''>回执</a>";
            remark += script;
            sql = "update workflow_form set remark='" + remark + "' where requestid=" + requestid;
            System.out.println("---->sql:" + sql);
            rs.executeSql(sql);
        }
        return SUCCESS;
    }
}
