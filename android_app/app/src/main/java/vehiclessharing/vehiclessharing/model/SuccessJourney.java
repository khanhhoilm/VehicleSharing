package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hihihehe on 12/13/2017.
 */

public class SuccessJourney {
    @SerializedName("driver")
    @Expose
    private List<JourneyDone> driver;

    @SerializedName("hiker")
    @Expose
    private List<JourneyDone> hiker;

    public List<JourneyDone> getDriver() {
        return driver;
    }

    public void setDriver(List<JourneyDone> driver) {
        this.driver = driver;
    }

    public List<JourneyDone> getHiker() {
        return hiker;
    }

    public void setHiker(List<JourneyDone> hiker) {
        this.hiker = hiker;
    }


    public class UserAction {
        @SerializedName("rating_value")
        @Expose
        private Integer ratingValue;

        @SerializedName("comment")
        @Expose
        private String comment;

        public Integer getRatingValue() {
            return ratingValue;
        }

        public void setRatingValue(Integer ratingValue) {
            this.ratingValue = ratingValue;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
