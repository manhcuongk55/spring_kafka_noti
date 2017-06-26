package vn.viettel.browser.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by quytx on 4/11/2017.
 * Project: App.model:Social_Login
 */
@Entity
@Table(name = "device")
public class DeviceModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "type")
    private String type;

    @Column(name = "reg_firebase_id")
    private String reg_firebase_id;

    public DeviceModel() {
        super();
    }

    public DeviceModel(long id, String type, String reg_firebase_id) {
        this.id = id;
        this.type = type;
        this.reg_firebase_id = reg_firebase_id;
    }

    public DeviceModel(long id, String type) {
        this.id = id;
        this.type = type;
        this.reg_firebase_id = "";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReg_firebase_id() {
        return reg_firebase_id;
    }

    public void setReg_firebase_id(String reg_firebase_id) {
        this.reg_firebase_id = reg_firebase_id;
    }
}
