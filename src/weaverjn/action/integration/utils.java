package weaverjn.action.integration;

/**
 * Created by zhaiyaqi on 2017/2/14.
 */
public class utils {
    public static String[] slice(String s, int range, int n) {
        String[] arr = new String[n];
        int len = s.length();
        int a = len / range;
        int b = len % range;
        int max = a > n ? n : a;
        for (int i = 0; i < max; i++) {
            arr[i] = s.substring(i * range, (i + 1) * range);
        }
        if (max < n) {
            arr[max] = s.substring(len - b, len);
            for (int i = max + 1; i < n; i++) {
                arr[i] = "";
            }
        }
        return arr;
    }

    public static void main(String[] args) {
        String s = "123456789qwerasd";
        String[] arr = slice(s, 5, 4);
        for (String i : arr) {
            System.out.println(i);
        }
    }
}
