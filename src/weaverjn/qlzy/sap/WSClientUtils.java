package weaverjn.qlzy.sap;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

public class WSClientUtils {
    public WSClientUtils() {
        super();
    }

    public static String callWebService(String soapRequest, String serviceEpr, String username, String password) {
        return callWebService(soapRequest, serviceEpr, "application/soap+xml; charset=utf-8", username, password);
    }

    public static String callWebService(String soapRequest, String serviceEpr, String contentType, String username, String password) {
        PostMethod postMethod = new PostMethod(serviceEpr);
        //设置POST方法请求超时
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 50000);

        try {
            StringRequestEntity requestEntity = new StringRequestEntity(soapRequest, contentType, "UTF-8");
            postMethod.setRequestEntity(requestEntity);

            HttpClient httpClient = new HttpClient();
            Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
            httpClient.getState().setCredentials(AuthScope.ANY, defaultcreds);
            HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
            // 设置连接超时时间(单位毫秒)
            managerParams.setConnectionTimeout(300000);
            // 设置读数据超时时间(单位毫秒)
            managerParams.setSoTimeout(6000000);
            int statusCode = httpClient.executeMethod(postMethod);
            String soapRequestData = postMethod.getResponseBodyAsString();
            if (statusCode != HttpStatus.SC_OK) {
//                throw new IllegalStateException("调用webservice错误 : " + postMethod.getStatusLine() + ";" + statusCode + ";;" + serviceEpr);
                soapRequestData = "SAP接口报错";
            }
            return soapRequestData;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "errorMessage : " + e.getMessage();
        } catch (HttpException e) {
            e.printStackTrace();
            return "errorMessage : " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return "errorMessage : " + e.getMessage();
        } finally {
            postMethod.releaseConnection();
        }
    }


    public static String callWebServiceWithHttpHeaderParm(String soapRequest, String serviceEpr, HashMap<String, String> httpHeaderParm) {
        return callWebService(soapRequest, serviceEpr, "application/soap+xml; charset=utf-8", httpHeaderParm);
    }

    public static String callWebService(String soapRequest, String serviceEpr, String contentType, HashMap<String, String> httpHeaderParm) {

        PostMethod postMethod = new PostMethod(serviceEpr);
        //设置POST方法请求超时
        Set<String> keySet = httpHeaderParm.keySet();

        for (String key : keySet) {
            postMethod.setRequestHeader(key, httpHeaderParm.get(key));
        }

        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 600000);

        try {
            StringRequestEntity requestEntity = new StringRequestEntity(soapRequest, contentType, "UTF-8");
            postMethod.setRequestEntity(requestEntity);

            HttpClient httpClient = new HttpClient();
            /*RecordSet rs = new RecordSet();
            rs.executeSql("select username, password from pi4oa_info where id=0");*/
            String username = "zappluser_oa";
            String password = "a1234567";
            /*if (rs.next()) {
                username = rs.getString("username");
                password = rs.getString("password");
                System.out.println("---->username:" + username + "\n---->password:" + password);
            }*/
            Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
            httpClient.getState().setCredentials(AuthScope.ANY, defaultcreds);
            HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
            // 设置连接超时时间(单位毫秒)
            managerParams.setConnectionTimeout(300000);
            // 设置读数据超时时间(单位毫秒)
            managerParams.setSoTimeout(6000000);
            int statusCode = httpClient.executeMethod(postMethod);
            String soapRequestData = postMethod.getResponseBodyAsString();
            if (statusCode != HttpStatus.SC_OK) {
//                throw new IllegalStateException("调用webservice错误 : " + postMethod.getStatusLine() + ";" + statusCode + ";" + serviceEpr + ";" + soapRequest);
                soapRequestData = "SAP 接口报错";
            }
            return soapRequestData;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "errorMessage : " + e.getMessage();
        } catch (HttpException e) {
            e.printStackTrace();
            return "errorMessage : " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return "errorMessage : " + e.getMessage();
        } finally {
            postMethod.releaseConnection();
        }
    }
}
