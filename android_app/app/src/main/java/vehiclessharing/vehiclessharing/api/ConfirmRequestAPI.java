package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

/**
 * Created by Hihihehe on 11/22/2017.
 */

public class ConfirmRequestAPI {
    private RestManager restManager;
    private static ConfirmRequestCallback confirmRequestCallback;
    //   private Dialog dialogConfirmSend;


    public static ConfirmRequestAPI getInstance(ConfirmRequestCallback callBack) {

        confirmRequestCallback = callBack;
        return new ConfirmRequestAPI();
    }

    public void sendConfirmRequest(String apiToken, int senderId, final int confirmId)
    {
        restManager.getApiService().confirmRequest(apiToken,senderId,confirmId).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if(response.isSuccessful()&&response.body().getStatus().getError()==0) {
                    //error=0 is success and 1 is failure
                    confirmRequestCallback.confirmRequestSuccess(confirmId);
                }else {
                    confirmRequestCallback.confirmRequestFailure(response.body().getStatus().getMessage(),confirmId);
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                confirmRequestCallback.confirmRequestFailure("OnFailure",confirmId);
            }
        });
    }


    public interface ConfirmRequestCallback {
        void confirmRequestSuccess(int confirmId);

        void confirmRequestFailure(String message,int confirmId);

     }
}
