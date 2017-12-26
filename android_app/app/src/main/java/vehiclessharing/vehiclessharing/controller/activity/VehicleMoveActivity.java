package vehiclessharing.vehiclessharing.controller.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.EndTheTrip;
import vehiclessharing.vehiclessharing.api.GetUserInfo;
import vehiclessharing.vehiclessharing.api.StartTripAPI;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ConfirmRequest;
import vehiclessharing.vehiclessharing.model.JourneyInfo;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.permission.CheckInternetAndLocation;
import vehiclessharing.vehiclessharing.permission.CheckerGPS;
import vehiclessharing.vehiclessharing.push.CustomFirebaseMessagingService;
import vehiclessharing.vehiclessharing.service.AlarmReceiver;
import vehiclessharing.vehiclessharing.utils.DrawRoute;
import vehiclessharing.vehiclessharing.utils.Helper;

public class VehicleMoveActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,
        StartTripAPI.StartTripRequestCallback, GetUserInfo.GetInfoUserCallback, EndTheTrip.EndTripRequestCallback {

    public static String CALL_FROM_WHAT_ACTIVITY = "call_from_what_activity";

    public static String START_TRIP = "start_the_trip";
    public static String ALARM_START = "alarm_start";
    public static String CONFIRM_REQUEST = "confirm_request";
    public static String RECEIVE_CONFIRM_REQUEST = "receive_confirm_request";
    public static String JOURNEY_ID="journey_id";

    private GoogleMap mGoogleMap;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private RequestInfo myRequestInfo, yourRequestInfo;
    private int myUserId;
    private HashMap<String, Marker> listMarker;
    private Location lastLocation = null;
    private Marker mySourceMarker, hereMarker;
    private Location desLocation;
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private ConfirmRequest confirmRequest;
    private FloatingActionButton btnStartTrip;
    public static FloatingActionButton btnEndTrip;
    private String apiToken = "", phone = "";
    private int journeyId = 0;
    final private static int REQ_PERMISSION = 20;//Value request permission
    private CheckerGPS checkerGPS;
    private String callFrom = "";

    public static AlarmManager alarmManager;
    public static PendingIntent pendingIntent;
    private SharedPreferences sharedPreferencesScreen;
    private SharedPreferences.Editor editorScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_move);

        Bundle bundle = getIntent().getExtras();
        //data receiver null if move from ConfirmActity
        String dataReceive = bundle.getString(CustomFirebaseMessagingService.DATA_RECEIVE, "");
        callFrom = bundle.getString(CALL_FROM_WHAT_ACTIVITY, "");
        journeyId = bundle.getInt("journey_id", 0);
        if (!dataReceive.equals("")) {
            Gson gson = new Gson();
            confirmRequest = gson.fromJson(dataReceive, ConfirmRequest.class);
        }

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        sharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        myUserId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        apiToken = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        databaseHelper = new DatabaseHelper(this);

        myRequestInfo = databaseHelper.getRequestInfo(myUserId);
        yourRequestInfo = databaseHelper.getRequestInfoNotMe(myUserId);

        listMarker = new HashMap<>();
        /*if (confirmRequest != null && callFrom.equals("receive_confirm_request")) {
            GetUserInfo.getInstance(this).getUserInfoFromAPI(apiToken, confirmRequest.getUserId());
        }*/
        checkerGPS = new CheckerGPS(this, this);
        addControls();
        addEvents();
        if (!callFrom.equals(ALARM_START) && !callFrom.equals(START_TRIP)) {
            addAlarm();
        }
        editorScreen=sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME,MainActivity.WAIT_START_TRIP);
        editorScreen.commit();
    }

    private void addAlarm() {
        try {
            int hour;
            int minute;
            String[] time;
            if (myRequestInfo.getVehicleType() != 0) {
                time = myRequestInfo.getTimeStart().split(":");
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
            } else {
                time = yourRequestInfo.getTimeStart().split(":");
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent myIntent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    alarmManager.cancel(pendingIntent);
                }
            }, 3000);
        } catch (Exception e) {

        }
    }

    private void addEvents() {
        btnStartTrip.setOnClickListener(this);
        btnEndTrip.setOnClickListener(this);
    }

    private void addControls() {
        btnStartTrip = findViewById(R.id.btnStartStrip);

        btnEndTrip = findViewById(R.id.btnEndTrip);


        if (callFrom.equals(CONFIRM_REQUEST) || callFrom.equals(ALARM_START)) {
            if (VehicleMoveActivity.alarmManager != null && VehicleMoveActivity.pendingIntent != null) {
                VehicleMoveActivity.alarmManager.cancel(VehicleMoveActivity.pendingIntent);
            }
            if (myRequestInfo.getVehicleType() == 0) {
                showStartFloatingButton(true);
            } else {
                showStartFloatingButton(false);
            }
        } else if (callFrom.equals(RECEIVE_CONFIRM_REQUEST)) {
            if (confirmRequest.getVehicleType() != 0) {
              showStartFloatingButton(true);
            } else {
                showStartFloatingButton(false);
            }
        } else if (callFrom.equals(START_TRIP)) {

            editorScreen=sharedPreferencesScreen.edit();
            editorScreen.putInt(MainActivity.SCREEN_NAME,MainActivity.STARTED_TRIP);
            editorScreen.commit();
            showEndButton(true);

        }else {
           int screenName= sharedPreferencesScreen.getInt(MainActivity.SCREEN_NAME,0);
           if(screenName==MainActivity.WAIT_START_TRIP){
               if (myRequestInfo.getVehicleType() == 0) {
                   showStartFloatingButton(true);
               } else {
                   showStartFloatingButton(false);
               }
           }else if(screenName==MainActivity.STARTED_TRIP){
               showEndButton(true);
           }
        }
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }
    private void showStartFloatingButton(boolean show){
        if (show) {
            btnStartTrip.setVisibility(View.VISIBLE);
            btnEndTrip.setVisibility(View.GONE);
        } else {
            btnStartTrip.setVisibility(View.GONE);
            btnEndTrip.setVisibility(View.GONE);
        }
    }
    private void showEndButton(boolean show){
        btnStartTrip.setVisibility(View.GONE);
        btnEndTrip.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_trip, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.call) {
            if (!phone.equals("")) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //return TODO;

                }
                startActivity(callIntent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //add source and des marker for me
        if (myRequestInfo != null && myRequestInfo.getSourceLocation() != null && myRequestInfo.getDestLocation() != null) {
            setMarkerForUser();
        }
        setLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    setLocation();
                } else {
                    checkerGPS.checkLocationPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public void setLocation() {

        checkerGPS.checkLocationPermission();

        mGoogleMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("onLocationChanged", "onLocationChanged Location listener");
                if (mySourceMarker != null) {
                    updateMarker(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, locationListener);

        Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        getMyLocation(myLocation);
        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.d("onMyLocationChange", "onMyLocationChange");
                if (mySourceMarker != null) {
                    updateMarker(location);
                }
            }
        });
    }

    private void getMyLocation(Location location) {
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        hereMarker = mGoogleMap.addMarker(new MarkerOptions().title("Bạn đang ở đây").position(myLatLng));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 20);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    private void updateMarker(Location location) {
        Log.d("onMyLocationChange", "onMyLocationChange update");

        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        hereMarker.setPosition(newLatLng);

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));

    }

    public void setMarkerForUser() {
        if (myRequestInfo != null && yourRequestInfo != null) {
            LatLng mySouceLatLng = Helper.convertLatLngLocationToLatLng(myRequestInfo.getSourceLocation());
            LatLng myDestLatLng = Helper.convertLatLngLocationToLatLng(myRequestInfo.getDestLocation());

            desLocation = new Location("DesLocation");
            desLocation.setLatitude(myDestLatLng.latitude);
            desLocation.setLongitude(myDestLatLng.longitude);

            mySourceMarker = mGoogleMap.addMarker(new MarkerOptions().title("Bạn bắt đầu").position(mySouceLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_red_700_36dp)));
            listMarker.put("Source " + String.valueOf(myUserId), mySourceMarker);
            Marker myDesMarker = mGoogleMap.addMarker(new MarkerOptions().position(myDestLatLng).title("Bạn kết thúc ở đây")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red_700_36dp)));
            listMarker.put("Des " + String.valueOf(myUserId), myDesMarker);

            //add source and des marker for another user
            LatLng yourSourceLatLng = Helper.convertLatLngLocationToLatLng(yourRequestInfo.getSourceLocation());
            LatLng yourDesLatLng = Helper.convertLatLngLocationToLatLng(yourRequestInfo.getDestLocation());

            Marker yourSourceMarker = mGoogleMap.addMarker(new MarkerOptions().position(yourSourceLatLng).title("Rước").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_indigo_500_24dp)
            ));
            Marker yourDesMarker = mGoogleMap.addMarker(new MarkerOptions().position(yourDesLatLng).title("Đến nơi").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_indigo_500_24dp)
            ));
            listMarker.put("Source " + yourRequestInfo.getUserId(), yourSourceMarker);
            listMarker.put("Des " + yourRequestInfo.getUserId(), yourDesMarker);

            DrawRoute drawRoute = new DrawRoute(this, mGoogleMap);
            if (myRequestInfo.getVehicleType() != 0) {
                drawRoute.drawroadBetween4Location(mySouceLatLng, yourSourceLatLng, yourDesLatLng, myDestLatLng, 1);
            } else {
                drawRoute.drawroadBetween4Location(yourSourceLatLng, mySouceLatLng, myDestLatLng, yourDesLatLng, 1);
            }

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mySouceLatLng, 20));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartStrip:
                startTrip();

                break;
            case R.id.btnEndTrip:
                SharedPreferences sharedEndTrip = getSharedPreferences(CustomFirebaseMessagingService.SHARE_PREFER_END_TRIP, MODE_PRIVATE);
                boolean isEndTrip = sharedEndTrip.getBoolean(CustomFirebaseMessagingService.IS_END_TRIP, false);
                endTrip();
        }
    }

    public void startTrip() {
        StartTripAPI.getInstance(this).sendNotiStartTripToUserTogether(apiToken, yourRequestInfo.getUserId());
    }

    public void endTrip() {
        if (CheckInternetAndLocation.isOnline(this)) {
            if (journeyId != 0) {
                EndTheTrip.getInstance(this).endTheTripWithUserTogether(apiToken, journeyId);

                editorScreen=sharedPreferencesScreen.edit();
                editorScreen.putInt(MainActivity.SCREEN_NAME,MainActivity.RATING);
                editorScreen.commit();
            }

        }
    }

    @Override
    public void startTripSuccess(JourneyInfo journeyInfo) {
        Toast.makeText(this, "Gửi thành công, bắt đầu di chuyển thôi", Toast.LENGTH_SHORT).show();

        btnStartTrip.setVisibility(View.GONE);
        journeyId = journeyInfo.getDetail().getJourneyId();

        btnEndTrip.setVisibility(View.VISIBLE);

        editorScreen=sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME,MainActivity.STARTED_TRIP);
        editorScreen.putInt(JOURNEY_ID,0);
        editorScreen.commit();
    }

    @Override
    public void startTripFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getInfoUserSuccess(User userInfo) {
        phone = userInfo.getPhone();
    }

    @Override
    public void getUserInfoFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void endTripSuccess() {
        moveToRatingScreen();
    }

    private void moveToRatingScreen() {
        Intent intent = new Intent(this, RatingActivity.class);
        intent.putExtra("journey_id", journeyId);
        startActivity(intent);
    }

    @Override
    public void endTripFailure(String message) {
        if(message.equals("unsuccessful")){
            moveToRatingScreen();
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
