package vn.viettel.browser.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import vn.viettel.browser.dao.iface.UserDAOIface;
import vn.viettel.browser.model.UserModel;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by quytx on 4/6/2017.
 * Project: App.dao:Social_Login
 */
@Repository
public class UserDAO implements UserDAOIface {
    private final static Logger logger = LoggerFactory.getLogger(AccountDAO.class);

    @Override
    public void addUser(Session session, UserModel userModel) {
//        session.persist(userModel);
        session.save(userModel);
        logger.info("User added successfully, User Details=" + userModel);
    }

    @Override
    public void updateUser(Session session, UserModel userModel) {
        session.update(userModel);
        logger.info("User updated successfully, User Details=" + userModel);
    }

    @Override
    public List<UserModel> listUsers(Session session) {
        List<UserModel> usersList = session.createQuery("from UserModel").list();
        for (UserModel userModel : usersList) logger.info("User List::" + userModel);
        return usersList;
    }

    @Override
    public UserModel getUserByID(Session session, int id) {
        UserModel userModel = (UserModel) session.load(UserModel.class, new Integer(id));
        logger.info("User loaded successfully, User details=" + userModel);
        return userModel;
    }

    @Override
    public void removeUser(Session session, int id) {
        UserModel userModel = (UserModel) session.load(UserModel.class, new Integer(id));
        if (null != userModel) {
            session.delete(userModel);
        }
        logger.info("User deleted successfully, User details=" + userModel);
    }

    @Override
    public long getID(Session session, UserModel userModel) {
        return userModel.getId();
    }

    @Override
    public UserModel findByID(Session session, int id) {
        UserModel userModel = new UserModel();
        try {
            TypedQuery<UserModel> query = (TypedQuery<UserModel>) session.createQuery("from UserModel where id= :id");
            query.setParameter("id", id);
            userModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userModel;
    }
}

