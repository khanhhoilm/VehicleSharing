package vehiclessharing.vehiclessharing.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.location.LocationListener;
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
import io.realm.Realm;
import vehiclessharing.vehiclessharing.api.RelateRequestAPI;
import vehiclessharing.vehiclessharing.asynctask.CustomMarkerAsync;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.controller.fragment.AddRequestFragment;
import vehiclessharing.vehiclessharing.controller.fragment.SendRequestFragment;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ActiveUser;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.permission.CheckWriteStorage;
import vehiclessharing.vehiclessharing.permission.CheckerGPS;
import vehiclessharing.vehiclessharing.utils.DrawRoute;
import vehiclessharing.vehiclessharing.utils.Helper;
import vehiclessharing.vehiclessharing.utils.Logout;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        AddRequestFragment.OnFragmentAddRequestListener, RelateRequestAPI.CancelRequestCallBack, SendRequestFragment.SendRequestCallBack {

    //    private String TAG=MainActivity.this.getLocalClassName();
    private SessionManager sessionManager;
    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private View viewHeader = null; // View header
    private TextView txtFullName, txtPhone;
    public static ImageView imgUser; // Avatar of user
    public static ProgressBar progressBar;
    public static Bitmap bmImgUser = null; // Bitmap of avatar
    private static int CONTROLL_ON = 1;//Controll to on Locationchanged
    private static int CONTROLL_OFF = -1;//Controll to off Locationchanged


    public static GoogleMap mGoogleMap = null;//Instance google map API
    public static Polyline polyline = null;//Instance
    public static CheckerGPS checkerGPS;
    private User userInfo;
    private Marker myMarker = null;
    private Location previousLocation = null;
    public static HashMap<ActiveUser, Marker> markerHashMap;
    public static int userId;
    public static String sessionId = "";
    private boolean changeLocation = false;
    private LatLng mySource, myDes;
    private List<ActiveUser> listActiveUser;
    public static List<Polyline> polylineList;
    public static HashMap<Marker, ActiveUser> userHashMap;
    public Marker anotherDesMarker;
    private DatabaseHelper mDatabase;//
    private boolean doubleBackToExitPressedOnce = false;
    private Location myLocation;
    private FrameLayout frameLayoutMarkerInfo;


    final private static int REQ_PERMISSION = 20;//Value request permission
    private static String DIRECTION_KEY_API = "AIzaSyAGjxiNRAHypiFYNCN-qcmUgoejyZPtS9c";

    private FloatingActionButton btnFindPeople, btnFindVehicles, btnCancelRequest; // button fab action
    private int checkOnScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(MainActivity.this);
        if (!sessionManager.isLoggedIn()) {
            Intent signIn = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(signIn);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        userId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        sessionId = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        Log.d("SessionId", "SessionId=" + sessionId);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        userInfo = databaseHelper.getUser(userId);
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("Main Activity");
        //realm=Realm.getDefaultInstance();
        // userOnDevice= RealmDatabase.getCurrentUser(userId);
        mDatabase = new DatabaseHelper(this);
        addControls();
        addEvents();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn 1 lần nữa để thoát app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void addEvents() {
        btnFindPeople.setOnClickListener(this);
        btnFindVehicles.setOnClickListener(this);
        btnCancelRequest.setOnClickListener(this);
    }

    private void addControls() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnFindPeople = (FloatingActionButton) findViewById(R.id.btnFindPeople);
        btnFindVehicles = (FloatingActionButton) findViewById(R.id.btnFindVehicle);

        viewHeader = navigationView.getHeaderView(0);

        txtPhone = (TextView) viewHeader.findViewById(R.id.txtPhone);
        txtFullName = (TextView) viewHeader.findViewById(R.id.txtFullName);
        imgUser = (ImageView) viewHeader.findViewById(R.id.imgUser);
        txtPhone.setText(userInfo.getPhone());
        txtFullName.setText(userInfo.getName());

        //progressBar = (ProgressBar) viewHeader.findViewById(R.id.loading_progress_img);
        checkerGPS = new CheckerGPS(MainActivity.this, this);

        btnCancelRequest = (FloatingActionButton) findViewById(R.id.btnCancelRequest);

        frameLayoutMarkerInfo = (FrameLayout) findViewById(R.id.frameContainerMarker);
        frameLayoutMarkerInfo.setVisibility(View.GONE);
        checkOnScreen = 0;
        markerHashMap = new HashMap<>();
        userHashMap = new HashMap<>();
        listActiveUser = new ArrayList<>();
        polylineList = new ArrayList<>();


    }

    @Override
    protected void onStart() {
        super.onStart();
        // updateUIHeader(loginWith);//Update information user into header layout
        /*Intent intent = getIntent();
        if (intent != null) {
            Log.d("notification_aaaa", String.valueOf(intent.getStringExtra("notification")));
            Log.d("notification_aaaa", String.valueOf(intent.getStringExtra("body")));

        }
*/       /* if (checkerGPS.checkLocationPermission() && !TrackGPSService.isRunning)
            startService(new Intent(this, TrackGPSService.class));//Enable tracking GPS*/
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(MainActivity.this, RatingActivity.class);
            startActivity(intent);
            // Handle the camera action
//            fragmentManager.beginTransaction().replace(R.id.frameContainer, new Home_Fragment(), Utils.Home_Fragment).commit();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(historyIntent);
            //   startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        } else if (id == R.id.nav_about) {
            // fab.callOnClick();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        Logout.actionLogout(this, getSupportFragmentManager());

        sessionManager.logoutUser();
        finish();
        /* Intent signIn = new Intent(MainActivity.this, SigninActivity.class);
        startActivity(signIn);*/

    }

    @Override
    public void onClick(View v) {
        String whatbtnClick = "";
        android.support.v4.app.DialogFragment dialogFragment;
        switch (v.getId()) {
            case R.id.btnFindPeople:
                whatbtnClick = "btnFindPeople";
                // dialogTitle[0] = "If you want find a vehicle together, you can fill out the form";
                dialogFragment = AddRequestFragment.newInstance(whatbtnClick);
                dialogFragment.show(getSupportFragmentManager(), "From Needer");
                break;
            case R.id.btnFindVehicle:
                whatbtnClick = "btnFindVehicles";
                dialogFragment = AddRequestFragment.newInstance(whatbtnClick);
                dialogFragment.show(getSupportFragmentManager(), "From Grabber");
                break;
            case R.id.btnCancelRequest:
                cancelRequest();
                break;
        }

    }


    private void cancelRequest() {
        RelateRequestAPI.getInstance(this).cancelRequest(sessionId);
    }

    private void hideButtonFindVehicleAndPeople() {
        if (btnFindVehicles.getVisibility() == View.VISIBLE && btnFindPeople.getVisibility() == View.VISIBLE) {
            btnFindPeople.setVisibility(View.GONE);
            btnFindVehicles.setVisibility(View.GONE);
            if (btnCancelRequest.getVisibility() == View.GONE) {
                btnCancelRequest.setVisibility(View.VISIBLE);
            }
        }
    }

    private void visibleButtonFindVehicleAndPeople() {
        if (btnFindVehicles.getVisibility() == View.GONE && btnFindPeople.getVisibility() == View.GONE) {
            btnFindPeople.setVisibility(View.VISIBLE);
            btnFindVehicles.setVisibility(View.VISIBLE);
            if (btnCancelRequest.getVisibility() == View.VISIBLE) {
                btnCancelRequest.setVisibility(View.GONE);
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

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if(mGoogleMap!=null) {
                        setLocation();
                    }
                } else {
                    checkerGPS.checkLocationPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        try {
            if (mGoogleMap != null) {

                setLocation();

            }
        } catch (Exception e) {
            Toast.makeText(this, "Check permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocation() {
        if (checkerGPS.checkLocationPermission()) {
            mGoogleMap.setMyLocationEnabled(true);//Enable mylocation

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            getMyLocation(myLocation);

            mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    getMyLocation(location);
                }
            });
        }
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            }
        });
    }

    private void getMyLocation(Location myLocation) {
        if (!changeLocation) {
            final double[] longitude = {myLocation.getLongitude()};
            final double[] latitude = {myLocation.getLatitude()};
            LatLng myLatLng = new LatLng(latitude[0], longitude[0]);

            if (!myLocation.equals(previousLocation) && previousLocation != null) {

                if (myLocation.distanceTo(previousLocation) > 20.0 || !myLocation.equals(previousLocation)) {
                    if (myMarker != null) {
                        myMarker.remove();

                    }
                    myMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.here)));
                    //  myMarker.setTag("me");
                    //  markerHashMap.put(userId,myMarker);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 17);
                    mGoogleMap.animateCamera(cameraUpdate);

                }
            }
            if (previousLocation == null) {
                previousLocation = myLocation;
                myMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.here)));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 17);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        }

    }


    private void makeCustomMarkerMyself(LatLng source, LatLng destination) throws IOException {
        mGoogleMap.clear();
        BitmapDescriptor bitmapSource = BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_red_700_36dp);
        BitmapDescriptor bitmapDestination = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red_700_36dp);
        Marker sourceMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title("Chuyến đi của bạn bắt đầu tại: " + PlaceHelper.getInstance(this).getAddressByLatLng(source)).icon(bitmapSource));
        Marker destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(destination).title("Chuyến đi kết thúc tại: " + PlaceHelper.getInstance(this).getAddressByLatLng(destination)).icon(bitmapDestination));
        // userHashMap.put(sourceMarker,null);
        sourceMarker.setTag("here");
        destinationMarker.setTag("des");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("onMarkerClick", "success");
        try {
            displayInfoMarkerClick(marker);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * if user not current user custom info of marker. Info marker have info about fullname, source location, destination location
     * Time start
     *
     * @param marker
     * @return
     */
    private void displayInfoMarkerClick(Marker marker) throws IOException {

        frameLayoutMarkerInfo.setVisibility(View.VISIBLE);
        btnCancelRequest.setVisibility(View.GONE);
        //  View v = getLayoutInflater().inflate(R.layout.info_marker, null);
        final TextView txtFullname, txtSourceLocation, txtDeslocation, txtTime, txtTouch, txtDistance;
        final ImageView imgVehicleType;
        final Button btnsend, btncancel;

        txtFullname = (TextView) findViewById(R.id.txtFullNameUser);
        txtSourceLocation = (TextView) findViewById(R.id.txtSourceLocationUser);
        txtDeslocation = (TextView) findViewById(R.id.txtDesLocationUser);
        txtTime = (TextView) findViewById(R.id.txtTimeUser);
        imgVehicleType = (ImageView) findViewById(R.id.imgVehicleTypeUser);
        //  txtTouch = (TextView) findViewById(R.id.txtTouchSendRequest);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        btnsend = (Button) findViewById(R.id.btnSendRequest);
        btncancel = (Button) findViewById(R.id.btnCancelSendRequest);

        String time = "";
        int vehicleType;


        final ActiveUser anotherUser = userHashMap.get(marker);

        if (anotherUser != null) {
            time = anotherUser.getRequestInfo().getTimeStart();
            vehicleType = anotherUser.getRequestInfo().getVehicleType();
            LatLng sourceLocation = Helper.convertLatLngLocationToLatLng(anotherUser.getRequestInfo().getSourceLocation());
            LatLng desLocation = Helper.convertLatLngLocationToLatLng(anotherUser.getRequestInfo().getDestLocation());

            String sourceAddress = PlaceHelper.getInstance(this).getAddressByLatLng(sourceLocation);
            //LatLng des = new LatLng(desLocation.getLatidude(), desLocation.getLongtitude());
            String destinationAddress = PlaceHelper.getInstance(this).getAddressByLatLng(desLocation);
            String name = anotherUser.getUserInfo().getName();
            txtFullname.setText(name);
            txtSourceLocation.setText(sourceAddress);
            txtDeslocation.setText(destinationAddress);
            txtTime.setText(time);
            switch (vehicleType) {
                case 0:
                    imgVehicleType.setImageResource(R.drawable.ic_accessibility_indigo_700_24dp);
                    break;
                case 1:
                    imgVehicleType.setImageResource(R.drawable.ic_directions_car_indigo_700_24dp);
                    break;
                case 2:
                    imgVehicleType.setImageResource(R.drawable.ic_motorcycle_indigo_a700_24dp);
                    break;
            }
            if (DrawRoute.polylineNotCurUser != null) {
                DrawRoute.polylineNotCurUser.remove();
            }
            BitmapDescriptor bitmapDestination = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_indigo_500_24dp);
            if (anotherDesMarker != null) {
                anotherDesMarker.remove();
            }
            anotherDesMarker = mGoogleMap.addMarker(new MarkerOptions().position(desLocation).title("Chuyến đi của " + anotherUser.getUserInfo().getName() + " kết thúc tại đây ").icon(bitmapDestination));
            // sourceMarker.setTag();
            DrawRoute draw = new DrawRoute(this, mGoogleMap);
            //code test
            if (polylineList.size() > 1) {
                polylineList.get(1).remove();

            }
            if (anotherUser.getRequestInfo().getVehicleType() == 0) {
                draw.drawroadBetween4Location(mySource, sourceLocation, desLocation, myDes, 1);
            } else {
                draw.drawroadBetween4Location(sourceLocation, mySource, myDes, desLocation, 1);
            }

            Location location = new Location(sourceAddress);
            location.setLatitude(sourceLocation.latitude);
            location.setLongitude(sourceLocation.longitude);
            float distance = Helper.getKiloMeter(myLocation.distanceTo(location));
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
                    frameLayoutMarkerInfo.setVisibility(View.GONE);
                    btnCancelRequest.setVisibility(View.VISIBLE);
                }
            });
            //  location.distanceTo()
        }
    }

    private void confirmSureSendRequestToAnotherUser(ActiveUser userIsChosen) {
        android.support.v4.app.DialogFragment dialogFragment;
        dialogFragment = SendRequestFragment.newInstance(sessionId, userIsChosen.getUserInfo().getId(), this);
        dialogFragment.show(getSupportFragmentManager(), "SendRequest");

    }

    @Override
    public void addRequestSuccess(LatLng cur, LatLng des, String time, int type, List<ActiveUser> list) {
        String requestName = "";
        if (type == 0) {
            requestName = "Bạn đã tạo thành công yêu cầu tìm xe";
        } else {
            requestName = "Bạn đã tạo thành công yêu cầu cho đi nhờ xe";
        }
        Toast.makeText(this, requestName, Toast.LENGTH_SHORT).show();

        frameLayoutMarkerInfo.setVisibility(View.GONE);
        btnCancelRequest.setVisibility(View.VISIBLE);

        if (list.size() > 0) {
            listActiveUser = list;

        }
        changeLocation = true;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cur, 17);
        mGoogleMap.animateCamera(cameraUpdate);

        DrawRoute drawRoute = new DrawRoute(this, mGoogleMap);
        drawRoute.setmSubject(0);
        mySource = cur;
        myDes = des;
        drawRoute.drawroadBetween2Location(cur, des, 0);
        try {
            makeCustomMarkerMyself(cur, des);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //drawroadBetween2Location(curLocation, desLocation);
        hideButtonFindVehicleAndPeople();
        if (list.size() > 0) {
            for (ActiveUser activeUser : list) {
                new CustomMarkerAsync(this).execute(activeUser);
            }
        }
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setVehicleType(type);
        requestInfo.setTimeStart(time);
        requestInfo.setSourceLocation(Helper.convertLatLngToLatLngLocation(cur));
        requestInfo.setDestLocation(Helper.convertLatLngToLatLngLocation(des));

        CheckWriteStorage.getInstance(this, this).isStoragePermissionGranted();
        mDatabase.insertRequest(requestInfo, userId);
        //ForGraber.getInstance().getAllNeederNear(this, mUser.getUid());
        myLocation = new Location("MyLocation");
        myLocation.setLatitude(cur.latitude);
        myLocation.setLongitude(cur.longitude);
    }

    @Override
    public void addRequestFailure() {
        Toast.makeText(this, "Tạo yêu cầu thất bại", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelRequestSuccess(boolean success) {
        //if(success) {
        btnCancelRequest.setVisibility(View.GONE);
        btnFindPeople.setVisibility(View.VISIBLE);
        btnFindVehicles.setVisibility(View.VISIBLE);
        if (listActiveUser.size() > 0) {
            for (ActiveUser user : listActiveUser) {
                if (markerHashMap.containsKey(user)) {
                    Marker markerRm = markerHashMap.get(user);
                    markerRm.remove();
                    //        markerHashMap.remove(user);
                }
            }
            markerHashMap.clear();
        }
        mGoogleMap.clear();
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("onLocationChanged", "onLocationChanged");
            }
        };
        mDatabase.deleteRequest(userId);
        setLocation();

    }


    @Override
    public void sendRequestSuccess() {
        frameLayoutMarkerInfo.setVisibility(View.GONE);
        btnCancelRequest.setVisibility(View.VISIBLE);
    }
}
