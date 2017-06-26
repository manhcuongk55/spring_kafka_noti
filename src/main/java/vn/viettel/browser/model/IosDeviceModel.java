package vn.viettel.browser.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by quytx on 4/6/2017.
 * Project: App.model:Social_Login
 */

@Entity
@Table(name = "ios_device")
public class IosDeviceModel implements Serializable {
    @Id
    @Column(name = "identifierForVendor")
    private String identifierForVendor;

    @Column(name = "name")
    private String name;

    @Column(name = "model")
    private String model;

    @Column(name = "systemName")
    private String systemName;

    @Column(name = "systemVersion")
    private String systemVersion;

    @Column(name = "device_id")
    private long idDevice;

    public IosDeviceModel() {
        super();
    }

    public IosDeviceModel(String identifierForVendor, String name, String model,
                          String systemName, String systemVersion, long idDevice) {
        this.identifierForVendor = identifierForVendor;
        this.name = name;
        this.model = model;
        this.systemName = systemName;
        this.systemVersion = systemVersion;
        this.idDevice = idDevice;
    }

    public String getIdentifierForVendor() {
        return identifierForVendor;
    }

    public void setIdentifierForVendor(String identifierForVendor) {
        this.identifierForVendor = identifierForVendor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public long getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(long idDevice) {
        this.idDevice = idDevice;
    }

}
