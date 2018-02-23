package vehiclessharing.vehiclessharing.api;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import vehiclessharing.vehiclessharing.model.History;
import vehiclessharing.vehiclessharing.model.PathImageUpload;
import vehiclessharing.vehiclessharing.model.RequestResult;
import vehiclessharing.vehiclessharing.model.ResultSendRequest;
import vehiclessharing.vehiclessharing.model.SignInResponse;
import vehiclessharing.vehiclessharing.model.StartStripResponse;
import vehiclessharing.vehiclessharing.model.StatusResponse;
import vehiclessharing.vehiclessharing.model.UserInfo;


public interface ApiService {

    // String BASE_URL = "http://vehiclessharing.viecit.co/";
    String BASE_URL = "https://vehicle-sharing.herokuapp.com/";
    String BASE_URL_UPLOAD_IMAGE="https://upload-file-server.herokuapp.com/";

    @POST("users/register")
    @FormUrlEncoded
    Call<StatusResponse> signUp(@Field("phone") String phone,
                                @Field("name") String name,
                                @Field("password") String password,
                                @Field("gender") int gender);

    @POST("users/signin")
    @FormUrlEncoded
    Call<SignInResponse> signIn(@Field("phone") String phone,
                                @Field("password") String password);

    @POST("users/signout")
    @FormUrlEncoded
    Call<StatusResponse> signOut(@Field("api_token") String apiToken);

    @POST("users/update")
    @FormUrlEncoded
    Call<StatusResponse> updateInfoUser(@Field("api_token") String apiToken, @Field("name") String name, @Field("email") String email,
                                        @Field("avatar_link") String avatarLink ,@Field("gender") int gender,
                                        @Field("address") String address, @Field("birthday") String birthday);

    @POST("users/update")
    @FormUrlEncoded
    Call<StatusResponse> updateAvatar (@Field("api_token") String apiToken,
                                        @Field("avatar_link") String avatarLink);

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("api/request")
    Call<RequestResult> registerRequest(@Field("user_id") int userId, @Field("source_location") String sourceLocation,
                                        @Field("destination_location") String desLocation, @Field("time_start") String timeStart,
                                        @Field("api_token") String session, @Field("device_id") String devideId, @Field("vehicle_type") int vehicleType,
                                        @Field("device_token") String deviceToken,@Field("current_time") String currenttime);


    @FormUrlEncoded
    @POST("api/get-active-request")
    Call<RequestResult> updateListActiveUser(@Field("api_token") String apiToken, @Field("vehicle_type") int vehicleType,@Field("current_time") String currentTime);

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
    Call<StartStripResponse> startTheTrip(@Field("api_token") String apiToken,@Field("partner_id") int parnerId,@Field("vehicle_type ") int vehicleType);

    @FormUrlEncoded
    @POST("api/cancel-the-trip")
    Call<StatusResponse> cancelTrip(@Field("api_token") String apiToken,@Field("vehicle_type") int vehicleType,@Field("comment") String comment);

    @FormUrlEncoded
    @POST("users/show")
    Call<UserInfo> getMyInfo(@Field("api_token") String apiToken);


    @FormUrlEncoded
    @POST("users/show")
    Call<UserInfo> getUserInfo(@Field("api_token") String apiToken, @Field("user_id") int userId);

    @FormUrlEncoded
    @POST("api/rating")
    Call<StatusResponse> ratingUserTogether(@Field("api_token") String apiToken, @Field("journey_id") int journeyId,
                                            @Field("rating_value") float ratingValue, @Field("comment") String comment);

    @FormUrlEncoded
    @POST("api/end-the-trip")
    Call<StatusResponse> endTheTrip(@Field("api_token") String apiToken, @Field("journey_id") int journeyId);

    @FormUrlEncoded
    @POST("users/show-history")
    Call<History> getHistory(@Field("api_token") String apiToken,@Field("user_type") String userType);

    @FormUrlEncoded
    @POST("users/show-history")
    Call<History> getHistoryAnotherUser(@Field("api_token") String apiToken,@Field("user_type") String userType,@Field("user_id") int anotherUserId);

    @FormUrlEncoded
    @POST("users/add-to-favorite")
    Call<StatusResponse> addToFavorite(@Field("api_token") String apiToken,@Field("partner_id") int partnerId);

    @Multipart
    @POST("upload/")
    Call<PathImageUpload> postImage(@Part MultipartBody.Part imagex);

    @FormUrlEncoded
    @POST("api/report")
    Call<StatusResponse> sendSOS(@Field("api_token")String apiToken,@Field("vehicle_type") int vehicleType, @Field("address") String address,@Field("comment") String comment);
}
