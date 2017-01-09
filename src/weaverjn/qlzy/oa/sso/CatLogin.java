package weaverjn.qlzy.oa.sso;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;

/**
 * 用户通过三统一平台认证后，将用户的登录信息写入Session
 * 
 * @author 侯正刚
 * @version 1.0
 * @created 20140730
 */
public class CatLogin {
	/**
	 * 根据用户ID获取用户信息写入Session
	 * 
	 * @param request
	 * @param httpservletresponse
	 * @param userId
	 * @return
	 */
	public static String loginByUserId(HttpServletRequest request,HttpServletResponse httpservletresponse, String userId) {
		RecordSet rs=new RecordSet();
		if("1".equals(userId)){
			rs.executeSql("select * from HrmResourceManager where id='"+userId+"'");
			if(rs.next())
			{
				login(request,httpservletresponse,rs);
				return "1";
			}
		}else{
			rs.executeSql("select * from hrmresource where id='"+userId+"'");
			if(rs.next())
			{
				login(request,httpservletresponse,rs);
				return "1";
			}
		}
		return "26";
	}
	/**
	 * 根据用户Code获取用户信息写入Session
	 * 
	 * @param request
	 * @param httpservletresponse
	 * @param code
	 * @return
	 */
	public static String loginByCode(HttpServletRequest request,HttpServletResponse httpservletresponse, String code) {
		RecordSet rs=new RecordSet();
		rs.executeSql("select * from hrmresource where workcode='"+code+"'");
		if(rs.next())
		{
			login(request,httpservletresponse,rs);
			return "1";
		}
		return "26";
	}
	/**
	 * 根据用户登录名获取用户信息写入Session
	 * @param request
	 * @param httpservletresponse
	 * @param loginId
	 * @return
	 */
	public static String loginByLoginId(HttpServletRequest request,HttpServletResponse httpservletresponse, String loginId) {
		RecordSet rs=new RecordSet();
		rs.executeSql("select * from hrmresource where loginid='"+loginId+"'");
		if(rs.next())
		{
			login(request,httpservletresponse,rs);
			return "1";
		}
		return "26";
	}
	/**
	 * 用户信息写入Session
	 * @param httpservletrequest
	 * @param httpservletresponse
	 * @param recordset
	 */
	public static void login(HttpServletRequest httpservletrequest,HttpServletResponse httpservletresponse, RecordSet recordset) {
		Calendar calendar = Calendar.getInstance();
		String s10 = (new StringBuilder())
				.append(Util.add0(calendar.get(1), 4)).append("-")
				.append(Util.add0(calendar.get(2) + 1, 2)).append("-")
				.append(Util.add0(calendar.get(5), 2)).toString();

		User user = new User();
		user.setUid(recordset.getInt("id"));
		user.setLoginid(recordset.getString("loginid"));
		user.setFirstname(recordset.getString("firstname"));
		user.setLastname(recordset.getString("lastname"));
		user.setAliasname(recordset.getString("aliasname"));
		user.setTitle(recordset.getString("title"));
		user.setTitlelocation(recordset.getString("titlelocation"));
		user.setSex(recordset.getString("sex"));
		String s23 = recordset.getString("systemlanguage");
		user.setLanguage(Util.getIntValue(s23, 0));
		user.setTelephone(recordset.getString("telephone"));
		user.setMobile(recordset.getString("mobile"));
		user.setMobilecall(recordset.getString("mobilecall"));
		user.setEmail(recordset.getString("email"));
		user.setCountryid(recordset.getString("countryid"));
		user.setLocationid(recordset.getString("locationid"));
		user.setResourcetype(recordset.getString("resourcetype"));
		user.setStartdate(recordset.getString("startdate"));
		user.setEnddate(recordset.getString("enddate"));
		user.setContractdate(recordset.getString("contractdate"));
		user.setJobtitle(recordset.getString("jobtitle"));
		user.setJobgroup(recordset.getString("jobgroup"));
		user.setJobactivity(recordset.getString("jobactivity"));
		user.setJoblevel(recordset.getString("joblevel"));
		user.setSeclevel(recordset.getString("seclevel"));
		user.setUserDepartment(Util.getIntValue(
				recordset.getString("departmentid"), 0));
		user.setUserSubCompany1(Util.getIntValue(
				recordset.getString("subcompanyid1"), 0));
		user.setUserSubCompany2(Util.getIntValue(
				recordset.getString("subcompanyid2"), 0));
		user.setUserSubCompany3(Util.getIntValue(
				recordset.getString("subcompanyid3"), 0));
		user.setUserSubCompany4(Util.getIntValue(
				recordset.getString("subcompanyid4"), 0));
		user.setManagerid(recordset.getString("managerid"));
		user.setAssistantid(recordset.getString("assistantid"));
		user.setPurchaselimit(recordset.getString("purchaselimit"));
		user.setCurrencyid(recordset.getString("currencyid"));
		user.setLastlogindate(s10);
		user.setLogintype("1");
		user.setAccount(recordset.getString("account"));
		user.setLoginip(httpservletrequest.getRemoteAddr());
		httpservletrequest.getSession(true).setAttribute("weaver_user@bean",
				user);
		//TODO 此处暂时定为login/login.jsp  惹遇到其它登录问题需要改动这里
		Util.setCookie(httpservletresponse, "loginfileweaver", "/login/login.jsp", 0x2a300);
		Util.setCookie(httpservletresponse, "loginidweaver", recordset.getString("loginid"), 0x2a300);
		Util.setCookie(httpservletresponse, "loginfileweaver", "/login/login.jsp", 0x2a300);
		Util.setCookie(httpservletresponse, "loginidweaver",
				(new StringBuilder()).append("").append(user.getUID())
						.toString(), 0x2a300);
		Util.setCookie(httpservletresponse, "languageidweaver", s23, 0x2a300);

	}
//	public static void main(String args[]){
//		String code="";
//		String s = CatLogin.loginByCode(HttpServletRequest request,HttpServletResponse httpservletresponse, code);
//	}
}
