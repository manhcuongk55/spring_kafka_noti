package vn.viettel.browser.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.browser.dao.AccountDAO;
import vn.viettel.browser.dao.MapDeviceAccDAO;
import vn.viettel.browser.dao.UserDAO;
import vn.viettel.browser.model.AccountModel;
import vn.viettel.browser.model.MapDeviceAccModel;
import vn.viettel.browser.model.UserModel;
import vn.viettel.browser.service.iface.AccountServiceIface;
import vn.viettel.browser.ultils.HibernateUtils;

import java.util.List;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.service:Social_Login
 */

@Service
public class AccountService implements AccountServiceIface {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final
    DeviceService deviceService;
    private AccountDAO accountDAO = new AccountDAO();
    private UserDAO userDAO = new UserDAO();
    private MapDeviceAccDAO mapDeviceAccDAO = new MapDeviceAccDAO();

    @Autowired
    public AccountService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void createNewAccount(AccountModel accountModel) {

    }

    @Override
    public void updateLastLoginAccount(Session session, AccountModel accountModel, long unixTimeNow) {
        accountModel.setLastLogin(unixTimeNow);
        accountDAO.updateAccount(session, accountModel);
    }

    @Override
    public void createNewAccountbyDevice(AccountModel accountUpdate, UserModel userUpdate, long idDevice, String accountName) {
        MapDeviceAccModel mapDeviceAccModel = deviceService.checkConnectDeviceAcc(idDevice, accountName);
        int checkCreateNewMap = 0;
        SessionFactory factory = HibernateUtils.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            userDAO.addUser(session, userUpdate);
            long user_profile_id = userUpdate.getId();
            accountUpdate.setUserProfileId(user_profile_id);
            accountDAO.addAccount(session, accountUpdate);
            session.getTransaction().commit();
            if (mapDeviceAccModel.getAccountName() == null) {
                checkCreateNewMap = 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        if (checkCreateNewMap == 1)
            deviceService.addNewConnectDeviceAcc(idDevice, accountName);
        logger.info("Created new Account Success : " + accountUpdate.getAccountName());
    }

    @Override
    public void updateAccount(AccountModel accountModel) {
        AccountDAO accountDAO = new AccountDAO();
        SessionFactory factory = HibernateUtils.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            accountDAO.updateAccount(session, accountModel);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        logger.info("Update Account Success : " + accountModel.getAccountName());
    }

    @Override
    public List<AccountModel> listAccounts() {
        return null;
    }

    @Override
    public AccountModel getAccountByID(int id) {
        return null;
    }

    @Override
    public void removeAccount(int id) {

    }

    @Override
    public String getID(AccountModel accountModel) {
        return null;
    }

    @Override
    public AccountModel findByName(String accountName) {
        AccountDAO accountDAO = new AccountDAO();
        AccountModel accountModel = new AccountModel();
        SessionFactory factory = HibernateUtils.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            accountModel = accountDAO.findByName(session, accountName);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return accountModel;
    }

    @Override
    public MapDeviceAccModel findByAccessToken(String accessToken) {
        MapDeviceAccModel mapDeviceAccModel = new MapDeviceAccModel();
        SessionFactory factory = HibernateUtils.getSessionFactory();
        try (Session session = factory.getCurrentSession()) {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            mapDeviceAccModel = mapDeviceAccDAO.findByAccessToken(session, accessToken);
            if (mapDeviceAccModel.getAccountName() != null) {
                session.close();
                return mapDeviceAccModel;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapDeviceAccModel;
    }

    @Override
    public void updateMapDeviceAccount(MapDeviceAccModel mapDeviceAccModel) {
        SessionFactory factory = HibernateUtils.getSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            if (!session.beginTransaction().isActive()) {
                session.beginTransaction().begin();
            }
            mapDeviceAccDAO.updateAccessTokenMapDeviceAcc(session, mapDeviceAccModel);
            session.getTransaction().commit();
            if (mapDeviceAccModel.getAccountName() != null) {
                session.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        logger.info("Update mapDeviceAccModel Success : " + mapDeviceAccModel.getAccountName());
    }
}
