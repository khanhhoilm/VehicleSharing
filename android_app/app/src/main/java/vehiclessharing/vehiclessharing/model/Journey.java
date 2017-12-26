package vehiclessharing.vehiclessharing.model;

import android.view.ViewDebug;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 12/13/2017.
 */

public class Journey {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("rating_value")
    @Expose
    private Integer ratingValue;

    @SerializedName("start_time")
    @Expose
    private StartTime startTime;

    @SerializedName("finish_time")
    @Expose
    private String finishTime;

    @SerializedName("cancel_time")
    @Expose
    private String cancelTime;

    @SerializedName("start_location")
    @Expose
    private LatLngLocation startLocation;

    @SerializedName("end_location")
    @Expose
    private LatLngLocation endLocation;

    @SerializedName("partner")
    @Expose
    private Partner partner;

    @SerializedName("partner_rating")
    @Expose
    private PartnerRating partnerRating;

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public PartnerRating getPartnerRating() {
        return partnerRating;
    }

    public void setPartnerRating(PartnerRating partnerRating) {
        this.partnerRating = partnerRating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }
    public StartTime getStartTime() {
        return startTime;
    }

    public void setStartTime(StartTime startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
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

    public Partner getPartner() {
        return partner;
    }

    public void setParnerInfo(Partner parnter) {
        this.partner = partner;
    }

    public class StartTime {
        @SerializedName("date")
        @Expose
        private String date;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public class PartnerRating {

      @SerializedName("rating_value")
        @Expose
        private Integer ratingValue;

      @SerializedName("comment")
        @Expose
        private String comment;

      @SerializedName("vehicle_type")
        @Expose
        private Integer vehicleType;

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

        public Integer getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(Integer vehicleType) {
            this.vehicleType = vehicleType;
        }
    }

    /*                          "journey_id": 31,
                                "user_id": 21,
                                "rating_value": 5,
                                "comment": "good",
                                "vehicle_type": 0
                            }
                     */
}
