package vn.viettel.browser.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import vn.viettel.browser.dao.iface.AccountDAOIface;
import vn.viettel.browser.model.AccountModel;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by quytx on 4/6/2017.
 * Project: App.dao:Social_Login
 */
@Repository
public class AccountDAO implements AccountDAOIface {
    private static final Logger logger = LoggerFactory.getLogger(AccountDAO.class);

    @Override
    public void addAccount(Session session, AccountModel accountModel) {
//        session.persist(accountModel);
        session.save(accountModel);
        logger.info("User saved successfully, User Details=" + accountModel);
    }

    @Override
    public void updateAccount(Session session, AccountModel accountModel) {
        session.update(accountModel);
        logger.info("User updated successfully, User Details=" + accountModel);
    }

    @Override
    public List<AccountModel> listAccounts(Session session) {
        List<AccountModel> accountModelList = session.createQuery("from AccountModel").list();
        for (AccountModel accountModel : accountModelList) logger.info("User List::" + accountModel);
        return accountModelList;
    }

    @Override
    public AccountModel getAccountByID(Session session, int id) {
        AccountModel accountModel = session.load(AccountModel.class, new Integer(id));
        logger.info("User loaded successfully, User details=" + accountModel);
        return accountModel;
    }

    @Override
    public void removeAccount(Session session, int id) {
        AccountModel accountModel = session.load(AccountModel.class, new Integer(id));
        if (null != accountModel) {
            session.delete(accountModel);
        }
        logger.info("User deleted successfully, User details=" + accountModel);
    }

    @Override
    public String getID(Session session, AccountModel accountModel) {
        return accountModel.getAccountName();
    }

    @Override
    public AccountModel findByName(Session session, String accountName) {
        AccountModel accountModel = new AccountModel();
        try {
            TypedQuery<AccountModel> query = (TypedQuery<AccountModel>) session.createQuery
                    ("from AccountModel where accountName= :accountName");
            query.setParameter("accountName", accountName);
            accountModel = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accountModel;
    }

//    @Override
//    public AccountModel findByAccessToken(Session session, String accessToken) {
//        AccountModel accountModel = new AccountModel();
//        try{
//            TypedQuery<AccountModel> query = (TypedQuery<AccountModel>) session.createQuery
//                    ("from AccountModel where accessToken= :accessToken");
//            query.setParameter("accessToken",accessToken);
//            accountModel = query.getSingleResult();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return accountModel;
//    }

}
