package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

/**
 * Created by Hihihehe on 12/11/2017.
 */

public class EndTheTrip {
    private static EndTheTrip ourInstance = null;
    private static RestManager restManager;
    private static EndTheTrip.EndTripRequestCallback requestCallback;

    public static EndTheTrip getInstance(EndTheTrip.EndTripRequestCallback callback) {
        requestCallback = callback;
        restManager = new RestManager();
        ourInstance = new EndTheTrip();
        return ourInstance;
    }

    public void endTheTripWithUserTogether(String apiToken, int journeyId) {
        restManager.getApiService().endTheTrip(apiToken, journeyId).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    requestCallback.endTripSuccess();
                } else {
                    requestCallback.endTripFailure("unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                requestCallback.endTripFailure("onFailure");
            }
        });
    }

    public interface EndTripRequestCallback {
        void endTripSuccess();

        void endTripFailure(String message);

    }
}
