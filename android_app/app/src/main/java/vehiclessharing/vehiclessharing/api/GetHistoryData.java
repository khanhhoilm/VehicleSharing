package vehiclessharing.vehiclessharing.api;

import android.os.Handler;
import android.os.HandlerThread;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.History;
import vehiclessharing.vehiclessharing.model.UserHistoryInfo;

/**
 * Created by Hihihehe on 12/13/2017.
 */

public class GetHistoryData {
    private static HistoryDriverCallback historyDriverCallback;
    private static HistoryHikerCallback historyHikerCallback;
    private static RestManager restManager;
    private static GetHistoryData getHistoryData;
    private int page;
    private boolean isCalling=false;

    public static GetHistoryData getInstance(HistoryDriverCallback callback) {
        historyDriverCallback = callback;
        return new GetHistoryData();
    }

    public static GetHistoryData getInstance(HistoryHikerCallback callback) {
        historyHikerCallback = callback;
        return new GetHistoryData();
    }

    public void getHistoryDriver(final String apiToken, final String userType){
      restManager.getApiService().getHistory(apiToken,userType).enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful()&& response.body().getStatus().getError()==0){
                    historyDriverCallback.getHistoryDriverSuccess(response.body().getUserHistoryInfo().get(0),userType);
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                historyDriverCallback.getHistoryFailured("onFailure");
            }
        });
    }
    public void getHistoryHiker(final String apiToken, final String userType){
        restManager.getApiService().getHistory(apiToken,userType).enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful()&& response.body().getStatus().getError()==0){
                    historyHikerCallback.getHistoryHikerSuccess(response.body().getUserHistoryInfo().get(0),userType);
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                historyHikerCallback.getHistoryFailured("onFailure");
            }
        });
    }
    public interface HistoryDriverCallback {
        void getHistoryDriverSuccess(UserHistoryInfo userHistoryInfo,String userType);

        void getHistoryFailured(String message);
    }
    public interface HistoryHikerCallback {
        void getHistoryHikerSuccess(UserHistoryInfo userHistoryInfo,String userType);

        void getHistoryFailured(String message);
    }

}
