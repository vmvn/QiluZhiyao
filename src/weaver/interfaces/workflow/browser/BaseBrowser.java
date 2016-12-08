package weaver.interfaces.workflow.browser;

import java.io.StringReader;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import weaver.conn.ConnStatement;
import weaver.conn.ConnectionPool;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.SoapService;
import weaver.general.Util;
import weaver.interfaces.datasource.DataSource;
import weaver.servicefiles.BrowserXML;
import weaver.workflow.dmlaction.DBTypeUtil;
import weaver.workflow.workflow.WorkflowBillComInfo;
import weaver.wsclient.bean.MethodBean;
import weaver.wsclient.util.WSDLFacade;

/**
 * User: xiaofeng.zhang
 * Date: 2007-10-12
 * Time: 17:41:57
 */
public class BaseBrowser extends BaseBean implements Browser {
    private Log log = LogFactory.getLog(Browser.class.getName());
    private DataSource ds;
    private String name = "";
    private String customid = "";
    private String search = "";
    private String tempsearch = "";
    private String searchById = "";
    private String searchByName = "";
    private String nameHeader;
    private String descriptionHeader;

    private String parentfield = "";
    private String keyfield = "";
    private String namefield = "";
    
    private String outPageURL;
    private String from;//1 代表来自模块的自定义浏览按钮，其它值代表用户自己创建的
    private String href;//页面打开的地址
    
    Map paramvalues = new HashMap();
    
    private String showname = "";
    private int showclass = -1;
    private int showtype = -1;
    private String showtree;
    private String nodename;
    private String parentid;
    private String ismutil;
    private String datasourceid;
    private int datafrom = -1;
    private String customhref = "";
    
    private String wsurl = "";//web service地址
    private String wsoperation = "";//调用的web service的方法
	private String wsworkname = "";//调用的web service空间名称
    private String returntype = "";
    private Map values = new LinkedHashMap();
	
    private Map showfieldMap = new LinkedHashMap();
    private Map showfieldTranMap = new LinkedHashMap();
    
    private Map searchfieldMap = new LinkedHashMap();
    private Map searchfieldTypeMap = new LinkedHashMap();
    //private Map searchValueMap = new LinkedHashMap();
    private Map wokflowfieldnameMap = new LinkedHashMap();
    
    private Map chineseStrMap = new LinkedHashMap();
    
    private int parserChineseStringIndex = 0;//中文索引
    
    private Map specialStrMap = new LinkedHashMap();
    
    private int parserSpecialStringIndex = 0;//特殊字符索引
    
    private boolean isMobile =false;
    
    private Map requestFormInfoMap = new HashMap();//流程信息
    
    public synchronized boolean initBaseBrowser(String id,String type,String from)
    {
    	if(((!"".equals(type)&&"2".equals(from))||!"".equals(id))&&!"".equals(from))
    	{
    		search = "";
    		tempsearch = "";
    		this.from = "2";
    		String newtype = type.replaceAll("browser\\.", "");//177842 解决不能使用browser为前缀命名的问题
    		RecordSet rs = new RecordSet();
    		RecordSet rs2 = new RecordSet();
    		
    		
    		String sqltext = "";
    		
    		int selecttype = -1;
    		String xmltext = "";
    		
    		String showfield = "";
    		
			String sql = "";
			if(!"".equals(id))
				sql = "select * from datashowset where id='"+id+"' and showclass=2 ";
			else
				sql = "select * from datashowset where showname='"+newtype+"' and showclass=1 ";
			
			//System.out.println("type : "+type+"  from : "+from+"   "+sql);
			rs.executeSql(sql);
			if(rs.next())
			{
				id = Util.null2String(rs.getString("id"));
				name = Util.null2String(rs.getString("name"));
				showname = Util.null2String(rs.getString("showname"));
				wsworkname = Util.null2String(rs.getString("wsworkname"));
				
				showclass = Util.getIntValue(rs.getString("showclass"),0);
				datafrom = Util.getIntValue(rs.getString("datafrom"),-1);
				datasourceid = Util.null2String(rs.getString("datasourceid"));
				sqltext = Util.null2String(rs.getString("sqltext"));
				searchById = Util.null2String(rs.getString("searchById"));
				wsurl = Util.null2String(rs.getString("wsurl"));
				customhref = Util.null2String(rs.getString("customhref"));
				wsoperation = Util.null2String(rs.getString("wsoperation"));
				if(datafrom==0)
				{
					WSDLFacade WSDLFacade = new WSDLFacade();
					wsurl = WSDLFacade.getWebserviceUrlFromDB(wsurl);
					MethodBean mb = WSDLFacade.getWSMethodFromDB(wsoperation);
					values = WSDLFacade.getWSMethodParamValueFromDB(wsoperation, "6", ""+id);
					
					paramvalues = (Map)values.get("value");
					wsoperation = mb.getMethodname();
					returntype = mb.getMethodreturntype();
				}
				xmltext = Util.null2String(rs.getString("xmltext"));
				
				showtype = Util.getIntValue(rs.getString("showtype"),0);
				selecttype = Util.getIntValue(rs.getString("selecttype"),0);
				keyfield = Util.null2String(rs.getString("keyfield"));
				if(!"".equals(keyfield))
					searchfieldMap.put(keyfield, "");
				if(!"".equals(keyfield))
					searchfieldTypeMap.put(keyfield, "key");
				
				parentfield = Util.null2String(rs.getString("parentfield"));
				showfield = Util.null2String(rs.getString("showfield"));
				
				outPageURL = Util.null2String(rs.getString("showpageurl"));
				href = Util.null2String(rs.getString("detailpageurl"));
				//System.out.println("showclass : "+showclass+" showtype : "+showtype);
				//System.out.println("showclass : "+showclass+"  showtype : "+showtype);
				if(showclass==2||(showclass==1&&showtype==1))
				{
					//System.out.println("SELECT * FROM datasearchparam where mainid="+id+" order by id");
					search = "select "+keyfield;
					rs2.executeSql("SELECT * FROM datasearchparam where mainid="+id+" order by id");
					
					while (rs2.next()) 
					{
				        String fieldname = Util.null2String(rs2.getString("fieldname"));
				        String searchname = Util.null2String(rs2.getString("searchname"));
				        String wokflowfieldname = Util.null2String(rs2.getString("wokflowfieldname"));
				        
				        //解决sql中带回车问题
				        wokflowfieldname = wokflowfieldname.replaceAll("\r", " ").replaceAll("\n", " ");
				        wokflowfieldname = wokflowfieldname.trim();
				        
				        searchfieldMap.put(searchname, fieldname);
				        String fieldtype = Util.null2String(rs2.getString("fieldtype"));
				        //System.out.println("111 fieldname : "+fieldname+"  searchname : "+searchname+" fieldtype : "+fieldtype );
				        searchfieldTypeMap.put(searchname, fieldtype);
				        wokflowfieldnameMap.put(searchname, wokflowfieldname);
					}
					
					
					rs2.executeSql("SELECT * FROM datashowparam where mainid="+id+" order by isshowname desc, id");
					while (rs2.next()) 
					{
				        String fieldname = Util.null2String(rs2.getString("fieldname"));
				        String searchname = Util.null2String(rs2.getString("searchname"));
				        String isshowname = Util.null2String(rs2.getString("isshowname"));
				        if("".equals(namefield)&&"1".equals(isshowname))
				        	namefield = searchname;
				        String transql = Util.null2String(rs2.getString("transql"));
				        //System.out.println("222 searchname : "+searchname+" fieldname : "+fieldname);
				        
				        showfieldMap.put(searchname, fieldname);
				        showfieldTranMap.put(searchname, transql);
				        
				        if(keyfield.equalsIgnoreCase(searchname)&&1==datafrom)
				        {
				        	continue;
				        }
				        if(1==datafrom)
				        	search += ","+searchname;
					}
					if(1==datafrom){
						String select = sqltext.substring(0,sqltext.toLowerCase().indexOf("from"));
						if(select.indexOf("*")>0 || select.matches("^[\\W\\w]*[ ,.\\s]+"+keyfield+"[ ,\\s]+[\\W\\w]*$")){//select头中是否有主键
							search = sqltext;//+= " "+sqltext.substring(sqltext.toLowerCase().indexOf("from"),sqltext.length());
						}else{
							if(!keyfield.equals("")){
							search = select+","+keyfield+" "+sqltext.substring(sqltext.toLowerCase().indexOf("from"),sqltext.length());
							}else{
								search = sqltext;
							}
						}
					}
				}
				else if(showclass==1&&showtype==2)
				{
					//树状显示
					//System.out.println("SELECT * FROM datashowparam where mainid="+id+" order by id");
					if(1==datafrom)
					{
						search = "select "+keyfield+","+showfield+","+parentfield;
						if(keyfield.equalsIgnoreCase(showfield)&&1==datafrom)
				        {
							search = "select "+keyfield+","+parentfield;
				        }
						search += " "+sqltext.substring(sqltext.toLowerCase().indexOf("from"),sqltext.length());
					}
					if("".equals(namefield))
			        	namefield = showfield;
					//System.out.println("333 showfield : "+showfield+" keyfield : "+keyfield+" datafrom : "+datafrom);
					if(!"".equals(keyfield)&&(0==datafrom||2==datafrom))
					{
						showfieldMap.put(keyfield, "");
					}
					if(!"".equals(parentfield)&&(0==datafrom||2==datafrom))
					{
						showfieldMap.put(parentfield, "");
					}
					showfieldMap.put(showfield, "");
					//System.out.println("initBaseBrowser keys : "+showfieldMap.keySet());
					searchfieldMap.put(parentfield, "");
			        searchfieldTypeMap.put(parentfield, "1");
				}
				//解决sql中带回车问题
				search = search.replaceAll("\r", " ").replaceAll("\n", " ");
				tempsearch = searchById.replaceAll("\r", " ").replaceAll("\n", " ");
				//System.out.println("init 123  "+search);
				return true;
			}
    	}
    	if((!"".equals(type)&&!"2".equals(from)))
    	{
    		search = "";
    		this.from = from;
    		String newtype = type.replaceAll("browser.", "");
    		
    		if(!type.equals("")){
	    		if(!"".equals(newtype))
	    		{
	    			BrowserXML browserxml=new BrowserXML();
	    			Hashtable dataHST = browserxml.getDataHST();
	    			  Hashtable thisDetailHST = (Hashtable)dataHST.get(newtype);
	    			    if(thisDetailHST!=null){
	    			    	search= (String)thisDetailHST.get("search");
	    			    	searchById = (String)thisDetailHST.get("searchById");
	    			    	searchByName = (String)thisDetailHST.get("searchByName");
	    			    }
	    		}
    		}
    		return true;
    	}
    	return false;
    }
    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public DataSource getDs() {
        return ds;
    }

    public void setDs(DataSource ds) {
        this.ds = ds;
    }

    public String getSearch() {
        return search;
    }
	
	public String getSearch(String userid) {
    	String search_temp = this.search;
    	if(search_temp.indexOf("$userid$")>-1) search_temp = search_temp.replace("$userid$",userid);
        return search_temp;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSearchById() {
        return searchById;
    }

    public void setSearchById(String searchById) {
        this.searchById = searchById;
    }

    public String getSearchByName() {
        return searchByName;
    }

    public void setSearchByName(String searchByName) {
        this.searchByName = searchByName;
    }

    public String getNameHeader() {
        return nameHeader;
    }

    public void setNameHeader(String nameHeader) {
        this.nameHeader = nameHeader;
    }

    public String getDescriptionHeader() {
        return descriptionHeader;
    }

    public void setDescriptionHeader(String descriptionHeader) {
        this.descriptionHeader = descriptionHeader;
    }
    
    public String getOutPageURL() {
		return outPageURL;
	}

	public void setOutPageURL(String outPageURL) {
		this.outPageURL = outPageURL;
	}
	
    public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public List search() {
    	return search("");
    }
    
    public List search(String userid) {
    	return search(userid,getSearch());
    }
    /**
	 * 获取数据
	 * @param lm
	 * @param synts
	 * @return
	 */
	private List getData(String intetype,String userid,String sql,Map searchValueMap)
	{
		if("1".equals(intetype))
			return this.getDataFromSql(userid,sql,showfieldMap,showfieldTranMap,searchValueMap);
		else if("0".equals(intetype))
		{
			//System.out.println("getData keys : "+showfieldMap.keySet());
			return this.getDataFromWS(userid,wsurl,wsoperation,values,showfieldMap,showfieldTranMap,searchValueMap);
			
		}
		else if("2".equals(intetype))
		{
			//System.out.println("getData keys : "+showfieldMap.keySet());
			return this.getDataFromHTTP(userid,customhref,showfieldMap,showfieldTranMap,searchValueMap);
			
		}
		return null;
	}
	
	
    /**
	 * 获取并且拼装数据
	 * @param syncrs
	 * @param fieldMap
	 * @param tranMap
	 * @return
	 */
	private List getDataFromSql(String userid,String sql,Map fieldMap,Map tranMap,Map searchValueMap)
	{
		List dataList = new ArrayList();
		String dbtype = "";
		Connection conn = null;
		boolean isconn = false;
        if (getDs() == null) {
        	isconn = true;
			ConnectionPool pool = ConnectionPool.getInstance();
			conn = pool.getConnection();
			dbtype = pool.getDbtype();
		}
        else
        {
        	isconn = true;
        	conn = getDs().getConnection();
        	dbtype = getDs().getType();
        }
        
        List l = new ArrayList();
        try 
        {
        	if(sql.indexOf("$userid$")>-1) sql = sql.replace("$userid$",userid);
        	//System.out.println("1 getDataFromSql sql : "+sql);
        	if(Util.getIntValue(userid)>0){
        		sql = this.replaceDefaultValue(userid, sql);
        	}else{
        		
        	}
            if(null!=conn)
            {
            	if("2".equals(from))
            		sql = getSearchSql(dbtype,sql,searchValueMap);
            	//System.out.println("2 getDataFromSql sql : "+sql);
            	if(parserOrder(sql).indexOf(" ORDER BY ")==-1)
            	{
            		if(keyfield.equalsIgnoreCase(namefield))
            		{
            			sql += " ORDER BY "+keyfield;
            		}else{
            			sql += " ORDER BY "+keyfield+","+namefield;
            		}
            	}
            	//恢复sql的中文
            	sql = renewChineseStrSearch(sql);
            	//恢复sql中的oracle特殊字符串
            	sql = renewOracleSpecialStrSearch(sql);
            	
            	sql = runSqlBeforePro(sql);
            	this.writeLog(this.name+": getDataFromSql sql : "+sql);
            	//System.out.println("3 getDataFromSql sql : "+sql);
	            PreparedStatement s = conn.prepareStatement(sql);
	            if("2".equals(from))
            		this.setPreparedStatement(s,searchValueMap);
	            ResultSet rs = s.executeQuery();
	            while (rs.next()) {
					Map valueMap = new HashMap();
					valueMap.put(keyfield, rs.getString(keyfield));
					if(!parentfield.equals("")){
					valueMap.put(parentfield, Util.null2String(rs.getString(parentfield)));
					}
					Set keys = fieldMap.keySet();
					for(Iterator i = keys.iterator();i.hasNext();)
					{
						String key = Util.null2String((String)i.next());
						String value = "";
						String transql = Util.null2String((String)tranMap.get(key));
						if(!"".equals(key))
						{
							value = decode(Util.null2String(rs.getString(key)));
						}
						else
						{
							value = "";
						}
						if(!"".equals(transql))
						{
							value = getTranSqlValue(conn,transql,value);
						}
						//System.out.println("222 key : "+key+"  value : "+value);
						valueMap.put(key, value);
					}
					dataList.add(valueMap);
	            }
            }
            else if(!isconn)
            {
            	RecordSet rs = new RecordSet();
            	sql = getSearchSqlByValue(rs.getDBType(),sql,searchValueMap);
            	rs.executeSql(sql);
 	            while (rs.next()) {
 	            	Map valueMap = new HashMap();
 	            	valueMap.put(rebuildField(keyfield), rs.getString(rebuildField(keyfield)));
					Set keys = fieldMap.keySet();
					for(Iterator i = keys.iterator();i.hasNext();)
					{
						String key = Util.null2String((String)i.next());
						String value = "";
						String transql = Util.null2String((String)tranMap.get(key));
						if(!"".equals(key))
						{
							value = decode(Util.null2String(rs.getString(key)));
						}
						else
						{
							value = "";
						}
						if(!"".equals(transql))
						{
							value = getTranSqlValue(conn,transql,value);
						}
						valueMap.put(key, value);
					}
					dataList.add(valueMap);
 	            }
            }
		}
        catch (Exception e) {
        	e.printStackTrace();
            getLog().error(e);
        } finally {
            try {
            	if(null!=conn)
            	{
            		//关闭连接
            		conn.close();
            	}
            } catch (Exception e) {
                getLog().error(e);
            }
        }
		return dataList;
	}
	/**
	 * 重构字段名
	 * @param fieldname
	 * @return
	 */
	private String rebuildField(String fieldname){
		return fieldname.substring(fieldname.lastIndexOf(".")+1);
	}
    /**
	 * 获取webservice结果并且拼装数据
	 * @param lm
	 * @param synts
	 * @param webserviceurl
	 * @param mothod
	 * @param params
	 * @param pvalues
	 * @param fieldMap
	 * @param tranMap
	 * @return
	 */
	private List getDataFromWS(String userid,String webserviceurl,String mothod,Map pvalues,Map fieldMap,Map tranMap,Map searchValueMap)
	{
		List dataList = new ArrayList();
		Map newpvalues = this.replaceDefaultValueMap(userid, pvalues);
		//System.out.println("getDataFromWS webserviceurl : "+webserviceurl+" mothod : "+mothod);
		String result = "";
		if(wsworkname.equals("")){
			result=SoapService.serviceSend(webserviceurl, mothod,newpvalues,returntype);
		}else{
			result=SoapService.serviceSend(webserviceurl,wsworkname,mothod,newpvalues,returntype);
		}
		//String result = SoapService.serviceSend(webserviceurl, mothod,newpvalues,returntype);
		//System.out.println("getDataFromWS result : "+result);
		dataList = this.parseResultXML(result, fieldMap, tranMap,searchValueMap);
		return dataList;
	}
	/**
	 * 获取http请求结果并且拼装数据
	 * @param userid
	 * @param customhref
	 * @param fieldMap
	 * @param tranMap
	 * @return
	 */
	public List getDataFromHTTP(String userid,String customhref,Map fieldMap,Map tranMap,Map searchValueMap)
	{
		List dataList = new ArrayList();
		String newcustomhref = this.replaceDefaultValue(userid, customhref);
		String result = BrowserIOServlet.sendGet(newcustomhref, "");
		result = URLDecoder.decode(result);
		writeLog("newcustomhref:" + newcustomhref);
		dataList = this.parseResultXML(result, fieldMap, tranMap,searchValueMap);
		return dataList;
	}
	/**
	 * 解析xml，得到数据结果
	 * @param result
	 * @param fieldMap
	 * @param tranMap
	 * @return
	 */
	private List parseResultXML(String result,Map fieldMap,Map tranMap,Map searchValueMap)
	{
		List dataList = new ArrayList();
		if(!result.equals(""))
		{
			Set keys = fieldMap.keySet();
			//System.out.println("getDataFromWS keys : "+keys);
			Map valueMap = new HashMap();
			int colsize = 0;
			for(Iterator i = keys.iterator();i.hasNext();)
			{
				String key = Util.null2String((String)i.next());
				//System.out.println("getDataFromWS key : "+key);
				List values = SoapService.parseServiceResult(result,"//" + key);
				if(null!=values)
				{
					colsize = values.size();
					for(int j = 0;j<values.size();j++)
					{
						String value = Util.null2String((String)values.get(j));
						String transql = Util.null2String((String)tranMap.get(key));
						if(!"".equals(transql))
						{
							value = getTranSqlValue(null,transql,value);
						}
						values.set(j, value);
					}
					valueMap.put(key, values);
				}
			}
			
			if(colsize>0)
			{
				for(int k = 0;k<colsize;k++)
				{
					try
					{
						Map tempvalueMap = new HashMap();
						for(Iterator i = keys.iterator();i.hasNext();)
						{
							String key = Util.null2String((String)i.next());
							List values = (List)valueMap.get(key);
							String value = Util.null2String((String)values.get(k));
							//System.out.println("getDataFromWS key : "+key+" value : "+value);
							tempvalueMap.put(key, value);
						}
						dataList.add(tempvalueMap);
					}
					catch(Exception e)
					{
						return null;
					}
				}
			}
		}
		if(null!=searchfieldMap&&null!=searchValueMap&&searchValueMap.size()>0)
    	{
    		Set keyset = searchfieldMap.keySet();
    	    for(Iterator it = keyset.iterator();it.hasNext();)
    	    {
    	    	String keyname = (String)it.next();
    	    	String keyvalue = Util.null2String((String)searchValueMap.get(keyname)).trim();
    	    	
    	    	if(!"".equals(keyvalue))
    	    	{
    	    		Map tempvalueMap = new HashMap();
    	    		String tempvalue = "";
    	    		for(int dd = 0;dd<dataList.size();dd++)
    				{
    	    			tempvalueMap = null;
    	    			tempvalue = "";
    	    			tempvalueMap = (Map)dataList.get(dd);
    	    			if(null!=tempvalueMap)
    	    			{
	    	    			tempvalue = Util.null2String((String)tempvalueMap.get(keyname)).trim();
	    	    			if(keyname.equalsIgnoreCase(parentfield))
	    	    			{
	    	    				if("".equals(keyvalue)||"0".equals(keyvalue))
	    	    				{
	    	    					//System.out.println("111 keyname : "+keyname+" keyvalue : "+keyvalue+" tempvalue : "+tempvalue+" keyname : "+keyname);
	    	    					if(!"".equals(tempvalue)&&!"0".equals(tempvalue))
			    	    			{
	    	    						dataList.set(dd, null);
			    	    			}
	    	    				}
	    	    				else
		    	    			{
	    	    					//System.out.println("222 : "+keyname+" keyvalue : "+keyvalue+" tempvalue : "+tempvalue+" keyname : "+keyname);
	    	    					if(tempvalue.compareTo(keyvalue)!=0)
			    	    			{
	    	    						//System.out.println("333 : "+keyname+" keyvalue : "+keyvalue+" tempvalue : "+tempvalue+" keyname : "+keyname);
			    	    				dataList.set(dd, null);
			    	    			}
		    	    			}
	    	    			}
	    	    			else
	    	    			{
		    	    			if(tempvalue.indexOf(keyvalue)==-1)
		    	    			{
		    	    				dataList.set(dd, null);
		    	    			}
	    	    			}
    	    			}
    	    			else
    	    			{
    	    				dataList.set(dd, null);
    	    			}
    				}
    	    	}
    	    }
    	}
		List newDataList = new ArrayList();
		for(int dd = 0;dd<dataList.size();dd++)
		{
			Map tempvalueMap = (Map)dataList.get(dd);
			if(null!=tempvalueMap)
				newDataList.add(tempvalueMap);
		}
		dataList.clear();
		dataList = null;
		return newDataList;
	}
	public List searchForFrom(String userid,String sql,Map searchValueMap)
	{
        List l = new ArrayList();
        try {
        	String id;
        	String parentId;
        	List dataList = getData(""+datafrom,userid,sql,searchValueMap);
        	//System.out.println("dataList : "+dataList);
			if(null==dataList)
			{
				return l;
			}
			for(int dd = 0;dd<dataList.size();dd++)
			{
				Map valueMap1 = (Map)dataList.get(dd);
				if(null==valueMap1)
					continue;
        		id  = (String)valueMap1.get(keyfield);
        		
        		//System.out.println("id : "+id+" keyfield : "+keyfield);
                BrowserBean bean = new BrowserBean();
                bean.setId(id);
                if(!parentfield.equals("")){
            		parentId=(String)valueMap1.get(parentfield);
            		bean.setParentId(parentId);
                }
                if(showclass==1&&showtype==2)
				{
                	bean.setName(Util.null2String((String)valueMap1.get(namefield)));
				}
        		else
        		{
        			Map valueMap = new HashMap();
	                Set keyset = showfieldMap.keySet();
	                for(Iterator it = keyset.iterator();it.hasNext();)
	                {
	                	String fieldname = (String)it.next();
	                	
	                	String fieldvalue = (String)valueMap1.get(fieldname);
	                	//System.out.println(fieldname+"  "+fieldvalue);
	                	valueMap.put(fieldname,fieldvalue);
	                }
	                bean.setValueMap(valueMap);
        		}
                
                if(!Util.null2String(this.href).equals("")){
                	bean.setHref(Util.null2String(this.href+id));
                }
                l.add(bean);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            getLog().error(e);
        }
        return l;  //To change body of implemented methods use File | Settings | File Templates.
	}
	/**
	 * 获取数据
	 */
    public List search(String userid,String sql) {
    	return this.search(userid,sql,null);
    }
    /**
     * 获取数据
     * @param userid
     * @param sql
     * @param searchValueMap
     * @return
     */
    public List search(String userid,String sql,Map searchValueMap)
	{
    	sql = rebuildSql(userid,sql);
		if("2".equals(from))
    		return this.searchForFrom(userid,sql,searchValueMap);
    	Connection conn = null;
        if (getDs() == null) {
			ConnectionPool pool = ConnectionPool.getInstance();
			conn = pool.getConnection();
		}
        else
        	conn = getDs().getConnection();
        List l = new ArrayList();
        try {
            //通过jdbc获取browserbean list
//        	String sql = getSearch();
        	if(sql.indexOf("$userid$")>-1) sql = sql.replace("$userid$",userid);
        	if(Util.getIntValue(userid)>0)
        		sql = this.replaceDefaultValue(userid, sql);
        	String id;
            String name;
            String description;
            
            //恢复sql的中文
        	sql = renewChineseStrSearch(sql);
        	//恢复sql中的oracle特殊字符串
        	sql = renewOracleSpecialStrSearch(sql);
        	
            sql = runSqlBeforePro(sql);
        	this.writeLog(this.name+": getDataFromSql sql : "+sql);
        	
            PreparedStatement s = conn.prepareStatement(sql);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                id = rs.getString(1);
                name = rs.getString(2);
                description = rs.getString(3);
                BrowserBean bean = new BrowserBean();
                bean.setId(id);
                bean.setName(name);
                bean.setDescription(description);
                if(!Util.null2String(this.href).equals("")){
                	bean.setHref(Util.null2String(this.href+id));
                }
                l.add(bean);
            }
            //关闭记录集
            rs.close();
            //关闭statement
            s.close();
        } catch (Exception e) {
            getLog().error(e);
        } finally {
            try {
                //关闭连接
                conn.close();
            } catch (Exception e) {
                getLog().error(e);
            }
        }
        return l;  //To change body of implemented methods use File | Settings | File Templates.
	}
    public String getSearchSql(String dbtype,String sql,Map searchValueMap)
    {
    	if(null!=searchfieldMap&&null!=searchValueMap)
    	{
    		//对主键字段的特殊处理
    		String keyFieldName = Util.null2String((String)searchValueMap.get("$KEY_FIELD_NAME$"));
    		String keyFieldValue = Util.null2String((String)searchValueMap.get("$KEY_FIELD_VALUE$"));
    		
    		String searchSql = "";
    		String mobileSearchSql = "";
    		Set keyset = searchfieldMap.keySet();
    		int i = 0;
    	    for(Iterator it = keyset.iterator();it.hasNext();)
    	    {
    	    	String keyname = (String)it.next();
    	    	String keyvalue = Util.null2String((String)searchValueMap.get(keyname)).trim();
    	    	if(!"".equals(keyvalue))
    	    	{
	    	    	String keytype = (String)searchfieldTypeMap.get(keyname);
	    	    	if("1".equals(keytype) || "key".equals(keytype))
	    	    	{
	    	    		if(parentfield.equalsIgnoreCase(keyname))
	    	        	{
	    	    			//searchSql += this.getParentidSql(keyvalue, dbtype);
	    	        	}
	    	        	else
	    	        	{
	    	        		searchSql += " AND "+keyname+" = ? ";
	    	        	}
	    	    	}
	    	        else if("2".equals(keytype)){
	    	        	searchSql += " AND "+keyname+" like ? ";
	    	        	if(this.isMobile){
	    	        		mobileSearchSql += " OR "+keyname+" like ? ";
	    	        		i++;
	    	        	}
	    	        }
    	    	}
    	    }
    	    if(i==0){
	    		Set searchValueMapKeyset = searchValueMap.keySet();
	    		for(Iterator it = searchValueMapKeyset.iterator();it.hasNext();){
	    			String keyname = (String)it.next();
	    	    	String keyvalue = Util.null2String((String)searchValueMap.get(keyname)).trim();
	    	    	if(!"".equals(keyvalue)){
	    	    		mobileSearchSql += " OR "+keyname+" like ? ";
	    	    	}
	    	    }
	    	}
    	    
    	    if(this.isMobile && !mobileSearchSql.equals("")){
    	    	mobileSearchSql = " AND (" + mobileSearchSql.replaceFirst(" OR ", "") + ")";
    	    	searchSql = mobileSearchSql;
    	    }
    	    
    	    //对主键字段的特殊处理
    	    if(!keyFieldName.equals("") && !keyFieldValue.equals("")&&!isMobile){
    	    	String tempField = Util.null2String((String)searchfieldMap.get(keyFieldName));
    	    	if(!tempField.equals("") && !tempField.equals(keyFieldName)){
    	    		searchSql += " AND "+keyFieldName+"='"+keyFieldValue+"' ";
    	    	}
    	    }
    	    
    	    if(!"".equals(searchSql))
    	    {
    	    	String ordersql = "";
    	    	String tempsql = sql;
    	    	/*if(sql.indexOf(" order ")>-1)
    	    	{
    	    		ordersql = sql.substring(sql.indexOf(" order "),sql.length());
    	    		tempsql = sql.substring(0,sql.indexOf(" order "));
    	    	}*/
    	    	
    	    	ordersql = parserOrder(tempsql);//使用sql语法解析器返回sql语名的排序语句
    	    	if(ordersql.length()>0){
    	    		tempsql = tempsql.replaceFirst(ordersql, "");
    	    	}
    	    	
    	    	if(isExistWhere(tempsql))//使用sql语法解析器判断sql语句是否存在where条件
    	    	{ 
    	    		String where = getWhere(tempsql);
    	    		tempsql = tempsql.substring(0,tempsql.indexOf(where))+ where + searchSql;
    	    	
    	    	}else
    	    	{
    	    		tempsql += " WHERE 1=1 "+ searchSql;
    	    	}
    	    	sql = tempsql+" "+ordersql;
    	    }
    	}
    	return sql;
    }
    /**
     * 使用sql语法解析器判断sql语句是否存在where条件
     * @param sql
     * @return
     */
    private boolean isExistWhere(String sql){
    	boolean result = false;
    	String where = getWhere(sql);
    	result = !where.equals("");
		return result;
    }
    
    private String getWhere(String sql){
    	String result = "";
    	try{
    		//String tsql = new StringBuffer(sql).toString();
	    	CCJSqlParserManager parserManager = new CCJSqlParserManager();
	    	Select select = (Select) parserManager.parse(new StringReader(sql));
    		PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
			Expression where = plainSelect.getWhere();
			result = where==null?"":where.toString();
    	}catch(Exception e){
    		writeLog(this.name+" : "+sql);
    		result = "";
    	}
		return result;
    }
    /**
     * 使用sql语法解析器返回sql语名的排序语句
     * @param sql
     * @return
     */
    private String parserOrder(String sql){
    	String result = "";
    	try{
    		//String tsql = new StringBuffer(sql).toString();
    		//tsql = tsql.replaceAll("[\u4e00-\u9fa5]+", "_TEMP_");
    		CCJSqlParserManager parserManager = new CCJSqlParserManager();
	    	Select select = (Select) parserManager.parse(new StringReader(sql));
    		PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
    		List orderList = plainSelect.getOrderByElements();
			if(orderList == null || orderList.size()==0){
				result = "";
			}else{
				for(int i=0;i<orderList.size();i++){
					if(!result.equals("")){
						result += ",";
					}
					result += (OrderByElement) orderList.get(i);
				}
				result = " ORDER BY "+result;
			}
    	}catch(Exception e){
    		writeLog(this.name+" : "+sql);
    		result = "";
    	}
		return result;
    }
    
    public String getSearchSqlByValue(String dbtype,String sql,Map searchValueMap)
    {
    	if(null!=searchfieldMap&&null!=searchValueMap)
    	{
    		//对主键字段的特殊处理
    		String keyFieldName = Util.null2String((String)searchValueMap.get("$KEY_FIELD_NAME$"));
    		String keyFieldValue = Util.null2String((String)searchValueMap.get("$KEY_FIELD_VALUE$"));
    		
    		String searchSql = "";
    		Set keyset = searchfieldMap.keySet();
    	    for(Iterator it = keyset.iterator();it.hasNext();)
    	    {
    	    	String keyname = (String)it.next();
    	    	String keyvalue = Util.null2String((String)searchValueMap.get(keyname)).trim();
    	    	
    	    	if(!"".equals(keyvalue))
    	    	{
	    	    	String keytype = (String)searchfieldTypeMap.get(keyname);
	    	    	//System.out.println("222 keyvalue : "+keyvalue+" keyname : "+keyname+" keytype : "+keytype);
	    	    	if("1".equals(keytype) || "key".equals(keytype))
	    	    	{
	    	    		if(parentfield.equalsIgnoreCase(keyname))
	    	        	{
	    	    			//searchSql += this.getParentidSql(keyvalue, dbtype);
	    	        	}
	    	    		else
	    	    		{
	    	    			searchSql += " AND "+keyname+" = '"+keyvalue+"' ";
	    	    		}
	    	    	}
	    	        else if("2".equals(keytype))
	    	        	searchSql += " AND "+keyname+" LIKE '%"+keyvalue+"%' ";
    	    	}
    	    }

    	    //对主键字段的特殊处理
    	    if(!keyFieldName.equals("") && !keyFieldValue.equals("")&&!isMobile){
    	    	String tempField = Util.null2String((String)searchfieldMap.get(keyFieldName));
    	    	if(!tempField.equals(keyFieldName)){
    	    		searchSql += " AND "+keyFieldName+"='"+keyFieldValue+"' ";
    	    	}
    	    }
    	    if(!"".equals(searchSql))
    	    {
    	    	String ordersql = "";
    	    	String tempsql = sql;
    	    	/*if(sql.indexOf(" order ")>-1)
    	    	{
    	    		ordersql = sql.substring(sql.indexOf(" order "),sql.length());
    	    		tempsql = sql.substring(0,sql.indexOf(" order "));
    	    	}*/
    	    	
    	    	ordersql = parserOrder(tempsql);//使用sql语法解析器返回sql语名的排序语句
    	    	if(ordersql.length()>0){
    	    		tempsql = tempsql.replaceFirst(ordersql, "");
    	    	}
    	    	
    	    	if(isExistWhere(tempsql))//使用sql语法解析器判断sql语句是否存在where条件
    	    	{
    	    		String where = getWhere(tempsql);
    	    		tempsql = tempsql.substring(0,tempsql.indexOf(where))+ where + searchSql;
    	    	}
    	    	else
    	    	{
    	    		tempsql += " WHERE 1=1 "+searchSql;
    	    	}
    	    	
    	    	sql = tempsql+" "+ordersql;
    	    }
    	}
    	return sql;
    }
    /**
     * 获取上级字段sql
     * @param keyvalue
     * @param dbtype
     * @return
     */
    private String getParentidSql(String keyvalue,String dbtype)
    {
    	String sql = "";
    	if("0".equals(keyvalue))
		{
    		if (dbtype.equalsIgnoreCase("oracle")) {
    			if(!"".equals(parentid))
    				sql += " and nvl("+parentid+",'0')='0' ";
    	    } else if (dbtype.toLowerCase().indexOf("sqlserver")>-1||dbtype.equalsIgnoreCase("sybase")) {
    	     	if(!"".equals(parentid))
    				sql += " and isnull("+parentid+",'0')='0' ";
    	    } else if (dbtype.equalsIgnoreCase("informix")) {
    	     	if(!"".equals(parentid))
    				sql += " and ("+parentid+" is null or and "+parentid+" = 0) ";
    	    } else if (dbtype.equalsIgnoreCase("mysql")) {
    	     	if(!"".equals(parentid))
    				sql += " and IFNULL("+parentid+",'0')='0' ";
    	    } else if (dbtype.equalsIgnoreCase("db2")) {
    	     	if(!"".equals(parentid))
    				sql += " and coalesce("+parentid+",'0')='0' ";
    	    }else{
    	     	if(!"".equals(parentid))
    				sql += " and ("+parentid+" is null or and "+parentid+" = 0) ";
    	    }
		}
		else
		{
			if (dbtype.equalsIgnoreCase("oracle")) {
				if(!"".equals(parentid))
					sql += " and nvl("+parentid+",'0') = '"+keyvalue+"'";
		    } else if (dbtype.toLowerCase().indexOf("sqlserver")>-1||dbtype.equalsIgnoreCase("sybase")) {
		     	if(!"".equals(parentid))
					sql += " and isnull("+parentid+",'0') = '"+keyvalue+"'";
		    } else if (dbtype.equalsIgnoreCase("informix")) {
		     	if(!"".equals(parentid))
					sql += " and "+parentid+" = '"+keyvalue+"'";
		    } else if (dbtype.equalsIgnoreCase("mysql")) {
		     	if(!"".equals(parentid))
					sql += " and IFNULL("+parentid+",'0') = '"+keyvalue+"'";
		    } else if (dbtype.equalsIgnoreCase("db2")) {
		     	if(!"".equals(parentid))
					sql += " and coalesce("+parentid+",'0') = '"+keyvalue+"'";
		    }else{
		     	if(!"".equals(parentid))
					sql += " and "+parentid+" = '"+keyvalue+"'";
		    }
		}
    	return sql;
    }
    public PreparedStatement setPreparedStatement(PreparedStatement stmt,Map searchValueMap) throws SQLException
    {
    	if(null!=searchfieldMap&&null!=searchValueMap)
    	{
    		String searchSql = "";
    		Set keyset = searchfieldMap.keySet();
    		int i = 0;
    	    for(Iterator it = keyset.iterator();it.hasNext();)
    	    {
    	    	String keyname = (String)it.next();
    	    	String keyvalue = Util.null2String((String)searchValueMap.get(keyname)).trim();
    	    	if(!"".equals(keyvalue))
    	    	{
    	    		if(parentfield.equalsIgnoreCase(keyname))
    	        	{
    	    			continue;
    	        	}
    	    		i ++;
	    	    	String keytype = (String)searchfieldTypeMap.get(keyname);
	    	    	//System.out.println("keyname : "+keyname+ "  keyvalue : "+keyvalue+" i : "+i);
	    	    	if("1".equals(keytype))
	    	    	{
	    	        	stmt.setInt(i, Util.getIntValue(keyvalue,0));
	    	    	}
	    	        else if("2".equals(keytype))
	    	        {
		    	        stmt.setString(i, "%"+keyvalue+"%");
	    	        }
	    	        else if("key".equals(keytype))
	    	        {
	    	        	stmt.setString(i, keyvalue);
	    	        }
    	    	}
    	    }
    	    if(i==0 && this.isMobile){
    	    	Set searchValueMapKeyset = searchValueMap.keySet();
    	    	for(Iterator it = searchValueMapKeyset.iterator();it.hasNext();)
        	    {
    	    		String keyname = (String)it.next();
        	    	String keyvalue = Util.null2String((String)searchValueMap.get(keyname)).trim();
        	    	i++;
        	    	stmt.setString(i, "%"+keyvalue+"%");
        	    }
    	    }
    	}
    	return stmt;
    }
    /**
     * 获取转换sql值
     * @param conn
     * @param sql
     * @param value
     * @return
     */
    public String getTranSqlValue(Connection conn,String sql,String value)
	{
    	String newvalue = "";
    	sql = DBTypeUtil.replaceString(sql,"{?currentvalue}",value);
    	if(null==conn)
    	{
			RecordSet syncrs = new RecordSet();
			syncrs.executeSql(sql);
			if(syncrs.next())
			{
				newvalue = syncrs.getString(1);
			}
    	}
    	else
    	{
    		try
    		{
	    		PreparedStatement s = conn.prepareStatement(sql);
	            ResultSet rs = s.executeQuery();
	            if (rs.next()) {
	            	newvalue = rs.getString(1);
	            }
	            //关闭记录集
	            rs.close();
	            //关闭statement
	            s.close();
    		}
    		catch(Exception e)
    		{
    			
    		}
    	}
		return newvalue;
	}
    public BrowserBean searchById(String id) {
        String realId = "";
        if(tempsearch.equals("") && !searchById.equals(""))
            tempsearch = searchById;
        if(!tempsearch.equals(""))
            realId = rebuildTempSearch(id);
        else
            realId = rebuildSearch(id);
    	return searchById("",realId);
    }
    /**
     * 重构sql
     * @param id
     */
    private String rebuildSearch(String str) {
    	search = removeDefaultValue((search+" "));//去除表单参数和自定义参数
    	parserChineseStr();//对sql中的中文做特殊处理
    	parserOracleSpecialStr();//解析oracle特别字符串
    	String realId = "";
    	if(str.indexOf("^~^")>-1){//带分隔符的特殊处理
    		String []strArray = str.split("\\^~\\^");
    		
    		if(existReplaceField()){//判断sql中是否包含需要替换的字段变量
    			String requestid = strArray[0];
    			realId = strArray[1];
    			String rowno = "";
    			if(strArray.length==3){
    				rowno = strArray[2];
    			}
				HashMap fieldValueMap = getTableFieldValueMap( requestid);
				
				//用正则表达式找到要替换的字段名
				search = replaceFieldValue(search, rowno, fieldValueMap);
    		}else{
    			realId = strArray[1];
    		}
			
    	}else{//不带分隔符
    		realId = str;
    	}
    	reformatSearch();
    	return realId;
	}
    
    /**
     * 重构sql
     * @param id
     */
    private String rebuildSql(String userid,String sql) {
    	if(Util.getIntValue(userid)>0){
    		sql = this.replaceDefaultValue(userid, sql);
    	}
    	sql = parserChineseStrSql(sql);//对sql中的中文做特殊处理
    	sql = parserOracleSpecialString(sql);//解析oracle特别字符串
    	sql = reformatSql(sql);
    	return sql;
	}
    /**
     * 重构sql
     * @param id
     */
    private String rebuildTempSearch(String str) {
    	parserChineseStrTemp();//对sql中的中文做特殊处理
    	
    	String realId = "";
    	if(str.indexOf("^~^")>-1){//带分隔符的特殊处理
    		String []strArray = str.split("\\^~\\^");
    		
    		if(existReplaceField()){//判断sql中是否包含需要替换的字段变量
    			String requestid = strArray[0];
    			realId = strArray[1];
    			String rowno = "";
    			if(strArray.length==3){
    				rowno = strArray[2];
    			}
				HashMap fieldValueMap = getTableFieldValueMap( requestid);
				
				//用正则表达式找到要替换的字段名
				tempsearch = replaceFieldValue(tempsearch, rowno, fieldValueMap);
    		}else{
    			realId = strArray[1];
    		}
			
    	}else{//不带分隔符
    		realId = str;
    	}
    	reformatTempSearch();
    	return realId;
	}
    
    /**
     * 用正则表达式找到要替换的字段名
     * @param rowno 
     * @param fieldValueMap
     */
	private String replaceFieldValue(String sql, String rowno, Map fieldValueMap) {
		Pattern p = Pattern.compile("(\\$[a-zA-Z][a-zA-Z0-9_]*\\$)"); 
		Matcher m = p.matcher(sql);
		while(m.find()){
			String fieldname1 = m.group();
			String fieldname2 = rebuildFieldname(fieldname1,rowno);//针对明细字段重构字段名
			if (!fieldValueMap.containsKey(fieldname2)) {
				continue;
			}
			String fieldvalue = Util.null2String((String)fieldValueMap.get(fieldname2));
			fieldvalue = rebuildMultiFieldValue(fieldvalue);//处理多值
			sql = sql.replace(fieldname1,fieldvalue);//替换字段值
		}
		
		return sql;
	}
    
    /**
     * 对sql中的中文做特殊处理
     */
    private void parserChineseStr(){
    	search = parserChineseString(search);
    }
    
    /**
     * 对sql中的中文做特殊处理
     */
    private String parserChineseStrSql(String sql){
    	return parserChineseString(sql);
    }
    
    /**
     * 对sql中的中文做特殊处理
     */
    private void parserChineseStrTemp(){
    	tempsearch = parserChineseString(tempsearch);
    }
    
    /**
     * 对sql中的中文做特殊处理
     * @param sql 
     */
    private String parserChineseString(String sql){
    	/* jsqlparser 0.9.4支持中文，下面的代码不需要了。
    	if(!Util.null2String(sql).equals("")){
    	Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]+");
		Matcher matcher = pattern.matcher(sql);
		
		while (matcher.find()) {
			String chineseStrKey = "@!@CHINESE_VALUE_" + parserChineseStringIndex + "@!@";
			String chineseStrVal = matcher.group();
			chineseStrMap.put(chineseStrKey, chineseStrVal);
			sql = sql.replaceFirst(chineseStrVal, chineseStrKey);
			parserChineseStringIndex++;
		}
    	}
    	*/
    	sql = parserSpecialString(sql);
		return sql;
    }
    
    private String parserSpecialString(String sql){
    	if(!Util.null2String(sql).equals("")){
    		String splitter = "\\\\";
        	String [] sqlPartArray = sql.split(splitter);
        	for(int i=0;i<sqlPartArray.length;i++){
    			String chineseStrKey = "@!@SPECIAL_VALUE_" + parserSpecialStringIndex + "@!@";
    			specialStrMap.put(chineseStrKey, splitter);
    			sql = sql.replaceFirst(splitter, chineseStrKey);
    			parserSpecialStringIndex++;
        	}
    	}
		return sql;
    }
    
    /**
     * 解析oracle特别字符串
     */
    private void parserOracleSpecialStr(){
    	search = parserChineseString(search);
    }
    
    /**
     * 解析oracle特别字符串
     * @param sql
     * @return
     */
    private String parserOracleSpecialString(String sql){
    	if(!Util.null2String(sql).equals("")){
    		sql = sql.replaceAll("from table\\(", "from table_(");//对table表函数的特殊处理
    	}
    	
		return sql;
    }
    /**
     * 重构查询语句
     */
    private void reformatSearch(){
    	search = reformatSqlString(search);
    }
    
    /**
     * 重构查询语句
     */
    private String reformatSql(String sql){
    	return reformatSqlString(sql);
    }
    
    /**
     * 重构查询语句
     */
    private void reformatTempSearch(){
    	tempsearch = reformatSqlString(tempsearch);
    }
    
    /**
     * 重构查询语句
     */
    private String reformatSqlString(String sql){
    	sql = parserChineseStrSql(sql);//对sql中的中文做特殊处理
    	sql = parserOracleSpecialString(sql);//解析oracle特别字符串
    	
    	sql = sql + " ";
    	sql = this.removeParam(sql);
		sql = runSqlBeforePro(sql);
    	sql = removeDefaultValueAgain(sql);
		
    	if(!Util.null2String(sql).equals("")){
    	try{
    		CCJSqlParserManager parserManager = new CCJSqlParserManager();
	    	Select select = (Select) parserManager.parse(new StringReader(sql));
	    	PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
			sql = plainSelect.toString();
    	}catch(Exception e){
    		writeLog(this.name+" : "+sql);
    		writeLog(e);
    	}
    	}
    	return sql;
    }
    
    /**
     * 恢复sql语句中的中文
     */
    private String renewChineseStrSearch(String sql){
    	/*jsqlparser 0.9.4，已经支持中文，下面的代码不需要了。
    	Set keys = chineseStrMap.keySet();
		for(Iterator it = keys.iterator();it.hasNext();)
		{
			String key = Util.null2String((String)it.next());
			String value = (String)chineseStrMap.get(key);
			sql = sql.replaceFirst(key, value);
		}
		*/
		sql = renewSpecialStrSearch(sql);
		return sql;
	}
    
    private String renewSpecialStrSearch(String sql){
    	Set keys = specialStrMap.keySet();
		for(Iterator it = keys.iterator();it.hasNext();)
		{
			String key = Util.null2String((String)it.next());
			String value = (String)specialStrMap.get(key);
			sql = sql.replaceFirst(key, value);
		}
		
		return sql;
    }
    /**
     * 恢复sql语句中的oracle特殊字符串
     */
    private String renewOracleSpecialStrSearch(String sql){
    	sql = sql.replaceAll("FROM table_\\(", "FROM table(");
		
		return sql;
	}
    
    /**
	 * 对多值进行处理，加上单引号
	 * @param fieldvalue
	 * @return
	 */
	public String rebuildMultiFieldValue(String fieldvalue){
		String result = "";
		if(fieldvalue.indexOf(",")>-1){
			String [] array = fieldvalue.split(",");
			for(int i=0;i<array.length;i++){
				if(!array[i].equals("")){
					if(result.equals("")){
						result += "'";
					}else{
						result += ",'";
					}
					
					result += array[i]+"'";
				}
			}
		}else{
			result = fieldvalue;
		}
		
		return result;
	}
	
    /**
     * 判断sql中是否包含需要替换的字段变量
     * @return
     */
    private boolean existReplaceField(){
    	Pattern p = Pattern.compile("\\$[a-zA-Z][a-zA-Z0-9_]*\\$"); 
		Matcher m = p.matcher(search); 
    	return m.find();
    }
    
    /**
     * 对明细表字段名需要重构
     * @param fieldname
     * @param rowno
     * @return
     */
    private String rebuildFieldname(String fieldname,String rowno){
    	String result = "";
    	//匹配formtable_main_10_dt1_fieldname这种格式的字段名
    	Pattern p = Pattern.compile("\\$formtable_main_[0-9]+_dt[0-9]+_[a-zA-Z][a-zA-Z0-9_]*\\$"); 
		Matcher m = p.matcher(fieldname);
		if(m.find()){
			String group = m.group();
			result = group.substring(0,group.length()-1)+"_"+rowno+group.substring(group.length()-1);
		}else{
			p = Pattern.compile("\\$detail_[a-zA-Z][a-zA-Z0-9_]*\\$");
			m = p.matcher(fieldname);
			if(m.find()){
				String group = m.group();
				result = group.substring(0,group.length()-1)+"_"+rowno+group.substring(group.length()-1);
			}else{
				result = fieldname;
			}
		}
		
		return result;
    }
    
    /**
     * 获取表单字段
     * @param formid
     * @param isbill
     * @param workflowid
     * @param requestid
     * @return
     */
    private HashMap getTableFieldValueMap(String requestid){
		HashMap fieldValueMap = new HashMap();
		List mainFieldList = new ArrayList();//主表字段列表
		List detailFieldList = new ArrayList();//明细表字段列表
		List detailTableList = new ArrayList();//明细表列表
		
		String sql = "";
		RecordSet rs = new RecordSet();
		
		//String workflowid = "";
		String isbill = "";
		String formid = "";
		sql = "select t2.workflowid,t1.formid,t1.isbill from workflow_base t1 join workflow_requestbase t2 on t1.id=t2.workflowid where t2.requestid="+requestid;
		rs.executeSql(sql);
		if(rs.next()){
			//workflowid = Util.null2String(rs.getString("workflowid"));
			formid = Util.null2String(rs.getString("formid"));
			isbill = Util.null2String(rs.getString("isbill"));
		}
		if(isbill.equals("1")){//单据
			//获取单据主表字段名
			sql = "select id,fieldname,detailtable from workflow_billfield where (detailtable is null or detailtable='') and billid = "+formid;
			rs.executeSql(sql);
			while(rs.next()){
				String fieldname = Util.null2String(rs.getString("fieldname")).toLowerCase();
				mainFieldList.add(fieldname);
			}
			
			WorkflowBillComInfo wbci = null;
			try {
				wbci = new WorkflowBillComInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String mainTableName = wbci.getTablename(formid);//主表名称
			String detailkeyfield = wbci.getDetailkeyfield(formid);//明细表外键字段名称
			String mainid = "";//主表id
			
			//查询单据主表字段值
			sql = "select * from "+mainTableName +" where requestid="+requestid;
			rs.executeSql(sql);
			if(rs.next()){
				mainid = Util.null2String(rs.getString("id"));
				for(int i=0;i<mainFieldList.size();i++){
					String fieldname = (String) mainFieldList.get(i);
					String fieldvalue  = Util.null2String(rs.getString(fieldname));
					fieldValueMap.put("$"+fieldname+"$", fieldvalue);
				}
			}
			
			//获取单据明细表名称
			sql = "select distinct detailtable from workflow_billfield where detailtable is not null and billid = "+formid;
			rs.executeSql(sql);
			while(rs.next()){
				//单据明细表名称
				String detailtable = Util.null2String(rs.getString("detailtable")).toLowerCase();
				RecordSet RecordSet = new RecordSet();
				//获取指定单据明细表字段名
				sql = "select fieldname from workflow_billfield where detailtable='"+detailtable+"' and billid = "+formid;
				RecordSet.executeSql(sql);
				while(RecordSet.next()){
					String fieldname = Util.null2String(RecordSet.getString("fieldname")).toLowerCase();
					detailFieldList.add(fieldname);
				}
				//获取指定单据明细表字段值
				sql = "select * from "+detailtable+" where "+detailkeyfield+"="+mainid+" order by id ";//解决oracle排序错乱问题
				RecordSet RecordSet1 = new RecordSet();
				RecordSet1.executeSql(sql);
				int rowno = 0;
				while(RecordSet1.next()){
					for(int i=0;i<detailFieldList.size();i++){
						String fieldname = (String) detailFieldList.get(i);
						String fieldvalue  = Util.null2String(RecordSet1.getString(fieldname));
						fieldValueMap.put("$"+detailtable+"_"+fieldname+"_"+rowno +"$", fieldvalue);
					}
					rowno++;
				}
			}
			
		}else{//表单
			//查询表单主表字段名
			sql = "select b.id,b.fieldname from workflow_formfield a,workflow_formdict b where a.fieldid = b.id and formid = " + formid;
			rs.executeSql(sql);
			while(rs.next()){
				String fieldname = Util.null2String(rs.getString("fieldname"));
				mainFieldList.add(fieldname);
			}
			String mainid = "";//主表id
			
			//查询表单主表字段值
			sql = "select * from workflow_form where requestid="+requestid;
			rs.executeSql(sql);
			if(rs.next()){
				mainid = requestid;
				for(int i=0;i<mainFieldList.size();i++){
					String fieldname = (String) mainFieldList.get(i);
					String fieldvalue  = Util.null2String(rs.getString(fieldname));
					fieldValueMap.put("$"+fieldname+"$", fieldvalue);
				}
			}
			
			//查询表单明细表字段名
			sql = "select b.id,b.fieldname from workflow_formfield a,workflow_formdictdetail b where a.fieldid = b.id and formid = " + formid;
			rs.executeSql(sql);
			while(rs.next()){
				String fieldname = Util.null2String(rs.getString("fieldname"));
				detailFieldList.add(fieldname);
			}
			//查询表单明细表字段值
			sql = "select * from workflow_formdetail where requestid="+requestid;
			rs.executeSql(sql);
			if(rs.next()){
				mainid = requestid;
				for(int i=0;i<detailFieldList.size();i++){
					String fieldname = (String) detailFieldList.get(i);
					String fieldvalue  = Util.null2String(rs.getString(fieldname));
					fieldValueMap.put("$detail_"+fieldname+"_"+i+"$", fieldvalue);
				}
			}
		}
		
		return fieldValueMap;
	}
    
	public BrowserBean searchByIdLocal(String userid,String id) {
        BrowserBean bean = new BrowserBean();
        ConnStatement st = new ConnStatement();
        String vdatasource = getPoolname();
        try {
        	String sql = getSearchById();
        	if(sql.indexOf("$userid$")>-1) sql = sql.replace("$userid$",userid);
        	if(Util.getIntValue(userid)>0)
        		sql = this.replaceDefaultValue(userid, sql);
            String name;
            String description;
            if(vdatasource==null || vdatasource.equals("")){
            	st.setStatementSql(sql);
            }else{
            	st.setStatementSql(sql, vdatasource,false);
            }
            st.setString(1,id);
            st.executeQuery();
            while (st.next()) {
                name = st.getString(1);
                description = st.getString(2);
                bean.setId(id);
                bean.setName(name);
                bean.setDescription(description);
                if(!Util.null2String(this.href).equals("")){
                	bean.setHref(Util.null2String(this.href+id));
                }
            }
            st.close();
        } catch (Exception e) {
            getLog().error(e);
        } finally {
            try {
                st.close();
            } catch (Exception e) {
                getLog().error(e);
            }
        }
        return bean;
    }
    public List searchListByIdForFrom(String userid,String pfieldname,String pfieldvalue) {
    	BrowserBean bean = null;
    	Map searchValueMap = new LinkedHashMap();
    	if(!isMobile){
    	searchValueMap.put(pfieldname, pfieldvalue);
    	
    	//对主键字段的特殊处理
    	searchValueMap.put("$KEY_FIELD_NAME$",pfieldname);
    	searchValueMap.put("$KEY_FIELD_VALUE$",pfieldvalue);
    	}else{//手机版查询时构造查询条件
    		if(null!=this.searchfieldMap)
    		{
    			Set keyset = this.searchfieldMap.keySet();
    			int i=0;
    		    for(Iterator it = keyset.iterator();it.hasNext();)
    		    {
    		    	String keyname = (String)it.next();
    		    	if(!keyname.equals(this.keyfield)){
	    		    	String keyvalue = pfieldvalue;
	    		    	if(!"".equals(keyvalue)){
	    		    		searchValueMap.put(keyname,keyvalue);
	    		    		i++;
	    		    	}
    		    	}
    		    	
    		    }
    		    if(i==0){//当没有设置查询条件时，默认使用标题字段做为查询条件。
    		    	searchValueMap.put(pfieldname, pfieldvalue);
    		    }
    		}
    	}
    	String tempsql = this.tempsearch.equals("")?search:this.tempsearch;
    	//System.out.println("tempsql : "+tempsql);
    	if(userid.equals("")){//回显数据时的特殊处理
    		tempsql = removeDefaultValue(tempsql);
    	}
    	tempsql = rebuildSql(userid,tempsql);
    	List values = this.searchForFrom(userid, tempsql,searchValueMap);
    	searchValueMap.clear();
    	this.tempsearch = "";
    	return values;
    }
    /**
     * 通过关键字段获取单个数据完整信息
     * @param userid
     * @param pfieldname
     * @param pfieldvalue
     * @return
     */
    public BrowserBean searchByIdForFrom(String userid,String pfieldname,String pfieldvalue) {
    	BrowserBean bean = null;
    	List values = this.searchListByIdForFrom(userid, pfieldname, pfieldvalue);
    	//System.out.println(" userid : "+userid+"  search : "+search+ " values : "+values.size());
    	if(null!=values&&values.size()>0)
    	{
    		BrowserBean valuebean = (BrowserBean)values.get(0);
	    	bean = new BrowserBean();
	    	bean.setId(valuebean.getId());
	        Set keyset = showfieldMap.keySet();
	        int i = 1;
	        Map fieldValueMap = valuebean.getValueMap();
	        if(null!=fieldValueMap&&fieldValueMap.size()>0)
	        {
		        for(Iterator it = keyset.iterator();it.hasNext();)
		        {
		         	String fieldname = (String)it.next();
		         	i++;
		         	String fieldvalue = Util.null2String((String)fieldValueMap.get(fieldname));
		         	if(i==2)
		         		bean.setName(fieldvalue);
		         	else if(i==3)
		         		bean.setDescription(fieldvalue);
		        }
		        if(keyset.size()<3 && Util.null2String(bean.getDescription()).equals(""))
		        {
		        	bean.setDescription(bean.getName());
		        }
	        }else{
	        	bean.setName(valuebean.getName());
	        }
	        if(!Util.null2String(this.href).equals("")){
	         	bean.setHref(Util.null2String(this.href+valuebean.getId()));
	        }
	        //为了新的多选浏览框，把值全都放进去
	        bean.setValueMap(fieldValueMap);
    	}
    	return bean;
    }
    /**
     * 通过关键字段获取单个数据完整信息
     * @param userid
     * @param id
     * @return
     */
    public BrowserBean searchById(String userid,String id) {
    	
    	if(Util.null2String(this.from).equals("1")){//来自模块的自定义浏览按钮
    		return searchByIdLocal(userid,id);
    	}
    	if("2".equals(from))
    		return this.searchByIdForFrom(userid,keyfield,id);
    	
        BrowserBean bean = new BrowserBean();
        Connection conn = null;
        if (getDs() == null) {
			ConnectionPool pool = ConnectionPool.getInstance();
			conn = pool.getConnection();
		}
        else
        	conn = getDs().getConnection();
        try {
            //通过jdbc获取browserbean
        	String sql = getSearchById();
        	sql = rebuildSql(userid,sql);
        	if(sql.indexOf("$userid$")>-1) sql = sql.replace("$userid$",userid);
        	if(Util.getIntValue(userid)>0)
        		sql = this.replaceDefaultValue(userid, sql);
            String name;
            String description;
            
            //恢复sql的中文
        	sql = renewChineseStrSearch(sql);
        	//恢复sql中的oracle特殊字符串
        	sql = renewOracleSpecialStrSearch(sql);
        	
            sql = runSqlBeforePro(sql);
        	this.writeLog(this.name+": getDataFromSql sql : "+sql);
        	
            PreparedStatement s = conn.prepareStatement(sql);
            s.setString(1,id);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
            	
                name = rs.getString(1);
                description = rs.getString(2);
                bean.setId(id);
                bean.setName(name);
                bean.setDescription(description);
                if(!Util.null2String(this.href).equals("")){
                	bean.setHref(Util.null2String(this.href+id));
                }
            }
            //关闭记录集
            rs.close();
            //关闭statement
            s.close();
        } catch (Exception e) {
            getLog().error(e);
        } finally {
            try {
                //关闭连接
                conn.close();
            } catch (Exception e) {
                getLog().error(e);
            }
        }
        return bean;  //To change body of implemented methods use File | Settings | File Templates.
    }
    /**
     * 为了防止单实例类变量的混乱，增加这个查询方法
     */
    public BrowserBean searchSqlById(String id,String searchsql)
    {
    	this.setTempsearch(searchsql);
    	String realId = rebuildTempSearch(id);
    	return searchById("",realId);
    }
    public String getSearchByIdSql(String sql)
    {
    	String ordersql = "";
    	String tempsql = sql;
    	if(sql.indexOf(" order ")>-1)
    	{
    		ordersql = sql.substring(sql.indexOf(" order "),sql.length());
    		tempsql = sql.substring(0,sql.indexOf(" order "));
    	}
    	
    	if(tempsql.indexOf("where")>-1)
    	{
    		tempsql += " and "+keyfield+"=?";
    	}
    	else
    	{
    		tempsql += " where "+keyfield+"=?";
    	}
    	sql = tempsql+" "+ordersql;
    	return sql;
    }
    public String getSearchByIdValueSql(String sql,String id)
    {
    	String ordersql = "";
    	String tempsql = sql;
    	if(sql.indexOf(" order ")>-1)
    	{
    		ordersql = sql.substring(sql.indexOf(" order "),sql.length());
    		tempsql = sql.substring(0,sql.indexOf(" order "));
    	}
    	
    	if(tempsql.indexOf("where")>-1)
    	{
    		tempsql += " and "+keyfield+"=?";
    	}
    	else
    	{
    		tempsql += " where "+keyfield+"=?";
    	}
    	sql = tempsql+" "+ordersql;
    	return sql;
    }
    public List searchByName(String name) {
    	return searchByName("",name);
    }
    
    public List searchByName(String userid,String name) {
    	return searchByName(userid,name,getSearchByName());
    }
    
    public List searchByName(String userid,String name,String sql) {
    	writeLog("BaseBrowser.searchByName: userid="+userid+", name="+name," sql="+sql);
    	sql = rebuildSql(userid,sql);
        Connection conn = null;
        if("2".equals(from))
    	{
        	if(this.search.trim().indexOf("select")==0){
        		this.search = sql;
        	}
    		return this.searchListByIdForFrom(userid,namefield,name);
    	}
        if (getDs() == null) {//来自模块的自定义按钮也可能来自虚拟表单 
			ConnectionPool pool = ConnectionPool.getInstance();
			conn = pool.getConnection();
		}
        else
        	conn = getDs().getConnection();
        List l = new ArrayList();
        try {
            //通过jdbc获取browserbean list

//        	String sql = getSearchByName();
        	if(sql.indexOf("$userid$")>-1) sql = sql.replace("$userid$",userid);
        	if(Util.getIntValue(userid)>0)
        		sql = this.replaceDefaultValue(userid, sql);
            String id;
            String beanName;
            String description;
            
            //恢复sql的中文
        	sql = renewChineseStrSearch(sql);
        	//恢复sql中的oracle特殊字符串
        	sql = renewOracleSpecialStrSearch(sql);
            
            sql = runSqlBeforePro(sql);
        	this.writeLog(this.name+": getDataFromSql sql : "+sql);
        	
            PreparedStatement s = conn.prepareStatement(sql);
            s.setString(1,"%"+name+"%");
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                id = rs.getString(1);
                beanName = rs.getString(2);
                description = rs.getString(3);
                BrowserBean bean = new BrowserBean();
                bean.setId(id);
                bean.setName(beanName);
                bean.setDescription(description);
                if(!Util.null2String(this.href).equals("")){
                	bean.setHref(Util.null2String(this.href+id));
                }
                l.add(bean);
            }
            //关闭记录集
            rs.close();
            //关闭statement
            s.close();
        } catch (Exception e) {
            getLog().error(e);
        } finally {
            try {
                //关闭连接
                conn.close();
            } catch (Exception e) {
                getLog().error(e);
            }
        }
        return l;  //To change body of implemented methods use File | Settings | File Templates.
    }
    /**
     * 自定义浏览框明细导入时用到的由名称转换成id
     */
	public BrowserBean searchForImport(String name) {
		reformatSearch();//用sql语法解析器格式化sql
		if("2".equals(from))
    	{
			this.search += " ";
			//用正则表达式找到要替换的字段名
			search = replaceFieldValue(search, "", this.requestFormInfoMap);
    		return this.searchByIdForFrom("",namefield,name);
    	}
		Connection conn = null;
		//模块自定义浏览按钮
		if (ds == null) {
			ConnectionPool pool = ConnectionPool.getInstance();
			conn = pool.getConnection();
		} else {
			conn = ds.getConnection();
		}
		BrowserBean browserBean = null;
        try {
            String sql = getSearchByName();
            //改造sql，将like替换成=
            sql = sql.replaceAll(" like ", " = ").replaceAll(" LIKE ", " = ");
            if(ds==null && !"".equals(sql)&&Util.getIntValue(customid)>0&&this.outPageURL!=null&&this.outPageURL.indexOf("customid=")!=-1){
            	sql = sql + " and formmodeid is not NULL ";
            }
            PreparedStatement s = conn.prepareStatement(sql);
            //System.out.println(sql+", name="+name);
            s.setString(1,""+name+"");
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
            	//System.out.println(1);
            	browserBean = new BrowserBean();
                browserBean.setId(rs.getString(1));
                browserBean.setName(rs.getString(2));
                browserBean.setDescription(rs.getString(3));
            } else { //如果查不到数据，可能是多语言，再查一次
            	sql = getSearchByName();
            	String[] sqlstrs = sql.split(" where ");
            	sql = sqlstrs[0] + " where " + " (" + sqlstrs[1] +" or " + sqlstrs[1] + " or " + sqlstrs[1]+") ";
            	if(ds==null && !"".equals(sql)&&Util.getIntValue(customid)>0&&this.outPageURL!=null&&this.outPageURL.indexOf("customid=")!=-1){
                	sql = sql + " and formmodeid is not NULL ";
                }
                s = conn.prepareStatement(sql);
                //System.out.println(sql+", name="+name);
                String newname1 = "`~`7 "+name+"`~`";
                String newname2 = "`~`8 "+name+"`~`";
                String newname3 = "`~`9 "+name+"`~`";
                s.setString(1,"%"+newname1+"%");
                s.setString(2,"%"+newname2+"%");
                s.setString(3,"%"+newname3+"%");
                rs = s.executeQuery();
                if (rs.next()) { 
                	browserBean = new BrowserBean();
                    browserBean.setId(rs.getString(1));
                    browserBean.setName(rs.getString(2));
                    browserBean.setDescription(rs.getString(3));
                }
            }
            //关闭记录集
            rs.close();
            //关闭statement
            s.close();
        } catch (Exception e) {
            getLog().error(e);
        } finally {
            try {
                //关闭连接
                conn.close();
            } catch (Exception e) {
                getLog().error(e);
            }
        }
        return browserBean;
	}
	
	private String[] getIdByName(String name){
		String[] resultStrings = new String[3];
		if("2".equals(from))
    	{
			this.search += " ";
			//用正则表达式找到要替换的字段名
			search = replaceFieldValue(search, "", this.requestFormInfoMap);
    		BrowserBean bean = this.searchByIdForFrom("",namefield,name);
    		resultStrings[0] = bean.getId();
        	resultStrings[1] = bean.getName();
        	resultStrings[2] = bean.getDescription();
        	return resultStrings;
    	}
		Connection conn = null;
		//模块自定义浏览按钮
		if (ds == null) {
			ConnectionPool pool = ConnectionPool.getInstance();
			conn = pool.getConnection();
		} else {
			conn = ds.getConnection();
		}
		
		try {
            String sql = getSearchByName();
            //改造sql，将like替换成=
            sql = sql.replaceAll(" like ", " = ").replaceAll(" LIKE ", " = ");
            if(ds==null && !"".equals(sql)&&Util.getIntValue(customid)>0&&this.outPageURL!=null&&this.outPageURL.indexOf("customid=")!=-1){
            	sql = sql + " and formmodeid is not NULL ";
            }
            PreparedStatement s = conn.prepareStatement(sql); 
            s.setString(1,""+name+"");
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
            	resultStrings[0] = rs.getString(1);
            	resultStrings[1] = rs.getString(2);
            	resultStrings[2] = rs.getString(3);
            } else { //如果查不到数据，可能是多语言，再查一次
            	sql = getSearchByName();
            	String[] sqlstrs = sql.split(" where ");
            	sql = sqlstrs[0] + " where " + " (" + sqlstrs[1] +" or " + sqlstrs[1] + " or " + sqlstrs[1]+") ";
            	if(ds==null && !"".equals(sql)&&Util.getIntValue(customid)>0&&this.outPageURL!=null&&this.outPageURL.indexOf("customid=")!=-1){
                	sql = sql + " and formmodeid is not NULL ";
                }
                s = conn.prepareStatement(sql);
                String newname1 = "`~`7 "+name+"`~`";
                String newname2 = "`~`8 "+name+"`~`";
                String newname3 = "`~`9 "+name+"`~`";
                s.setString(1,"%"+newname1+"%");
                s.setString(2,"%"+newname2+"%");
                s.setString(3,"%"+newname3+"%");
                rs = s.executeQuery();
                if (rs.next()) { 
                	resultStrings[0] = rs.getString(1);
                	resultStrings[1] = rs.getString(2);
                	resultStrings[2] = rs.getString(3);
                }
            }
            //关闭记录集
            rs.close();
            //关闭statement
            s.close();
        } catch (Exception e) {
            getLog().error(e);
        } finally {
            try {
                //关闭连接
                conn.close();
            } catch (Exception e) {
                getLog().error(e);
            }
        }
        return resultStrings;
	}
	public BrowserBean searchForImport2(String names) {
		reformatSearch();//用sql语法解析器格式化sql
		BrowserBean browserBean = null;
		String[] name = names.split(",");
		String idString = "";
		String nameString = "";
		String descString = "";
		for(int i=0;i<name.length;i++){
			String[] resultString  = getIdByName(name[i]);
			idString +=resultString[0]+",";
			nameString +=resultString[1]+",";
			descString +=resultString[2]+",";
		}
		idString = idString.substring(0,idString.lastIndexOf(","));
		nameString = nameString.substring(0,nameString.lastIndexOf(","));
		descString = descString.substring(0,descString.lastIndexOf(","));
		browserBean = new BrowserBean();
        browserBean.setId(idString);
        browserBean.setName(nameString);
        browserBean.setDescription(descString);
        
        return browserBean;
	}
	/**
	 * 生成新的webservice需要的数据
	 * @param userid
	 * @param values
	 * @return
	 */
	public Map replaceDefaultValueMap(String userid,Map values)
	{
		if(Util.getIntValue(userid)<2)
		{
			return values;
		}
		Map newvalues = new HashMap();
		Map newparamtypes = new HashMap();
		Map newparamvalues = new HashMap();
		Map newparamarrays = new HashMap();
		
		Map paramtypes = (Map)values.get("type");
		Map paramvalues = (Map)values.get("value");
		Map paramarrays = (Map)values.get("array");
		newparamtypes.putAll(paramtypes);
		newparamarrays.putAll(paramarrays);
		Set keys = paramvalues.keySet();
		for(Iterator it = keys.iterator();it.hasNext();)
		{
			String key = Util.null2String((String)it.next());
			String keyvalue = (String)paramvalues.get(key);
			keyvalue = this.getDefaultValue(userid, keyvalue);
			newparamvalues.put(key, keyvalue);
		}
		newvalues.put("type", newparamtypes);
		newvalues.put("value", newparamvalues);
		newvalues.put("array", newparamarrays);
		return newvalues;
	}
	/**
	 * 替换字符串中的默认值
	 * @param userid
	 * @param str
	 * @return
	 */
	public String replaceDefaultValue(String userid,String str)
	{
		if(str.indexOf("{?userid}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?userid}", this.getDefaultValue(userid, "{?userid}"));
		}
		if(str.indexOf("{?loginid}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?loginid}", this.getDefaultValue(userid, "{?loginid}"));
		}
		if(str.indexOf("{?username}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?username}", this.getDefaultValue(userid, "{?username}"));
		}
		if(str.indexOf("{?workcode}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?workcode}", this.getDefaultValue(userid, "{?workcode}"));
		}
		if(str.indexOf("{?password}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?password}", this.getDefaultValue(userid, "{?password}"));
		}
		if(str.indexOf("{?departmentid}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?departmentid}", this.getDefaultValue(userid, "{?departmentid}"));
		}
		if(str.indexOf("{?departmentcode}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?departmentcode}", this.getDefaultValue(userid, "{?departmentcode}"));
		}
		if(str.indexOf("{?departmentname}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?departmentname}", this.getDefaultValue(userid, "{?departmentname}"));
		}
		if(str.indexOf("{?subcompanyid}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?subcompanyid}", this.getDefaultValue(userid, "{?subcompanyid}"));
		}
		if(str.indexOf("{?subcompanycode}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?subcompanycode}", this.getDefaultValue(userid, "{?subcompanycode}"));
		}
		if(str.indexOf("{?subcompanyname}")>-1)
		{
			str = DBTypeUtil.replaceString(str, "{?subcompanyname}", this.getDefaultValue(userid, "{?subcompanyname}"));
		}
		return str;
	}
	
	/**
	 * 在最终要到数据库执行SQL前，对SQL进行最后的表达式过滤
	 * @param sql
	 * @return
	 */
	public String  runSqlBeforePro(String sql) {
		sql = sql.replaceAll("''''","''").replaceAll("%''%","%%");//对空字符串的特殊处理
		sql = sql.replaceAll("from[ ]+table[ ]+\\(", "from table(");//对oracle表函数的特殊处理
		sql = sql.replaceAll("\\([ ]+\\{\\?", "({?");//对自定义变量的特殊处理
		
		//in和not in特殊情况处理
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]+((not|NOT)[\\s]+)?(in|IN)[\\s]+[\\(][\\s]*''''[\\s]*[\\)][\\s]+", " 1=1 ");
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]+((not|NOT)[\\s]+)?(in|IN)[\\s]+[\\(][\\s]*[']?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})[']?[\\s]*[\\)][\\s]+", " 1=1 ");
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]+((not|NOT)[\\s]+)?(in|IN)[\\s]+[\\(][\\s]*'*[\\s]*'*[\\s]*[\\)][\\s]+", " 1=1 ");
    	//like和not like特殊情况处理
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]+((not|NOT)[\\s]+)?(like|LIKE)[\\s]+[\\(]?[\\s]*[']?%?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})%?[']?[\\s]*[\\)]?[\\s]+", " 1=1 ");
    	sql = sql.replaceAll("'%''%'", "'%%'");
    	//去除between and
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]+((not|NOT)[\\s]+)?(between|BETWEEN)[\\s]+[']?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})[']?[\\s]+(and|AND)[\\s]+[']?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})[']?[\\s]+", " 1=1 ");
    	
    	//id=$fieldname$ id=$userid$
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]*[=<>!]{1,2}[\\s]*[']?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})[']?[\\s]+", " 1=1 ");
    	//$fieldname$=id  $userid$=id
    	sql = sql.replaceAll("[\\s]+[']?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})[']?[\\s]*[=<>!]{1,2}[\\s]*[_a-zA-Z0-9.]+[\\s]+", " 1=1 ");
    	
    	sql = sql.replaceAll("([ \\s\\(]+)[_a-zA-Z0-9.]*[\\(]?[_a-zA-Z0-9,.]*[']?[%]?\\$[_a-zA-Z0-9]+\\$[%]?[']?[_a-zA-Z0-9,.]*[\\)]?[ \\s]*([=<>!]{1,2}|not[\\s]+like|like|not[\\s]+in|in|NOT[\\s]+LIKE|LIKE|NOT[\\s]+IN|IN)[\\s]+[_a-zA-Z0-9.,\\(\\)]+([\\) \\s]+)", " $1 1=1 $3 ");
		sql = sql.replaceAll("([ \\s\\(]+)[_a-zA-Z0-9.,\\(\\)]+[ \\s]*([=<>!]{1,2}|not[\\s]+like|like|not[\\s]+in|in|NOT[\\s]+LIKE|LIKE|NOT[\\s]+IN|IN)[ \\s]*[_a-zA-Z0-9.]*[\\(]?[_a-zA-Z0-9,.]*[']?[%]?\\$[_a-zA-Z0-9]+\\$[%]?[']?[_a-zA-Z0-9,.]*[\\s]+[\\)]?([\\) \\s]+)", " $1 1=1 $3 ");
		sql = sql.replaceAll("([ \\s\\(]+)[_a-zA-Z0-9.]*[\\(]?[_a-zA-Z0-9,.]*[']?[%]?\\{\\?[a-z]+\\}[%]?[']?[_a-zA-Z0-9,.]*[\\)]?[ \\s]*([=<>!]{1,2}|not[\\s]+like|like|not[\\s]+in|in|NOT[\\s]+LIKE|LIKE|NOT[\\s]+IN|IN)[ \\s]*[_a-zA-Z0-9.,\\(\\)]+([\\) \\s]+)", " $1 1=1 $3 ");
    	sql = sql.replaceAll("([ \\s\\(]+)[_a-zA-Z0-9.,\\(\\)]+[ \\s]*([=<>!]{1,2}|not[\\s]+like|like|not[\\s]+in|in|NOT[\\s]+LIKE|LIKE|NOT[\\s]+IN|IN)[ \\s]*[_a-zA-Z0-9.]*[\\(]?[_a-zA-Z0-9,.]*[']?[%]?\\{\\?[a-z]+\\}[%]?[']?[_a-zA-Z0-9,.]*[\\)]?([\\) \\s]+)", " $1 1=1 $3 ");
    	
    	return sql;
	}
	
	/**
	 * 去除人员关联字段
	 * @param sql
	 * @return
	 */
	public String removeDefaultValue(String sql)
	{
		sql = sql.replaceAll("''''","''").replaceAll("%''%","%%");//对空字符串的特殊处理
		sql = sql.replaceAll("from[ ]+table[ ]+\\(", "from table(");//对oracle表函数的特殊处理
		sql = sql.replaceAll("\\([ ]+\\{\\?", "({?");//对自定义变量的特殊处理
		sql = sql.replaceAll("([ \\s\\(]+)[_a-zA-Z0-9.]*[\\(]?[_a-zA-Z0-9,.]*[']?[%]?\\{\\?[a-z]+\\}[%]?[']?[_a-zA-Z0-9,.]*[\\)]?[ \\s]*([=<>!]{1,2}|like|in|LIKE|IN)[ \\s]*[_a-zA-Z0-9.,\\(\\)]+([\\) \\s]+)", " $1 1=1 $3 ");
    	sql = sql.replaceAll("([ \\s\\(]+)[_a-zA-Z0-9.,\\(\\)]+[ \\s]*([=<>!]{1,2}|like|in|LIKE|IN)[ \\s]*[_a-zA-Z0-9.]*[\\(]?[_a-zA-Z0-9,.]*[']?[%]?\\{\\?[a-z]+\\}[%]?[']?[_a-zA-Z0-9,.]*[\\)]?([\\) \\s]+)", " $1 1=1 $3 ");
		
		return sql;
	}
	
	public String removeDefaultValueAgain(String sql){
		return sql.replaceAll("\\{\\?[a-z]+\\}","'@^@'");
	}
	
	public String removeParam(String sql){
		sql = sql.replaceAll("[ \\\\s]+[_a-zA-Z0-9.,\\(\\)]+[ \\\\s]*=[ \\\\s]*[']?\\$[_a-zA-Z0-9]+\\$[']?[ \\\\s]+", " 1=1 ");
    	sql = sql.replaceAll("[ \\\\s]+[_a-zA-Z0-9.,\\(\\)]+[ \\\\s]*=[ \\\\s]*[']?\\{\\?[_a-zA-Z0-9]+\\}[']?[ \\\\s]+", " 1=1 ");
    	
    	//in和not in特殊情况处理
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]+((not|NOT)[\\s]+)?(in|IN)[\\s]+[\\(][\\s]*''''[\\s]*[\\)][\\s]+", " 1=1 ");
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]+((not|NOT)[\\s]+)?(in|IN)[\\s]+[\\(][\\s]*[']?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})[']?[\\s]*[\\)][\\s]+", " 1=1 ");
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]+((not|NOT)[\\s]+)?(in|IN)[\\s]+[\\(][\\s]*'*[\\s]*'*[\\s]*[\\)][\\s]+", " 1=1 ");
    	//like和not like特殊情况处理
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]+((not|NOT)[\\s]+)?(like|LIKE)[\\s]+[\\(]?[\\s]*[']?%?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})%?[']?[\\s]*[\\)]?[\\s]+", " 1=1 ");
    	sql = sql.replaceAll("'%''%'", "'%%'");
    	//去除between and
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]+((not|NOT)[\\s]+)?(between|BETWEEN)[\\s]+[']?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})[']?[\\s]+(and|AND)[\\s]+[']?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})[']?[\\s]+", " 1=1 ");
    	
    	//id=$fieldname$ id=$userid$
    	sql = sql.replaceAll("[\\s]+[_a-zA-Z0-9.]+[\\s]*[=<>!]{1,2}[\\s]*[']?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})[']?[\\s]+", " 1=1 ");
    	//$fieldname$=id  $userid$=id
    	sql = sql.replaceAll("[\\s]+[']?(\\$|\\{\\?)[_a-zA-Z0-9]+(\\$|\\})[']?[\\s]*[=<>!]{1,2}[\\s]*[_a-zA-Z0-9.]+[\\s]+", " 1=1 ");
    	
    	return sql;
	}
	
	/**
	 * 获取默认熟悉数据
	 * @param userid
	 * @param paramname
	 * @return
	 */
	public String getDefaultValue(String userid,String paramname)
	{
		String sql = "select * from hrmresource h where h.id="+userid;
		RecordSet rs = new RecordSet();
		rs.executeSql(sql);
		rs.next();
		String resultval = "";
		if(paramname.indexOf("{?userid}")>-1)
		{
			resultval = userid;
		}
		else if(paramname.indexOf("{?loginid}")>-1)
		{
			resultval = rs.getString("loginid");
		}
		else if(paramname.indexOf("{?username}")>-1)
		{
			resultval = rs.getString("lastname");
		}
		else if(paramname.indexOf("{?workcode}")>-1)
		{
			resultval = rs.getString("workcode");
		}
		else if(paramname.indexOf("{?password}")>-1)
		{
			resultval = rs.getString("password");
		}
		else if(paramname.indexOf("{?departmentid}")>-1)
		{
			resultval = rs.getString("departmentid");
		}
		else if(paramname.indexOf("{?departmentcode}")>-1)
		{
			resultval = rs.getString("departmentid");
			if(!"".equals(resultval))
			{
				rs.executeSql("select departmentcode from hrmdepartment d where id="+resultval);
				rs.next();
				resultval = rs.getString("departmentcode");
			}
			else
			{
				resultval = "";
			}
		}
		else if(paramname.indexOf("{?departmentname}")>-1)
		{
			resultval = rs.getString("departmentid");
			if(!"".equals(resultval))
			{
				rs.executeSql("select departmentname from hrmdepartment d where id="+resultval);
				rs.next();
				resultval = rs.getString("departmentname");
			}
			else
			{
				resultval = "";
			}
		}
		else if(paramname.indexOf("{?subcompanyid}")>-1)
		{
			resultval = rs.getString("subcompanyid1");
		}
		else if(paramname.indexOf("{?subcompanycode}")>-1)
		{
			resultval = rs.getString("subcompanyid1");
			if(!"".equals(resultval))
			{
				rs.executeSql("select subcompanycode from hrmsubcompany s where id="+resultval);
				rs.next();
				resultval = rs.getString("subcompanycode");
			}
			else
			{
				resultval = "";
			}
		}
		else if(paramname.indexOf("{?subcompanyname}")>-1)
		{
			resultval = rs.getString("subcompanyid1");
			if(!"".equals(resultval))
			{
				rs.executeSql("select subcompanyname from hrmsubcompany s where id="+resultval);
				rs.next();
				resultval = rs.getString("subcompanyname");
			}
			else
			{
				resultval = "";
			}
		}else {
			resultval=paramname;
		}
		return resultval;
	}
	/**
	 * 获取被替换后的链接地址
	 */
	public String getHref(String userid,String href)
	{
		return this.replaceDefaultValue(userid, href);
	}
	public Map getShowfieldMap()
	{
		return showfieldMap;
	}
	public void setShowfieldMap(Map showfieldMap)
	{
		this.showfieldMap = showfieldMap;
	}
	public Map getSearchfieldMap()
	{
		// TODO Auto-generated method stub
		return searchfieldMap;
	}
	public void setSearchfieldMap(Map searchfieldMap)
	{
		this.searchfieldMap = searchfieldMap;
	}
	public void setSearchValueMap(Map searchValueMap)
	{
		// TODO Auto-generated method stub
		//this.searchValueMap = searchValueMap;
	}
	public String getShowtree()
	{
		return showtree;
	}
	public void setShowtree(String showtree)
	{
		this.showtree = showtree;
	}
	public String getNodename()
	{
		return nodename;
	}
	public void setNodename(String nodename)
	{
		this.nodename = nodename;
	}
	public String getParentid()
	{
		return parentid;
	}
	public void setParentid(String parentid)
	{
		this.parentid = parentid;
	}
	public String getIsmutil()
	{
		return ismutil;
	}
	public void setIsmutil(String ismutil)
	{
		this.ismutil = ismutil;
	}
	public String getParentfield()
	{
		return parentfield;
	}
	public void setParentfield(String parentfield)
	{
		this.parentfield = parentfield;
	}
	public String getDatasourceid()
	{
		return datasourceid;
	}
	public void setDatasourceid(String datasourceid)
	{
		this.datasourceid = datasourceid;
	}
	public String getShowname()
	{
		return showname;
	}
	public void setShowname(String showname)
	{
		this.showname = showname;
	}
	public String getKeyfield()
	{
		return keyfield;
	}
	public String getNamefield()
	{
		return namefield;
	}
	public int getDatafrom()
	{
		return datafrom;
	}
	public void setDatafrom(int datafrom)
	{
		this.datafrom = datafrom;
	}
	public Map getWokflowfieldnameMap()
	{
		return wokflowfieldnameMap;
	}
	
	public String getPoolname() {
		String vdatasource = "";
		//如果有外部数据源就用外部数据源没有就用系统数据源
        if(ds!=null){
        	vdatasource = ds.toString();
        	int index = vdatasource.indexOf("datasource.");
        	if(index>-1){
        		vdatasource = vdatasource.substring(index, vdatasource.indexOf("("));
        		vdatasource = vdatasource.replace("datasource.", "");
        	}
        }
        return vdatasource;
	}
	public String getName()
	{
		return this.name;
	}
	public Map getParamvalues() {
		return paramvalues;
	}
	public void setParamvalues(Map paramvalues) {
		this.paramvalues = paramvalues;
		this.values.put("value", this.paramvalues);
	}
	public Map getSearchValueMap() {
		return null;
	}
	public String getCustomid() {
		return customid;
	}
	public void setCustomid(String customid) {
		this.customid = customid;
	}
	public String getTempsearch() {
		return tempsearch;
	}
	public void setTempsearch(String tempsearch) {
		this.tempsearch = tempsearch;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * unicode解码
	 * @param s
	 * @return
	 */
	public String decode(String s){
		String result = "";
		s = s.replaceAll("\\\\\\\\u", "\\\\u");
		result = decodeUnicode(s);
		result = decodeHtmlUnicode(result);
		
		return result;
	}
	
	private String decodeHtmlUnicode(String htmlUnicode){
		String s = htmlUnicode;
		Pattern pattern = Pattern.compile("&#\\d*;");
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()){
			String f = matcher.group();
			String c = f.substring(2,f.length()-1);
			String d = Character.toString((char) Integer.parseInt(c));
			s = s.replaceFirst(f, d);
		}
		
		return s;
	}
	
	private String decodeUnicode(String s) {
		char[] in = s.toCharArray();
		int off = 0;
		char c;
		char[] out = new char[in.length];
		int outLen = 0;
		while (off < in.length) {
			c = in[off++];
			if (c == '\\') {
				if (in.length > off) { // 是否有下一个字符
					c = in[off++]; // 取出下一个字符
				} else {
					out[outLen++] = '\\'; // 末字符为'\'，返回
					break;
				}
				if (c == 'u') { // 如果是"\\u"

					int value = 0;
					if (in.length >= off + 4) { // 判断"\\u"后边是否有四个字符

						boolean isUnicode = true;
						for (int i = 0; i < 4; i++) { // 遍历四个字符
							c = in[off++];
							switch (c) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + c - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + c - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + c - 'A';
								break;
							default:
								isUnicode = false; // 判断是否为unicode码
							}
						}
						if (isUnicode) { // 是unicode码转换为字符
							out[outLen++] = (char) value;
						} else { // 不是unicode码把"\\uXXXX"填入返回值

							off = off - 4;
							out[outLen++] = '\\';
							out[outLen++] = 'u';
							out[outLen++] = in[off++];
						}
					} else { // 不够四个字符则把"\\u"放入返回结果并继续

						out[outLen++] = '\\';
						out[outLen++] = 'u';
						continue;
					}
				} else {
					switch (c) { // 判断"\\"后边是否接特殊字符，回车，tab一类的
					case 't':
						c = '\t';
						out[outLen++] = c;
						break;
					case 'r':
						c = '\r';
						out[outLen++] = c;
						break;
					case 'n':
						c = '\n';
						out[outLen++] = c;
						break;
					case 'f':
						c = '\f';
						out[outLen++] = c;
						break;
					default:
						out[outLen++] = '\\';
						out[outLen++] = c;
						break;
					}
				}
			} else {
				out[outLen++] = (char) c;
			}
		}
		return new String(out, 0, outLen);
	}
	
	public void isMobile(String ismobile) {
		if("1".equals(ismobile)){
		   this.isMobile = true;
		}else{
			this.isMobile = false;
		}
	}
	
	public void setRequestFormInfoForImport(Map requestFormInfoMap){
		 this.requestFormInfoMap = requestFormInfoMap;
	 }
}
