package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hihihehe on 10/15/2017.
 */

public class RequestResult {
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("active_users")
    @Expose
    private List<ActiveUser> activeUsers = null;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<ActiveUser> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(List<ActiveUser> activeUsers) {
        this.activeUsers = activeUsers;
    }
}
