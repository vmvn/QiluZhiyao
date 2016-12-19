package weaverjn.qlzy.sap.webservice.oa.workflow;

import java.util.ArrayList;

/**
 * Created by zhaiyaqi on 2016/12/15.
 */
public interface ZGSDWWXSQ {
    //总公司对外维修申请
    ArrayList<ZGSDWWXSQrequestid> createWorkflow(ZGSDWWXSQparameters parameters);
}
