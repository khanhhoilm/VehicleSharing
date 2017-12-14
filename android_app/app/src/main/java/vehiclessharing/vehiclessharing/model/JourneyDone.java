package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 12/13/2017.
 */

public class JourneyDone {
    @SerializedName("journey")
    @Expose
    private Journey journey;

    @SerializedName("user_action")
    @Expose
    private SuccessJourney.UserAction userAction;

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public SuccessJourney.UserAction getUserAction() {
        return userAction;
    }

    public void setUserAction(SuccessJourney.UserAction userAction) {
        this.userAction = userAction;
    }
}

