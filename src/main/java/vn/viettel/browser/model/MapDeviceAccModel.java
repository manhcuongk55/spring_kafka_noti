package vn.viettel.browser.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.model:Social_Login
 */

@Entity
@Table(name = "map_device_acc")
public class MapDeviceAccModel implements Serializable {
    @Id
    @Column(name = "device_id")
    private long idDevice;

    @Id
    @Column(name = "account_name")
    private String accountName;

    @Id
    @Column(name = "last_login")
    private long lastLogin;

    @Column(name = "access_token")
    private String accessToken;

    public MapDeviceAccModel() {
        super();
    }

    public MapDeviceAccModel(long idDevice, String accountName, long lastLogin, String accessToken) {
        this.accountName = accountName;
        this.idDevice = idDevice;
        this.lastLogin = lastLogin;
        this.accessToken = accessToken;
    }

    public long getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(long id) {
        this.idDevice = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String name) {
        this.accountName = name;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
