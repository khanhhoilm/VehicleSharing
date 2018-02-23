package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.ResultSendRequest;

public class SendRequestAPI {
    private RestManager mRestManager;
    private SendRequestInterface mSendRequestInterface;

    public SendRequestAPI(SendRequestInterface sendRequestInterface) {
        this.mSendRequestInterface = sendRequestInterface;
        mRestManager=new RestManager();
    }

    public void sendRequestToChosenUser(String apiToken, int receiverId, String note){
        mRestManager.getApiService().sendRequestTogether(apiToken,receiverId,note).enqueue(new Callback<ResultSendRequest>() {
            @Override
            public void onResponse(Call<ResultSendRequest> call, Response<ResultSendRequest> response) {
                if(response.isSuccessful() && response.body().getStatus().getError() == 0){
                    mSendRequestInterface.sendRequestSuccess();}
                else {
                    mSendRequestInterface.sendRequestFailure();
                }
            }
            @Override
            public void onFailure(Call<ResultSendRequest> call, Throwable t) {
                mSendRequestInterface.sendRequestFailure();
            }
        });
    }

    public interface SendRequestInterface{
        void sendRequestSuccess();
        void sendRequestFailure();
    }

}
