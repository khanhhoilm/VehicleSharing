package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

public class RatingUserTogetherAPI {
    private RestManager mRestManager;
    private RatingCallback mRatingInterfaceCallback;

    public RatingUserTogetherAPI(RatingCallback ratingCallback) {
        this.mRatingInterfaceCallback = ratingCallback;
        mRestManager=new RestManager();
    }

    public void rating(String apiToken, int journeyId, final float ratingValue, String comment) {
        mRestManager.getApiService().ratingUserTogether(apiToken, journeyId, ratingValue, comment).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    mRatingInterfaceCallback.ratingSuccess();
                } else {
                    mRatingInterfaceCallback.ratingFailure("Response unsucessful ");
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                mRatingInterfaceCallback.ratingFailure("onFailure");
            }
        });
    }

    public interface RatingCallback {
        void ratingSuccess();

        void ratingFailure(String message);
    }
}
