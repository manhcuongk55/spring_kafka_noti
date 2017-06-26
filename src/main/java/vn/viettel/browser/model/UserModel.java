package vn.viettel.browser.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by quytx on 4/6/2017.
 * Project: App.model:Social_Login
 */

@Entity
@Table(name = "user_profile")
public class UserModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "age")
    private int age;

    @Column(name = "gmail")
    private String gmail;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "sex")
    private String sex;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "country")
    private String country;

    @Column(name = "link_fb")
    private String linkFB;

    public UserModel() {
        super();
    }

    public UserModel(int id, String lastName, String firstName, int age, String gmail, String dateOfBirth,
                     String sex, String mobileNumber, String country, String linkFB) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
        this.gmail = gmail;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.mobileNumber = mobileNumber;
        this.country = country;
        this.linkFB = linkFB;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLinkFB() {
        return linkFB;
    }

    public void setLinkFB(String linkFB) {
        this.linkFB = linkFB;
    }


}
