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
    private static final StartTripAPI ourInstance = new StartTripAPI();
    private static RestManager restManager;
    private static StartTripRequestCallback requestCallback;

    public static StartTripAPI getInstance(StartTripRequestCallback callback) {
        requestCallback = callback;
        restManager = new RestManager();
        return ourInstance;
    }

    private StartTripAPI() {
    }

    public void sendNotiStartTripToUserTogether(String apiToken,int partnerId) {
        restManager.getApiService().startTheTrip(apiToken,partnerId).enqueue(new Callback<StartStripResponse>() {
            @Override
            public void onResponse(Call<StartStripResponse> call, Response<StartStripResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    requestCallback.startTripSuccess(response.body().getJourneyInfo());
                } else {
                    requestCallback.startTripFailure(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<StartStripResponse> call, Throwable t) {
                requestCallback.startTripFailure("onFailure");
            }
        });
    }

    public interface StartTripRequestCallback {
        void startTripSuccess(JourneyInfo journeyInfo);

        void startTripFailure(String message);

    }
}
