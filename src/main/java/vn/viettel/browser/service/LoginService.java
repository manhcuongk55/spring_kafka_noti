package vn.viettel.browser.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.browser.dao.AccountDAO;
import vn.viettel.browser.dao.UserDAO;
import vn.viettel.browser.exception.DataNotFoundException;
import vn.viettel.browser.global.Variables;
import vn.viettel.browser.model.*;
import vn.viettel.browser.service.iface.LoginServiceIface;
import vn.viettel.browser.ultils.HibernateUtils;
import vn.viettel.browser.ultils.HttpConnection;
import vn.viettel.browser.ultils.MD5;
import vn.viettel.browser.ultils.VerifyIdToken;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.service:Social_Login
 */
@Service
public class LoginService implements LoginServiceIface {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
    private static final AtomicLong idUser = new AtomicLong();
    private final DeviceService deviceService;
    private final UserService userService;
    private final AccountService accountService;
    private AccountDAO accountDAO = new AccountDAO();
    private UserDAO userDAO = new UserDAO();

    @Autowired
    public LoginService(DeviceService deviceService, UserService userService, AccountService accountService) {
        this.deviceService = deviceService;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public JSONObject login(String strInput) {
        int noLogin = 0;
        JSONObject responseLogin = new JSONObject();
        String AutoGenAccessToken = "";
        JSONParser parser = new JSONParser();
        JSONObject input;
        JSONObject device;
        String strDevice;
        try {
            input = (JSONObject) parser.parse(strInput);
            strDevice = input.get("device").toString();
            device = (JSONObject) parser.parse(strDevice);
        } catch (ParseException e) {
            e.printStackTrace();
            responseLogin.put("access_token", "Failed");
            return responseLogin;
        }
        String accessToken = input.get("access_token").toString();
        String type = input.get("type").toString();

        AccountModel accountModel = new AccountModel();
        try {
            String accountName;
            String typeDevice = device.get("type_device").toString();
            JSONObject detailedDevice = (JSONObject) device.get("detail");
            long time = new Date().getTime() / 1000;
            logger.info("Start login " + new Date().toString());
            switch (type) {
                case "google":
                    accountModel = loginByGoogle(accessToken);
                    break;
                case "facebook":
                    accountModel = loginByFacebook(accessToken);
                    break;
                default:
                    noLogin = 1;
                    break;
            }

            logger.info("Done login " + new Date().toString() + ": " + (new Date().getTime() / 1000 - time));
            if (noLogin == 0) {

                long unixCurrentTime = new Date().getTime() / 1000;
                SessionFactory factory = HibernateUtils.getSessionFactory();
                Session session = factory.getCurrentSession();
                try {
                    if (!session.beginTransaction().isActive()) {
                        session.beginTransaction().begin();
                    }
                    accountService.updateLastLoginAccount(session, accountModel, unixCurrentTime);
                    session.getTransaction().commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    session.getTransaction().rollback();
                } finally {
                    session.close();
                }

                accountName = accountModel.getAccountName();
//                idUser = accountModel.getUserProfileId();
                if (accountModel.getAccountName() == null) {
                    responseLogin.put("access_token", "Failed");
                    return responseLogin;
                }
                JSONArray listDevices = deviceService.findDeviceByAccountName(accountName);
                responseLogin.put("devices", listDevices);
                MapDeviceAccModel mapDeviceAccModel = new MapDeviceAccModel();
                switch (typeDevice) {
                    case "android":
                        mapDeviceAccModel = loginByAndroidDevice(detailedDevice, accountName);
                        break;
                    case "ios":
                        mapDeviceAccModel = loginByIosDevice(detailedDevice, accountName);
                        break;
                    case "windows":
                        mapDeviceAccModel = loginByWindowsDevice(detailedDevice, accountName);
                        break;
                }
                AutoGenAccessToken = mapDeviceAccModel.getAccessToken();
                logger.info("Done Login by Device " + new Date().toString() + " : " + (new Date().getTime() / 1000 - unixCurrentTime));
            } else {
                switch (typeDevice) {
                    case "android":
                        noLoginByAndroidDevice(detailedDevice);
                        break;
                    case "ios":
                        noLoginByIosDevice(detailedDevice);
                        break;
                    case "windows":
                        noLoginByWindowsDevice(detailedDevice);
                        break;
                }
            }

        } catch (Exception e) {
            logger.error("Failed");
            e.printStackTrace();
            responseLogin.put("access_token", "Failed");
            return responseLogin;
        }
        responseLogin.put("access_token", AutoGenAccessToken);
        return responseLogin;
    }

    @Override
    public AccountModel loginByGoogle(String accessToken) {
        AccountModel accountResponse = new AccountModel();
        AccountModel accountUpdate = new AccountModel();
        UserModel userUpdate;
//        String url = Variables.URL_GOOGLE + accessToken;
        try {
            VerifyIdToken verifyIdToken = new VerifyIdToken();
            JSONObject json = verifyIdToken.verifyIdToken(accessToken);
            long time = new Date().getTime() / 1000;
            if (json != null) {
                accountUpdate.setAccountName(json.get("id").toString());
                accountUpdate.setLastLogin(time);
                SessionFactory factory = HibernateUtils.getSessionFactory();
                Session session = factory.getCurrentSession();
                try {
                    if (!session.beginTransaction().isActive()) {
                        session.beginTransaction().begin();
                    }
                    accountResponse = accountDAO.findByName(session, json.get("id").toString());
                    if (accountResponse.getAccountName() == null) {
                        userUpdate = userService.createNewUserByGmail(json, idUser);
                        userDAO.addUser(session, userUpdate);
                        long user_profile_id = userUpdate.getId();
                        accountUpdate.setUserProfileId(user_profile_id);
                        accountDAO.addAccount(session, accountUpdate);
                        accountResponse = accountUpdate;
                    } else {
                        accountDAO.updateAccount(session, accountResponse);
                        session.getTransaction().commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    session.getTransaction().rollback();
                } finally {
                    session.close();
                }
            } else {
                throw new DataNotFoundException("user not found");
            }
        } catch (Exception e) {
            throw new DataNotFoundException("user not found");
        }
        return accountResponse;
    }

    @Override
    public AccountModel loginByFacebook(String accessToken) {
        AccountModel accountResponse = new AccountModel();
        AccountModel accountUpdate = new AccountModel();
        UserModel userUpdate;
        String url = Variables.URL_FACEBOOK + accessToken;
        try {
            String response = HttpConnection.sendGet(url); // Call get method
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            long time = new Date().getTime() / 1000;
            if (json != null) {
                accountUpdate.setAccountName(json.get("id").toString());
                accountUpdate.setLastLogin(time);
                SessionFactory factory = HibernateUtils.getSessionFactory();
                Session session = factory.getCurrentSession();
                try {
                    if (!session.beginTransaction().isActive()) {
                        session.beginTransaction().begin();
                    }
                    accountResponse = accountDAO.findByName(session, json.get("id").toString());
                    if (accountResponse.getAccountName() == null) {
                        userUpdate = userService.createNewUserByFacebook(json, idUser);
                        userDAO.addUser(session, userUpdate);
                        long user_profile_id = userUpdate.getId();
                        accountUpdate.setUserProfileId(user_profile_id);
                        accountDAO.addAccount(session, accountUpdate);
                        session.getTransaction().commit();
                        accountResponse = accountUpdate;
                    } else {
                        accountDAO.updateAccount(session, accountResponse);
                        session.getTransaction().commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    session.getTransaction().rollback();
                } finally {
                    session.close();
                }
            } else {
                throw new DataNotFoundException("user not found");
            }
        } catch (Exception e) {
            throw new DataNotFoundException("user not found");
        }
        return accountResponse;
    }

    private MapDeviceAccModel generateAccessToken(MapDeviceAccModel mapDeviceAccModel) {
        long timeNow = new Date().getTime();
        long deviceId = mapDeviceAccModel.getIdDevice();
        String accountName = mapDeviceAccModel.getAccountName();
        MD5 md5 = new MD5();
        String hashTime = "SFive.";
        hashTime += md5.getMD5("SFiveBrowser");
        hashTime += "." + md5.getMD5(String.valueOf(timeNow));
        hashTime += "." + md5.getMD5(String.valueOf(deviceId));
        hashTime += md5.getMD5(accountName);
        mapDeviceAccModel.setAccessToken(hashTime);
        deviceService.updateAccessToken(mapDeviceAccModel);
        return mapDeviceAccModel;
    }

    @Override
    public MapDeviceAccModel loginByAndroidDevice(JSONObject detailedDevice, String accountName) {
        long deviceId;
        AndroidDeviceModel androidDeviceModel;
        deviceId = deviceService.checkExistedAndroidDevice(detailedDevice);
        if (deviceId == 0L) {
            androidDeviceModel = deviceService.createNewAndroidDevice(detailedDevice);
            deviceService.addNewAndroidDevice(androidDeviceModel);
            deviceId = androidDeviceModel.getidDevice();
        }
        MapDeviceAccModel mapDeviceAccModel = deviceService.checkConnectDeviceAcc(deviceId, accountName);
        if (mapDeviceAccModel.getAccountName() == null) {
            mapDeviceAccModel = deviceService.addNewConnectDeviceAcc(deviceId, accountName);
        } else {
            deviceService.updateLastLoginDeviceAcc(mapDeviceAccModel);
        }
        mapDeviceAccModel = generateAccessToken(mapDeviceAccModel);
        return mapDeviceAccModel;
    }

    @Override
    public MapDeviceAccModel loginByIosDevice(JSONObject detailedDevice, String accountName) {
        long deviceId;
        IosDeviceModel iosDeviceModel;
        deviceId = deviceService.checkExistedIosDevice(detailedDevice);
        if (deviceId == 0L) {
            iosDeviceModel = deviceService.createNewIosDevice(detailedDevice);
            deviceService.addNewIosDevice(iosDeviceModel);
            deviceId = iosDeviceModel.getIdDevice();
        }
        MapDeviceAccModel mapDeviceAccModel = deviceService.checkConnectDeviceAcc(deviceId, accountName);
        if (mapDeviceAccModel.getAccountName() == null) {
            mapDeviceAccModel = deviceService.addNewConnectDeviceAcc(deviceId, accountName);
        } else {
            deviceService.updateLastLoginDeviceAcc(mapDeviceAccModel);
        }
        mapDeviceAccModel = generateAccessToken(mapDeviceAccModel);
        deviceService.updateAccessToken(mapDeviceAccModel);
        return mapDeviceAccModel;
    }

    @Override
    public MapDeviceAccModel loginByWindowsDevice(JSONObject detailedDevice, String accountName) {
//        long idDevice = 0L;
        MapDeviceAccModel mapDeviceAccModel = new MapDeviceAccModel();
//        WindowsDeviceModel windowsDeviceModel;
//        idDevice = deviceService.checkExistedWindowsDevice(detailedDevice);
//        if( idDevice == 0L){
//            windowsDeviceModel = deviceService.createNewWindowsDevice(detailedDevice);
//            deviceService.addNewWindowsDevice(windowsDeviceModel);
//            idDevice = windowsDeviceModel.getIdDevice();
//        }
//        if(!deviceService.checkConnectDeviceAcc(idDevice,accountName)){
//            deviceService.addNewConnectDeviceAcc(idDevice,accountName);
//        }
        mapDeviceAccModel = generateAccessToken(mapDeviceAccModel);
        return mapDeviceAccModel;
    }

    private void noLogin(JSONObject detailedDevice, String accountName, Long deviceId) {
        AccountModel accountUpdate = new AccountModel();
        accountUpdate.setAccountName(accountName);
        UserModel userUpdate = userService.createNewUserByDevice(detailedDevice, idUser, deviceId);
        accountService.createNewAccountbyDevice(accountUpdate, userUpdate, deviceId, accountName);
    }

    @Override
    public long noLoginByAndroidDevice(JSONObject detailedDevice) {
        AndroidDeviceModel androidDeviceModel;
        long deviceId = deviceService.checkExistedAndroidDevice(detailedDevice);
        if (deviceId != 0L) {
            String accountName = "GuestVT-" + deviceId;
            AccountModel accountModel = accountService.findByName(accountName);
            if (accountModel.getAccountName() == null) {
                noLogin(detailedDevice, accountName, deviceId);
            }
        }
        if (deviceId == 0L) {
            androidDeviceModel = deviceService.createNewAndroidDevice(detailedDevice);
            deviceService.addNewAndroidDevice(androidDeviceModel);
            deviceId = androidDeviceModel.getidDevice();
            String accountName = "GuestVT-" + deviceId;
            noLogin(detailedDevice, accountName, deviceId);
        }
        return 0;
    }

    @Override
    public long noLoginByIosDevice(JSONObject detailedDevice) {
        IosDeviceModel iosDeviceModel;
        long deviceId = deviceService.checkExistedIosDevice(detailedDevice);
        if (deviceId != 0L) {
            String accountName = "GuestVT-" + deviceId;
            AccountModel accountModel = accountService.findByName(accountName);
            if (accountModel.getAccountName() == null) {
                noLogin(detailedDevice, accountName, deviceId);
            }
        } else {
            iosDeviceModel = deviceService.createNewIosDevice(detailedDevice);
            deviceService.addNewIosDevice(iosDeviceModel);
            deviceId = iosDeviceModel.getIdDevice();
            String accountName = "GuestVT-" + deviceId;
            noLogin(detailedDevice, accountName, deviceId);
        }
        return 0;
    }

    @Override
    public long noLoginByWindowsDevice(JSONObject detailedDevice) {
        return 0;
    }


//    @Override
//    public String login(String access_token, String strInput) {
////        JSONParser parser = new JSONParser();
////        JSONObject input;
////        try {
////            input = (JSONObject) parser.parse(strInput);
////            input.put("access_token", access_token);
////            return login(input.toJSONString());
////        } catch (ParseException e) {
////            e.printStackTrace();
////            return "Failed";
////        }
//    }

//    @Override
//    public JSONObject setAccessTokenFireBase(String accessToken, String strInput) {
//        JSONParser parser = new JSONParser();
//        JSONObject input;
//        try {
//            input = (JSONObject) parser.parse(strInput);
//            input.put("access_token", accessToken);
//            return setAccessTokenFireBase(input.toJSONString());
//        } catch (ParseException e) {
//            e.printStackTrace();
//            JSONObject response = new JSONObject();
//            response.put("code",1);
//            response.put("status","Failed");
//            return response;
//        }
//    }
}
