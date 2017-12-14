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

    @SerializedName("finnish_time")
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

    @SerializedName("parner")
    @Expose
    private User parnerInfo;

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

    public User getParnerInfo() {
        return parnerInfo;
    }

    public void setParnerInfo(User parnerInfo) {
        this.parnerInfo = parnerInfo;
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

    /*{
                            "start_location": {
                                "lat": "10.798132",
                                "lng": "106.68890699999997"
                            },
                            "end_location": {
                                "lat": "40.741895",
                                "lng": "-73.989308"
                            },
                            "partner": {
                                "id": 21,
                                "phone": "01677735016",
                                "name": "Hội Khánh 016",
                                "email": null,
                                "google_id": null,
                                "facebook_id": null,
                                "avatar_link": null,
                                "gender": 1,
                                "address": null,
                                "birthday": null
                            },
                            "partner_rating": {
                                "journey_id": 31,
                                "user_id": 21,
                                "rating_value": 5,
                                "comment": "good",
                                "vehicle_type": 0
                            }
                        },
                        "user_action": {
                            "rating_value": 5,
                            "comment": "Lái xe an toàn"
                        }
                    }*/
}
