package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;


public class SignUpAPI {
    private RestManager mRestManager;
    private SignUpInterface mSignUpInterface;
    private static SignUpAPI instance;
    public static SignUpAPI getInstance(SignUpInterface signUpInterface){
        if(instance==null){
            instance=new SignUpAPI(signUpInterface);
        }
        return instance;
    }

    private SignUpAPI(SignUpInterface signUpInterface) {
        this.mSignUpInterface = signUpInterface;
        mRestManager=new RestManager();
    }

    public void signUp(String phone,String name, String password, int gender){
        mRestManager.getApiService().signUp(phone,name,password,gender).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if(response.isSuccessful()){
                    switch (response.body().getStatus().getError()) {
                        case 0:
                            mSignUpInterface.signUpSuccess();
                            break;
                        case 1:
                            mSignUpInterface.signUpUnsuccess();
                            break;
                    }
                }else {
                    mSignUpInterface.signUpFailure();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                mSignUpInterface.signUpFailure();
            }
        });
    }

    public interface SignUpInterface{
        void signUpSuccess();
        void signUpUnsuccess();
        void signUpFailure();
    }
}
