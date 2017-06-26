package vn.viettel.browser.dao.iface;

import org.hibernate.Session;
import vn.viettel.browser.model.UserModel;

import java.util.List;

/**
 * Created by quytx on 4/10/2017.
 * Project: App.dao.iface:Social_Login
 */
public interface UserDAOIface {
    void addUser(Session session, UserModel userModel);

    void updateUser(Session session, UserModel userModel);

    List<UserModel> listUsers(Session session);

    UserModel getUserByID(Session session, int id);

    void removeUser(Session session, int id);

    long getID(Session session, UserModel userModel);

    UserModel findByID(Session session, int id);
}
