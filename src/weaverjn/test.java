package weaverjn;

import weaver.general.Base64;

/**
 * Created by zhaiyaqi on 2017/6/26.
 */
public class test {
    public static void main(String[] args) {
        String s = "select * from (select rownum as r,t.* (select t1.requestid,\n" +
                "       t1.creater,\n" +
                "       t1.creatertype,\n" +
                "       t1.workflowid,\n" +
                "       t1.requestname,\n" +
                "       t2.userid,\n" +
                "       t2.receivedate,\n" +
                "       t2.receivetime,\n" +
                "       t2.viewtype,\n" +
                "       t2.isreminded,\n" +
                "       t2.workflowtype,\n" +
                "       t2.nodeid,\n" +
                "       t1.requestlevel,\n" +
                "       t2.isremark,\n" +
                "       t2.isprocessed,\n" +
                "       t2.agentorbyagentid,\n" +
                "       t2.agenttype,\n" +
                "       (case\n" +
                "         WHEN t2.operatedate IS NULL THEN\n" +
                "          t2.receivedate\n" +
                "         ELSE\n" +
                "          t2.operatedate\n" +
                "       END) operatedate,\n" +
                "       (case\n" +
                "         WHEN t2.operatetime IS NULL THEN\n" +
                "          t2.receivetime\n" +
                "         ELSE\n" +
                "          t2.operatetime\n" +
                "       END) operatetime\n" +
                "  from workflow_requestbase     t1,\n" +
                "       workflow_currentoperator t2,\n" +
                "       workflow_base            t3\n" +
                " where t1.requestid = t2.requestid\n" +
                "   and (t2.isremark in ('2', '4') or t2.isremark = '0' and takisremark = -2)\n" +
                "   and iscomplete = 1\n" +
                "   and t1.currentnodetype = '3'\n" +
                "   and t2.islasttimes = 1\n" +
                "   and t2.userid in (1)\n" +
                "   and t2.usertype = 0\n" +
                "   and (t1.deleted = 0 or t1.deleted is null)\n" +
                "   and t3.id = t2.workflowid\n" +
                "   and (t3.isvalid = '1' or t3.isvalid = '3')) t) tt\n";
        s = "select t1.workflowid,count(*) as num\n" +
                "  from workflow_requestbase     t1,\n" +
                "       workflow_currentoperator t2,\n" +
                "       workflow_base            t3\n" +
                " where t1.requestid = t2.requestid\n" +
                "   and (t2.isremark in ('2', '4') or t2.isremark = '0' and takisremark = -2)\n" +
                "   and iscomplete = 1\n" +
                "   and t1.currentnodetype = '3'\n" +
                "   and t2.islasttimes = 1\n" +
                "   and t2.userid in (1)\n" +
                "   and t2.usertype = 0\n" +
                "   and (t1.deleted = 0 or t1.deleted is null)\n" +
                "   and t3.id = t2.workflowid\n" +
                "   and (t3.isvalid = '1' or t3.isvalid = '3')\n" +
                " group by t1.workflowid";
        s = "SELECT t1.workflowid,count(*) as num\n" +
                "  FROM workflow_requestbase     t1,\n" +
                "       workflow_currentoperator t2,\n" +
                "       workflow_base            t3\n" +
                " WHERE t1.requestid = t2.requestid\n" +
                "   AND (t2.isremark = '0' AND (takisremark IS NULL OR takisremark = 0) OR\n" +
                "       t2.isremark = '1' OR t2.isremark = '5' OR t2.isremark = '7')\n" +
                "   AND t2.islasttimes = 1\n" +
                "   AND t2.userid IN (1453)\n" +
                "   AND t2.usertype = 0\n" +
                "   AND (t1.deleted = 0 OR t1.deleted IS NULL)\n" +
                "   AND t3.id = t2.workflowid\n" +
                "   AND (t3.isvalid = '1' OR t3.isvalid = '3')\n" +
                "   group by t1.workflowid";
        s = "SELECT t1.workflowid,count(*) as num,t3.workflowname\n" +
                "  FROM workflow_requestbase     t1,\n" +
                "       workflow_currentoperator t2,\n" +
                "       workflow_base            t3\n" +
                " WHERE t1.requestid = t2.requestid\n" +
                "   AND (t2.isremark = '0' AND (takisremark IS NULL OR takisremark = 0) OR\n" +
                "       t2.isremark = '1' OR t2.isremark = '5' OR t2.isremark = '7')\n" +
                "   AND t2.islasttimes = 1\n" +
                "   AND t2.userid IN (" + 1 + ")\n" +
                "   AND t2.usertype = 0\n" +
                "   AND (t1.deleted = 0 OR t1.deleted IS NULL)\n" +
                "   AND t3.id = t2.workflowid\n" +
                "   AND (t3.isvalid = '1' OR t3.isvalid = '3')\n" +
                "   group by t1.workflowid,t3.workflowname";
        s = "SELECT t1.workflowid,count(*) as num,t3.workflowname\n" +
                "  FROM workflow_requestbase     t1,\n" +
                "       workflow_currentoperator t2,\n" +
                "       workflow_base            t3\n" +
                " WHERE t1.requestid = t2.requestid\n" +
                "   AND (t2.isremark = '0' AND (takisremark IS NULL OR takisremark = 0) OR\n" +
                "       t2.isremark = '1' OR t2.isremark = '5' OR t2.isremark = '7')\n" +
                "   AND t2.islasttimes = 1\n" +
                "   AND t2.userid IN (" + 1 + ")\n" +
                "   AND t2.usertype = 0\n" +
                "   AND (t1.deleted = 0 OR t1.deleted IS NULL)\n" +
                "   AND t3.id = t2.workflowid\n" +
                "   AND (t3.isvalid = '1' OR t3.isvalid = '3')\n" +
                "   group by t1.workflowid,t3.workflowname";
        s = "select * from (select rownum as r, t.* from (SELECT t1.workflowid,\n" +
                "       t3.workflowname,\n" +
                "       t2.requestid,\n" +
                "       t2.receivedate,\n" +
                "       t2.receivetime,\n" +
                "       t1.createdate,\n" +
                "       t1.createtime,\n" +
                "       (select lastname\n" +
                "          from hrmresource\n" +
                "        union\n" +
                "        select lastname\n" +
                "          from HrmResourceManager\n" +
                "         where id = t1.creater) as lastname\n" +
                "  FROM workflow_requestbase     t1,\n" +
                "       workflow_currentoperator t2,\n" +
                "       workflow_base            t3\n" +
                " WHERE t1.requestid = t2.requestid\n" +
                "   AND (t2.isremark = '0' AND (takisremark IS NULL OR takisremark = 0) OR\n" +
                "       t2.isremark = '1' OR t2.isremark = '5' OR t2.isremark = '7')\n" +
                "   AND t2.islasttimes = 1\n" +
                "   AND t2.userid IN (1)\n" +
                "   AND t2.usertype = 0\n" +
                "   AND (t1.deleted = 0 OR t1.deleted IS NULL)\n" +
                "   AND t3.id = t2.workflowid\n" +
                "   AND (t3.isvalid = '1' OR t3.isvalid = '3')) t) tt\n";
        s = "select count(*) as n from (SELECT t1.workflowid,\n" +
                "       t3.workflowname,\n" +
                "       t2.requestid,\n" +
                "       t2.receivedate,\n" +
                "       t2.receivetime,\n" +
                "       t1.createdate,\n" +
                "       t1.createtime,\n" +
                "       (select lastname\n" +
                "          from hrmresource\n" +
                "        union\n" +
                "        select lastname\n" +
                "          from HrmResourceManager\n" +
                "         where id = t1.creater) as lastname\n" +
                "  FROM workflow_requestbase     t1,\n" +
                "       workflow_currentoperator t2,\n" +
                "       workflow_base            t3\n" +
                " WHERE t1.requestid = t2.requestid\n" +
                "   AND (t2.isremark = '0' AND (takisremark IS NULL OR takisremark = 0) OR\n" +
                "       t2.isremark = '1' OR t2.isremark = '5' OR t2.isremark = '7')\n" +
                "   AND t2.islasttimes = 1\n" +
                "   AND t2.userid IN (1)\n" +
                "   AND t2.usertype = 0\n" +
                "   AND (t1.deleted = 0 OR t1.deleted IS NULL)\n" +
                "   AND t3.id = t2.workflowid\n" +
                "   AND (t3.isvalid = '1' OR t3.isvalid = '3')) t\n";

        System.out.println(s);
    }
}
