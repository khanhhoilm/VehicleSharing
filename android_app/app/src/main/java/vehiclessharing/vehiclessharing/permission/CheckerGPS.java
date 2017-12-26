package vehiclessharing.vehiclessharing.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class CheckerGPS {
    public static Context mContext;//context
    public static Activity mActivity;
    final private static int REQ_PERMISSION = 20;// Value permission locaiton

    public CheckerGPS(){

    }

    public CheckerGPS(Context context,Activity activity){
        mContext = context;
        mActivity=activity;
    }

    /**
     * Check permission to using location
     * @return true - can
     */
    public boolean checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {


                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{  Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQ_PERMISSION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{  Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQ_PERMISSION);
            }
            return false;
        } else {
            //mGoogleMap.setMyLocationEnabled(true);
            return true;
        }
    }


    /**
     * Check permission to using location for setMyLocationEnable (Point blue in google map)
     * @return true - can
     */
    public boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return false;
        } else {
            return true;
        }
    }

    public boolean getPermissionLocal() {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(mActivity, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_PERMISSION);
            return false;
        }
    }


}
