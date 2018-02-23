package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

public class FavoriteUserAPI {
    private RestManager mRestManager;
    private FavoriteCallback mFavoriteIntefaceCallback;

    public FavoriteUserAPI(FavoriteCallback favoriteCallback) {
        this.mFavoriteIntefaceCallback = favoriteCallback;
        mRestManager=new RestManager();
    }

    public void like(String apiToken, int partnerId) {
        mRestManager.getApiService().addToFavorite(apiToken, partnerId).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    mFavoriteIntefaceCallback.favoriteSuccess();
                } else {
                    mFavoriteIntefaceCallback.favoriteFailure();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                mFavoriteIntefaceCallback.favoriteFailure();
            }
        });
    }

    public interface FavoriteCallback {
        void favoriteSuccess();

        void favoriteFailure();
    }
}
