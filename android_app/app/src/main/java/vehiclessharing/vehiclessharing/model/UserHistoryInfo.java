package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hihihehe on 12/13/2017.
 */

public class UserHistoryInfo {
    @SerializedName("success")
    @Expose
    private List<JourneyDone> successJourney;

    @SerializedName("fail")
    @Expose
    private List<JourneyDone> failJourney;

    @SerializedName("user_type")
    @Expose
    private String userType;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<JourneyDone> getSuccessJourney() {
        return successJourney;
    }


    public void setSuccessJourney(List<JourneyDone> successJourney) {
        this.successJourney = successJourney;
    }

    public List<JourneyDone> getFailJourney() {
        return failJourney;
    }

    public void setFailJourney(List<JourneyDone> failJourney) {
        this.failJourney = failJourney;
    }
}
