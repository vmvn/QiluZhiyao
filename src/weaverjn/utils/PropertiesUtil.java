package weaverjn.utils;

import weaver.general.BaseBean;
import weaver.general.GCONST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhaiyaqi on 2017/3/3.
 */
public class PropertiesUtil extends BaseBean {
    private String root = GCONST.getPropertyPath();

    public String getPropValue(String propFile, String key) {
        Properties properties = new Properties();
        String value = "";
        String filepath = root + propFile + ".properties";
        if (new File(filepath).exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(filepath);
                properties.load(fileInputStream);
                fileInputStream.close();
                value = properties.getProperty(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            writeLog("<PropertiesUtil> file '" + propFile + "' do not exists");
        }
        return value;
    }

    public void setPropValue(String propFile, String key, String value) {
        Properties properties = new Properties();
        String filepath = root + propFile + ".properties";
        if (new File(filepath).exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(filepath);
                properties.setProperty(key, value);
                properties.store(fileOutputStream, "Update " + key + " value");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            writeLog("<PropertiesUtil> file '" + propFile + "' do not exists");
        }
    }

    public boolean compare(String v1, String propFile, String key) {
        String v2 = this.getPropValue(propFile, key);
        return v1.equals(v2);
    }
}
