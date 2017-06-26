package vn.viettel.browser.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import vn.viettel.browser.model.IosDeviceModel;

import javax.persistence.TypedQuery;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.dao.iface:Social_Login
 */
@Repository
public class IosDeviceDAO {
    private static final Logger logger = LoggerFactory.getLogger(IosDeviceDAO.class);

    public void addDevice(Session session, IosDeviceModel iosDeviceModel) {
        session.save(iosDeviceModel);
        logger.info("iosDeviceModel added successfully, User Details=" + iosDeviceModel);

    }

    public IosDeviceModel getIosDeviceByID(Session session, String identifierForVendor) {
        IosDeviceModel iosDeviceModel = new IosDeviceModel();
        try {
            TypedQuery<IosDeviceModel> query = (TypedQuery<IosDeviceModel>) session.createQuery
                    ("from IosDeviceModel where identifierForVendor= :identifierForVendor");
            query.setParameter("identifierForVendor", identifierForVendor);
            iosDeviceModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iosDeviceModel;
    }

    public IosDeviceModel getIosDeviceByDeviceID(Session session, Long deviceId) {
        IosDeviceModel iosDeviceModel = new IosDeviceModel();
        try {
            TypedQuery<IosDeviceModel> query = (TypedQuery<IosDeviceModel>) session.createQuery
                    ("from IosDeviceModel where idDevice= :deviceId");
            query.setParameter("deviceId", deviceId);
            iosDeviceModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iosDeviceModel;

    }
}

