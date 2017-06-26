package vn.viettel.browser.service.iface;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.service.iface:Social_Login
 */
public interface DataServiceIface {
//    long getIdFromAccessToken(String accessToken);

    JSONObject getData(String input);

    JSONObject getData(String accessToken, String input);

    JSONObject putData(String input);

    JSONObject putData(String accessToken, String input);

    JSONObject putDataByIdUserAndDeviceId(long idUser, long DeviceId, JSONObject jsonObject, ArrayList<String> selections);

    JSONObject getHistory(String accessToken, String input);

    JSONObject setAccessTokenFireBaseFirst(String accessToken, String input);

    JSONObject setAccessTokenFireBaseFirst(String input);

}
