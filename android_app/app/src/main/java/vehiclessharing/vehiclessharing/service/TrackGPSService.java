package vehiclessharing.vehiclessharing.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import vehiclessharing.vehiclessharing.constant.Utils;

public class TrackGPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    public static boolean isRunning = false;//Instance to check service is running or not
    private GoogleMap mGoogleMap;
    private Context mContext;
    boolean zoomOneTime = true;//Just zoom 1 time

    public static Location mLocation = null;


    private static int CONTROLL_ON = 1;
    private static int CONTROLL_OFF = -1;

    final private static int REQ_PERMISSION = 20;// Value permission locaiton


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("servicelocation", "create");
        buildGoogleApiClient();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MyService", "Received start id " + startId + ": " + intent);
        isRunning = true;
        return START_STICKY; // run until explicitly stopped.
    }

    /**
     * Build googleAPI Client
     */
    public synchronized void buildGoogleApiClient() {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.d("bug1111", "1");
            mGoogleApiClient.connect();
        } catch (Exception e) {
            Log.e(Utils.TAG_ERROR_LOCATION, String.valueOf(e.getMessage()));
        }
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
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {


                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQ_PERMISSION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
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
    public boolean checkPermission(Context mContext) {
        if (ActivityCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return false;
        } else {
            return true;
        }
    }

    /**
     * Register / Unregister the listener location changed
     * @param value
     */
    public void controllonLocationChanged(int value) {
        if (value == CONTROLL_OFF)// unregister the listener
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (value == CONTROLL_ON) {//register the listener to listen location change
            if (checkLocationPermission()) {
                if (mGoogleMap != null) mGoogleMap.setMyLocationEnabled(true);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        }
    }

    /**
     * Create location request
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(5); //5 meter
//        controllonLocationChanged(CONTROLL_ON);//Enable Listener
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("service_aaaaaaa", "connect");
        try {
            Toast.makeText(this, "Location  service connected", Toast.LENGTH_SHORT).show();

            createLocationRequest();//Create location request
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        } catch (Throwable t) { //you should always ultimately catch all exceptions in timer tasks.
            Log.e("Google APi Connected", "Google APi Connected Failed.", t);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Every n s(setInterval + setFastest) will get location. This function called when the location has changed.
     * @param location current loacation
     */
    @Override
    public void onLocationChanged(Location location) {
        if(zoomOneTime && mGoogleMap != null && mGoogleMap.getCameraPosition().zoom == 2.0){//Just zoom 1 time
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),11));
            zoomOneTime =false;
        }
        Log.d("ONLOCATIONCHANGE","onLocationChanged");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
