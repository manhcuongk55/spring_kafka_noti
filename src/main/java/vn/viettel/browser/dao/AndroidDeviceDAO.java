package vn.viettel.browser.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import vn.viettel.browser.model.AndroidDeviceModel;

import javax.persistence.TypedQuery;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.dao:Social_Login
 */
@Repository
public class AndroidDeviceDAO {
    private static final Logger logger = LoggerFactory.getLogger(AndroidDeviceDAO.class);

    public void addDevice(Session session, AndroidDeviceModel androidDeviceModel) {
        session.save(androidDeviceModel);
        logger.info("AndroidDevice added successfully, User Details=" + androidDeviceModel);
    }

    public void updateDevice(Session session, AndroidDeviceModel androidDeviceModel) {

    }

    public AndroidDeviceModel getAndroidDeviceByDeviceID(Session session, long id) {
        AndroidDeviceModel androidDeviceModel = new AndroidDeviceModel();
        try {
            TypedQuery<AndroidDeviceModel> query = (TypedQuery<AndroidDeviceModel>) session.createQuery
                    ("from AndroidDeviceModel where deviceId= :id");
            query.setParameter("id", id);
            androidDeviceModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidDeviceModel;
    }

    public AndroidDeviceModel getAndroidDeviceBySerial(Session session, String serial) {
        AndroidDeviceModel androidDeviceModel = new AndroidDeviceModel();
        try {
            TypedQuery<AndroidDeviceModel> query = (TypedQuery<AndroidDeviceModel>) session.createQuery
                    ("from AndroidDeviceModel where serialNumber= :serial");
            query.setParameter("serial", serial);
            androidDeviceModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidDeviceModel;
    }

    public AndroidDeviceModel getAndroidDeviceByMacWifi(Session session, String macWifi) {
        AndroidDeviceModel androidDeviceModel = new AndroidDeviceModel();
        try {
            TypedQuery<AndroidDeviceModel> query = (TypedQuery<AndroidDeviceModel>) session.createQuery
                    ("from AndroidDeviceModel where macWifi= :macWifi");
            query.setParameter("macWifi", macWifi);
            androidDeviceModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidDeviceModel;
    }
}
