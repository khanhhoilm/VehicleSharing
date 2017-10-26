package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 10/15/2017.
 */

public class RequestInfo {
    @SerializedName("vehicle_type")
    @Expose
    private Integer vehicleType;
    @SerializedName("source_location")
    @Expose
    private LatLngLocation sourceLocation;
    @SerializedName("dest_location")
    @Expose
    private LatLngLocation destLocation;
    @SerializedName("time_start")
    @Expose
    private String timeStart;

    public Integer getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Integer vehicleType) {
        this.vehicleType = vehicleType;
    }

    public LatLngLocation getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(LatLngLocation sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public LatLngLocation getDestLocation() {
        return destLocation;
    }

    public void setDestLocation(LatLngLocation destLocation) {
        this.destLocation = destLocation;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

}
