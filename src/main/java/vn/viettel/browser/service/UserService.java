package vn.viettel.browser.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.browser.dao.AccountDAO;
import vn.viettel.browser.dao.MapDeviceAccDAO;
import vn.viettel.browser.model.AccountModel;
import vn.viettel.browser.model.MapDeviceAccModel;
import vn.viettel.browser.model.UserModel;
import vn.viettel.browser.service.iface.UserServiceIface;
import vn.viettel.browser.ultils.HibernateUtility;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by quytx on 3/31/2017.
 * Project: App.service:Social_Login
 */
@Service
public class UserService implements UserServiceIface {
    private static AccountDAO accountDAO = new AccountDAO();
    private static MapDeviceAccDAO mapDeviceAccDAO = new MapDeviceAccDAO();

    private final
    AccountService accountService;

    @Autowired
    public UserService(AccountService accountService) {
        this.accountService = accountService;
    }

    public UserModel createNewUserByGmail(JSONObject json, AtomicLong idUser) {
        UserModel newUser = new UserModel();
        newUser.setId(idUser.incrementAndGet());
        newUser.setGmail(json.get("email").toString());
        newUser.setFirstName(json.get("given_name").toString());
        newUser.setLastName(json.get("family_name").toString());
        newUser.setAge(18);
        newUser.setCountry(json.get("locale").toString());
        newUser.setDateOfBirth("1-1-1970");
        newUser.setSex("Male");
        newUser.setMobileNumber("0");
        newUser.setLinkFB("");
        return newUser;
    }

    @Override
    public UserModel createNewUserByFacebook(JSONObject json, AtomicLong idUser) {
        UserModel newUser = new UserModel();
        newUser.setId(idUser.incrementAndGet());
        newUser.setGmail(" ");
        newUser.setFirstName(json.get("first_name").toString());
        newUser.setLastName(json.get("last_name").toString());
        newUser.setAge(18);
        newUser.setCountry("VN");
        newUser.setDateOfBirth("1-1-1970");
        newUser.setSex("Male");
        newUser.setMobileNumber("0");
        newUser.setLinkFB("https://www.facebook.com/" + json.get("id").toString());
        return newUser;
    }

    @Override
    public UserModel createNewUserByMobile(JSONObject json, AtomicLong idUser) {
        return null;
    }

    @Override
    public UserModel createNewUserByDevice(JSONObject json, AtomicLong idUser, long idDevice) {
        UserModel newUser = new UserModel();
        newUser.setId(idUser.incrementAndGet());
        newUser.setGmail(" ");
        newUser.setFirstName("GuestVT-" + String.valueOf(idDevice));
        newUser.setLastName(" ");
        newUser.setAge(18);
        newUser.setCountry("VN");
        newUser.setDateOfBirth("1-1-1970");
        newUser.setSex("Male");
        newUser.setMobileNumber("0");
        newUser.setLinkFB(" ");
        return newUser;
    }

    @Override
    public long getIdFromAccessToken(String accessToken) {
        long idUser = 0L;
        try {
            SessionFactory factory = HibernateUtility.getSessionFactory();
            Session session = factory.getCurrentSession();
            try {
                if (!session.beginTransaction().isActive()) {
                    session.beginTransaction().begin();
                }
                MapDeviceAccModel mapDeviceAccModel = mapDeviceAccDAO.findByAccessToken(session, accessToken);
                AccountModel accountResponse = accountDAO.findByName(session, mapDeviceAccModel.getAccountName());
                long unixCurrentTime = new Date().getTime() / 1000;
                accountService.updateLastLoginAccount(session, accountResponse, unixCurrentTime);
                idUser = accountResponse.getUserProfileId();
            } catch (Exception e) {
                e.printStackTrace();
                session.getTransaction().rollback();
            } finally {
                session.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idUser;

    }

}
