package vehiclessharing.vehiclessharing.api;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.PathImageUpload;
import vehiclessharing.vehiclessharing.model.StatusResponse;

/**
 * Created by Hihihehe on 1/11/2018.
 */

public class UpdateImageAPI {
    private RestUploadFile mRestUploadFile;
    private RestManager mRestManager;
    private UpdateImageCallback mUpdateImageCallback;

    public UpdateImageAPI(UpdateImageCallback callback) {
        mUpdateImageCallback=callback;
        mRestManager=new RestManager();
        mRestUploadFile=new RestUploadFile();
    }

    public void getURLImage(MultipartBody.Part body){
        final Call<PathImageUpload> req = mRestUploadFile.getApiService().postImage(body);
        req.enqueue(new Callback<PathImageUpload>() {
            @Override
            public void onResponse(Call<PathImageUpload> call, Response<PathImageUpload> response) {
                if (response.code() == 200) {
                    mUpdateImageCallback.getURLImageSuccess(response.body().getFilePath());
                }else {
                    mUpdateImageCallback.getURLImageFailure();
                }
            }

            @Override
            public void onFailure(Call<PathImageUpload> call, Throwable t) {
                mUpdateImageCallback.updateImageFailure();
            }
        });

    }

    public void updateInfoUser(String apiToken, String name, String email,String avatarLink, int gender,String address, String birthday){
        mRestManager.getApiService().updateInfoUser(apiToken, name,email,avatarLink, gender, address, birthday).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    mUpdateImageCallback.updateInfoUserSuccess();
                }else {
                    mUpdateImageCallback.updateInfoUserFailure();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {

                mUpdateImageCallback.updateInfoUserFailure();
            }
        });
    }
    public interface UpdateImageCallback{
        void getURLImageSuccess(String url);
        void getURLImageFailure();

        void updateImageSuccess();
        void updateImageFailure();

        void updateInfoUserSuccess();
        void updateInfoUserFailure();
    }
}
