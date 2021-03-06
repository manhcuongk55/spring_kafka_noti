package vn.viettel.browser.service;

import com.google.common.collect.Iterators;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import vn.viettel.browser.Application;
import vn.viettel.browser.config.ProductionConfig;
import vn.viettel.browser.ultils.HibernateUtils;
import vn.viettel.browser.ultils.JSONUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Service
public final class ProducerPutMessKafkaNotiService {

    public static <T> boolean hasDuplicate(Iterable<T> all) {
        Set<T> set = new HashSet<T>();
        // Set#add returns false if the set does not change, which
        // indicates that a duplicate element has been added.
        for (T each : all) if (!set.add(each)) return true;
        return false;
    }

    public static int getTotalDevicesByConfigCate(JSONObject inputSearch) throws JSONException {
        int total = 0;
        JSONObject input = new JSONObject();

        try {
            input = (JSONObject) Application.elasticsearchUtils.getListDeviceIdsFromAllCategories(inputSearch)
                    .get("data");
            System.out.println("input ............. " + input);
        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        Iterator<?> keys = input.keys();
        total = Iterators.size(keys);
        return total;

    }

    public static int getTotalDevicesByConfig(JSONObject inputSearch) throws JSONException {
        int total = 0;
        JSONObject input = new JSONObject();

        try {
            input = (JSONObject) Application.elasticsearchUtils.getListAllDevices(inputSearch).get("data");
            System.out.println("input ............. " + input);
        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        Iterator<?> keys = input.keys();
        total = Iterators.size(keys);
        return total;

    }

    private static JSONObject createInputSearch(String mess) throws JSONException {
        JSONObject messJson = new JSONObject(mess);

        // create JsonSearch
        JSONObject input = new JSONObject();
        String deviceType = "";
        String deviceVersion = "";

        // parse JsonInput from CMS
        String appVersion = "";
        String appIosVersion = "";
        String appAndroidVersion = "";
        String deviceAndroidVersion = "";
        String deviceIosVersion = "";

        if (messJson.has("deviceType")) {
            if (messJson.getString("deviceType").trim().contains("android,ios")) {
                deviceType = "*";
            } else if (messJson.getString("deviceType").trim().contains("android")) {
                deviceType = "android";
            } else {
                deviceType = "ios";
            }
        }
        if (messJson.getString("appAndroidVersion").trim().equals("[-1]")) {
            if (messJson.getString("appIosVersion").trim().equals("[-1]")) {
                appVersion = null;
            } else if (messJson.getString("appIosVersion").trim().equals("[0]")) {
                appVersion = "*";
            } else {
                appVersion = HibernateUtils.getVersionIosAppFromKeyInDB(messJson.getString("appIosVersion"));
            }

        } else if (messJson.getString("appAndroidVersion").trim().equals("[0]")) {
            appVersion = "*";
        } else {
            if (!messJson.getString("appIosVersion").trim().equals("[-1]")) {
                appIosVersion = HibernateUtils.getVersionIosAppFromKeyInDB(messJson.getString("appIosVersion"));
            }

            appAndroidVersion = HibernateUtils.getVersionAndroidAppFromKeyInDB(messJson.getString("appAndroidVersion"));
            if (!appAndroidVersion.isEmpty() && !appIosVersion.isEmpty()) {
                appVersion = appAndroidVersion + "," + appIosVersion;
            } else if (appAndroidVersion.isEmpty()) {
                appVersion = appIosVersion;
            } else {
                appVersion = appAndroidVersion;
            }
        }

        if (messJson.getString("deviceAndroidVersion").trim().equals("[-1]")) {
            if (messJson.getString("deviceIosVersion").trim().equals("[-1]")) {
                deviceVersion = null;
            } else if (messJson.getString("deviceIosVersion").trim().equals("[0]")) {
                deviceVersion = "*";
            } else {
                deviceVersion = HibernateUtils.getVersionIosDeviceFromKeyInDB(messJson.getString("deviceIosVersion"));
            }

        } else if (messJson.getString("deviceAndroidVersion").trim().equals("[0]")) {
            deviceVersion = "*";
        } else {
            if (!messJson.getString("deviceIosVersion").trim().equals("[-1]")) {
                deviceIosVersion = HibernateUtils.getVersionIosDeviceFromKeyInDB(messJson.getString("deviceIosVersion"));
            }

            deviceAndroidVersion = HibernateUtils.getVersionAndroidDeviceFromKeyInDB(messJson.getString("deviceAndroidVersion"));
            if (!deviceAndroidVersion.isEmpty() && !deviceIosVersion.isEmpty()) {
                deviceVersion = deviceAndroidVersion + "," + deviceIosVersion;
            } else if (deviceAndroidVersion.isEmpty()) {
                deviceVersion = deviceIosVersion;
            } else {
                deviceVersion = deviceAndroidVersion;
            }
        }
        if (appVersion != null) {
            input.put("appVersion", appVersion);
        }
        if (deviceVersion != null) {
            input.put("deviceVersion", deviceVersion);
        }
        input.put("deviceType", deviceType);
        return input;
    }

    public String SendMess(String mess, int typeMess) throws Exception {
        // Start Notification Producer Thread
        JSONObject info = new JSONObject();
        int totalCount = 0;
        JSONObject resultsAndroid = new JSONObject();
        JSONObject resultsIos = new JSONObject();
        String idJob = "";
        JSONObject messJson = new JSONObject();

        JSONObject results = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject notification = new JSONObject();
        messJson = new JSONObject(mess);
        JSONObject inputSearchListDevices = createInputSearch(mess);
        //System.out.println(inputSearchListDevices);
        //return "";
        String categoryId = "";
        if (messJson.has("category")) {
            categoryId = messJson.getString("category");
        }
        if (typeMess == 0) {

            resultsAndroid = JSONUtils.createJSonForAndroidNotification(mess);
            resultsIos = JSONUtils.createJSonForIosNotification(mess);
            messJson = new JSONObject(mess);
            idJob = messJson.getString("articleId");
            categoryId = "categoryId:" + messJson.getString("category");

            totalCount = Application.elasticsearchUtils.getTotalDeviceByCategoryId(inputSearchListDevices, categoryId);
            if (totalCount == 0) {
                info.put("mess", messJson);
                info.put("totalCount_devices", totalCount);
                return info.toString();
            }
            Application.jedisUtils.set("sent" + idJob, 0 + "");
            Application.jedisUtils.set("received" + idJob, 0 + "");
            Application.jedisUtils.set("sent_total" + idJob, 0 + "_" + totalCount);
            resultsIos.put("idJob", idJob);
            resultsIos.put("totalCount", totalCount);
            resultsAndroid.put("idJob", idJob);
            resultsAndroid.put("totalCount", totalCount);
            getNotiToListDeviceByCategoryId(inputSearchListDevices, categoryId, resultsIos, resultsAndroid);
        } else if (typeMess == 1) {
            resultsAndroid = JSONUtils.createJSonForAndroidNotification(mess);
            resultsIos = JSONUtils.createJSonForIosNotification(mess);
            messJson = new JSONObject(mess);
            idJob = messJson.getString("articleId");
            totalCount = Application.elasticsearchUtils.getTotalDevice(inputSearchListDevices);
            if (totalCount == 0) {
                info.put("mess", messJson);
                info.put("totalCount_devices", totalCount);
                return info.toString();
            }
            Application.jedisUtils.set("sent" + idJob, 0 + "");
            Application.jedisUtils.set("received" + idJob, 0 + "");
            Application.jedisUtils.set("sent_total" + idJob, 0 + "_" + totalCount);
            resultsIos.put("idJob", idJob);
            resultsIos.put("totalCount", totalCount);
            resultsAndroid.put("idJob", idJob);
            resultsAndroid.put("totalCount", totalCount);
            getAllDevicesToSendNotification(inputSearchListDevices, resultsIos, resultsAndroid);
        } else if (typeMess == 2) {
            totalCount = Application.elasticsearchUtils.getTotalDevice(inputSearchListDevices);
            if (totalCount == 0) {
                info.put("mess", messJson);
                info.put("totalCount_devices", totalCount);
                return info.toString();
            }
            messJson = new JSONObject(mess);
            data.put("content", messJson.getString("content"));
            data.put("title", messJson.getString("title"));
            data.put("type", messJson.getString("type"));
            idJob = messJson.getString("jobId");
            data.put("jobId", idJob);
            Application.jedisUtils.set("sent" + idJob, 0 + "");
            Application.jedisUtils.set("received" + idJob, 0 + "");
            Application.jedisUtils.set("sent_total" + idJob, 0 + "_" + totalCount);
            results.put("idJob", idJob);
            results.put("totalCount", totalCount);
            getListDeviceToSendMessByConfig(inputSearchListDevices, results, data, notification, messJson);
        } else if (typeMess == 3) {
            totalCount = Application.elasticsearchUtils.getTotalDevice(inputSearchListDevices);
            if (totalCount == 0) {
                info.put("mess", messJson);
                info.put("totalCount_devices", totalCount);
                return info.toString();
            }
            messJson = new JSONObject(mess);
            data.put("content", messJson.getString("content"));
            data.put("title", messJson.getString("title"));
            data.put("type", messJson.getString("type"));
            idJob = messJson.getString("jobId");
            data.put("jobId", idJob);
            Application.jedisUtils.set("sent" + idJob, 0 + "");
            Application.jedisUtils.set("received" + idJob, 0 + "");
            Application.jedisUtils.set("sent_total" + idJob, 0 + "_" + totalCount);
            results.put("idJob", idJob);
            results.put("totalCount", totalCount);
            //return inputSearchListDevices.toString();
            getAllDeviceToSendMessByConfig(inputSearchListDevices, results, data, notification, messJson);
        } else if (typeMess == 4) {
            resultsAndroid = JSONUtils.createJSonForAndroidNotification(mess);
            resultsIos = JSONUtils.createJSonForIosNotification(mess);
            messJson = new JSONObject(mess);
            idJob = messJson.getString("articleId");

            totalCount = ProductionConfig.TestFireBase.length;
            if (totalCount == 0) {
                info.put("mess", messJson);
                info.put("totalCount_devices", totalCount);
                return info.toString();
            }
            data.put("jobId", idJob);
            Application.jedisUtils.set("sent" + idJob, 0 + "");
            Application.jedisUtils.set("received" + idJob, 0 + "");
            Application.jedisUtils.set("sent_total" + idJob, 0 + "_" + totalCount);
            resultsIos.put("idJob", idJob);
            resultsIos.put("totalCount", totalCount);
            resultsAndroid.put("idJob", idJob);
            resultsAndroid.put("totalCount", totalCount);
            sendNotiToDeviceTest(ProductionConfig.TestFireBase, resultsIos, resultsAndroid);
        } else if (typeMess == 5) {
            String[] listDevices = new String[0];
            messJson = new JSONObject(mess);
            data.put("content", messJson.getString("content"));
            data.put("title", messJson.getString("title"));
            data.put("type", messJson.getString("type"));
            idJob = messJson.getString("jobId");
            data.put("jobId", idJob);
            String listTestDevices = HibernateUtils.getListTestDevices();
            if (listTestDevices.contains(",") && listTestDevices.length() > 1) {
                listDevices = listTestDevices.split(",");
            }

            Application.jedisUtils.set("sent" + idJob, 0 + "");
            Application.jedisUtils.set("received" + idJob, 0 + "");
            Application.jedisUtils.set("sent_total" + idJob, 0 + "_" + totalCount);
            results.put("idJob", idJob);
            results.put("totalCount", listDevices.length);

            getAllTestDevicesToSendMessageBox(listDevices, results, data, notification, messJson);
            //sendNotiToDeviceTest(ProductionConfig.TestFireBase, resultsIos, resultsAndroid);
        }
        info.put("mess", messJson);
        info.put("totalCount_devices", totalCount);
        return mess;
    }

    public void getNotiToListDeviceByCategoryId(JSONObject inputSearch, String categoryId, JSONObject resultsIos,
                                                JSONObject resultsAndroid) throws JSONException {
        JSONObject input = null;
        try {
            input = (JSONObject) Application.elasticsearchUtils.getListDeviceIdsFromAllCategories(inputSearch)
                    .get("data");
            System.out.println("input ............. " + input);
        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        Iterator<?> keys = input.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            JSONObject obj = null;
            try {
                obj = (JSONObject) input.get(key);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (obj.has(categoryId)) {
                System.out.println("==============>Send to: " +key);
                if (key.contains("ios")) {
                    key.replace("ios", "");
                    resultsIos.put("to", key);
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, resultsIos.toString());
                    Application.producer.send(record);
                } else {
                    resultsAndroid.put("to", key);
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, resultsAndroid.toString());
                    Application.producer.send(record);
                }

            }

        }

    }

    public void getAllDevicesToSendNotification(JSONObject inputSearch, JSONObject resultsIos,
                                                JSONObject resultsAndroid) throws Exception {
        JSONObject input = null;
        ArrayList<String> output = new ArrayList<>();
        //System.out.println(inputSearch);
        try {
            input = (JSONObject) Application.elasticsearchUtils.getListAllDevices(inputSearch).get("data");
            //System.out.println("input ............. " + input);
        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        Iterator<?> keys = input.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (key.contains("ios")) {
                key.replace("ios", "");
                resultsIos.put("to", key);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, resultsIos.toString());
                Application.producer.send(record);
            } else {
                resultsAndroid.put("to", key);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, resultsAndroid.toString());
                Application.producer.send(record);
            }

        }
        //boolean isDuplicated = hasDuplicate();
    }

    public void sendNotiToDeviceTest(String[] listDevices, JSONObject resultsIos, JSONObject resultsAndroid)
            throws Exception {
        String listTestDevices = HibernateUtils.getListTestDevices();
        if (listTestDevices.contains(",") && listTestDevices.length() > 1) {
            listDevices = listTestDevices.split(",");
        }
        for (int i = 0; i < listDevices.length; i++) {
            String key = listDevices[i];
            if (key.contains("ios")) {
                System.out.println("Send to: " + key);
                key.replace("ios", "");
                resultsIos.put("to", key);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, resultsIos.toString());
                try {
                    Application.producer.send(record);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Send to: " + key);
                resultsAndroid.put("to", key);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, resultsAndroid.toString());
                try {
                    Application.producer.send(record);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getListDeviceToSendMessByConfig(JSONObject inputSearch, JSONObject results, JSONObject data,
                                                JSONObject notification, JSONObject messJson) throws JSONException {
        JSONObject input = null;
        try {
            input = (JSONObject) Application.elasticsearchUtils.getListAllDevices(inputSearch).get("data");
            System.out.println("input ............. " + input);
        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        Iterator<?> keys = input.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (key.contains("ios")) {
                key.replace("ios", "");
                results.put("data", data);
                notification.put("body", messJson.getString("title"));
                // notification.put("badge",0);
                notification.put("sound", "default");
                results.put("notification", notification);
                results.put("mutable_content", true);
                results.put("to", key);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, results.toString());
                Application.producer.send(record);
            } else {
                results.put("data", data);
                results.put("to", key);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, results.toString());
                Application.producer.send(record);
            }

        }
    }

    public void getAllDeviceToSendMessByConfig(JSONObject inputSearch, JSONObject results, JSONObject data,
                                               JSONObject notification, JSONObject messJson) throws JSONException {
        JSONObject input = null;
        try {
            input = (JSONObject) Application.elasticsearchUtils.getListAllDevices(inputSearch).get("data");
        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        Iterator<?> keys = input.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (key.contains("ios")) {
                key.replace("ios", "");
                results.put("data", data);
                notification.put("body", messJson.getString("title"));
                // notification.put("badge",0);
                notification.put("sound", "default");
                results.put("notification", notification);
                results.put("mutable_content", true);
                results.put("to", key);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, results.toString());
                Application.producer.send(record);
            } else {
                results.put("data", data);
                results.put("to", key);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, results.toString());
                Application.producer.send(record);
            }


        }

    }

    public void getAllTestDevicesToSendMessageBox(String[] listDevices, JSONObject results1, JSONObject data,
                                               JSONObject notification, JSONObject messJson) throws JSONException {

        for (String key : listDevices) {
            JSONObject results  = new JSONObject();
            results.put("idJob", results1.get("idJob") != null ?results1.get("idJob"): "" );
            results.put("totalCount", results1.get("totalCount") != null ? results1.get("totalCount"): "");
            System.out.println(key);
            if (key.startsWith("ios")) {
                key.replace("ios", "");
                results.put("data", data);
                notification.put("body", messJson.getString("title"));
                // notification.put("badge",0);
                notification.put("sound", "default");
                results.put("notification", notification);
                results.put("mutable_content", true);
                results.put("to", key);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, results.toString());
                Application.producer.send(record);
            } else {
                results.put("data", data);
                results.put("to", key);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(Application.topic, results.toString());
                Application.producer.send(record);
            }


        }

    }
}
