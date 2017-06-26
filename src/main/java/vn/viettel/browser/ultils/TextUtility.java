package vn.viettel.browser.ultils;

/**
 * Created by quytx on 3/31/2017.
 * Project: App.ultils:Social_Login
 */
public class TextUtility {
    public static String concat_strings(String[] input) {
        String result = "";
        for (int i = 0; i < input.length; i++) {
            if (i < input.length - 1) {
                result += "\"" + input[i] + "\",";
            } else {
                result += "\"" + input[i] + "\"";
            }
        }
        return result;
    }
}

