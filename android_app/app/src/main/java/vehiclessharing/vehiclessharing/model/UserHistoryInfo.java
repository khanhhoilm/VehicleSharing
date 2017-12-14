package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 12/13/2017.
 */

public class UserHistoryInfo {
    @SerializedName("success_journey")
    @Expose
    private SuccessJourney successJourney;

    @SerializedName("fail_journey")
    @Expose
    private FailJourney failJourney;

    public SuccessJourney getSuccessJourney() {
        return successJourney;
    }

    public void setSuccessJourney(SuccessJourney successJourney) {
        this.successJourney = successJourney;
    }

    public FailJourney getFailJourney() {
        return failJourney;
    }

    public void setFailJourney(FailJourney failJourney) {
        this.failJourney = failJourney;
    }
}
