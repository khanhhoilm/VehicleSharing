package vehiclessharing.vehiclessharing.model;

import io.realm.RealmObject;

/**
 * Created by Tuan on 09/05/2017.
 */

public class AddressOnDevice extends RealmObject {
    private String country;//user's country
    private String district;//user's district
    private String province;//user's province

    public AddressOnDevice() {
        this.country = "";
        this.district = "";
        this.province = "";
    }

    public AddressOnDevice(String country, String district, String province) {
        this.country = country;
        this.district = district;
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
