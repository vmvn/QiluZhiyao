package weaverjn.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by zhaiyaqi on 2017/4/26.
 */
public class util {
    public static void writeLog(String var1, Object var2) {
        Log var3 = LogFactory.getLog(var1);
        if(var2 instanceof Exception) {
            var3.error(var1, (Exception)var2);
        } else {
            var3.error(var2);
        }
    }
}
