package vn.viettel.browser.dao.iface;

import org.hibernate.Session;
import vn.viettel.browser.model.MapDeviceAccModel;

import java.util.List;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.dao.iface:Social_Login
 */
public interface MapDeviceAccountDAOIface {
    MapDeviceAccModel checkExistedMapDeviceAcc(Session session, long idDevice, String accountName);

    void addMapDeviceAcc(Session session, MapDeviceAccModel map);

    MapDeviceAccModel findAccountNameFromIdDevice(Session session, long idDevice);

    List<MapDeviceAccModel> findDevicesByAccountName(Session session, String accountName);

    void updateLastLoginMapDeviceAcc(Session session, MapDeviceAccModel mapDeviceAccModel);

    MapDeviceAccModel findByAccessToken(Session session, String accessToken);

    void updateAccessTokenMapDeviceAcc(Session session, MapDeviceAccModel mapDeviceAccModel);
}
