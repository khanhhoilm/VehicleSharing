package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

public class EndTheTripAPI {
    private RestManager mRestManager;
    private EndTheTripAPI.EndTripRequestCallback mEndTripInterfaceCallback;

    public EndTheTripAPI(EndTripRequestCallback requestCallback) {
        mRestManager = new RestManager();
        this.mEndTripInterfaceCallback = requestCallback;
    }

    public void endTheTripWithUserTogether(String apiToken, int journeyId) {
        mRestManager.getApiService().endTheTrip(apiToken, journeyId).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful()) {
                    if( response.body().getStatus().getError() == 0) {
                        mEndTripInterfaceCallback.endTripSuccess();
                    }else if (response.body().getStatus().getError()==3){
                        mEndTripInterfaceCallback.endTripFailureBecauseDanger();
                    }else {
                        mEndTripInterfaceCallback.endTripSuccess();
                    }
                } else {
                    mEndTripInterfaceCallback.endTripFailure("unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                mEndTripInterfaceCallback.endTripFailure("onFailure");
            }
        });
    }

    public interface EndTripRequestCallback {
        void endTripSuccess();

        void endTripFailure(String message);

        void endTripFailureBecauseDanger();
    }
}
