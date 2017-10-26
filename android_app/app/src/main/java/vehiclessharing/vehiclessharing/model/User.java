package vehiclessharing.vehiclessharing.model;

/**
 * Created by Tuan on 13/03/2017.
 */

public class User {

    private String email;
    private String image;
    private String fullName;
    private String phoneNumber;
    private String sex;
    private UserAddress address;
    private BirthDay birthDay;

    public User() {
    }

    /**
     * Contructor
     * @param email - user's email
     * @param image - user's url image
     * @param fullName - user's fullname
     * @param phoneNumber - user's phoneNumber
     * @param sex - user's sex
     * @param address - user's address
     * @param  birthDay- user's birthday
     */
    public User(String email, String image, String fullName, String phoneNumber, String sex, UserAddress address, BirthDay birthDay) {
        this.email = email;
        this.image = image;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.sex = sex;
        this.address = address;
        this.birthDay = birthDay;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    public UserAddress getAddress() {
        return address;
    }

    public void setAddress(UserAddress address) {
        this.address = address;
    }

    public BirthDay getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(BirthDay birthDay) {
        this.birthDay = birthDay;
    }

}

