package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import vehiclessharing.vehiclessharing.model.History;
import vehiclessharing.vehiclessharing.model.RequestResult;
import vehiclessharing.vehiclessharing.model.ResultSendRequest;
import vehiclessharing.vehiclessharing.model.SignInResult;
import vehiclessharing.vehiclessharing.model.StartStripResponse;
import vehiclessharing.vehiclessharing.model.StatusResponse;
import vehiclessharing.vehiclessharing.model.UserInfo;


public interface ApiService {

    // String BASE_URL = "http://vehiclessharing.viecit.co/";
    String BASE_URL = "https://vehicle-sharing.herokuapp.com/";

    @POST("users/register")
    @FormUrlEncoded
    Call<StatusResponse> signUp(@Field("phone") String phone,
                                @Field("name") String name,
                                @Field("password") String password,
                                @Field("gender") int gender);

    @POST("users/signin")
    @FormUrlEncoded
    Call<SignInResult> signIn(@Field("phone") String phone,
                              @Field("password") String password);

    @POST("users/signout")
    @FormUrlEncoded
    Call<StatusResponse> signOut(@Field("api_token") String apiToken);

    @POST("users/update")
    @FormUrlEncoded
    Call<StatusResponse> updateInfoUser(@Field("api_token") String apiToken, @Field("name") String name, @Field("email") String email,
                                        @Field("avatar_link") String avatarLink, @Field("password") String password, @Field("gender") int gender,
                                        @Field("address") String address, @Field("birthday") String birthday);

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("api/request")
    Call<RequestResult> registerRequest(@Field("user_id") int userId, @Field("source_location") String sourceLocation,
                                        @Field("destination_location") String desLocation, @Field("time_start") String timeStart,
                                        @Field("api_token") String session, @Field("device_id") String devideId, @Field("vehicle_type") int vehicleType,
                                        @Field("device_token") String deviceToken);


    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("api/send-request")
    Call<ResultSendRequest> sendRequestTogether(@Field("api_token") String apiToken, @Field("receiver_id") int receiverId, @Field("note") String note);

    @FormUrlEncoded
    @POST("api/cancel-request")
    Call<StatusResponse> cancelRequest(@Field("api_token") String apiToken);

    @FormUrlEncoded
    @POST("api/confirm-request")
    Call<StatusResponse> confirmRequest(@Field("api_token") String apiToken, @Field("sender_id") int senderId, @Field("confirm_id") int confirmId);

    @FormUrlEncoded
    @POST("api/start-the-trip")
    Call<StartStripResponse> startTheTrip(@Field("api_token") String apiToken);

    @FormUrlEncoded
    @POST("users/show")
    Call<UserInfo> getUserInfo(@Field("api_token") String apiToken, @Field("user_id") int userId);

    @FormUrlEncoded
    @POST("api/rating")
    Call<StatusResponse> ratingUserTogether(@Field("api_token") String apiToken, @Field("journey_id") int journeyId,
                                            @Field("rating_value") int ratingValue, @Field("comment") String comment);

    @FormUrlEncoded
    @POST("api/end-the-trip")
    Call<StatusResponse> endTheTrip(@Field("api_token") String apiToken, @Field("journey_id") int journeyId);

    /*{
    "status": {
        "error": 0,
        "message": "Success"
    },
    "user_history_info": [
        {
            "success_journey": {
                "driver": [
                    {
                        "journey": {
                            "id": 31,
                            "rating_value": 5,
                            "start_time": {
                                "date": "2017-12-13 02:31:44.000000",
                                "timezone_type": 3,
                                "timezone": "UTC"
                            },
                            "finish_time": "2017-12-13 03:43:53",
                            "cancel_time": null,
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
                    }
                ],
                "hiker": []
            },
            "fail_journey": {
                "driver": [],
                "hiker": []
            }
        }
    ]
}*/
    @FormUrlEncoded
    @POST("users/show-history")
    Call<History> getHistory(@Field("api_token") String apiToken);
}
