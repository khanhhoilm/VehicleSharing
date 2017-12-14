package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hihihehe on 12/13/2017.
 */

public class History {
    @SerializedName("status")
    @Expose
    private Status status;

    @SerializedName("user_history_info")
    @Expose
    private List<UserHistoryInfo> userHistoryInfo;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<UserHistoryInfo> getUserHistoryInfo() {
        return userHistoryInfo;
    }

    public void setUserHistoryInfo(List<UserHistoryInfo> userHistoryInfo) {
        this.userHistoryInfo = userHistoryInfo;
    }

}
