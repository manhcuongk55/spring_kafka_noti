package vn.viettel.browser.service.iface;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import vn.viettel.browser.model.AndroidDeviceModel;
import vn.viettel.browser.model.IosDeviceModel;
import vn.viettel.browser.model.MapDeviceAccModel;
import vn.viettel.browser.model.WindowsDeviceModel;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.service.iface:Social_Login
 */
public interface DeviceServiceIface {

    long addNewDevice(String type);

    long checkExistedDevice(String typeDevice, JSONObject detailedDevice);

    long checkExistedAndroidDevice(JSONObject jsonObject);

    void addNewAndroidDevice(AndroidDeviceModel androidDeviceModel);

    AndroidDeviceModel createNewAndroidDevice(JSONObject jsonObject);

    void addNewIosDevice(IosDeviceModel iosDeviceModel);

    long checkExistedIosDevice(JSONObject jsonObject);

    IosDeviceModel createNewIosDevice(JSONObject jsonObject);

    void addNewWindowsDevice(WindowsDeviceModel windowsDeviceModel);

    int checkExistedWindowsDevice(JSONObject jsonObject);

    WindowsDeviceModel createNewWindowsDevice(JSONObject jsonObject);

    MapDeviceAccModel checkConnectDeviceAcc(long idDevice, String accountName);

    MapDeviceAccModel addNewConnectDeviceAcc(long idDevice, String accountName);

    long getIdUserFromDevice(JSONObject detailedDevice, String typeDevice);

    JSONArray findDeviceByAccountName(String accountName);

    long getDeviceIdFromDevice(JSONObject detailedDevice, String typeDevice);

    void updateLastLoginDeviceAcc(MapDeviceAccModel mapDeviceAccModel);

    void updateAccessToken(MapDeviceAccModel mapDeviceAccModel);
}
