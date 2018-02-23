package vehiclessharing.vehiclessharing.view.activity;

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
import android.widget.Button;
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
import vehiclessharing.vehiclessharing.api.CancelTripAPI;
import vehiclessharing.vehiclessharing.api.EndTheTripAPI;
import vehiclessharing.vehiclessharing.api.SendSOSToAPI;
import vehiclessharing.vehiclessharing.api.StartTripAPI;
import vehiclessharing.vehiclessharing.api.UserInfoAPI;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.view.fragment.CancelTripDialog;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ConfirmRequest;
import vehiclessharing.vehiclessharing.model.InfomationUser;
import vehiclessharing.vehiclessharing.model.JourneyInfo;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.permission.CallPermission;
import vehiclessharing.vehiclessharing.utils.NetworkUtils;
import vehiclessharing.vehiclessharing.permission.CheckerGPS;
import vehiclessharing.vehiclessharing.push.CustomFirebaseMessagingService;
import vehiclessharing.vehiclessharing.service.AlarmReceiver;
import vehiclessharing.vehiclessharing.utils.DrawRoute;
import vehiclessharing.vehiclessharing.utils.Helper;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

//import com.amitshekhar.DebugDB;

public class VehicleMoveActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,
        StartTripAPI.StartTripRequestCallback, UserInfoAPI.GetInfoUserCallback, EndTheTripAPI.EndTripRequestCallback,
        CancelTripAPI.CancelTripInterface, SendSOSToAPI.SendSOSCallback {

    public static String CALL_FROM_WHAT_ACTIVITY = "call_from_what_activity";
    final private static int REQ_PERMISSION = 20;//Value request permission

    public static String START_TRIP = "start_the_trip";
    public static String ALARM_START = "alarm_start";
    public static String CONFIRM_REQUEST = "confirm_request";
    public static String RECEIVE_CONFIRM_REQUEST = "receive_confirm_request";
    public static String JOURNEY_ID = "journey_id";

    public static AlarmManager sAlarmManager;
    public static PendingIntent sPendingIntent;

    private GoogleMap mGoogleMap;
    private DatabaseHelper mDatabaseHelper;
    private SharedPreferences mSharedPreferences;
    private RequestInfo mMyRequestInfo, mYourRequestInfo;
    private HashMap<String, Marker> mListMarker;
    private Marker mMySourceMarker, mHereMarker;
    private Location mDesLocation;
    private LocationManager mLocationManager;
    private android.location.LocationListener mLocationListener;
    private ConfirmRequest mConfirmRequest;
    private FloatingActionButton mBtnStartTrip, mBtnCancelTrip, mBtnEndTrip;
    private Button mBtnSOS;

    private String mApiToken = "", mPhone = "", mMyAddress = "",mCallFrom = "";
    private int mJourneyId = 0,mMyUserId;

    private CheckerGPS mCheckerGPS;

    private SharedPreferences mSharedPreferencesScreen;
    private SharedPreferences.Editor mEditorScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_move);

        Bundle bundle = getIntent().getExtras();

        String dataReceive = bundle.getString(CustomFirebaseMessagingService.DATA_RECEIVE, "");
        mCallFrom = bundle.getString(CALL_FROM_WHAT_ACTIVITY, "");
        mJourneyId = bundle.getInt("journey_id", 0);
        Log.d("journeyId", String.valueOf(mJourneyId));

        if (!dataReceive.equals("")) {
            Gson gson = new Gson();
            mConfirmRequest = gson.fromJson(dataReceive, ConfirmRequest.class);
        }

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        mSharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        mMyUserId = mSharedPreferences.getInt(SessionManager.USER_ID, 0);
        mApiToken = mSharedPreferences.getString(SessionManager.KEY_SESSION, "");
        mDatabaseHelper = new DatabaseHelper(this);


        mMyRequestInfo = mDatabaseHelper.getRequestInfo(mMyUserId);
        mYourRequestInfo = mDatabaseHelper.getRequestInfoNotMe(mMyUserId);

        mSharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);

        mListMarker = new HashMap<>();
        if (mConfirmRequest != null && mCallFrom.equals("receive_confirm_request")) {
        }
        UserInfoAPI userInfoAPI=new UserInfoAPI(this);
               userInfoAPI.getUserInfoFromAPI(mApiToken, mYourRequestInfo.getUserId());

        mCheckerGPS = new CheckerGPS(this, this);

        addControls();
        addEvents();
        if (!mCallFrom.equals(ALARM_START) && !mCallFrom.equals(START_TRIP)) {
            addAlarm();
        }
        if (mJourneyId == 0) {
            mJourneyId = mSharedPreferencesScreen.getInt(JOURNEY_ID, 0);
        }
        Log.d("journeyId", String.valueOf(mJourneyId));
    }

    private void addAlarm() {
        try {
            int hour;
            int minute;
            String[] time;
            if (mMyRequestInfo.getVehicleType() != 0) {
                time = mMyRequestInfo.getTimeStart().split(":");
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
            } else {
                time = mYourRequestInfo.getTimeStart().split(":");
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent myIntent = new Intent(this, AlarmReceiver.class);
            sPendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
            sAlarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), sPendingIntent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sAlarmManager.cancel(sPendingIntent);
                }
            }, 3000);
        } catch (Exception e) {

        }
    }

    private void addEvents() {
        mBtnStartTrip.setOnClickListener(this);
        mBtnCancelTrip.setOnClickListener(this);
        mBtnEndTrip.setOnClickListener(this);
        mBtnSOS.setOnClickListener(this);
    }

    private void addControls() {
        mBtnStartTrip = findViewById(R.id.btnStartStrip);
        mBtnCancelTrip = findViewById(R.id.btnCancelStrip);
        mBtnEndTrip = findViewById(R.id.btnEndTrip);
        mBtnSOS = findViewById(R.id.btnSOS);

        if (mCallFrom.equals(CONFIRM_REQUEST)) {
            if (VehicleMoveActivity.sAlarmManager != null && VehicleMoveActivity.sPendingIntent != null) {
                VehicleMoveActivity.sAlarmManager.cancel(VehicleMoveActivity.sPendingIntent);
            }
            showStartFloatingButton(true);

            mEditorScreen = mSharedPreferencesScreen.edit();
            mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.WAIT_START_TRIP);
            mEditorScreen.commit();

        } else if (mCallFrom.equals(RECEIVE_CONFIRM_REQUEST)) {
            showStartFloatingButton(true);

            mEditorScreen = mSharedPreferencesScreen.edit();
            mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.WAIT_START_TRIP);
            mEditorScreen.commit();
        } else if (mCallFrom.equals(START_TRIP)) {

            mEditorScreen = mSharedPreferencesScreen.edit();
            mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.STARTED_TRIP);
            mEditorScreen.commit();
            showStartFloatingButton(false);
            mBtnCancelTrip.setVisibility(View.GONE);

        } else {
            int screenName = mSharedPreferencesScreen.getInt(MainActivity.SCREEN_NAME, 0);
            if (screenName == MainActivity.WAIT_START_TRIP) {
                sAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                showStartFloatingButton(true);
            } else if (screenName == MainActivity.STARTED_TRIP) {
                showStartFloatingButton(false);
            } else if (screenName == MainActivity.RATING) {
                finish();
                Intent intent = new Intent(this, RatingActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private void showStartFloatingButton(boolean show) {
        if (show) {
            mBtnStartTrip.setVisibility(View.VISIBLE);
            mBtnCancelTrip.setVisibility(View.VISIBLE);
            mBtnSOS.setVisibility(View.GONE);
            mBtnEndTrip.setVisibility(View.GONE);
        } else {
            mBtnStartTrip.setVisibility(View.GONE);
            mBtnCancelTrip.setVisibility(View.GONE);
            mBtnSOS.setVisibility(View.VISIBLE);
            mBtnEndTrip.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_trip, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.call) {
            if (!mPhone.equals("")) {
                if (CallPermission.checkCall(this, this)) {

                }
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + mPhone));

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            CallPermission.REQUEST_CALL_PHONE);
                }
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "Số điện thoại không có sẵn", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //add source and des marker for me
        if (mMyRequestInfo != null && mMyRequestInfo.getSourceLocation() != null && mMyRequestInfo.getDestLocation() != null) {
            setMarkerForUser();
        }
        setLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocation();
                } else {
                    mCheckerGPS.checkLocationPermission();
                }
                return;
            }
            case CallPermission.REQUEST_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mPhone.equals("")) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + mPhone));

                        startActivity(callIntent);
                    }
                } else {
                    CallPermission.checkCall(this, this);
                }
        }
    }


    public void setLocation() {

        mCheckerGPS.checkLocationPermission();

        mGoogleMap.setMyLocationEnabled(true);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("onLocationChanged", "onLocationChanged Location listener");
                if (mMySourceMarker != null) {
                    updateMarker(location, false);
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
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, mLocationListener);

        Location myLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        getMyLocation(myLocation);
        final boolean[] firstTime = {true};

        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.d("onMyLocationChange", "onMyLocationChange");
                if (mMySourceMarker != null) {
                    updateMarker(location, firstTime[0]);
                    firstTime[0] = false;
                }
            }
        });
    }

    private void getMyLocation(Location location) {
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mHereMarker = mGoogleMap.addMarker(new MarkerOptions().title("Bạn đang ở đây").position(myLatLng));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 20);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    private void updateMarker(Location location, boolean firstTime) {
        Log.d("onMyLocationChange", "onMyLocationChange update");

        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mHereMarker.setPosition(newLatLng);
        if (!firstTime) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));
        } else {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 15));
        }
    }

    public void setMarkerForUser() {
        if (mMyRequestInfo != null && mYourRequestInfo != null) {
            LatLng mySouceLatLng = Helper.convertLatLngLocationToLatLng(mMyRequestInfo.getSourceLocation());
            LatLng myDestLatLng = Helper.convertLatLngLocationToLatLng(mMyRequestInfo.getDestLocation());

            mDesLocation = new Location("DesLocation");
            mDesLocation.setLatitude(myDestLatLng.latitude);
            mDesLocation.setLongitude(myDestLatLng.longitude);

            mMySourceMarker = mGoogleMap.addMarker(new MarkerOptions().title("Bạn bắt đầu").position(mySouceLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_red_700_36dp)));
            mListMarker.put("Source " + String.valueOf(mMyUserId), mMySourceMarker);
            Marker myDesMarker = mGoogleMap.addMarker(new MarkerOptions().position(myDestLatLng).title("Bạn kết thúc ở đây")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red_700_36dp)));
            mListMarker.put("Des " + String.valueOf(mMyUserId), myDesMarker);

            //add source and des marker for another user
            LatLng yourSourceLatLng = Helper.convertLatLngLocationToLatLng(mYourRequestInfo.getSourceLocation());
            LatLng yourDesLatLng = Helper.convertLatLngLocationToLatLng(mYourRequestInfo.getDestLocation());

            String markerStart = "", markerEnd = "";
            if (mYourRequestInfo.getVehicleType() == 0) {
                markerStart = "Bạn đến đây chở";
            } else {
                markerStart = "Người chở bắt đầu ở đây";
            }

            Marker yourSourceMarker = mGoogleMap.addMarker(new MarkerOptions().position(yourSourceLatLng).title(markerStart).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_indigo_500_24dp)
            ));
            Marker yourDesMarker = mGoogleMap.addMarker(new MarkerOptions().position(yourDesLatLng).title("Đến nơi").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_indigo_500_24dp)
            ));
            mListMarker.put("Source " + mYourRequestInfo.getUserId(), yourSourceMarker);
            mListMarker.put("Des " + mYourRequestInfo.getUserId(), yourDesMarker);

            DrawRoute drawRoute = new DrawRoute(this, mGoogleMap);
            if (mMyRequestInfo.getVehicleType() != 0) {
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
        if (mLocationManager != null && mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartStrip:
                mBtnCancelTrip.setVisibility(View.GONE);
                startTrip();
                break;
            case R.id.btnEndTrip: {

                SharedPreferences sharedEndTrip = getSharedPreferences(CustomFirebaseMessagingService.SHARE_PREFER_END_TRIP, MODE_PRIVATE);
                boolean isEndTrip = sharedEndTrip.getBoolean(CustomFirebaseMessagingService.IS_END_TRIP, false);
                endTrip();
                break;
            }
            case R.id.btnCancelStrip:
                cancelTrip();
                break;
            case R.id.btnSOS: {
                sendSOSToServer();
                if (CallPermission.checkCall(this, this)) {

                }
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:113"));

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            CallPermission.REQUEST_CALL_PHONE);
                }
                startActivity(callIntent);

                break;
            }
        }
    }

    private void sendSOSToServer() {
        try {
            mMyAddress = PlaceHelper.getInstance(this).getCurrentPlace(mGoogleMap);
        } catch (Exception e) {
            Log.d("sendSOSToServer","Error get current location");
        }
        SendSOSToAPI sosToAPI=new SendSOSToAPI(this);
        sosToAPI.sendSOS(mApiToken, mMyAddress, mMyRequestInfo.getVehicleType());

    }

    private void cancelTrip() {

        android.support.v4.app.DialogFragment dialogFragment;
        dialogFragment = CancelTripDialog.newInstance(MainActivity.sSessionId, mMyRequestInfo.getVehicleType(), this);
        dialogFragment.show(getSupportFragmentManager(), "CancelTrip");

    }

    public void startTrip() {
        StartTripAPI startTripAPI=new StartTripAPI(this);
        startTripAPI.sendNotiStartTripToUserTogether(mApiToken, mYourRequestInfo.getUserId(), mMyRequestInfo.getVehicleType());
    }

    public void endTrip() {
        if (NetworkUtils.isNetworkConnected(this)) {

            Log.d("journeyId", "endTrip" + String.valueOf(mJourneyId));
            if (mJourneyId != 0) {
                EndTheTripAPI endTheTripAPI=new EndTheTripAPI(this);
                endTheTripAPI.endTheTripWithUserTogether(mApiToken, mJourneyId);

                mEditorScreen = mSharedPreferencesScreen.edit();
                mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.RATING);
                mEditorScreen.commit();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra, không thể kết thúc chuyến đi", Toast.LENGTH_SHORT).show();
                moveToMainActiity();
            }
        }
    }

    @Override
    public void startTripSuccess(JourneyInfo journeyInfo) {
        Toast.makeText(this, "Gửi thành công, bắt đầu di chuyển thôi", Toast.LENGTH_SHORT).show();

        mBtnStartTrip.setVisibility(View.GONE);
        mJourneyId = journeyInfo.getDetail().getJourneyId();

        Log.d("journeyId", "start Trip success" + String.valueOf(mJourneyId));
        mBtnEndTrip.setVisibility(View.VISIBLE);
        mBtnSOS.setVisibility(View.VISIBLE);

        mEditorScreen = mSharedPreferencesScreen.edit();
        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.STARTED_TRIP);
        mEditorScreen.putInt(JOURNEY_ID, mJourneyId);
        mEditorScreen.commit();
    }

    @Override
    public void startTripFailure(String message) {
        Log.d("startTrip", message);

        Log.d("journeyId", "startTripFailureS" + String.valueOf(mJourneyId));
        mEditorScreen = mSharedPreferencesScreen.edit();
        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.STARTED_TRIP);
        if (mJourneyId != 0) {
            mEditorScreen.putInt(JOURNEY_ID, mJourneyId);
        }
        mEditorScreen.commit();
        showStartFloatingButton(false);
    }

    @Override
    public void getInfoUserSuccess(InfomationUser infomationUser) {
        mPhone = infomationUser.getUserInfo().getPhone();
        SharedPreferences sharedPreferencesPhone = getSharedPreferences("phone_partner", MODE_PRIVATE);
        SharedPreferences.Editor editorPhone = sharedPreferencesPhone.edit();
        editorPhone.putString("phone_number", mPhone);
        editorPhone.commit();
        Log.d("phone number: ", mPhone);
    }

    @Override
    public void getUserInfoFailure(String message) {
        Log.d("getUserInfo", message);
    }

    @Override
    public void endTripSuccess() {
        moveToRatingScreen();
    }

    private void moveToRatingScreen() {
        Intent intent = new Intent(this, RatingActivity.class);
        intent.putExtra("journey_id", mJourneyId);
        startActivity(intent);
    }

    @Override
    public void endTripFailure(String message) {
        if (message.equals("unsuccessful")) {

        }
        moveToRatingScreen();
        Log.d("endTrip", message);
    }

    @Override
    public void endTripFailureBecauseDanger() {
        Toast.makeText(this, "Chuyến đi đã bị báo nguy hiểm", Toast.LENGTH_SHORT).show();
        moveToMainActiity();
    }

    @Override
    public void cancelTripSuccess() {

        moveToMainActiity();
    }

    @Override
    public void cancelTripFailed() {
        Toast.makeText(this, "Hủy chuyến đi thất bại", Toast.LENGTH_SHORT).show();

        moveToMainActiity();


    }

    private void moveToMainActiity() {
        mEditorScreen = mSharedPreferencesScreen.edit();
        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
        mEditorScreen.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void sendSOSSuccess() {
        mEditorScreen = mSharedPreferencesScreen.edit();
        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
        mEditorScreen.commit();
    }

    @Override
    public void sendSOSFailed() {
        sendSOSToServer();
    }
}
