package vehiclessharing.vehiclessharing.push;

import android.content.SharedPreferences;
import android.net.http.RequestQueue;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class CustomFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = CustomFirebaseInstanceIDService.class.getSimpleName();
    public static String DEVICE_TOKEN_REFRESH = "device_token_refresh";
    public static String DEVICE_TOKEN = "device_token";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token Value: " + refreshedToken);
        SharedPreferences deviceTokenShare = getSharedPreferences(DEVICE_TOKEN_REFRESH, MODE_PRIVATE);
        SharedPreferences.Editor editor = deviceTokenShare.edit();
        editor.putString(DEVICE_TOKEN, refreshedToken);
        editor.commit();
    }
}
