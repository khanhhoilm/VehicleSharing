package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import vehiclessharing.vehiclessharing.model.RequestResult;

public class AddRequestAPI {
    private RestManager mRestManager;
    private AddRequestInterfaceCallback mAddRequestInterfaceCallback;

    public AddRequestAPI(AddRequestInterfaceCallback addRequestInterfaceCallback) {
        this.mAddRequestInterfaceCallback = addRequestInterfaceCallback;
        mRestManager = new RestManager();
    }

    public void addRequest(int userId, String sourLocation, String desLocation, String time, String sessionId, String deviceId, int vehicleType, String fcmToken, String currentTime) {
        mRestManager.getApiService().registerRequest(userId, sourLocation, desLocation, time, sessionId, deviceId,
                vehicleType, fcmToken, currentTime).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().getError() == 0) {
                    mAddRequestInterfaceCallback.addRequestSuccess(response.body());
                }else {
                    mAddRequestInterfaceCallback.addRequestUnsuccess(response.code());
                }
            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
                mAddRequestInterfaceCallback.addRequestFailure();

            }
        });
    }

    public interface AddRequestInterfaceCallback {
        void addRequestSuccess(RequestResult requestResult);

        void addRequestUnsuccess(int statusCode);

        void addRequestFailure();
    }
}
