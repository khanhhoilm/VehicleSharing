package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

/**
 * Created by Hihihehe on 12/5/2017.
 */

public class RatingUserTogether {
    private static RestManager restManager;
    private static RatingCallback ratingCallback;


    public static RatingUserTogether getInstance(RatingCallback callBack) {

        ratingCallback = callBack;
        restManager=new RestManager();
        return new RatingUserTogether();
    }

    public void rating(String apiToken, int journeyId, final float ratingValue, String comment) {
        restManager.getApiService().ratingUserTogether(apiToken, journeyId, ratingValue, comment).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    ratingCallback.ratingSuccess();
                } else {
                    ratingCallback.ratingFailure("Response unsucessful status error code = ");
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                ratingCallback.ratingFailure("onFailure ");
            }
        });
    }

    public interface RatingCallback {
        void ratingSuccess();
        void ratingFailure(String message);
    }
}
