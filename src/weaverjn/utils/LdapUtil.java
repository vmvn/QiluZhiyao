package weaverjn.utils;

import com.novell.ldap.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LdapUtil {

	private LDAPConnection lc = null;

	public boolean verify(String loginId, String password) {
		boolean b;
		String ldapHost = "192.168.1.35";
		String loginDN = "";
		try {
			loginDN = getDN(loginId);
			closeLDAP();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int ldapVersion = LDAPConnection.LDAP_V3;
		int ldapPort = LDAPConnection.DEFAULT_PORT;
		try {
			LDAPConnection LDApConnection = new LDAPConnection();
			LDApConnection.connect(ldapHost, ldapPort);
			LDApConnection.bind(ldapVersion, loginDN, password.getBytes("UTF8"));
			System.out.println(loginDN + " 连接成功!");
			b = true;
			LDApConnection.disconnect();
		} catch (LDAPException e) {
			System.out.println(loginDN + " 连接异常!");
			b = false;
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			b = false;
			e.printStackTrace();
		}
		return b;
	}

	// 链接
	public LDAPConnection connetLDAP() throws UnsupportedEncodingException {
		String ldapHost = "192.168.1.35"; //PropertiesUtil.getValue("sccbaldap", "ldaphost");
		String loginDN = "CN=Administrator,CN=Users,DC=qilu-pharma,DC=com";//PropertiesUtil.getValue("sccbaldap", "logindn");
		String password = "www.qlzy.com123"; //PropertiesUtil.getValue("sccbaldap", "password");
		int ldapVersion = LDAPConnection.LDAP_V3;
		int ldapPort = LDAPConnection.DEFAULT_PORT;
		try {
			lc = new LDAPConnection();
			lc.connect(ldapHost, ldapPort);
			lc.bind(ldapVersion, loginDN, password.getBytes("UTF8"));
			System.out.println("连接成功!");
		} catch (LDAPException e) {
			System.out.println("连接异常!");
			e.printStackTrace();
		}
		return lc;
	}

	// 断开链接
	public void closeLDAP() {
		if (lc != null) {
			try {
				lc.disconnect();
			} catch (LDAPException e) {
				e.printStackTrace();
			}
		}
	}

	// 查询用户DN
	public String getDN(String samaccountname) throws UnsupportedEncodingException {
		String userDN = "";
		try {
			if (lc == null) {
				connetLDAP();
			}
			LDAPSearchResults rs = lc.search("DC=qilu-pharma,DC=com",LDAPConnection.SCOPE_SUB,"(&(objectCategory=person)(objectClass=user)(samaccountname="+ samaccountname + "))", null, false);

			if (rs.hasMore()) {
				LDAPEntry entry = rs.next();
				userDN = entry.getDN();
			}
		} catch (LDAPException e) {
			e.printStackTrace();
		}
		return userDN;
	}

	// 返回entry
	public LDAPEntry getEntry(String samaccountname) throws UnsupportedEncodingException {
		try {
			if (lc == null) {
				connetLDAP();
			}
			LDAPSearchResults rs = lc.search("dc=sccba,dc=org",LDAPConnection.SCOPE_SUB,"(&(objectCategory=person)(objectClass=user)(samaccountname="+ samaccountname + "))", null, false);

			if (rs.hasMore()) {
				LDAPEntry entry = rs.next();
				return entry;
			} else {
				return null;
			}
		} catch (LDAPException e) {
			e.printStackTrace();
			return null;
		}

	}

	// 添加用户
	public void addUser(String uid,String displayName,String departmentname,String ou) throws UnsupportedEncodingException {
		try {
			if (lc == null) {
				connetLDAP();
			}
			String baseDN = "OU="+departmentname+","+ou+",OU=BanksAlliance,dc=sccba,dc=org";// baseDN要从流程页面获取
			String dn = "cn=" + uid + baseDN;
			// 以下字段从流程页面获取
			LDAPAttributeSet attributeSet = new LDAPAttributeSet();
			attributeSet.add(new LDAPAttribute("objectclass", "user"));
			attributeSet.add(new LDAPAttribute("sAMAccountName", uid));
			attributeSet.add(new LDAPAttribute("userPrincipalName", uid + "@sccba.org"));
			attributeSet.add(new LDAPAttribute("displayName", displayName));
			attributeSet.add(new LDAPAttribute("department", departmentname));
			attributeSet.add(new LDAPAttribute("company", departmentname));
			attributeSet.add(new LDAPAttribute("sn", uid));
			attributeSet.add(new LDAPAttribute("distinguishedName", dn));
			attributeSet.add(new LDAPAttribute("userAccountControl", "553"));
			attributeSet.add(new LDAPAttribute("pwdLastSet", "-1"));
			// 创建的用户密码为空节点结束后提示用户修改密码
			LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
			lc.add(newEntry);
			System.out.println("创建成功！");
			System.out.println("Add object: " + dn + " successfully.");
		} catch (LDAPException e) {
			e.printStackTrace();
		}
	}

	// 添加联盟用户
	public void addlmUser(String uid, String num,String displayName,String departmentname) throws UnsupportedEncodingException {
		try {
			if (lc == null) {
				connetLDAP();
			}
			String baseDN = "OU="+departmentname+",OU=SCCBA,OU=BanksAlliance,dc=sccba,dc=org";// baseDN要从流程页面获取
			String dn = "cn=" + uid + "," + baseDN;
			// 以下字段从流程页面获取

			LDAPAttributeSet attributeSet = new LDAPAttributeSet();
			attributeSet.add(new LDAPAttribute("objectclass", "user"));
			attributeSet.add(new LDAPAttribute("sAMAccountName", uid));
			attributeSet.add(new LDAPAttribute("userPrincipalName", uid + "@sccba.org"));
			attributeSet.add(new LDAPAttribute("displayName", displayName));
			attributeSet.add(new LDAPAttribute("pager", num));// 联盟员工必须填写员工号
			attributeSet.add(new LDAPAttribute("sn", uid));
			attributeSet.add(new LDAPAttribute("distinguishedName", dn));
			attributeSet.add(new LDAPAttribute("userAccountControl", "553"));
			attributeSet.add(new LDAPAttribute("pwdLastSet", "-1"));
			// 创建的用户密码为空节点结束后提示用户修改密码
			LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);

			lc.add(newEntry);
			System.out.println("创建成功！");
			System.out.println("Add object: " + dn + " successfully.");
		} catch (LDAPException e) {
			e.printStackTrace();
		}
	}

	// 禁用用户
	public void disableUser(String uid) throws UnsupportedEncodingException,LDAPException {
		if (lc == null) {
			connetLDAP();
		}
		List<LDAPModification> modList = new ArrayList<LDAPModification>();

		String dn = getDN(uid);
		// LDAPEntry userEntry =getEntry(uid);
		try {
			LDAPAttribute attribute = new LDAPAttribute("userAccountControl","514");
			modList.add(new LDAPModification(LDAPModification.REPLACE,attribute));
			// 禁用
			// attribute = new LDAPAttribute("telephoneNumber", "11111111");
			// modList.add(new LDAPModification(LDAPModification.ADD,
			// attribute));
			LDAPModification[] mods = new LDAPModification[modList.size()];
			mods = (LDAPModification[]) modList.toArray(mods);
			
			lc.modify(dn, mods);
		} catch (LDAPException e) {
			e.printStackTrace();
		}

		System.out.println("禁用 : " + uid + " successfully.");
	}

	// 删除用户
	public void deleteUser(String uid) throws UnsupportedEncodingException {
		try {
			if (lc == null) {
				connetLDAP();
			}
			String dn = "cn=" + uid + ",dc=sccba,dc=org";
			lc.delete(dn);
			System.out.println("删除用户成功！");
			System.out.println("Delete object: " + dn + " successfully.");
		} catch (LDAPException e) {
			e.printStackTrace();
		}
	}

	// 判断用户是否存在
	public String userExits(String uid) throws UnsupportedEncodingException,LDAPException {
		try {
			if (lc == null) {
				connetLDAP();
			}
//			String dn = "cn=" + uid + ",dc=sccba,dc=org";
			LDAPSearchResults rs = lc.search("dc=sccba,dc=org",LDAPConnection.SCOPE_SUB,"(&(objectCategory=person)(objectClass=user)(samaccountname="+ uid + "))", null, false);
			if (rs.getCount() == 4 || rs.getCount() == 0) {
				System.out.println("用户已经存在");
				return "4";
			} else {
				System.out.println("用户不存在");
				return "3";
			}
		} catch (LDAPException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	// 添加到组
	public void addtoGroup(String groupname, String username) throws UnsupportedEncodingException {
		String groupDN = "";
		String userDN = "";
		if (lc == null) {
			connetLDAP();
		}

		groupDN = getgroupDN(groupname);
		userDN = getDN(username);
		List<LDAPModification> modList = new ArrayList<LDAPModification>();
		try {
			LDAPAttribute attribute = new LDAPAttribute("member", userDN);
			modList.add(new LDAPModification(LDAPModification.ADD, attribute));

			LDAPModification[] mods = new LDAPModification[modList.size()];
			mods = (LDAPModification[]) modList.toArray(mods);

			lc.modify(groupDN, mods);
			System.out.println("添加 : " + username + " 到" + groupname + "成功");
		} catch (LDAPException e) {
			e.printStackTrace();
		}
	}

	// 返回组DN
	public String getgroupDN(String groupname) throws UnsupportedEncodingException {
		String groupDN = "";
		try {
			if (lc == null) {
				connetLDAP();
			}
			LDAPSearchResults rs = lc.search("dc=sccba,dc=org",LDAPConnection.SCOPE_SUB,"(&(objectCategory=group)(samaccountname=" + groupname + "))", null, false);

			if (rs.hasMore()) {
				LDAPEntry entry = rs.next();
				groupDN = entry.getDN();
				System.out.println(groupDN);
			}
		} catch (LDAPException e) {
			e.printStackTrace();
		}
		return groupDN;
	}

	public static void main(String[] args) throws Exception {
		LdapUtil tt = new LdapUtil();
//		String s = tt.getgroupDN("Citrix Restricted WAN Users");
//		System.out.println(s);
		tt.verify("lixin.geng", "gaoxinqa1201+");
		// tt.deleteUser("zhansan33");
	}
}