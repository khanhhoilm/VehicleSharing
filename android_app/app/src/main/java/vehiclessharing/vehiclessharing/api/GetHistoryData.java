package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.History;
import vehiclessharing.vehiclessharing.model.UserHistoryInfo;

/**
 * Created by Hihihehe on 12/13/2017.
 */

public class GetHistoryData {
    private static HistoryCallback historyCallback;
    private static RestManager restManager;
    private static GetHistoryData getHistoryData;

    public static GetHistoryData getInstance(HistoryCallback callback) {
        historyCallback = callback;
        return new GetHistoryData();
    }

    public void getHistory(String apiToken){
        restManager.getApiService().getHistory(apiToken).enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful()&& response.body().getStatus().getError()==0){
                    historyCallback.getHistorySuccess(response.body().getUserHistoryInfo().get(0));
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                historyCallback.getHistoryFailured("onFailure");
            }
        });
    }
    public interface HistoryCallback {
        void getHistorySuccess(UserHistoryInfo userHistoryInfo);

        void getHistoryFailured(String message);
    }
}
