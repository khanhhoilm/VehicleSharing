package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.JourneyInfo;
import vehiclessharing.vehiclessharing.model.StartStripResponse;

/**
 * Created by Hihihehe on 12/4/2017.
 */

public class StartTripAPI {
    private RestManager mRestManager;
    private StartTripRequestCallback mRequestIntefaceCallback;

    public StartTripAPI(StartTripRequestCallback requestCallback) {
        this.mRequestIntefaceCallback = requestCallback;
        mRestManager=new RestManager();
    }
    
    public void sendNotiStartTripToUserTogether(String apiToken,int partnerId,int vehiclesType) {
        mRestManager.getApiService().startTheTrip(apiToken,partnerId,vehiclesType).enqueue(new Callback<StartStripResponse>() {
            @Override
            public void onResponse(Call<StartStripResponse> call, Response<StartStripResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    mRequestIntefaceCallback.startTripSuccess(response.body().getJourneyInfo());
                } else {
                    mRequestIntefaceCallback.startTripFailure("start trip failed");
                }
            }

            @Override
            public void onFailure(Call<StartStripResponse> call, Throwable t) {
                mRequestIntefaceCallback.startTripFailure("onFailure");
            }
        });
    }

    public interface StartTripRequestCallback {
        void startTripSuccess(JourneyInfo journeyInfo);

        void startTripFailure(String message);

    }
}
