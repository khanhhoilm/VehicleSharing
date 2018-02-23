package vehiclessharing.vehiclessharing.api;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

public class CancelRequestAPI {

    private RestManager mRestManager;
    private CancelRequestCallBack mCancelRequestCallBack;

    public CancelRequestAPI(CancelRequestCallBack cancelRequestCallBack) {
        this.mCancelRequestCallBack = cancelRequestCallBack;
        mRestManager=new RestManager();
    }

    public void cancelRequest(String apiToken) {
        mRestManager.getApiService().cancelRequest(apiToken).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().getError() == 0) {
                    mCancelRequestCallBack.cancelRequestSuccess(true);
                } else {
                    mCancelRequestCallBack.cancelRequestSuccess(false);
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Log.d("CancelRequest", "failed");
                mCancelRequestCallBack.cancelRequestFailed();
            }
        });
    }


    public interface CancelRequestCallBack {
        void cancelRequestSuccess(boolean success);
        void cancelRequestFailed();
    }

}
