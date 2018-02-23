package vehiclessharing.vehiclessharing.api;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.History;
import vehiclessharing.vehiclessharing.model.UserHistoryInfo;

public class HistoryDataAPI {
    private HistoryDriverCallback mHistoryDriverCallback;
    private HistoryHikerCallback mHistoryHikerCallback;
    private RestManager mRestManager, mHistoryRest;

    private HistoryDataAPI mGetHistoryData;
    private int page;
    private boolean isCalling = false;

    public HistoryDataAPI(HistoryDriverCallback historyDriverCallback) {
        this.mHistoryDriverCallback = historyDriverCallback;
        mRestManager=new RestManager();
    }

    public HistoryDataAPI(HistoryHikerCallback historyHikerCallback) {
        this.mHistoryHikerCallback = historyHikerCallback;
        mRestManager=new RestManager();
    }

    public void getHistoryDriver(final String apiToken, final String userType) {
        mRestManager.getApiService().getHistory(apiToken, userType).enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    mHistoryDriverCallback.getHistoryDriverSuccess(response.body().getUserHistoryInfo().get(0), userType);
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                mHistoryDriverCallback.getHistoryFailured("onFailure");
            }
        });
    }

    public void getHistoryHiker(final String apiToken, final String userType) {
        mRestManager.getApiService().getHistory(apiToken, userType).enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    mHistoryHikerCallback.getHistoryHikerSuccess(response.body().getUserHistoryInfo().get(0), userType);
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                mHistoryHikerCallback.getHistoryFailured("onFailure");
            }
        });
    }


    public void getHistoryAnotherDriver(final String apiToken, final String userType, int anotherUserId) {
        mHistoryRest.getApiService().getHistoryAnotherUser(apiToken, userType, anotherUserId).enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    mHistoryDriverCallback.getHistoryDriverSuccess(response.body().getUserHistoryInfo().get(0), userType);
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                mHistoryDriverCallback.getHistoryFailured("onFailure");
            }
        });

    }

    public void getHistoryAnotherHiker(final String apiToken, final String userType, int anotherUserId) {
        try {
            mHistoryRest.getApiService().getHistoryAnotherUser(apiToken, userType, anotherUserId).enqueue(new Callback<History>() {
                @Override
                public void onResponse(Call<History> call, Response<History> response) {
                    if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                        mHistoryHikerCallback.getHistoryHikerSuccess(response.body().getUserHistoryInfo().get(0), userType);
                    }
                }

                @Override
                public void onFailure(Call<History> call, Throwable t) {
                    mHistoryHikerCallback.getHistoryFailured("onFailure");
                }
            });
        } catch (Exception e) {
            Log.d("exception", "gethistory");
        }
    }

    public interface HistoryDriverCallback {
        void getHistoryDriverSuccess(UserHistoryInfo userHistoryInfo, String userType);

        void getHistoryFailured(String message);
    }

    public interface HistoryHikerCallback {
        void getHistoryHikerSuccess(UserHistoryInfo userHistoryInfo, String userType);

        void getHistoryFailured(String message);
    }

}
