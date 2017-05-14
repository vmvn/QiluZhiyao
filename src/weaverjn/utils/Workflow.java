package weaverjn.utils;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.hrm.resource.ResourceComInfo;
import weaver.soa.workflow.request.MainTableInfo;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.RequestService;
import weaver.workflow.request.RequestManager;
import weaver.workflow.workflow.WorkflowComInfo;

import java.util.HashMap;
import java.util.Map;

public class Workflow {
	private RequestManager requestManager=null;
	private boolean iscreate=false;
	public RequestInfo requestinfo=null;
	private Map table=null;
	public String requestid="";
	public String nodeid="";
	public String status="";
	public String userid="";
	public String nodetype="";
	public String tablename="";
	public String billid="";
	public Workflow() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 实例化一个工作流
	 * @param id 工作流实例id
	 * @return Instance as Workflow
	 * @throws Exception 
	 */
	public static Workflow getInstance(String id,String userid) throws Exception
	{
		Workflow workflow=new Workflow();
		getManager(id,userid,workflow);
		return workflow;
	}
	
	/**
	 * 创建一个流程实例
	 * @param workflowtype 工作流类型
	 * @param instance 实例
	 * @return new Instance as Workflow
	 * @throws Exception 
	 */
	public static Workflow createInstance(String userid,String workflowid,String workflowname,Map instance)throws Exception
	{
		String Iscreate = "0";
		String id="";
        RequestService request=new RequestService();
        RequestInfo requestinfo =new RequestInfo();
        requestinfo.setCreatorid(userid);
        requestinfo.setWorkflowid(workflowid);
        requestinfo.setRequestlevel("0");
        requestinfo.setRemindtype("0");
        requestinfo.setDescription(workflowname);
        MainTableInfo table=new MainTableInfo();
        requestinfo.setMainTableInfo(table);
        requestinfo.setIsNextFlow("0");
        Object o[]=instance.keySet().toArray();
        for(int i=0;i<o.length;i++)
        {
        	Property p=new Property();
            p.setName(o[i]+"");
            //p.set_dt("1");
            p.setValue(Util.null2String((String) instance.get(o[i])));
            table.addProperty(p);
        }
        try {
			id=request.createRequest(requestinfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception(e.getMessage());
		}
		
		return getInstance(id,userid);
	}
	private static RequestManager getManager(String requestid,String userid,Workflow workflow)throws Exception
	{
		RequestInfo requestinfo=new RequestService().getRequest(Util.getIntValue(requestid));
		RecordSet rs=new RecordSet();
		rs.executeSql("select workflow_requestbase.*,workflow_form.billid from workflow_requestbase,workflow_form where workflow_requestbase.requestid=workflow_form.requestid and workflow_requestbase.requestid="+requestinfo.getRequestid());
		if(!rs.next())
		{
			throw new Exception("工作流实例不存在");
		}
		RequestManager manager=new RequestManager();
		//manager.setIsMultiDoc(isMultiDoc) ;
		manager.setSrc("submit") ;
		WorkflowComInfo workflowcominfo = new WorkflowComInfo();
		
		if(rs.getString("currentnodetype").equals("0"))
		{
			manager.setIscreate("0") ;//==============================
		}else
		{
			manager.setIscreate("0") ;//==============================
		}
		
		manager.setRequestid(Util.getIntValue(requestinfo.getRequestid())) ;
		manager.setWorkflowid(Util.getIntValue(requestinfo.getWorkflowid())) ;
		manager.setWorkflowtype(workflowcominfo.getWorkflowtype(requestinfo.getWorkflowid())) ;
		manager.setIsremark(0) ;
		manager.setFormid(Util.getIntValue(workflowcominfo.getFormId(requestinfo.getWorkflowid()))) ;
		manager.setIsbill(Util.getIntValue(workflowcominfo.getIsBill(requestinfo.getWorkflowid()))) ;
		manager.setBillid(Util.getIntValue(rs.getString("billid"))) ;
		if(manager.getIsbill()==1)
		{
			int tableid=manager.getFormid();
			if(tableid<0)tableid=0-tableid;
			workflow.tablename="formtable_main_"+tableid;
		}else
		{
			workflow.tablename="WORKFLOW_FORM";
		}
		manager.setNodeid(Util.getIntValue(rs.getString("currentnodeid"))) ;
		manager.setNodetype(rs.getString("currentnodetype")) ;
		manager.setRequestname(requestinfo.getDescription()) ;
		manager.setRequestlevel(requestinfo.getRequestlevel()) ;
		manager.setUser(SetCreater(Util.getIntValue(userid)));
		workflow.requestid=requestid;
		workflow.nodeid=rs.getString("currentnodeid");
		workflow.nodetype=rs.getString("currentnodetype");
		workflow.status=rs.getString("status");
		workflow.requestManager=manager;
		workflow.userid=userid;
		workflow.requestinfo=requestinfo;
		workflow.billid=rs.getString("billid");
		
		workflow.getTable();
		return manager;
	}
	public static User SetCreater(int i)
    {
        User user = new User();
        try
        {
            ResourceComInfo resourcecominfo = new ResourceComInfo();
            user.setUid(i);
            user.setLoginid(resourcecominfo.getLoginID(String.valueOf(i)));
            user.setFirstname(resourcecominfo.getFirstname(String.valueOf(i)));
            user.setLastname(resourcecominfo.getLastname(String.valueOf(i)));
            user.setLogintype("1");
            user.setSex(resourcecominfo.getSexs(String.valueOf(i)));
            user.setLanguage(7);
            user.setEmail(resourcecominfo.getEmail(String.valueOf(i)));
            user.setLocationid(resourcecominfo.getLocationid(String.valueOf(i)));
            user.setResourcetype(resourcecominfo.getResourcetype(String.valueOf(i)));
            user.setJobtitle(resourcecominfo.getJobTitle(String.valueOf(i)));
            user.setJoblevel(resourcecominfo.getJoblevel(String.valueOf(i)));
            user.setSeclevel(resourcecominfo.getSeclevel(String.valueOf(i)));
            user.setUserDepartment(Util.getIntValue(resourcecominfo.getDepartmentID(String.valueOf(i)), 0));
            user.setManagerid(resourcecominfo.getManagerID(String.valueOf(i)));
            user.setAssistantid(resourcecominfo.getAssistantID(String.valueOf(i)));
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return user;
    }
	/**
	 * 流程的下一步
	 * @return
	 * @throws Exception 
	 */
	public boolean next() throws Exception
	{
		boolean flag=requestManager.flowNextNode();
		getManager(this.requestid,this.userid,this);
		return flag;
	}
	public Map getTable()
	{
		this.table=new HashMap();
		MainTableInfo ti=requestinfo.getMainTableInfo();
		for(int i=0;i<ti.getPropertyCount();i++)
		{
			Property p=ti.getProperty(i);
			this.table.put(p.getName().toUpperCase(),p.getValue());
		}
		if(this.tablename.equals("WORKFLOW_FORM"))
		{
			this.table.put("REQUESTID",this.requestid);
		}else
		{
			
		}
		return this.table;
	}
	public String getValue(String columname)
	{
		MainTableInfo ti=requestinfo.getMainTableInfo();
		for(int i=0;i<ti.getPropertyCount();i++)
		{
			Property p=ti.getProperty(i);
			if(p.getName().toUpperCase().equals(columname.toUpperCase()))
			{
				return p.getValue();
				
			}
		}
		return null;
	}
	public void setValue(String columname,String value)
	{
		MainTableInfo ti=requestinfo.getMainTableInfo();
		for(int i=0;i<ti.getPropertyCount();i++)
		{
			Property p=ti.getProperty(i);
			if(p.getName().toUpperCase().equals(columname.toUpperCase()))
			{
				p.setValue(value);
				break;
			}
		}
		this.table.put(columname.toUpperCase(),value);
	}
//	public void update() throws Exception
//	{
//		if(this.tablename.equals("WORKFLOW_FORM"))
//		{
//			SessionFactory.getSession("",false).update(this.table,this.tablename,"requestid");
//		}else
//		{
//			SessionFactory.getSession("",false).update(this.table,this.tablename,"ID");
//		}
//		
//	}
	/**
	 * @param args
	 */
/*	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		com.cat.test.junit.Test.init();
		Workflow wf = new Workflow();
		Map<String,String> map = new HashMap<String,String>();
		map.put("accepterid", "1");
		map.put("subject", "ceshi");
//		Set set = map.entrySet();         
//		Iterator i = set.iterator();         
//		while(i.hasNext()){      
//		     Map.Entry<String, String> entry1=(Map.Entry<String, String>)i.next();    
//		     System.out.println(entry1.getKey()+"=="+entry1.getValue());    
//		}  
		try {
			wf.createInstance("1", "3", "innerceshi", map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.gc();
	}*/

}