package vn.viettel.browser.global;

import org.apache.hadoop.hbase.client.Table;

/**
 * Created by quytx on 3/31/2017.
 * Project: App.global:Social_Login
 */
public class Variables {
    // Url check token
    public static final String URL_GOOGLE = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=";
    public static final String URL_FACEBOOK = "https://graph.facebook.com/v2.6/me?fields=first_name,last_name&access_token=";

    // Hbase variable
    public static String nameHtable = "data_user";
    public static String nameFamily = "detail";
    public static String quorum_hbase = "zk01.vbrowser.vn,zk02.vbrowser.vn,zk03.vbrowser.vn";
    public static String quorum_hbase_local = "brwsr-03,brwsr-02,brwsr-01";
    public static Table table;
}
