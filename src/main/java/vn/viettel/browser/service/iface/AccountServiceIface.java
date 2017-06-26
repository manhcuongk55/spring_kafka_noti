package vn.viettel.browser.service.iface;

import org.hibernate.Session;
import vn.viettel.browser.model.AccountModel;
import vn.viettel.browser.model.MapDeviceAccModel;
import vn.viettel.browser.model.UserModel;

import java.util.List;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.service:Social_Login
 */
public interface AccountServiceIface {

    void createNewAccount(AccountModel accountModel);

    void createNewAccountbyDevice(AccountModel accountUpdate, UserModel userUpdate,
                                  long idDevice, String accountName);

    void updateLastLoginAccount(Session session, AccountModel accountModel, long unixTimeNow);

    void updateAccount(AccountModel accountModel);

    List<AccountModel> listAccounts();

    AccountModel getAccountByID(int id);

    void removeAccount(int id);

    String getID(AccountModel accountModel);

    AccountModel findByName(String account_name);

    MapDeviceAccModel findByAccessToken(String accessToken);

    void updateMapDeviceAccount(MapDeviceAccModel mapDeviceAccModel);
}
