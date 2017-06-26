package vn.viettel.browser.service.iface;

import org.json.simple.JSONObject;
import vn.viettel.browser.model.UserModel;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.service.iface:Social_Login
 */
public interface UserServiceIface {
    UserModel createNewUserByGmail(JSONObject json, AtomicLong idUser);

    UserModel createNewUserByFacebook(JSONObject json, AtomicLong idUser);

    UserModel createNewUserByMobile(JSONObject json, AtomicLong idUser);

    UserModel createNewUserByDevice(JSONObject json, AtomicLong idUser, long idDevice);

    long getIdFromAccessToken(String accessToken);
}
