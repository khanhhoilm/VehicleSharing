package vehiclessharing.vehiclessharing.controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vehiclessharing.vehiclessharing.api.ApiService;

public class RestManager {

    private static ApiService apiService;


    public static ApiService getApiService() {
        Gson gson = new GsonBuilder().create();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                okhttp3.Request original = chain.request();

                okhttp3.Request request = original.newBuilder()
                       // .header("User-Agent",headerClient)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client= httpClient.build();


        if (apiService == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiService.BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

}
