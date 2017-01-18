package weaverjn.qlzy.crm;

import weaver.general.MD5;
import weaver.hrm.User;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaiyaqi on 2017/1/11.
 */
public class sso {
    public static void main(String[] args) {
        MD5 md5 = new MD5();
        String s = md5.getMD5ofStr("wanhe" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "InspurPassw0rd");
        System.out.println(new sso().ssourl("wanhe", "http://192.168.1.56/drp/gs5/DrpLogin/GenerSoftErpSSODo.ashx", "001"));
    }

    public String ssourl(String loginid, String url, String dbid) {
        MD5 md5 = new MD5();
        String U_Token = md5.getMD5ofStr(loginid + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "InspurPassw0rd");
        String ssourl = url+ "?dbid=" + dbid + "&userid=" + loginid + "&U_Token=" + U_Token;
        return ssourl;
    }

}
