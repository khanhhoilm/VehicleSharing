package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

public class ConfirmRequestAPI {
    private RestManager mRestManager;
    private ConfirmRequestCallback mConfirmRequestCallback;

    public ConfirmRequestAPI(ConfirmRequestCallback confirmRequestCallback) {
        this.mConfirmRequestCallback = confirmRequestCallback;
        mRestManager=new RestManager();
    }

    public void sendConfirmRequest(String apiToken, int senderId, final int confirmId)
    {
        mRestManager.getApiService().confirmRequest(apiToken,senderId,confirmId).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if(response.isSuccessful()&&response.body().getStatus().getError()==0) {
                    //error=0 is success and 1 is failure
                    mConfirmRequestCallback.confirmRequestSuccess(confirmId);
                }else {
                    mConfirmRequestCallback.confirmRequestFailure(String.valueOf(response.body().getStatus().getError()),confirmId);
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                mConfirmRequestCallback.confirmRequestFailure("OnFailure ",confirmId);
            }
        });
    }


    public interface ConfirmRequestCallback {
        void confirmRequestSuccess(int confirmId);

        void confirmRequestFailure(String message,int confirmId);

     }
}
