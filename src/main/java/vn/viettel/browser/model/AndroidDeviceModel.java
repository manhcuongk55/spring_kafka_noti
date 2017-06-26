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
@Table(name = "android_device")
public class AndroidDeviceModel implements Serializable {
    @Id
    @Column(name = "serial_number")
    private String serialNumber;

    @Id
    @Column(name = "mac_wifi")
    private String macWifi;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "bootloader")
    private String bootloader;

    @Column(name = "display")
    private String display;

    @Column(name = "hardware")
    private String hardware;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "model")
    private String model;

    @Column(name = "product")
    private String product;

    @Column(name = "user")
    private String user;

    @Column(name = "os_name")
    private String osName;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "device_id")
    private long deviceId;

    public AndroidDeviceModel() {
        super();
    }

    public AndroidDeviceModel(String serialNumber, String macWifi, String deviceName, String bootloader, String display,
                              String hardware, String manufacturer, String model, String product, String user,
                              String osName, String osVersion, long deviceId) {
        this.serialNumber = serialNumber;
        this.macWifi = macWifi;
        this.deviceName = deviceName;
        this.deviceId = deviceId;
        this.bootloader = bootloader;
        this.display = display;
        this.hardware = hardware;
        this.manufacturer = manufacturer;
        this.model = model;
        this.product = product;
        this.user = user;
        this.osName = osName;
        this.osVersion = osVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMacWifi() {
        return macWifi;
    }

    public void setMacWifi(String macWifi) {
        this.macWifi = macWifi;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBootloader() {
        return bootloader;
    }

    public void setBootloader(String bootloader) {
        this.bootloader = bootloader;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osName) {
        this.osName = osName;
    }

    public long getidDevice() {
        return deviceId;
    }

    public void setidDevice(long deviceId) {
        this.deviceId = deviceId;
    }

}
