package vn.viettel.browser.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.browser.dao.DeviceDAO;
import vn.viettel.browser.model.AccountModel;
import vn.viettel.browser.model.DeviceModel;
import vn.viettel.browser.model.MapDeviceAccModel;
import vn.viettel.browser.service.iface.DataServiceIface;
import vn.viettel.browser.ultils.HBaseUtility;
import vn.viettel.browser.ultils.HibernateUtility;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.service:Social_Login
 */
@Service
public class DataService implements DataServiceIface {
    private final
    DeviceService deviceService;
    private final
    UserService userService;
    private final
    AccountService accountService;
    private final
    LoginService loginService;

    private DeviceDAO deviceDAO = new DeviceDAO();

    @Autowired
    public DataService(DeviceService deviceService, AccountService accountService, LoginService loginService, UserService userService) {
        this.deviceService = deviceService;
        this.accountService = accountService;
        this.loginService = loginService;
        this.userService = userService;
    }


    @Override
    public JSONObject putData(String input) {
        JSONParser parser = new JSONParser();
        long userId;
        long deviceId;
        JSONObject jsonObject;
        ArrayList<String> selections;
        try {
            jsonObject = (JSONObject) parser.parse(input);
            selections = (ArrayList<String>) jsonObject.get("selections");
            String strDevice = jsonObject.get("device").toString();
            JSONObject device = (JSONObject) parser.parse(strDevice);
            String typeDevice = device.get("type_device").toString();
            JSONObject detailedDevice = (JSONObject) device.get("detail");
            userId = checkExistAccessToken(jsonObject);
            deviceId = deviceService.checkExistedDevice(typeDevice, detailedDevice);
        } catch (Exception e) {
            jsonObject = new JSONObject();
            e.printStackTrace();
            if(e.getMessage().equals("Wrong accessToken"))
            {
                jsonObject.put("status", "Wrong accessToken");
            }else {
                jsonObject.put("status", "Wrong format login's body");
            }

            jsonObject.put("code", 1);
            return jsonObject;
        }
        jsonObject = putDataByIdUserAndDeviceId(userId, deviceId, jsonObject, selections);
        return jsonObject;
    }

    private long checkExistAccessToken(JSONObject jsonObject) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        String accessToken = jsonObject.get("access_token").toString();
        String strDevice = jsonObject.get("device").toString();
        JSONObject device = (JSONObject) parser.parse(strDevice);
        String typeDevice = device.get("type_device").toString();
        JSONObject detailedDevice = (JSONObject) device.get("detail");
        long userId;
        if (!accessToken.equals("\"\"")) userId = userService.getIdFromAccessToken(accessToken);
        else {
            jsonObject.put("type", "");
            loginService.login(jsonObject.toJSONString());
            userId = deviceService.getIdUserFromDevice(detailedDevice, typeDevice);
        }
        if (userId == 0L) throw new IOException("Wrong accessToken");
        return userId;
    }

    @Override
    public JSONObject putData(String accessToken, String input) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(input);
            if(accessToken == null) accessToken = "";
            jsonObject.put("access_token", accessToken);
            return putData(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("status", "Wrong Format Login's Body Data");
            return jsonObject;
        }
    }


    @Override
    public JSONObject getHistory(String accessToken, String input) {
        long idUser, deviceId;
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = new JSONObject();
        long timeEnd;
        long duration;
        try {
            JSONObject jsonObject;
            jsonObject = (JSONObject) parser.parse(input);
            timeEnd = Long.valueOf(jsonObject.get("time_end").toString());
            duration = Long.valueOf(jsonObject.get("duration").toString());
            deviceId = Long.valueOf(jsonObject.get("device_id").toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return jsonResponse;
        }
        try {
            idUser = userService.getIdFromAccessToken(accessToken);
            if (idUser == 0L) {
                return jsonResponse;
            }
            HBaseUtility hBaseUtility = new HBaseUtility();
            jsonResponse = hBaseUtility.getHistoryFromHBase(idUser, deviceId, timeEnd, duration);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonResponse;
        }
        return jsonResponse;
    }

    @Override
    public JSONObject getData(String input) {
        JSONParser parser = new JSONParser();
        long idUser, deviceId;
        JSONObject jsonObject;
        ArrayList<String> selections;
        try {
            jsonObject = (JSONObject) parser.parse(input);
            String accessToken = jsonObject.get("access_token").toString();
            deviceId = Long.valueOf(jsonObject.get("device_id").toString());
            MapDeviceAccModel mapDeviceAccModel = accountService.findByAccessToken(accessToken);
            if (mapDeviceAccModel.getAccountName() == null)
                return null;
            AccountModel accountModel = accountService.findByName(mapDeviceAccModel.getAccountName());
            idUser = accountModel.getUserProfileId();
            if (idUser == 0L) {
                return null;
            }
            selections = (ArrayList<String>) jsonObject.get("selections");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        jsonObject = getDataFromIdUser(idUser, deviceId, selections);
        return jsonObject;
    }

    @Override
    public JSONObject getData(String accessToken, String input) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(input);
            jsonObject.put("access_token", accessToken);
            return getData(jsonObject.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject getDataFromIdUser(long userId, long deviceId, ArrayList<String> selections) {
        HBaseUtility utility = new HBaseUtility();
        return utility.getOtherStatus(userId, deviceId, selections);
    }

    @Override
    public JSONObject putDataByIdUserAndDeviceId(long idUser, long deviceId, JSONObject jsonObject,
                                                 ArrayList<String> selections) {
        String speedDial;
        String statusNews;
        JSONArray history;
        String bookmark;

        JSONObject response = new JSONObject();
        try {
            speedDial = jsonObject.get("speed_dial").toString();
            statusNews = jsonObject.get("status_news").toString();
            JSONObject list = (JSONObject) jsonObject.get("history");
            history = (JSONArray) list.get("list");
            bookmark = jsonObject.get("bookmark").toString();
            HBaseUtility hBaseUtility = new HBaseUtility();
            hBaseUtility.pushHistory(history, idUser, deviceId);
            if (selections.size() > 0)
                hBaseUtility.pushOtherStatus(speedDial, statusNews, bookmark, idUser, deviceId, selections);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 1);
            response.put("status", "Failed to put data");
            return response;
        }
        response.put("code", 0);
        response.put("status", "Success put data");
        return response;
    }


    @Override
    public JSONObject setAccessTokenFireBaseFirst(String input) {
        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(input);
            return setAccessTokenFireBase(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject response = new JSONObject();
        response.put("code", 1);
        response.put("status", "Failed");
        return response;
    }

    @Override
    public JSONObject setAccessTokenFireBaseFirst(String accessToken, String strInput) {
        JSONParser parser = new JSONParser();
        JSONObject input;
        try {
            input = (JSONObject) parser.parse(strInput);
            if(accessToken == null) accessToken = "";
            input.put("access_token", accessToken);
            return setAccessTokenFireBaseFirst(input.toJSONString());
        } catch (ParseException e) {
            e.printStackTrace();
            JSONObject response = new JSONObject();
            response.put("code", 1);
            response.put("status", "Failed");
            return response;
        }
    }

    private JSONObject setAccessTokenFireBase(JSONObject jsonObject) {
        String accessTokenFireBase = jsonObject.get("access_token_firebase").toString();
        JSONObject device = (JSONObject) jsonObject.get("device");
        String typeDevice = device.get("type_device").toString();
        JSONObject detailedDevice = (JSONObject) device.get("detail");

        try {
            checkExistAccessToken(jsonObject);
        } catch (ParseException | IOException e) {
            jsonObject = new JSONObject();
            jsonObject.put("code", 1);
            jsonObject.put("status", "Wrong Format Login's Body Data");
            return jsonObject;
        }
        long idDevice = deviceService.checkExistedDevice(typeDevice, detailedDevice);

        if (idDevice != 0) {
            SessionFactory factory = HibernateUtility.getSessionFactory();
            Session session = factory.getCurrentSession();
            try {
                if (!session.beginTransaction().isActive()) {
                    session.beginTransaction().begin();
                }
                DeviceModel deviceModel = new DeviceModel(idDevice, typeDevice, accessTokenFireBase);
                deviceDAO.updateDevice(session, deviceModel);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
        JSONObject response = new JSONObject();
        response.put("code", 0);
        response.put("status", "Success");
        return response;
    }

}
