package vehiclessharing.vehiclessharing.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.ActiveUser;
import vehiclessharing.vehiclessharing.model.RequestResult;

/**
 * Created by Hihihehe on 12/18/2017.
 */

public class UpdateListActiveUser {
    private static RestManager restManager;
    private static UpdateListActiveUser updateListActiveUser;
    private static UpdateListActiveUserCallback updateListActiveUserCallback;

    public static UpdateListActiveUser getInstance(UpdateListActiveUserCallback callback){
        updateListActiveUserCallback=callback;
        restManager=new RestManager();
        return new UpdateListActiveUser();
    }

    public void getListUpdate(String apiToken, int vehicleType){
        restManager.getApiService().updateListActiveUser(apiToken,vehicleType).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                if(response.isSuccessful()&&response.body().getStatus().getError()==0&&response.body().getActiveUsers()!=null&&response.body().getActiveUsers().size()>0){
                    updateListActiveUserCallback.getListActiveUserUpdateSuccess(response.body().getActiveUsers());
                }else {
                    updateListActiveUserCallback.getListActiveUserFailure("Không có user nào");
                }
            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
                updateListActiveUserCallback.getListActiveUserFailure("onFailure");
            }
        });
    }


    public interface UpdateListActiveUserCallback{
        void getListActiveUserUpdateSuccess(List<ActiveUser> activeUsers);
        void getListActiveUserFailure(String message);
    }
}
