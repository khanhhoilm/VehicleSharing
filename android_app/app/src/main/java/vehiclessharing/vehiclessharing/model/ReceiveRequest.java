package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 11/20/2017.
 */

public class ReceiveRequest {

    @SerializedName("avatar_link")
    @Expose
    private String avartarLink;

    @SerializedName("start_location")
    @Expose
    private LatLngLocation startLocation;

    @SerializedName("vehicle_type")
    @Expose
    private Integer vehicleType;

    @SerializedName("start_time")
    @Expose
    private String startTime;

    @SerializedName("end_location")
    @Expose
    private LatLngLocation endLocation;

    @SerializedName("user_id")
    @Expose
    private Integer userId;

    @SerializedName("note")
    @Expose
    private String note;


    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("user_name")
    @Expose
    private String userName;

    public String getAvartarLink() {
        return avartarLink;
    }

    public void setAvartarLink(String avartarLink) {
        this.avartarLink = avartarLink;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Integer vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public LatLngLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLngLocation startLocation) {
        this.startLocation = startLocation;
    }

    public LatLngLocation getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLngLocation endLocation) {
        this.endLocation = endLocation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
