package vn.viettel.browser.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.viettel.browser.dao.*;
import vn.viettel.browser.model.*;
import vn.viettel.browser.service.iface.DeviceServiceIface;
import vn.viettel.browser.ultils.HibernateUtility;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.service:Social_Login
 */
@Service
public class DeviceService implements DeviceServiceIface {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);
    private static final AtomicLong idDevice = new AtomicLong();
    private static DeviceDAO deviceDAO = new DeviceDAO();
    private static AccountDAO accountDAO = new AccountDAO();
    private static AndroidDeviceDAO androidDeviceDAO = new AndroidDeviceDAO();
    private static IosDeviceDAO iosDeviceDAO = new IosDeviceDAO();
    //    private static WindowsDeviceDAO windowsDeviceDAO = new WindowsDeviceDAO();
    private static MapDeviceAccDAO mapDeviceAccDAO = new MapDeviceAccDAO();


    @Override
    public AndroidDeviceModel createNewAndroidDevice(JSONObject jsonObject) {
        String serialNumber = jsonObject.get("serial_number").toString();
        String macWifi = jsonObject.get("mac_wifi").toString();
        String deviceName = jsonObject.get("device_name").toString();
        String bootLoader = jsonObject.get("bootloader").toString();
        String display = jsonObject.get("display").toString();
        String hardware = jsonObject.get("hardware").toString();
        String manufacturer = jsonObject.get("manufacturer").toString();
        String model = jsonObject.get("model").toString();
        String product = jsonObject.get("product").toString();
        String user = jsonObject.get("user").toString();
        String OSName = jsonObject.get("os_name").toString();
        String OSVersion = jsonObject.get("os_version").toString();
        AndroidDeviceModel androidDeviceModel = new AndroidDeviceModel(serialNumber, macWifi, deviceName, bootLoader, display,
                hardware, manufacturer, model, product, user, OSName, OSVersion, 0L);
        logger.info("Create new Android Device: " + androidDeviceModel);
        return androidDeviceModel;
    }

    @Override
    public long addNewDevice(String type) {
        DeviceModel deviceModel = new DeviceModel(idDevice.incrementAndGet(), type);
        logger.info("Added new Device: " + deviceModel);
        SessionFactory factory = HibernateUtility.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            deviceDAO.addNewDevice(session, deviceModel);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            return 0L;
        } finally {
            session.close();
        }
        return deviceModel.getId();
    }

    @Override
    public long checkExistedDevice(String typeDevice, JSONObject detailedDevice) {
        switch (typeDevice) {
            case "android":
                return checkExistedAndroidDevice(detailedDevice);
            case "ios":
                return checkExistedIosDevice(detailedDevice);
            case "windows":
                return checkExistedWindowsDevice(detailedDevice);
        }
        return 0L;
    }

    @Override
    public long checkExistedAndroidDevice(JSONObject jsonObject) {
        String serialNumber = jsonObject.get("serial_number").toString();
        String macWifi = jsonObject.get("mac_wifi").toString();
        AndroidDeviceModel androidDeviceModel;
        SessionFactory factory = HibernateUtility.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            androidDeviceModel = androidDeviceDAO.getAndroidDeviceBySerial(session, serialNumber);
            if (androidDeviceModel.getSerialNumber() != null)
                return androidDeviceModel.getidDevice();
            androidDeviceModel = androidDeviceDAO.getAndroidDeviceByMacWifi(session, macWifi);
            if (androidDeviceModel.getSerialNumber() != null)
                return androidDeviceModel.getidDevice();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            return 0L;
        } finally {
            session.close();
        }
        return 0L;
    }

    @Override
    public long checkExistedIosDevice(JSONObject jsonObject) {
        String identifierForVendor = jsonObject.get("identifierForVendor").toString();
        IosDeviceModel iosDeviceModel;
        SessionFactory factory = HibernateUtility.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            iosDeviceModel = iosDeviceDAO.getIosDeviceByID(session, identifierForVendor);
            if (iosDeviceModel.getIdentifierForVendor() != null)
                return iosDeviceModel.getIdDevice();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            return 0L;
        } finally {
            session.close();
        }
        return 0L;
    }

    @Override
    public IosDeviceModel createNewIosDevice(JSONObject jsonObject) {
        String identifierForVendor = jsonObject.get("identifierForVendor").toString();
        String name = jsonObject.get("name").toString();
        String model = jsonObject.get("model").toString();
        String systemName = jsonObject.get("systemName").toString();
        String systemVersion = jsonObject.get("systemVersion").toString();
        IosDeviceModel iosDeviceModel = new IosDeviceModel(identifierForVendor, name, model,
                systemName, systemVersion, 0L);
        logger.info("Create new IOS Device: " + iosDeviceModel);
        return iosDeviceModel;
    }

    @Override
    public int checkExistedWindowsDevice(JSONObject jsonObject) {
        return 0;
    }

    @Override
    public WindowsDeviceModel createNewWindowsDevice(JSONObject jsonObject) {
        return null;
    }

    @Override
    public void addNewAndroidDevice(AndroidDeviceModel androidDeviceModel) {
        long idDevice = addNewDevice("android");
        androidDeviceModel.setidDevice(idDevice);
        logger.info("Added new Android Device: " + androidDeviceModel);

        SessionFactory factory = HibernateUtility.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            // Android Device
            androidDeviceDAO.addDevice(session, androidDeviceModel);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    @Override
    public void addNewIosDevice(IosDeviceModel iosDeviceModel) {
        long idDevice = addNewDevice("ios");
        iosDeviceModel.setIdDevice(idDevice);
        logger.info("Added new IOS Device: " + iosDeviceModel);
        SessionFactory factory = HibernateUtility.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            // IOS Device
            iosDeviceDAO.addDevice(session, iosDeviceModel);
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void addNewWindowsDevice(WindowsDeviceModel windowsDeviceModel) {

    }

    @Override
    public MapDeviceAccModel checkConnectDeviceAcc(long idDevice, String accountName) {
        MapDeviceAccModel mapDeviceAccModel = new MapDeviceAccModel();
        MapDeviceAccDAO mapDeviceAccDAO = new MapDeviceAccDAO();
        logger.info("checkConnectDeviceAcc: idDevice = " + idDevice + ", accountName = " + accountName);
        SessionFactory factory = HibernateUtility.getSessionFactory();
        try (Session session = factory.getCurrentSession()) {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            mapDeviceAccModel = mapDeviceAccDAO.checkExistedMapDeviceAcc(session, idDevice, accountName);
            if (mapDeviceAccModel.getIdDevice() != 0) {
                session.close();
                return mapDeviceAccModel;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapDeviceAccModel;
    }

    @Override
    public MapDeviceAccModel addNewConnectDeviceAcc(long idDevice, String accountName) {
        long timeNow = new Date().getTime() / 1000;
        MapDeviceAccModel mapDeviceAccModel = new MapDeviceAccModel(idDevice, accountName, timeNow, String.valueOf(new Date().getTime()));
        MapDeviceAccDAO mapDeviceAccDAO = new MapDeviceAccDAO();
        logger.info("addNewConnectDeviceAcc: idDevice = " + idDevice + ", accountName = " + accountName);
        SessionFactory factory = HibernateUtility.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            mapDeviceAccDAO.addMapDeviceAcc(session, mapDeviceAccModel);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return mapDeviceAccModel;
    }

    @Override
    public JSONArray findDeviceByAccountName(String accountName) {
        JSONArray listDevices = new JSONArray();
        List<MapDeviceAccModel> mapDeviceAccModelArrayList;
        SessionFactory factory = HibernateUtility.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            mapDeviceAccModelArrayList = mapDeviceAccDAO.findDevicesByAccountName(session, accountName);
            for (MapDeviceAccModel mapDeviceAccModel : mapDeviceAccModelArrayList) {
                Long deviceId = mapDeviceAccModel.getIdDevice();
                Long lastLogin = mapDeviceAccModel.getLastLogin();
                DeviceModel deviceModel = deviceDAO.findDeviceById(session, deviceId);
                JSONObject infoDevice = getInfoDevice(session, deviceModel);
                infoDevice.put("last_login", lastLogin);
                listDevices.add(infoDevice);
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }

        return listDevices;
    }

    @Override
    public long getDeviceIdFromDevice(JSONObject detailedDevice, String typeDevice) {
        return checkExistedDevice(typeDevice, detailedDevice);
    }

    @Override
    public void updateLastLoginDeviceAcc(MapDeviceAccModel mapDeviceAccModel) {
        long timeNow = new Date().getTime() / 1000;
        mapDeviceAccModel.setLastLogin(timeNow);
        SessionFactory factory = HibernateUtility.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            mapDeviceAccDAO.updateLastLoginMapDeviceAcc(session, mapDeviceAccModel);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    @Override
    public void updateAccessToken(MapDeviceAccModel mapDeviceAccModel) {
        SessionFactory factory = HibernateUtility.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            mapDeviceAccDAO.updateAccessTokenMapDeviceAcc(session, mapDeviceAccModel);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    @Override
    public long getIdUserFromDevice(JSONObject detailedDevice, String typeDevice) {
        long idDevice;
        long idUser;
        idDevice = checkExistedDevice(typeDevice, detailedDevice);
        SessionFactory factory = HibernateUtility.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            MapDeviceAccModel mapDeviceAccModel = mapDeviceAccDAO.findAccountNameFromIdDevice(session, idDevice);
            String accountName = mapDeviceAccModel.getAccountName();
            AccountModel accountModel = accountDAO.findByName(session, accountName);
            idUser = accountModel.getUserProfileId();
            return idUser;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return 0L;
        } finally {
            session.close();
        }
    }

    private JSONObject getInfoDevice(Session session, DeviceModel deviceModel) {
        JSONObject infoDevice = new JSONObject();
        String typeDevice = deviceModel.getType();
        Long deviceId = deviceModel.getId();
        switch (typeDevice) {
            case "android": {
                AndroidDeviceModel androidDeviceModel = androidDeviceDAO.getAndroidDeviceByDeviceID(session, deviceId);
                infoDevice.put("device_id", deviceId);
                infoDevice.put("name", androidDeviceModel.getDeviceName());
                infoDevice.put("os_name", androidDeviceModel.getOsName());
                infoDevice.put("os_version", androidDeviceModel.getOsVersion());
            }
            break;
            case "ios": {
                IosDeviceModel iosDeviceModel = iosDeviceDAO.getIosDeviceByDeviceID(session, deviceId);
                infoDevice.put("device_id", deviceId);
                infoDevice.put("name", iosDeviceModel.getModel());
                infoDevice.put("os_name", iosDeviceModel.getSystemName());
                infoDevice.put("os_version", iosDeviceModel.getSystemVersion());

            }
            break;
            case "windows": {
//                WindowsDeviceModel windowsDeviceModel = WindowsDeviceDAO.get
            }
            break;
            default:
        }

        return infoDevice;
    }
}
