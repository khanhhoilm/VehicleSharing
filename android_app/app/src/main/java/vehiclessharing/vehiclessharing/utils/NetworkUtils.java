package vehiclessharing.vehiclessharing.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

public class NetworkUtils {

    private NetworkUtils() {
    }

    public static boolean isNetworkConnected(Context mContext) {
        boolean rsCheck=false;
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting())
        {
            rsCheck=true;
        }
        return rsCheck;
    }
}
