package vn.viettel.browser.model;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by giang on 04/10/2017.
 */
@Entity
@Table(name = "test_devices")
public class TestDevices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    int id;

    public String getFirebase_id() {
        return firebase_id;
    }

    @Column(name = "firebase_id")
    String firebase_id;

    @Column(name = "device_name")
    String device_name;

    @Column(name = "app_version")
    String app_version;

    @Column(name = "device_version")
    String device_version;

    @Column(name = "time_created")
    Date time_created;

    public int getId() {
        return id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public String getApp_version() {
        return app_version;
    }

    public String getDevice_version() {
        return device_version;
    }

    public Date getTime_created() {
        return time_created;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirebase_id(String firebase_id) {
        this.firebase_id = firebase_id;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public void setDevice_version(String device_version) {
        this.device_version = device_version;
    }

    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }

    public TestDevices(int id, String firebase_id, String device_name, String app_version, String device_version,
                       Date time_created) {
        this.id = id;
        this.firebase_id = firebase_id;
        this.device_name = device_name;
        this.app_version = app_version;
        this.device_version = device_version;
        this.time_created = time_created;
    }

    public TestDevices() {
    }
}
