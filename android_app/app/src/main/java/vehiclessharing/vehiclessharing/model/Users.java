package vehiclessharing.vehiclessharing.model;

/**
 * Created by Hihihehe on 9/19/2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

//import vehiclessharing.vehiclessharing.database.RealmDatabase;

public class Users implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("google_id")
    @Expose
    private Object googleId;
    @SerializedName("facebook_id")
    @Expose
    private Object facebookId;
    @SerializedName("avatar_link")
    @Expose
    private Object avatarLink;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("gender")
    @Expose
    private Integer gender;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("session_id")
    @Expose
    private Object sessionId;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("delete_at")
    @Expose
    private Object deleteAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getGoogleId() {
        return googleId;
    }

    public void setGoogleId(Object googleId) {
        this.googleId = googleId;
    }

    public Object getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Object facebookId) {
        this.facebookId = facebookId;
    }

    public Object getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(Object avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getSessionId() {
        return sessionId;
    }

    public void setSessionId(Object sessionId) {
        this.sessionId = sessionId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Object getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(Object deleteAt) {
        this.deleteAt = deleteAt;
    }
}
