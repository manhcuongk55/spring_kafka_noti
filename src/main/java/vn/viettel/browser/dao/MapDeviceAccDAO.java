package vn.viettel.browser.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import vn.viettel.browser.dao.iface.MapDeviceAccountDAOIface;
import vn.viettel.browser.model.MapDeviceAccModel;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.dao:Social_Login
 */
@Repository
public class MapDeviceAccDAO implements MapDeviceAccountDAOIface {
    private final static Logger logger = LoggerFactory.getLogger(MapDeviceAccDAO.class);

    @Override
    public MapDeviceAccModel checkExistedMapDeviceAcc(Session session, long idDevice, String accountName) {
        MapDeviceAccModel mapDeviceAccModel = new MapDeviceAccModel();
        try {
            TypedQuery<MapDeviceAccModel> query = (TypedQuery<MapDeviceAccModel>) session.createQuery(
                    "from MapDeviceAccModel where idDevice= :idDevice and accountName= :accountName");
            query.setParameter("idDevice", idDevice);
            query.setParameter("accountName", accountName);
            mapDeviceAccModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapDeviceAccModel;
    }

    @Override
    public void addMapDeviceAcc(Session session, MapDeviceAccModel map) {
        session.save(map);
        logger.info("MapDeviceAccModel added successfully, User Details=" + map);
    }

    @Override
    public MapDeviceAccModel findAccountNameFromIdDevice(Session session, long idDevice) {
        MapDeviceAccModel mapDeviceAccModel = new MapDeviceAccModel();
        try {
            TypedQuery<MapDeviceAccModel> query = (TypedQuery<MapDeviceAccModel>) session.createQuery(
                    "from MapDeviceAccModel where idDevice= :idDevice ");
            query.setParameter("idDevice", idDevice);
            mapDeviceAccModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapDeviceAccModel;
    }

    @Override
    public List<MapDeviceAccModel> findDevicesByAccountName(Session session, String accountName) {
        List<MapDeviceAccModel> mapDeviceAccModelArrayList = new ArrayList<>();
        try {
            TypedQuery<MapDeviceAccModel> query = (TypedQuery<MapDeviceAccModel>) session.createQuery(
                    "from MapDeviceAccModel where accountName= :accountName ");
            query.setParameter("accountName", accountName);
            mapDeviceAccModelArrayList = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapDeviceAccModelArrayList;
    }

    @Override
    public void updateLastLoginMapDeviceAcc(Session session, MapDeviceAccModel mapDeviceAccModel) {
//        session.update(mapDeviceAccModel);
        long lastLogin = mapDeviceAccModel.getLastLogin();
        String accountName = mapDeviceAccModel.getAccountName();
        long deviceId = mapDeviceAccModel.getIdDevice();
        TypedQuery<MapDeviceAccModel> query = (TypedQuery<MapDeviceAccModel>) session.createQuery(
                "update MapDeviceAccModel set lastLogin= :lastLogin where accountName= :accountName and idDevice= :deviceId");
        query.setParameter("accountName", accountName);
        query.setParameter("lastLogin", lastLogin);
        query.setParameter("deviceId", deviceId);
        query.executeUpdate();
        logger.info("MapDeviceAccModel update successfully, User Details=" + mapDeviceAccModel);
    }

    @Override
    public MapDeviceAccModel findByAccessToken(Session session, String accessToken) {
        MapDeviceAccModel mapDeviceAccModel = new MapDeviceAccModel();
        try {
            TypedQuery<MapDeviceAccModel> query = (TypedQuery<MapDeviceAccModel>) session.createQuery(
                    "from MapDeviceAccModel where accessToken= :accessToken ");
            query.setParameter("accessToken", accessToken);
            mapDeviceAccModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapDeviceAccModel;
    }

    @Override
    public void updateAccessTokenMapDeviceAcc(Session session, MapDeviceAccModel mapDeviceAccModel) {
        String accessToken = mapDeviceAccModel.getAccessToken();
        String accountName = mapDeviceAccModel.getAccountName();
        long deviceId = mapDeviceAccModel.getIdDevice();
        TypedQuery<MapDeviceAccModel> query = (TypedQuery<MapDeviceAccModel>) session.createQuery(
                "update MapDeviceAccModel set accessToken= :accessToken where accountName= :accountName and idDevice= :deviceId");
        query.setParameter("accountName", accountName);
        query.setParameter("accessToken", accessToken);
        query.setParameter("deviceId", deviceId);
        query.executeUpdate();
        logger.info("MapDeviceAccModel update successfully, User Details=" + mapDeviceAccModel);
    }


}
