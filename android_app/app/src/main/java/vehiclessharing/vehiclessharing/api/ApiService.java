package vehiclessharing.vehiclessharing.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import vehiclessharing.vehiclessharing.model.RequestResult;
import vehiclessharing.vehiclessharing.model.SignInResult;
import vehiclessharing.vehiclessharing.model.SignUpResult;


public interface ApiService {

    String BASE_URL = "http://vehiclessharing.viecit.co/";

    @POST("users")
    @FormUrlEncoded
    Call<SignUpResult> signUp(@Field("phone") String phone,
                              @Field("name") String name,
                              @Field("password") String password,
                              @Field("gender") int gender);

    @POST("users/signin")
    @FormUrlEncoded
    Call<SignInResult> signIn(@Field("phone") String phone,
                              @Field("password") String password);
/*
     + user_id
 + source_location (lat - long )
 + destination_location ( lat-long )
 + time_start
 + api_token
 + device_id
+ vehicle_type
    */
    @POST("api/request")
    Call<RequestResult> registerARequest(@Query("user_id") int userId, @Query("source_location") String sourceLocation,
                                         @Query("destination_location") String desLocation, @Query("time_start") String timeStart,
                                         @Query("api_token") String session, @Query("device_id") String devideId, @Query("vehicle_type") int vehicleType);

   /* @Multipart
    @POST("api/request")
    Call<RequestResult> registerRequest(@Part("user_id") int userId, @Part("source_location") RequestBody sourceLocation,
                                        @Part("destination_location") RequestBody desLocation, @Part("time_start") String timeStart,
                                        @Part("api_token") String session, @Part("device_id") String devideId, @Part("vehicle_type") int vehicleType);
*/
    @FormUrlEncoded
    @POST("api/request")
    Call<RequestResult> registerRequest(@Field("user_id") int userId, @Field("source_location") String sourceLocation,
                                        @Field("destination_location") String desLocation, @Field("time_start") String timeStart,
                                        @Field("api_token") String session, @Field("device_id") String devideId, @Field("vehicle_type") int vehicleType);

    /*
* ultipart
@POST("/api/v2/search_bounds")
void search_bounds(@Part("bounds") ArrayList<Bounds> bounds, @Part("filters") SearchCriteria searchCriteria, Callback<SearchResults> cb);
and
*/

   /*For now i created my own FormBody.Builder like i said before
https://github.com/square/retrofit/issues/1407
 @POST(LOGIN_URL)
 Call<BaseResponse> loginUser(@Body RequestBody body);
 RequestBody formBody = new FormBody.Builder()
                .add(EMAIL, emailId.getText().toString())
                .add(PASSWORD, password.getText().toString())
                .build();

 Call<BaseResponse> loginCall = RetorfitService.service.loginUser(formBody);*/
    /*@GET("/appdev/catpage?token=dee03981&cid=2&limit=10&page=1")
    Call<List<ItemCategory>> getItemByCategory();

    @GET("/appdev/catpage?token=dee03981")
    Call<List<ItemCategory>> getItemByCategoryDynamic(@Query("cid") int cid, @Query("limit") int limit, @Query("page") int page, @Query("prior") int prior);

    @GET("/appdev/getobjectdetail?token=dee03981")
    Call<ItemDetail> getArticleDetail(@Query("id") String id);*/


}
