package vn.viettel.browser.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.viettel.browser.model.DeviceModel;

import javax.persistence.TypedQuery;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.dao:Social_Login
 */
public class DeviceDAO {

    private final static Logger logger = LoggerFactory.getLogger(DeviceDAO.class);

    public void addNewDevice(Session session, DeviceModel deviceModel) {
        session.save(deviceModel);
        logger.info("Device added successfully, User Details=" + deviceModel);
    }

    public void updateDevice(Session session, DeviceModel deviceModel) {
        session.update(deviceModel);
        logger.info("Device updated successfully, User Details=" + deviceModel);
    }

    public DeviceModel findDeviceById(Session session, Long deviceId) {
        DeviceModel deviceModel = new DeviceModel();
        try {
            TypedQuery<DeviceModel> query = (TypedQuery<DeviceModel>) session.createQuery(
                    "from DeviceModel where id= :deviceId ");
            query.setParameter("deviceId", deviceId);
            deviceModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceModel;
    }
}

