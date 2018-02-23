package vehiclessharing.vehiclessharing.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hihihehe on 1/10/2018.
 */

public class RestUploadFile {
        private ApiService mApiService;

        public ApiService getApiService() {
            Gson gson = new GsonBuilder().create();
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS);
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
            OkHttpClient client = httpClient.build();

            if (mApiService == null) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiService.BASE_URL_UPLOAD_IMAGE).client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                mApiService = retrofit.create(ApiService.class);
            }
            return mApiService;

        }
}
