package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.InfomationUser;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.model.UserInfo;

/**
 * Created by Hihihehe on 12/4/2017.
 */

public class UserInfoAPI {
    private RestManager mRestManager;
    private GetInfoUserCallback mGetInfoUserInterfaceCallback;

    public UserInfoAPI(GetInfoUserCallback getInfoUserCallback) {
        this.mGetInfoUserInterfaceCallback = getInfoUserCallback;
        mRestManager=new RestManager();
    }

    public void getUserInfoFromAPI(String apiToken, int userId) {
        mRestManager.getApiService().getUserInfo(apiToken,userId).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.isSuccessful()&&response.body().getStatus().getError()==0) {
                    mGetInfoUserInterfaceCallback.getInfoUserSuccess(response.body().getInfomationUser());
                }else {
                    mGetInfoUserInterfaceCallback.getUserInfoFailure("Response unsuccessful or info user null");
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                mGetInfoUserInterfaceCallback.getUserInfoFailure("onFailure");
            }
        });
    }
    public void getMyInfo(String apiToken) {
        mRestManager.getApiService().getMyInfo(apiToken).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.isSuccessful()&&response.body().getStatus().getError()==0) {
                    mGetInfoUserInterfaceCallback.getInfoUserSuccess(response.body().getInfomationUser());
                }else {
                    mGetInfoUserInterfaceCallback.getUserInfoFailure("Response unsuccessful or info user null");
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                mGetInfoUserInterfaceCallback.getUserInfoFailure("onFailure");
            }
        });
    }


    public interface GetInfoUserCallback {
        void getInfoUserSuccess(InfomationUser userInfo);

        void getUserInfoFailure(String message);
    }
}
