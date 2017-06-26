package vn.viettel.browser.dao.iface;

import org.hibernate.Session;
import vn.viettel.browser.model.AccountModel;

import java.util.List;

/**
 * Created by quytx on 4/10/2017.
 * Project: App.dao.iface:Social_Login
 */
public interface AccountDAOIface {
    void addAccount(Session session, AccountModel accountModel);

    void updateAccount(Session session, AccountModel accountModel);

    List<AccountModel> listAccounts(Session session);

    AccountModel getAccountByID(Session session, int id);

    void removeAccount(Session session, int id);

    String getID(Session session, AccountModel accountModel);

    AccountModel findByName(Session session, String account_name);

//    public AccountModel findByAccessToken(Session session, String accessToken);
}
