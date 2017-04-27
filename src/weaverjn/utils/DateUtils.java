package weaverjn.utils;

import com.weaver.general.BaseBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaiyaqi on 2017/4/25.
 */
public class DateUtils extends BaseBean {
    public static String Date2Str(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static int dateDifDays(String dateStr1, String dateStr2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int difDays = 0;
        try {
            Date date1 = simpleDateFormat.parse(dateStr1);
            Date date2 = simpleDateFormat.parse(dateStr2);

            long l = Math.abs(date1.getTime() - date2.getTime()) / (1000 * 3600 * 24);
            difDays = Integer.parseInt(String.valueOf(l));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return difDays;
    }
}
