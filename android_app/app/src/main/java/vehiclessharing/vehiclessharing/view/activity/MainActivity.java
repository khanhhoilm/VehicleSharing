package vehiclessharing.vehiclessharing.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.CancelRequestAPI;
import vehiclessharing.vehiclessharing.api.LogoutAPI;
import vehiclessharing.vehiclessharing.api.UpdateListActiveUserAPI;
import vehiclessharing.vehiclessharing.api.UserInfoAPI;
import vehiclessharing.vehiclessharing.asynctask.CustomMarkerAsync;
import vehiclessharing.vehiclessharing.asynctask.GetAvatar;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ActiveUser;
import vehiclessharing.vehiclessharing.model.InfomationUser;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.permission.CheckWriteStorage;
import vehiclessharing.vehiclessharing.permission.CheckerGPS;
import vehiclessharing.vehiclessharing.utils.DrawRoute;
import vehiclessharing.vehiclessharing.utils.Helper;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;
import vehiclessharing.vehiclessharing.view.fragment.AddRequestFragment;
import vehiclessharing.vehiclessharing.view.fragment.SendRequestFragment;

//import com.amitshekhar.DebugDB;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        AddRequestFragment.OnFragmentAddRequestListener, CancelRequestAPI.CancelRequestCallBack,
        SendRequestFragment.SendRequestCallBack, UpdateListActiveUserAPI.UpdateListActiveUserCallback,
        UserInfoAPI.GetInfoUserCallback, GetAvatar.GetBitMapAvatarInterface {

    public final static int ADDED_REQUEST = 1;
    public final static int MAIN_ACTIVITY = 0;
    public final static int WAIT_CONFIRM = 2;
    public final static int CONFIRM_ACCEPT = 3;
    public final static int CONFIRM_DENY = 4;
    public final static int RECEIVE_CONFRIM_REQUEST = 5;
    public final static int WAIT_START_TRIP = 6;
    public final static int STARTED_TRIP = 7;
    public final static int END_TRIP = 8;
    public static final int RATING = 9;
    public static final int BLOCK_USER = 10;

    public static String SCREEN_AFTER_BACK = "screen_after_back";
    public static String SCREEN_NAME = "screen_name";
    public static String MY_VEHICLE_TYPE = "my_vehicle_type";
    public static String TIME_SEND_REQUEST = "time_send_request";
    final public static int REQ_PERMISSION = 20;

    public static boolean sBtnAddClick = false;
    public static GoogleMap mGoogleMap = null;
    public static int sUserId;
    public static String sSessionId = "";
    public static List<Polyline> sPolylineList;
    public static HashMap<Marker, ActiveUser> sUserHashMap;
    public static HashMap<ActiveUser, Marker> sMarkerHashMap;
    public static HashMap<Integer, Marker> sNumberMarkerInHashMap;
    public static CheckerGPS sCheckerGPS;
    public static ImageView sAvatar;

    private SharedPreferences mSharedPreferencesCheckScreen;
    private SharedPreferences.Editor mEditorScreen;
    private DatabaseHelper mDatabase;

    private SessionManager mSessionManager;
    private User mUserInfo;

    private NavigationView mNavigationView = null;
    private Toolbar mToolbar = null;
    private View mViewHeader = null;
    private TextView mTxtFullName, mTxtPhone;
    private FloatingActionButton mBtnFindPeople, mBtnFindVehicles, mBtnCancelRequest;
    private FrameLayout mFrameLayoutMarkerInfo;

    private Marker mMyMarker = null, mAnotherDesMarker;
    private Location mPreviousLocation = null, mMyLocation;
    private LatLng mMySource, mMyDes;
    private LocationManager mLocationManager;
    private android.location.LocationListener mLocationListener;

    private boolean isSending = false, isCancel = false, mDoubleBackToExitPressedOnce = false, mChangeLocation = false;
    private int checkOnScreen, mVehicleType;

    private UpdateListActiveUserAPI mListActiveUserAPI;
    private List<ActiveUser> mListActiveUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSessionManager = new SessionManager(MainActivity.this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (!mSessionManager.isLoggedIn()) {
            Intent signIn = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(signIn);
            finish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        sUserId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        sSessionId = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        Log.d("SessionId", "SessionId=" + sSessionId);


        mSharedPreferencesCheckScreen = getSharedPreferences(SCREEN_AFTER_BACK, MODE_PRIVATE);
        checkOnScreen = mSharedPreferencesCheckScreen.getInt(SCREEN_NAME, 0);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        mUserInfo = databaseHelper.getUser(sUserId);
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("Main Activity");

        mDatabase = new DatabaseHelper(this);
        mListActiveUserAPI = new UpdateListActiveUserAPI(this);
        addControls();
        addEvents();
        setScreen();

    }

    private void setScreen() {
        checkOnScreen = mSharedPreferencesCheckScreen.getInt(SCREEN_NAME, 0);
        Log.d("On Screen", "value: " + checkOnScreen);
        switch (checkOnScreen) {
            case ADDED_REQUEST:
                setViewAddedRequest(ADDED_REQUEST);
                break;
            case WAIT_CONFIRM:
                setViewAddedRequest(WAIT_CONFIRM);
                break;
            case CONFIRM_DENY:
                setViewAddedRequest(CONFIRM_DENY);
                break;
            case WAIT_START_TRIP:
                setViewDirect(WAIT_START_TRIP);
                break;
            case STARTED_TRIP:
                setViewDirect(STARTED_TRIP);
                break;
            case END_TRIP:
                setViewRating(END_TRIP);
                break;
            case RATING:
                setViewRating(RATING);
                break;
            default:
                setMainView();
                break;
        }
    }

    private boolean overFiveMinute() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("HH:mm");
        String currentTime = sdf1.format(calendar.getTime());
        String[] currentHourMinute = currentTime.split(":");
        String timeSendRequest = mSharedPreferencesCheckScreen.getString(TIME_SEND_REQUEST, "");
        if (!timeSendRequest.equals("")) {
            String[] hourMinuteSend = timeSendRequest.split(":");
            if (currentHourMinute[0].equals(hourMinuteSend[0])) {
                return false;
            } else if (Integer.parseInt(currentHourMinute[1]) - Integer.parseInt(hourMinuteSend[1]) > 5) {
                return false;
            }
        }
        return true;
    }

    private void setMainView() {
        showAddRequestButton();
    }

    private void setViewRating(int endTrip) {
        Intent intentRating = new Intent(this, RatingActivity.class);
        startActivity(intentRating);
    }

    private void setViewDirect(int waitStartTrip) {
        Intent intentDirect = new Intent(this, VehicleMoveActivity.class);
        startActivity(intentDirect);
    }

    private void showAddRequestButton() {
        mBtnCancelRequest.setVisibility(View.GONE);
        mBtnFindVehicles.setVisibility(View.VISIBLE);
        mBtnFindPeople.setVisibility(View.VISIBLE);
    }

    private void hideAddRequestButton() {
        mBtnCancelRequest.setVisibility(View.VISIBLE);
        mBtnFindVehicles.setVisibility(View.GONE);
        mBtnFindPeople.setVisibility(View.GONE);
    }

    private void disableAllButton() {
        mBtnFindPeople.setEnabled(false);
        mBtnFindVehicles.setEnabled(false);
    }

    private void enableAllButton() {
        mBtnFindVehicles.setEnabled(true);
        mBtnFindPeople.setEnabled(true);
    }

    private void setViewAddedRequest(final int addedRequest) {
        hideAddRequestButton();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int myVehicleType = mSharedPreferencesCheckScreen.getInt(MY_VEHICLE_TYPE, 3);
                if (myVehicleType != 3) {
                    mListActiveUserAPI.getListUpdate(sSessionId, myVehicleType);
                }

                isSending = false;
                addMarkerAndDrawRoute();
            }
        }, 1000);

    }

    private void addMarkerAndDrawRoute() {
        RequestInfo requestInfo = mDatabase.getRequestInfo(sUserId);

        if (requestInfo != null && requestInfo.getSourceLocation() != null && requestInfo.getDestLocation() != null) {
            mMySource = Helper.convertLatLngLocationToLatLng(requestInfo.getSourceLocation());
            mMyDes = Helper.convertLatLngLocationToLatLng(requestInfo.getDestLocation());

            DrawRoute drawRoute = new DrawRoute(this, mGoogleMap);
            drawRoute.setmSubject(0);

            drawRoute.drawroadBetween2Location(mMySource, mMyDes, 0);
            try {
                makeCustomMarkerMyself(mMySource, mMyDes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mBtnCancelRequest.setVisibility(View.GONE);
            showAddRequestButton();
            mGoogleMap.clear();
            isCancel = true;
            setLocation();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDoubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.mDoubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn 1 lần nữa để thoát app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mDoubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void addEvents() {
        mBtnFindPeople.setOnClickListener(this);
        mBtnFindVehicles.setOnClickListener(this);
        mBtnCancelRequest.setOnClickListener(this);
    }

    private void addControls() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mBtnFindPeople = (FloatingActionButton) findViewById(R.id.btnFindPeople);
        mBtnFindVehicles = (FloatingActionButton) findViewById(R.id.btnFindVehicle);

        mViewHeader = mNavigationView.getHeaderView(0);

        mTxtPhone = (TextView) mViewHeader.findViewById(R.id.txtPhone);
        mTxtFullName = (TextView) mViewHeader.findViewById(R.id.txtFullName);
        sAvatar = (ImageView) mViewHeader.findViewById(R.id.imgUser);
        mTxtPhone.setText(mUserInfo.getPhone());
        mTxtFullName.setText(mUserInfo.getName());

        UserInfoAPI userInfoAPI = new UserInfoAPI(this);
        userInfoAPI.getMyInfo(sSessionId);

        sCheckerGPS = new CheckerGPS(MainActivity.this, this);

        mBtnCancelRequest = (FloatingActionButton) findViewById(R.id.btnCancelRequest);

        mFrameLayoutMarkerInfo = (FrameLayout) findViewById(R.id.frameContainerMarker);
        mFrameLayoutMarkerInfo.setVisibility(View.GONE);

        sMarkerHashMap = new HashMap<>();
        sUserHashMap = new HashMap<>();
        sNumberMarkerInHashMap = new HashMap<>();
        mListActiveUser = new ArrayList<>();
        sPolylineList = new ArrayList<>();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(historyIntent);
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        LogoutAPI logoutAPI = new LogoutAPI();
        logoutAPI.actionLogout(this, getSupportFragmentManager());
        mDatabase.deleteAll();
        mSessionManager.logoutUser();
        mEditorScreen = mSharedPreferencesCheckScreen.edit();
        mEditorScreen.putInt(SCREEN_NAME, MAIN_ACTIVITY);
        mEditorScreen.commit();
        finish();
    }

    @Override
    public void onClick(View v) {
        String whatbtnClick = "";
        android.support.v4.app.DialogFragment dialogFragment;
        switch (v.getId()) {
            case R.id.btnFindPeople:

                isCancel = false;
                whatbtnClick = "btnFindPeople";
                disableAllButton();
                try {
                    if (!sBtnAddClick) {
                        String presentAddress = PlaceHelper.getInstance(this).
                                getAddressByLatLng(new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude()));
                        dialogFragment = AddRequestFragment.newInstance(whatbtnClick, presentAddress);
                        dialogFragment.show(getSupportFragmentManager(), "From Needer");
                        sBtnAddClick = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                enableAllButton();
                break;
            case R.id.btnFindVehicle:

                disableAllButton();
                isCancel = false;
                whatbtnClick = "btnFindVehicles";
                try {
                    if (!sBtnAddClick) {
                        sBtnAddClick = true;
                        String presentAddress = PlaceHelper.getInstance(this).
                                getAddressByLatLng(new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude()));

                        dialogFragment = AddRequestFragment.newInstance(whatbtnClick, presentAddress);
                        dialogFragment.show(getSupportFragmentManager(), "From Grabber");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                enableAllButton();
                break;
            case R.id.btnCancelRequest:

                cancelRequest();
                isCancel = true;
                break;
        }

    }

    private void cancelRequest() {
        CancelRequestAPI relateRequestAPI = new CancelRequestAPI(this);
        relateRequestAPI.cancelRequest(sSessionId);

        enableAllButton();
        showAddRequestButton();

    }

    private void hideButtonFindVehicleAndPeople() {
        if (mBtnFindVehicles.getVisibility() == View.VISIBLE && mBtnFindPeople.getVisibility() == View.VISIBLE) {
            mBtnFindPeople.setVisibility(View.GONE);
            mBtnFindVehicles.setVisibility(View.GONE);
            if (mBtnCancelRequest.getVisibility() == View.GONE) {
                mBtnCancelRequest.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGoogleMap != null) {
                        setLocation();
                    }
                } else {
                    sCheckerGPS.checkLocationPermission();
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        try {
            if (mGoogleMap != null && sCheckerGPS.checkLocationPermission()) {
                mGoogleMap.setMyLocationEnabled(true);
                if (checkOnScreen == 0) {
                    setLocation();
                }

            }
        } catch (Exception e) {
            Toast.makeText(this, "Check permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocation() {
        if (sCheckerGPS.checkLocationPermission()) {
            mGoogleMap.setMyLocationEnabled(true);//Enable mylocation

            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mMyLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            getMyLocation(mMyLocation);
            final int[] firstTime = {1};

            mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    setLocationChange(location, firstTime[0]);
                    firstTime[0] = 0;
                }
            });
        }
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            }
        });
    }

    private void setLocationChange(Location location, int firstTime) {
        if (!mChangeLocation) {
            final double[] longitude = {location.getLongitude()};
            final double[] latitude = {location.getLatitude()};
            LatLng myLatLng = new LatLng(latitude[0], longitude[0]);
            mMyLocation = location;
            if (!location.equals(mPreviousLocation) && mPreviousLocation != null) {

                if (location.distanceTo(mPreviousLocation) > 20.0 || !location.equals(mPreviousLocation)) {
                    if (mMyMarker != null) {
                        mMyMarker.remove();
                    }
                    mMyMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.here)));
                    mMyMarker.setTag("here");
                    CameraUpdate cameraUpdate = null;
                    if (firstTime == 1) {
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 17);
                        mGoogleMap.animateCamera(cameraUpdate);
                    }/* else {
                        cameraUpdate = CameraUpdateFactory.newLatLng(myLatLng);
                    }
*/
                    mMyLocation = location;
                }
            }
            if (mPreviousLocation == null) {
                mPreviousLocation = location;
                mMyMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.here)));
                mMyMarker.setTag("here");
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 17);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        }

    }

    private void getMyLocation(Location location) {
        if (!mChangeLocation) {
            final double[] longitude = {location.getLongitude()};
            final double[] latitude = {location.getLatitude()};
            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMyLocation = location;
            if (!location.equals(mPreviousLocation) && mPreviousLocation != null) {

                if (location.distanceTo(mPreviousLocation) > 20.0 || !location.equals(mPreviousLocation)) {
                    if (mMyMarker != null) {
                        mMyMarker.remove();

                    }
                    mMyMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.here)));
                    mMyMarker.setTag("here");

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
                    mGoogleMap.animateCamera(cameraUpdate);
                    mMyLocation = location;
                }
            }
            if (mPreviousLocation == null) {
                mPreviousLocation = location;
                mMyMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.here)));
                mMyMarker.setTag("here");
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        }

    }


    private void makeCustomMarkerMyself(final LatLng source, final LatLng destination) throws IOException {
        if (mGoogleMap != null) {
            addMarker(source, destination);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mGoogleMap != null) {
                        addMarker(source, destination);
                    }
                }
            }, 500);

        }
    }

    private void addMarker(LatLng source, LatLng destination) {
        BitmapDescriptor bitmapSource = BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_red_700_36dp);
        BitmapDescriptor bitmapDestination = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red_700_36dp);
        try {
            Marker sourceMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title("Chuyến đi của bạn bắt đầu tại: " + PlaceHelper.getInstance(this).getAddressByLatLng(source)).icon(bitmapSource));
            Marker destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(destination).title("Chuyến đi kết thúc tại: " + PlaceHelper.getInstance(this).getAddressByLatLng(destination)).icon(bitmapDestination));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(source, 13);
            mGoogleMap.animateCamera(cameraUpdate);

            sourceMarker.setTag("here");
            destinationMarker.setTag("des");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("onMarkerClick", "success");
        try {
            if (marker != null && marker.getTag() != null) {
                if (!marker.getTag().equals("here") && !marker.getTag().equals("des")) {
                    displayInfoMarkerClick(marker);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    private void displayInfoMarkerClick(Marker marker) throws IOException {

        mFrameLayoutMarkerInfo.setVisibility(View.VISIBLE);
        mBtnCancelRequest.setVisibility(View.GONE);
        final TextView txtName, txtSourceLocation, txtDeslocation, txtTime, txtTouch, txtDistance;
        final ImageView imgVehicleType;
        final Button btnsend, btncancel;

        txtName = (TextView) findViewById(R.id.txtFullNameUser);
        txtSourceLocation = (TextView) findViewById(R.id.txtSourceLocationUser);
        txtDeslocation = (TextView) findViewById(R.id.txtDesLocationUser);
        txtTime = (TextView) findViewById(R.id.txtTimeUser);
        imgVehicleType = (ImageView) findViewById(R.id.imgVehicleTypeUser);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        btnsend = (Button) findViewById(R.id.btnSendRequest);
        btncancel = (Button) findViewById(R.id.btnCancelSendRequest);
        String time = "";
        int vType;


        final ActiveUser anotherUser = sUserHashMap.get(marker);

        if (anotherUser != null) {
            time = anotherUser.getRequestInfo().getTimeStart();
            vType = anotherUser.getRequestInfo().getVehicleType();
            LatLng sourceLocation = Helper.convertLatLngLocationToLatLng(anotherUser.getRequestInfo().getSourceLocation());
            LatLng desLocation = Helper.convertLatLngLocationToLatLng(anotherUser.getRequestInfo().getDestLocation());

            String sourceAddress = PlaceHelper.getInstance(this).getAddressByLatLng(sourceLocation);

            String destinationAddress = PlaceHelper.getInstance(this).getAddressByLatLng(desLocation);
            String name = anotherUser.getUserInfo().getName();
            txtName.setText(name);
            txtSourceLocation.setText(sourceAddress);
            txtDeslocation.setText(destinationAddress);
            txtTime.setText(time);

            if (isSending) {
                btnsend.setVisibility(View.GONE);
                btnsend.setAlpha(0.5f);
            } else {
                btnsend.setVisibility(View.VISIBLE);
                btnsend.setAlpha(1.0f);
            }

            switch (vType) {
                case 0:
                    imgVehicleType.setImageResource(R.drawable.ic_accessibility_indigo_700_24dp);
                    break;
                case 2:
                    imgVehicleType.setImageResource(R.drawable.ic_directions_car_indigo_700_24dp);
                    break;
                case 1:
                    imgVehicleType.setImageResource(R.drawable.ic_motorcycle_indigo_a700_24dp);
                    break;
            }
            if (DrawRoute.sPolylineNotCurUser != null) {
                DrawRoute.sPolylineNotCurUser.remove();
            }
            BitmapDescriptor bitmapDestination = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_indigo_500_24dp);
            if (mAnotherDesMarker != null) {
                mAnotherDesMarker.remove();
            }
            mAnotherDesMarker = mGoogleMap.addMarker(new MarkerOptions().position(desLocation).title("Chuyến đi của " + anotherUser.getUserInfo().getName() + " kết thúc tại đây ").icon(bitmapDestination));
            DrawRoute draw = new DrawRoute(this, mGoogleMap);
            if (sPolylineList.size() > 1) {
                sPolylineList.get(1).remove();

            }
            if (vType == 0) {
                draw.drawroadBetween4Location(mMySource, sourceLocation, desLocation, mMyDes, 1);
            } else {
                draw.drawroadBetween4Location(sourceLocation, mMySource, mMyDes, desLocation, 1);
            }

            Location location = new Location(sourceAddress);
            location.setLatitude(sourceLocation.latitude);
            location.setLongitude(sourceLocation.longitude);
            if (mMyLocation == null) {
                mMyLocation = new Location("MyLocation");
                mMyLocation.setLatitude(mMySource.latitude);
                mMyLocation.setLongitude(mMySource.longitude);
            }
            float distance = Helper.getKiloMeter(mMyLocation.distanceTo(location));
            txtDistance.setText(String.valueOf(distance) + " km");

            btnsend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmSureSendRequestToAnotherUser(anotherUser);

                }
            });
            btncancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFrameLayoutMarkerInfo.setVisibility(View.GONE);
                    mBtnCancelRequest.setVisibility(View.VISIBLE);
                }
            });
            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intenSeeProfile = new Intent(MainActivity.this, AnotherUserProfileActivity.class);
                    intenSeeProfile.putExtra(AnotherUserProfileActivity.USER_ID, anotherUser.getUserInfo().getId());
                    startActivity(intenSeeProfile);
                }
            });
        }
    }

    private void confirmSureSendRequestToAnotherUser(ActiveUser userIsChosen) {
        android.support.v4.app.DialogFragment dialogFragment;
        dialogFragment = SendRequestFragment.newInstance(sSessionId, userIsChosen.getUserInfo().getId(), this);
        dialogFragment.show(getSupportFragmentManager(), "SendRequest");

    }

    @Override
    public void addRequestSuccess(LatLng cur, LatLng des, String time, final int type, List<ActiveUser> list) {
        try {
            isCancel = false;


            mVehicleType = type;
            String requestName = "";
            if (type == 0) {
                requestName = "Bạn đã tạo thành công yêu cầu tìm xe";
            } else {
                requestName = "Bạn đã tạo thành công yêu cầu cho đi nhờ xe";
            }
            Toast.makeText(this, requestName, Toast.LENGTH_SHORT).show();

            mFrameLayoutMarkerInfo.setVisibility(View.GONE);
            mBtnCancelRequest.setVisibility(View.VISIBLE);

            if (list != null && list.size() > 0) {
                mListActiveUser = list;
            }
            mChangeLocation = true;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cur, 17);
            mGoogleMap.animateCamera(cameraUpdate);

            try {
                makeCustomMarkerMyself(cur, des);
            } catch (IOException e) {
                e.printStackTrace();
            }
            DrawRoute drawRoute = new DrawRoute(this, mGoogleMap);
            drawRoute.setmSubject(0);

            drawRoute.drawroadBetween2Location(cur, des, 0);
            mMySource = cur;
            mMyDes = des;

            hideButtonFindVehicleAndPeople();
            if (list.size() > 0) {
                if (sNumberMarkerInHashMap.size() > 0) {
                    sNumberMarkerInHashMap.clear();
                }
                for (int i = 0; i < list.size(); i++) {
                    ActiveUser activeUser = list.get(i);
                    new CustomMarkerAsync(this, i).execute(activeUser);
                }
            }
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setVehicleType(type);
            requestInfo.setTimeStart(time);
            requestInfo.setSourceLocation(Helper.convertLatLngToLatLngLocation(cur));
            requestInfo.setDestLocation(Helper.convertLatLngToLatLngLocation(des));

            CheckWriteStorage.getInstance(this, this).isStoragePermissionGranted();
            mDatabase.insertRequest(requestInfo, sUserId);
            mMyLocation = new Location("MyLocation");
            mMyLocation.setLatitude(cur.latitude);
            mMyLocation.setLongitude(cur.longitude);
        } catch (Exception e) {
            Toast.makeText(this, "addRequestSuccess interface error", Toast.LENGTH_SHORT).show();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListActiveUserAPI.getListUpdate(sSessionId, mVehicleType);
            }
        }, 30000);

        mEditorScreen = mSharedPreferencesCheckScreen.edit();
        mEditorScreen.putInt(SCREEN_NAME, ADDED_REQUEST);
        mEditorScreen.putInt(MY_VEHICLE_TYPE, type);
        mEditorScreen.commit();
        checkOnScreen = ADDED_REQUEST;

    }

    @Override
    public void addRequestFailure() {
        Toast.makeText(this, "Tạo yêu cầu thất bại", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelRequestSuccess(boolean success) {
        isSending = false;

        showAddRequestButton();
        enableAllButton();
        mBtnCancelRequest.setEnabled(true);

        if (mListActiveUser.size() > 0) {
            for (ActiveUser user : mListActiveUser) {
                if (sMarkerHashMap.containsKey(user)) {
                    Marker markerRm = sMarkerHashMap.get(user);
                    markerRm.remove();
                }
            }
            sMarkerHashMap.clear();
        }
        mGoogleMap.clear();
        mLocationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getMyLocation(location);
                Log.d("onLocationChanged", "onLocationChanged");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mDatabase.deleteRequest(sUserId);
        setLocation();

        SharedPreferences.Editor editor = mSharedPreferencesCheckScreen.edit();
        editor.putInt(SCREEN_NAME, MAIN_ACTIVITY);
        editor.commit();
    }

    @Override
    public void cancelRequestFailed() {

        showAddRequestButton();
        mBtnCancelRequest.setEnabled(true);
        enableAllButton();
        try {
            if (mListActiveUser.size() > 0) {
                for (ActiveUser user : mListActiveUser) {
                    if (sMarkerHashMap.containsKey(user)) {
                        Marker markerRm = sMarkerHashMap.get(user);
                        markerRm.remove();
                    }
                }
                sMarkerHashMap.clear();
            }
            mGoogleMap.clear();
            mLocationListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    getMyLocation(location);
                    Log.d("onLocationChanged", "onLocationChanged");
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            mDatabase.deleteRequest(sUserId);
            setLocation();

            SharedPreferences.Editor editor = mSharedPreferencesCheckScreen.edit();
            editor.putInt(SCREEN_NAME, MAIN_ACTIVITY);
            editor.commit();
        } catch (Exception e) {
            SharedPreferences.Editor editor = mSharedPreferencesCheckScreen.edit();
            editor.putInt(SCREEN_NAME, MAIN_ACTIVITY);
            editor.commit();
        }
    }

    @Override
    public void sendRequestSuccess() {
        isSending = true;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isSending = false;

                checkOnScreen = mSharedPreferencesCheckScreen.getInt(SCREEN_NAME, 0);
                if (checkOnScreen == WAIT_CONFIRM) {

                    mEditorScreen = mSharedPreferencesCheckScreen.edit();
                    mEditorScreen.putInt(SCREEN_NAME, ADDED_REQUEST);
                    mEditorScreen.commit();
                }
            }
        }, 300000);

        mFrameLayoutMarkerInfo.setVisibility(View.GONE);
        mBtnCancelRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public void getListActiveUserUpdateSuccess(List<ActiveUser> activeUsers) {

        if (!isCancel) {
            if (sNumberMarkerInHashMap.size() > 0) {
                for (int i = 0; i < sNumberMarkerInHashMap.size(); i++) {
                    Marker marker = sNumberMarkerInHashMap.get(i);
                    marker.remove();
                }
                sNumberMarkerInHashMap.clear();
                sMarkerHashMap.clear();
                sUserHashMap.clear();

            }
            if (activeUsers.size() > 0) {

                for (int i = 0; i < activeUsers.size(); i++) {
                    ActiveUser activeUser = activeUsers.get(i);

                    new CustomMarkerAsync(this, i).execute(activeUser);
                }
            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (checkOnScreen == ADDED_REQUEST || checkOnScreen == WAIT_CONFIRM) {
                        mListActiveUserAPI.getListUpdate(sSessionId, mVehicleType);
                    }
                }
            }, 30000);
        }
    }

    @Override
    public void getListActiveUserFailure(String message) {
        if (!isCancel) {
            if (message.equals("1")) {
                if (sNumberMarkerInHashMap.size() > 0) {
                    for (int i = 0; i < sNumberMarkerInHashMap.size(); i++) {
                        Marker marker = sNumberMarkerInHashMap.get(i);
                        marker.remove();
                    }
                    sNumberMarkerInHashMap.clear();
                    sMarkerHashMap.clear();
                    sUserHashMap.clear();
                }
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListActiveUserAPI.getListUpdate(sSessionId, mVehicleType);
                }
            }, 30000);
        }
    }

    @Override
    public void noRequestInCurrent() {
        Toast.makeText(this, getResources().getString(R.string.no_request), Toast.LENGTH_SHORT).show();
        showAddRequestButton();

        if (mListActiveUser.size() > 0) {
            for (ActiveUser user : mListActiveUser) {
                if (sMarkerHashMap.containsKey(user)) {
                    Marker markerRm = sMarkerHashMap.get(user);
                    markerRm.remove();

                }
            }
            sMarkerHashMap.clear();
        }
        mGoogleMap.clear();
        mLocationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getMyLocation(location);
                Log.d("onLocationChanged", "onLocationChanged");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mDatabase.deleteRequest(sUserId);
        setLocation();

        SharedPreferences.Editor editor = mSharedPreferencesCheckScreen.edit();
        editor.putInt(SCREEN_NAME, MAIN_ACTIVITY);
        editor.commit();

    }

    @Override
    public void unAuthorize() {
        logout();
    }

    @Override
    public void getInfoUserSuccess(InfomationUser userInfo) {
        if (userInfo.getUserInfo().getAvatarLink() != null && !userInfo.getUserInfo().getAvatarLink().equals("")) {
            Glide.with(this).load(userInfo.getUserInfo().getAvatarLink()).placeholder(getResources()
                    .getDrawable(R.drawable.temp)).centerCrop().into(sAvatar);
            new GetAvatar(this, this).execute(userInfo.getUserInfo().getAvatarLink());

        } else {
            sAvatar.setImageResource(R.drawable.temp);
        }
    }

    @Override
    public void getUserInfoFailure(String message) {

    }

    @Override
    public void getBitMapSuccess(Bitmap bitmap) {
        if (bitmap != null && !bitmap.equals(""))
            sAvatar.setImageBitmap(bitmap);
    }
}
