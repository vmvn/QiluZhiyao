package weaverjn.qlzy.sap.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;
import weaver.general.BaseBean;
import weaverjn.qlzy.sap.browser.Equipment2OABrowser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhaiyaqi on 2016/12/13.
 */
public class linkage extends BaseBean {
    public String run(String s, String type, String company) {
        if (type.equals("Equipment2OA")) {
            Equipment2OABrowser equipment2OABrowser = new Equipment2OABrowser();
            String datas = equipment2OABrowser.run(s, company, "", "", "", "", "");
            Document dom;
            try {
                dom = DocumentHelper.parseText(datas);
                Element root = dom.getRootElement();
                Iterator iterator = root.elementIterator("bean");
                Map<String, Map<String, String>> m = new HashMap<String, Map<String, String>>();
                while (iterator.hasNext()) {
                    Element e = (Element) iterator.next();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("EQKTX", e.elementText("EQKTX"));
                    map.put("EQART", e.elementText("EQART"));
                    map.put("TYPBZ", e.elementText("TYPBZ"));
                    map.put("INBDT", e.elementText("INBDT"));
                    map.put("TPLNR", e.elementText("TPLNR"));
                    map.put("HERST", e.elementText("HERST"));
                    map.put("TIDNR", e.elementText("TIDNR"));
                    map.put("KOSTL", e.elementText("KOSTL"));
                    map.put("ANLNR", e.elementText("ANLNR"));
                    map.put("ANLN2", e.elementText("ANLN2"));
                    map.put("SRC_VALUE", e.elementText("SRC_VALUE"));
                    map.put("NET_VALUE", e.elementText("NET_VALUE"));
                    m.put(e.elementText("EQUNR"), map);
                }
                JSONObject json = new JSONObject(m);
                System.out.println(new JSONObject((Map)json.get(s)));
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void main(String[] args) {
        linkage t = new linkage();
        t.run("000000000010000044", "Equipment2OA", "63");
    }
}
