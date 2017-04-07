package weaverjn.test;

import com.runqian.report4.model.expression.function.string.URLEncode;
import com.sap.mw.jco.JCO;
import com.steadystate.css.parser.SACParserCSS1;
import freemarker.ext.beans.HashAdapter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaverjn.qlzy.sap.WSClientUtils;
import weaverjn.qlzy.sap.utils.SAPEquipmentInfo;
import weaverjn.qlzy.sap.utils.UpdateITEquipmentInfoFromSAP;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by zhaiyaqi on 2016/11/29.
 */
public class test {
    public static void main(String[] args) {
        String s = "00000000000000000000123";
        System.out.println(new test().del0(s));
    }

    private String del0(String s) {
        while (s.startsWith("0")) {
            s = s.substring(1);
        }
        return s;
    }
}
