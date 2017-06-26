package vn.viettel.browser.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by quytx on 3/31/2017.
 * Project: App.model:Social_Login
 */

@Entity
@Table(name = "account")
public class AccountModel implements Serializable {
    @Id
    @Column(name = "account_name")
    private String accountName;
    @Column(name = "last_login")
    private long lastLogin;
    @Column(name = "user_profile_id")
    private long userProfileId;


    public AccountModel() {
        super();
    }

    public AccountModel(String accountName, long lastLogin, long userProfileId) {
        super();
        this.accountName = accountName;
        this.lastLogin = lastLogin;
        this.userProfileId = userProfileId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
    }

}
