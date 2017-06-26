package vn.viettel.browser.service.iface;

import org.json.simple.JSONObject;
import vn.viettel.browser.model.AccountModel;
import vn.viettel.browser.model.MapDeviceAccModel;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.service.iface:Social_Login
 */
public interface LoginServiceIface {
    JSONObject login(String input);

//    public String login(String access_token, String trim);

    AccountModel loginByGoogle(String accessToken);

    AccountModel loginByFacebook(String accessToken);

    MapDeviceAccModel loginByAndroidDevice(JSONObject detailedDevice, String accountName);

    MapDeviceAccModel loginByIosDevice(JSONObject detailedDevice, String accountName);

    MapDeviceAccModel loginByWindowsDevice(JSONObject detailedDevice, String accountName);

    long noLoginByAndroidDevice(JSONObject detailedDevice);

    long noLoginByIosDevice(JSONObject detailedDevice);

    long noLoginByWindowsDevice(JSONObject detailedDevice);

}
