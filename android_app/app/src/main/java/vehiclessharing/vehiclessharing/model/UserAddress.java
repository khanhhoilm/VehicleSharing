package vehiclessharing.vehiclessharing.model;

import java.util.HashMap;

/**
 * Created by Tuan on 02/04/2017.
 */

public class UserAddress {
    private String country;//user's country
    private String district;//user's district
    private String province;//user's province


    public UserAddress()
    {
        /*this.country = "";
        this.district = "";
        this.province = "";*/
    }

    public UserAddress(String country, String district, String province) {
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

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("country",country);
        result.put("district",district);
        result.put("province",province);
        return result;
    }
}
